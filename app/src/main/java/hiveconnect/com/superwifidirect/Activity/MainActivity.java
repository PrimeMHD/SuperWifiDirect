package hiveconnect.com.superwifidirect.Activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hiveconnect.com.superwifidirect.Broadcast.DirectBroadcastReceiver;
import hiveconnect.com.superwifidirect.Callback.DirectActionListener;
import hiveconnect.com.superwifidirect.Bean.Event_FunctionFragmentEvent;
import hiveconnect.com.superwifidirect.Fragment.BasicFragment.BaseMainFragment;
import hiveconnect.com.superwifidirect.Fragment.BasicFragment.ContainerFragment;
import hiveconnect.com.superwifidirect.R;
import hiveconnect.com.superwifidirect.Service.HandshakeServerService;
import hiveconnect.com.superwifidirect.Service.WifiServerService;
import hiveconnect.com.superwifidirect.Task.SendHandshakeTask;
import hiveconnect.com.superwifidirect.Util.EnumPack;
import hiveconnect.com.superwifidirect.Util.LoadingDialog;
import me.yokeyword.fragmentation.SupportActivity;
import me.yokeyword.fragmentation.SupportFragment;

import static hiveconnect.com.superwifidirect.Bean.Event_FunctionFragmentEvent.ConcreteEvent.onConnectionInfoAvailable;
import static hiveconnect.com.superwifidirect.Bean.Event_FunctionFragmentEvent.ConcreteEvent.onDisconnection;
import static hiveconnect.com.superwifidirect.Bean.Event_FunctionFragmentEvent.ConcreteEvent.onPeersAvailable;
import static hiveconnect.com.superwifidirect.Bean.Event_FunctionFragmentEvent.ConcreteEvent.onSelfDeviceAvailable;

