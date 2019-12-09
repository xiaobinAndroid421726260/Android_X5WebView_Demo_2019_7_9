package com.x5webview.android_2019_7_9_x5webview_demo.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import com.x5webview.android_2019_7_9_x5webview_demo.R;

/**
 * description:
 *
 * @author Db_z
 * date 2019/9/29 10:48
 * @version V1.0
 */
public class DownloadLoading extends AlertDialog {

    private CirCleBarView mProgressBar;
    private Context mContext;

    public DownloadLoading(Context context) {
        super(context, R.style.dialog);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_download);
        setCancelable(false);
        mProgressBar = findViewById(R.id.circleProgress);
    }

    public CirCleBarView getmProgressBar() {
        return mProgressBar;
    }

    @Override
    public void dismiss() {
        if (mContext != null && mContext instanceof Activity && !((Activity) mContext).isDestroyed() && !((Activity) mContext).isFinishing()) {
            super.dismiss();
        }
    }

    @Override
    public void show() {
        if (mContext != null && mContext instanceof Activity && !((Activity) mContext).isDestroyed() && !((Activity) mContext).isFinishing()) {
            super.show();
        }
    }
}