package com.wanchuan.opencvdemo.camera;

import android.content.Context;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CameraView extends GLSurfaceView implements GLSurfaceView.Renderer {

    private KitkatCamera mCamera2;
    private CameraDrawer mCameraDrawer;
    private int cameraId = 1;

    private Runnable mRunnable;

    public CameraView(Context context) {
        super(context, null);
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setEGLContextClientVersion(3);
        setRenderer(this);
        setRenderMode(RENDERMODE_WHEN_DIRTY);

        mCamera2 = new KitkatCamera();
        mCameraDrawer = new CameraDrawer(getResources());

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mCameraDrawer.onSurfaceCreated(gl, config);
        if (mRunnable != null) {
            mRunnable.run();
            mRunnable = null;
        }
        mCamera2.open(cameraId);
        mCameraDrawer.setCameraId(cameraId);
        Point point = mCamera2.getPreviewSize();
        mCameraDrawer.setDataSize(point.x, point.y);
        mCamera2.setPreviewTexture(mCameraDrawer.getSurfaceTexture());
        mCameraDrawer.getSurfaceTexture().setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                requestRender();// 请求渲染
            }
        });
        mCamera2.preview(); // 开始预览

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mCameraDrawer.setViewSize(width, height);
        GLES30.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mCameraDrawer.onDrawFrame(gl);
    }

    /**
     * 切换摄像头
     */
    public void switchCamera() {
        mRunnable = new Runnable() {
            @Override
            public void run() {
                mCamera2.close();
                cameraId = cameraId == 1 ? 0 : 1;
            }
        };
        onPause();
        onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        mCamera2.close();
    }
}