public class MainActivity extends SupportActivity implements DirectActionListener,
        BaseMainFragment.OnBackToFirstListener {


    private static final String TAG = "MainActivity";
    private WifiP2pManager wifiP2pManager;//提供接口给上层调用，控制WifiP2pService

    private WifiP2pManager.Channel channel;
    private WifiP2pGroup mWifiP2pGroup;
    private WifiP2pInfo mWifiP2pInfo;
    private BroadcastReceiver broadcastReceiver;

    private ProgressDialog progressDialog;
    private FragmentManager fragmentManager;
    private SupportFragment[] mFragments = new SupportFragment[4];
    public LoadingDialog loadingDialog;
    private boolean mWifiP2pEnabled = false;
    public List<WifiP2pDevice> wifiP2pMasterList;//在没有加入组群之前的可选Master列表
    public List<WifiP2pDevice> wifiP2pSlaveList;//创建组群之后的成员列表
    public List<String>wifiP2pMasterMacList;
    public List<String>wifiP2pSlaveMacList;

    public Map<String,Integer> devicePortMap;//存储P2pSlaves的端口信息
    public Map<String,InetAddress>deviceIPMap;//存储P2pSlaves的IP信息
    private WifiP2pDevice SelfP2pDevice;
    public static int MasterDistributedPort;
    public static String SelfIP;
    private HandshakeServerService handshakeServerService;
    private WifiServerService wifiServerService;
    private WifiServerService.MyBinder wifiServerServiceBinder;

    public static void setMasterDistributedPort(int masterDistributedPort) {
        MasterDistributedPort = masterDistributedPort;
    }

    private EnumPack.DBRState mDBRState;
    private  HandshakeServerService.MyBinder handshakeServiceBinder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wifiP2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);

        channel = wifiP2pManager.initialize(this, getMainLooper(), this);
        broadcastReceiver = new DirectBroadcastReceiver(wifiP2pManager, channel, this);
        registerReceiver(broadcastReceiver, DirectBroadcastReceiver.getIntentFilter());
        SupportFragment containerFragment = findFragment(ContainerFragment.class);
        if (containerFragment == null) {
            containerFragment = ContainerFragment.newInstance();
            loadRootFragment(R.id.lay_frame, containerFragment);
        }
        wifiP2pMasterList = new ArrayList<>();
        wifiP2pSlaveList = new ArrayList<>();
        wifiP2pMasterMacList = new ArrayList<>();
        wifiP2pSlaveMacList = new ArrayList<>();
        devicePortMap =new HashMap<String, Integer>();
        deviceIPMap=new HashMap<String, InetAddress>();
        //initialize lists
        mDBRState = EnumPack.DBRState.DEFAULT;

        bindService("WifiServerService");//绑定wifiServerService
        bindService("HandshakeServiceService");//绑定HandshakeServerService



    }


    public void bindService(final String serviceName) {
        Intent intent;
        switch (serviceName){
            case "WifiServerService":
                intent = new Intent(this, WifiServerService.class);
                Log.e(TAG,"bind wifiservice");
                bindService(intent, serviceConnection, BIND_AUTO_CREATE);
                break;
            case "HandshakeServiceService":
                intent = new Intent(this, HandshakeServerService.class);
                Log.e(TAG,"bind handshakeservice");
                bindService(intent, serviceConnection_handshake, BIND_AUTO_CREATE);
                break;
        }

    }




    private ServiceConnection serviceConnection_handshake=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            handshakeServiceBinder=(HandshakeServerService.MyBinder)iBinder;
            Log.e(TAG,"handshakeService创建成功");
            handshakeServiceBinder.bindMainActivity(MainActivity.this);
            handshakeServerService = ((HandshakeServerService.MyBinder) iBinder).getService();
            handshakeServerService.setmHandshakeFinishLisner(new HandshakeServerService.HandshakeFinishLisner() {
                @Override
                public void onHandshakeFinish(int mMasterDestributedPort) {

                    MasterDistributedPort=mMasterDestributedPort;
                    Log.e(TAG,"将通信Port改变为"+mMasterDestributedPort);
                }
                @Override
                public void onHandshakeFinish(String SlaveMac,InetAddress SlaveIp ) {
                    //接收Slave发来的IP信息汇报之后,需要告诉Slave新规定的端口
                    int PortToSend=devicePortMap.get(SlaveMac);
                    Log.e(TAG,"查找到SlavePORT为:"+PortToSend);
                    Log.e(TAG,"要去向的SlaveIp为:"+SlaveIp.toString());

                    new SendHandshakeTask(SlaveIp, SendHandshakeTask.GroupCharactor.MASTER).execute(Integer.toString(PortToSend),mWifiP2pGroup.getOwner().deviceAddress);

                }
            });

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            wifiServerServiceBinder = (WifiServerService.MyBinder) service;
            wifiServerService = wifiServerServiceBinder.getService();
            Log.e(TAG,"wifiserverservice创建成功");
            //wifiServerService.setProgressChangListener(progressChangListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            wifiServerService = null;
            bindService("WifiServerService");
        }
    };

    public void startWifiServerSerivce(WifiServerService.DATA_TYPE dataType) {
        Log.e(TAG,"准备接收文件");
        wifiServerServiceBinder.setData_type(dataType);
        wifiServerServiceBinder.setPORT(MasterDistributedPort);
        startService(new Intent(MainActivity.this, WifiServerService.class));
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

    public void setHandshakeServiceCharactor(HandshakeServerService.GroupCharactor groupCharactor){
        handshakeServiceBinder.setGroupCharacter(groupCharactor);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        unbindService(serviceConnection);
        wifiP2pManager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.e(TAG, "removeGroup onSuccess");
                showToast("onSuccess");
            }

            @Override
            public void onFailure(int reason) {
                Log.e(TAG, "removeGroup onFailure");
                showToast("onFailure");
            }
        });
    }


    //开始实现DirectActionListener
    @Override
    public void wifiP2pEnabled(boolean enabled) {
        mWifiP2pEnabled = true;
        wifiP2pManager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener() {
            @Override
            public void onGroupInfoAvailable(WifiP2pGroup wifiP2pGroup) {
                mWifiP2pGroup = wifiP2pGroup;
                Log.e(TAG, "Master状态获取到了wifiP2pGroup的信息");
                Log.e(TAG, wifiP2pGroup + "");
                if (mWifiP2pGroup != null) {
                    wifiP2pSlaveList.clear();
                    wifiP2pSlaveList.addAll(mWifiP2pGroup.getClientList());
                    Log.e(TAG, "SlaveList有" + wifiP2pSlaveList.size());
                    EventBus.getDefault().post(new Event_FunctionFragmentEvent(onConnectionInfoAvailable));
                }
            }
        });
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {

        mWifiP2pInfo=wifiP2pInfo;
        switch (mDBRState) {
            case GROUP_CREATE:
            case DEFAULT:
                wifiP2pManager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener() {
                    @Override
                    public void onGroupInfoAvailable(WifiP2pGroup wifiP2pGroup) {
                        mWifiP2pGroup = wifiP2pGroup;
                        Log.e(TAG, "Master状态获取到了wifiP2pGroup的信息");
                        Log.e(TAG, wifiP2pGroup + "");
                        if (mWifiP2pGroup != null) {
                            wifiP2pSlaveList.clear();
                            wifiP2pSlaveList.addAll(mWifiP2pGroup.getClientList());
                            Log.e(TAG, "SlaveList有" + wifiP2pSlaveList.size());
                            EventBus.getDefault().post(new Event_FunctionFragmentEvent(onConnectionInfoAvailable));
                        }
                    }
                });
                break;
            case GROUP_FIND:
                wifiP2pManager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener() {
                    @Override
                    public void onGroupInfoAvailable(WifiP2pGroup wifiP2pGroup) {
                        mWifiP2pGroup = wifiP2pGroup;
                        Log.e(TAG, "Slave状态获取到了wifiP2pGroup的信息");
                        Log.e(TAG, wifiP2pGroup + "");
                        if (mWifiP2pGroup != null) {
//                            wifiP2pSlaveList.clear();
//                            wifiP2pSlaveList.addAll(mWifiP2pGroup.getClientList());
//                            Log.e(TAG, "SlaveList有" + wifiP2pSlaveList.size());
                            EventBus.getDefault().post(new Event_FunctionFragmentEvent(onConnectionInfoAvailable));
                        }
                    }
                });
                break;

        }


    }

    @Override
    public void onDisconnection() {
        switch (mDBRState) {
            case DEFAULT:
                break;
            case GROUP_CREATE://MASTER在创建组
                //要时刻关注Slave成员
                Log.e(TAG, "发送了EventBus信息onDisconnection");
                wifiP2pSlaveList.clear();
                Log.e(TAG, "SlaveList" + wifiP2pSlaveList.size());
                EventBus.getDefault().post(new Event_FunctionFragmentEvent(onDisconnection));
                break;
        }
    }

    @Override
    public void onSelfDeviceAvailable(WifiP2pDevice wifiP2pDevice) {
        SelfP2pDevice=wifiP2pDevice;
        EventBus.getDefault().post(new Event_FunctionFragmentEvent(onSelfDeviceAvailable));



    }

    @Override
    public void onPeersAvailable(Collection<WifiP2pDevice> wifiP2pDeviceList) {
        switch (mDBRState) {
            case GROUP_FIND:
                wifiP2pMasterList.clear();
                wifiP2pMasterList.addAll(wifiP2pDeviceList);
                Log.e(TAG,"可用的MASTER有"+wifiP2pMasterList.toString());
                EventBus.getDefault().post(new Event_FunctionFragmentEvent(onPeersAvailable));

                break;
        }
//        wifiP2pManager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener() {
//            @Override
//            public void onGroupInfoAvailable(WifiP2pGroup wifiP2pGroup) {
//                mWifiP2pGroup = wifiP2pGroup;
//                Log.e(TAG, "获取到了wifiP2pGroup的信息");
//                if(mWifiP2pGroup!=null){
//                Log.e(TAG, wifiP2pGroup + ""+"slave有"+mWifiP2pGroup.getClientList().size());
//                } else{
//                    wifiP2pSlaveList.clear();
//                }
//            }
//
//        });
    }


    @Override
    public void onChannelDisconnected() {

    }
    //结束实现DirectActionListener


    @Override
    public void onBackPressedSupport() {
        Log.d(TAG, "在这儿按下了back");
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            pop();
        } else {
            ActivityCompat.finishAfterTransition(this);
        }
    }

    @Override
    public void onBackToFirstFragment() {

    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    protected void showLoadingDialog(String message) {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(this);
        }
        loadingDialog.show(message, true, false);
    }

    protected void dismissLoadingDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }


    public EnumPack.DBRState getDBRState() {
        return mDBRState;
    }

    public void setDBRState(EnumPack.DBRState DBRState) {
        this.mDBRState = DBRState;
    }

    public WifiP2pManager getWifiP2pManager() {
        return wifiP2pManager;
    }

    public WifiP2pManager.Channel getChannel() {
        return channel;
    }

    public BroadcastReceiver getBroadcastReceiver() {
        return broadcastReceiver;
    }

    public WifiServerService getWifiServerService() {
        return wifiServerService;
    }

    public ProgressDialog getProgressDialog() {
        return progressDialog;
    }

    public boolean ismWifiP2pEnabled() {
        return mWifiP2pEnabled;
    }

    public WifiP2pDevice getSelfP2pDevice() {
        return SelfP2pDevice;
    }

    public LoadingDialog getLoadingDialog() {
        return loadingDialog;
    }

    public WifiP2pGroup getmWifiP2pGroup() {
        return mWifiP2pGroup;
    }

    public WifiP2pInfo getmWifiP2pInfo() {
        return mWifiP2pInfo;
    }
}
