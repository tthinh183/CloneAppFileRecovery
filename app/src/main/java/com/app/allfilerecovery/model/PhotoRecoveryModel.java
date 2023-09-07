package com.app.allfilerecovery.model;

import java.util.ArrayList;

public class PhotoRecoveryModel {
    public static final int CATEGORY_TODAY = 0;
    public static final int CATEGORY_YESTERDAY = 1;
    public static final int CATEGORY_OTHER = 2;
    private ArrayList<ImageDataModel> photoList;
    private int categoryPhoto;
    private int amountItemOfCategory;

    public PhotoRecoveryModel(ArrayList<ImageDataModel> photoList, int categoryPhoto) {
        this.photoList = photoList;
        this.categoryPhoto = categoryPhoto;
    }

    public ArrayList<ImageDataModel> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(ArrayList<ImageDataModel> photoList) {
        this.photoList = photoList;
    }

    public int getCategoryPhoto() {
        return categoryPhoto;
    }

    public void setCategoryPhoto(int categoryPhoto) {
        this.categoryPhoto = categoryPhoto;
    }

    public int getAmountItemOfCategory() {
        return amountItemOfCategory;
    }

    public void setAmountItemOfCategory(int amountItemOfCategory) {
        this.amountItemOfCategory = amountItemOfCategory;
    }
}
