package com.example.android.basicpermissions.location;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
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

public class LocationMainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int PERMISSION_REQUEST_GPS_CODE = 0;
    private static final int PERMISSION_REQUEST_NET_CODE = 1;

    private static final int PERMISSION_GO_SETTING = 1;

    private static final String TAG = "Permission";

    private View mLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_activity_main);
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
        findViewById(R.id.button_gps).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndGPS();
            }
        });
        findViewById(R.id.button_gps_without_perm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gpsLocation();
            }
        });

        findViewById(R.id.button_net).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndNet();
            }
        });

        findViewById(R.id.button_net_without_perm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                netLocation();
            }
        });
    }


    private void checkAndGPS() {
        // 检查权限是否已被授予
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // 权限已经可用
            gpsLocation();
        } else {
            // Permission is missing and must be requested.
            requestGPSPermission();
        }
    }

    private void requestGPSPermission() {
        boolean isPermissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION);
        // 尚未授予许可，必须申请。
        if (isPermissionRationale) {
            showGPSRationaleDialog();
        } else {
            // 请求许可。结果将在 onRequestPermissionResult() 中接收。
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_GPS_CODE);
        }
    }

    private void checkAndNet() {
        // 检查权限是否已被授予
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // 权限已经可用
            netLocation();
        } else {
            // Permission is missing and must be requested.
            requestNetPermission();
        }
    }

    private void requestNetPermission() {
        boolean isPermissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        // 尚未授予许可，必须申请。
        if (isPermissionRationale) {
            showNetRationaleDialog();
        } else {
            // 请求许可。结果将在 onRequestPermissionResult() 中接收。
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_NET_CODE);
        }
    }


    private boolean netLocation() {
        LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Location lastKnownLocation = null;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            lastKnownLocation = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if (lastKnownLocation != null) {
            Log.e(TAG, "getLastKnownLocation= " + lastKnownLocation.toString());
            Toast.makeText(this, lastKnownLocation.toString(), Toast.LENGTH_SHORT).show();
        }

        locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0F, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.e(TAG, "NETWORK_PROVIDER--onLocationChanged= " + location.toString());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.e(TAG, "NETWORK_PROVIDER--onStatusChanged= " + status);

            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.e(TAG, "NETWORK_PROVIDER--onProviderEnabled= " + provider);

            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.e(TAG, "NETWORK_PROVIDER--onProviderDisabled= " + provider);

            }
        });

        return true;
    }

    private boolean gpsLocation() {
        LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

//        Location lastKnownLocation = null;
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            lastKnownLocation = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        }
//        if (lastKnownLocation != null) {
//            Log.e(TAG, "getLastKnownLocation= " + lastKnownLocation.toString());
//            Toast.makeText(this, lastKnownLocation.toString(), Toast.LENGTH_SHORT).show();
//        }

        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0F, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.e(TAG, "GPS_PROVIDER--onLocationChanged= " + location.toString());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.e(TAG, "GPS_PROVIDER--onStatusChanged= " + status);

            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.e(TAG, "GPS_PROVIDER--onProviderEnabled= " + provider);

            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.e(TAG, "GPS_PROVIDER--onProviderDisabled= " + provider);

            }
        });

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
        if (requestCode == PERMISSION_REQUEST_GPS_CODE) {
            if (verifyPermissions(grantResults)) {
                gpsLocation();
            } else {
                boolean isPermissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION);
                if (!isPermissionRationale) {
                    onNeverAskAgain();
                } else {
                    onDenied();
                }
            }
        } else if (requestCode == PERMISSION_REQUEST_NET_CODE) {
            if (verifyPermissions(grantResults)) {
                netLocation();
            } else {
                boolean isPermissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION);
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


    private void showGPSRationaleDialog() {
        new AlertDialog.Builder(this)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(LocationMainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_GPS_CODE);
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


    private void showNetRationaleDialog() {
        new AlertDialog.Builder(this)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(LocationMainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_NET_CODE);
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
                        Uri uri = Uri.fromParts("package", LocationMainActivity.this.getPackageName(), null);
                        intent.setData(uri);
                        LocationMainActivity.this.startActivityForResult(intent, PERMISSION_GO_SETTING);
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
