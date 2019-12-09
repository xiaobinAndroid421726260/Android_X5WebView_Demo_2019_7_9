package com.x5webview.android_2019_7_9_x5webview_demo.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionManagerUtils {

    private static final int REQUEST_CODE = 0x100;
    private static PermissionManagerUtils sInstance;
    private List<String> mPermissionList;
    private Context mContext;

    public PermissionManagerUtils(Context context) {
        mContext = context.getApplicationContext();
        mPermissionList = new ArrayList<>();
    }

    public static PermissionManagerUtils getsInstance(Context context) {
        if (sInstance == null) {
            synchronized (PermissionManagerUtils.class) {
                if (sInstance == null) {
                    sInstance = new PermissionManagerUtils(context);
                }
            }
        }
        return sInstance;
    }

    /**
     * 权限接口回调
     */
    public interface OnPermissionListener {
        void onRequestSuccess(String[] permissions); // 权限成功

        void onRequestFail(String[] permissions); // 权限被拒绝
    }


    /**
     * 请求多个权限
     */
    public void requestPermissions(@NonNull Activity activity, String... permissions) {
        for (String permission : permissions) {
            if (!isGranted(permission) && !isRevoked(permission)) {
                mPermissionList.add(permission);
            }
        }

        if (mPermissionList.size() == 0) return;
        if (mPermissionList.size() > 0) {
            String[] permission = new String[mPermissionList.size()];
            executeRequestPermissions(activity, mPermissionList.toArray(permission));
        }
    }

    /**
     * 请求一个权限
     */
    public void requestPermissions(@NonNull Activity activity, String permission) {
        if (!isGranted(permission) && !isRevoked(permission)) {
            executeRequestPermissions(activity, permission);
        }
    }

    /**
     * 当用户拒绝掉权限时，显示对话框
     */
    private void shouldShowRequestPermissionRationale(@NonNull Activity activity, String permission) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            shouldShowRationale(activity, permission);
        } else {
            executeRequestPermissions(activity, permission);
        }
    }


    /**
     * 请求权限的结果, 在activity中的onRequestPermissionsResult()中调用
     */
    public static void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults,
                                                  OnPermissionListener listener) {
        if (REQUEST_CODE == requestCode && grantResults != null && grantResults.length > 0) {
            boolean isGranted = false;
            if (listener != null) {
                // 请求一个权限和请求多个权限都可以用这个方法，不受影响
                // 循环判断请求的权限是否被授权 (如果有一个权限被拒绝  那么就请求失败)
                for (int i = 0; i < grantResults.length; i++) {
                    if (isGrantedPermissions(grantResults[i])) {
                        isGranted = true;
                    } else {
                        isGranted = false;
                    }
                }
                if (isGranted) {
                    listener.onRequestSuccess(permissions);
                } else {
                    listener.onRequestFail(permissions);
                }
            }
        }
    }


    /**
     * 执行请求权限
     */
    private void executeRequestPermissions(Activity activity, String... permission) {
        if (activity == null) return;
        if (isM()) {
            ActivityCompat.requestPermissions(activity, permission, REQUEST_CODE);
        }
    }


    /**
     * 检查权限是否被授权
     */
    private boolean isGranted(@NonNull String permission) {
        return isM() && ContextCompat.checkSelfPermission(mContext, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 判断是不是在包中申明
     */
    @TargetApi(Build.VERSION_CODES.M)
    private boolean isRevoked(@NonNull String permission) {
        return isM() && mContext.getPackageManager().isPermissionRevokedByPolicy(permission, mContext.getPackageName());
    }


    /**
     * 判断所请求的权限是否已经被授权
     */
    private static boolean isGrantedPermissions(int grantResult) {
        if (grantResult == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }


    /**
     * 判断是不是M及以上版本
     */
    private boolean isM() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 当用户拒绝权限时的对话框
     */
    private void shouldShowRationale(final Activity activity, final String permission) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);

        dialog.setTitle("此页面功能需要该权限！")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        executeRequestPermissions(activity, permission);
                        dialog.dismiss();
                    }
                }).setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }


    /**
     * 拒绝权限对话框
     */
    public void showDialog(final Activity activity, String message, final String... permissions) {
//        if (StringUtils.isEmpty(message)){
//            message = "您已拒绝了该权限，部分功能将不能实现，请到权限管理中去设置！";
//        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setMessage(message)
                .setTitle("提示")
                .setPositiveButton("继续请求", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        executeRequestPermissions(activity, permissions);
                        dialog.dismiss();
                    }
                }).setNegativeButton("去设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                activity.startActivity(intent);
                dialog.dismiss();
            }
        }).create().show();
    }

}
