package net.alexandroid.network.cctvportscanner.ping;

import net.alexandroid.utils.mylog.MyLog;

import java.io.IOException;

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
        Runtime runtime = Runtime.getRuntime();
        try {
            Process mIpAddrProcess = runtime.exec("/system/bin/ping -c 1 " + host);
            int mExitValue = mIpAddrProcess.waitFor();
            MyLog.d(" mExitValue " + mExitValue);
            return mExitValue == 0;
        } catch (InterruptedException | IOException ex) {
            ex.printStackTrace();
            MyLog.d("Exception:" + ex);
        }
        return false;
    }

    public interface CallBack {
        void onResult(String host, boolean pingResult);
    }
}
