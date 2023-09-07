package com.app.allfilerecovery.model;

import com.app.allfilerecovery.utils.FileUtils;

import java.io.File;

public class ImageDataModel {

    private String path;
    private String folder;
    private String size;
    private long lastModified;
    private boolean isChecked;

    public ImageDataModel() {
    }

    public ImageDataModel(String path, String folder, boolean isChecked) {
        this.path = path;
        this.folder = folder;
        this.isChecked = isChecked;

        File file = new File(path);
        size = FileUtils.getFileSizeKiloBytes(file);
        lastModified = file.lastModified();
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }
}
