package com.example.parking.com.example.IOSocket;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;
import android.view.ViewDebug;


import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.parking.HeadProto;
import com.example.parking.LoginActivity;
import com.example.parking.LoginProto;
import com.example.parking.R;
import com.example.parking.functionActivity;
import com.example.parking.information.SocketServiceInfo;
import com.example.parking.interfaces.ICommunication;
import com.google.protobuf.InvalidProtocolBufferException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import Function.PackRequest;


public class SocketService extends Service {
    private static final String TAG = "SocketService";
    //心跳包频率
    private static final long HEART_BEAT_RATE = 30 * 1000;

    public static final String HOST = "8.129.79.24";
    public static final int PORT = 6000;


    public static final String HEART_BEAT_STRING="Client_Heart";//心跳包内容

    private ReadThread mReadThread;

    private LocalBroadcastManager mLocalBroadcastManager;

    private WeakReference<Socket> mSocket;

    private boolean isSocketInit = false;

    private Map<String,String> headMap;

    NotificationManager notificationManager;
    String notificationId = "channelId";

    String notificationName = "channelName";



    private long sendTime = 0L;



    private void startForegroundService() {

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //创建NotificationChannel
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(notificationId, notificationName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        startForeground(1,getNotification());
    }

    private Notification getNotification() {

        Notification.Builder builder = new Notification.Builder(this)

                .setSmallIcon(R.mipmap.ic_launcher)

                .setContentTitle("网络服务")

                .setContentText("网络服务正在运行...");
        //设置Notification的ChannelID,否则不能正常显示

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            builder.setChannelId(notificationId);

        }
        Notification notification = builder.build();

        return notification;

    }

        private Handler mHandler = new Handler();
    private Runnable heartBeatRunnable = new Runnable() {

        @Override
        public void run() {
            if (System.currentTimeMillis() - sendTime >= HEART_BEAT_RATE) {
                boolean isSuccess = false;//就发送一个HEART_BEAT_STRING过去 如果发送失败，就重新初始化一个socket
                    isSuccess = sendMsg(HEART_BEAT_STRING.getBytes());

                if (!isSuccess) {
                    mHandler.removeCallbacks(heartBeatRunnable);
                    mReadThread.release();
                    releaseLastSocket(mSocket);
                    new InitSocketThread().start();
                }
            }
            mHandler.postDelayed(this, HEART_BEAT_RATE);
        }
    };


    // 提供绑定service 外部通信
    private class InnerBinder extends Binder implements ICommunication {

        @Override
        public boolean sendMessage(byte[] message) {

            return sendMsg(message);
        }
    }
    @Override
    public IBinder onBind(Intent arg0) {
        return new InnerBinder();
    }

    @Override
    public void onCreate() {


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        startForegroundService();
        super.onCreate();

        headMap = new HashMap<>();
        headMap.put(SocketServiceInfo.MESSAGE_LOGIN,SocketServiceInfo.ACTIVITY_LOGIN); // 登录
        headMap.put(SocketServiceInfo.MESSAGE_FINDPWD,SocketServiceInfo.ACTIVITY_FINDPWD); // 找回密码
        headMap.put(SocketServiceInfo.MESSAGE_REGISTER,SocketServiceInfo.ACTIVITY_REGISTER); // 注册
        headMap.put(SocketServiceInfo.MESSAGE_SELECTCAR,SocketServiceInfo.ACTIVITY_SELECTCAR); // 预定车位

        if(!isSocketInit) {

            new InitSocketThread().start();
            isSocketInit = true;
        }

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);

