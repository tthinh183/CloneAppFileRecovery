package com.app.allfilerecovery.view.others;

import static com.app.allfilerecovery.config.Config.DATA;
import static com.app.allfilerecovery.config.Config.FILE_TYPE;
import static com.app.allfilerecovery.config.Config.FILE_TYPE_AUDIO;
import static com.app.allfilerecovery.config.Config.FILE_TYPE_FILE;
import static com.app.allfilerecovery.config.Config.FILE_TYPE_IMAGE;
import static com.app.allfilerecovery.config.Config.FILE_TYPE_VIDEO;
import static com.app.allfilerecovery.config.Config.UPDATE;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.amazic.ads.callback.InterCallback;
import com.amazic.ads.callback.NativeCallback;
import com.amazic.ads.util.Admob;
import com.amazic.ads.util.AppOpenManager;
import com.app.allfilerecovery.BuildConfig;
import com.app.allfilerecovery.MainActivity;
import com.app.allfilerecovery.R;
import com.app.allfilerecovery.async.ScannerAsyncTask;
import com.app.allfilerecovery.base.BaseActivity;
import com.app.allfilerecovery.config.Config;
import com.app.allfilerecovery.local.SystemUtil;
import com.app.allfilerecovery.model.ImageDataModel;
import com.app.allfilerecovery.utils.AdsUtils;
import com.app.allfilerecovery.view.recovery.PreOtherRecoveryActivity;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Locale;

import pl.droidsonroids.gif.GifImageView;

public class MediaScannerActivity extends BaseActivity {

    private static final String TAG = "MediaScannerActivity_";

    private final ArrayList<ImageDataModel> alImageData = new ArrayList<>();
    Context context;
    TextView tvScan;
    TextView tvCountFile;
    TextView tvDone;
    TextView tvHeaderTitle;
    LinearLayout llScan;
    ImageView imgScan;
    ImageView imgBack;
    GifImageView gifScan;
    BottomSheetDialog bottomSheetDialog;
    String fileType = FILE_TYPE_IMAGE;
    private boolean firstScan;
    private boolean firstClick = true;
    private String idNative = "";
    private Boolean _isBack = false;
    String codeLang;

    private FrameLayout native_ads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemUtil.setLocale(this);
        setContentView(R.layout.activity_media_scanner);

        codeLang = Locale.getDefault().getLanguage();

        //define ads
        configMediationProvider();

        native_ads = findViewById(R.id.native_ads);
        Admob.getInstance().loadNativeAd(this, idNative, new NativeCallback() {
            @Override
            public void onNativeAdLoaded(NativeAd nativeAd) {
                NativeAdView adView = ( NativeAdView) LayoutInflater.from(MediaScannerActivity.this).inflate(R.layout.custom_native_admod_medium, null);
                native_ads.addView(adView);
                Admob.getInstance().pushAdsToViewCustom(nativeAd, adView);
            }

            @Override
            public void onAdFailedToLoad() {
                native_ads.removeAllViews();
            }
        });


        context = this;
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        firstScan = prefs.getBoolean("first_scan", true);
        initView();
        getIntentData();
        llScan.setOnClickListener(v -> {
            if (firstClick) {
                if(checkPermission()){
                    startScanning();
                    initAsync();
                }
            }
        });

        if (firstScan) {
            showInstruction1();
            prefs.edit().putBoolean("first_scan", false).apply();
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

        AppOpenManager.getInstance().enableAppResumeWithActivity(MediaScannerActivity.class);
        if (!networkConnected) {
            native_ads.setVisibility(View.GONE);
        } else {
            native_ads.setVisibility(View.VISIBLE);
        }
    }

    private void getIntentData() {
        if (getIntent().hasExtra(FILE_TYPE)) {
            fileType = getIntent().getStringExtra(FILE_TYPE);
        }
        tvHeaderTitle.setText(getTitleMediaScanner());
    }

