package com.app.allfilerecovery.model;

import com.app.allfilerecovery.utils.FileUtils;

import java.io.File;

public class RecycleBinModel {
    private int categoryFile;
    private String path;
    private String typeFile;
    private String size;
    private long lastModified;
    private boolean isChecked;

    public RecycleBinModel() {
    }

    public RecycleBinModel(String path, String typeFile, boolean isChecked) {
        this.path = path;
        this.typeFile = typeFile;
        this.isChecked = isChecked;

        File file = new File(path);
        size = FileUtils.getFileSizeKiloBytes(file);
        lastModified = file.lastModified();
    }

    public String getTypeFile() {
        return typeFile;
    }

    public void setTypeFile(String typeFile) {
        this.typeFile = typeFile;
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

    public int getCategoryFile() {
        return categoryFile;
    }

    public void setCategoryFile(int categoryFile) {
        this.categoryFile = categoryFile;
    }
}
