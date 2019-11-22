package com.x5webview.android_2019_7_9_x5webview_demo;

import android.app.Application;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.tencent.smtt.sdk.QbSdk;

import java.io.File;
import java.io.IOException;

/**
 * description:
 *
 * @author Db_z
 * date 2019/7/10 10:24
 * @version V1.0
 */
public class App extends Application {

    public static String FILE_DIR = "/sdcard/Downloads/test/";
    public static final String TAG = "TBSInit";

    public static final String SD_FilePath = Environment.getExternalStorageDirectory() + "/file/";
    public static String file_path;

    @Override
    public void onCreate() {
        super.onCreate();
        initX5Web();
        FILE_DIR = new File(getFilesDir(), "test").getAbsolutePath() + File.separator;
        file_path = new File(SD_FilePath).getAbsolutePath();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                FileUtils.copyAssetsDir(this, "test", FILE_DIR);
            }
        } catch (
                IOException e) {
            e.printStackTrace();
        }

    }

    private void initX5Web() {
        QbSdk.initX5Environment(getApplicationContext(), new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
                Log.e(TAG, "onCoreInitFinished");
            }

            @Override
            public void onViewInitFinished(boolean initResult) {
                Log.e(TAG, "onViewInitFinished" + initResult);
            }
        });
    }
}
