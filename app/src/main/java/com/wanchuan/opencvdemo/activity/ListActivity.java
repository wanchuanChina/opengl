package com.wanchuan.opencvdemo.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.wanchuan.opencvdemo.bean.ActivityName;
import com.wanchuan.opencvdemo.R;
import com.wanchuan.opencvdemo.adapter.ListAdapter;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    private RecyclerView rv_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        initView();
        initData();
    }

    private void initData() {
        ArrayList<ActivityName> activities = new ArrayList<>();
        activities.add(new ActivityName("直角三角形",OpenGlDemo1Activity.class));
        activities.add(new ActivityName("等腰直角三角形",TragleEqualEdgActivity.class));
        activities.add(new ActivityName("正方形",SquareActivity.class));
        activities.add(new ActivityName("圆形",OvalActivity.class));
        activities.add(new ActivityName("立方体",CubeActivity.class));
        activities.add(new ActivityName("圆柱",CylinderActivity.class));
        activities.add(new ActivityName("圆锥",ConeActivity.class));
        activities.add(new ActivityName("球体",BallActivity.class));


        ListAdapter adapter = new ListAdapter(this, activities);
        rv_list.setAdapter(adapter);
    }

    private void initView() {
        rv_list = findViewById(R.id.rv_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_list.setLayoutManager(linearLayoutManager);
    }
}
