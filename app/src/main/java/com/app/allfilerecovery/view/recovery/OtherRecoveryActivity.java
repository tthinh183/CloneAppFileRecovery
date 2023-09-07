package com.app.allfilerecovery.view.recovery;

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
import com.app.allfilerecovery.model.FileDataModel;
import com.app.allfilerecovery.model.ImageDataModel;
import com.app.allfilerecovery.utils.AdsUtils;
import com.app.allfilerecovery.utils.Const;
import com.app.allfilerecovery.utils.FileUtils;
import com.app.allfilerecovery.view.others.PreRecoveryActivity;
import com.app.allfilerecovery.view.others.RestoreSuccessfullyActivity;
import com.app.allfilerecovery.view.others.adapter.FilterAdapter;
import com.app.allfilerecovery.view.others.detail_screen.AudioDetailActivity;
import com.app.allfilerecovery.view.others.detail_screen.FileDetailActivity;
import com.app.allfilerecovery.view.others.detail_screen.VideoDetailActivity;
import com.app.allfilerecovery.view.recovery.adapter.FileRecoveryAdapter;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class OtherRecoveryActivity extends BaseActivity {

    private static final String TAG = "OtherRecoveryActivity";
    public static final String KEY_PATH = "file_name";
    public static final String KEY_SIZE = "file_size";
    public static final String KEY_DATE = "file_date";
    Context context;
    RecyclerView rvFiles;
    LinearLayout layoutNotFound;
    private MySpinner spinCreated, spinFileSize, spinFileType;
    TextView tvSelectAll;
    private ProgressDialog progressRestoring;

    private ArrayList<String> fileDateSpinner = new ArrayList<>();
    private ArrayList<String> fileSizeSpinner = new ArrayList<>();
    private ArrayList<String> fileTypeSpinner = new ArrayList<>();
    public ArrayList<String> selectedFiles = new ArrayList<>();
    public ArrayList<ImageDataModel> recoveredFiles = new ArrayList<>();
    public ArrayList<ImageDataModel> scannedFilesToday = new ArrayList<>();
    public ArrayList<ImageDataModel> scannedFilesYesterday = new ArrayList<>();
    public ArrayList<ImageDataModel> scannedFilesOther = new ArrayList<>();
    public ArrayList<FileDataModel> fileRecoveryList = new ArrayList<>();
    private ArrayList<ImageDataModel> scannedFiles = PreOtherRecoveryActivity.scannedFiles;
    private ArrayList<ImageDataModel> listFileFilterByDate = new ArrayList<>();
    private ArrayList<ImageDataModel> listFileFilterBySize = new ArrayList<>();
    private ArrayList<ImageDataModel> listFileFilterByType = new ArrayList<>();
    private FileRecoveryAdapter fileAdapter;
    private boolean isSelectAll = false;

    String fileType = FILE_TYPE_IMAGE;
    String nameFolder;

    private String idNative = "";
    private FrameLayout native_ads;
    private boolean isFirstEntered = true;

    private int idDateSelected = 0;
    private int idSizeSelected = 0;
    private int idTypeSelected = 0;

    private boolean isDateSelectAgain = true;
    private boolean isSizeSelectAgain = true;
    private boolean isTypeSelectAgain = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemUtil.setLocale(this);
        setContentView(R.layout.activity_other_recovery);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        context = this;

        getIntentData();
        initView();
        getScannedFilesByTime(scannedFiles);
        setupSpinner();

        initListFile();

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

        TextView tvRecovery = findViewById(R.id.tv_recovery);
        tvSelectAll = findViewById(R.id.tv_select_all);
        TextView tvHeader = findViewById(R.id.tv_header_title);
        ImageView imgBack = findViewById(R.id.btn_back);
        rvFiles = findViewById(R.id.rv_files);

        for (int i = 0; i < scannedFiles.size(); i++) {
            scannedFiles.get(i).setChecked(false);
        }

        tvSelectAll.setOnClickListener(view -> selectedAllFile());

        //define ads
        configMediationProvider();
        native_ads = findViewById(R.id.native_ads);

        Admob.getInstance().loadNativeAd(this, idNative, new NativeCallback() {
            @Override
            public void onNativeAdLoaded(NativeAd nativeAd) {
                NativeAdView adView = (NativeAdView) LayoutInflater.from(OtherRecoveryActivity.this).inflate(R.layout.custom_native_admod_medium3, null);
                native_ads.addView(adView);
                Admob.getInstance().pushAdsToViewCustom(nativeAd, adView);
            }

            @Override
            public void onAdFailedToLoad() {
                native_ads.removeAllViews();
            }
        });

        tvRecovery.setOnClickListener(view -> {
            tvSelectAll.setText(getString(R.string.select_all));
            initRestoring();
            recoveredFiles.clear();
            for (String path : selectedFiles) {
                recoveredFiles.add(new ImageDataModel(path, "", false));
            }

            if (recoveredFiles.isEmpty()) {
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

    private void selectedAllFile() {
        isSelectAll = !isSelectAll;
        if (isSelectAll) {
            tvSelectAll.setText(R.string.cancel);
        } else {
            tvSelectAll.setText(com.files.commons.R.string.select_all);
        }

        fileAdapter.selectAll(isSelectAll);
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
                if (!isFirstEntered) {
                    filterFileByDate(position, scannedFiles);
                    // case just filter date
                    if (isSizeSelectAgain && isTypeSelectAgain) {
                        getScannedFilesByTime(listFileFilterByDate);
                    } else if (isSizeSelectAgain) { //case filter date and type
                        filterFileByType(fileTypeSpinner.get(idTypeSelected), listFileFilterByDate);
                        getScannedFilesByTime(listFileFilterByType);
                    } else if (isTypeSelectAgain) { //case filter date and size
                        filterFileBySize(idSizeSelected, listFileFilterByDate);
                        getScannedFilesByTime(listFileFilterBySize);
                    } else { // case filter date, size, and type
                        filterFileBySize(idSizeSelected, listFileFilterByDate);
                        filterFileByType(fileTypeSpinner.get(idTypeSelected), listFileFilterBySize);
                        getScannedFilesByTime(listFileFilterByType);
                    }
                    initListFile();

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
                    filterFileBySize(i, scannedFiles);
                    // case just filter size
                    if (isDateSelectAgain && isTypeSelectAgain) {
                        getScannedFilesByTime(listFileFilterBySize);
                    } else if (isDateSelectAgain) { //case filter size and type
                        filterFileByType(fileTypeSpinner.get(idTypeSelected), listFileFilterBySize);
                        getScannedFilesByTime(listFileFilterByType);
                    } else if (isTypeSelectAgain) { //case filter size and date
                        filterFileByDate(idDateSelected, listFileFilterBySize);
                        getScannedFilesByTime(listFileFilterByDate);
                    } else { // case filter date, size, and type
                        filterFileByDate(idDateSelected, listFileFilterBySize);
                        filterFileByType(fileTypeSpinner.get(idTypeSelected), listFileFilterByDate);
                        getScannedFilesByTime(listFileFilterByType);
                    }

                    initListFile();
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
                    filterFileByType(fileTypeSpinner.get(i), scannedFiles);
                    // case just filter type
                    if (isDateSelectAgain && isSizeSelectAgain) {
                        getScannedFilesByTime(listFileFilterByType);
                    } else if (isDateSelectAgain) { //case filter type and size
                        filterFileBySize(idSizeSelected, listFileFilterByType);
                        getScannedFilesByTime(listFileFilterBySize);
                    } else if (isSizeSelectAgain) { //case filter type and date
                        filterFileByDate(idDateSelected, listFileFilterByType);
                        getScannedFilesByTime(listFileFilterByDate);
                    } else { // case filter date, size, and type
                        filterFileByDate(idDateSelected, listFileFilterByType);
                        filterFileBySize(idSizeSelected, listFileFilterByDate);
                        getScannedFilesByTime(listFileFilterBySize);
                    }

                    initListFile();

                    if (isTypeSelectAgain) {
                        isTypeSelectAgain = false;
                        spinFileType.setSelection(i);
                    }

                    fileTypeAdapter.updateTitle(fileTypeSpinner.get(i));
                }

                idTypeSelected = i;
                // when isFirstEntered = false, can filter item
                isFirstEntered = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void filterFileByDate(int filter, ArrayList<ImageDataModel> listFileFilter) {
        listFileFilterByDate.clear();
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DATE);

        switch (filter) {
            case Const.FILTER_TODAY:
                for (int i = 0; i < listFileFilter.size(); i++) {
                    Calendar cal2 = Calendar.getInstance();
                    cal2.setTimeInMillis(listFileFilter.get(i).getLastModified());
                    if (cal2.get(Calendar.YEAR) == year
                            && cal2.get(Calendar.MONTH) == month
                            && cal2.get(Calendar.DATE) == day) {
                        listFileFilterByDate.add(listFileFilter.get(i));
                    }
                }
                break;

            case Const.FILTER_YESTERDAY:
                for (int i = 0; i < listFileFilter.size(); i++) {
                    Calendar cal2 = Calendar.getInstance();
                    cal2.setTimeInMillis(listFileFilter.get(i).getLastModified());
                    if (cal2.get(Calendar.YEAR) == year
                            && cal2.get(Calendar.MONTH) == month
                            && cal2.get(Calendar.DATE) == (day - 1)) {
                        listFileFilterByDate.add(listFileFilter.get(i));
                    }
                }
                break;

            case Const.FILTER_OTHER:
                for (int i = 0; i < listFileFilter.size(); i++) {
                    Calendar cal2 = Calendar.getInstance();
                    cal2.setTimeInMillis(listFileFilter.get(i).getLastModified());
                    if (!(cal2.get(Calendar.YEAR) == year
                            && cal2.get(Calendar.MONTH) == month
                            && cal2.get(Calendar.DATE) >= (day - 1))) {
                        listFileFilterByDate.add(listFileFilter.get(i));
                    }
                }
                break;
        }

    }

    private void filterFileBySize(int index, ArrayList<ImageDataModel> listFileFilter) {
        listFileFilterBySize.clear();

        switch (index) {
            case Const.FILTER_LESS_1MB:
                // get file has size less 1MB
                filterBySize(0, 1, listFileFilter);
                break;
            case Const.FILTER_1MB_10MB:
                // get file has size from 1MB to 10MB
                filterBySize(1, 10, listFileFilter);
                break;
            case Const.FILTER_10MB_100MB:
                // get file has size from 10MB to 100MB
                filterBySize(10, 100, listFileFilter);
                break;
            case Const.FILTER_100MB_500MB:
                // get file has size from 100MB to 500MB
                filterBySize(100, 500, listFileFilter);
                break;
            case Const.FILTER_500MB_THAN_1GB:
                // get file has size from 500MB to 1GB
                filterBySize(500, 1024, listFileFilter);
                break;
        }
    }

    private void filterBySize(int startSizeCompare, int endSizeCompare, ArrayList<ImageDataModel> listFileFilter) {
        for (int i = 0; i < listFileFilter.size(); i++) {
            File file = new File(listFileFilter.get(i).getPath());
            double size = FileUtils.getFileSizeMB(file);
            if (size > startSizeCompare && size <= endSizeCompare) {
                listFileFilterBySize.add(listFileFilter.get(i));
            }
        }

    }

    private void filterFileByType(String filter, ArrayList<ImageDataModel> listFileFilter) {
        listFileFilterByType.clear();
        String pathFile;

        for (int i = 0; i < listFileFilter.size(); i++) {
            // get path of file
            pathFile = listFileFilter.get(i).getPath();
            if (pathFile.contains(filter)) {
                listFileFilterByType.add(listFileFilter.get(i));
            }
        }

        getScannedFilesByTime(listFileFilterByType);
    }

    private void getScannedFilesByTime(ArrayList<ImageDataModel> listFiles) {
        scannedFilesToday.clear();
        scannedFilesYesterday.clear();
        scannedFilesOther.clear();
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
                scannedFilesToday.add(listFiles.get(i));
            } else if (cal2.get(Calendar.YEAR) == year
                    && cal2.get(Calendar.MONTH) == month
                    && cal2.get(Calendar.DATE) == (day - 1)) {
                scannedFilesYesterday.add(listFiles.get(i));
            } else {
                scannedFilesOther.add(listFiles.get(i));
            }
        }
    }

    public void setSelectedFiles(String path, boolean isSelected) {
        if (isSelected) {
            if (!selectedFiles.contains(path)) {
                selectedFiles.add(path);
            }
        } else {
            selectedFiles.remove(path);
        }

        int totalFile = scannedFilesToday.size() + scannedFilesYesterday.size() + scannedFilesOther.size();
        if (selectedFiles.size() == totalFile) {
            isSelectAll = true;
            tvSelectAll.setText(R.string.cancel);
        } else {
            isSelectAll = false;
            tvSelectAll.setText(R.string.select_all);
        }
    }

    public void goDetailFile(FileDataModel fileData) {
        Intent intent;
        switch (fileType) {
            case FILE_TYPE_VIDEO:
                intent = new Intent(context, VideoDetailActivity.class);
                intent.putExtra(KEY_PATH, fileData.getPath());
                startActivity(intent);
                break;
            case FILE_TYPE_AUDIO:
                intent = new Intent(context, AudioDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(KEY_SIZE, fileData.getSize());
                bundle.putString(KEY_PATH, fileData.getPath());
                bundle.putLong(KEY_DATE, fileData.getLastModified());
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case FILE_TYPE_FILE:
                intent = new Intent(context, FileDetailActivity.class);
                Bundle bundleFile = new Bundle();
                bundleFile.putString(KEY_SIZE, fileData.getSize());
                bundleFile.putString(KEY_PATH, fileData.getPath());
                bundleFile.putLong(KEY_DATE, fileData.getLastModified());
                intent.putExtras(bundleFile);
                startActivity(intent);
                break;
        }
    }

    private void getIntentData() {
        if (getIntent().hasExtra(FILE_TYPE)) {
            fileType = getIntent().getStringExtra(FILE_TYPE);
        }

        if (getIntent().hasExtra(NAME_FOLDER)) {
            nameFolder = getIntent().getStringExtra(NAME_FOLDER);
            Log.d("OtherRecoveryActivity====", nameFolder);
            if (!nameFolder.equals(getString(R.string.all))) {
                scannedFiles = getScannedFileByFolder();
            }
        }
    }

    private ArrayList<ImageDataModel> getScannedFileByFolder() {
        ArrayList<ImageDataModel> filesFilter = new ArrayList<>();
        for (int i = 0; i < scannedFiles.size(); i++) {
            // get name folder of file in scannedFiles
            int lastIdFolder = scannedFiles.get(i).getFolder().lastIndexOf("/") + 1;
            String nameFolderCompare = scannedFiles.get(i).getFolder().substring(lastIdFolder);
            if (nameFolderCompare.equals(nameFolder)) {
                Log.d("RecoveryActivity====", "remove item with :" + nameFolder);
                filesFilter.add(scannedFiles.get(i));
            }
        }
        return filesFilter;
    }

    private String getTitleMediaScanner() {
        switch (fileType) {
            case FILE_TYPE_IMAGE:
                return getString(R.string.photo_recovery);
            case FILE_TYPE_AUDIO:
                return getString(R.string.audio_recovery);
            case FILE_TYPE_VIDEO:
                return getString(R.string.video_recovery);
            case FILE_TYPE_FILE:
                return getString(R.string.file_recovery);
        }
        return getString(R.string.photo_recovery);
    }

    private void initAsync() {
        MyDataHandler myDataHandler = new MyDataHandler();
        new RestoreAsyncTask(this, myDataHandler, fileType, recoveredFiles).execute("restored");
    }


    void updateResult(){
        if(fileAdapter != null && fileAdapter.fileList.size() > 0){
            layoutNotFound.setVisibility(View.GONE);
            rvFiles.setVisibility(View.VISIBLE);
        } else {
            rvFiles.setVisibility(View.GONE);
            layoutNotFound.setVisibility(View.VISIBLE);
        }
    }

    private void initCategoryFileRecoveryList(ArrayList<ImageDataModel> listFile, int category) {
        for (int i = 0; i < listFile.size(); i++) {
            ImageDataModel file = listFile.get(i);
            fileRecoveryList.add(new FileDataModel(file.getPath(), file.getFolder(), false, category));
        }

    }

    private void initListFile() {
        sortByLastModified();
        fileRecoveryList.clear();

        if (!scannedFilesToday.isEmpty()) {
            //FileDataModel with empty data will be set for category item
            //category today
            FileDataModel categoryToday = new FileDataModel("", "", false, Const.CATEGORY_TODAY);
            fileRecoveryList.add(categoryToday);
            initCategoryFileRecoveryList(scannedFilesToday, Const.CATEGORY_TODAY);
        }

        if (!scannedFilesYesterday.isEmpty()) {
            //FileDataModel with empty data will be set for category item
            //category yesterday
            FileDataModel categoryYesterday = new FileDataModel("", "", false, Const.CATEGORY_YESTERDAY);
            fileRecoveryList.add(categoryYesterday);
            initCategoryFileRecoveryList(scannedFilesYesterday, Const.CATEGORY_YESTERDAY);
        }

        if (!scannedFilesOther.isEmpty()) {
            //FileDataModel with empty data will be set for category item
            //category other
            FileDataModel categoryOther = new FileDataModel("", "", false, Const.CATEGORY_OTHER);
            fileRecoveryList.add(categoryOther);
            initCategoryFileRecoveryList(scannedFilesOther, Const.CATEGORY_OTHER);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        rvFiles.setLayoutManager(layoutManager);
        fileAdapter = new FileRecoveryAdapter(fileRecoveryList, context, fileType);
        rvFiles.setAdapter(fileAdapter);
        updateResult();
    }

    public void sortByLastModified() {

        Collections.sort(PreRecoveryActivity.scannedImages, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                ImageDataModel i1 = (ImageDataModel) o1;
                ImageDataModel i2 = (ImageDataModel) o2;
                return Long.compare(i2.getLastModified(), i1.getLastModified());
            }
        });

    }

    public class MyDataHandler extends Handler {

        public void handleMessage(Message message) {

            super.handleMessage(message);

            if (message.what == Config.DATA) {

                recoveredFiles.clear();
                recoveredFiles.addAll((ArrayList) message.obj);
//                adapter.notifyDataSetChanged();

            } else if (message.what == Config.REPAIR) {
                if(progressRestoring.isShowing()){
                    progressRestoring.cancel();
                }
                //onPostExecute
                for (int i = 0; i < scannedFiles.size(); i++) {
                    scannedFiles.get(i).setChecked(false);
                }
                selectedFiles.clear();
                initListFile();
                Admob.getInstance().showInterAds(OtherRecoveryActivity.this, AdsUtils.interstitialAdAll, new InterCallback() {
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