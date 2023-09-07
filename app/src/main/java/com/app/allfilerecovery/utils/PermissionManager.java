package com.app.allfilerecovery.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class PermissionManager {

    public static boolean permissionCheck(Context context, String permission) {

        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
            return true;
        } else if (shouldShowRequestPermissionRationale()) {
            // In an educational UI, explain to the user why your app requires this
            // permission for a specific feature to behave as expected. In this UI,
            // include a "cancel" or "no thanks" button that allows the user to
            // continue using your app without granting the permission.
            return false;
        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            ActivityCompat.requestPermissions((Activity) context, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,}, Const.READ_STORAGE_CODE);

            return false;
        }

    }

    private static boolean shouldShowRequestPermissionRationale() {
        return false;
    }

}
