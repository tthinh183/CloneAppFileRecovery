package com.app.allfilerecovery.view.history;

import static com.app.allfilerecovery.config.Config.DATA;
import static com.app.allfilerecovery.config.Config.FILE_TYPE_AUDIO;
import static com.app.allfilerecovery.config.Config.FILE_TYPE_FILE;
import static com.app.allfilerecovery.config.Config.FILE_TYPE_IMAGE;
import static com.app.allfilerecovery.config.Config.FILE_TYPE_VIDEO;
import static com.app.allfilerecovery.config.Config.UPDATE;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
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
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amazic.ads.callback.NativeCallback;
import com.amazic.ads.util.Admob;
import com.app.allfilerecovery.BuildConfig;
import com.app.allfilerecovery.R;
import com.app.allfilerecovery.async.RestoreHistoryAsyncTask;
import com.app.allfilerecovery.base.BaseActivity;
import com.app.allfilerecovery.base.BaseFragment;
import com.app.allfilerecovery.callback.IClickItemHistory;
import com.app.allfilerecovery.config.Config;
import com.app.allfilerecovery.databinding.FragmentHistoryBinding;
import com.app.allfilerecovery.local.SystemUtil;
import com.app.allfilerecovery.model.HistoryFileModel;
import com.app.allfilerecovery.utils.Const;
import com.app.allfilerecovery.utils.PermissionManager;
import com.app.allfilerecovery.view.history.adapter.HistoryAdapter;
import com.app.allfilerecovery.view.history.detail.AudioHistoryDetailActivity;
import com.app.allfilerecovery.view.history.detail.FileHistoryDetailActivity;
import com.app.allfilerecovery.view.history.detail.PhotoHistoryDetailActivity;
import com.app.allfilerecovery.view.history.detail.VideoHistoryDetailActivity;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;

import java.util.ArrayList;

public class HistoryFragment extends BaseFragment<FragmentHistoryBinding> {
    private static final String TAG = "HistoryActivity_";
    private final ArrayList<HistoryFileModel> alFileData = new ArrayList();
    HistoryAdapter historyAdapter;
    private ArrayList<HistoryFileModel> listHistoryItem = new ArrayList<>();
    private String idNative = "";
    private MyDataHandler myDataHandler;
    private Boolean _isBack = false;
    Activity context;
    String fileType = FILE_TYPE_IMAGE;
    private ProgressDialog progressHistory;

