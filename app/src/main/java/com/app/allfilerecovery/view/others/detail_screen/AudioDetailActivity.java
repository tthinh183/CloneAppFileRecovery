package com.app.allfilerecovery.view.others.detail_screen;

import static com.app.allfilerecovery.config.Config.FILE_TYPE_AUDIO;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.amazic.ads.callback.InterCallback;
import com.amazic.ads.util.Admob;
import com.app.allfilerecovery.BuildConfig;
import com.app.allfilerecovery.R;
import com.app.allfilerecovery.async.RestoreAsyncTask;
import com.app.allfilerecovery.base.BaseActivity;
import com.app.allfilerecovery.config.Config;
import com.app.allfilerecovery.model.ImageDataModel;
import com.app.allfilerecovery.utils.AdsUtils;
import com.app.allfilerecovery.view.others.RestoreSuccessfullyActivity;
import com.app.allfilerecovery.view.recovery.OtherRecoveryActivity;
import com.google.android.gms.ads.interstitial.InterstitialAd;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AudioDetailActivity extends BaseActivity {
    private static final String TAG = "AudioDetailActivity";
    String path;
    String size;
    String name;
    Long date;
    private MediaPlayer mp;
    private SeekBar sbAudio;
    private TextView tvTimePlay, tvTimeTotal;
    private TextView tvNameAudio, tvPathAudio, tvSizeAudio, tvCreatedAudio;
    private TextView tvRestore;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    private ArrayList<ImageDataModel> recoveredFiles = new ArrayList<>();
    private MyDataHandler myDataHandler;
    private String idBanner = "";
    private View adsView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_detail);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        path = getIntent().getExtras().getString(OtherRecoveryActivity.KEY_PATH);
        size = getIntent().getExtras().getString(OtherRecoveryActivity.KEY_SIZE);
        date = getIntent().getExtras().getLong(OtherRecoveryActivity.KEY_DATE);

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
        // If true -> onNextAction() is called right after Ad Interstitial showed
        Admob.getInstance().setOpenActivityAfterShowInterAds(false);

        tvTimePlay = findViewById(R.id.tv_time_play);
        tvTimeTotal = findViewById(R.id.tv_time_total);
        sbAudio = findViewById(R.id.sbAudio);
        tvNameAudio = findViewById(R.id.tv_name_audio);
        tvPathAudio = findViewById(R.id.tv_path_audio);
        tvSizeAudio = findViewById(R.id.tv_size_audio);
        tvCreatedAudio = findViewById(R.id.tv_audio_created);
        tvRestore = findViewById(R.id.tv_restore);
        adsView = findViewById(R.id.include);

        setDataAudioDetail();

        configMediationProvider();
        //load banner ads
        Admob.getInstance().loadBannerFloor(this, AdsUtils.listBannerAllId);

        tvRestore.setOnClickListener(view -> {
            Admob.getInstance().showInterAds(AudioDetailActivity.this, AdsUtils.interstitialAdAll, new InterCallback() {
                @Override
                public void onNextAction() {
                    initFileDetailAsync();
                }});
        });

        ImageView btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(view -> onBackPressed());
    }

    private void setDataAudioDetail() {

        tvSizeAudio.setText(size);

        int startIndex = path.lastIndexOf("/") + 1;
        name = path.substring(startIndex);
        tvNameAudio.setText(name);
        tvPathAudio.setText(path.substring(0, startIndex));

        Date photoDate = new Date(date);
        String strDate = dateFormat.format(photoDate);
        tvCreatedAudio.setText(strDate);

        recoveredFiles.add(new ImageDataModel(path, "", false));
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

    private void initFileDetailAsync() {
        myDataHandler = new MyDataHandler();
        new RestoreAsyncTask(this, this.myDataHandler, FILE_TYPE_AUDIO, recoveredFiles).execute("restored");
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
                Intent intent = new Intent(AudioDetailActivity.this, RestoreSuccessfullyActivity.class);
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
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