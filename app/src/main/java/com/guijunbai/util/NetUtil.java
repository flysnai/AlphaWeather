package com.guijunbai.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by 44516 on 2018/1/25.
 */

public class NetUtil {
    public static boolean getNetworkState(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (NetworkInfo.State.CONNECTED.equals(networkInfo.getState())) {
                return true;
            }
        }
        return false;
    }
}
