package hiveconnect.com.superwifidirect.Fragment.BasicFragment;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import hiveconnect.com.superwifidirect.Activity.MainActivity;
import hiveconnect.com.superwifidirect.Service.WifiServerService;
import hiveconnect.com.superwifidirect.util.LoadingDialog;
import me.yokeyword.fragmentation.SupportFragment;

public class MySupportFragment extends SupportFragment {

    protected Context mContext;
    protected MainActivity mainActivity;
    protected WifiP2pManager wifiP2pManager;
    protected WifiP2pManager.Channel channel;
    //private BroadcastReceiver broadcastReceiver;
    protected WifiServerService wifiServerService;
    //protected ProgressDialog progressDialog;
    //protected FragmentManager fragmentManager;




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
