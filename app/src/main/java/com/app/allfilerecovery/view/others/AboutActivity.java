package com.app.allfilerecovery.view.others;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazic.ads.util.Admob;
import com.app.allfilerecovery.BuildConfig;
import com.app.allfilerecovery.R;
import com.app.allfilerecovery.base.BaseActivity;
import com.app.allfilerecovery.local.SystemUtil;
import com.app.allfilerecovery.utils.AdsUtils;

public class AboutActivity extends BaseActivity {
    ImageView btn_back;
    TextView app_ver;
    private String idBanner = "";
    private View adsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemUtil.setLocale(this);
        setContentView(R.layout.activity_about);

        //set background status bar
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        btn_back = findViewById(R.id.btn_back);
        app_ver = findViewById(R.id.app_ver);
        adsView = findViewById(R.id.include);
        String appVersion = String.format(getString(com.files.commons.R.string.app_version), "1.2.5");
        app_ver.setText(appVersion);
        btn_back.setOnClickListener(v -> onBackPressed());
        configMediationProvider();

        Admob.getInstance().loadBannerFloor(this, AdsUtils.listBannerAllId);
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
            adsView.setVisibility(View.GONE);
        } else {
            adsView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
    private void configMediationProvider() {
        //idBanner = BuildConfig.banner_all;
    }
}