        Log.i(TAG,"Socket init ok !!");


    }
    public boolean sendMsg(final byte[] msg)  {
        if (null == mSocket || null == mSocket.get()) {
            return false;
        }
        /*
        Boolean isSend = null;

        Callable<Boolean> callable = new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                Socket soc = mSocket.get();
                try {
                    if (!soc.isClosed() && !soc.isOutputShutdown()) {
                        OutputStream os = soc.getOutputStream();
                        os.write(msg);
                        os.flush();
                        Log.i(TAG, "sendMessage is = " +msg.toString());
                        sendTime = System.currentTimeMillis();//每次发送成数据，就改一下最后成功发送的时间，节省心跳间隔时间
                    } else {
                        return false;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }

                return true;
            }
        };

        FutureTask<Boolean> futureTask = new FutureTask<>(callable);
        futureTask.run();
        isSend = futureTask.get();
        while (isSend != null) {
            isSend = null;
            return true;
        }


        return false;

         */

        Socket soc = mSocket.get();
        //PrintWriter pw;
        try {
            if (!soc.isClosed() && !soc.isOutputShutdown()) {
                OutputStream os = soc.getOutputStream();
                os.write(msg);
                os.flush();
                 Log.i(TAG, "sendMessage is = " +msg.toString());
                sendTime = System.currentTimeMillis();//每次发送成数据，就改一下最后成功发送的时间，节省心跳间隔时间
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;


    }

    private void initSocket() {//初始化Socket
        try {
            Socket so = new Socket(HOST, PORT);
            mSocket = new WeakReference<Socket>(so);
            mReadThread = new ReadThread(so);
            mReadThread.start();

            mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE); //初始化成功后，就准备发送心跳包

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void releaseLastSocket(WeakReference<Socket> mSocket) {
        try {
            if (null != mSocket) {
                Socket sk = mSocket.get();
                if (!sk.isClosed()) {
                    sk.close();
                    Log.i(TAG,"Socket close!");
                }
                sk = null;
                mSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class InitSocketThread extends Thread {
        @Override
        public void run() {
            super.run();
            initSocket();
        }
    }

    // Thread to read content from Socket
    class ReadThread extends Thread {
        private WeakReference<Socket> mWeakSocket;
        private boolean isStart = true;

        public ReadThread(Socket socket) {
            mWeakSocket = new WeakReference<Socket>(socket);
        }

        public void release() {
            isStart = false;
            releaseLastSocket(mWeakSocket);
        }

        @Override
        public void run() {
            super.run();
            Socket socket = mWeakSocket.get();
            if (null != socket) {

                try {
                    InputStream is = socket.getInputStream();

                    byte[] buffer = new byte[1024 * 4];
                    int length = 0;

                    ByteArrayOutputStream output = new ByteArrayOutputStream();

                    while (!socket.isClosed() && !socket.isInputShutdown()
                            && isStart && ((length = is.read(buffer)) != -1)) {
                        if (length > 0) {


                            String message = new String(Arrays.copyOf(buffer,
                                    length));
                            String headSizeInfo = message.substring(0,4);
                            int headSize = PackRequest.byteArrayToInt(headSizeInfo.getBytes());
                            String headerString = message.substring(4,4+headSize);

                            HeadProto.Head headProto = HeadProto.Head.parseFrom(headerString.getBytes());

                            String bodyString = message.substring(4+headSize);
                            byte[] result = bodyString.getBytes();
                            String val = headMap.get(headProto.getHead());

                            Log.i(TAG,"head is "+headProto.getHead());

                            if(val != null) {

                                Intent intent=new Intent(val);
                                intent.putExtra(headProto.getHead(),result);
                                mLocalBroadcastManager.sendBroadcast(intent);

                            } else  {

                                Log.i(TAG,"map size is"+headMap.size());
                                Log.i(TAG,"请求未找到");
                            }



/*



                            try {



                                LoginProto.LoginResponse response = LoginProto.LoginResponse.parseFrom(result);
                               // LoginProto.LoginResponse response = LoginProto.LoginResponse.newBuilder().mergeFrom(result).build();

                                String name = response.getMsgname();
                                String word = response.getMsgword();
                                //String info = response.getMsg();
                            } catch (InvalidProtocolBufferException e) {
                                e.printStackTrace();
                            }


 */


                            /*
                            byte[] headInfo = new byte[4];
                            System.arraycopy(result,0,headInfo,0,4);
                            int headSize = PackRequest.byteArrayToInt(headInfo);
                            byte[] headMsg = new byte[headSize];
                            System.arraycopy(result,4,headMsg,0,headSize);

                            String head = headMsg.toString();
                            byte[] lastResult = new byte[result.length-4-headSize];
                            System.arraycopy(result,4+headSize,lastResult,0,lastResult.length);
                            Log.e(TAG, message);
                            //收到服务器过来的消息，就通过Broadcast发送出去

                             */
                            /*
                            if(message.equals(HEART_BEAT_STRING)){//处理心跳回复

                             */
                                /*

                               可以通知处理心跳

                                 */

                          /*
                            }else if(message.equals(SocketServiceInfo.MESSAGE_LOGIN)) {


                           */
                                // 登录消息回复
                                /*
                                Intent intent=new Intent(SocketServiceInfo.ACTIVITY_LOGIN);
                                intent.putExtra(SocketServiceInfo.KEY_LOGIN,lastResult);
                                mLocalBroadcastManager.sendBroadcast(intent);

                                 */

                        //    } else if(message.equals("ok")){




                                //其他消息回复

                            /*
                            }else  {

                                Intent intent=new Intent(SocketServiceInfo.ACTIVITY_LOGIN);
                                intent.putExtra(SocketServiceInfo.KEY_LOGIN,result);
                                mLocalBroadcastManager.sendBroadcast(intent);

                            }

                             */
                            //output.write(buffer, 0, length);
/*
                            LoginProto.LoginResponse response = LoginProto.LoginResponse.parseFrom(result);
                            String name = response.getMsgname();
                            String word = response.getMsgword();

                            Log.i(TAG,"name is "+name);
                            Log.i(TAG,"word is"+word);

 */

                            /*
                            Intent intent=new Intent(SocketServiceInfo.ACTIVITY_LOGIN);
                            intent.putExtra(SocketServiceInfo.KEY_LOGIN,result);
                            mLocalBroadcastManager.sendBroadcast(intent);

                             */
                        }
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
