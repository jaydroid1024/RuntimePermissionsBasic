package com.example.android.basicpermissions.list;

/**
 * @author wangxuejie
 * @date 10/7/23
 */
public class PermissionGroupData {

    public Class<?> clazz;
    public String groupName;
    public String groupLabel;
    public String groupDescription;


    public PermissionGroupData(Class<?> clazz, String groupName, String groupLabel, String groupDescription) {
        this.clazz = clazz;
        this.groupName = groupName;
        this.groupLabel = groupLabel;
        this.groupDescription = groupDescription;
    }
}
