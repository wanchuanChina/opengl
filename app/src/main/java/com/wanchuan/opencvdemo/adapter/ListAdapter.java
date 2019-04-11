package com.wanchuan.opencvdemo.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wanchuan.opencvdemo.bean.ActivityName;
import com.wanchuan.opencvdemo.R;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private Activity activity;
    private List<ActivityName> activityNameList;

    public ListAdapter(Activity activity, List<ActivityName> activityNameList) {
        this.activity = activity;
        this.activityNameList = activityNameList;
    }

    @NonNull
    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.list_adapter, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListAdapter.ViewHolder viewHolder, final int i) {
        viewHolder.tv_name.setText(activityNameList.get(i).getName());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(activity, activityNameList.get(i).gettClass()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return activityNameList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tv_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
        }
    }
}
