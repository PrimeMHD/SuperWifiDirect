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
import hiveconnect.com.superwifidirect.Util.Md5Util;

/**
 * 作者：叶应是叶
 * 时间：2018/2/15 8:51
 * 描述：客户端发送文件
 */
public class SendByteTask extends AsyncTask<String, Integer, Boolean> {

    private ProgressDialog progressDialog;
    private InetAddress targetIP;
    private int targetPort;
    private FileTransfer fileTransfer;

   //private static final int PORT = 4786;

    private static final String TAG = "SendFileTask";

    public SendByteTask(Context context, FileTransfer fileTransfer, InetAddress targetInetAddress, int targetPort) {
        this.fileTransfer = fileTransfer;
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
        fileTransfer.setMd5(Md5Util.getMd5(new File(fileTransfer.getFilePath())));
        Log.e(TAG, "文件的MD5码值是：" + fileTransfer.getMd5());
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
            objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(fileTransfer);
            inputStream = new FileInputStream(new File(fileTransfer.getFilePath()));
            long fileSize = fileTransfer.getFileLength();
            long total = 0;
            byte buf[] = new byte[512];
            int len;
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
                total += len;
                int progress = (int) ((total * 100) / fileSize);
                publishProgress(progress);
                Log.e(TAG, "文件发送进度：" + progress);
            }
            outputStream.close();
            objectOutputStream.close();
            inputStream.close();
            socket.close();
            outputStream = null;
            objectOutputStream = null;
            inputStream = null;
            socket = null;
            Log.e(TAG, "文件发送成功");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "文件发送异常 Exception: " + e.getMessage());
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
