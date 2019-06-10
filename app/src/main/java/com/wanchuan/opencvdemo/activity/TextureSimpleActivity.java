package com.wanchuan.opencvdemo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.wanchuan.opencvdemo.R;
import com.wanchuan.opencvdemo.image.SGLView;
import com.wanchuan.opencvdemo.image.render.ColorFilter;
import com.wanchuan.opencvdemo.image.render.ContrastColorFilter;
import com.wanchuan.opencvdemo.render.FGLView;

public class TextureSimpleActivity extends AppCompatActivity {

    private SGLView mSgl_view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_texture_simple);
        mSgl_view = findViewById(R.id.sgl_view);
        initData();
        mSgl_view.getRender().getFilter().setHalf(true);
        mSgl_view.requestRender();
    }


    private void initData() {
        int type = getIntent().getIntExtra("type", 0);
        switch (type){
            case 0: // 常规
                mSgl_view.setFilter(new ContrastColorFilter(this, ColorFilter.Filter.NONE));
                break;
            case 1: // 黑白
                mSgl_view.setFilter(new ContrastColorFilter(this, ColorFilter.Filter.GRAY));
                break;
            case 2: // 冷色调
                mSgl_view.setFilter(new ContrastColorFilter(this, ColorFilter.Filter.COOL));
                break;
            case 3: // 暖色调
                mSgl_view.setFilter(new ContrastColorFilter(this, ColorFilter.Filter.WARM));
                break;
            case 4: // 模糊
                mSgl_view.setFilter(new ContrastColorFilter(this, ColorFilter.Filter.BLUR));
                break;
            case 5: // 放大镜
                mSgl_view.setFilter(new ContrastColorFilter(this, ColorFilter.Filter.MAGN));
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSgl_view.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSgl_view.onPause();
    }

}
