package hiveconnect.com.superwifidirect.Fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

import hiveconnect.com.superwifidirect.Bean.Event_ServiceToFragment;
import hiveconnect.com.superwifidirect.Fragment.BasicFragment.MySupportFragment;
import hiveconnect.com.superwifidirect.Model.FileTransfer;
import hiveconnect.com.superwifidirect.R;
import hiveconnect.com.superwifidirect.Service.WifiServerService;
import hiveconnect.com.superwifidirect.Task.SendByteTask;
import hiveconnect.com.superwifidirect.Task.SendFileTask;
import hiveconnect.com.superwifidirect.Util.FileSizeUtil;
import hiveconnect.com.superwifidirect.Util.Md5Util;
import hiveconnect.com.superwifidirect.Util.UriToPathUtil;
import hiveconnect.com.superwifidirect.Widget.PlayView;

public class Fragment_HelpInfoOffer extends MySupportFragment {


    private static final String TAG = "Fragment_HelpInfoOffer";
    private PlayView button_OfferHelpInfoStart;
    private EditText et_helpinfo_offer;
    private String stringToSend;
    private boolean shouldSend = false;


    public static Fragment_HelpInfoOffer newInstance() {
        Bundle args = new Bundle();
        Fragment_HelpInfoOffer fragment = new Fragment_HelpInfoOffer();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.layout_fragment_helpinfo_offer, container, false);


        initView();
        return fragmentView;
    }

    private void initView() {
        button_OfferHelpInfoStart = (PlayView) fragmentView.findViewById(R.id.button_OfferHelpInfoStart);
        et_helpinfo_offer = (EditText) fragmentView.findViewById(R.id.et_helpinfo_offer);
        final String exampleHelpInfo = "基站名：Z977-03H\n" +
                "经度：33.0935308325\n" +
                "纬度：103.9340565258\n" +
                "海拔：2432米\n" +
                "救援信息：东南方向断崖高度600m切勿前往，若发生山崩泥石流等危险请向西南方向逃生，山坡南侧岩质脆弱切勿攀爬岩石，灾害发生时，土壤质地紧实，植被为多年生木本植物，若来不及逃生可暂时爬到树上躲避等候救援\n" +
                "医疗救助：距离3号急救站约2.8km，向北绕过山口即可到达\n" +
                "毒物提示：此地区尚未发现毒性蛇类出没记录\n" +
                "雷击提示：海拔处于高耸地势，雷雨天气请立刻从北侧山口下山";
        et_helpinfo_offer.setText(exampleHelpInfo);
        button_OfferHelpInfoStart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                int ex = (int) event.getX();
                int ey = (int) event.getY();

                if (ex < 0 || ey < 0 || ex > button_OfferHelpInfoStart.getWidth() || ey > button_OfferHelpInfoStart.getHeight()) {
                    button_OfferHelpInfoStart.release();
                    return true;
                }

                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        button_OfferHelpInfoStart.press();
                        return true;
                    case MotionEvent.ACTION_UP:
                        button_OfferHelpInfoStart.release();
                        button_OfferHelpInfoStart.toggle();
                        shouldSend = !shouldSend;
                        stringToSend = et_helpinfo_offer.getText().toString();
                        if (stringToSend != null) {
                            int TargetPort = 0;
                            InetAddress TargetInetAddress;

                                try
                                {
                                    Thread.currentThread().sleep(3000);//毫秒
                                }
                                catch(Exception e){}
                                for (WifiP2pDevice mWifiP2pDevice : mainActivity.wifiP2pSlaveList) {
                                    TargetPort = mainActivity.devicePortMap.get(mWifiP2pDevice.deviceAddress);
                                    TargetInetAddress = mainActivity.deviceIPMap.get(mWifiP2pDevice.deviceAddress);
                                    if (TargetInetAddress != null && TargetPort != 0) {
                                        //byte[] buffer = TargetInetAddress.toString().getBytes();
                                        Log.e(TAG, "发了一份");
                                        //slaveIP = new String(ByteUtil.readBytes(inputStream, len));
                                        Log.e(TAG, "待发送的ip:" + TargetInetAddress.toString() + ",待发送的port" + TargetPort);
                                        Log.e(TAG,"发送的字符串为:"+stringToSend);
                                        new SendByteTask(mainActivity, TargetInetAddress, TargetPort).execute(stringToSend);
                                    } else {
                                        Log.e(TAG, "TargetPort或者TargetInetAddress为空");
                                    }
                                }

                        }

                }

                return false;
            }
        });


    }


}
