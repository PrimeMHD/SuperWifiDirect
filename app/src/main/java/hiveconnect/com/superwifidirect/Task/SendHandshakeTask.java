package hiveconnect.com.superwifidirect.Task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;



import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import hiveconnect.com.superwifidirect.Util.ByteUtil;


public class SendHandshakeTask extends AsyncTask<String, Integer, Boolean> {


    public enum GroupCharactor {NOCHA, MASTER, SLAVE};



    private static final int PORT = 4786;
    private InetAddress targetIP;
    private GroupCharactor mGroupCharactor;
    //private String StringToSend;//发送一个字符串，可能是端口或者IP
    private static final String TAG = "SendHandshakeTask";


    public SendHandshakeTask(InetAddress targetIP, GroupCharactor mGroupCharactor) {
        this.targetIP = targetIP;
        this.mGroupCharactor = mGroupCharactor;

    }

    @Override
    protected void onPreExecute() {
        Log.e(TAG,"准备开始AsyncTask");
    }

    @Override
    protected Boolean doInBackground(String... strings) {

        String StringToSend=strings[0];
        String SelfP2pDeviceMac=strings[1];
        Socket socket = null;
        OutputStream outputStream = null;
        ObjectOutputStream objectOutputStream = null;
        switch (mGroupCharactor) {
            case SLAVE:
                try {
                    //Slave要汇报自己的IP，stringToSend里存储的是Slave的IP

                    socket = new Socket(targetIP, PORT);
                    outputStream = socket.getOutputStream();

                    byte[] buffer = StringToSend.getBytes("GBK");
                    byte[] lengthbytes = ByteUtil.integerToBytes(StringToSend.length(), 4);
                    outputStream.write(lengthbytes);
                    outputStream.write(buffer, 0, buffer.length);
                    Log.e(TAG,"StringToSend:"+StringToSend+"length:"+ByteUtil.bytesToInteger(lengthbytes));

                    buffer = SelfP2pDeviceMac.getBytes("GBK");
                    lengthbytes = ByteUtil.integerToBytes(SelfP2pDeviceMac.length(), 4);
                    outputStream.write(lengthbytes);
                    outputStream.write(buffer, 0, buffer.length);
                    outputStream.flush();
                    Log.e(TAG,"StringToSend:"+SelfP2pDeviceMac+"length:"+ByteUtil.bytesToInteger(lengthbytes));


                    outputStream.close();
                    socket.close();
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
                    if (socket != null) {
                        try {
                            socket.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }


                break;
            case MASTER:
                try {
                    //Master要告诉Slave新分配的端口

                    socket = new Socket(targetIP, PORT);
                    outputStream = socket.getOutputStream();

                    byte[] buffer = StringToSend.getBytes("GBK");
                    byte[] lengthbytes = ByteUtil.integerToBytes(StringToSend.length(), 4);
                    outputStream.write(lengthbytes);
                    outputStream.write(buffer, 0, buffer.length);
                    Log.e(TAG,"Master:StringToSend:"+StringToSend+"length:"+ByteUtil.bytesToInteger(lengthbytes));
                    outputStream.flush();


                    outputStream.close();
                    socket.close();
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
                    if (socket != null) {
                        try {
                            socket.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
        }


        return false;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {

    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {

        Log.e(TAG, "onPostExecute: " + aBoolean);


    }

}
