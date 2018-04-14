package hiveconnect.com.superwifidirect.Fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collection;

import hiveconnect.com.superwifidirect.Activity.MainActivity;
import hiveconnect.com.superwifidirect.Adapter.DeviceAdapter;
import hiveconnect.com.superwifidirect.Bean.Event_FunctionFragmentEvent;
import hiveconnect.com.superwifidirect.Callback.DirectActionListener;
import hiveconnect.com.superwifidirect.Fragment.BasicFragment.MySupportFragment;
import hiveconnect.com.superwifidirect.R;
import hiveconnect.com.superwifidirect.Service.WifiServerService;
import hiveconnect.com.superwifidirect.util.EnumPack;

//import static android.content.ContentValues.TAG;

public class Fragment_GroupCreate extends MySupportFragment{


    private Button button_CreateGroup;
    private Button button_DismissGroup;
    private Button button_ChooseFunc_Trans;
    private Button button_ChooseFunc_Sign;
    private Button button_ChooseFunc_SeekHelp;
    private RecyclerView rv_SlaveList;
    private DeviceAdapter deviceAdapter;
    private static final String TAG="Fragment_GroupCreate";
    private static EnumPack.GroupFunc groupFunc= EnumPack.GroupFunc.NOFUNC;




    public static Fragment_GroupCreate newInstance() {
        Bundle args = new Bundle();
        Fragment_GroupCreate fragment = new Fragment_GroupCreate();
        fragment.setArguments(args);
        return fragment;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event_FunctionFragmentEvent event) {
        Log.e(TAG,"收到了EventBus信息"+event.getmConcreteEvent());
        switch (event.getmConcreteEvent()){
            case wifiP2pEnabled:
                handleEvent_wifiP2pEnabled();;
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




    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.layout_fragment_formgroup_master, container, false);
        EventBus.getDefault().register(this);
        deviceAdapter=new DeviceAdapter(mainActivity.wifiP2pSlaveList);
        initView();
        return fragmentView;
    }

    private void initView() {
        button_CreateGroup=(Button)fragmentView.findViewById(R.id.button_CreateGroup);
        button_DismissGroup=(Button)fragmentView.findViewById(R.id.button_DismissGroup);
        button_ChooseFunc_Trans=(Button)fragmentView.findViewById(R.id.button_ChooseFunc_Trans);
        rv_SlaveList=(RecyclerView)fragmentView.findViewById(R.id.rv_SlaveList);
        button_CreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createGroup(view);
            }
        });
        button_DismissGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeGroup();
                //TODO 清空设备List

            }
        });
        button_ChooseFunc_Trans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                groupFunc= EnumPack.GroupFunc.TRANS;

            }
        });
        deviceAdapter = new DeviceAdapter(mainActivity.wifiP2pSlaveList);
        deviceAdapter.setClickListener(new DeviceAdapter.OnClickListener() {
            @Override
            public void onItemClick(int position) {
                //TODO 可以在这里设置驱逐的方法
            }
        });
        rv_SlaveList.setAdapter(deviceAdapter);
        rv_SlaveList.setLayoutManager(new LinearLayoutManager(mainActivity));


    }

    private void handleEvent_wifiP2pEnabled(){

    }

    private void handleEvent_onConnectionInfoAvailable(){
        deviceAdapter.notifyDataSetChanged();


    }

    private void handleEvent_onDisconnection(){

    }

    private void handleEvent_onSelfDeviceAvailable(){

    }

    private void handleEvent_onPeersAvailable(){

    }






    public void createGroup(View view) {
        //showLoadingDialog("正在创建群组");
        wifiP2pManager.createGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.e(TAG, "createGroup onSuccess");
                //dismissLoadingDialog();
                showToast("onSuccess");
                wifiP2pManager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener() {
                    @Override
                    public void onGroupInfoAvailable(WifiP2pGroup wifiP2pGroup) {

                        Log.e(TAG,"获取到了wifiP2pGroup的信息");
                        Log.e(TAG,wifiP2pGroup+"");
                    }
                });
            }

            @Override
            public void onFailure(int reason) {
                Log.e(TAG, "createGroup onFailure: " + reason);
               // dismissLoadingDialog();
                showToast("onFailure");
            }
        });
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
                Log.e(TAG, "removeGroup onFailure");
                showToast("onFailure");
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
    }
}
