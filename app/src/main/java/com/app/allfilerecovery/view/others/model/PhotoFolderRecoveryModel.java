package com.app.allfilerecovery.view.others.model;

import com.app.allfilerecovery.model.ImageDataModel;

import java.util.ArrayList;

public class PhotoFolderRecoveryModel {

    private String nameFolder;
    private int amountPhotos;

    private ArrayList<ImageDataModel> listImages;

    public PhotoFolderRecoveryModel(){}

    public PhotoFolderRecoveryModel(String nameFolder, int amountPhotos, ArrayList<ImageDataModel> listImages) {
        this.nameFolder = nameFolder;
        this.amountPhotos = amountPhotos;
        this.listImages = listImages;
    }

    public String getNameFolder() {
        return nameFolder;
    }

    public void setNameFolder(String nameFolder) {
        this.nameFolder = nameFolder;
    }

    public ArrayList<ImageDataModel> getListImages() {
        return listImages;
    }

    public void setListImages(ArrayList<ImageDataModel> listImages) {
        this.listImages = listImages;
    }

    public int getAmountPhotos() {
        return amountPhotos;
    }

    public void setAmountPhotos(int amountPhotos) {
        this.amountPhotos = amountPhotos;
    }
}
