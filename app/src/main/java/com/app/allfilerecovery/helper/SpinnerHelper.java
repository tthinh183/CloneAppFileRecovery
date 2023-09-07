package com.app.allfilerecovery.helper;

import android.content.Context;

import com.app.allfilerecovery.R;
import com.app.allfilerecovery.utils.Const;

import java.util.ArrayList;

public class SpinnerHelper {
    private final Context context;
    public ArrayList<String> listDate;
    public ArrayList<String> listSize;
    public ArrayList<String> listTypeImage;
    public ArrayList<String> listTypeVideo;
    public ArrayList<String> listTypeAudio;
    public ArrayList<String> listTypeFile;

    public SpinnerHelper(Context context) {
        this.context = context;
        init();
    }

    private void init() {
        if (context != null) {
            listDate = new ArrayList<>();
            listDate.add(context.getString(R.string.spinner_today));
            listDate.add(context.getString(R.string.spinner_yesterday));
            listDate.add(context.getString(R.string.spinner_other));
        }

        listSize = new ArrayList<>();
        listSize.add("< 1MB");
        listSize.add("1MB ~ 10MB");
        listSize.add("10MB ~ 100MB");
        listSize.add("100MB ~ 500MB");
        listSize.add("500MB ~ >1GB");

        listTypeImage = new ArrayList<>();
        listTypeImage.add(Const.FILTER_PNG);
        listTypeImage.add(Const.FILTER_JPG);
        listTypeImage.add(Const.FILTER_JPEG);
        listTypeImage.add(Const.FILTER_WEBP);

        listTypeVideo = new ArrayList<>();
        listTypeVideo.add(Const.FILTER_MP4);
        listTypeVideo.add(Const.FILTER_WEBM);
        listTypeVideo.add(Const.FILTER_3GP);

        listTypeAudio = new ArrayList<>();
        listTypeAudio.add(Const.FILTER_MP3);
        listTypeAudio.add(Const.FILTER_MAV);
        listTypeAudio.add(Const.FILTER_FLAC);
        listTypeAudio.add(Const.FILTER_AIFF);
        listTypeAudio.add(Const.FILTER_AAC);

        listTypeFile = new ArrayList<>();
        listTypeFile.add(Const.FILTER_PDF);
        listTypeFile.add(Const.FILTER_JSON);
        listTypeFile.add(Const.FILTER_DOC);
        listTypeFile.add(Const.FILTER_APK);
        listTypeFile.add(Const.FILTER_TXT);
        listTypeFile.add(Const.FILTER_DAT);
    }
}
