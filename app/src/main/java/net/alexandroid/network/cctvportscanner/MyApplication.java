package net.alexandroid.network.cctvportscanner;

import android.app.Application;

import net.alexandroid.shpref.ShPref;
import net.alexandroid.utils.mylog.MyLog;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ShPref.init(this, ShPref.APPLY);
        MyLog.init(this);
        MyLog.setTag("ZAQ");
    }
}
