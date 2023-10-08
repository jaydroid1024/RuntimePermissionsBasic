package com.example.android.basicpermissions.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.android.basicpermissions.R;

import java.util.Collections;
import java.util.List;

/**
 * @author wangxuejie
 * @date 10/7/23
 */
public class PermissionAdapter extends RecyclerView.Adapter<PermissionViewHolder> {

    List<PermissionGroupData> list = Collections.emptyList();

    Context context;
    PermissionClickListener listiner;

    public PermissionAdapter(List<PermissionGroupData> list, Context context, PermissionClickListener listener) {
        this.list = list;
        this.context = context;
        this.listiner = listener;
    }

    @Override
    public PermissionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View photoView = inflater.inflate(R.layout.permission_item, parent, false);
        return new PermissionViewHolder(photoView);
    }

    @Override
    public void onBindViewHolder(final PermissionViewHolder viewHolder, final int position) {
        final int index = viewHolder.getAdapterPosition();
        final PermissionGroupData permissionGroupData = list.get(position);
        viewHolder.name.setText(permissionGroupData.groupName);
        viewHolder.label.setText(index + ", " + permissionGroupData.groupLabel);
        viewHolder.desc.setText(permissionGroupData.groupDescription);
        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listiner != null) {
                    listiner.click(permissionGroupData);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(
            RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


}

