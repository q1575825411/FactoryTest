package com.sunmi.factorytest;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.sunmi.factorytest.databinding.ActivityMainBinding;
import com.sunmi.factorytest.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author sm2886
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "tianyou";

    private ActivityMainBinding binding;
    private final String rootPath = "/backup/camera/";
    private final String rootPath1 = "/backup/camera/alignment/";
    private final String rootPath2 = "/backup/camera/calibration/";
    private List<String> fileList = new ArrayList<String>();

    private FileUtils fileUtils;

    private int size = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 为按钮添加点击事件
        binding.btnRead.setOnClickListener(this::onReadButtonClick);
        binding.btnWrite.setOnClickListener(this::onWriteButtonClick);
        fileUtils = FileUtils.getInstance();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        size = 0;
    }

    private void onReadButtonClick(View view) {

        String selectedRootPath = binding.checkBox.isChecked() ? rootPath2 : rootPath1;
        List<Map<String, Object>> textFilesData = fileUtils.parseTxtAndYamlFilesInDirectory(selectedRootPath);
        StringBuilder resultText = new StringBuilder();

        for (Map<String, Object> fileData : textFilesData) {
            String filePath = fileData.get("file_path").toString();
            String fileContent = fileData.get("file_content").toString();

            resultText.append("File Path: ").append(filePath).append("\n");
            resultText.append("File Content: ").append(fileContent).append("\n\n");
        }

        binding.tvResult.setText(resultText.toString());
    }

    private void onWriteButtonClick(View view) {

        String directoryPath = binding.checkBox.isChecked() ? rootPath2 : rootPath1;
        do {
            fileUtils.createAndWriteRandomTextFile(directoryPath + size + "/");
            size++;
        } while (size % 3 == 0);
    }

}

