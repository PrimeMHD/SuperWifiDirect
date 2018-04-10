package hiveconnect.com.superwifidirect.Fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import java.util.Collection;

import hiveconnect.com.superwifidirect.Activity.MainActivity;
import hiveconnect.com.superwifidirect.Callback.DirectActionListener;
import hiveconnect.com.superwifidirect.Fragment.BasicFragment.MySupportFragment;
import hiveconnect.com.superwifidirect.R;
import hiveconnect.com.superwifidirect.Service.WifiServerService;

//import static android.content.ContentValues.TAG;

public class Fragment_GroupCreate extends MySupportFragment implements DirectActionListener {


    private Button button_CreateGroup;
    private Button button_DismissGroup;
    private View fragment_view;
    private static final String TAG="Fragment_GroupCreate";





    public static Fragment_GroupCreate newInstance() {
        Bundle args = new Bundle();
        Fragment_GroupCreate fragment = new Fragment_GroupCreate();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragment_view = inflater.inflate(R.layout.layout_fragment_formgroup_master, container, false);
        button_CreateGroup=(Button)fragment_view.findViewById(R.id.button_CreateGroup);
        button_DismissGroup=(Button)fragment_view.findViewById(R.id.button_DismissGroup);
        setTouchEvent();
        return fragment_view;
    }


    private void setTouchEvent(){
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
            }
        });

    }






    public void createGroup(View view) {
        //showLoadingDialog("正在创建群组");
        wifiP2pManager.createGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.e(TAG, "createGroup onSuccess");
                //dismissLoadingDialog();
                showToast("onSuccess");
            }

            @Override
            public void onFailure(int reason) {
                Log.e(TAG, "createGroup onFailure: " + reason);
               // dismissLoadingDialog();
                showToast("onFailure");
            }
        });
    }

    public void removeGroup(View view) {
        removeGroup();
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
    public void wifiP2pEnabled(boolean enabled) {
        Log.e(TAG, "wifiP2pEnabled: " + enabled);
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
        Log.e(TAG, "onConnectionInfoAvailable");
        Log.e(TAG, "isGroupOwner：" + wifiP2pInfo.isGroupOwner);
        Log.e(TAG, "groupFormed：" + wifiP2pInfo.groupFormed);
        if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
            if (wifiServerService != null) {
                mainActivity.startWifiServerSerivce();
            }
        }
    }

    @Override
    public void onDisconnection() {
        Log.e(TAG, "onDisconnection");
    }

    @Override
    public void onSelfDeviceAvailable(WifiP2pDevice wifiP2pDevice) {
        Log.e(TAG, "onSelfDeviceAvailable");
    }

    @Override
    public void onPeersAvailable(Collection<WifiP2pDevice> wifiP2pDeviceList) {
        Log.e(TAG, "onPeersAvailable");
    }

    @Override
    public void onChannelDisconnected() {
        Log.e(TAG, "onChannelDisconnected");
    }


}
