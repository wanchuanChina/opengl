package com.wanchuan.opencvdemo.render;

import android.opengl.GLES30;
import android.opengl.GLES30;
import android.opengl.Matrix;
import android.util.Log;
import android.view.View;

import com.wanchuan.opencvdemo.utils.ShaderUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 圆锥
 */
public class Cone extends Shape {

    private int mProgram;

    private Oval oval;
    private FloatBuffer vertexBuffer;

    private float[] mViewMatrix = new float[16];
    private float[] mProjectMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    private int n = 360;  //切割份数
    private float height = 2.0f;  //圆锥高度
    private float radius = 1.0f;  //圆锥底面半径
    private int vSize;


    public Cone(View view) {
        super(view);

        oval = new Oval(mView);
        ArrayList<Float> pos = new ArrayList<>();
        pos.add(0.0f);
        pos.add(0.0f);
        pos.add(height);
        float angDegSpan = 360f / n;
        for (float i = 0; i < 360 + angDegSpan; i += angDegSpan) {
            pos.add((float) (radius * Math.sin(i * Math.PI / 180f)));
            pos.add((float) (radius * Math.cos(i * Math.PI / 180f)));
            pos.add(0.0f);
        }
        float[] d = new float[pos.size()];
        for (int i = 0; i < d.length; i++) {
            d[i] = pos.get(i);
        }
        vSize = d.length / 3;
        ByteBuffer buffer = ByteBuffer.allocateDirect(d.length * 4);
        buffer.order(ByteOrder.nativeOrder());
        vertexBuffer = buffer.asFloatBuffer();
        vertexBuffer.put(d);
        vertexBuffer.position(0);

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES30.glEnable(GLES30.GL_DEPTH_TEST); // 开启深度测试
        mProgram = ShaderUtils.createProgram(mView.getResources(), "vshader/Cone.sh", "fshader/Cone.sh");
        oval.onSurfaceCreated(gl, config);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // 计算宽高比
        float ratio = (float) width / height;
        // 设置透视投影
        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 20);
        // 设置相机投影
        Matrix.setLookAtM(mViewMatrix, 0, 1f, -10f, -4f, 0f, 0f, 0f, 0f, 1f, 0f);
        // 计算相机变换矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glUseProgram(mProgram);
//
        int vMatirx = GLES30.glGetUniformLocation(mProgram, "vMatrix");
        // 给vMatrix 赋值
        GLES30.glUniformMatrix4fv(vMatirx, 1, false, mMVPMatrix, 0);
        // 获取位置句柄
        int vPosition = GLES30.glGetAttribLocation(mProgram, "vPosition");
        // 启用位置句柄
        GLES30.glEnableVertexAttribArray(vPosition);
        // 设置位置坐标点
        GLES30.glVertexAttribPointer(vPosition, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, vSize);
        GLES30.glDisableVertexAttribArray(vPosition);

        oval.setMatrix(mMVPMatrix);
        oval.onDrawFrame(gl);

    }
}
