package com.app.allfilerecovery.view.home;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.amazic.ads.util.AppOpenManager;
import com.app.allfilerecovery.R;
import com.app.allfilerecovery.base.BaseFragment;
import com.app.allfilerecovery.databinding.FragmentHomeBinding;
import com.app.allfilerecovery.dialog.RatingDialog;
import com.app.allfilerecovery.local.SharePrefUtils;
import com.app.allfilerecovery.singleton.MyCountBackHomeSingleton;
import com.app.allfilerecovery.view.others.MediaScannerActivity;
import com.app.allfilerecovery.view.settings.InfoScreenActivity;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;

import static com.app.allfilerecovery.config.Config.FILE_TYPE;
import static com.app.allfilerecovery.config.Config.FILE_TYPE_AUDIO;
import static com.app.allfilerecovery.config.Config.FILE_TYPE_FILE;
import static com.app.allfilerecovery.config.Config.FILE_TYPE_IMAGE;
import static com.app.allfilerecovery.config.Config.FILE_TYPE_VIDEO;

public class HomeFragment extends BaseFragment<FragmentHomeBinding> {
    private static final String TAG = "HomeActivity_";
    Activity context;
    private ReviewManager manager = null;
    private ReviewInfo reviewInfo = null;

    @Override
    protected FragmentHomeBinding setViewBinding(LayoutInflater inflater, @Nullable ViewGroup viewGroup) {
        return FragmentHomeBinding.inflate(inflater, viewGroup, false);
    }

    protected void initView() {
        AppOpenManager.getInstance().enableAppResumeWithActivity(HomeFragment.class);

        context = requireActivity();
        context.getSharedPreferences("prefs", Context.MODE_PRIVATE).edit()
                .putBoolean("firstOpen", false).apply();


        handleOnClick();
    }

    @Override
    protected void viewListener() {
        binding.icSettings.setOnClickListener(v -> startActivity(new Intent(context, InfoScreenActivity.class)));

    }

    @Override
    protected void dataObservable() {

    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sharedPref;
        SharedPreferences.Editor editor;
        sharedPref = context.getSharedPreferences("Rating", Context.MODE_PRIVATE);
        boolean isRating;
        isRating = sharedPref.getBoolean("IS_RATED", false);
        if (!isRating) {
            if (MyCountBackHomeSingleton.getInstance().getMyInt() == 2 ||
                    MyCountBackHomeSingleton.getInstance().getMyInt() == 5 ||
                    MyCountBackHomeSingleton.getInstance().getMyInt() == 7 ||
                    MyCountBackHomeSingleton.getInstance().getMyInt() == 9) {
                if (!MyCountBackHomeSingleton.getInstance().isShowRate()) {
                    showRateSettingDialog();
                    MyCountBackHomeSingleton.getInstance().setShowRate(true);
                }
            }
        }
    }

    private void handleOnClick() {
        binding.llPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(context, MediaScannerActivity.class);
            intent.putExtra(FILE_TYPE, FILE_TYPE_IMAGE);
            startActivity(intent);
        });

        binding.llVideo.setOnClickListener(v -> {
            Intent intent = new Intent(context, MediaScannerActivity.class);
            intent.putExtra(FILE_TYPE, FILE_TYPE_VIDEO);
            startActivity(intent);
        });

        binding.llAudio.setOnClickListener(v -> {
            Intent intent = new Intent(context, MediaScannerActivity.class);
            intent.putExtra(FILE_TYPE, FILE_TYPE_AUDIO);
            startActivity(intent);
        });

        binding.llFile.setOnClickListener(v -> {
            Intent intent = new Intent(context, MediaScannerActivity.class);
            intent.putExtra(FILE_TYPE, FILE_TYPE_FILE);
            startActivity(intent);
        });

//        llRecycle.setOnClickListener(v -> {
//            if(checkPermission()){
//                Intent intent = new Intent(context, RecycleBinActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        llHistory.setOnClickListener(v -> {
//            if(!checkPermission()){
//                return;
//            }
//
//            Admob.getInstance().showInterAds(HomeActivity.this, mInterstitialAd, new InterCallback() {
//                @Override
//                public void onNextAction() {
//                    mInterstitialAd = null;
//                    loadInterRestore();
//                    Intent intent = new Intent(context, HistoryActivity.class);
//                    startActivity(intent);
//                }
//            });
//
//        });
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
                    context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int permissionRead = ActivityCompat.checkSelfPermission(
                    context, Manifest.permission.READ_EXTERNAL_STORAGE);

            if (permissionWrite == PackageManager.PERMISSION_GRANTED && permissionRead == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                askPermission();
                return false;
            }
        }
    }

    private void askPermission() {
        final Dialog dialog = new Dialog(context);
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
                    AppOpenManager.getInstance().disableAppResumeWithActivity(HomeFragment.class);
                    //request for the permission
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                    intent.setData(uri);

                    if (intent.resolveActivity(context.getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
            } else {
                AppOpenManager.getInstance().disableAppResumeWithActivity(HomeFragment.class);
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        });
        dialog.show();
    }


    private void showRateSettingDialog() {
        SharedPreferences sharedPref;
        SharedPreferences.Editor editor;
        sharedPref = context.getSharedPreferences("Rating", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        RatingDialog ratingDialog = new RatingDialog(context);
        ratingDialog.init(context, new RatingDialog.OnPress() {
            @Override
            public void send() {
                AppOpenManager.getInstance().disableAdResumeByClickAction();
                ratingDialog.dismiss();
                String uriText = "mailto:" + SharePrefUtils.email + "," + SharePrefUtils.email1
                        + "?subject=" + SharePrefUtils.subjectRate + "&body=" + getString(R.string.app_name) + "\nRate: " + ratingDialog.getRating() + "(star)\nContent:".trim();
                Uri uri = Uri.parse(uriText);
                Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                sendIntent.setData(uri);
                try {
                    startActivity(Intent.createChooser(sendIntent, getString(R.string.Send_Email)));
                    SharePrefUtils.forceRated(context);
                    editor.putBoolean("IS_RATED", true);
                    editor.apply();
                } catch (ActivityNotFoundException ex) {
                    Toast.makeText(context, getString(R.string.There_is_no), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void rating() {
                AppOpenManager.getInstance().disableAdResumeByClickAction();
                manager = ReviewManagerFactory.create(context);
                Task<ReviewInfo> request = manager.requestReviewFlow();
                request.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        reviewInfo = task.getResult();
                        Log.e("ReviewInfo", "" + reviewInfo);
                        Task<Void> flow = manager.launchReviewFlow(context, reviewInfo);
                        flow.addOnSuccessListener(result -> {
                            editor.putBoolean("IS_RATED", true);
                            editor.apply();
                            SharePrefUtils.forceRated(context);
                            ratingDialog.dismiss();
                        });
                    } else {
                        ratingDialog.dismiss();
                    }
                });
            }

            @Override
            public void cancel() {
                SharePrefUtils.increaseCountOpenApp(context);
                ///System.exit(0);
            }

            @Override
            public void later() {
                SharePrefUtils.increaseCountOpenApp(context);
                ratingDialog.dismiss();
            }
        });
        try {
            ratingDialog.show();
        } catch (WindowManager.BadTokenException e) {
            e.printStackTrace();
        }
    }
}
