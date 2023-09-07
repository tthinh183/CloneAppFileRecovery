package com.app.allfilerecovery.api;

public class AdsModel {
    int id;
    String app_id;
    String name;
    String ads_id;

    public AdsModel(int id, String app_id, String name, String ads_id) {
        this.id = id;
        this.app_id = app_id;
        this.name = name;
        this.ads_id = ads_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAds_id() {
        return ads_id;
    }

    public void setAds_id(String ads_id) {
        this.ads_id = ads_id;
    }
}
