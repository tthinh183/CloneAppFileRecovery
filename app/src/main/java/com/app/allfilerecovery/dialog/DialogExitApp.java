package com.app.allfilerecovery.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.app.allfilerecovery.R;
import com.app.allfilerecovery.callback.IBaseListener;

public class DialogExitApp extends Dialog {
    public Activity activity;
    TextView tvCancel, tvExit;
    IBaseListener iBaseListener;

    public DialogExitApp(Activity activity, IBaseListener iBaseListener) {
        super(activity, R.style.BaseDialog);
        this.activity = activity;
        this.iBaseListener = iBaseListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_exit_app);
        setCancelable(true);

        tvCancel = findViewById(R.id.tv_cancel);
        tvExit = findViewById(R.id.tv_exit);

        tvCancel.setOnClickListener(v -> iBaseListener.onCancel());

        tvExit.setOnClickListener(v -> iBaseListener.onExit());
    }
}
