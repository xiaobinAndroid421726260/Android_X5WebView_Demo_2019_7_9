package com.x5webview.android_2019_7_9_x5webview_demo;

import android.Manifest;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;

import java.io.File;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        requestPermissions();
    }

    public void initView() {
        findViewById(R.id.btn_open_pdf_with_tbs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileWithTbs(getFilePath("TestPDF.pdf"));
                AppDownloadUtils.getInstance(MainActivity.this)
                        .setDownUrl("http://res01.zyxschool.com/images/teacherfiles/201907/2019/1562656185_287559.pdf")
                        .setCallBack(new AppDownloadUtils.CallBack() {
                            @Override
                            public void onClick(File file, String filePath) {
                                openFileWithTbs(filePath);
                            }
                        })
                        .start();
            }
        });

        findViewById(R.id.btn_open_doc_with_tbs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileWithTbs(getFilePath("TestDoc.doc"));
                AppDownloadUtils.getInstance(MainActivity.this)
                        .setDownUrl("http://res01.zyxschool.com/images/teacherfiles/201906/2019/1561689034_870775.docx")
                        .setCallBack(new AppDownloadUtils.CallBack() {
                            @Override
                            public void onClick(File file, String filePath) {
                                openFileWithTbs(filePath);
                            }
                        })
                        .start();
            }
        });
        findViewById(R.id.btn_open_ppt_with_tbs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppDownloadUtils.getInstance(MainActivity.this)
                        .setDownUrl("http://res01.zyxschool.com/images/teacherfiles/201906/2019/1561689073_341560.jpg")
                        .setCallBack(new AppDownloadUtils.CallBack() {
                            @Override
                            public void onClick(File file, String filePath) {
                                openFileWithTbs(file.getAbsolutePath());
                            }
                        })
                        .start();
            }
        });
        findViewById(R.id.btn_open_excel_with_tbs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppDownloadUtils.getInstance(MainActivity.this)
                        .setDownUrl("http://res01.zyxschool.com/images/teacherfiles/201907/2019/1562656228_721347.xls")
                        .setCallBack(new AppDownloadUtils.CallBack() {
                            @Override
                            public void onClick(File file, String filePath) {
                                openFileWithTbs(file.getAbsolutePath());
                            }
                        })
                        .start();
            }
        });
    }

    private void requestPermissions() {
        PermissionManagerUtils.getsInstance(this).requestPermissions(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManagerUtils.onRequestPermissionsResult(requestCode, permissions, grantResults, new PermissionManagerUtils.OnPermissionListener() {
            @Override
            public void onRequestSuccess(String[] permissions) {
                LogUtils.e("---onRequestSuccess");
            }

            @Override
            public void onRequestFail(String[] permissions) {
                LogUtils.e("---onRequestFail");
            }
        });
    }

    private String getFilePath(String fileName) {
        return new File(App.FILE_DIR + fileName).getAbsolutePath();
    }

    private void openFileWithTbs(String filePath) {
        LogUtils.e("Open File: " + filePath);
        TBSFileViewActivity.viewFile(this, filePath);
    }
}