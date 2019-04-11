package com.wanchuan.opencvdemo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.wanchuan.opencvdemo.R;
import com.wanchuan.opencvdemo.render.Ball;
import com.wanchuan.opencvdemo.render.Cone;
import com.wanchuan.opencvdemo.render.FGLView;


/**
 * 圆柱绘制
 */
public class BallActivity extends AppCompatActivity {

    private FGLView mFgl_view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cylinder);
        mFgl_view = findViewById(R.id.fgl_view);
        mFgl_view.setShape(Ball.class);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mFgl_view.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFgl_view.onPause();
    }

}
