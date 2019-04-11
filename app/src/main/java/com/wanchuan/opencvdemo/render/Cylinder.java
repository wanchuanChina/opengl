package com.wanchuan.opencvdemo.render;

import android.opengl.GLES30;
import android.opengl.Matrix;
import android.view.View;

import com.wanchuan.opencvdemo.utils.ShaderUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Cylinder extends Shape {


    private int mProgram;

    private Oval ovalBottom, ovalTop;
    private FloatBuffer vertexBuffer;

    private float[] mViewMatrix = new float[16];
    private float[] mProjectMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    private int n = 360;  //切割份数
    private float height = 2.0f;  //圆柱高度
    private float radius = 1.0f;  //圆柱底面半径

    private int vSize; // 点的数量

    public Cylinder(View view) {
        super(view);

        ovalBottom = new Oval(mView);
        ovalTop = new Oval(mView, height);
        ArrayList<Float> pos = new ArrayList<>();
        float angDegSpan = 360f / n;
        for (float i = 0; i < 360 + angDegSpan; i += angDegSpan) {
            pos.add((float) (radius * Math.sin(i * Math.PI / 180f)));
            pos.add((float) (radius * Math.cos(i * Math.PI / 180f)));
            pos.add(height);
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
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        mProgram = ShaderUtils.createProgram(mView.getResources(), "vshader/Cone.sh", "fshader/Cone.sh");
        ovalBottom.onSurfaceCreated(gl, config);
        ovalTop.onSurfaceCreated(gl, config);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //计算宽高比
        float ratio = (float) width / height;
        //设置透视投影
        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 20);
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 1.0f, -10.0f, -4.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glUseProgram(mProgram);
        int mMatrix = GLES30.glGetUniformLocation(mProgram, "vMatrix");
        GLES30.glUniformMatrix4fv(mMatrix, 1, false, mMVPMatrix, 0);
        int mPositionHandle = GLES30.glGetAttribLocation(mProgram, "vPosition");
        GLES30.glEnableVertexAttribArray(mPositionHandle);
        GLES30.glVertexAttribPointer(mPositionHandle, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, vSize);
        GLES30.glDisableVertexAttribArray(mPositionHandle);
        ovalBottom.setMatrix(mMVPMatrix);
        ovalBottom.onDrawFrame(gl);
        ovalTop.setMatrix(mMVPMatrix);
        ovalTop.onDrawFrame(gl);
    }
}
