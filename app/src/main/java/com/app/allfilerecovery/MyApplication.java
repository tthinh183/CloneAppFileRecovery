package com.app.allfilerecovery;

import androidx.appcompat.app.AppCompatDelegate;

import com.amazic.ads.util.Admob;
import com.amazic.ads.util.AdsApplication;
import com.amazic.ads.util.AppOpenManager;
import com.app.allfilerecovery.view.splash.SplashActivity;
import com.appsflyer.AppsFlyerLib;

import java.util.List;


public class MyApplication extends AdsApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        AppOpenManager.getInstance().disableAppResumeWithActivity(SplashActivity.class);

        // Auto disable ad resume after user click ads and back to app
        Admob.getInstance().setDisableAdResumeWhenClickAds(true);
        // If true -> onNextAction() is called right after Ad Interstitial showed
        Admob.getInstance().setOpenActivityAfterShowInterAds(true);
        AppsFlyerLib.getInstance().init(getString(R.string.AF_DEV_KEY), null, this);
        AppsFlyerLib.getInstance().start(this);
    }

    @Override
    public boolean enableAdsResume() {
        return true;
    }

    @Override
    public List<String> getListTestDeviceId() {
        return null;
    }

    @Override
    public String getResumeAdId() {
        return BuildConfig.appopen_resume;
    }

    @Override
    public Boolean buildDebug() {
        return null;
    }
}
