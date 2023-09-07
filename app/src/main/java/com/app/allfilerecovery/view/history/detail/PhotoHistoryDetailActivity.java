package com.app.allfilerecovery.view.history.detail;

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
import com.app.allfilerecovery.utils.Const;
import com.bumptech.glide.Glide;

public class PhotoHistoryDetailActivity extends BaseActivity {

    String path;
    String size;
    String name;
    String date;

    TextView tvName, tvPath, tvSize, tvRestored;
    ImageView imgPhotoDetail;
    private String idBanner = "";
    private View adsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemUtil.setLocale(this);
        setContentView(R.layout.activity_photo_history_detail);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));

        name = getIntent().getExtras().getString(Const.NAME_HISTORY);
        path = getIntent().getExtras().getString(Const.PATH_HISTORY);
        size = getIntent().getExtras().getString(Const.SIZE_HISTORY);
        date = getIntent().getExtras().getString(Const.TIME_RESTORE_HISTORY);

        init();
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

    private void init() {
        adsView = findViewById(R.id.include);
        tvName = findViewById(R.id.tv_name_photo);
        tvPath = findViewById(R.id.tv_path_photo);
        tvSize = findViewById(R.id.tv_size_photo);
        tvRestored = findViewById(R.id.tv_photo_restored);
        imgPhotoDetail = findViewById(R.id.img_photo_detail);
        ImageView imgBack = findViewById(R.id.btn_back);
        imgBack.setOnClickListener(view -> onBackPressed());

        setDataPhotoDetail();

        configMediationProvider();

        //load ads banner
        Admob.getInstance().loadBannerFloor(this, AdsUtils.listBannerAllId);
    }

    private void setDataPhotoDetail() {
        Glide.with(this).load(path).into(imgPhotoDetail);
        tvSize.setText(size);
        tvPath.setText(path);
        tvName.setText(name);
        tvRestored.setText(date);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void configMediationProvider() {
        //idBanner = BuildConfig.banner_all;
    }
}