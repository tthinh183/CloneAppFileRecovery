package com.app.allfilerecovery.view.others.detail_screen;

import static com.app.allfilerecovery.config.Config.FILE_TYPE_IMAGE;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amazic.ads.callback.InterCallback;
import com.amazic.ads.util.Admob;
import com.app.allfilerecovery.BuildConfig;
import com.app.allfilerecovery.R;
import com.app.allfilerecovery.async.RestoreAsyncTask;
import com.app.allfilerecovery.base.BaseActivity;
import com.app.allfilerecovery.config.Config;
import com.app.allfilerecovery.local.SystemUtil;
import com.app.allfilerecovery.model.ImageDataModel;
import com.app.allfilerecovery.utils.AdsUtils;
import com.app.allfilerecovery.view.others.RecoveryActivity;
import com.app.allfilerecovery.view.others.RestoreSuccessfullyActivity;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.interstitial.InterstitialAd;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PhotoDetailActivity extends BaseActivity {

    String path;
    String size;
    String name;
    Long date;

    TextView tvName, tvPath, tvSize, tvCreated;
    ImageView imgPhotoDetail;
    LinearLayout btnRestore;

    private MyDataHandler myDataHandler;
    private ArrayList<ImageDataModel> recoveredImages = new ArrayList<>();

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

    private String idBanner = "";

    private View adsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemUtil.setLocale(this);
        setContentView(R.layout.activity_photo_detail);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        path = getIntent().getExtras().getString(RecoveryActivity.KEY_PATH);
        size = getIntent().getExtras().getString(RecoveryActivity.KEY_SIZE);
        date = getIntent().getExtras().getLong(RecoveryActivity.KEY_DATE);

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
        // If true -> onNextAction() is called right after Ad Interstitial showed
        Admob.getInstance().setOpenActivityAfterShowInterAds(false);

        tvName = findViewById(R.id.tv_name_photo);
        tvPath = findViewById(R.id.tv_path_photo);
        tvSize = findViewById(R.id.tv_size_photo);
        tvCreated = findViewById(R.id.tv_photo_created);
        imgPhotoDetail = findViewById(R.id.img_photo_detail);
        btnRestore = findViewById(R.id.btn_restore_photo);
        ImageView imgBack = findViewById(R.id.btn_back);
        imgBack.setOnClickListener(view -> onBackPressed());

        adsView = findViewById(R.id.include);

        setDataPhotoDetail();

        btnRestore.setOnClickListener(view -> {
            Admob.getInstance().showInterAds(PhotoDetailActivity.this, AdsUtils.interstitialAdAll, new InterCallback() {
                @Override
                public void onNextAction() {
                    initPhotoDetailAsync();
                }});
        });

        //load ads
        configMediationProvider();

        //load ads banner
        Admob.getInstance().loadBannerFloor(this, AdsUtils.listBannerAllId);
    }

    private void setDataPhotoDetail() {
        Glide.with(this).load(path).into(imgPhotoDetail);
        tvSize.setText(size);

        int startIndex = path.lastIndexOf("/") + 1;
        name = path.substring(startIndex);
        tvPath.setText(path.substring(0, startIndex));
        tvName.setText(name);

        Date photoDate = new Date(date);
        String strDate = dateFormat.format(photoDate);
        tvCreated.setText(strDate);

        recoveredImages.add(new ImageDataModel(path, "", false));
    }

    private void initPhotoDetailAsync() {
        myDataHandler = new MyDataHandler();
        new RestoreAsyncTask(this, this.myDataHandler, FILE_TYPE_IMAGE, recoveredImages).execute("restored");
    }

    class MyDataHandler extends Handler {

        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == Config.DATA) {
                recoveredImages.clear();
                recoveredImages.addAll((ArrayList) message.obj);

            } else if (message.what == Config.REPAIR) {
                //onPostExecute
                finish();
                Intent intent = new Intent(PhotoDetailActivity.this, RestoreSuccessfullyActivity.class);
                startActivity(intent);
            }
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