package com.app.allfilerecovery.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.Locale;

public class SystemUtil {
    private static Locale myLocale;
    // Lưu ngôn ngữ đã cài đặt
    public static void saveLocale(Context context,String lang) {
        setPreLanguage(context, lang);
    }

    // Load lại ngôn ngữ đã lưu và thay đổi chúng
    public static void setLocale(Context context) {
        String language = getPreLanguage(context);
        if (language.equals("")){
            Configuration config = new Configuration();
            Locale locale = Locale.getDefault();
            Locale.setDefault(locale);
            config.locale = locale;
            context.getResources()
                    .updateConfiguration(config, context.getResources().getDisplayMetrics());
        }else {
            changeLang(language, context);
        }
    }
    // method phục vụ cho việc thay đổi ngôn ngữ.
    public static void changeLang(String lang, Context context) {
        if (lang.equalsIgnoreCase(""))
            return;
        myLocale = new Locale(lang);
        saveLocale(context,lang);
        Locale.setDefault(myLocale);
        Configuration config = new Configuration();
        config.locale = myLocale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

    public static String getPreLanguage(Context mContext) {
        SharedPreferences preferences = mContext.getSharedPreferences("data", Context.MODE_PRIVATE);
        return preferences.getString("KEY_LANGUAGE", "");
    }

    public static void setPreLanguage(Context context, String language) {
        if (language==null || language.equals("")){
        }else{
            SharedPreferences preferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("KEY_LANGUAGE", language);
            editor.apply();
        }
    }
}
