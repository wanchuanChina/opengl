package com.wanchuan.opencvdemo.activity;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.wanchuan.opencvdemo.cube.GLRender;
import com.wanchuan.opencvdemo.utils.GlImage;

public class TextureCubeActivity extends AppCompatActivity {

    private GLRender mRender;
    private float mPreviousX;
    private float mPreviousY;
    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private final float TRACKBALL_SCALE_FACTOR = 36.0f;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new GlImage(this);
        mRender = new GLRender();
        GLSurfaceView glView = new GLSurfaceView(this);
        glView.setRenderer(mRender);
        setContentView(glView);

    }


    public boolean onKeyUp(int keyCode, KeyEvent event) {
        mRender.onKeyUp(keyCode, event);
        return false;
    }


    public boolean onTrackballEvent(MotionEvent e) {
        mRender.xrot += e.getX() * TRACKBALL_SCALE_FACTOR;
        mRender.yrot += e.getY() * TRACKBALL_SCALE_FACTOR;
        return true;
    }

    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dx = x - mPreviousX;
                float dy = y - mPreviousY;
                mRender.xrot += dx * TOUCH_SCALE_FACTOR;
                mRender.yrot += dy * TOUCH_SCALE_FACTOR;
        }
        mPreviousX = x;
        mPreviousY = y;
        return true;
    }

}
