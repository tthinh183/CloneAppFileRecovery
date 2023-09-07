package com.app.allfilerecovery.view.splash;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.amazic.ads.callback.AdCallback;
import com.amazic.ads.util.AppOpenManager;
import com.app.allfilerecovery.R;
import com.app.allfilerecovery.api.AdsModel;
import com.app.allfilerecovery.api.ApiService;
import com.app.allfilerecovery.base.BaseActivity;
import com.app.allfilerecovery.utils.AdsUtils;
import com.app.allfilerecovery.utils.Const;
import com.app.allfilerecovery.utils.NetworkUtil;
import com.app.allfilerecovery.view.language.LanguageStartActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends BaseActivity {
    boolean firstStart;
    Context context;
    private AdCallback adCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (!isTaskRoot()
                && getIntent().hasCategory(Intent.CATEGORY_LAUNCHER)
                && getIntent().getAction() != null
                && getIntent().getAction().equals(Intent.ACTION_MAIN)
        ) {
            finish();
            return;
        }

        context = this;
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        firstStart = prefs.getBoolean("firstOpen", true);

        adCallback = new AdCallback() {
            @Override
            public void onNextAction() {
                super.onNextAction();
                moveForeword();
            }
        };
        loadSplash();
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        AppOpenManager.getInstance().onCheckShowSplashWhenFail(this, adCallback, 1000);
    }

    private void moveForeword() {
        Intent intent;
        intent = new Intent(SplashActivity.this, LanguageStartActivity.class);
        startActivity(intent);
        finish();
    }

    private void loadSplash() {
        if (NetworkUtil.isInternetConnected(this)) {
            try {
                ApiService.apiService.callAdsSplash().enqueue(new Callback<List<AdsModel>>() {
                    @Override
                    public void onResponse(Call<List<AdsModel>> call, Response<List<AdsModel>> response) {
                        if (response.body().size() != 0) {
                            for (AdsModel adsModel : response.body()) {
                                switch (adsModel.getName()) {
                                    case "native_language":
                                        AdsUtils.listNativeLangId.add(adsModel.getAds_id());
                                        break;
                                    case "native_permission":
                                        AdsUtils.listNativePerId.add(adsModel.getAds_id());
                                        break;
                                    case "native_intro":
                                        AdsUtils.listNativeIntroId.add(adsModel.getAds_id());
                                        break;
                                    case "open_splash":
                                        AdsUtils.listOpenSplashId.add(adsModel.getAds_id());
                                        break;
                                    case "collapse_banner":
                                        AdsUtils.listCollapseBannerId.add(adsModel.getAds_id());
                                        break;
                                    case "inter_all":
                                        AdsUtils.listInterAllId.add(adsModel.getAds_id());
                                        break;
                                    case "banner_all":
                                        AdsUtils.listBannerAllId.add(adsModel.getAds_id());
                                        break;
                                }
                            }
                            AppOpenManager.getInstance().loadOpenAppAdSplashFloor(SplashActivity.this, AdsUtils.listOpenSplashId, true, adCallback);
                        } else {
                            new Handler().postDelayed(() -> moveForeword(), 5000);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<AdsModel>> call, Throwable t) {
                        new Handler().postDelayed(() -> moveForeword(), 5000);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            new Handler().postDelayed(this::moveForeword, 3000);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Const.READ_STORAGE_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    moveForeword();
                } else {
                    Toast.makeText(context, "Please open settings and allow the permission.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}