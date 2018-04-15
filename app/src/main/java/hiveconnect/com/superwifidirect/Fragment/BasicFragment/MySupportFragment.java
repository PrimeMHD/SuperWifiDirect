package hiveconnect.com.superwifidirect.Fragment.BasicFragment;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager;
import android.view.View;

import hiveconnect.com.superwifidirect.Activity.MainActivity;
import hiveconnect.com.superwifidirect.Service.WifiServerService;
import me.yokeyword.fragmentation.SupportFragment;

public class MySupportFragment extends SupportFragment {

    protected Context mContext;
    protected MainActivity mainActivity;
    protected WifiP2pManager wifiP2pManager;
    protected WifiP2pManager.Channel channel;
    protected boolean mWifiP2pEnabled=false;
    //private BroadcastReceiver broadcastReceiver;
    protected WifiServerService wifiServerService;
    //protected ProgressDialog progressDialog;
    //protected FragmentManager fragmentManager;
    protected View fragmentView;



    protected void showToast(String message) {
        ((MainActivity)mContext).showToast(message);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext=context;
        mainActivity=(MainActivity) context;
        wifiP2pManager=mainActivity.getWifiP2pManager();
        channel=mainActivity.getChannel();
        wifiServerService=mainActivity.getWifiServerService();
        mWifiP2pEnabled=mainActivity.ismWifiP2pEnabled();
    }

    protected void showLoadingDialog(String message) {
        mainActivity.loadingDialog.show(message, true, false);
    }

    protected void dismissLoadingDialog() {
        if (mainActivity.loadingDialog != null) {
            mainActivity.loadingDialog.dismiss();
        }
    }




}
