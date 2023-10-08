

package com.example.android.basicpermissions.storage;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.android.basicpermissions.R;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class StorageMainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int PERMISSION_REQUEST_WRITE_CODE = 0;
    private static final int PERMISSION_REQUEST_READ_CODE = 1;

    private static final int PERMISSION_GO_SETTING = 1;

    private static final String TAG = "Permission";

    private View mLayout;

    String[] permissionArray;

    ///storage/emulated/0/read_write_test_file.txt
    private String mTestFileName = "read_write_test_file.txt";
    private String mTestFileDir = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.storage_activity_main);
        initIntent();
        initView();
        mTestFileDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        Log.d(TAG, "mTestFileDir= " + mTestFileDir);
        Log.d(TAG, "mTestFileName= " + mTestFileName);


    }

    private void initIntent() {
        String group = getIntent().getStringExtra("group");
        Log.d(TAG, "initIntent,group=" + group);
        //获取所有组内权限
        permissionArray = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    private void initView() {
        mLayout = findViewById(R.id.main_layout);
        // Register a listener for the 'Show Camera Preview' button.
        findViewById(R.id.button_write).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndSaveFile();
            }
        });
        findViewById(R.id.button_write_without_perm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveFile();
            }
        });

        findViewById(R.id.button_read).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndReadFile();
            }
        });

        findViewById(R.id.button_read_without_perm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readFile();
            }
        });
    }


    private void checkAndSaveFile() {
        // 检查权限是否已被授予
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // 权限已经可用
            saveFile();
        } else {
            // Permission is missing and must be requested.
            requestWritePermission();
        }
    }

    private void requestWritePermission() {
        boolean isPermissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        // 尚未授予许可，必须申请。
        if (isPermissionRationale) {
            showWriteRationaleDialog();
        } else {
            // 请求许可。结果将在 onRequestPermissionResult() 中接收。
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_WRITE_CODE);
        }
    }

    private void checkAndReadFile() {
        // 检查权限是否已被授予
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // 权限已经可用
            readFile();
        } else {
            // Permission is missing and must be requested.
            requestReadPermission();
        }
    }

    private void requestReadPermission() {
        boolean isPermissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        // 尚未授予许可，必须申请。
        if (isPermissionRationale) {
            showReadRationaleDialog();
        } else {
            // 请求许可。结果将在 onRequestPermissionResult() 中接收。
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_READ_CODE);
        }
    }


    private boolean readFile() {
        String strFullPath = mTestFileDir + "/" + mTestFileName;
        StringBuffer strBuffer = new StringBuffer();
        byte[] buffer = new byte[1024];
        try {
            FileInputStream fi = new FileInputStream(strFullPath);
            while (true) {
                int len = fi.read(buffer);
                if (len > 0) {
                    strBuffer.append(new String(buffer, 0, len));
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            Log.i(TAG, e.toString());
            return false;
        }
        Log.i(TAG, "读出文件成功");
        Toast.makeText(this, strBuffer, Toast.LENGTH_SHORT).show();
        return true;
    }

    private boolean saveFile() {
        File fDir = new File(mTestFileDir);
        if (!fDir.exists()) {
            if (!fDir.mkdirs()) {
                Toast.makeText(this, "创建目录失败:" + mTestFileDir, Toast.LENGTH_SHORT).show();
                return false;
            }
            Log.i(TAG, "创建目录" + mTestFileDir + "成功");
        } else {
            Log.i(TAG, "目录" + mTestFileDir + "已经存在");
        }
        //创建一个文件，用来写入测试数据
        String fileFullPath = mTestFileDir + "/" + mTestFileName;
        try {
            FileOutputStream fo = new FileOutputStream(fileFullPath);
            String strTest = "hero come here to test you: " + System.currentTimeMillis();
            fo.write(strTest.getBytes());
            fo.close();
        } catch (IOException e) {
            Log.i(TAG, e.toString());
            return false;
        }
        Log.i(TAG, "写入文件成功");
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
        if (requestCode == PERMISSION_REQUEST_WRITE_CODE) {
            if (verifyPermissions(grantResults)) {
                saveFile();
            } else {
                boolean isPermissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (!isPermissionRationale) {
                    onNeverAskAgain();
                } else {
                    onDenied();
                }
            }
        } else if (requestCode == PERMISSION_REQUEST_READ_CODE) {
            if (verifyPermissions(grantResults)) {
                readFile();
            } else {
                boolean isPermissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE);
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


    private void showWriteRationaleDialog() {
        new AlertDialog.Builder(this)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(StorageMainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_WRITE_CODE);
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


    private void showReadRationaleDialog() {
        new AlertDialog.Builder(this)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(StorageMainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_READ_CODE);
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
                        Uri uri = Uri.fromParts("package", StorageMainActivity.this.getPackageName(), null);
                        intent.setData(uri);
                        StorageMainActivity.this.startActivityForResult(intent, PERMISSION_GO_SETTING);
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
