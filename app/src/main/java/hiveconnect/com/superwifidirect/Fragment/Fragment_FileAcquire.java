package hiveconnect.com.superwifidirect.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;

import java.io.File;
import java.util.Locale;

import hiveconnect.com.superwifidirect.Fragment.BasicFragment.MySupportFragment;
import hiveconnect.com.superwifidirect.Model.FileTransfer;
import hiveconnect.com.superwifidirect.R;
import hiveconnect.com.superwifidirect.Service.WifiServerService;

public class Fragment_FileAcquire extends MySupportFragment {

    private Button button_ReceiveFile;
    private static final String TAG="Fragment_FileAcquire";
    private WifiServerService.OnProgressChangListener progressChangListener = new WifiServerService.OnProgressChangListener() {
        @Override
        public void onProgressChanged(final FileTransfer fileTransfer, final int progress) {

        }

        @Override
        public void onTransferFinished(final File file) {

            if (file != null && file.exists()) {
                openFile(file.getPath());
            }

        }
    };


    public static Fragment_FileAcquire newInstance() {
        Bundle args = new Bundle();
        Fragment_FileAcquire fragment = new Fragment_FileAcquire();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.layout_fragment_file_acquire, container, false);


        initView();
        return fragmentView;
    }


    public void initView(){
        button_ReceiveFile=(Button)fragmentView.findViewById(R.id.button_ReceiveFile);
        button_ReceiveFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.getWifiServerService().setProgressChangListener(progressChangListener);
                mainActivity.startWifiServerSerivce();

            }
        });
    }



    private void openFile(String filePath) {
        String ext = filePath.substring(filePath.lastIndexOf('.')).toLowerCase(Locale.US);
        try {
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            String mime = mimeTypeMap.getMimeTypeFromExtension(ext.substring(1));
            mime = TextUtils.isEmpty(mime) ? "" : mime;
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(filePath)), mime);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "文件打开异常：" + e.getMessage());
            showToast("文件打开异常：" + e.getMessage());
        }
    }


}
