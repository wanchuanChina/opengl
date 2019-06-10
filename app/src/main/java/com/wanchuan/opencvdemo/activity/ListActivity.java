package com.wanchuan.opencvdemo.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.wanchuan.opencvdemo.bean.ActivityName;
import com.wanchuan.opencvdemo.R;
import com.wanchuan.opencvdemo.adapter.ListAdapter;
import com.wanchuan.opencvdemo.camera.Camera2Activity;
import com.wanchuan.opencvdemo.camera.CameraActivity;

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
        activities.add(new ActivityName("基本相机使用", CameraUtilsActivity.class));
        activities.add(new ActivityName("基本相机使用-切换预览尺寸", CameraUtils2Activity.class));
        activities.add(new ActivityName("直角三角形", OpenGlDemo1Activity.class));
        activities.add(new ActivityName("等腰直角三角形", TragleEqualEdgActivity.class));
        activities.add(new ActivityName("正方形", SquareActivity.class));
        activities.add(new ActivityName("圆形", OvalActivity.class));
        activities.add(new ActivityName("立方体", CubeActivity.class));
        activities.add(new ActivityName("圆柱", CylinderActivity.class));
        activities.add(new ActivityName("圆锥", ConeActivity.class));
        activities.add(new ActivityName("球体", BallActivity.class));
        activities.add(new ActivityName("纹理贴图-平面-普通", TextureSimpleActivity.class, 0));
        activities.add(new ActivityName("纹理贴图-黑白", TextureSimpleActivity.class, 1));
        activities.add(new ActivityName("纹理贴图-冷色调", TextureSimpleActivity.class, 2));
        activities.add(new ActivityName("纹理贴图-暖色调", TextureSimpleActivity.class, 3));
        activities.add(new ActivityName("纹理贴图-模糊", TextureSimpleActivity.class, 4));
        activities.add(new ActivityName("纹理贴图-放大镜", TextureSimpleActivity.class, 5));
        activities.add(new ActivityName("纹理贴图-立方体", TextureCubeActivity.class));
        activities.add(new ActivityName("相机", CameraActivity.class));
        activities.add(new ActivityName("相机2 动画", Camera2Activity.class));


        ListAdapter adapter = new ListAdapter(this, activities);
        rv_list.setAdapter(adapter);
    }

    private void initView() {
        rv_list = findViewById(R.id.rv_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_list.setLayoutManager(linearLayoutManager);
    }
}
