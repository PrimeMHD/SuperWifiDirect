package hiveconnect.com.superwifidirect.Activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Collection;
import java.util.List;

import hiveconnect.com.superwifidirect.Broadcast.DirectBroadcastReceiver;
import hiveconnect.com.superwifidirect.Callback.DirectActionListener;
import hiveconnect.com.superwifidirect.Fragment.BasicFragment.BaseMainFragment;
import hiveconnect.com.superwifidirect.Fragment.BasicFragment.ContainerFragment;
import hiveconnect.com.superwifidirect.R;
import hiveconnect.com.superwifidirect.Service.WifiServerService;
import hiveconnect.com.superwifidirect.util.LoadingDialog;
import me.yokeyword.fragmentation.SupportActivity;
import me.yokeyword.fragmentation.SupportFragment;

public class MainActivity extends SupportActivity implements DirectActionListener,
BaseMainFragment.OnBackToFirstListener{

    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel channel;
    private BroadcastReceiver broadcastReceiver;
    private WifiServerService wifiServerService;
    private ProgressDialog progressDialog;
    private FragmentManager fragmentManager;
    private SupportFragment[] mFragments = new SupportFragment[4];
    public LoadingDialog loadingDialog;
    private boolean mWifiP2pEnabled = false;
    public List<WifiP2pDevice> wifiP2pMasterList;
    public List<WifiP2pDevice> wifiP2pSlaveList;




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

    public LoadingDialog getLoadingDialog() {
        return loadingDialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wifiP2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = wifiP2pManager.initialize(this, getMainLooper(), this);
        broadcastReceiver = new DirectBroadcastReceiver(wifiP2pManager, channel, this);
        registerReceiver(broadcastReceiver, DirectBroadcastReceiver.getIntentFilter());
        SupportFragment containerFragment = findFragment(ContainerFragment.class);
        if(containerFragment==null){
            containerFragment=ContainerFragment.newInstance();
            loadRootFragment(R.id.lay_frame,containerFragment);
        }
        bindService();



    }


    private void bindService() {
        Intent intent = new Intent(this, WifiServerService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            WifiServerService.MyBinder binder = (WifiServerService.MyBinder) service;
            wifiServerService = binder.getService();
            //wifiServerService.setProgressChangListener(progressChangListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            wifiServerService = null;
            bindService();
        }
    };
    public void startWifiServerSerivce(){
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
    private void bindService(Context context, ServiceConnection serviceConnection) {
        Intent intent = new Intent(context, WifiServerService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }


    @Override
    public void wifiP2pEnabled(boolean enabled){
        mWifiP2pEnabled=true;
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo){

    }

    @Override
    public void onDisconnection(){

    }

    @Override
    public void onSelfDeviceAvailable(WifiP2pDevice wifiP2pDevice){

    }

    @Override
    public void onPeersAvailable(Collection<WifiP2pDevice> wifiP2pDeviceList){

    }


    @Override
    public void onChannelDisconnected() {

    }


    @Override
    public void onBackPressedSupport() {
//        Log.d(TAG, "在这儿按下了back");
//        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
//            pop();
//        } else {
//            ActivityCompat.finishAfterTransition(this);
//        }
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
}
