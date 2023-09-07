package com.app.allfilerecovery.utils;

import android.content.Context;
import android.net.ConnectivityManager;

public class NetworkUtil {

//    public static boolean isInternetConnected(Context context){
//        boolean have_WIFI= false;
//        boolean have_MobileData = false;
//        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(CONNECTIVITY_SERVICE);
//        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
//        for(NetworkInfo info:networkInfos){
//            if (info.getTypeName().equalsIgnoreCase("WIFI"))if (info.isConnected())have_WIFI=true;
//            if (info.getTypeName().equalsIgnoreCase("MOBILE DATA"))if (info.isConnected())have_MobileData=true;
//        }
//        return have_WIFI||have_MobileData;
//    }

    public static boolean isInternetConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

}
