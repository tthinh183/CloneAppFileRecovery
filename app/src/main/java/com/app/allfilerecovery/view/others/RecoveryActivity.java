package com.app.allfilerecovery.view.others;

import static com.app.allfilerecovery.config.Config.FILE_TYPE;
import static com.app.allfilerecovery.config.Config.FILE_TYPE_AUDIO;
import static com.app.allfilerecovery.config.Config.FILE_TYPE_FILE;
import static com.app.allfilerecovery.config.Config.FILE_TYPE_IMAGE;
import static com.app.allfilerecovery.config.Config.FILE_TYPE_VIDEO;
import static com.app.allfilerecovery.config.Config.NAME_FOLDER;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazic.ads.callback.InterCallback;
import com.amazic.ads.callback.NativeCallback;
import com.amazic.ads.util.Admob;
import com.app.allfilerecovery.BuildConfig;
import com.app.allfilerecovery.R;
import com.app.allfilerecovery.async.RestoreAsyncTask;
import com.app.allfilerecovery.base.BaseActivity;
import com.app.allfilerecovery.config.Config;
import com.app.allfilerecovery.helper.MySpinner;
import com.app.allfilerecovery.helper.SpinnerHelper;
import com.app.allfilerecovery.local.SystemUtil;
import com.app.allfilerecovery.model.ImageDataModel;
import com.app.allfilerecovery.model.PhotoRecoveryModel;
import com.app.allfilerecovery.utils.AdsUtils;
import com.app.allfilerecovery.utils.Const;
import com.app.allfilerecovery.utils.FileUtils;
import com.app.allfilerecovery.view.others.adapter.FilterAdapter;
import com.app.allfilerecovery.view.others.adapter.PhotoRecoveryAdapter;
import com.app.allfilerecovery.view.others.detail_screen.PhotoDetailActivity;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class RecoveryActivity extends BaseActivity {
    private static final String TAG = "RecoveryActivity_";
    public static final String KEY_PATH = "photo_name";
    public static final String KEY_SIZE = "photo_size";
    public static final String KEY_DATE = "photo_date";
    private ProgressDialog progressRestoring;
    Context context;
    RecyclerView rvPhotos;
    LinearLayout layoutNotFound;
    TextView tvSelectAll;
    private MySpinner spinCreated, spinFileSize, spinFileType;
    private ArrayList<String> fileDateSpinner = new ArrayList<>();
    private ArrayList<String> fileSizeSpinner = new ArrayList<>();
    private ArrayList<String> fileTypeSpinner = new ArrayList<>();
    public ArrayList<String> selectedImages = new ArrayList<>();
    public ArrayList<ImageDataModel> recoveredImages = new ArrayList<>();
    public ArrayList<ImageDataModel> scannedImagesToday = new ArrayList<>();
    public ArrayList<ImageDataModel> scannedImagesYesterday = new ArrayList<>();
    public ArrayList<ImageDataModel> scannedImagesOther = new ArrayList<>();
    public ArrayList<PhotoRecoveryModel> photoRecoveryList = new ArrayList<>();
    private ArrayList<ImageDataModel> scannedImages = PreRecoveryActivity.scannedImages;
    private ArrayList<ImageDataModel> listFileFilterByDate = new ArrayList<>();
    private ArrayList<ImageDataModel> listFileFilterBySize = new ArrayList<>();
    private ArrayList<ImageDataModel> listFileFilterByType = new ArrayList<>();
    private int idDateSelected = 0;
    private int idSizeSelected = 0;
    private int idTypeSelected = 0;
    private PhotoRecoveryAdapter photoRecoveryAdapter;
    private boolean isSelectAll = false;

    String fileType = FILE_TYPE_IMAGE;
    String nameFolder;
    private String idNative = "";

    private FrameLayout native_ads;
    private boolean isFirstEntered = true;
    private boolean isDateSelectAgain = true;
    private boolean isSizeSelectAgain = true;
    private boolean isTypeSelectAgain = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemUtil.setLocale(this);
        setContentView(R.layout.activity_recovery);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));

        context = this;

        getIntentData();
        initView();
        getScannedImagesByTime(scannedImages);
        setupSpinner();

        initListPhoto();
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

    private void initView() {
        // If true -> onNextAction() is called right after Ad Interstitial showed
        Admob.getInstance().setOpenActivityAfterShowInterAds(false);

        spinCreated = findViewById(R.id.spin_created);
        spinFileSize = findViewById(R.id.spin_file_size);
        spinFileType = findViewById(R.id.spin_file_type);
        layoutNotFound = findViewById(R.id.layout_notfound);
        View tvRecovery = findViewById(R.id.btn_recovery);
        //tvSelectAll = findViewById(R.id.tv_select_all);
        TextView tvHeader = findViewById(R.id.tv_header_title);
        ImageView imgBack = findViewById(R.id.btn_back);
        rvPhotos = findViewById(R.id.rv_photos);

        for (int i = 0; i < scannedImages.size(); i++) {
            scannedImages.get(i).setChecked(false);
        }

        //tvSelectAll.setOnClickListener(view -> selectedAllPhoto());

        //define ads
        configMediationProvider();

        native_ads = findViewById(R.id.native_ads);
        Admob.getInstance().loadNativeAd(this, idNative, new NativeCallback() {
            @Override
            public void onNativeAdLoaded(NativeAd nativeAd) {
                NativeAdView adView = ( NativeAdView) LayoutInflater.from(RecoveryActivity.this).inflate(R.layout.custom_native_admod_medium3, null);
                native_ads.addView(adView);
                Admob.getInstance().pushAdsToViewCustom(nativeAd, adView);
            }

            @Override
            public void onAdFailedToLoad() {
                native_ads.removeAllViews();
            }
        });

        tvRecovery.setOnClickListener(view -> {
            initRestoring();
            //tvSelectAll.setText(getString(R.string.select_all));
            recoveredImages.clear();
            for (String path : selectedImages) {
                recoveredImages.add(new ImageDataModel(path, "", false));
            }

            if (recoveredImages.isEmpty()) {
                Toast.makeText(context, getString(R.string.please_select_at_least_one_item), Toast.LENGTH_SHORT).show();
                if(progressRestoring.isShowing()){
                    progressRestoring.cancel();
                }
            } else {
                initAsync();
            }
        });

        tvHeader.setText(getTitleMediaScanner());
        imgBack.setOnClickListener(view -> onBackPressed());
    }
    private void initRestoring() {
        progressRestoring = new ProgressDialog(context);
        progressRestoring.setMessage(getString(R.string.restoring));
        progressRestoring.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressRestoring.setCancelable(false);
        progressRestoring.show();
    }

    private ArrayList<ImageDataModel> getScannedPhotoByFolder() {
        ArrayList<ImageDataModel> imagesFilter = new ArrayList<>();
        for (int i = 0; i < scannedImages.size(); i++) {
            //get name folder of file in scannedImages
            int lastIndexFolder = scannedImages.get(i).getFolder().lastIndexOf("/") + 1;
            String nameFolderCompare = scannedImages.get(i).getFolder().substring(lastIndexFolder);
            if (nameFolderCompare.equals(nameFolder)) {
                Log.d("RecoveryActivity====", "remove item with :" + nameFolder);
                imagesFilter.add(scannedImages.get(i));
            }
        }
        return  imagesFilter;
    }
    public void selectedAllPhoto(TextView tvSelectAll) {
        isSelectAll = !isSelectAll;
        if (isSelectAll) {
            tvSelectAll.setText(R.string.cancel);
        } else {
            tvSelectAll.setText(R.string.select_all);
            isSelectAll = false;
        }
        photoRecoveryAdapter.selectAll(isSelectAll);
    }

    private void setupSpinner() {
        SpinnerHelper spinnerHelper = new SpinnerHelper(context);

        switch (fileType) {
            case FILE_TYPE_IMAGE:
                fileTypeSpinner = spinnerHelper.listTypeImage;
                break;
            case FILE_TYPE_AUDIO:
                fileTypeSpinner = spinnerHelper.listTypeAudio;
                break;
            case FILE_TYPE_VIDEO:
                fileTypeSpinner = spinnerHelper.listTypeVideo;
                break;
            case FILE_TYPE_FILE:
                fileTypeSpinner = spinnerHelper.listTypeFile;
                break;
        }

        fileSizeSpinner = spinnerHelper.listSize;
        fileDateSpinner = spinnerHelper.listDate;

        FilterAdapter createdAdapter = new FilterAdapter(this, R.layout.item_spinner_filter_selected, fileDateSpinner, getString(R.string.created_label));
        FilterAdapter fileSizeAdapter = new FilterAdapter(this, R.layout.item_spinner_filter_selected, fileSizeSpinner, getString(R.string.size_label));
        FilterAdapter fileTypeAdapter = new FilterAdapter(this, R.layout.item_spinner_filter_selected, fileTypeSpinner, getString(R.string.type_label));

        spinCreated.setAdapter(createdAdapter);
        spinFileSize.setAdapter(fileSizeAdapter);
        spinFileType.setAdapter(fileTypeAdapter);

        spinCreated.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if(!isFirstEntered) {
                    filterImageByDate(position, scannedImages);
                    // case just filter date
                    if (isSizeSelectAgain && isTypeSelectAgain) {
                        getScannedImagesByTime(listFileFilterByDate);
                        initListPhoto();
                    } else if (isSizeSelectAgain){ //case filter date and type
                        filterImageByType(fileTypeSpinner.get(idTypeSelected), listFileFilterByDate);
                        getScannedImagesByTime(listFileFilterByType);
                        initListPhoto();
                    } else if (isTypeSelectAgain) { //case filter date and size
                        filterImageBySize(idSizeSelected, listFileFilterByDate);
                        getScannedImagesByTime(listFileFilterBySize);
                        initListPhoto();
                    } else { // case filter date, size, and type
                        filterImageBySize(idSizeSelected, listFileFilterByDate);
                        filterImageByType(fileTypeSpinner.get(idTypeSelected), listFileFilterBySize);
                        getScannedImagesByTime(listFileFilterByType);
                        initListPhoto();
                    }


                    if (isDateSelectAgain) {
                        isDateSelectAgain = false;
                        spinCreated.setSelection(position);
                    }
                    createdAdapter.updateTitle(fileDateSpinner.get(position));
                }

                idDateSelected = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinFileSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!isFirstEntered) {
                    filterImageBySize(i, scannedImages);
                    // case just filter size
                    if (isDateSelectAgain && isTypeSelectAgain) {
                        getScannedImagesByTime(listFileFilterBySize);
                        initListPhoto();
                    } else if (isDateSelectAgain){ //case filter size and type
                        filterImageByType(fileTypeSpinner.get(idTypeSelected), listFileFilterBySize);
                        getScannedImagesByTime(listFileFilterByType);
                        initListPhoto();
                    } else if (isTypeSelectAgain) { //case filter size and date
                        filterImageByDate(idDateSelected, listFileFilterBySize);
                        getScannedImagesByTime(listFileFilterByDate);
                        initListPhoto();
                    } else { // case filter date, size, and type
                        filterImageByDate(idDateSelected, listFileFilterBySize);
                        filterImageByType(fileTypeSpinner.get(idTypeSelected), listFileFilterByDate);
                        getScannedImagesByTime(listFileFilterByType);
                        initListPhoto();
                    }

                    if (isSizeSelectAgain) {
                        isSizeSelectAgain = false;
                        spinFileSize.setSelection(i);
                    }

                    fileSizeAdapter.updateTitle(fileSizeSpinner.get(i));
                }

                idSizeSelected = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinFileType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!isFirstEntered) {
                    filterImageByType(fileTypeSpinner.get(i), scannedImages);
                    // case just filter type
                    if (isDateSelectAgain && isSizeSelectAgain) {
                        getScannedImagesByTime(listFileFilterByType);
                        initListPhoto();
                    } else if (isDateSelectAgain){ //case filter type and size
                        filterImageBySize(idSizeSelected, listFileFilterByType);
                        getScannedImagesByTime(listFileFilterBySize);
                        initListPhoto();
                    } else if (isSizeSelectAgain) { //case filter type and date
                        filterImageByDate(idDateSelected, listFileFilterByType);
                        getScannedImagesByTime(listFileFilterByDate);
                        initListPhoto();
                    } else { // case filter date, size, and type
                        filterImageByDate(idDateSelected, listFileFilterByType);
                        filterImageBySize(idSizeSelected, listFileFilterByDate);
                        getScannedImagesByTime(listFileFilterBySize);
                        initListPhoto();
                    }

                    if (isTypeSelectAgain) {
                        isTypeSelectAgain = false;
                        spinFileType.setSelection(i);
                    }

                    fileTypeAdapter.updateTitle(fileTypeSpinner.get(i));
                }
                // when isFirstEntered = false, can filter item
                isFirstEntered = false;

                idTypeSelected = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void filterImageByDate(int filter, ArrayList<ImageDataModel> listFilter) {
        listFileFilterByDate.clear();
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DATE);

        switch (filter) {
            case Const.FILTER_TODAY:
                for (int i = 0; i < listFilter.size(); i++) {
                    Calendar cal2 = Calendar.getInstance();
                    cal2.setTimeInMillis(listFilter.get(i).getLastModified());
                    if (cal2.get(Calendar.YEAR) == year
                            && cal2.get(Calendar.MONTH) == month
                            && cal2.get(Calendar.DATE) == day) {
                        listFileFilterByDate.add(listFilter.get(i));
                    }
                }
                break;

            case Const.FILTER_YESTERDAY:
                for (int i = 0; i < listFilter.size(); i++) {
                    Calendar cal2 = Calendar.getInstance();
                    cal2.setTimeInMillis(listFilter.get(i).getLastModified());
                    if (cal2.get(Calendar.YEAR) == year
                            && cal2.get(Calendar.MONTH) == month
                            && cal2.get(Calendar.DATE) == (day - 1)) {
                        listFileFilterByDate.add(listFilter.get(i));
                    }
                }
                break;

            case Const.FILTER_OTHER:
                for (int i = 0; i < listFilter.size(); i++) {
                    Calendar cal2 = Calendar.getInstance();
                    cal2.setTimeInMillis(listFilter.get(i).getLastModified());
                    if (!(cal2.get(Calendar.YEAR) == year
                            && cal2.get(Calendar.MONTH) == month
                            && cal2.get(Calendar.DATE) >= (day - 1))) {
                        listFileFilterByDate.add(listFilter.get(i));
                    }
                }
                break;
        }
    }

    private void updateListFile() {
        //implement later
    }

    private void filterImageBySize(int index, ArrayList<ImageDataModel> listFilter) {
        listFileFilterBySize.clear();
        switch (index) {
            case Const.FILTER_LESS_1MB:
                // get file has size less 1MB
                filterBySize(0, 1, listFilter);
                break;
            case Const.FILTER_1MB_10MB:
                // get file has size from 1MB to 10MB
                filterBySize(1, 10, listFilter);
                break;
            case Const.FILTER_10MB_100MB:
                // get file has size from 10MB to 100MB
                filterBySize(10, 100, listFilter);
                break;
            case Const.FILTER_100MB_500MB:
                // get file has size from 100MB to 500MB
                filterBySize(100, 500, listFilter);
                break;
            case Const.FILTER_500MB_THAN_1GB:
                // get file has size from 500MB to 1GB
                filterBySize(500, 1024, listFilter);
                break;
        }

        updateListFile();
    }

    private void filterBySize(int startSizeCompare, int endSizeCompare, ArrayList<ImageDataModel> listFilter) {
        for (int i = 0; i < listFilter.size(); i++) {
            File file = new File(listFilter.get(i).getPath());
            double size = FileUtils.getFileSizeMB(file);
            if (size > startSizeCompare && size <= endSizeCompare) {
                listFileFilterBySize.add(listFilter.get(i));
            }
        }

    }

    private void filterImageByType(String filter, ArrayList<ImageDataModel> listFilter) {
        listFileFilterByType.clear();
        String pathFile;

        for (int i = 0; i < listFilter.size(); i++) {
            // get path of file
            pathFile = listFilter.get(i).getPath();
            if (pathFile.contains(filter)) {
                listFileFilterByType.add(listFilter.get(i));
            }
        }

        updateListFile();
    }

    private void getScannedImagesByTime(ArrayList<ImageDataModel> listFiles) {
        scannedImagesToday.clear();
        scannedImagesYesterday.clear();
        scannedImagesOther.clear();
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DATE);
        for (int i = 0; i < listFiles.size(); i++) {
            Calendar cal2 = Calendar.getInstance();
            cal2.setTimeInMillis(listFiles.get(i).getLastModified());
            if (cal2.get(Calendar.YEAR) == year
                    && cal2.get(Calendar.MONTH) == month
                    && cal2.get(Calendar.DATE) == day) {
                scannedImagesToday.add(listFiles.get(i));
            } else if (cal2.get(Calendar.YEAR) == year
                    && cal2.get(Calendar.MONTH) == month
                    && cal2.get(Calendar.DATE) == (day - 1)) {
                scannedImagesYesterday.add(listFiles.get(i));
            } else {
                scannedImagesOther.add(listFiles.get(i));
            }
        }
    }

    public void setSelectedImages(String path, boolean isSelected, TextView tvSelectAll) {
        if (isSelected) {
            if (!selectedImages.contains(path)) {
                selectedImages.add(path);
            }
        } else {
            selectedImages.remove(path);
        }

        int totalFile = scannedImagesToday.size() + scannedImagesYesterday.size() + scannedImagesOther.size();
        if (selectedImages.size() == totalFile) {
            isSelectAll = true;
            tvSelectAll.setText(R.string.cancel);

        } else{
            isSelectAll = false;
            tvSelectAll.setText(R.string.select_all);
        }
    }

    public void goDetailPhoto(ImageDataModel imgData) {
        Intent intent = new Intent(context, PhotoDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_SIZE, imgData.getSize());
        bundle.putString(KEY_PATH, imgData.getPath());
        bundle.putLong(KEY_DATE, imgData.getLastModified());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void getIntentData() {
        if (getIntent().hasExtra(FILE_TYPE)) {
            fileType = getIntent().getStringExtra(FILE_TYPE);
        }

        if (getIntent().hasExtra(NAME_FOLDER)) {
            nameFolder = getIntent().getStringExtra(NAME_FOLDER);
            Log.d("RecoveryActivity====", nameFolder);
            scannedImages = getScannedPhotoByFolder();
        }
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
        return getString(R.string.photos_button);
    }

    private void initAsync() {
        MyDataHandler myDataHandler = new MyDataHandler();
        new RestoreAsyncTask(this, myDataHandler, fileType, recoveredImages).execute("restored");
    }

    void updateResult(){
        if(photoRecoveryAdapter != null && photoRecoveryAdapter.imgPhotoList.size() > 0){
            layoutNotFound.setVisibility(View.GONE);
            rvPhotos.setVisibility(View.VISIBLE);
        } else {
            rvPhotos.setVisibility(View.GONE);
            layoutNotFound.setVisibility(View.VISIBLE);
        }
    }

    // get list image for each category
    private void initCategoryPhotoRecoveryList(ArrayList<ImageDataModel> listImage, int category) {
        int countItem = 0;
        ArrayList<ImageDataModel> imgDataList = new ArrayList<>();
        for (int i = 0; i < listImage.size(); i++) {
            countItem++;
            imgDataList.add(listImage.get(i));
            if (countItem == 3) {
                photoRecoveryList.add(new PhotoRecoveryModel(imgDataList, category));
                countItem = 0;
                imgDataList = new ArrayList<>();
            }
        }

        if (listImage.size() % 3 != 0) {
            photoRecoveryList.add(new PhotoRecoveryModel(imgDataList, category));
        }
    }

    private void initListPhoto() {
        sortByLastModified();
        photoRecoveryList.clear();
        if (!scannedImagesToday.isEmpty()) {
            //PhotoRecoveryModel with empty data will be set for category item
            //category today
            PhotoRecoveryModel categoryToday = new PhotoRecoveryModel(new ArrayList<>(), PhotoRecoveryModel.CATEGORY_TODAY);
            categoryToday.setAmountItemOfCategory(scannedImagesToday.size());
            photoRecoveryList.add(categoryToday);
            initCategoryPhotoRecoveryList(scannedImagesToday, PhotoRecoveryModel.CATEGORY_TODAY);
        }

        if (!scannedImagesYesterday.isEmpty()) {
            //category yesterday
            PhotoRecoveryModel categoryYesterday = new PhotoRecoveryModel(new ArrayList<>(), PhotoRecoveryModel.CATEGORY_YESTERDAY);
            categoryYesterday.setAmountItemOfCategory(scannedImagesYesterday.size());
            photoRecoveryList.add(categoryYesterday);
            initCategoryPhotoRecoveryList(scannedImagesYesterday, PhotoRecoveryModel.CATEGORY_YESTERDAY);
        }

        if (!scannedImagesOther.isEmpty()) {
            //category other
            PhotoRecoveryModel categoryOther = new PhotoRecoveryModel(new ArrayList<>(), PhotoRecoveryModel.CATEGORY_OTHER);
            categoryOther.setAmountItemOfCategory(scannedImagesOther.size());
            photoRecoveryList.add(categoryOther);
            initCategoryPhotoRecoveryList(scannedImagesOther, PhotoRecoveryModel.CATEGORY_OTHER);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        rvPhotos.setLayoutManager(layoutManager);
        photoRecoveryAdapter = new PhotoRecoveryAdapter(photoRecoveryList, context);
        rvPhotos.setAdapter(photoRecoveryAdapter);
        updateResult();
    }

    public void sortByLastModified() {

        Collections.sort(PreRecoveryActivity.scannedImages, (Comparator) (o1, o2) -> {
            ImageDataModel i1 = (ImageDataModel) o1;
            ImageDataModel i2 = (ImageDataModel) o2;
            return Long.compare(i2.getLastModified(), i1.getLastModified());
        });

    }

    public class MyDataHandler extends Handler {

        public void handleMessage(Message message) {

            super.handleMessage(message);

            if (message.what == Config.DATA) {

                recoveredImages.clear();
                recoveredImages.addAll((ArrayList) message.obj);
//                adapter.notifyDataSetChanged();

            } else if (message.what == Config.REPAIR) {

                //onPostExecute
                for (int i = 0; i < scannedImages.size(); i++) {
                    scannedImages.get(i).setChecked(false);
                }
                selectedImages.clear();
                initListPhoto();

                Admob.getInstance().showInterAds(RecoveryActivity.this, AdsUtils.interstitialAdAll, new InterCallback() {
                    @Override
                    public void onNextAction() {
                        Intent intent = new Intent(context, RestoreSuccessfullyActivity.class);
                        startActivity(intent);
                    }});

            } else if (message.what == Config.UPDATE) {

            }
        }
    }

    private void configMediationProvider() {
        idNative = BuildConfig.native_detail;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
