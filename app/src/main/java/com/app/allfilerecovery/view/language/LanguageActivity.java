package com.app.allfilerecovery.view.language;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amazic.ads.util.Admob;
import com.app.allfilerecovery.BuildConfig;
import com.app.allfilerecovery.MainActivity;
import com.app.allfilerecovery.R;
import com.app.allfilerecovery.base.BaseActivity;
import com.app.allfilerecovery.utils.AdsUtils;
import com.app.allfilerecovery.view.home.HomeFragment;
import com.app.allfilerecovery.view.language.Interface.IClickItemLanguage;
import com.app.allfilerecovery.view.language.Model.LanguageModel;
import com.app.allfilerecovery.view.language.adapter.LanguageAdapter;
import com.app.allfilerecovery.local.SystemUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LanguageActivity extends BaseActivity {
    RecyclerView recyclerView;
    ImageView btn_back;
    List<LanguageModel> listLanguage;
    String codeLang;
    private String idBanner = "";
    private View adsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemUtil.setLocale(this);
        setContentView(R.layout.activity_language);

        //set background status bar
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        recyclerView = findViewById(R.id.recyclerView);
        btn_back = findViewById(R.id.btn_back);
        adsView = findViewById(R.id.include);
        codeLang = Locale.getDefault().getLanguage();

        initData();
        btn_back.setOnClickListener(v -> onBackPressed());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        LanguageAdapter languageAdapter = new LanguageAdapter(listLanguage, code -> {
            codeLang = code;
            SystemUtil.saveLocale(getBaseContext(), codeLang);
            back();
        }, this);

        languageAdapter.setCheck(SystemUtil.getPreLanguage(getBaseContext()));


        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(languageAdapter);
        //   recyclerView.addItemDecoration(itemDecoration);
        configMediationProvider();

        //load ads banner
        Admob.getInstance().loadBannerFloor(this, AdsUtils.listBannerAllId);
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
            adsView.setVisibility(View.GONE);
        } else {
            adsView.setVisibility(View.VISIBLE);
        }
    }

    private void initData() {
        listLanguage = new ArrayList<>();
        listLanguage.add(new LanguageModel("English", "en"));
        listLanguage.add(new LanguageModel("French", "fr"));
        listLanguage.add(new LanguageModel("Portuguese", "pt"));
        listLanguage.add(new LanguageModel("Spanish", "es"));
        listLanguage.add(new LanguageModel("Hindi", "hi"));
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void back() {
        startActivity(new Intent(LanguageActivity.this, MainActivity.class));
        finishAffinity();
    }

    private void configMediationProvider() {
        //idBanner = BuildConfig.banner_all;
    }
}
