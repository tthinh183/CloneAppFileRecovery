package com.app.allfilerecovery.view.recycle_bin;

import static com.app.allfilerecovery.config.Config.DATA;
import static com.app.allfilerecovery.config.Config.FILE_TYPE_IMAGE;
import static com.app.allfilerecovery.config.Config.UPDATE;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.amazic.ads.callback.InterCallback;
import com.amazic.ads.util.Admob;
import com.app.allfilerecovery.BuildConfig;
import com.app.allfilerecovery.R;
import com.app.allfilerecovery.async.RestoreAsyncTask;
import com.app.allfilerecovery.async.ScannerRecycleBinAsyncTask;
import com.app.allfilerecovery.base.BaseFragment;
import com.app.allfilerecovery.config.Config;
import com.app.allfilerecovery.databinding.FragmentRecycleBinBinding;
import com.app.allfilerecovery.model.ImageDataModel;
import com.app.allfilerecovery.model.RecycleBinModel;
import com.app.allfilerecovery.utils.AdsUtils;
import com.app.allfilerecovery.utils.Const;
import com.app.allfilerecovery.view.others.RestoreSuccessfullyActivity;
import com.app.allfilerecovery.view.recycle_bin.adapter.RecycleBinAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class RecycleBinFragment extends BaseFragment<FragmentRecycleBinBinding> {
    private static final String TAG = "RecycleBinActivity_";

    Activity activity;
    public ArrayList<String> selectedFiles = new ArrayList<>();
    public ArrayList<ImageDataModel> recoverFiles = new ArrayList<>();
    public ArrayList<RecycleBinModel> scannedFilesToday = new ArrayList<>();
    public ArrayList<RecycleBinModel> scannedFilesYesterday = new ArrayList<>();
    public ArrayList<RecycleBinModel> scannedFilesOther = new ArrayList<>();
    public ArrayList<RecycleBinModel> fileRecycleBinList = new ArrayList<>();
    private RecycleBinAdapter fileRecycleBinAdapter;
    private boolean isSelectAll = false;

    String fileType = FILE_TYPE_IMAGE;
    String nameFolder;
    private String idNative = "";

    private ArrayList<RecycleBinModel> scannedFiles = new ArrayList<>();
    private final ArrayList<RecycleBinModel> alFileData = new ArrayList<>();
    private Boolean _isBack = false;
    private ProgressDialog progressRecycleBin;
    private ProgressDialog progressRestoring;

    @Override
    public void onResume() {
        super.onResume();
        ConnectivityManager connectivityManager = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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
            binding.nativeAds.setVisibility(View.GONE);
        } else {
            binding.nativeAds.setVisibility(View.GONE);
        }
    }

    @Override
    protected FragmentRecycleBinBinding setViewBinding(LayoutInflater inflater, @Nullable ViewGroup viewGroup) {
        return FragmentRecycleBinBinding.inflate(inflater, viewGroup, false);
    }
    protected void initView() {
        // If true -> onNextAction() is called right after Ad Interstitial showed
        Admob.getInstance().setOpenActivityAfterShowInterAds(false);

        updateResult();
        activity = requireActivity();

        initLoading();
//        requestPermission();
        checkPermission();

        binding.tvRestore.setOnClickListener(view -> {
            initRestoring();
            recoverFiles.clear();
            if (selectedFiles.size() <= 0) {
                if(progressRestoring.isShowing()){
                    progressRestoring.cancel();
                }
                Toast.makeText(activity, getString(R.string.please_select_at_least_one_item), Toast.LENGTH_SHORT).show();
                return;
            }
            for (String path : selectedFiles) {
                recoverFiles.add(new ImageDataModel(path, "", false));
            }
            initRestoreAsync();

        });

        //define ads
        configMediationProvider();

        /*Admob.getInstance().loadNativeAd(context, idNative, new NativeCallback() {
            @Override
            public void onNativeAdLoaded(NativeAd nativeAd) {
                NativeAdView adView = (NativeAdView) LayoutInflater.from(context).inflate(R.layout.custom_native_admod_medium3, null);
                binding.nativeAds.addView(adView);
                Admob.getInstance().pushAdsToViewCustom(nativeAd, adView);
            }

            @Override
            public void onAdFailedToLoad() {
                binding.nativeAds.removeAllViews();
            }
        });*/
    }

    @Override
    protected void viewListener() {

    }

    @Override
    protected void dataObservable() {

    }

    private void initLoading() {
        progressRecycleBin = new ProgressDialog(activity);
        progressRecycleBin.setMessage(getString(R.string.scanning));
        progressRecycleBin.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressRecycleBin.setCancelable(false);
        progressRecycleBin.show();
    }

    private void initRestoring() {
        progressRestoring = new ProgressDialog(activity);
        progressRestoring.setMessage(getString(R.string.restoring));
        progressRestoring.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressRestoring.setCancelable(false);
        progressRestoring.show();
    }

    void updateResult(){
        if(scannedFiles != null && scannedFiles.size() > 0){
            binding.layoutNotfound.setVisibility(View.GONE);
            binding.layoutResult.setVisibility(View.VISIBLE);
        } else {
            binding.layoutNotfound.setVisibility(View.GONE);
            binding.layoutResult.setVisibility(View.VISIBLE);
        }
    }

    private void selectedAllFile() {
        isSelectAll = !isSelectAll;
        if (isSelectAll) {
            binding.tvSelectAll.setText(R.string.cancel);
        } else {
            binding.tvSelectAll.setText(com.files.commons.R.string.select_all);
        }

        fileRecycleBinAdapter.selectAll(isSelectAll);
    }

    private void getScannedFilesByTime() {
        scannedFilesToday.clear();
        scannedFilesYesterday.clear();
        scannedFilesOther.clear();
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DATE);
        for (int i = 0; i < scannedFiles.size(); i++) {
            Calendar cal2 = Calendar.getInstance();
            cal2.setTimeInMillis(scannedFiles.get(i).getLastModified());
            if (cal2.get(Calendar.YEAR) == year
                    && cal2.get(Calendar.MONTH) == month
                    && cal2.get(Calendar.DATE) == day) {
                scannedFilesToday.add(scannedFiles.get(i));
            } else if (cal2.get(Calendar.YEAR) == year
                    && cal2.get(Calendar.MONTH) == month
                    && cal2.get(Calendar.DATE) == (day - 1)) {
                scannedFilesYesterday.add(scannedFiles.get(i));
            } else {
                scannedFilesOther.add(scannedFiles.get(i));
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
            binding.tvSelectAll.setText(R.string.cancel);
        } else{
            isSelectAll = false;
            binding.tvSelectAll.setText(R.string.select_all);
        }
        binding.tvRestore.setText(getString(R.string.restore) + " " + "("+ selectedFiles.size() + ")");
    }

    private void initCategoryFileRecoveryList(ArrayList<RecycleBinModel> listFile, int category) {
        for (int i = 0; i < listFile.size(); i++) {
            RecycleBinModel file = listFile.get(i);
            file.setCategoryFile(category);
            fileRecycleBinList.add(file);
        }
    }

    private void initListFile() {
        sortByLastModified();

        fileRecycleBinList.clear();

        if (!scannedFilesToday.isEmpty()) {
            //RecycleBinModel with empty data will be set for category item
            //category today
            RecycleBinModel categoryToday = new RecycleBinModel("", "", false);
            categoryToday.setCategoryFile(Const.CATEGORY_TODAY);
            fileRecycleBinList.add(categoryToday);
            initCategoryFileRecoveryList(scannedFilesToday, Const.CATEGORY_TODAY);
        }

        if (!scannedFilesYesterday.isEmpty()) {
            //RecycleBinModel with empty data will be set for category item
            //category yesterday
            RecycleBinModel categoryYesterday = new RecycleBinModel("", "", false);
            categoryYesterday.setCategoryFile(Const.CATEGORY_YESTERDAY);
            fileRecycleBinList.add(categoryYesterday);
            initCategoryFileRecoveryList(scannedFilesYesterday, Const.CATEGORY_YESTERDAY);
        }

        if (!scannedFilesOther.isEmpty()) {
            //RecycleBinModel with empty data will be set for category item
            //category other
            RecycleBinModel categoryOther = new RecycleBinModel("", "", false);
            categoryOther.setCategoryFile(Const.CATEGORY_OTHER);
            fileRecycleBinList.add(categoryOther);
            initCategoryFileRecoveryList(scannedFilesOther, Const.CATEGORY_OTHER);
        }

        if(fileRecycleBinList.size() == 0){
            RecycleBinModel category = new RecycleBinModel("empty", "", false);
            fileRecycleBinList.add(category);
            binding.rvFiles.setVisibility(View.VISIBLE);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        binding.rvFiles.setLayoutManager(layoutManager);
        fileRecycleBinAdapter = new RecycleBinAdapter(fileRecycleBinList, activity, this);
        binding.rvFiles.setAdapter(fileRecycleBinAdapter);
        updateResult();
    }

    private void sortByLastModified() {
        Collections.sort(scannedFiles, (Comparator) (o1, o2) -> {
            RecycleBinModel i1 = (RecycleBinModel) o1;
            RecycleBinModel i2 = (RecycleBinModel) o2;
            return Long.compare(i2.getLastModified(), i1.getLastModified());
        });

    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                initAsync();
            } else {
            }
        } else {
            int permissionWrite = ActivityCompat.checkSelfPermission(
                    activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int permissionRead = ActivityCompat.checkSelfPermission(
                    activity, Manifest.permission.READ_EXTERNAL_STORAGE);

            if (permissionWrite == PackageManager.PERMISSION_GRANTED && permissionRead == PackageManager.PERMISSION_GRANTED) {
                initAsync();
            } else {

            }
        }
    }

    private void initAsync() {
        MyDataHandler myDataHandler = new MyDataHandler();
        new ScannerRecycleBinAsyncTask(myDataHandler).execute("recycle bin");
    }

    private class MyDataHandler extends Handler {

        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (_isBack) return;
            if (message.what == Config.DATA) {

                alFileData.clear();
                alFileData.addAll((ArrayList) message.obj);
                Log.d(TAG, "handleMessage: " + DATA);

                next();

            } else if (message.what == Config.REPAIR) {

                Log.d(TAG, "handleMessage: " + Config.REPAIR);

            } else if (message.what == Config.UPDATE) {
                Log.d(TAG, "handleMessage: " + UPDATE);
            }
        }
    }

    public void next() {
        if (alFileData.size() > 0) {
            scannedFiles = alFileData;
            for (int i = 0; i < scannedFiles.size(); i++) {
                scannedFiles.get(i).setChecked(false);
            }
            getScannedFilesByTime();


            binding.tvSelectAll.setOnClickListener(view -> selectedAllFile());

            binding.rvFiles.setVisibility(View.VISIBLE);
            binding.tvTotal.setText(getString(R.string.total) + " (" + scannedFiles.size() + ")");
        } else {
            binding.tvTotal.setText(getString(R.string.total) + " (0)");
        }
        initListFile();
        updateResult();
        if (progressRecycleBin.isShowing()) {
            progressRecycleBin.dismiss();
        }
    }

    private void initRestoreAsync() {
        MyRestoreHandler restoreHandler = new MyRestoreHandler();
        new RestoreAsyncTask(activity, restoreHandler, fileType, recoverFiles).execute("restored");
    }

    public class MyRestoreHandler extends Handler {

        public void handleMessage(Message message) {

            super.handleMessage(message);

            if (message.what == Config.DATA) {


            } else if (message.what == Config.REPAIR) {
                if(progressRestoring.isShowing()){
                    progressRestoring.cancel();
                }

                //onPostExecute
                // reset data when back to screen
                if (!scannedFiles.isEmpty()) {
                    for (int i = 0; i < scannedFiles.size(); i++) {
                        scannedFiles.get(i).setChecked(false);
                    }

                    initListFile();
                }
                selectedFiles.clear();
                binding.tvRestore.setText(getString(R.string.restore) + " " + "("+ selectedFiles.size() + ")");
                Admob.getInstance().showInterAds(activity, AdsUtils.interstitialAdAll, new InterCallback() {
                    @Override
                    public void onNextAction() {
                        Intent intent = new Intent(activity, RestoreSuccessfullyActivity.class);
                        startActivity(intent);
                    }});

            } else if (message.what == Config.UPDATE) {


            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (progressRecycleBin.isShowing()) {
            progressRecycleBin.dismiss();
        }
    }

    public void onBackPressed() {
        _isBack = true;
        activity.finish();
    }

    private void configMediationProvider() {
        idNative = BuildConfig.native_detail;
    }
}