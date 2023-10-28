package com.sunmi.factorytest.utils;

import android.util.Log;

import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

/**
 * @author sm2886
 */
public class FileUtils {

    private static FileUtils instance = new FileUtils();

    private FileUtils() {

    }

    public static FileUtils getInstance() {
        return instance;
    }

    private static final String TAG = "FileUtils";

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

    public List<String> getAllTextFileContentsInSubdirectories(String directoryPath) {
        List<String> fileContents = new ArrayList<>();
        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            File[] subdirectories = directory.listFiles(File::isDirectory);

            if (subdirectories != null) {
                for (File subdirectory : subdirectories) {
                    // 递归读取子目录下的文本文件内容
                    List<String> subdirectoryContents = Collections.singletonList(subdirectory.getPath() + getAllTextFileContentsInDirectory(subdirectory.getPath()));
                    fileContents.addAll(subdirectoryContents);
                }
            }
        }
        return fileContents;
    }

    private List<String> getAllTextFileContentsInDirectory(String directoryPath) {
        List<String> fileContents = new ArrayList<>();
        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles((dir, name) -> {
                File file = new File(dir, name);
                return file.isFile() && name.toLowerCase().endsWith(".txt");
            });

            if (files != null) {
                for (File file : files) {
                    try {
                        String content = readTextFromFile(file);
                        fileContents.add(content);
                    } catch (IOException e) {
                        e.printStackTrace();
                        // 处理读取文件时的异常
                    }
                }
            }
        }
        return fileContents;
    }

    private String readTextFromFile(File file) throws IOException {
        StringBuilder content = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line).append('\n');
                Log.e(TAG, "tianyou: " + line);
            }
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return content.toString();
    }


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


    public List<Map<String, Object>> parseTxtAndYamlFilesInDirectory(String directoryPath) {
        List<Map<String, Object>> textFilesData = new ArrayList<>();
        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            scanDirectoryForTextAndYamlFiles(directory, textFilesData);
        }
        return textFilesData;
    }

    private static void scanDirectoryForTextAndYamlFiles(File directory, List<Map<String, Object>> textFilesData) {
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

        File[] subdirectories = directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory();
            }
        });

        if (subdirectories != null) {
            for (File subdirectory : subdirectories) {
                scanDirectoryForTextAndYamlFiles(subdirectory, textFilesData);
            }
        }
    }

    private static String readFileContent(File file) throws IOException {
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
