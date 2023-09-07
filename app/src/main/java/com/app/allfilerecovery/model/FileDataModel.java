package com.app.allfilerecovery.model;

import com.app.allfilerecovery.utils.FileUtils;

import java.io.File;

public class FileDataModel {
    private int categoryFile;
    private String path;
    private String folder;
    private String size;
    private long lastModified;
    private boolean isChecked;

    public FileDataModel() {
    }

    public FileDataModel(String path, String folder, boolean isChecked, int categoryFile) {
        this.path = path;
        this.folder = folder;
        this.isChecked = isChecked;
        this.categoryFile = categoryFile;

        File file = new File(path);
        size = FileUtils.getFileSizeKiloBytes(file);
        lastModified = file.lastModified();
    }

    public int getCategoryFile() {
        return categoryFile;
    }

    public void setCategoryFile(int categoryFile) {
        this.categoryFile = categoryFile;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
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

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
