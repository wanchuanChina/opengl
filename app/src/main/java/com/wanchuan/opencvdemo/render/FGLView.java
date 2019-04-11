package com.wanchuan.opencvdemo.render;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class FGLView extends GLSurfaceView {

    private FGLRender mRender;

    public FGLView(Context context) {
        super(context, null);
    }

    public FGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setEGLContextClientVersion(3);
        setRenderer(mRender = new FGLRender(this));
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public void setShape(Class<? extends Shape> clazz){
        try {
            mRender.setShape(clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
