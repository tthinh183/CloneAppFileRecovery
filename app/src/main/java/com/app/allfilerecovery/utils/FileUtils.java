package com.app.allfilerecovery.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtils {

    public static void copy(File source, File destination) throws IOException {

        FileChannel sourceCh = null;
        FileChannel destinationCh = null;

        sourceCh = new FileInputStream(source).getChannel();
        destinationCh = new FileOutputStream(destination).getChannel();

        sourceCh.transferTo(0, sourceCh.size(), destinationCh);

        if (sourceCh != null)
            sourceCh.close();

        if (destinationCh != null)
            destinationCh.close();

    }

    public static String getFileSizeMegaBytes(File file) {
        return (double) file.length() / (1024 * 1024) + " mb";
    }

    public static String getFileSizeKiloBytes(File file) {
        return (int) file.length() / 1024 + "  kb";
    }

    public static double getFileSizeMB(File file) {
        return (double) file.length() / (1024 * 1024);
    }

    public static String getLastModified(File file) {

        long fileDate = file.lastModified();
        String fileName = file.getName();
        String fileSize = FileUtils.getFileSizeKiloBytes(file);

        Date fileData = new Date(fileDate);
        SimpleDateFormat simpleDate = new SimpleDateFormat("dd MMM yyyy");
        String stringDate = simpleDate.format(fileData);
        return stringDate;
    }

    public static String getFileSizeBytes(File file) {
        return file.length() + " bytes";
    }

}
