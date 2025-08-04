package com.qsystem.clientlib;

import java.io.*;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * ZIP工具类
 */
public class ZipUtil {
    
    public static void unZip(String localPath) {
        try {
            File folder = new File(localPath);
            File[] zipFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".zip"));
            if (zipFiles != null) {
                for (File zipFile : zipFiles) {
                    try {
                        extractZipFile(zipFile.getAbsolutePath(), localPath);
                    } catch (Exception e) {
                        // continue
                    }
                }
            }
        } catch (Exception e) {
            // ignore
        }
    }
    
    public static void unZipSingleFile(String filePath) {
        try {
            File file = new File(filePath);
            String extractPath = file.getParent();
            if (extractPath != null) {
                extractZipFile(filePath, extractPath);
            }
        } catch (Exception e) {
            // ignore
        }
    }
    
    private static void extractZipFile(String zipFilePath, String destDir) throws IOException {
        byte[] buffer = new byte[1024];
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                File newFile = new File(destDir, zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    // 创建父目录
                    new File(newFile.getParent()).mkdirs();
                    // 写入文件
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zipEntry = zis.getNextEntry();
            }
        }
    }
} 