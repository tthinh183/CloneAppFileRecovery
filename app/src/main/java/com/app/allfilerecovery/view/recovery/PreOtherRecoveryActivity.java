package com.app.allfilerecovery.view.recovery;

import static com.app.allfilerecovery.config.Config.FILE_TYPE;
import static com.app.allfilerecovery.config.Config.FILE_TYPE_AUDIO;
import static com.app.allfilerecovery.config.Config.FILE_TYPE_FILE;
import static com.app.allfilerecovery.config.Config.FILE_TYPE_IMAGE;
import static com.app.allfilerecovery.config.Config.FILE_TYPE_VIDEO;
import static com.app.allfilerecovery.config.Config.NAME_FOLDER;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.TextView;

import com.amazic.ads.callback.NativeCallback;
import com.amazic.ads.util.Admob;
import com.app.allfilerecovery.BuildConfig;
import com.app.allfilerecovery.R;
import com.app.allfilerecovery.base.BaseActivity;
import com.app.allfilerecovery.local.SystemUtil;
import com.app.allfilerecovery.model.ImageDataModel;
import com.app.allfilerecovery.view.recovery.adapter.OtherFolderRecoveryAdapter;
import com.app.allfilerecovery.view.recovery.model.OtherFolderRecoveryModel;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;

import java.util.ArrayList;
import java.util.List;

public class PreOtherRecoveryActivity extends BaseActivity {
    ImageView imgBack;
    TextView tvHeaderTitle;
    String fileType = FILE_TYPE_IMAGE;
    ImageView imgFileType;
    TextView tvCountOtherFiles, tvCountOtherFolder;
    RecyclerView rvFolder;

