package hiveconnect.com.superwifidirect.Task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import hiveconnect.com.superwifidirect.Model.FileTransfer;
import hiveconnect.com.superwifidirect.Util.ByteUtil;
import hiveconnect.com.superwifidirect.Util.Md5Util;

/**
 * 作者：叶应是叶
 * 时间：2018/2/15 8:51
 * 描述：客户端发送Byte
 */
public class SendByteTask extends AsyncTask<String, Integer, Boolean> {

    private ProgressDialog progressDialog;
    private InetAddress targetIP;
    private int targetPort;


    private static final String TAG = "SendByteTask";

    public SendByteTask(Context context, InetAddress targetInetAddress, int targetPort) {

        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("正在发送文件");
        progressDialog.setMax(100);
        this.targetIP=targetInetAddress;
        this.targetPort=targetPort;
    }





    @Override
    protected void onPreExecute() {
        progressDialog.show();
    }



    //execute传来的第一个参数是IP，第二个参数是地址
    @Override
    protected Boolean doInBackground(String... strings) {


        Socket socket = null;
        OutputStream outputStream = null;
        ObjectOutputStream objectOutputStream = null;
        InputStream inputStream = null;

//        try {
//            targetIP = InetAddress.getByAddress(strings[0].getBytes());
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }

//        int PORT=Integer.valueOf(strings[1]);
        Log.e(TAG,"SocketTargetIp是"+targetIP+"SocketPort为"+targetPort);

        try {
//            socket = new Socket();
//            socket.bind(null);
//            socket.connect((new InetSocketAddress(strings[0], Integer.valueOf(strings[1]))), 10000);

            socket = new Socket(targetIP, targetPort);
            outputStream = socket.getOutputStream();



            String StringToSend=strings[0];
            Log.e(TAG,"StringToSend为"+StringToSend);
            byte buf[] = StringToSend.getBytes();
            Log.e(TAG,"bufToString"+buf.toString());
            byte[] lengthbytes = ByteUtil.integerToBytes(buf.length, 4);
            outputStream.write(lengthbytes);
            outputStream.write(buf, 0, buf.length);



            outputStream.flush();



            outputStream.close();
            socket.close();
            outputStream = null;
            socket = null;
            Log.e(TAG, "Byte发送成功,长度为"+buf.length);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Byte发送异常 Exception: " + e.getMessage());
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        progressDialog.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        progressDialog.cancel();
        Log.e(TAG, "onPostExecute: " + aBoolean);
    }

}
