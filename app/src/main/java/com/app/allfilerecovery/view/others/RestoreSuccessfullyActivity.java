package com.app.allfilerecovery.view.others;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.amazic.ads.callback.NativeCallback;
import com.amazic.ads.util.Admob;
import com.app.allfilerecovery.BuildConfig;
import com.app.allfilerecovery.MainActivity;
import com.app.allfilerecovery.R;
import com.app.allfilerecovery.base.BaseActivity;
import com.app.allfilerecovery.singleton.MyCountBackHomeSingleton;
import com.app.allfilerecovery.view.home.HomeFragment;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;

public class RestoreSuccessfullyActivity extends BaseActivity {
    private String idNative = "";
    Context context;

    private FrameLayout native_ads;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore_successfully);
        // set background status bar
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));

        //define ads
        configMediationProvider();
        native_ads = findViewById(R.id.native_ads);

        Admob.getInstance().loadNativeAd(this, idNative, new NativeCallback() {
            @Override
            public void onNativeAdLoaded(NativeAd nativeAd) {
                NativeAdView adView = ( NativeAdView) LayoutInflater.from(RestoreSuccessfullyActivity.this).inflate(R.layout.custom_native_admod_medium, null);
                native_ads.addView(adView);
                Admob.getInstance().pushAdsToViewCustom(nativeAd, adView);
            }

            @Override
            public void onAdFailedToLoad() {
                native_ads.removeAllViews();
            }
        });

        ImageView imgBack = findViewById(R.id.btn_back);
        imgBack.setOnClickListener(view -> onBackPressed());
        ImageView imgHis = findViewById(R.id.btn_history);
        imgHis.setOnClickListener(view -> onBackHis());

        //count restore file
        int count = MyCountBackHomeSingleton.getInstance().getMyInt();
        MyCountBackHomeSingleton.getInstance().setMyInt(count + 1);

        if (MyCountBackHomeSingleton.getInstance().getMyInt() == 2 ||
                MyCountBackHomeSingleton.getInstance().getMyInt() == 5 ||
                MyCountBackHomeSingleton.getInstance().getMyInt() == 7) {
            MyCountBackHomeSingleton.getInstance().setShowRate(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
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
            native_ads.setVisibility(View.GONE);
        } else {
            native_ads.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void onBackHis() {
        Intent intent = new Intent(context, MainActivity.class);
        finishAffinity();
        startActivity(intent);
    }

    private void configMediationProvider() {
        idNative = BuildConfig.native_success;
    }
}