package com.x5webview.android_2019_7_9_x5webview_demo;

import android.Manifest;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.x5webview.android_2019_7_9_x5webview_demo.utils.AndroidUtils;
import com.x5webview.android_2019_7_9_x5webview_demo.utils.DocumentFileUtils;
import com.x5webview.android_2019_7_9_x5webview_demo.utils.PermissionManagerUtils;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    // 让单位后台人员提供下载 测试地址...
    String url_pdf = "";
    String url_docx = "";
    String url_jpg = "";
    String url_xls = "";

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
                // 本地打开
                openFileWithTbs(getFilePath("TestPDF.pdf"));
                // 下载到本地打开
//                DocumentFileUtils.getInstance(MainActivity.this)
//                        .setDisplayFile(new DocumentFileUtils.OnDisplayFile() {
//                            @Override
//                            public void displayError() {
//
//                            }
//
//                            @Override
//                            public void displayImage() {
//                                // 如果是图片，处理方法
//                            }
//
//                            @Override
//                            public void displayFile(File file, String filePath) {
//                                openFileWithTbs(filePath);
//                            }
//                        })
//                        .setFileName(DocumentFileUtils.getFileName(url_pdf))
//                        .setLoadUrl(url_pdf)
//                        .start();
            }
        });

        findViewById(R.id.btn_open_doc_with_tbs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileWithTbs(getFilePath("TestDoc.doc"));

//                DocumentFileUtils.getInstance(MainActivity.this)
//                        .setDisplayFile(new DocumentFileUtils.OnDisplayFile() {
//                            @Override
//                            public void displayError() {
//
//                            }
//
//                            @Override
//                            public void displayImage() {
//                                // 如果是图片，处理方法
//                            }
//
//                            @Override
//                            public void displayFile(File file, String filePath) {
//                                openFileWithTbs(filePath);
//                            }
//                        })
//                        .setFileName(DocumentFileUtils.getFileName(url_docx))
//                        .setLoadUrl(url_docx)
//                        .start();
            }
        });
        findViewById(R.id.btn_open_ppt_with_tbs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileWithTbs(getFilePath("TestPPT.ppt"));

//                DocumentFileUtils.getInstance(MainActivity.this)
//                        .setDisplayFile(new DocumentFileUtils.OnDisplayFile() {
//                            @Override
//                            public void displayError() {
//
//                            }
//
//                            @Override
//                            public void displayImage() {
//                                // 如果是图片，处理方法
//                                AndroidUtils.showToast(MainActivity.this, "这个是图片：" + url_jpg);
//                            }
//
//                            @Override
//                            public void displayFile(File file, String filePath) {
//                                openFileWithTbs(filePath);
//                            }
//                        })
//                        .setFileName(DocumentFileUtils.getFileName(url_jpg))
//                        .setLoadUrl(url_jpg)
//                        .start();
            }
        });
        findViewById(R.id.btn_open_excel_with_tbs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileWithTbs(getFilePath("TestExcel.xls"));
//                DocumentFileUtils.getInstance(MainActivity.this)
//                        .setDisplayFile(new DocumentFileUtils.OnDisplayFile() {
//                            @Override
//                            public void displayError() {
//
//                            }
//
//                            @Override
//                            public void displayImage() {
//                                // 如果是图片，处理方法
//                            }
//
//                            @Override
//                            public void displayFile(File file, String filePath) {
//                                openFileWithTbs(filePath);
//                            }
//                        })
//                        .setFileName(DocumentFileUtils.getFileName(url_xls))
//                        .setLoadUrl(url_xls)
//                        .start();
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