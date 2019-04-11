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

public class Ball extends Shape {

    private float step = 5f;
    private FloatBuffer vertexBuffer;
    private int vSize;

    private int mProgram;
    private float[] mViewMatrix = new float[16];
    private float[] mProjectMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];


    public Ball(View view) {
        super(view);

        float[] ballPos = createBallPos();
        ByteBuffer buffer = ByteBuffer.allocateDirect(ballPos.length * 4);
        buffer.order(ByteOrder.nativeOrder());
        vertexBuffer = buffer.asFloatBuffer();
        vertexBuffer.put(ballPos);
        vertexBuffer.position(0);

        vSize = ballPos.length / 3;

    }


    private float[] createBallPos() {
        //球以(0,0,0)为中心，以R为半径，则球上任意一点的坐标为
        // ( R * cos(a) * sin(b),y0 = R * sin(a),R * cos(a) * cos(b))
        // 其中，a为圆心到点的线段与xz平面的夹角，b为圆心到点的线段在xz平面的投影与z轴的夹角

        ArrayList<Float> pos = new ArrayList<>();
        float r1, r2;
        float h1, h2;
        float sin, cos;
        for (int i = -90; i < 90 + step; i += step) {
            r1 = (float) Math.cos(i * Math.PI / 180);
            r2 = (float) Math.cos((i + step) * Math.PI / 180);
            h1 = (float) Math.sin(i * Math.PI / 180f);
            h2 = (float) Math.sin((i + step) * Math.PI / 180f);
            // 固定维度，360度旋转遍历一圈
            float setp2 = step * 2;

            for (int j = 0; j < 360 + step; j += setp2) {
                cos = (float) Math.cos(j * Math.PI / 180f);
                sin = (float) -Math.sin(j * Math.PI / 180f);

                pos.add(r2 * cos);
                pos.add(h2);
                pos.add(r2 * sin);
                pos.add(r1 * cos);
                pos.add(h1);
                pos.add(r1 * sin);

            }
        }
        float f[] = new float[pos.size()];
        for (int i = 0; i < f.length; i++) {
            f[i] = pos.get(i);
        }
        return f;

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        mProgram = ShaderUtils.createProgram(mView.getResources(), "vshader/Ball.sh", "fshader/Cone.sh");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // 计算宽高比
        float ratio = (float) width / height;
        // 设置透视投影
        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1f, 1f, 3, 20);
        // 设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 1f, -10f, -4f, 0f, 0f, 0f, 0f, 1f, 0f);
        // 计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glUseProgram(mProgram);
        // 获取矩阵句柄
        int vMatrix = GLES30.glGetUniformLocation(mProgram, "vMatrix");
        GLES30.glUniformMatrix4fv(vMatrix, 1, false, mMVPMatrix, 0);
        int vPosition = GLES30.glGetAttribLocation(mProgram, "vPosition");
        GLES30.glEnableVertexAttribArray(vPosition);
        GLES30.glVertexAttribPointer(vPosition, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, vSize);
        GLES30.glDisableVertexAttribArray(vPosition);


    }
}
