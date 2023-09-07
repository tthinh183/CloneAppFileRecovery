package com.app.allfilerecovery.view.history.detail;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.amazic.ads.util.Admob;
import com.app.allfilerecovery.BuildConfig;
import com.app.allfilerecovery.R;
import com.app.allfilerecovery.base.BaseActivity;
import com.app.allfilerecovery.local.SystemUtil;
import com.app.allfilerecovery.utils.AdsUtils;
import com.app.allfilerecovery.utils.Const;

public class VideoHistoryDetailActivity extends BaseActivity {
    private static final String TAG = "VideoHistoryDetailActivity_";
    ImageView btn_back;
    private SeekBar sbVideo;
    private TextView tvTimePlay, tvTimeTotal;
    private VideoView videoPlay;

    String pathVideo;

    private String idBanner = "";
    private View adsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SystemUtil.setLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_history_detail);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));

        init();

        pathVideo = getIntent().getExtras().getString(Const.PATH_HISTORY);
        controlVideo(pathVideo);
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
        tvTimePlay = findViewById(R.id.tv_time_play);
        tvTimeTotal = findViewById(R.id.tv_time_total);
        sbVideo = findViewById(R.id.sbVideo);
        videoPlay = findViewById(R.id.video_detail);
        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(view -> onBackPressed());

        //load ads banner
        configMediationProvider();
        Admob.getInstance().loadBannerFloor(this, AdsUtils.listBannerAllId);
    }

    private void controlVideo(String path) {
        Uri uri = Uri.parse(path);
        videoPlay.setVideoURI(uri);
        initialiseSeekBar(uri);
        videoPlay.start();
        sbVideo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) videoPlay.seekTo(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initialiseSeekBar(Uri uri) {
        String time;
        long timeInSecond;
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(this, uri);
            time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        } catch (Exception e) {
            time = "0";
            e.printStackTrace();
        }

        try {
            timeInSecond = Long.parseLong(time);
        } catch (NumberFormatException e) {
            timeInSecond = 0;
        }

        int timeSecond = (int) (timeInSecond/1000);

        sbVideo.setMax(timeSecond);
        tvTimeTotal.setText(convertTime(timeSecond));
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    sbVideo.setProgress(videoPlay.getCurrentPosition()/1000);
                    tvTimePlay.setText(convertTime(videoPlay.getCurrentPosition()/1000));
                    handler.postDelayed(this, 1000);
                } catch (Exception e) {
                    sbVideo.setProgress(0);
                }
            }
        },0);
    }

    private String convertTime(int seconds) {
        int minuteCount = seconds / 60;
        int secondCount = seconds % 60;
        String minute;
        String second;
        if (minuteCount < 10) {
            minute = "0" + minuteCount;
        } else {
            minute = "" + minuteCount;
        }
        if (secondCount < 10) {
            second = "0" + secondCount;
        } else {
            second = "" + secondCount;
        }
        return minute + ":" + second;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void configMediationProvider() {
        //idBanner = BuildConfig.banner_all;
    }
}
