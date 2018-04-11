package hiveconnect.com.superwifidirect.Fragment;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import hiveconnect.com.superwifidirect.Fragment.BasicFragment.MySupportFragment;
import hiveconnect.com.superwifidirect.R;

public class Fragment_GroupFind extends MySupportFragment {


    private Button btn_quitGroup;
    private Button btn_chooseFile;
    private Button btn_searchGroup;


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
        initView();
        return fragmentView;
    }

    private void initView() {
        btn_chooseFile=(Button)fragmentView.findViewById(R.id.btn_chooseFile);
        btn_searchGroup=(Button)fragmentView.findViewById(R.id.btn_searchGroup);
        btn_quitGroup=(Button)fragmentView.findViewById(R.id.btn_quitGroup);
        btn_searchGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mWifiP2pEnabled){
                    showToast("请先打开wifi开关");

                }else{
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


    }


}
