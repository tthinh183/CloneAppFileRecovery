package com.app.allfilerecovery.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.allfilerecovery.R;

public class RatingDialog extends Dialog {
    private OnPress onPress;
    private final Context context;
    private final TextView btnRate;
    private final TextView btnNotNow;
    private final TextView btnNever;
    private final ImageView imgStar1, imgStar2, imgStar3, imgStar4, imgStar5;
    private int rating = 0;

    public RatingDialog(Context context2) {
        super(context2, R.style.BaseDialog);
        this.context = context2;
        setContentView(R.layout.dialog_rating_app);
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
        attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(attributes);
        getWindow().setSoftInputMode(16);
        btnRate = findViewById(R.id.btnSubmit);
        btnNotNow = findViewById(R.id.btnNotNow);
        btnNever = findViewById(R.id.btnNever);
        //init view for rating
        imgStar1 = findViewById(R.id.img_star1);
        imgStar2 = findViewById(R.id.img_star2);
        imgStar3 = findViewById(R.id.img_star3);
        imgStar4 = findViewById(R.id.img_star4);
        imgStar5 = findViewById(R.id.img_star5);
        rating = 5;
        onclick();
    }

    private void handleRating() {
        imgStar1.setOnClickListener(view -> {
            imgStar1.setImageResource(R.drawable.rating_selected);
            imgStar2.setImageResource(R.drawable.rating_non_select);
            imgStar3.setImageResource(R.drawable.rating_non_select);
            imgStar4.setImageResource(R.drawable.rating_non_select);
            imgStar5.setImageResource(R.drawable.rating_non_select);
            rating = 1;
        });

        imgStar2.setOnClickListener(view -> {
            imgStar1.setImageResource(R.drawable.rating_selected);
            imgStar2.setImageResource(R.drawable.rating_selected);
            imgStar3.setImageResource(R.drawable.rating_non_select);
            imgStar4.setImageResource(R.drawable.rating_non_select);
            imgStar5.setImageResource(R.drawable.rating_non_select);
            rating = 2;
        });

        imgStar3.setOnClickListener(view -> {
            imgStar1.setImageResource(R.drawable.rating_selected);
            imgStar2.setImageResource(R.drawable.rating_selected);
            imgStar3.setImageResource(R.drawable.rating_selected);
            imgStar4.setImageResource(R.drawable.rating_non_select);
            imgStar5.setImageResource(R.drawable.rating_non_select);
            rating = 3;
        });

        imgStar4.setOnClickListener(view -> {
            imgStar1.setImageResource(R.drawable.rating_selected);
            imgStar2.setImageResource(R.drawable.rating_selected);
            imgStar3.setImageResource(R.drawable.rating_selected);
            imgStar4.setImageResource(R.drawable.rating_selected);
            imgStar5.setImageResource(R.drawable.rating_non_select);
            rating = 4;
        });

        imgStar5.setOnClickListener(view -> {
            imgStar1.setImageResource(R.drawable.rating_selected);
            imgStar2.setImageResource(R.drawable.rating_selected);
            imgStar3.setImageResource(R.drawable.rating_selected);
            imgStar4.setImageResource(R.drawable.rating_selected);
            imgStar5.setImageResource(R.drawable.rating_selected);
            rating = 5;
        });
    }

    public interface OnPress {
        void send();

        void rating();

        void cancel();

        void later();
    }

    public void init(Context context, OnPress onPress) {
        this.onPress = onPress;
    }

    public String getRating() {
        return String.valueOf(rating);
    }

    public void onclick() {
        btnRate.setOnClickListener(view -> {
            if (rating == 0) {
                return;
            }
            if (rating <= 3.0) {
                onPress.send();
                context.getSharedPreferences("Rating", Context.MODE_PRIVATE).edit()
                        .putBoolean("IS_RATED", true).apply();
            } else {
                onPress.rating();
                context.getSharedPreferences("Rating", Context.MODE_PRIVATE).edit()
                        .putBoolean("IS_RATED", true).apply();
            }
        });

        btnNotNow.setOnClickListener(view -> onPress.later());
        btnNever.setOnClickListener(view -> {
            context.getSharedPreferences("Rating", Context.MODE_PRIVATE).edit()
                    .putBoolean("IS_RATED", true).apply();
            onPress.later();
        });

        handleRating();
    }
}
