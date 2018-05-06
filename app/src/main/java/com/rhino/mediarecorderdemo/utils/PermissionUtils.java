package com.rhino.mediarecorderdemo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;


/**
 * @author LuoLin
 * @since Create on 2018/5/5.
 **/
public class PermissionUtils {

    private static final String PACKAGE_URL_SCHEME = "package:";

    /**
     * 请求权限
     *
     * @param activity    Activity
     * @param permissions 权限数组
     * @param requestCode 请求码
     */
    public static void requestPermissions(final @NonNull Activity activity,
            final @NonNull String[] permissions, final @IntRange(from = 0) int requestCode) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }

    /**
     * 检查是否有该权限
     *
     * @param context     上下文
     * @param permissions 权限
     * @return true 已拥有该权限
     */
    public static boolean checkPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (checkPermission(context, permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查是否有该权限
     *
     * @param context    上下文
     * @param permission 权限
     * @return true 已拥有该权限
     */
    public static boolean checkPermission(Context context, String permission) {
        return PackageManager.PERMISSION_DENIED == ContextCompat.checkSelfPermission(context, permission);
    }


    /**
     * 检查权限请求结果
     *
     * @param grantResults 权限请求结果
     * @return true 已请求权限都被允许
     */
    public static boolean checkPermissionsGrantResults(@NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (PackageManager.PERMISSION_DENIED == grantResult) {
                return false;
            }
        }
        return true;
    }

    /**
     * 显示缺失权限对话框提示
     *
     * @param activity              Activity
     * @param cancelable            是否可以取消对话框
     * @param titleText             标题
     * @param message               描述
     * @param positiveText          确认按键文字描述
     * @param positiveClickListener 确认按键点击事件
     * @param negativeText          取消按键文字描述
     * @param negativeClickListener 取消按键点击事件
     */
    public static void showMissingPermissionDialog(Activity activity,
            boolean cancelable, String titleText, String message,
            String positiveText, DialogInterface.OnClickListener positiveClickListener,
            String negativeText, DialogInterface.OnClickListener negativeClickListener) {
        new AlertDialog.Builder(activity)
                .setCancelable(cancelable)
                .setTitle(titleText)
                .setMessage(message)
                .setNegativeButton(positiveText, positiveClickListener)
                .setPositiveButton(negativeText, negativeClickListener)
                .show();
    }

    /**
     * 进入app设置界面
     *
     * @param activity Activity
     */
    public static void gotToAppSettings(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse(PACKAGE_URL_SCHEME + activity.getPackageName()));
        activity.startActivity(intent);
    }

}