    @Override
    public void onResume() {
        super.onResume();
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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

    void updateResult(){
        if(historyAdapter != null && historyAdapter.historyFileList.size() > 0){
            binding.layoutNotfound.setVisibility(View.GONE);
            binding.layoutResult.setVisibility(View.VISIBLE);
        } else {
            binding.layoutResult.setVisibility(View.GONE);
            binding.layoutNotfound.setVisibility(View.VISIBLE);
        }
    }

    private void initRecycleViewHistory() {
        historyAdapter = new HistoryAdapter(listHistoryItem, context, bundle -> {
            Intent intent;
            switch (bundle.getString(Const.FILE_TYPE_HISTORY)) {
                case FILE_TYPE_IMAGE:
                    intent = new Intent(context, PhotoHistoryDetailActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                case FILE_TYPE_VIDEO:
                    intent = new Intent(context, VideoHistoryDetailActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                case FILE_TYPE_AUDIO:
                    intent = new Intent(context, AudioHistoryDetailActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                case FILE_TYPE_FILE:
                    intent = new Intent(context, FileHistoryDetailActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        binding.rvHistory.setLayoutManager(layoutManager);
        binding.rvHistory.setAdapter(historyAdapter);
        updateResult();
    }

    private void initLoading() {
        progressHistory = new ProgressDialog(context);
        progressHistory.setMessage(getString(R.string.scanning));
        progressHistory.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressHistory.setCancelable(false);
        progressHistory.show();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                initAsync();
            } else {

            }
        } else {
            int permissionWrite = ActivityCompat.checkSelfPermission(
                    context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int permissionRead = ActivityCompat.checkSelfPermission(
                    context, Manifest.permission.READ_EXTERNAL_STORAGE);

            if (permissionWrite == PackageManager.PERMISSION_GRANTED && permissionRead == PackageManager.PERMISSION_GRANTED) {
                initAsync();
            } else {
            }
        }
    }

    private void configMediationProvider() {
        idNative = BuildConfig.native_detail;
    }

    private int filterItem(String fileType) {
        ArrayList<HistoryFileModel> newListHistoryItem = new ArrayList<>();
        for (HistoryFileModel item : listHistoryItem) {
            if (item.getTypeFile().equals(fileType)) {
                newListHistoryItem.add(item);
            }
        }
        if(historyAdapter != null){
            historyAdapter.updateListFile(newListHistoryItem);
            updateResult();
        }
        return newListHistoryItem.size();
    }

    private void initAsync() {
        myDataHandler = new MyDataHandler();
        new RestoreHistoryAsyncTask(context, this.myDataHandler).execute("history");
    }

    @Override
    protected FragmentHistoryBinding setViewBinding(LayoutInflater inflater, @Nullable ViewGroup viewGroup) {
        return FragmentHistoryBinding.inflate(inflater, viewGroup, false);
    }

    @Override
    protected void initView() {
        configMediationProvider();

        context = requireActivity();

        initLoading();
        setUpButtonFilter();
        checkPermission();

        /*Admob.getInstance().loadNativeAd(context, idNative, new NativeCallback() {
            @Override
            public void onNativeAdLoaded(NativeAd nativeAd) {
                NativeAdView adView = ( NativeAdView) LayoutInflater.from(context).inflate(R.layout.custom_native_admod_medium3, null);
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

    private void next() {
        if (alFileData.size() > 0) {

            listHistoryItem = alFileData;
            initRecycleViewHistory();
            binding.tvFilter.setText(getString(R.string.all) + " (" + listHistoryItem.size() + ")");
        } else {
            noFileRestore();
        }

        if (progressHistory.isShowing()) {
            progressHistory.dismiss();
        }
    }

    private void noFileRestore() {
        // display have not file recovery yet
    }

    private void setUpButtonFilter() {
        binding.btnAllFilter.setOnClickListener(view -> {
            binding.btnAllFilter.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_button_selected));
            binding.btnAllFilter.setTextColor(ContextCompat.getColor(context, R.color.white));
            binding.btnPhotoFilter.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_button_non_selected));
            binding.btnPhotoFilter.setTextColor(ContextCompat.getColor(context, R.color.black));
            binding.btnVideoFilter.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_button_non_selected));
            binding.btnVideoFilter.setTextColor(ContextCompat.getColor(context, R.color.black));
            binding.btnAudioFilter.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_button_non_selected));
            binding.btnAudioFilter.setTextColor(ContextCompat.getColor(context, R.color.black));
            binding.btnFileFilter.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_button_non_selected));
            binding.btnFileFilter.setTextColor(ContextCompat.getColor(context, R.color.black));
            binding.tvFilter.setText(getString(R.string.all) + " (" + listHistoryItem.size() + ")");
            if(historyAdapter != null) {
                historyAdapter.updateListFile(listHistoryItem);
                updateResult();
            }
        });

        binding.btnPhotoFilter.setOnClickListener(view -> {
            binding.btnAllFilter.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_button_non_selected));
            binding.btnAllFilter.setTextColor(ContextCompat.getColor(context, R.color.black));
            binding.btnPhotoFilter.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_button_selected));
            binding.btnPhotoFilter.setTextColor(ContextCompat.getColor(context, R.color.white));
            binding.btnVideoFilter.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_button_non_selected));
            binding.btnVideoFilter.setTextColor(ContextCompat.getColor(context, R.color.black));
            binding.btnAudioFilter.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_button_non_selected));
            binding.btnAudioFilter.setTextColor(ContextCompat.getColor(context, R.color.black));
            binding.btnFileFilter.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_button_non_selected));
            binding.btnFileFilter.setTextColor(ContextCompat.getColor(context, R.color.black));
            binding.tvFilter.setText(getString(R.string.photo_filter) + " (" + filterItem(FILE_TYPE_IMAGE) + ")");
        });

        binding.btnVideoFilter.setOnClickListener(view -> {
            binding.btnAllFilter.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_button_non_selected));
            binding.btnAllFilter.setTextColor(ContextCompat.getColor(context, R.color.black));
            binding.btnPhotoFilter.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_button_non_selected));
            binding.btnPhotoFilter.setTextColor(ContextCompat.getColor(context, R.color.black));
            binding.btnVideoFilter.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_button_selected));
            binding.btnVideoFilter.setTextColor(ContextCompat.getColor(context, R.color.white));
            binding.btnAudioFilter.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_button_non_selected));
            binding.btnAudioFilter.setTextColor(ContextCompat.getColor(context, R.color.black));
            binding.btnFileFilter.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_button_non_selected));
            binding.btnFileFilter.setTextColor(ContextCompat.getColor(context, R.color.black));
            binding.tvFilter.setText(getString(R.string.video_filter) + " (" + filterItem(FILE_TYPE_VIDEO) + ")");
        });

        binding.btnAudioFilter.setOnClickListener(view -> {
            binding.btnAllFilter.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_button_non_selected));
            binding.btnAllFilter.setTextColor(ContextCompat.getColor(context, R.color.black));
            binding.btnPhotoFilter.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_button_non_selected));
            binding.btnPhotoFilter.setTextColor(ContextCompat.getColor(context, R.color.black));
            binding.btnVideoFilter.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_button_non_selected));
            binding.btnVideoFilter.setTextColor(ContextCompat.getColor(context, R.color.black));
            binding.btnAudioFilter.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_button_selected));
            binding.btnAudioFilter.setTextColor(ContextCompat.getColor(context, R.color.white));
            binding.btnFileFilter.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_button_non_selected));
            binding.btnFileFilter.setTextColor(ContextCompat.getColor(context, R.color.black));
            binding.tvFilter.setText(getString(R.string.audio_filter) + " (" + filterItem(FILE_TYPE_AUDIO) + ")");
        });

        binding.btnFileFilter.setOnClickListener(view -> {
            binding.btnAllFilter.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_button_non_selected));
            binding.btnAllFilter.setTextColor(ContextCompat.getColor(context, R.color.black));
            binding.btnPhotoFilter.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_button_non_selected));
            binding.btnPhotoFilter.setTextColor(ContextCompat.getColor(context, R.color.black));
            binding.btnVideoFilter.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_button_non_selected));
            binding.btnVideoFilter.setTextColor(ContextCompat.getColor(context, R.color.black));
            binding.btnAudioFilter.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_button_non_selected));
            binding.btnAudioFilter.setTextColor(ContextCompat.getColor(context, R.color.black));
            binding.btnFileFilter.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_button_selected));
            binding.btnFileFilter.setTextColor(ContextCompat.getColor(context, R.color.white));
            binding.tvFilter.setText(getString(R.string.file_filter) + " (" + filterItem(FILE_TYPE_FILE) + ")");
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (progressHistory.isShowing()) {
            progressHistory.dismiss();
        }
    }

    public void onBackPressed() {
        _isBack = true;
        context.finish();
    }
}