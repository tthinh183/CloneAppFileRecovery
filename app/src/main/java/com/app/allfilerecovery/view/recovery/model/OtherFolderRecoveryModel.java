package com.app.allfilerecovery.view.recovery.model;

public class OtherFolderRecoveryModel {

    private String nameFolder;
    private int amountFiles;

    public OtherFolderRecoveryModel(){}

    public OtherFolderRecoveryModel(String nameFolder, int amountFiles) {
        this.nameFolder = nameFolder;
        this.amountFiles = amountFiles;
    }

    public String getNameFolder() {
        return nameFolder;
    }

    public void setNameFolder(String nameFolder) {
        this.nameFolder = nameFolder;
    }

    public int getAmountFiles() {
        return amountFiles;
    }

    public void setAmountFiles(int amountFiles) {
        this.amountFiles = amountFiles;
    }
}