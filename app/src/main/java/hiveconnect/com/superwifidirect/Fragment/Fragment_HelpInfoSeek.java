package hiveconnect.com.superwifidirect.Fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import hiveconnect.com.superwifidirect.Bean.Event_ServiceToFragment;
import hiveconnect.com.superwifidirect.Fragment.BasicFragment.MySupportFragment;
import hiveconnect.com.superwifidirect.Model.FileTransfer;
import hiveconnect.com.superwifidirect.R;
import hiveconnect.com.superwifidirect.Service.WifiServerService;
import hiveconnect.com.superwifidirect.Widget.PlayView;

public class Fragment_HelpInfoSeek extends MySupportFragment {


    private static final String TAG = "Fragment_HelpInfoSeek";
    private PlayView button_SeekHelpInfoStart;
    private TextView tv_helpinfo_seeked;
    private String receivedString;
//    private WifiServerService.OnProgressChangListener progressChangListener = new WifiServerService.OnProgressChangListener() {
//        @Override
//        public void onProgressChanged(final FileTransfer fileTransfer, final int progress) {
//
//        }
//
//        @Override
//        public void onProgressChanged() {
//            button_SeekHelpInfoStart.press();
//            button_SeekHelpInfoStart.release();
//        }
//
//        @Override
//        public void onTransferFinished(final File file) {
//
//        }
//
//        @Override
//        public void onTransferFinished(String stringReceived) {
//            receivedString=stringReceived;
//            //changeText(stringReceived);
//
//        }
//    };



    public static Fragment_HelpInfoSeek newInstance() {
        Bundle args = new Bundle();
        Fragment_HelpInfoSeek fragment = new Fragment_HelpInfoSeek();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.layout_fragment_helpinfo_seek, container, false);


        initView();
        EventBus.getDefault().register(this);

        return fragmentView;
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event_ServiceToFragment event) {
        if (event.getTransEvent()== Event_ServiceToFragment.TransEvent.DOING){
            button_SeekHelpInfoStart.press();
            button_SeekHelpInfoStart.release();
            Log.e(TAG,"doing");
        }
        else if (event.getTransEvent()== Event_ServiceToFragment.TransEvent.FINISH){
            button_SeekHelpInfoStart.release();
            tv_helpinfo_seeked.setText(event.getReceivedString());
            Log.e(TAG,"finish");
        }

    };
    private void initView() {
        button_SeekHelpInfoStart=(PlayView)fragmentView.findViewById(R.id.button_SeekHelpInfoStart);
        tv_helpinfo_seeked=(TextView)fragmentView.findViewById(R.id.tv_helpinfo_seeked);
        button_SeekHelpInfoStart.setOnTouchListener( new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){


                int ex = (int) event.getX();
                int ey = (int) event.getY();

                if (ex < 0 || ey < 0 || ex > button_SeekHelpInfoStart.getWidth() || ey > button_SeekHelpInfoStart.getHeight()) {
                    button_SeekHelpInfoStart.release();
                    return true;
                }

                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        button_SeekHelpInfoStart.press();
                        return true;
                    case MotionEvent.ACTION_UP:
                        button_SeekHelpInfoStart.release();
                        button_SeekHelpInfoStart.toggle();
                        //mainActivity.getWifiServerService().setProgressChangListener(progressChangListener);
                        mainActivity.startWifiServerSerivce(WifiServerService.DATA_TYPE.STRING);

                }

                return false;
            }});


    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
    }



}
