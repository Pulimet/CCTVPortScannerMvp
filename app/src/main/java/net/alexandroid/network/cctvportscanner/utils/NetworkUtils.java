package net.alexandroid.network.cctvportscanner.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import net.alexandroid.shpref.Contextor;

public class NetworkUtils {
    public static boolean isConnected() {
        NetworkInfo info = NetworkUtils.getNetworkInfo(Contextor.getInstance().getContext());
        return (info != null && info.isConnected());
    }

    private static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm == null ? null : cm.getActiveNetworkInfo();
    }
}
