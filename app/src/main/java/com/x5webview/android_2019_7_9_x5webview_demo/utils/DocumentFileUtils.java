package com.x5webview.android_2019_7_9_x5webview_demo.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.x5webview.android_2019_7_9_x5webview_demo.App;
import com.x5webview.android_2019_7_9_x5webview_demo.view.DownloadLoading;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * description:
 *
 * @author Db_z
 * date 2019/7/10 16:02
 * @version V1.0
 */
public class DocumentFileUtils {
    private static final int DOWNLOAD_SUCCESS = 1;
    private static final int DOWNLOAD_FAIL = 2;
    private static final int DOWNLOAD_PROGRESS = 3;
    private static final int DOWNLOAD_IMAGE = 4;
    private static final int DOWNLOAD_REPEAT = 5;
    private static final int DOWNLOAD_URL_FAIL = 6;

    private final ThreadPoolExecutor mThreadPool = new ThreadPoolExecutor(5, 5, 50, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2000));
    private boolean isDownLoder;
    private DownLoadThread mDownLoadThread;
    private File mFile;
    private String mFileName;
    private long mTotalSize = -1;
    private Activity mActivity;
    private String loadUrl;
    private OnDisplayFile mDisplayFile;
    private String type[] = {".jpg", ".jpeg", ".png", ".gif"};
    private DownloadLoading mDownloadLoading;

    public static DocumentFileUtils getInstance(Activity activity) {
        return new DocumentFileUtils(activity);
    }

    private DocumentFileUtils(Activity activity) {
        this.mActivity = activity;
        mDownloadLoading = new DownloadLoading(mActivity);
    }

    public DocumentFileUtils setDisplayFile(OnDisplayFile displayFile) {
        mDisplayFile = displayFile;
        return this;
    }

    public interface OnDisplayFile {
        void displayError();

        void displayImage(String imageUrl);

        void displayFile(File file, String filePath);
    }

    /**
     * 是否是图片
     */
    private boolean isImageUrl(String url) {
        String fileName = getFileName(url);
        for (int i = 0; i < type.length; i++) {
            if (fileName.endsWith(type[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * 设置下载的网址
     */
    public DocumentFileUtils setLoadUrl(String url) {
        this.loadUrl = url;
        return this;
    }


    /**
     * 设置文件名字
     */
    public DocumentFileUtils setFileName(String fileName) {
        this.mFileName = fileName;
        return this;
    }

    //控制开始与下载
    public void start() {
        if (isNetworkConnected(mActivity)) {
            isDownLoder = true;
            mDownloadLoading.show();
            if (mDownLoadThread == null) {
                mDownLoadThread = new DownLoadThread();
            }
            mThreadPool.execute(mDownLoadThread);
        } else {
            stop();
            AndroidUtils.showToast(mActivity, "请检查网络连接之后再试！");
        }
    }

    //控制暂停下载
    public void stop() {
        isDownLoder = false;
        if (mDownLoadThread != null) {
            mThreadPool.remove(mDownLoadThread);
            mDownLoadThread = null;
        }
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DOWNLOAD_SUCCESS:
                    mDownloadLoading.dismiss();
                    LogUtils.e("----DOWNLOAD_SUCCESS");
                    mFile = (File) msg.obj;
                    FileUtils.rename(mFile, mFileName);
                    mDisplayFile.displayFile(mFile, mFile.getAbsolutePath().substring(0, mFile.getAbsolutePath().length() - 4));
                    break;
                case DOWNLOAD_FAIL:
                    mDownloadLoading.dismiss();
                    AndroidUtils.showToast(mActivity, "网络连接失败");
                    mDisplayFile.displayError();
                    break;
                case DOWNLOAD_PROGRESS:
                    long progress = (long) msg.obj;
                    break;
                case DOWNLOAD_IMAGE:
                    mDownloadLoading.dismiss();
                    mDisplayFile.displayImage(loadUrl);
                    break;
                case DOWNLOAD_REPEAT:
                    LogUtils.e("----DOWNLOAD_REPEAT");
                    mDownloadLoading.dismiss();
                    mFile = (File) msg.obj;
                    mDisplayFile.displayFile(mFile, mFile.getAbsolutePath());
                    break;
                case DOWNLOAD_URL_FAIL:
                    mDownloadLoading.dismiss();
                    AndroidUtils.showToast(mActivity, "文件地址不存在");
                    mDisplayFile.displayError();
                    break;
            }
        }
    };

    public static String getFileName(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    private int getContentLength() {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(loadUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            if (connection.getResponseCode() == 200) {
                return connection.getContentLength();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try { //释放资源
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    /**
     * 从服务器下载最新更新文件
     *
     * @return
     * @throws Exception
     */
    private void downloadFile() {
        if (StringUtils.isEmpty(loadUrl)) {
            mHandler.sendEmptyMessage(DOWNLOAD_URL_FAIL);
            return;
        }
        // 是图片  直接加载
        if (isImageUrl(loadUrl)) {
            mHandler.sendEmptyMessage(DOWNLOAD_IMAGE);
            return;
        }
        List<File> files = FileUtils.listFilesInDir(App.file);
        if (files != null && files.size() > 0) {
            for (File f : files) {
                if (f.isFile() && f.getName().equals(mFileName)) {
                    mHandler.sendMessage(mHandler.obtainMessage(DOWNLOAD_REPEAT, f));
                    return;
                }
            }
        }
        mTotalSize = (long) getContentLength();
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        HttpURLConnection connection;
        try {
            URL url = new URL(loadUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            mDownloadLoading.getmProgressBar().setTotalProgress(mTotalSize);
            // 如果以地址最后的文件命名，那么删除的时候就取这个名字删除
            File file = new File(App.file, mFileName + ".tmp");
            if (!FileUtils.isFileExists(file)) {//判断文件目录是否存在  
                file.createNewFile(); // 创建文件
            }

            outputStream = new FileOutputStream(file);
            inputStream = connection.getInputStream();
            byte[] buffer = new byte[1024];
            int len;
            long downSize = 0;
            while ((len = inputStream.read(buffer)) != -1 && isDownLoder) {
                outputStream.write(buffer, 0, len);
                downSize += len;
                // 获取当前下载量
                mDownloadLoading.getmProgressBar().setProgress(downSize);
//                mHandler.sendMessage(mHandler.obtainMessage(DOWNLOAD_PROGRESS, downSize));
            }
            stop();
            mHandler.sendMessage(mHandler.obtainMessage(DOWNLOAD_SUCCESS, file));
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.e("----e = " + e.getMessage());
            stop();
            mHandler.sendMessage(mHandler.obtainMessage(DOWNLOAD_FAIL, e.getMessage()));
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public File getmFile() {
        return mFile;
    }

    public void deleteFile() {
        if (mFile != null) {
            mFile.delete();
        }
    }

    class DownLoadThread extends Thread {
        @Override
        public void run() {
            super.run();
            downloadFile();
        }
    }


    /**
     * 判断是否有网络连接
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }
}