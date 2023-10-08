package com.example.android.basicpermissions.list;

/**
 * @author wangxuejie
 * @date 10/7/23
 */
public class PermissionData {

    String groupName;
    String groupLabel;
    String groupDescription;

    String perName;
    String perLabel;
    String perDescription;

    public PermissionData(String groupName, String groupLabel, String groupDescription, String perName, String perLabel, String perDescription) {
        this.groupName = groupName;
        this.groupLabel = groupLabel;
        this.groupDescription = groupDescription;
        this.perName = perName;
        this.perLabel = perLabel;
        this.perDescription = perDescription;
    }
}
