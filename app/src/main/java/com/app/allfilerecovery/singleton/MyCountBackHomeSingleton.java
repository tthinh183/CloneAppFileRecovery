package com.app.allfilerecovery.singleton;

public class MyCountBackHomeSingleton {
    private static MyCountBackHomeSingleton instance;
    private int myInt;
    private boolean isShowRate = false;

    private MyCountBackHomeSingleton() {
        // private constructor to prevent instantiation from outside
    }

    public static MyCountBackHomeSingleton getInstance() {
        if (instance == null) {
            instance = new MyCountBackHomeSingleton();
        }
        return instance;
    }

    public int getMyInt() {
        return myInt;
    }

    public void setMyInt(int myInt) {
        this.myInt = myInt;
    }

    public boolean isShowRate() {
        return isShowRate;
    }

    public void setShowRate(boolean showRate) {
        isShowRate = showRate;
    }
}
