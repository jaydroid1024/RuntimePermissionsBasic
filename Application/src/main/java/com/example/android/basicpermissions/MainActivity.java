package com.example.android.basicpermissions;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.basicpermissions.camera.CameraMainActivity;
import com.example.android.basicpermissions.list.PermissionAdapter;
import com.example.android.basicpermissions.list.PermissionClickListener;
import com.example.android.basicpermissions.list.PermissionGroupData;
import com.example.android.basicpermissions.location.LocationMainActivity;
import com.example.android.basicpermissions.microphone.MicrophoneMainActivity;
import com.example.android.basicpermissions.phone.PhoneMainActivity;
import com.example.android.basicpermissions.storage.StorageMainActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    PermissionAdapter adapter;
    RecyclerView recyclerView;
    PermissionClickListener listener;
    List<PermissionGroupData> list;
    TextView tvSettingAndroid;
    TextView tvSettingQihoo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list = getData();
        initView();
        initListener();
    }

    private void initListener() {

        tvSettingAndroid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", MainActivity.this.getPackageName(), null);
                intent.setData(uri);
                MainActivity.this.startActivity(intent);
            }
        });

        tvSettingQihoo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPermissionManageView();
            }
        });
    }

    //todo 继续更新我们自己的权限系统
    private void showPermissionManageView() {
        String QIHOO_PERMISSION_MANAGE_ACTION = "com.qihoo.kids.ACTION_APP_PERMISSION_LIST";
        String ANDROID_PERMISSION_MANAGE_ACTION = "android.intent.action.MANAGE_PERMISSIONS";
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(QIHOO_PERMISSION_MANAGE_ACTION);
        startActivity(intent);
    }

    private void initView() {
        tvSettingAndroid = (TextView) findViewById(R.id.tv_setting_android);
        tvSettingQihoo = (TextView) findViewById(R.id.tv_setting_qihoo);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new PermissionAdapter(list, getApplication(), listener = new PermissionClickListener() {
            @Override
            public void click(PermissionGroupData index) {
                try {
                    Intent intent = new Intent(MainActivity.this, index.clazz);
                    intent.putExtra("group", index.groupName);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
    }

    // Sample data for RecyclerView
    private List<PermissionGroupData> getData() {
        List<PermissionGroupData> permissionGroupData = new ArrayList<>();
        permissionGroupData.add(new PermissionGroupData(CameraMainActivity.class, "android.permission-group.CAMERA", "相机", "拍摄照片和录制视频"));
        permissionGroupData.add(new PermissionGroupData(MicrophoneMainActivity.class, "android.permission-group.MICROPHONE", "麦克风", "录制音频"));
        permissionGroupData.add(new PermissionGroupData(StorageMainActivity.class, "android.permission-group.STORAGE", "存储空间", "访问您设备上的照片、媒体内容和文件"));
        permissionGroupData.add(new PermissionGroupData(LocationMainActivity.class, "android.permission-group.LOCATION", "位置信息", "获取此设备的位置信息"));

        permissionGroupData.add(new PermissionGroupData(PhoneMainActivity.class, "android.permission-group.PHONE", "电话", "拨打电话和管理通话"));
        permissionGroupData.add(new PermissionGroupData(CameraMainActivity.class, "android.permission-group.SMS", "短信", "发送和查看短信"));
        permissionGroupData.add(new PermissionGroupData(CameraMainActivity.class, "android.permission-group.CONTACTS", "通讯录", "访问您的通讯录"));
        permissionGroupData.add(new PermissionGroupData(CameraMainActivity.class, "android.permission-group.SENSORS", "身体传感器", "访问与您的生命体征相关的传感器数据"));
        permissionGroupData.add(new PermissionGroupData(CameraMainActivity.class, "android.permission-group.CALENDAR", "日历", "访问您的日历"));
        return permissionGroupData;
    }
}
