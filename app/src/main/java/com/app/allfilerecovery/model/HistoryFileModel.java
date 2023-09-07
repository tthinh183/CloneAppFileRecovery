package com.app.allfilerecovery.model;

import com.app.allfilerecovery.utils.FileUtils;

import java.io.File;

public class HistoryFileModel {

    private String path;
    private String size;
    private long lastModified;
    private String folder;
    private String typeFile;

    public HistoryFileModel() {
    }

    public HistoryFileModel(String path, String typeFile, String folder) {
        this.path = path;
        this.typeFile = typeFile;
        this.folder = folder;

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
