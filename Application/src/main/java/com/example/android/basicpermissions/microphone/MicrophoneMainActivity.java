

package com.example.android.basicpermissions.microphone;

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
import com.example.android.basicpermissions.microphone.audio.AudioRecordTestActivity;
import com.google.android.material.snackbar.Snackbar;

public class MicrophoneMainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int PERMISSION_REQUEST_CODE = 0;

    private static final int PERMISSION_GO_SETTING = 1;

    private static final String TAG = "Permission";

    private View mLayout;

    String[] permissionArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.microphone_activity_main);
        initIntent();
        initView();
    }

    private void initIntent() {
        String group = getIntent().getStringExtra("group");
        Log.d(TAG, "initIntent,group=" + group);
        //获取所有组内权限
        permissionArray = new String[]{Manifest.permission.RECORD_AUDIO};
    }

    private void initView() {
        mLayout = findViewById(R.id.main_layout);
        // Register a listener for the 'Show Camera Preview' button.
        findViewById(R.id.button_open_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndStart();
            }
        });

        findViewById(R.id.button_start_without_perm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "onActivityResult,requestCode=" + requestCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PERMISSION_GO_SETTING) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                // 权限已经可用，开始相机预览
                start();
            } else {
                onDenied();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult,requestCode=" + requestCode);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (verifyPermissions(grantResults)) {
                start();
            } else {
                boolean isPermissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO);
                if (!isPermissionRationale) {
                    onNeverAskAgain();
                } else {
                    onDenied();
                }
            }
        }
    }

    private void onDenied() {
        // Permission request was denied.
        Snackbar.make(mLayout, R.string.camera_permission_denied, Snackbar.LENGTH_SHORT).show();
    }

    private void onNeverAskAgain() {
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

    private void checkAndStart() {
        // 检查权限是否已被授予
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            // 权限已经可用
            start();
        } else {
            // Permission is missing and must be requested.
            requestPermission();
        }
    }

    /**
     * Requests the {@link Manifest.permission#CAMERA} permission.
     * If an additional rationale should be displayed, the user has to launch the request from
     * a SnackBar that includes additional information.
     */
    private void requestPermission() {
        boolean isPermissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO);
        // 尚未授予许可，必须申请。
        if (isPermissionRationale) {
            showRationaleDialog();
        } else {
            // 请求许可。结果将在 onRequestPermissionResult() 中接收。
            ActivityCompat.requestPermissions(this, permissionArray, PERMISSION_REQUEST_CODE);
        }
    }

    private void start() {
        Intent intent = new Intent(this, AudioRecordTestActivity.class);
        startActivity(intent);
    }

    private void showRationaleDialog() {
        new AlertDialog.Builder(this)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MicrophoneMainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST_CODE);
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
                        Uri uri = Uri.fromParts("package", MicrophoneMainActivity.this.getPackageName(), null);
                        intent.setData(uri);
                        MicrophoneMainActivity.this.startActivityForResult(intent, PERMISSION_GO_SETTING);
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
