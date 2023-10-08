package com.example.android.basicpermissions.list;

import android.Manifest;
import android.os.Build;
import android.util.ArrayMap;

import java.util.ArrayList;

import static android.Manifest.permission_group.CALENDAR;
import static android.Manifest.permission_group.CAMERA;
import static android.Manifest.permission_group.CONTACTS;
import static android.Manifest.permission_group.LOCATION;
import static android.Manifest.permission_group.MICROPHONE;
import static android.Manifest.permission_group.PHONE;
import static android.Manifest.permission_group.SENSORS;
import static android.Manifest.permission_group.SMS;
import static android.Manifest.permission_group.STORAGE;

/**
 * @author wangxuejie
 * @date 3/8/23
 */
public class PermissionSysPerManager {

    public static final String TAG = "AndroidSysPerHelper";

    /**
     * Mapping permission -> group for all dangerous platform permissions
     */
    private static final ArrayMap<String, String> PLATFORM_PERMISSIONS;

    /**
     * Mapping group -> permissions for all dangerous platform permissions
     */
    private static final ArrayMap<String, ArrayList<String>> PLATFORM_PERMISSION_GROUPS;

    /*

按组展开所有权限 From android 8.1
adb shell pm list permissions -d -g
Dangerous Permissions:

group:android.permission-group.CONTACTS
  permission:android.permission.WRITE_CONTACTS
  permission:android.permission.GET_ACCOUNTS
  permission:android.permission.READ_CONTACTS

group:android.permission-group.PHONE
  permission:android.permission.READ_CALL_LOG
  permission:android.permission.ANSWER_PHONE_CALLS
  permission:android.permission.READ_PHONE_NUMBERS
  permission:android.permission.READ_PHONE_STATE
  permission:android.permission.CALL_PHONE
  permission:android.permission.WRITE_CALL_LOG
  permission:android.permission.USE_SIP
  permission:android.permission.PROCESS_OUTGOING_CALLS
  permission:com.android.voicemail.permission.ADD_VOICEMAIL

group:android.permission-group.CALENDAR
  permission:android.permission.READ_CALENDAR
  permission:android.permission.WRITE_CALENDAR

group:android.permission-group.CAMERA
  permission:android.permission.CAMERA

group:android.permission-group.SENSORS
  permission:android.permission.BODY_SENSORS

group:android.permission-group.LOCATION
  permission:android.permission.ACCESS_FINE_LOCATION
  permission:android.permission.ACCESS_COARSE_LOCATION

group:android.permission-group.STORAGE
  permission:android.permission.READ_EXTERNAL_STORAGE
  permission:android.permission.WRITE_EXTERNAL_STORAGE

group:android.permission-group.MICROPHONE
  permission:android.permission.RECORD_AUDIO

group:android.permission-group.SMS
  permission:android.permission.READ_SMS
  permission:android.permission.RECEIVE_WAP_PUSH
  permission:android.permission.RECEIVE_MMS
  permission:android.permission.RECEIVE_SMS
  permission:android.permission.SEND_SMS
  permission:android.permission.READ_CELL_BROADCASTS
     */
    static {
        PLATFORM_PERMISSIONS = new ArrayMap<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PLATFORM_PERMISSIONS.put(Manifest.permission.READ_CONTACTS, CONTACTS);
            PLATFORM_PERMISSIONS.put(Manifest.permission.WRITE_CONTACTS, CONTACTS);
            PLATFORM_PERMISSIONS.put(Manifest.permission.GET_ACCOUNTS, CONTACTS);
        }

        PLATFORM_PERMISSIONS.put(Manifest.permission.READ_CALENDAR, CALENDAR);
        PLATFORM_PERMISSIONS.put(Manifest.permission.WRITE_CALENDAR, CALENDAR);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PLATFORM_PERMISSIONS.put(Manifest.permission.SEND_SMS, SMS);
            PLATFORM_PERMISSIONS.put(Manifest.permission.RECEIVE_SMS, SMS);
            PLATFORM_PERMISSIONS.put(Manifest.permission.READ_SMS, SMS);
            PLATFORM_PERMISSIONS.put(Manifest.permission.RECEIVE_MMS, SMS);
            PLATFORM_PERMISSIONS.put(Manifest.permission.RECEIVE_WAP_PUSH, SMS);
        }

        PLATFORM_PERMISSIONS.put(Manifest.permission.READ_EXTERNAL_STORAGE, STORAGE);
        PLATFORM_PERMISSIONS.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE);

        PLATFORM_PERMISSIONS.put(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION);
        PLATFORM_PERMISSIONS.put(Manifest.permission.ACCESS_COARSE_LOCATION, LOCATION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PLATFORM_PERMISSIONS.put(Manifest.permission.READ_PHONE_STATE, PHONE);
            PLATFORM_PERMISSIONS.put(Manifest.permission.READ_CALL_LOG, PHONE);
            PLATFORM_PERMISSIONS.put(Manifest.permission.WRITE_CALL_LOG, PHONE);
            PLATFORM_PERMISSIONS.put(Manifest.permission.CALL_PHONE, PHONE);
            PLATFORM_PERMISSIONS.put(Manifest.permission.ADD_VOICEMAIL, PHONE);
            PLATFORM_PERMISSIONS.put(Manifest.permission.USE_SIP, PHONE);
            PLATFORM_PERMISSIONS.put(Manifest.permission.PROCESS_OUTGOING_CALLS, PHONE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                PLATFORM_PERMISSIONS.put(Manifest.permission.READ_PHONE_NUMBERS, PHONE);
                PLATFORM_PERMISSIONS.put(Manifest.permission.ANSWER_PHONE_CALLS, PHONE);
            }
        }
        PLATFORM_PERMISSIONS.put(Manifest.permission.RECORD_AUDIO, MICROPHONE);
        PLATFORM_PERMISSIONS.put(Manifest.permission.CAMERA, CAMERA);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PLATFORM_PERMISSIONS.put(Manifest.permission.BODY_SENSORS, SENSORS);
        }

        PLATFORM_PERMISSION_GROUPS = new ArrayMap<>();
        int numPlatformPermissions = PLATFORM_PERMISSIONS.size();
        for (int i = 0; i < numPlatformPermissions; i++) {
            String permission = PLATFORM_PERMISSIONS.keyAt(i);
            String permissionGroup = PLATFORM_PERMISSIONS.valueAt(i);
            ArrayList<String> permissionsOfThisGroup = PLATFORM_PERMISSION_GROUPS.get(permissionGroup);
            if (permissionsOfThisGroup == null) {
                permissionsOfThisGroup = new ArrayList<>();
                PLATFORM_PERMISSION_GROUPS.put(permissionGroup, permissionsOfThisGroup);
            }
            permissionsOfThisGroup.add(permission);
        }
    }

    private PermissionSysPerManager() {
    }

    private static PermissionSysPerManager instance;

    public static PermissionSysPerManager getInstance() {
        if (instance == null) {
            synchronized (PermissionSysPerManager.class) {
                if (instance == null) {
                    instance = new PermissionSysPerManager();
                }
            }
        }
        return instance;
    }
}
