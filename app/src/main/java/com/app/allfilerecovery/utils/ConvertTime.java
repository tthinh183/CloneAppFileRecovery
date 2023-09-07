package com.app.allfilerecovery.utils;

public class ConvertTime {

    public static String convertTime(int seconds) {
        int minuteCount = seconds / 60;
        int secondCount = seconds % 60;
        String minute;
        String second;
        if (minuteCount < 10) {
            minute = "0" + minuteCount;
        } else {
            minute = "" + minuteCount;
        }
        if (secondCount < 10) {
            second = "0" + secondCount;
        } else {
            second = "" + secondCount;
        }
        return minute + ":" + second;
    }

}
