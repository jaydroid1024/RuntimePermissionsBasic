package com.example.android.basicpermissions.list;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.android.basicpermissions.R;

/**
 * @author wangxuejie
 * @date 10/7/23
 */
public class PermissionViewHolder extends RecyclerView.ViewHolder {
    TextView name;
    TextView desc;
    TextView label;
    View view;

    PermissionViewHolder(View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.perm_group_name);
        label = (TextView) itemView.findViewById(R.id.perm_group_label);
        desc = (TextView) itemView.findViewById(R.id.perm_group_desc);
        view = itemView;
    }
}
