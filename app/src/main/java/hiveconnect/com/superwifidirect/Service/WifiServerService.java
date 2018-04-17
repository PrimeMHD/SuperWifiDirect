package hiveconnect.com.superwifidirect.Service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;


import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import hiveconnect.com.superwifidirect.Bean.Event_ServiceToFragment;
import hiveconnect.com.superwifidirect.Model.FileTransfer;
import hiveconnect.com.superwifidirect.Util.ByteUtil;
import hiveconnect.com.superwifidirect.Util.Md5Util;

/**
 * 作者：叶应是叶
 * 时间：2018/2/14 21:09
 * 描述：服务器端接收文件
 */
public class WifiServerService extends IntentService {

    private static final String TAG = "WifiServerService";
    public enum DATA_TYPE{FILE,STRING};
    private DATA_TYPE mdata_type;
    private String stringReceived;
    public interface OnProgressChangListener {

        //当传输进度发生变化时
        void onProgressChanged(FileTransfer fileTransfer, int progress);
        void onProgressChanged();

        //当传输结束时
        void onTransferFinished(File file);
        void onTransferFinished(String stringReceived);

    }

    private ServerSocket serverSocket;

    private InputStream inputStream;

    private ObjectInputStream objectInputStream;

    private FileOutputStream fileOutputStream;

    private OnProgressChangListener progressChangListener;

    private  int PORT = 4786;

    public class MyBinder extends Binder {
        public WifiServerService getService() {
            return WifiServerService.this;
        }
        public void setPORT(int port){
            PORT=port;
        }
        public void setData_type(DATA_TYPE data_type){mdata_type = data_type;}
    }



    public WifiServerService() {
        super("WifiServerService");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        switch (mdata_type){
            case FILE:
                Log.e(TAG,"准备接收文件,接收端口为"+PORT);
                clean();
                File file = null;
                try {
                    serverSocket = new ServerSocket();
                    serverSocket.setReuseAddress(true);
                    serverSocket.bind(new InetSocketAddress(PORT));
                    Socket client = serverSocket.accept();
                    Log.e(TAG, "客户端IP地址 : " + client.getInetAddress().getHostAddress());
                    inputStream = client.getInputStream();
                    objectInputStream = new ObjectInputStream(inputStream);
                    FileTransfer fileTransfer = (FileTransfer) objectInputStream.readObject();
                    Log.e(TAG, "待接收的文件: " + fileTransfer);
                    String name = new File(fileTransfer.getFilePath()).getName();
                    //将文件存储至指定位置
                    file = new File(Environment.getExternalStorageDirectory() + "/" + name);
                    fileOutputStream = new FileOutputStream(file);
                    byte buf[] = new byte[512];
                    int len;
                    long total = 0;
                    int progress;
                    while ((len = inputStream.read(buf)) != -1) {
                        fileOutputStream.write(buf, 0, len);
                        total += len;
                        progress = (int) ((total * 100) / fileTransfer.getFileLength());
                        Log.e(TAG, "文件接收进度: " + progress);
                        EventBus.getDefault().post(new Event_ServiceToFragment(Event_ServiceToFragment.TransEvent.DOING));
                        if (progressChangListener != null) {
                            progressChangListener.onProgressChanged(fileTransfer, progress);
                        }
                    }
                    EventBus.getDefault().post(new Event_ServiceToFragment(Event_ServiceToFragment.TransEvent.FINISH));

                    serverSocket.close();
                    inputStream.close();
                    objectInputStream.close();
                    fileOutputStream.close();
                    serverSocket = null;
                    inputStream = null;
                    objectInputStream = null;
                    fileOutputStream = null;
                    Log.e(TAG, "文件接收成功，文件的MD5码是：" + Md5Util.getMd5(file));
                } catch (Exception e) {
                    Log.e(TAG, "文件接收 Exception: " + e.getMessage());
                } finally {
                    clean();
                    if (progressChangListener != null) {
                        progressChangListener.onTransferFinished(file);
                    }
                    //再次启动服务，等待客户端下次连接
                    startService(new Intent(this, WifiServerService.class));
                }
                break;
            case STRING:
                Log.e(TAG,"准备接收字符串,接收端口为"+PORT);
                clean();
                try {
                    serverSocket = new ServerSocket();
                    serverSocket.setReuseAddress(true);
                    serverSocket.bind(new InetSocketAddress(PORT));
                    Socket client = serverSocket.accept();
                    Log.e(TAG, "客户端IP地址 : " + client.getInetAddress().getHostAddress());
                    inputStream = client.getInputStream();



                    int len= ByteUtil.bytesToInteger(ByteUtil.readBytes(inputStream,4));
                    Log.e(TAG,"接受文字Byte大小:"+len);
                    stringReceived= new String(ByteUtil.readBytes(inputStream, len));


                    Log.e(TAG,"StringReceived"+stringReceived);
                    serverSocket.close();
                    inputStream.close();

                    serverSocket = null;
                    inputStream = null;
                } catch (Exception e) {
                    Log.e(TAG, "字符串接收 Exception: " + e.getMessage());
                } finally {

                    if (progressChangListener != null) {
                        progressChangListener.onTransferFinished(stringReceived);
                    }
                    EventBus.getDefault().post(new Event_ServiceToFragment(Event_ServiceToFragment.TransEvent.FINISH,stringReceived));
                    clean();
                    //再次启动服务，等待客户端下次连接
                    startService(new Intent(this, WifiServerService.class));
                }
                break;
        }





    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clean();
    }

    public void setProgressChangListener(OnProgressChangListener progressChangListener) {
        this.progressChangListener = progressChangListener;
    }

    private void clean() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
                serverSocket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (inputStream != null) {
            try {
                inputStream.close();
                inputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (objectInputStream != null) {
            try {
                objectInputStream.close();
                objectInputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (fileOutputStream != null) {
            try {
                fileOutputStream.close();
                fileOutputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