    private void initView() {
        tvScan = findViewById(R.id.tv_scan);
        tvCountFile = findViewById(R.id.tv_count_file);
        imgScan = findViewById(R.id.img_scan);
        llScan = findViewById(R.id.ll_scan);
        gifScan = findViewById(R.id.gif_scan);
        imgBack = findViewById(R.id.btn_back);
        tvDone = findViewById(R.id.tv_done);
        tvHeaderTitle = findViewById(R.id.tv_header_title);
        tvScan.setText(getString(R.string.tap_to_start_scan));
        imgScan.setImageResource(R.drawable.ic_scan_1);
        imgScan.setVisibility(View.VISIBLE);
        tvDone.setVisibility(View.GONE);
        gifScan.setVisibility(View.GONE);

        imgBack.setOnClickListener(view -> onBackPressed());
    }

    private String getTitleMediaScanner() {
        switch (fileType) {
            case FILE_TYPE_IMAGE:
                return getString(R.string.photos_button);
            case FILE_TYPE_AUDIO:
                return getString(R.string.audios_button);
            case FILE_TYPE_VIDEO:
                return getString(R.string.videos_button);
            case FILE_TYPE_FILE:
                return getString(R.string.files_button);
        }
        return getString(R.string.photo_recovery);
    }

    private void startScanning() {
        tvScan.setText(getString(R.string.scanning));
        imgScan.setVisibility(View.GONE);
        gifScan.setVisibility(View.VISIBLE);
        firstClick = false;
    }

    private void initAsync() {
        MyDataHandler myDataHandler = new MyDataHandler();
        new ScannerAsyncTask(this, myDataHandler).execute(fileType);
    }

    private int getInstructImage(int indexInstruct) {
        int idLayout = 0;
        switch (codeLang) {
            case "en":
                idLayout = getLayoutByIndex(indexInstruct,
                        R.drawable.bg_instruct1,
                        R.drawable.bg_instruc2,
                        R.drawable.bg_instruc3);
                break;
            case "fr":
                idLayout = getLayoutByIndex(indexInstruct,
                        R.drawable.bg_fr_instruc1,
                        R.drawable.bg_fr_instruc2,
                        R.drawable.bg_fr_instruc3);
                break;
            case "pt":
                idLayout = getLayoutByIndex(indexInstruct,
                        R.drawable.bg_pt_instruc1,
                        R.drawable.bg_pt_instruc2,
                        R.drawable.bg_pt_instruc3);
                break;
            case "es":
                idLayout = getLayoutByIndex(indexInstruct,
                        R.drawable.bg_es_instruc1,
                        R.drawable.bg_es_instruc2,
                        R.drawable.bg_es_instruc3);
                break;
            case "hi":
                idLayout = getLayoutByIndex(indexInstruct,
                        R.drawable.bg_hi_instruc1,
                        R.drawable.bg_hi_instruc2,
                        R.drawable.bg_hi_instruc3);
                break;
        }
        return idLayout;
    }

