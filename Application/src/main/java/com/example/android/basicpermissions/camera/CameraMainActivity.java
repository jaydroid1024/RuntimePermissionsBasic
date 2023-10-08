

package com.example.android.basicpermissions.camera;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.android.basicpermissions.R;
import com.google.android.material.snackbar.Snackbar;

public class CameraMainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int PERMISSION_REQUEST_CODE = 0;

    private static final int PERMISSION_GO_SETTING = 1;

    private static final String TAG = "Permission";

    private View mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity_main);
        initIntent();
        initView();
    }

    private void initIntent() {
        String group = getIntent().getStringExtra("group");
        Log.d(TAG, "initIntent,group=" + group);
    }

    private void initView() {
        mLayout = findViewById(R.id.main_layout);
        // Register a listener for the 'Show Camera Preview' button.
        findViewById(R.id.button_open_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCameraPreview();
            }
        });

        findViewById(R.id.button_open_camera_without_perm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCamera();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "onActivityResult,requestCode=" + requestCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PERMISSION_GO_SETTING) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                // Permission is already available, start camera preview
                // 权限已经可用，开始相机预览
//                Snackbar.make(mLayout, R.string.camera_permission_available, Snackbar.LENGTH_SHORT).show();
                startCamera();
            } else {
                onCameraDenied();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult,requestCode=" + requestCode);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (verifyPermissions(grantResults)) {
                startCamera();
            } else {
                boolean isPermissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA);
                if (!isPermissionRationale) {
                    onCameraNeverAskAgain();
                } else {
                    onCameraDenied();
                }
            }
        }
    }

    private void onCameraDenied() {
        // Permission request was denied.
        Snackbar.make(mLayout, R.string.camera_permission_denied, Snackbar.LENGTH_SHORT).show();
    }

    private void onCameraNeverAskAgain() {
//        Snackbar.make(mLayout, R.string.camera_permission_denied_never_ask_again, Snackbar.LENGTH_SHORT).show();
        showGoSettingDialog();
    }


    /**
     * Checks all given permissions have been granted.
     *
     * @param grantResults results
     * @return returns true if all permissions have been granted.
     */
    public static boolean verifyPermissions(int... grantResults) {
        if (grantResults.length == 0) {
            return false;
        }
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void showCameraPreview() {
        // Check if the Camera permission has been granted
        // 检查相机权限是否已被授予
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // Permission is already available, start camera preview
            // 权限已经可用，开始相机预览
//            Snackbar.make(mLayout, R.string.camera_permission_available, Snackbar.LENGTH_SHORT).show();
            startCamera();
        } else {
            // Permission is missing and must be requested.
            requestCameraPermission();
        }
    }

    /**
     * Requests the {@link android.Manifest.permission#CAMERA} permission.
     * If an additional rationale should be displayed, the user has to launch the request from
     * a SnackBar that includes additional information.
     */
    private void requestCameraPermission() {
        boolean isPermissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA);
        // Permission has not been granted and must be requested.
        // 尚未授予许可，必须申请。
        if (isPermissionRationale) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with cda button to request the missing permission.
            // 如果未授予权限，则向用户提供额外的理由，并且用户将从使用该权限的其他上下文中受益。显示带有 cda 按钮的 SnackBar 以请求缺少的权限。
//            Snackbar.make(mLayout, R.string.camera_access_required, Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok,
//                    new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            // Request the permission
//                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
//                        }
//                    }).show();

            showRationaleDialog();

        } else {
//            Snackbar.make(mLayout, R.string.camera_unavailable, Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            // 请求许可。结果将在 onRequestPermissionResult() 中接收。
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
        }
    }

    private void startCamera() {
        Intent intent = new Intent(this, CameraPreviewActivity.class);
        startActivity(intent);
    }

    private void showRationaleDialog() {
        new AlertDialog.Builder(this)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(CameraMainActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Snackbar.make(mLayout, R.string.camera_permission_denied, Snackbar.LENGTH_SHORT).show();
                    }
                })
                .setCancelable(false)
                .setMessage("需要开启权限才能使用该功能")
                .show();
    }

    private void showGoSettingDialog() {
        new AlertDialog.Builder(this)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", CameraMainActivity.this.getPackageName(), null);
                        intent.setData(uri);
                        CameraMainActivity.this.startActivityForResult(intent, PERMISSION_GO_SETTING);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Snackbar.make(mLayout, R.string.camera_permission_denied, Snackbar.LENGTH_SHORT).show();
                    }
                })
                .setCancelable(false)
                .setMessage("去设置里打开所需权限才能使用")
                .show();
    }

}
