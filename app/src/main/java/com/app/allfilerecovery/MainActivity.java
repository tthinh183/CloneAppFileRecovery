package com.app.allfilerecovery;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PathPermission;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.amazic.ads.callback.InterCallback;
import com.amazic.ads.util.Admob;
import com.app.allfilerecovery.adapter.PagerAdapter;
import com.app.allfilerecovery.base.BaseActivity;
import com.app.allfilerecovery.utils.AdsUtils;
import com.app.allfilerecovery.view.component.BottomMenu;

import com.google.android.gms.ads.interstitial.InterstitialAd;

public class MainActivity extends BaseActivity {
    private ViewPager2 vpMain;
    private BottomMenu bottomMenu;
    private View adsView;
    private boolean isPermissionInRecycle = false;
    private boolean isPermissionInHistory = false;
    private boolean isBack = true;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set background status bar
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));

        initView();

        Admob.getInstance().loadCollapsibleBannerFloor(this, AdsUtils.listCollapseBannerId, "bottom");

        if (AdsUtils.interstitialAdAll == null) {
            loadInterAll(this);
        }
    }

    public static void loadInterAll(Context context) {
        Admob.getInstance().loadInterAdsFloor(context, AdsUtils.listInterAllId, new InterCallback() {
            @Override
            public void onInterstitialLoad(InterstitialAd interstitialAd) {
                super.onInterstitialLoad(interstitialAd);
                AdsUtils.interstitialAdAll = interstitialAd;
            }
        });
    }

    private void initView() {
        vpMain = findViewById(R.id.vp_main);
        bottomMenu = findViewById(R.id.bottom_menu);
        adsView = findViewById(R.id.include);

        PagerAdapter adapter = new PagerAdapter(this);
        vpMain.setAdapter(adapter);
        vpMain.setUserInputEnabled(false);

        viewListener();
    }

    public void onResume() {
        super.onResume();
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Network nw = connectivityManager.getActiveNetwork();
        boolean networkConnected;

        if (nw == null) {
            networkConnected = false;
        } else {
            NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
            networkConnected = actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
        }

        if (!networkConnected) {
            adsView.setVisibility(View.GONE);
        } else {
            adsView.setVisibility(View.VISIBLE);
        }

        isBack = !isBack;
        if (isBack) {
            checkPermissionIn();
        }

    }

    private void checkPermissionIn() {
        isBack = !isBack;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                if (isPermissionInRecycle) {
                    vpMain.setCurrentItem(1);
                }
                if (isPermissionInHistory) {
                    vpMain.setCurrentItem(2);
                }
            } else {
                //request for the permission
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
            ) {
                if (isPermissionInRecycle) {
                    vpMain.setCurrentItem(1);
                }
                if (isPermissionInHistory) {
                    vpMain.setCurrentItem(2);
                }

            } else {
                //request for the permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    public void viewListener() {
        bottomMenu.addListener(this, action -> {
            switch (action) {
                case OPEN_HOME:
                    if (vpMain.getCurrentItem() != 0) {
                        vpMain.setCurrentItem(0);
                    }
                    break;
                case OPEN_RECYCLE_BIN:
                    if (vpMain.getCurrentItem() != 1) {
                        if (requestPermission()) {
                            vpMain.setCurrentItem(1);
                        }
                        isPermissionInRecycle = true;
                    }
                    break;
                case OPEN_HISTORY:
                    if (vpMain.getCurrentItem() != 2) {
                        if(requestPermission()){
                            vpMain.setCurrentItem(2);
                        }
                        isPermissionInHistory = true;
                    }
                    break;
            }
        });
    }

    private boolean requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                return true;
            } else {
                //request for the permission
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                    isBack = false;
                }
                return false;
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED
            ) {
                return true;
            } else {
                isBack = false;
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if(grantResults.length > 0){
                    if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                        //todo when permission granted
                    } else {
                        vpMain.setCurrentItem(0);
                        bottomMenu.selectScreen(BottomMenu.ScreenTag.HOME);
                    }
                }
        }
    }
}
