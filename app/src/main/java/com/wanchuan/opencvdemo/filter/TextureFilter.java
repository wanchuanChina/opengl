package com.wanchuan.opencvdemo.filter;

import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.opengl.GLES30;
import android.opengl.GLES30;

import com.wanchuan.opencvdemo.utils.EasyGlUtils;

public class TextureFilter extends AFilter {

    private CameraFilter mFilter; // 设置摄像头数据旋转
    private int width = 0;
    private int height = 0;

    private int[] fFrame = new int[1];
    private int[] fTexture = new int[1];
    private int[] mCameraTexture = new int[1];

    private SurfaceTexture mSurfaceTexture;
    private float[] mCoordOM = new float[16];


    public TextureFilter(Resources mRes) {
        super(mRes);
        mFilter = new CameraFilter(mRes);
    }

    public void setCoordMatrix(float[] matrix) {
        mFilter.setCoordMatrix(matrix);
    }

    public SurfaceTexture getTexture() {
        return mSurfaceTexture;
    }

    @Override
    public void setFlag(int flag) {
        mFilter.setFlag(flag);
    }

    @Override
    protected void initBuffer() {
    }


    @Override
    public void setMatrix(float[] matrix) {
        mFilter.setMatrix(matrix);
    }

    @Override
    public int getOutputTexture() {
        return fTexture[0];
    }


    @Override
    protected void onDraw() {
        boolean b = GLES30.glIsEnabled(GLES30.GL_DEPTH_TEST);
        if (b) {
            GLES30.glDisable(GLES30.GL_DEPTH_TEST);
        }
        if (mSurfaceTexture != null) {
            mSurfaceTexture.updateTexImage();
            mSurfaceTexture.getTransformMatrix(mCoordOM);
            mFilter.setCoordMatrix(mCoordOM);
        }
        EasyGlUtils.bindFrameTexture(fFrame[0], fTexture[0]);
        GLES30.glViewport(0, 0, width, height);
        mFilter.setTextureId(mCameraTexture[0]);
        mFilter.draw();
        EasyGlUtils.unBindFrameBuffer();
        if (b) {
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        }
    }

    /**
     * 创建program
     */
    @Override
    protected void onCreate() {
        mFilter.onCreate();
        createOesTexture();
        mSurfaceTexture = new SurfaceTexture(mCameraTexture[0]);
    }

    @Override
    protected void onSizeChanged(int width, int height) {
        mFilter.setSize(width, height);
        if (this.width != width || this.height != height) {
            this.width = width;
            this.height = height;
            // 创建FrameBuffer 和 Texture
            deleteFrameBuffer();
            GLES30.glGenFramebuffers(1, fFrame, 0);
            EasyGlUtils.genTexturesWithParameter(1, fTexture, 0, GLES30.GL_RGBA, width, height);
        }
    }

    private void deleteFrameBuffer() {
        GLES30.glDeleteFramebuffers(1, fFrame, 0);
        GLES30.glDeleteTextures(1, fTexture, 0);
    }

    /**
     * 生成纹理
     */
    private void createOesTexture() {
        GLES30.glGenTextures(1, mCameraTexture, 0);
    }

}