    private int getLayoutByIndex(int indexInstruct, int idLayout1, int idLayout2, int idLayout3) {
        switch (indexInstruct) {
            case 0:
                return idLayout1;
            case 1:
                return idLayout2;
            case 2:
                return idLayout3;
            default:
                return 0;
        }
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                return true;
            } else {
                askPermission();
                return false;
            }
        } else {
            int permissionWrite = ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int permissionRead = ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE);

            if (permissionWrite == PackageManager.PERMISSION_GRANTED && permissionRead == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                askPermission();
                return false;
            }
        }
    }

    private void askPermission() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_permission_app);
        dialog.setCanceledOnTouchOutside(false);
        TextView cancel = dialog.findViewById(R.id.btnNotNow);
        TextView ok = dialog.findViewById(R.id.btnSubmit);
        cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
        ok.setOnClickListener(v -> {
            dialog.dismiss();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    //todo when permission is granted
                } else {
                    //request for the permission
                    AppOpenManager.getInstance().disableAppResumeWithActivity(MediaScannerActivity.class);
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);

                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
            } else {
                ActivityCompat.requestPermissions(MediaScannerActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        });
        dialog.show();
    }

    private void showInstruction1() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.setContentView(R.layout.dialog_instruct_photo_scan1);
        dialog.setCanceledOnTouchOutside(true);
        ImageView imgInstruct1 = dialog.findViewById(R.id.img_instruction1);
        imgInstruct1.setImageResource(getInstructImage(0));
        View masterView = dialog.findViewById(R.id.dialog_photo_instruction1);
        masterView.setOnClickListener(v -> {
            dialog.dismiss();
            showInstruction2();
        });
        dialog.show();
    }

    private void showInstruction2() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.setContentView(R.layout.dialog_instruct_photo_scan2);
        dialog.setCanceledOnTouchOutside(true);
        ImageView imgInstruct2 = dialog.findViewById(R.id.img_instruction2);
        imgInstruct2.setImageResource(getInstructImage(1));
        View masterView = dialog.findViewById(R.id.dialog_photo_instruction2);
        masterView.setOnClickListener(v -> {
            dialog.dismiss();
            showInstruction3();
        });
        dialog.show();
    }

    private void showInstruction3() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.WHITE));
        dialog.setContentView(R.layout.dialog_instruct_photo_scan3);
        dialog.setCanceledOnTouchOutside(true);
        ImageView imgInstruct3 = dialog.findViewById(R.id.img_instruction3);
        imgInstruct3.setImageResource(getInstructImage(2));
        View masterView = dialog.findViewById(R.id.dialog_photo_instruction3);
        masterView.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.show();
    }

    public void next() {
        if (alImageData.size() > 0) {
            if (bottomSheetDialog != null) {
                try {
                    bottomSheetDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            PreRecoveryActivity.scannedImages = alImageData;
            PreOtherRecoveryActivity.scannedFiles = alImageData;
            if (fileType.equals(FILE_TYPE_IMAGE)) {
                scanSuccess();
                tvDone.setOnClickListener(view -> {
                    Admob.getInstance().showInterAds(MediaScannerActivity.this, AdsUtils.interstitialAdAll, new InterCallback(){
                        @Override
                        public void onNextAction() {
                            super.onNextAction();
                            Intent intent;
                            intent = new Intent(context, PreRecoveryActivity.class);
                            intent.putExtra(FILE_TYPE, fileType);
                            startActivity(intent);
                            finish();
                            AdsUtils.interstitialAdAll = null;
                            MainActivity.loadInterAll(MediaScannerActivity.this);
                        }
                    });
                });
            } else {
                scanSuccess();
                tvDone.setOnClickListener(view -> {
                    Admob.getInstance().showInterAds(MediaScannerActivity.this, AdsUtils.interstitialAdAll, new InterCallback(){
                        @Override
                        public void onNextAction() {
                            super.onNextAction();
                            Intent intent;
                            intent = new Intent(context, PreOtherRecoveryActivity.class);
                            intent.putExtra(FILE_TYPE, fileType);
                            startActivity(intent);
                            finish();
                            AdsUtils.interstitialAdAll = null;
                            MainActivity.loadInterAll(MediaScannerActivity.this);
                        }
                    });
                });
            }
        } else {
            scanNotFound();
        }
    }

    public void scanSuccess(){
        imgScan.setVisibility(View.VISIBLE);
        tvDone.setVisibility(View.VISIBLE);
        gifScan.setVisibility(View.GONE);
        tvCountFile.setVisibility(View.GONE);
        tvScan.setVisibility(View.GONE);
    }

    void scanNotFound() {
        //Scan not found mode
        tvScan.setText(getString(R.string.file_not_found));
        imgScan.setVisibility(View.VISIBLE);
        imgScan.setImageResource(R.drawable.ic_scan_notfound);
        gifScan.setVisibility(View.GONE);
        tvDone.setVisibility(View.GONE);
        tvCountFile.setText("");
    }

    private class MyDataHandler extends Handler {

        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (_isBack) return;
            if (message.what == Config.DATA) {

                alImageData.clear();
                alImageData.addAll((ArrayList) message.obj);
                tvCountFile.setText(alImageData.size() + " " + getString(R.string.files_found));
                Log.d(TAG, "handleMessage: " + DATA);

                next();

            } else if (message.what == Config.REPAIR) {

                Log.d(TAG, "handleMessage: " + Config.REPAIR);

            } else if (message.what == Config.UPDATE) {
                Log.d(TAG, "handleMessage: " + UPDATE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        _isBack = true;
        finish();
    }

    private void configMediationProvider() {
        idNative = BuildConfig.native_photo_video_audio_file;
    }
}
