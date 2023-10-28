package com.sunmi.factorytest.utils;

import android.util.Log;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;
import java.util.Scanner;

/**
 * 文件操作工具类，用于创建随机文本文件、设置文件夹权限和解析文本和YAML文件。
 */
public class FileUtils {

    private static FileUtils instance = new FileUtils();

    private FileUtils() {

    }

    public static FileUtils getInstance() {
        return instance;
    }

    private static final String TAG = "FileUtils";

    /**
     * 创建并写入随机文本文件。
     *
     * @param directoryPath 要创建文件的目录路径
     */
    public void createAndWriteRandomTextFile(String directoryPath) {
        Random random = new Random();

        for (int i = 0; i < 3; i++) {
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            int fileNumber = i;
            File file;

            do {
                file = new File(directory, fileNumber + ".txt");
                fileNumber++;
            } while (file.exists());

            try {
                try (FileWriter writer = new FileWriter(file)) {
                    int randomNumber = random.nextInt(100);
                    writer.write(String.valueOf(randomNumber));
                }

            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Failed to create and write to file: " + e.getMessage());
            }
            setDirectoryPermissions(directory);
        }
    }

    /**
     * 递归设置文件夹及其子目录的权限。
     *
     * @param directory 要设置权限的目录
     */
    public static void setDirectoryPermissions(File directory) {
        if (directory.exists() && directory.isDirectory()) {
            // 设置文件夹权限为可读、可写和可执行
            directory.setReadable(true, false);
            directory.setWritable(true, false);
            directory.setExecutable(true, false);

            File[] files = directory.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        // 递归设置子目录的权限
                        setDirectoryPermissions(file);
                    }

                    // 设置文件的权限为可读、可写和可执行
                    file.setReadable(true, false);
                    file.setWritable(true, false);
                    file.setExecutable(true, false);
                }
            }
        }
    }

    /**
     * 解析指定目录下的文本和YAML文件，返回文件路径和文件内容的列表。
     *
     * @param directoryPath 指定目录的路径
     * @return 包含文件路径和文件内容的列表
     */
    public List<Map<String, Object>> parseTxtAndYamlFilesInDirectory(String directoryPath) {
        List<Map<String, Object>> textFilesData = new ArrayList<>();
        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            scanDirectoryForTextAndYamlFiles(directory, textFilesData);
        }
        return textFilesData;
    }

    private void scanDirectoryForTextAndYamlFiles(File directory, List<Map<String, Object>> textFilesData) {
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".txt") || name.endsWith(".yaml"));

        if (files != null) {
            for (File file : files) {
                Map<String, Object> fileData = new HashMap<>();
                fileData.put("file_path", file.getAbsolutePath());

                try {
                    String fileContent = readFileContent(file);
                    fileData.put("file_content", fileContent);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                textFilesData.add(fileData);
            }
        }

        File[] subdirectories = directory.listFiles(File::isDirectory);

        if (subdirectories != null) {
            for (File subdirectory : subdirectories) {
                scanDirectoryForTextAndYamlFiles(subdirectory, textFilesData);
            }
        }
    }

    private String readFileContent(File file) throws IOException {
        StringBuilder content = new StringBuilder();

        try (FileInputStream inputStream = new FileInputStream(file);
             Scanner scanner = new Scanner(inputStream)) {

            while (scanner.hasNextLine()) {
                content.append(scanner.nextLine()).append("\n");
            }
        }

        if (file.getName().endsWith(".yaml")) {
            try {
                Yaml yaml = new Yaml();
                Map<String, Object> yamlData = yaml.load(content.toString());
                return yamlData.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return content.toString();
    }
}
