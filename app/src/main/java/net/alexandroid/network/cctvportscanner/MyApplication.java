package net.alexandroid.network.cctvportscanner;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import net.alexandroid.shpref.ShPref;
import net.alexandroid.utils.mylog.MyLog;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        ShPref.init(this, ShPref.APPLY);
        MyLog.init(this);
        MyLog.setTag("ZAQ");
    }
}
