package hiveconnect.com.superwifidirect.Fragment;

import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.InetAddress;
import java.net.UnknownHostException;

import hiveconnect.com.superwifidirect.Adapter.DeviceAdapter;
import hiveconnect.com.superwifidirect.Bean.Event_FunctionFragmentEvent;
import hiveconnect.com.superwifidirect.Fragment.BasicFragment.MySupportFragment;
import hiveconnect.com.superwifidirect.R;
import hiveconnect.com.superwifidirect.Task.SendHandshakeTask;
import hiveconnect.com.superwifidirect.Util.NetUtil;

public class Fragment_GroupFind extends MySupportFragment {


    private Button btn_quitGroup;
    private Button btn_searchGroup;
    private Button button_ChooseFunc_Trans;
    private Button button_ChooseFunc_SeekHelp;
    private TextView tv_myDeviceStatus;
    private TextView tv_myDeviceName;
    private TextView tv_myDeviceAddress;

    private RecyclerView rv_MasterList;
    private DeviceAdapter deviceAdapter;
    private WifiP2pDevice mWifiP2pDevice;
    private int handshakePORT=4786;



    private static final String TAG="Fragment_GroupFind";
    public static Fragment_GroupFind newInstance() {
        Bundle args = new Bundle();
        Fragment_GroupFind fragment = new Fragment_GroupFind();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.layout_fragment_formgroup_slave, container, false);
        EventBus.getDefault().register(this);

        initView();

        return fragmentView;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event_FunctionFragmentEvent event) {
        Log.e(TAG,"收到了EventBus信息"+event.getmConcreteEvent());
        switch (event.getmConcreteEvent()){
            case wifiP2pEnabled:
                handleEvent_wifiP2pEnabled();
                break;
            case onDisconnection:
                handleEvent_onDisconnection();
                break;
            case onPeersAvailable:
                handleEvent_onPeersAvailable();
                break;
            case onSelfDeviceAvailable:
                handleEvent_onSelfDeviceAvailable();
                break;
            case onConnectionInfoAvailable:
                handleEvent_onConnectionInfoAvailable();
                break;
            default:
                break;
        }
    };








    private void initView() {

        tv_myDeviceStatus=(TextView)fragmentView.findViewById(R.id.tv_myDeviceStatus);
        tv_myDeviceName=(TextView)fragmentView.findViewById(R.id.tv_myDeviceName);
        tv_myDeviceAddress=(TextView)fragmentView.findViewById(R.id.tv_myDeviceAddress);
        btn_searchGroup = (Button) fragmentView.findViewById(R.id.btn_searchGroup);
        btn_quitGroup = (Button) fragmentView.findViewById(R.id.btn_quitGroup);
        button_ChooseFunc_Trans = (Button) fragmentView.findViewById(R.id.button_ChooseFunc_Trans);
        button_ChooseFunc_SeekHelp = (Button) fragmentView.findViewById(R.id.button_ChooseFunc_SeekHelp);
        rv_MasterList=(RecyclerView)fragmentView.findViewById(R.id.rv_MasterList);
        button_ChooseFunc_Trans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start(Fragment_FileAcquire.newInstance());
            }
        });
        button_ChooseFunc_SeekHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start(Fragment_HelpInfoSeek.newInstance());
            }
        });
        btn_searchGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mWifiP2pEnabled) {
                    showToast("请先打开wifi开关");

                } else {
                    showToast("正在搜索中");
                    //mainActivity.wifiP2pMasterList.clear();
                    wifiP2pManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            showToast("Success");

                        }

                        @Override
                        public void onFailure(int reasonCode) {
                            showToast("Failure");

                        }
                    });
                }
            }
        });
        btn_quitGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeGroup();
            }
        });

        deviceAdapter = new DeviceAdapter(mainActivity.wifiP2pMasterList);
        deviceAdapter.setClickListener(new DeviceAdapter.OnClickListener() {
            @Override
            public void onItemClick(int position) {
                mWifiP2pDevice = mainActivity.wifiP2pMasterList.get(position);
                showToast(mainActivity.wifiP2pMasterList.get(position).deviceName);
                connect();
            }
        });
        rv_MasterList.setAdapter(deviceAdapter);
        rv_MasterList.setLayoutManager(new LinearLayoutManager(mainActivity));

    }

    private void connect() {
        WifiP2pConfig config = new WifiP2pConfig();
        if (config.deviceAddress != null && mWifiP2pDevice != null) {
            config.deviceAddress = mWifiP2pDevice.deviceAddress;
            config.wps.setup = WpsInfo.PBC;
            //showLoadingDialog("正在连接 " + mWifiP2pDevice.deviceName);
            wifiP2pManager.connect(channel, config, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    //主动加入Group后的操作，为了能够双向操作
                    //与Master进行握手交换初始信息。分为3个阶段



                    Log.e(TAG, "connect onSuccess");
                }

                @Override
                public void onFailure(int reason) {
                    showToast("连接失败 " + reason);
                    //dismissLoadingDialog();
                }
            });
        }
    }



    private void removeGroup() {


        wifiP2pManager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.e(TAG, "removeGroup onSuccess");
                showToast("onSuccess");
            }

            @Override
            public void onFailure(int reason) {
                Log.e(TAG, "removeGroup onFailure,reason:"+reason);
                showToast("onFailure");
            }
        });
    }






    private void handleEvent_wifiP2pEnabled(){

    }

    private void handleEvent_onConnectionInfoAvailable(){




        deviceAdapter.notifyDataSetChanged();
        //连接成功后，向GroupMaster汇报自己的IP(当然还应该让Master知道是谁在汇报)
        Log.e(TAG,mainActivity.getmWifiP2pInfo().groupOwnerAddress.toString());

            String MyIp=new NetUtil().getIp(mContext);
            //InetAddress inetAddress= new InetAddress();
//            inetAddress.getHostAddress();
            Log.e(TAG,"本机的LocalHost:"+MyIp);
        new SendHandshakeTask(mainActivity.getmWifiP2pInfo().groupOwnerAddress, SendHandshakeTask.GroupCharactor.SLAVE).execute(MyIp,mainActivity.getSelfP2pDevice().deviceAddress);



    }

    private void handleEvent_onDisconnection(){

    }

    private void handleEvent_onSelfDeviceAvailable(){
        tv_myDeviceAddress.setText(mainActivity.getSelfP2pDevice().deviceAddress);
        tv_myDeviceName.setText(mainActivity.getSelfP2pDevice().deviceName);
        tv_myDeviceStatus.setText(getDeviceStatus(mainActivity.getSelfP2pDevice().status));

    }

    private void handleEvent_onPeersAvailable(){
        deviceAdapter.notifyDataSetChanged();
    }










    public static String getDeviceStatus(int deviceStatus) {
        switch (deviceStatus) {
            case WifiP2pDevice.AVAILABLE:
                return "可用的";
            case WifiP2pDevice.INVITED:
                return "邀请中";
            case WifiP2pDevice.CONNECTED:
                return "已连接";
            case WifiP2pDevice.FAILED:
                return "失败的";
            case WifiP2pDevice.UNAVAILABLE:
                return "不可用的";
            default:
                return "未知";
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
    }
}
