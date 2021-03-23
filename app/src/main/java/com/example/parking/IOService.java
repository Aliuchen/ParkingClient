package com.example.parking;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.parking.com.example.IOSocket.SocketService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IOService extends Service {


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private static final String TAG = "SocketService";
    // 心跳包频率
    private static final long HEART_BEAT_RATE = 30 * 1000;
    public static final String HOST = "8.129.79.24";
    public static final int POST = 6000;

    public static final String HEART_BEAT_ACTION = "com.example.parking.functionActivity";
    public static final String MESSAGE_ACTION = "com.example.parking.functionActivity";

    //心跳包内容
    public static final String HEART_BEAT_STRING = "ok";

    private LocalBroadcastManager mLocalBroadcastManager;

    private IOService.ReadThread mReadThread;

    private WeakReference<Socket> mSocket;

    private long sendTime = 0L;

    private ExecutorService executor = Executors.newFixedThreadPool(3);

    private Handler mHandler = new Handler();

    private Runnable heartBeatRunnable = new Runnable() {

        @Override
        public void run() {

            if(System.currentTimeMillis() - sendTime >= HEART_BEAT_RATE) {

                boolean isSuccess = sendMsg(HEART_BEAT_STRING);

                Log.i(TAG,"socket init");

                if (!isSuccess) {
                    mHandler.removeCallbacks(heartBeatRunnable);
                    mReadThread.release();
                    releaseLastSocket(mSocket);
                    new IOService.InitSocketThread().start();
                }

            }

            mHandler.postDelayed(this, HEART_BEAT_RATE);

        }
    };

    public boolean sendMsg (final  String  msg) {

        if(mSocket == null || null == mSocket.get()) {
            return false;
        }

        final Socket soc = mSocket.get();

        if (!soc.isClosed() && !soc.isOutputShutdown()) {

            executor.execute(new Runnable() {

                @Override
                public void run() {
                    try {
                        OutputStream os = soc.getOutputStream();
                        String message = msg;
                        os.write(message.getBytes());
                        os.flush();
                        //每次发送成数据，就改一下最后成功发送的时间，节省心跳间隔时间
                        sendTime = System.currentTimeMillis();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }else {

            return false;
        }

        return true;

    }


    private void releaseLastSocket(WeakReference<Socket> mSocket) {

        try {
            if( null != mSocket) {
                Socket st = mSocket.get();
                if(!st.isClosed()) {
                    st.close();
                }
                st = null;
                mSocket = null;
            }
        } catch (IOException e) {

            e.printStackTrace();
        }


    }


    private void initSocket() {

        try {
            Socket so = new Socket(HOST,POST);

            Log.i(TAG,"Socket init");

            mSocket = new WeakReference<Socket>(so);
            mReadThread = new IOService.ReadThread(so);
            mReadThread.start();

            mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);
        } catch (UnknownHostException e) {
            e.printStackTrace();
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

            if(null != socket) {

                try {

                    InputStream is = socket.getInputStream();
                    byte[] buffer = new byte[1024 * 4];
                    int length = 0;
                    while(!socket.isClosed() && !socket.isInputShutdown() && isStart && ((length = is.read(buffer)) != -1)) {

                        if(length > 0) {

                            String message = new String(Arrays.copyOf(buffer,
                                    length)).trim();

                            if (message.equals(HEART_BEAT_STRING)) {
                                Intent intent = new Intent(HEART_BEAT_ACTION);
                                mLocalBroadcastManager.sendBroadcast(intent);
                            }else {

                                Intent intent = new Intent(MESSAGE_ACTION);
                                intent.putExtra("msg", message);
                                sendBroadcast(intent);
                            }
                        }

                    }
                } catch (IOException e) {

                    e.printStackTrace();

                }
            }


        }

    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }
}
