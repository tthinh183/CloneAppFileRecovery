package com.app.allfilerecovery.utils;

public class Const {

    public static final int READ_STORAGE_CODE = 100;
    public static final int FILE_TYPE_PHOTO = 1;
    public static final int FILE_TYPE_VIDEO = 2;
    public static final int FILE_TYPE_AUDIO = 3;
    public static final int FILE_TYPE_FILE = 4;
    public static final String FILE_TYPE_HISTORY = "file_type_history";
    public static final String NAME_HISTORY = "name_file_history";
    public static final String PATH_HISTORY = "path_file_history";
    public static final String SIZE_HISTORY = "size_file_history";
    public static final String TIME_RESTORE_HISTORY = "time_restore_file_history";

    // filter file by date
    public static final int FILTER_TODAY = 0;
    public static final int FILTER_YESTERDAY = 1;
    public static final int FILTER_OTHER = 2;

    // filter file by size
    public static final int FILTER_LESS_1MB = 0;
    public static final int FILTER_1MB_10MB = 1;
    public static final int FILTER_10MB_100MB = 2;
    public static final int FILTER_100MB_500MB = 3;
    public static final int FILTER_500MB_THAN_1GB = 4;

    // filter type for image
    public static final String FILTER_JPG = ".jpg";
    public static final String FILTER_JPEG = ".jpeg";
    public static final String FILTER_PNG = ".png";
    public static final String FILTER_WEBP = ".webp";

    // filter type for video
    public static final String FILTER_MP4 = ".mp4";
    public static final String FILTER_WEBM = ".webm";
    public static final String FILTER_3GP = ".3gp";

    // filter type for audio
    public static final String FILTER_MP3 = ".mp3";
    public static final String FILTER_MAV = ".mav";
    public static final String FILTER_FLAC = ".flac";
    public static final String FILTER_AIFF = ".aiff";
    public static final String FILTER_AAC = ".aac";

    // filter type for file
    public static final String FILTER_PDF = ".pdf";
    public static final String FILTER_JSON = ".json";
    public static final String FILTER_DOC = ".doc";
    public static final String FILTER_APK = ".apk";
    public static final String FILTER_TXT = ".txt";
    public static final String FILTER_DAT = ".dat";


    // view type adapter
    public static final int VIEW_TYPE_HEADER = 0;
    public static final int VIEW_TYPE_ITEM = 1;

    // category of file
    public static final int CATEGORY_TODAY = 0;
    public static final int CATEGORY_YESTERDAY = 1;
    public static final int CATEGORY_OTHER = 2;

    // activity restore
    public static final String LABEL_ACTIVITY = "back_to_activity";
    public static final int PHOTO_RESTORE = 0;
    public static final int OTHER_RESTORE = 1;
    public static final int RECYCLE_BIN = 2;
}
