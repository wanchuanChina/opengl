package com.wanchuan.opencvdemo.camera;

import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import com.wanchuan.opencvdemo.filter.OesFilter;
import com.wanchuan.opencvdemo.utils.Gl2Utils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CameraDrawer implements GLSurfaceView.Renderer {

    private float[] matrix = new float[16];
    private int cameraId = 1;
    private final OesFilter mOesFilter;
    private SurfaceTexture surfaceTexture;
    private int width, height;
    private int dataWidth, dataHeight;

    public CameraDrawer(Resources res) {
        mOesFilter = new OesFilter(res);
    }

    public void setDataSize(int dataWidth, int dataHeight) {
        this.dataHeight = dataHeight;
        this.dataWidth = dataWidth;
        calculateMatrix();
    }

    /**
     * 重新调整界面显示图像矩阵，根据前后摄像头不同，旋转不同角度
     * @param width
     * @param height
     */
    public void setViewSize(int width, int height) {
        this.width = width;
        this.height = height;
        calculateMatrix();
    }


    /**
     * 计算矩阵变换
     */
    private void calculateMatrix() {
        Gl2Utils.getShowMatrix(matrix, dataWidth, dataHeight, width, height);
        if (cameraId == 1) { // 前置摄像头
            Gl2Utils.flip(matrix, true, false);// 镜像操作
            Gl2Utils.rotate(matrix, 90);  // 矩阵旋转
        } else { // 后置摄像头
            Gl2Utils.rotate(matrix, 270);
        }
        mOesFilter.setMatrix(matrix);
    }

    public SurfaceTexture getSurfaceTexture() {
        return surfaceTexture;
    }

    public void setCameraId(int cameraId) {
        this.cameraId = cameraId;
        calculateMatrix();
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        int texture = createTextureID();
        // SurfaceTexture 只是处理图像数据，并不显示，可以将它处理的数据交给其他 surfaceView 显示
        surfaceTexture = new SurfaceTexture(texture);
        // 创建program句柄
        mOesFilter.create();
        mOesFilter.setTextureId(texture);
    }

    /**
     * 创建纹理ID
     * @return 返回纹理 句柄
     */
    private int createTextureID() {
        int[] texture = new int[1];
        GLES30.glGenTextures(1,texture,0); // 生成纹理
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]); // 绑定纹理
        //过滤（纹理像素映射到坐标点）  （缩小、放大：GL_LINEAR线性）
        GLES30.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES30.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        //环绕（超出纹理坐标范围）  （s==x t==y GL_REPEAT 重复）
        GLES30.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        return texture[0];
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        setViewSize(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (surfaceTexture != null) {
            // 更新纹理图像，即摄像头数据实时渲染
            surfaceTexture.updateTexImage();
        }
        mOesFilter.draw();
    }
}
