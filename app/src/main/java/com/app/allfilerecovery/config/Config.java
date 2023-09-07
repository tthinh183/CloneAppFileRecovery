package com.app.allfilerecovery.config;

import android.os.Environment;

import java.io.File;

public class Config {

    public static final int DATA = 1000;
    public static final int REPAIR = 2000;
    public static final int UPDATE = 3000;
    public static final String IMAGE_RECOVER_DIRECTORY;

    static {

        StringBuilder sbDirectory = new StringBuilder();
        sbDirectory.append(Environment.getExternalStorageDirectory());
        sbDirectory.append(File.separator);
        sbDirectory.append("AllFileRecovery");
        IMAGE_RECOVER_DIRECTORY = sbDirectory.toString();

    }


    public static final String APP_PREFERENCE = "appPreference";
    public static final String FILE_TYPE = "file_type";
    public static final String FILE_TYPE_IMAGE = "image";
    public static final String FILE_TYPE_VIDEO = "video";

    public static final String FILE_TYPE_AUDIO = "audio";
    public static final String FILE_TYPE_FILE = "file";
    public static final String NAME_FOLDER = "name_folder";
}