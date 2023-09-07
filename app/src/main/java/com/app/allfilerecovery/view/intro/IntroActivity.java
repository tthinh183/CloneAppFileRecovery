package com.app.allfilerecovery.view.intro;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.amazic.ads.callback.InterCallback;
import com.amazic.ads.callback.NativeCallback;
import com.amazic.ads.util.Admob;
import com.app.allfilerecovery.MainActivity;
import com.app.allfilerecovery.R;
import com.app.allfilerecovery.adapter.SlideAdapter;
import com.app.allfilerecovery.base.BaseActivity;
import com.app.allfilerecovery.local.SystemUtil;
import com.app.allfilerecovery.utils.AdsUtils;
import com.app.allfilerecovery.view.home.HomeFragment;
import com.app.allfilerecovery.view.permission.PermissionActivity;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;

import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends BaseActivity {

    Context context;
    ImageView agreeText;
    TextView introTernText, tvNext, introConditionText;
    ViewPager2 vpSlideIntro;

    ImageView imgView1;
    ImageView imgView2;
    ImageView imgView3;
    List<String> introTextList;

    List<String> introConditionList;

    private FrameLayout nativeAds;
    private boolean firstStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SystemUtil.setLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_view);

        initView();
        initIntroSlide();
        context = this;

        //set background status bar
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        //load native ads
        nativeAds = findViewById(R.id.native_ads);

        Admob.getInstance().loadNativeAdFloor(this, AdsUtils.listNativeIntroId, new NativeCallback() {
            @Override
            public void onNativeAdLoaded(NativeAd nativeAd) {
                super.onNativeAdLoaded(nativeAd);
                NativeAdView adView = (NativeAdView) LayoutInflater.from(IntroActivity.this).inflate(R.layout.ads_native_intro_small, null);
                nativeAds.removeAllViews();
                nativeAds.addView(adView);
                Admob.getInstance().pushAdsToViewCustom(nativeAd, adView);
            }

            @Override
            public void onAdFailedToLoad() {
                super.onAdFailedToLoad();
                nativeAds.removeAllViews();
            }
        });

//        agreeText.setOnClickListener(v -> {
//            if (vpSlideIntro.getCurrentItem() < 2) {
//                vpSlideIntro.setCurrentItem(vpSlideIntro.getCurrentItem() + 1);
//            } else {
//                Admob.getInstance().showInterAds(IntroActivity.this, AdsUtils.interstitialAdIntro, new InterCallback() {
//                    @Override
//                    public void onNextAction() {
//                        AdsUtils.interstitialAdIntro = null;
//                        startActivity(new Intent(IntroActivity.this, MainActivity.class));
//                        finish();
//                    }
//                });
//            }
//        });

        tvNext.setOnClickListener(v -> {
            if (vpSlideIntro.getCurrentItem() < 2) {
                vpSlideIntro.setCurrentItem(vpSlideIntro.getCurrentItem() + 1);
            } else {
                moveForward();
            }
        });

        vpSlideIntro.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                changeColor();
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 2) {
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
                        //moveForward();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                changeColor();
            }
        });
    }

    private void initIntroSlide() {
        vpSlideIntro = findViewById(R.id.vp_slide_intro);
        List<Integer> introImageList = new ArrayList<>();
        introImageList.add(R.drawable.image_intro1);
        introImageList.add(R.drawable.image_intro2);
        introImageList.add(R.drawable.image_intro3);
        SlideAdapter slideAdapter = new SlideAdapter(introImageList);
        vpSlideIntro.setAdapter(slideAdapter);
    }

    private void initView() {
        imgView1 = findViewById(R.id.img_circle_1);
        imgView2 = findViewById(R.id.img_circle_2);
        imgView3 = findViewById(R.id.img_circle_3);
//        agreeText = findViewById(R.id.agree_image);
        introTernText = findViewById(R.id.terms_text);
        introConditionText = findViewById(R.id.conditions_text);
        tvNext = findViewById(R.id.tv_next);


        introTextList = new ArrayList<>();
        introTextList.add(getString(R.string.scan_files_in_your_phone));
        introTextList.add(getString(R.string.view_files_from_trash_bin));
        introTextList.add(getString(R.string.restore_files_back));
        introTernText.setText(introTextList.get(0));

        introConditionList = new ArrayList<>();
        introConditionList.add(getString(R.string.search_all_files));
        introConditionList.add(getString(R.string.have_a_look));
        introConditionList.add(getString(R.string.choose_the_file));
        introConditionText.setText(introConditionList.get(0));
    }

    void changeColor() {
        switch (vpSlideIntro.getCurrentItem()) {
            case 0:
                imgView1.setImageResource(R.drawable.ic_dot_selected);
                imgView2.setImageResource(R.drawable.ic_dot_not_select);
                imgView3.setImageResource(R.drawable.ic_dot_not_select);
                introTernText.setText(Html.fromHtml(introTextList.get(0)));
                introConditionText.setText(Html.fromHtml(introConditionList.get(0)));
                break;
            case 1:
                imgView1.setImageResource(R.drawable.ic_dot_not_select);
                imgView2.setImageResource(R.drawable.ic_dot_selected);
                imgView3.setImageResource(R.drawable.ic_dot_not_select);
                introTernText.setText(Html.fromHtml(introTextList.get(1)));
                introConditionText.setText(Html.fromHtml(introConditionList.get(1)));
                break;
            case 2:
                imgView1.setImageResource(R.drawable.ic_dot_not_select);
                imgView2.setImageResource(R.drawable.ic_dot_not_select);
                imgView3.setImageResource(R.drawable.ic_dot_selected);
                introTernText.setText(Html.fromHtml(introTextList.get(2)));
                introConditionText.setText(Html.fromHtml(introConditionList.get(2)));
                break;
        }
    }
    private void moveForward() {
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        firstStart = prefs.getBoolean("firstOpen", true);

        if(firstStart){
            Intent intent = new Intent(IntroActivity.this, PermissionActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(IntroActivity.this, MainActivity.class);
            startActivity(intent);
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
}
