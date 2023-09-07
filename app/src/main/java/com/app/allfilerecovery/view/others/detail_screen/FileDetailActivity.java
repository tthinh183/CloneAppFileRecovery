package com.app.allfilerecovery.view.others.detail_screen;

import static com.app.allfilerecovery.config.Config.FILE_TYPE_FILE;

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
import com.app.allfilerecovery.view.others.RestoreSuccessfullyActivity;
import com.app.allfilerecovery.view.recovery.OtherRecoveryActivity;
import com.google.android.gms.ads.interstitial.InterstitialAd;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FileDetailActivity extends BaseActivity {
    private static final String TAG = "VideoDetailActivity";
    private TextView tvNameFile, tvPathFile, tvSize, tvCreated;
    private TextView tvRestore;

    String path;
    String size;
    String name;
    Long date;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    private ArrayList<ImageDataModel> recoveredFiles = new ArrayList<>();
    private MyDataHandler myDataHandler;
    private String idBanner = "";
    private View adsView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SystemUtil.setLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_detail);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        path = getIntent().getExtras().getString(OtherRecoveryActivity.KEY_PATH);
        size = getIntent().getExtras().getString(OtherRecoveryActivity.KEY_SIZE);
        date = getIntent().getExtras().getLong(OtherRecoveryActivity.KEY_DATE);

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

        ImageView btnBack = findViewById(R.id.btn_back);
        tvNameFile = findViewById(R.id.tv_name_file);
        tvPathFile = findViewById(R.id.tv_path_file);
        tvSize = findViewById(R.id.tv_size_file);
        tvCreated = findViewById(R.id.tv_file_created);
        tvRestore = findViewById(R.id.tv_restore);
        adsView = findViewById(R.id.include);

        setDataAudioDetail();
        tvRestore.setOnClickListener(view -> {
            Admob.getInstance().showInterAds(FileDetailActivity.this, AdsUtils.interstitialAdAll, new InterCallback() {
                @Override
                public void onNextAction() {
                    initFileDetailAsync();
                }});
        });

        //load ads
        configMediationProvider();

        //load ads banner
        Admob.getInstance().loadBannerFloor(this, AdsUtils.listBannerAllId);
        btnBack.setOnClickListener(view -> onBackPressed());
    }

    private void setDataAudioDetail() {
        tvSize.setText(size);

        int startIndex = path.lastIndexOf("/") + 1;
        name = path.substring(startIndex);
        tvNameFile.setText(name);
        tvPathFile.setText(path.substring(0, startIndex));

        Date photoDate = new Date(date);
        String strDate = dateFormat.format(photoDate);
        tvCreated.setText(strDate);

        recoveredFiles.add(new ImageDataModel(path, "", false));
    }

    private void initFileDetailAsync() {
        myDataHandler = new MyDataHandler();
        new RestoreAsyncTask(this, this.myDataHandler, FILE_TYPE_FILE, recoveredFiles).execute("restored");
    }

    class MyDataHandler extends Handler {

        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == Config.DATA) {
                recoveredFiles.clear();
                recoveredFiles.addAll((ArrayList) message.obj);

            } else if (message.what == Config.REPAIR) {
                //onPostExecute
                finish();
                Intent intent = new Intent(FileDetailActivity.this, RestoreSuccessfullyActivity.class);
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