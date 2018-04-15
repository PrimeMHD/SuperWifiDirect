package hiveconnect.com.superwifidirect.Service;

import android.app.IntentService;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import hiveconnect.com.superwifidirect.Activity.MainActivity;
import hiveconnect.com.superwifidirect.Util.ByteUtil;
import hiveconnect.com.superwifidirect.Util.SysFreePort;


public class HandshakeServerService extends IntentService {


    private MainActivity mainActivity;
    public enum GroupCharactor{NOCHA,MASTER,SLAVE};
    private static final String TAG = "HandshakeServerService";
    private static GroupCharactor mGroupCharactor=GroupCharactor.MASTER;
    private HandshakeFinishLisner mHandshakeFinishLisner;

    public void setmHandshakeFinishLisner(HandshakeFinishLisner mHandshakeFinishLisner) {
        this.mHandshakeFinishLisner = mHandshakeFinishLisner;
    }

    public interface HandshakeFinishLisner{
        void onHandshakeFinish(String SlaveMac);
        void onHandshakeFinish();
    }



    private ServerSocket serverSocket;

    private InputStream inputStream;

    private ObjectInputStream objectInputStream;


    private static final int PORT = 4786;//握手的默认端口

    public class MyBinder extends Binder {
        public HandshakeServerService getService() {
            return HandshakeServerService.this;
        }
        public void setGroupCharacter(GroupCharactor groupCharactor){
            mGroupCharactor=groupCharactor;
        }
        public void bindMainActivity(MainActivity realMainActivity){
            mainActivity=realMainActivity;
        }
    }

    public HandshakeServerService() {
        super("WifiServerService");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG,"调用了onHandleIntent"+mGroupCharactor);
        clean();
        switch (mGroupCharactor){
            case MASTER://Master要接收来自Slave发送的（汇报的）ip地址（一个字符串）
                try {
                    serverSocket = new ServerSocket();
                    serverSocket.setReuseAddress(true);
                    serverSocket.bind(new InetSocketAddress(PORT));
                    Socket client = serverSocket.accept();
                    Log.e(TAG, "客户端IP地址 : " + client.getInetAddress().getHostAddress());
                    inputStream = client.getInputStream();
                    //objectInputStream = new ObjectInputStream(inputStream);
                    //FileTransfer fileTransfer = (FileTransfer) objectInputStream.readObject();
                    //Log.e(TAG, "待接收的文件: " + fileTransfer);
                    //String name = new File(fileTransfer.getFilePath()).getName();
                    //将文件存储至指定位置
                    //file = new File(Environment.getExternalStorageDirectory() + "/" + name);
                    //fileOutputStream = new FileOutputStream(file);

                    byte buf[] = new byte[512];


                    int len= ByteUtil.bytesToInteger(ByteUtil.readBytes(inputStream,4));
                    String slaveIP = new String(ByteUtil.readBytes(inputStream, len));

                    Log.e(TAG, "握手收到了SlaveIP:"+slaveIP);




                    len=ByteUtil.bytesToInteger(ByteUtil.readBytes(inputStream,4));
                    String SlaveP2pDeviceMac = new String(ByteUtil.readBytes(inputStream, len)).trim();
                    Log.e(TAG, "握手收到了SlaveP2pDevice:"+SlaveP2pDeviceMac);



                    String[] slaveIpSplitStr = slaveIP.split("\\.");
                    byte[] ipBuf = new byte[4];
                    for(int i = 0; i < 4; i++){
                        ipBuf[i] = (byte)(Integer.parseInt(slaveIpSplitStr[i])&0xff);
                    }

                    mainActivity.deviceIPMap.put(SlaveP2pDeviceMac, InetAddress.getByAddress(ipBuf));
                    Log.e(TAG,"ipbuf"+InetAddress.getByAddress(ipBuf));



                    serverSocket.close();
                    inputStream.close();

                    serverSocket = null;
                    inputStream = null;
                    objectInputStream = null;

                } catch (Exception e) {
                    Log.e(TAG, "文件接收 Exception: " + e.getMessage());
                } finally {
                    clean();
                    if(mHandshakeFinishLisner!=null){
                        mHandshakeFinishLisner.onHandshakeFinish();
                    }

                    //再次启动服务，等待客户端下次连接
                    startService(new Intent(this, HandshakeServerService.class));
                }


                break;
            case SLAVE:
                break;
        }




    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG,"这就销毁了");
        clean();
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

    }

}
