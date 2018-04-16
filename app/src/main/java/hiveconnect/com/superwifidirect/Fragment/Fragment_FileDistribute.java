package hiveconnect.com.superwifidirect.Fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.net.InetAddress;

import hiveconnect.com.superwifidirect.Fragment.BasicFragment.MySupportFragment;
import hiveconnect.com.superwifidirect.Model.FileTransfer;
import hiveconnect.com.superwifidirect.R;
import hiveconnect.com.superwifidirect.Task.SendFileTask;
import hiveconnect.com.superwifidirect.Util.UriToPathUtil;

public class Fragment_FileDistribute extends MySupportFragment {


    private static final String TAG="Fragment_FileDistribute";
    private Button button_ChooseFile;
    private Button button_SendFile;
    private Button button_EndDistribute;
    private TextView tv_FileInDistributeInfo;
    private RecyclerView rv_DeviceInReceiving;


    public static Fragment_FileDistribute newInstance() {
        Bundle args = new Bundle();
        Fragment_FileDistribute fragment = new Fragment_FileDistribute();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.layout_fragment_file_distribute, container, false);


        initView();
        return fragmentView;
    }

    private void initView() {
        button_ChooseFile = (Button) fragmentView.findViewById(R.id.button_ChooseFile);
        button_SendFile = (Button) fragmentView.findViewById(R.id.button_SendFile);
        button_EndDistribute = (Button) fragmentView.findViewById(R.id.button_EndDistribute);
        tv_FileInDistributeInfo = (TextView) fragmentView.findViewById(R.id.tv_FileInDistributeInfo);
        rv_DeviceInReceiving = (RecyclerView) fragmentView.findViewById(R.id.rv_DeviceInReceiving);
        button_ChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG,"onActivityResult,requestCode:"+requestCode+"resultCode"+resultCode);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                if (uri != null) {
                    //String path = getPath(mainActivity, uri);
                    String path= UriToPathUtil.getRealFilePath(mainActivity,uri);
                    if (path != null) {
                        File file = new File(path);
                        if (file.exists() && mainActivity.getmWifiP2pInfo() != null) {
                            FileTransfer fileTransfer = new FileTransfer(file.getPath(), file.length());
                            Log.e(TAG, "待发送的文件：" + fileTransfer);
                            //TODO 这里给SlaveList中所有的对象分发文件
                            int TargetPort=0;
                            InetAddress TargetInetAddress;
                            for(WifiP2pDevice mWifiP2pDevice:mainActivity.wifiP2pSlaveList){
                                TargetPort=mainActivity.devicePortMap.get(mWifiP2pDevice.deviceAddress);
                                TargetInetAddress=mainActivity.deviceIPMap.get(mWifiP2pDevice.deviceAddress);
                                if(TargetInetAddress!=null&&TargetPort!=0) {
                                    //byte[] buffer = TargetInetAddress.toString().getBytes();
                                    Log.e(TAG,"发了一份");
                                    //slaveIP = new String(ByteUtil.readBytes(inputStream, len));
                                    Log.e(TAG,"待发送的ip:"+TargetInetAddress.toString()+",待发送的port"+TargetPort);
                                    Log.e(TAG,"SlavePortMap:"+mainActivity.devicePortMap.toString());
                                    new SendFileTask(mainActivity, fileTransfer,TargetInetAddress,TargetPort).execute();
                                }else {
                                    Log.e(TAG,"TargetPort或者TargetInetAddress为空");
                                }
                            }

                        }else {
                            Log.e(TAG,"file不存在");
                        }
                    }else{
                        Log.e(TAG,"path不存在");
                    }
                }else {
                    Log.e(TAG,"uri不存在");
                }
            }
        }
    }

    private String getPath(Context context, Uri uri) {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{"_data"}, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    String data = cursor.getString(cursor.getColumnIndex("_data"));
                    cursor.close();
                    return data;
                }
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

}
