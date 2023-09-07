package com.app.allfilerecovery.view.language;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amazic.ads.callback.InterCallback;
import com.amazic.ads.callback.NativeCallback;
import com.amazic.ads.util.Admob;
import com.app.allfilerecovery.BuildConfig;
import com.app.allfilerecovery.R;
import com.app.allfilerecovery.base.BaseActivity;
import com.app.allfilerecovery.local.SharePrefUtils;
import com.app.allfilerecovery.local.SystemUtil;
import com.app.allfilerecovery.utils.AdsUtils;
import com.app.allfilerecovery.view.intro.IntroActivity;
import com.app.allfilerecovery.view.language.Model.LanguageModel;
import com.app.allfilerecovery.view.language.adapter.LanguageStartAdapter;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class LanguageStartActivity extends BaseActivity {
    private String idNative = "";

    RecyclerView recyclerView;
    List<LanguageModel> listLanguage;
    String codeLang;
    ImageView btnDone;

    private FrameLayout nativeAds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configMediationProvider();
        setContentView(R.layout.activity_language_start);
        SystemUtil.setLocale(this);
        recyclerView = findViewById(R.id.recyclerView);
        btnDone = findViewById(R.id.ic_done);
        codeLang = Locale.getDefault().getLanguage();
        initData();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        LanguageStartAdapter languageAdapter = new LanguageStartAdapter(listLanguage, code -> codeLang = code, this);

        btnDone.setOnClickListener(v -> {
            SharePrefUtils.increaseCountFirstHelp(LanguageStartActivity.this);
            SystemUtil.saveLocale(getBaseContext(), codeLang);
            startActivity(new Intent(LanguageStartActivity.this, IntroActivity.class));
            finish();
        });

        // set checked default lang local
        String codeLangDefault = Locale.getDefault().getLanguage();
        String[] langDefault = {"hi", "zh", "es", "fr", "pt", "in", "de", "ar", "be"};
        if (!Arrays.asList(langDefault).contains(codeLangDefault)) codeLang = "en";

        languageAdapter.setCheck(codeLang);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(languageAdapter);

        //define ads
        nativeAds = findViewById(R.id.native_ads);

        Admob.getInstance().loadNativeAdFloor(this, AdsUtils.listNativeLangId, new NativeCallback() {
            @Override
            public void onNativeAdLoaded(NativeAd nativeAd) {
                super.onNativeAdLoaded(nativeAd);
                NativeAdView adView = (NativeAdView) LayoutInflater.from(LanguageStartActivity.this).inflate(R.layout.custom_native_admod_medium, null);
                nativeAds.addView(adView);
                Admob.getInstance().pushAdsToViewCustom(nativeAd, adView);
            }

            @Override
            public void onAdFailedToLoad() {
                super.onAdFailedToLoad();
                nativeAds.removeAllViews();
            }
        });

        loadInterIntro();
    }

    private void loadInterIntro() {
        /*if (AdsUtils.interstitialAdIntro == null) {
            Admob.getInstance().loadInterAds(this, BuildConfig.inter_intro, new InterCallback() {
                @Override
                public void onInterstitialLoad(InterstitialAd interstitialAd) {
                    super.onInterstitialLoad(interstitialAd);
                    AdsUtils.interstitialAdIntro = interstitialAd;
                }
            });
        }*/
    }

    private void initData() {
        listLanguage = new ArrayList<>();
        String lang = Locale.getDefault().getLanguage();
        listLanguage.add(new LanguageModel("English", "en"));
        listLanguage.add(new LanguageModel("French", "fr"));
        listLanguage.add(new LanguageModel("Portuguese", "pt"));
        listLanguage.add(new LanguageModel("Spanish", "es"));
        listLanguage.add(new LanguageModel("Hindi", "hi"));

        for (int i = 0; i < listLanguage.size(); i++) {
            if (listLanguage.get(i).getCode().equals(lang)) {
                listLanguage.add(0, listLanguage.get(i));
                listLanguage.remove(i + 1);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
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
            nativeAds.setVisibility(View.GONE);
        } else {
            nativeAds.setVisibility(View.VISIBLE);
        }
    }

    private void configMediationProvider() {
        //idNative = BuildConfig.native_language;
    }

    @Override
    public void onBackPressed() {
        finish();
        System.exit(0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
