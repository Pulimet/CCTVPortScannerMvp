package net.alexandroid.network.cctvportscanner.ping;

import net.alexandroid.utils.mylog.MyLog;

import java.io.IOException;
import java.net.InetAddress;

public class PingRunnable implements Runnable {

    public static final int TIMEOUT = 5000;

    private String mHost;
    private CallBack mCallBack;

    public PingRunnable(String host, CallBack callBack) {
        mHost = host;
        mCallBack = callBack;
    }

    @Override
    public void run() {
        new Thread(timeOut).start();
        boolean result = pingHost(mHost);
        if (mCallBack != null) {
            mCallBack.onResult(mHost, result);
            mCallBack = null;
        }
    }

    private Runnable timeOut = new Runnable() {
        @Override
        public void run() {
            MyLog.d("Start timer");
            try {
                Thread.sleep(TIMEOUT);
            } catch (InterruptedException pE) {
                pE.printStackTrace();
            }
            MyLog.d("End timer");
            onTimeOut();
        }
    };

    private void onTimeOut() {
        if (mCallBack != null) {
            mCallBack.onResult(mHost, false);
            mCallBack = null;
        }
    }


    private boolean pingHost(String host) {
        MyLog.d("pingHost");
        try {
            return InetAddress.getByName(host).isReachable(TIMEOUT);
        } catch (IOException ex) {
            MyLog.e("Exception:" + ex.getMessage());
            return false;
        }
    }

    public interface CallBack {
        void onResult(String host, boolean pingResult);
    }
}
