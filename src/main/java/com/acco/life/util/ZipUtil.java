package com.acco.life.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ZipUtil {
    public static void zipWithPassword(java.util.List<java.io.File> sourceFiles, java.io.File targetZipFile, String password) throws java.io.IOException {
        if (sourceFiles == null || sourceFiles.isEmpty()) {
            throw new IllegalArgumentException("sourceFiles must not be empty");
        }
        if (targetZipFile == null) {
            throw new IllegalArgumentException("targetZipFile must not be null");
        }
        if (password == null) {
            throw new IllegalArgumentException("password must not be null");
        }

        try (net.lingala.zip4j.ZipFile zipFile = new net.lingala.zip4j.ZipFile(targetZipFile, password.toCharArray())) {
            net.lingala.zip4j.model.ZipParameters zipParameters = new net.lingala.zip4j.model.ZipParameters();
            zipParameters.setCompressionMethod(net.lingala.zip4j.model.enums.CompressionMethod.DEFLATE);
            zipParameters.setCompressionLevel(net.lingala.zip4j.model.enums.CompressionLevel.NORMAL);
            zipParameters.setEncryptFiles(true);
            zipParameters.setEncryptionMethod(net.lingala.zip4j.model.enums.EncryptionMethod.AES);
            zipParameters.setAesKeyStrength(net.lingala.zip4j.model.enums.AesKeyStrength.KEY_STRENGTH_256);

            for (java.io.File file : sourceFiles) {
                if (!file.exists()) {
                    continue;
                }
                if (file.isDirectory()) {
                    zipFile.addFolder(file, zipParameters);
                } else {
                    zipFile.addFile(file, zipParameters);
                }
            }
        }
    }


    public static List<String> randomDigit() {

        List<String> digits = new ArrayList<>();
        for (int i = 0; i <= 999999; i++) {
            digits.add(String.format("%06d", i));
        }
        Collections.shuffle(digits);
        return digits;
    }

    public static java.util.List<String> unzipWithPassword(File zipFilePath, File destinationDir, String password) throws java.io.IOException {
        if (zipFilePath == null || !zipFilePath.exists()) {
            throw new IllegalArgumentException("zipFilePath must exist");
        }
        if (destinationDir == null) {
            throw new IllegalArgumentException("destinationDir must not be null");
        }
        if (!destinationDir.exists()) {
            if (!destinationDir.mkdirs()) {
                throw new java.io.IOException("Failed to create destination directory: " + destinationDir);
            }
        }
        try (net.lingala.zip4j.ZipFile zipFile = new net.lingala.zip4j.ZipFile(zipFilePath)) {
            if (zipFile.isEncrypted()) {
                if (password == null) {
                    throw new IllegalArgumentException("Password is required for encrypted zip file");
                }
                zipFile.setPassword(password.toCharArray());
            }
            try {
                java.util.List<net.lingala.zip4j.model.FileHeader> headers = zipFile.getFileHeaders();
                zipFile.extractAll(destinationDir.getAbsolutePath());
                java.util.List<String> extractedPaths = new java.util.ArrayList<>();
                for (net.lingala.zip4j.model.FileHeader header : headers) {
                    // Skip directories
                    if (header.isDirectory()) {
                        continue;
                    }
                    java.io.File out = new java.io.File(destinationDir, header.getFileName());
                    extractedPaths.add(out.getAbsolutePath());
                }
                return extractedPaths;
            } catch (net.lingala.zip4j.exception.ZipException e) {
                throw new java.io.IOException("Failed to extract zip: " + e.getMessage(), e);
            }
        }
    }

    public static java.util.List<String> listEntries(java.io.File zipFilePath, String password) throws java.io.IOException {
        if (zipFilePath == null || !zipFilePath.exists()) {
            throw new IllegalArgumentException("zipFilePath must exist");
        }
        try (net.lingala.zip4j.ZipFile zipFile = new net.lingala.zip4j.ZipFile(zipFilePath)) {
            if (zipFile.isEncrypted() && password != null) {
                zipFile.setPassword(password.toCharArray());
            }
            try {
                java.util.List<net.lingala.zip4j.model.FileHeader> headers = zipFile.getFileHeaders();
                java.util.List<String> names = new java.util.ArrayList<>();
                for (net.lingala.zip4j.model.FileHeader header : headers) {
                    names.add(header.getFileName());
                }
                return names;
            } catch (net.lingala.zip4j.exception.ZipException e) {
                throw new java.io.IOException("Failed to list entries: " + e.getMessage(), e);
            }
        }
    }

    public static void main(String[] args) {
        File resourcesDir = new File("src/main/resources");
        File zipFile = new File(resourcesDir, "美团账单(20250101-20250331)——【解压密码请在申请记录页查看】.zip");
        AtomicBoolean flag = new AtomicBoolean(true);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        AtomicInteger count = new AtomicInteger();
        randomDigit().parallelStream().forEach(f -> {
            try {
                if (flag.get()) {
                    count.incrementAndGet();
                    unzipWithPassword(zipFile, resourcesDir, f);
                    System.out.println("Unzipped to: " + resourcesDir.getAbsolutePath() + " with password: " + f + " count: " + count);

                    flag.set(false);
                }
            } catch (Exception e) {
               // log.info("Failed to unzip: " + e.getMessage(), e);
            }
        });
        stopWatch.stop();
        System.out.println("Total time: " + stopWatch.prettyPrint());
    }
}