    private String idNative = "";
    private FrameLayout native_ads;
    OtherFolderRecoveryAdapter otherFolderRecoveryAdapter;
    public static ArrayList<ImageDataModel> scannedFiles = new ArrayList<>();
    public ArrayList<OtherFolderRecoveryModel> listFilesFolders = new ArrayList<>();

    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemUtil.setLocale(this);
        setContentView(R.layout.activity_pre_other_recovery);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        context = this;
        getListOtherFolder(scannedFiles);
        init();
        getIntentData();
    }

    private void init() {
        imgBack = findViewById(R.id.btn_back);
        tvHeaderTitle = findViewById(R.id.tv_header_title);
        imgFileType = findViewById(R.id.img_file_type);
        tvCountOtherFiles = findViewById(R.id.tv_count_other_file);
        tvCountOtherFolder = findViewById(R.id.tv_count_folder);
        rvFolder = findViewById(R.id.rv_other_folder);

        //define ads
        configMediationProvider();

        native_ads = findViewById(R.id.native_ads);

        Admob.getInstance().loadNativeAd(this, idNative, new NativeCallback() {
            @Override
            public void onNativeAdLoaded(NativeAd nativeAd) {
                NativeAdView adView = (NativeAdView) LayoutInflater.from(PreOtherRecoveryActivity.this).inflate(R.layout.custom_native_admod_medium3, null);
                native_ads.addView(adView);
                Admob.getInstance().pushAdsToViewCustom(nativeAd, adView);
            }

            @Override
            public void onAdFailedToLoad() {
                native_ads.removeAllViews();
            }
        });

        tvCountOtherFolder.setText(String.valueOf(listFilesFolders.size()));
        tvCountOtherFiles.setText(String.valueOf(scannedFiles.size()));
        imgBack.setOnClickListener(view -> onBackPressed());
        initFolderRecycler();
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

    private void getIntentData() {
        if (getIntent().hasExtra(FILE_TYPE)) {
            fileType = getIntent().getStringExtra(FILE_TYPE);
        }
        tvHeaderTitle.setText(getTitleMediaScanner());
    }

    private String getTitleMediaScanner() {
        switch (fileType) {
            case FILE_TYPE_IMAGE:
                return getString(R.string.photos_button);
            case FILE_TYPE_AUDIO:
                imgFileType.setImageResource(R.drawable.ic_audio_1);
                return getString(R.string.audios_button);
            case FILE_TYPE_VIDEO:
                imgFileType.setImageResource(R.drawable.ic_video_1);
                return getString(R.string.videos_button);
            case FILE_TYPE_FILE:
                imgFileType.setImageResource(R.drawable.ic_file_1);
                return getString(R.string.files_button);
        }
        return getString(R.string.photos_button);
    }

    private void goToDetail(String pathFolder) {
        Intent intent = new Intent(context, OtherRecoveryActivity.class);
        intent.putExtra(NAME_FOLDER, pathFolder);
        intent.putExtra(FILE_TYPE, fileType);
        startActivity(intent);
    }

    private void initFolderRecycler() {
        listFilesFolders.add(0, new OtherFolderRecoveryModel(getString(R.string.all), scannedFiles.size()));
        otherFolderRecoveryAdapter = new OtherFolderRecoveryAdapter(listFilesFolders, this::goToDetail);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rvFolder.setLayoutManager(linearLayoutManager);
        rvFolder.setAdapter(otherFolderRecoveryAdapter);
    }

    private void getListOtherFolder(List<ImageDataModel> listScannedFiles) {
        ArrayList<OtherFolderRecoveryModel> listOtherFolder = new ArrayList<>();
        ArrayList<String> listPathFolder = new ArrayList<>();
        String nameFolderScanned;
        String nameFolder;
        int countFile;
        OtherFolderRecoveryModel otherFolder;

        // filter list name folder -> begin
        for (int i = 0; i < listScannedFiles.size(); i++) {
            listPathFolder.add(listScannedFiles.get(i).getFolder());
        }

        ArrayList<String> listNameFolderFinal = new ArrayList<>();
        for (String path : listPathFolder) {

            //get folder name
            int lastIndex = path.lastIndexOf("/") + 1;
            String name = path.substring(lastIndex);
            if (!listNameFolderFinal.contains(path)) {
                boolean checkPathFolder = true;
                //filter list path folder has the same name folder
                for (String nameF : listNameFolderFinal) {
                    //get name folder in listNameFolderFinal
                    int lastIndexFolder = nameF.lastIndexOf("/") + 1;
                    if (lastIndexFolder < nameF.length()) {
                        String nameFolderCompare = nameF.substring(lastIndexFolder);
                        if (nameFolderCompare.equals(name)) {
                            checkPathFolder = false;
                            break;
                        }
                    }
                }

                if (checkPathFolder) {
                    listNameFolderFinal.add(path);
                }
            }
        }
        // filter list name folder -> finish

        // filter list folder -> begin
        for (int i = 0; i < listNameFolderFinal.size(); i++) {
            otherFolder = new OtherFolderRecoveryModel();
            otherFolder.setNameFolder(listNameFolderFinal.get(i));
            countFile = 0;
            for (int j = 0; j < listScannedFiles.size(); j++) {
                //get name folder of file in list scan
                int lastIndexFolderScanned = listScannedFiles.get(j).getFolder().lastIndexOf("/") + 1;
                nameFolderScanned = listScannedFiles.get(j).getFolder().substring(lastIndexFolderScanned);

                // get name folder in list name folder
                int lastIndexFolder = listNameFolderFinal.get(i).lastIndexOf("/") + 1;
                nameFolder = listNameFolderFinal.get(i).substring(lastIndexFolder);
                if (nameFolderScanned.equals(nameFolder)) {
                    countFile++;
                }
            }
            otherFolder.setAmountFiles(countFile);
            listOtherFolder.add(otherFolder);
        }
        // filter list folder -> finish

        listFilesFolders = listOtherFolder;
    }
    private void configMediationProvider() {
        idNative = BuildConfig.native_detail;
    }
    @Override
    public void onBackPressed() {
        finish();
    }
}