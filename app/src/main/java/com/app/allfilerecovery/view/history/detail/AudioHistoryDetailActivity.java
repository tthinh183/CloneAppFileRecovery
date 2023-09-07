package com.app.allfilerecovery.view.history.detail;

import android.content.Context;
import android.media.MediaPlayer;
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

import com.amazic.ads.util.Admob;
import com.app.allfilerecovery.BuildConfig;
import com.app.allfilerecovery.R;
import com.app.allfilerecovery.base.BaseActivity;
import com.app.allfilerecovery.utils.AdsUtils;
import com.app.allfilerecovery.utils.Const;

public class AudioHistoryDetailActivity extends BaseActivity {
    private static final String TAG = "AudioHistoryDetailActivity_";
    String path;
    String size;
    String name;
    String date;
    private MediaPlayer mp;
    private SeekBar sbAudio;
    private TextView tvTimePlay, tvTimeTotal;
    private TextView tvNameAudio, tvPathAudio, tvSizeAudio;
    private TextView tvRestoredAudio;
    private String idBanner = "";
    private View adsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_history_detail);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));

        name = getIntent().getExtras().getString(Const.NAME_HISTORY);
        path = getIntent().getExtras().getString(Const.PATH_HISTORY);
        size = getIntent().getExtras().getString(Const.SIZE_HISTORY);
        date = getIntent().getExtras().getString(Const.TIME_RESTORE_HISTORY);

        init();
        Uri uri = Uri.parse(path);
        controlSound(uri);
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
        tvTimePlay = findViewById(R.id.tv_time_play);
        tvTimeTotal = findViewById(R.id.tv_time_total);
        sbAudio = findViewById(R.id.sbAudio);
        tvNameAudio = findViewById(R.id.tv_name_audio);
        tvPathAudio = findViewById(R.id.tv_path_audio);
        tvSizeAudio = findViewById(R.id.tv_size_audio);
        tvRestoredAudio = findViewById(R.id.tv_audio_restored);
        adsView = findViewById(R.id.include);

        setDataAudioDetail();

        configMediationProvider();

        //load ads banner
        Admob.getInstance().loadBannerFloor(this, AdsUtils.listBannerAllId);

        ImageView btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(view -> onBackPressed());
    }

    private void setDataAudioDetail() {
        int startIndex = path.lastIndexOf("/") + 1;
        tvSizeAudio.setText(size);
        tvNameAudio.setText(name);
        tvPathAudio.setText(path.substring(0, startIndex));

        tvRestoredAudio.setText(date);

    }

    private void controlSound(Uri id) {
        mp = MediaPlayer.create(this, id);
        if (mp == null) {
            return;
        }
        initialiseSeekBar();
        mp.start();
        sbAudio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) mp.seekTo(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initialiseSeekBar() {
        sbAudio.setMax(mp.getDuration());
        tvTimeTotal.setText(convertTime(mp.getDuration()/1000));
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    sbAudio.setProgress(mp.getCurrentPosition());
                    tvTimePlay.setText(convertTime(mp.getCurrentPosition()/1000));
                    handler.postDelayed(this, 1000);
                } catch (Exception e) {
                    sbAudio.setProgress(0);
                }
            }
        }, 0);
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
    protected void onDestroy() {
        super.onDestroy();
        mp.stop();
        mp.reset();
        mp.release();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void configMediationProvider() {
        //idBanner = BuildConfig.banner_all;
    }
}