package com.app.allfilerecovery.view.others;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

import com.app.allfilerecovery.R;

public class PolicyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy);

        WebView webView = findViewById(R.id.webPolicy);

        webView.loadUrl(getString(R.string.policy));
    }
}