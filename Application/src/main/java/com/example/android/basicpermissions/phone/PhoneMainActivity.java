package com.example.android.basicpermissions.phone;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.android.basicpermissions.R;
import com.google.android.material.snackbar.Snackbar;

public class PhoneMainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int PERMISSION_REQUEST_CALL_CODE = 0;
    private static final int PERMISSION_REQUEST_NET_CODE = 1;

    private static final int PERMISSION_GO_SETTING = 1;

    private static final String TAG = "Permission";

    private View mLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_activity_main);
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
        findViewById(R.id.button_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndCall();
            }
        });
        findViewById(R.id.button_call_without_perm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callPhone();
            }
        });

        findViewById(R.id.button_state).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndState();
            }
        });

        findViewById(R.id.button_state_without_perm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getState();
            }
        });
    }


    private void checkAndCall() {
        // 检查权限是否已被授予
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            // 权限已经可用
            callPhone();
        } else {
            // Permission is missing and must be requested.
            requestCallPermission();
        }
    }

    private void requestCallPermission() {
        boolean isPermissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE);
        // 尚未授予许可，必须申请。
        if (isPermissionRationale) {
            showPhoneRationaleDialog();
        } else {
            // 请求许可。结果将在 onRequestPermissionResult() 中接收。
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_CALL_CODE);
        }
    }

    private void checkAndState() {
        // 检查权限是否已被授予
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            // 权限已经可用
            getState();
        } else {
            // Permission is missing and must be requested.
            requestStatePermission();
        }
    }

    private void requestStatePermission() {
        boolean isPermissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE);
        // 尚未授予许可，必须申请。
        if (isPermissionRationale) {
            showStateRationaleDialog();
        } else {
            // 请求许可。结果将在 onRequestPermissionResult() 中接收。
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSION_REQUEST_NET_CODE);
        }
    }


    private boolean getState() {
        // 获取TelephonyManager实例
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        // 用户授权成功，可以读取手机状态和身份信息
        String imei = telephonyManager.getDeviceId();
        String phoneNumber = telephonyManager.getLine1Number();
        int simState = telephonyManager.getSimState();
        String stateInfo = " imei: " + imei + " phoneNumber: " + phoneNumber + " simState: " + simState;
        Log.d(TAG, "getState ===>> stateInfo: " + stateInfo);
        Toast.makeText(this, "stateInfo: " + stateInfo, Toast.LENGTH_SHORT).show();

        return true;
    }

    private boolean callPhone() {
        Log.d(TAG, "callPhone ===>> ");
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:10086");
        intent.setData(data);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "onActivityResult,requestCode=" + requestCode);
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PERMISSION_GO_SETTING) {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
//                // 权限已经可用，开始相机预览
//                start();
//            } else {
//                onDenied();
//            }
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult,requestCode=" + requestCode);
        if (requestCode == PERMISSION_REQUEST_CALL_CODE) {
            if (verifyPermissions(grantResults)) {
                callPhone();
            } else {
                boolean isPermissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE);
                if (!isPermissionRationale) {
                    onNeverAskAgain();
                } else {
                    onDenied();
                }
            }
        } else if (requestCode == PERMISSION_REQUEST_NET_CODE) {
            if (verifyPermissions(grantResults)) {
                getState();
            } else {
                boolean isPermissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE);
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


    private void showPhoneRationaleDialog() {
        new AlertDialog.Builder(this)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(PhoneMainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_CALL_CODE);
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


    private void showStateRationaleDialog() {
        new AlertDialog.Builder(this)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(PhoneMainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSION_REQUEST_NET_CODE);
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
                        Uri uri = Uri.fromParts("package", PhoneMainActivity.this.getPackageName(), null);
                        intent.setData(uri);
                        PhoneMainActivity.this.startActivityForResult(intent, PERMISSION_GO_SETTING);
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
