package com.app.allfilerecovery.view.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.app.allfilerecovery.R;

public class BottomMenu extends ConstraintLayout {
    OnMenuClick onMenuClick;
    View btnHome, btnRecycleBin, btnHistory;
    TextView tvRecovery, tvRecycle, tvHistory;
    ImageView imgRecovery, imgRecycle, imgHistory;
    ImageView imgRecoverySelected, imgRecycleSelected, imgHistorySelected;
    private Context context;
    public BottomMenu(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView(context);
    }

    public BottomMenu(@NonNull Context context) {
        super(context);
        this.context = context;
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.layout_bottom_menu, this);
        tvRecovery = findViewById(R.id.tv_recovery);
        tvRecycle = findViewById(R.id.tv_recycle);
        tvHistory = findViewById(R.id.tv_history);
        imgRecovery = findViewById(R.id.img_recovery);
        imgRecycle = findViewById(R.id.img_recycle);
        imgHistory = findViewById(R.id.img_history);
        imgRecoverySelected = findViewById(R.id.img_recovery_selected);
        imgRecycleSelected = findViewById(R.id.img_recycle_selected);
        imgHistorySelected = findViewById(R.id.img_history_selected);
        btnHome = findViewById(R.id.btn_home);
        btnRecycleBin = findViewById(R.id.btn_recycle_bin);
        btnHistory = findViewById(R.id.btn_history);

        listenerOnClickItem();
    }

    private void listenerOnClickItem() {
        btnHome.setOnClickListener(view -> {
            selectScreen(ScreenTag.HOME);
            onMenuClick.onClick(Action.OPEN_HOME);
        });
        btnRecycleBin.setOnClickListener(view -> {
            selectScreen(ScreenTag.RECYCLE_BIN);
            onMenuClick.onClick(Action.OPEN_RECYCLE_BIN);
        });
        btnHistory.setOnClickListener(view -> {
            selectScreen(ScreenTag.HISTORY);
            onMenuClick.onClick(Action.OPEN_HISTORY);
        });
    }

    public void selectScreen(ScreenTag view){
        switch (view){
            case HOME:
                imgRecoverySelected.setVisibility(VISIBLE);
                imgRecycleSelected.setVisibility(GONE);
                imgHistorySelected.setVisibility(GONE);
                tvRecovery.setVisibility(GONE);
                imgRecovery.setVisibility(GONE);
                tvRecycle.setVisibility(VISIBLE);
                imgRecycle.setVisibility(VISIBLE);
                tvHistory.setVisibility(VISIBLE);
                imgHistory.setVisibility(VISIBLE);
                break;
            case RECYCLE_BIN:
                imgRecoverySelected.setVisibility(GONE);
                imgRecycleSelected.setVisibility(VISIBLE);
                imgHistorySelected.setVisibility(GONE);
                tvRecovery.setVisibility(VISIBLE);
                imgRecovery.setVisibility(VISIBLE);
                tvRecycle.setVisibility(GONE);
                imgRecycle.setVisibility(GONE);
                tvHistory.setVisibility(VISIBLE);
                imgHistory.setVisibility(VISIBLE);
                break;
            case HISTORY:
                imgRecoverySelected.setVisibility(GONE);
                imgRecycleSelected.setVisibility(GONE);
                imgHistorySelected.setVisibility(VISIBLE);
                tvRecovery.setVisibility(VISIBLE);
                imgRecovery.setVisibility(VISIBLE);
                tvRecycle.setVisibility(VISIBLE);
                imgRecycle.setVisibility(VISIBLE);
                tvHistory.setVisibility(GONE);
                imgHistory.setVisibility(GONE);
                break;
        }
    }
    public enum Action {
        OPEN_HOME, OPEN_RECYCLE_BIN, OPEN_HISTORY
    }
    public enum ScreenTag {
        HOME, RECYCLE_BIN, HISTORY
    }
    public void addListener(Context context, OnMenuClick onMenuClick){
        this.context = context;
        this.onMenuClick = onMenuClick;
    }
    public interface OnMenuClick{
        void onClick(Action action);
    }
}
