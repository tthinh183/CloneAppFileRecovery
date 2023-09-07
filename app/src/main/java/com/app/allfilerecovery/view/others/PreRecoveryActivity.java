package com.app.allfilerecovery.view.others;

import static com.app.allfilerecovery.config.Config.FILE_TYPE;
import static com.app.allfilerecovery.config.Config.FILE_TYPE_AUDIO;
import static com.app.allfilerecovery.config.Config.FILE_TYPE_FILE;
import static com.app.allfilerecovery.config.Config.FILE_TYPE_IMAGE;
import static com.app.allfilerecovery.config.Config.FILE_TYPE_VIDEO;
import static com.app.allfilerecovery.config.Config.NAME_FOLDER;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
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
import com.app.allfilerecovery.utils.AdsUtils;
import com.app.allfilerecovery.view.others.adapter.AllPhotoFolderRecoveryAdapter;
import com.app.allfilerecovery.view.others.adapter.PhotoFolderRecoveryAdapter;
import com.app.allfilerecovery.view.others.model.PhotoFolderRecoveryModel;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PreRecoveryActivity extends BaseActivity {

    private static final String TAG = "PreRecoveryActivity_";
    public static ArrayList<ImageDataModel> scannedImages = new ArrayList<>();

    public ArrayList<PhotoFolderRecoveryModel> listPhotoFolders = new ArrayList<>();

    Context context;
    RecyclerView rvAllPhotos;
    AllPhotoFolderRecoveryAdapter allPhotoFolderAdapter;
    ArrayList<String> selectedImages = new ArrayList<>();
    String fileType = FILE_TYPE_IMAGE;
    TextView tvCountPhotoVideo;
    TextView tvCountFolder;
    TextView tvAllPhotos;
    View viewGoDetailAllPhotos;
    ImageView imgBack;
    TextView tvHeaderTitle;

    private String idNative = "";
    private FrameLayout native_ads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemUtil.setLocale(this);
        setContentView(R.layout.activity_pre_recovery);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        getListFolder(scannedImages);
        initView();
        context = this;
        initAllFolder();

        getIntentData();

        Admob.getInstance().loadCollapsibleBannerFloor(this, AdsUtils.listCollapseBannerId, "bottom");
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
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
        }*/
    }

    private void initView() {
        tvCountPhotoVideo = findViewById(R.id.tv_count_photo_video);
        tvCountFolder = findViewById(R.id.tv_count_folder);
        tvAllPhotos = findViewById(R.id.tv_all_photo);
        tvHeaderTitle = findViewById(R.id.tv_header_title);
        viewGoDetailAllPhotos = findViewById(R.id.layout_all_folder);
        imgBack = findViewById(R.id.btn_back);

        tvCountFolder.setText("" + listPhotoFolders.size());
        tvCountPhotoVideo.setText(String.valueOf(scannedImages.size()));
        tvAllPhotos.setText(" (" + scannedImages.size() + ")");
        viewGoDetailAllPhotos.setOnClickListener(view -> {
            Intent intent = new Intent(this, RecoveryActivity.class);
            intent.putExtra(FILE_TYPE, fileType);
            startActivity(intent);
        });

        //define ads
        configMediationProvider();
        /*native_ads = findViewById(R.id.native_ads);

        Admob.getInstance().loadNativeAd(this, idNative, new NativeCallback() {
            @Override
            public void onNativeAdLoaded(NativeAd nativeAd) {
                NativeAdView adView = ( NativeAdView) LayoutInflater.from(PreRecoveryActivity.this).inflate(R.layout.custom_native_admod_medium3, null);
                native_ads.addView(adView);
                Admob.getInstance().pushAdsToViewCustom(nativeAd, adView);
            }

            @Override
            public void onAdFailedToLoad() {
                native_ads.removeAllViews();
            }
        });*/

        imgBack.setOnClickListener(view -> onBackPressed());
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
                return getString(R.string.audios_button);
            case FILE_TYPE_VIDEO:
                return getString(R.string.videos_button);
            case FILE_TYPE_FILE:
                return getString(R.string.files_button);
        }
        return getString(R.string.photo_recovery);
    }

    private void getListFolder(List<ImageDataModel> listScannedImages) {
        ArrayList<PhotoFolderRecoveryModel> listFolderPhoto = new ArrayList<>();
        ArrayList<String> listPathFolder = new ArrayList<>();
        String nameFolderScanned;
        String nameFolder;
        int countImage;
        PhotoFolderRecoveryModel photoFolder;
        ArrayList<ImageDataModel> listImages;

        // filter list name folder -> begin
        for (int i = 0; i < listScannedImages.size(); i++) {
            listPathFolder.add(listScannedImages.get(i).getFolder());
        }

        ArrayList<String> listNameFolderFinal = new ArrayList<>();
        for (String path : listPathFolder) {
            // get folder name
            int lastIndex = path.lastIndexOf("/") + 1;
            String name = path.substring(lastIndex);

            if (!listNameFolderFinal.contains(path)) {
                boolean checkPathFolder = true;
                //filter list path folder has the same name folder
                for (String nameF : listNameFolderFinal) {
                    //get name folder in listNameFolderFinal
                    int lastIndexFolder = nameF.lastIndexOf("/") + 1;
                    String nameFolderCompare = nameF.substring(lastIndexFolder);
                    if (nameFolderCompare.equals(name)) {
                        checkPathFolder = false;
                        break;
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
            photoFolder = new PhotoFolderRecoveryModel();
            listImages = new ArrayList<>();
            photoFolder.setNameFolder(listNameFolderFinal.get(i));
            countImage = 0;
            for (int j = 0; j < listScannedImages.size(); j++) {
                //get name folder of file in list scan
                int lastIndexFolderScanned = listScannedImages.get(j).getFolder().lastIndexOf("/") + 1;
                nameFolderScanned = listScannedImages.get(j).getFolder().substring(lastIndexFolderScanned);

                // get name folder in list name folder
                int lastIndex = listNameFolderFinal.get(i).lastIndexOf("/") + 1;
                nameFolder = listNameFolderFinal.get(i).substring(lastIndex);
                if (nameFolderScanned.equals(nameFolder)) {
                    listImages.add(listScannedImages.get(j));
                    countImage++;
                }
            }
            photoFolder.setAmountPhotos(countImage);
            photoFolder.setListImages(listImages);
            listFolderPhoto.add(photoFolder);
        }
        // filter list folder -> finish

        listPhotoFolders = listFolderPhoto;
        Log.d("PreRecovery===", "list photo folder: " + listFolderPhoto);
    }

//    private void initListFolder() {
//        PhotoFolderRecoveryAdapter photoFolderAdapter = new PhotoFolderRecoveryAdapter(context, listPhotoFolders, nameFolder -> {
//            Intent intent = new Intent(this, RecoveryActivity.class);
//            intent.putExtra(NAME_FOLDER, nameFolder);
//            intent.putExtra(FILE_TYPE, fileType);
//            startActivity(intent);
//        });
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
//        rvPhotoFolders.setLayoutManager(linearLayoutManager);
//        rvPhotoFolders.setAdapter(photoFolderAdapter);
//    }

    private void initAllFolder() {
        sortByLastModified();

        rvAllPhotos = findViewById(R.id.rv_all_photos);
        GridLayoutManager layoutManager = new GridLayoutManager(context, 3);
        rvAllPhotos.setLayoutManager(layoutManager);
        allPhotoFolderAdapter = new AllPhotoFolderRecoveryAdapter(scannedImages, context);
        rvAllPhotos.setAdapter(allPhotoFolderAdapter);
    }

    public void setSelectedImages(String path, boolean isSelected) {

        if (isSelected) {
            selectedImages.add(path);
        } else {
            selectedImages.remove(path);
        }

    }

    public void sortByLastModified() {

        Collections.sort(scannedImages, (Comparator) (o1, o2) -> {
            ImageDataModel i1 = (ImageDataModel) o1;
            ImageDataModel i2 = (ImageDataModel) o2;
            return Long.compare(i2.getLastModified(), i1.getLastModified());
        });

    }
    private void configMediationProvider() {
        idNative = BuildConfig.native_detail;
    }
    @Override
    public void onBackPressed() {
        finish();
    }
}