package com.app.allfilerecovery.view.permission;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.amazic.ads.callback.NativeCallback;
import com.amazic.ads.util.Admob;
import com.amazic.ads.util.AppOpenManager;
import com.app.allfilerecovery.MainActivity;
import com.app.allfilerecovery.R;
import com.app.allfilerecovery.databinding.ActivityPermissionBinding;
import com.app.allfilerecovery.local.SystemUtil;
import com.app.allfilerecovery.utils.AdsUtils;
import com.app.allfilerecovery.utils.PermissionManager;
import com.app.allfilerecovery.view.home.HomeFragment;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;

public class PermissionActivity extends AppCompatActivity {

    private ActivityPermissionBinding mBinding;
    private boolean isActiveFile = false;


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            fullScreenImmersive(getWindow());
        }
    }

    private void fullScreenImmersive(Window windows) {
        if (windows != null) {
            fullScreenImmersive(windows.getDecorView());
        }
    }

    private void fullScreenImmersive(View view) {
        int uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN);
        view.setSystemUiVisibility(uiOptions);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemUtil.setLocale(this);
        mBinding = ActivityPermissionBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initView();
        showNativePermission();
    }

    private void initView() {
        mBinding.tvContinue.setOnClickListener(v -> start());

        mBinding.imgSwitchFiles.setImageResource(isActiveFile ? R.drawable.switch_on : R.drawable.switch_off);

        mBinding.imgSwitchFiles.setOnClickListener(v -> {
            actionSwitchFiles();
        });
    }

    private void actionSwitchFiles() {
        isActiveFile = !isActiveFile;

        if(isActiveFile){

            mBinding.imgSwitchFiles.setImageResource(isActiveFile ? R.drawable.switch_on : R.drawable.switch_off);

            requestPermissionFiles();
        }
    }

    private void setEnableContinue() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            if(Environment.isExternalStorageManager()) {
                isActiveFile = true;
                mBinding.imgSwitchFiles.setImageResource(isActiveFile ? R.drawable.switch_on : R.drawable.switch_off);
                mBinding.imgSwitchFiles.setEnabled(false);
            } else {
                isActiveFile = false;
                mBinding.imgSwitchFiles.setImageResource(isActiveFile ? R.drawable.switch_on : R.drawable.switch_off);
                mBinding.imgSwitchFiles.setEnabled(true);
            }
        }
    }

    private void requestPermissionFiles(){
        new Handler().postDelayed(() -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    //todo when permission is granted
                } else {
                    //request for the permission
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                        isActiveFile = false;
                    }
                }
            } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED
                ) {
                    //todo when permission is granted
                } else {
                    isActiveFile = false;
                    ActivityCompat.requestPermissions(PermissionActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1111);
                }
            } else {
                isActiveFile = false;
                ActivityCompat.requestPermissions(PermissionActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1111);
            }
        }, 1000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1111:
                if(grantResults.length > 0){
                    if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                        mBinding.imgSwitchFiles.setImageResource(R.drawable.switch_on);
                        mBinding.imgSwitchFiles.setEnabled(true);
                    } else {
                        boolean showRationable = shouldShowRequestPermissionRationale(permissions[0]);
                        if(!showRationable){

                        }
                        mBinding.imgSwitchFiles.setImageResource(R.drawable.switch_off);
                        mBinding.imgSwitchFiles.setEnabled(false);
                    }
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    private void showNativePermission() {
        Admob.getInstance().loadNativeAdFloor(this, AdsUtils.listNativePerId, new NativeCallback() {
            @Override
            public void onNativeAdLoaded(NativeAd nativeAd) {
                super.onNativeAdLoaded(nativeAd);
                NativeAdView adView = (NativeAdView) LayoutInflater.from(PermissionActivity.this).inflate(R.layout.custom_native_admod_medium, null);
                mBinding.nativeAds.addView(adView);
                Admob.getInstance().pushAdsToViewCustom(nativeAd, adView);
            }

            @Override
            public void onAdFailedToLoad() {
                super.onAdFailedToLoad();
                mBinding.nativeAds.removeAllViews();
            }
        });
    }

    private void start() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setEnableContinue();
        AppOpenManager.getInstance().enableAppResumeWithActivity(PermissionActivity.class);
        mBinding.nativeAds.setVisibility(View.VISIBLE);
    }
}
