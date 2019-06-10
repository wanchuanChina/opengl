package com.wanchuan.opencvdemo.image.render;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.wanchuan.opencvdemo.utils.ShaderUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public abstract class AFilter implements GLSurfaceView.Renderer {

    private Context mContext;
    private int mProgram;
    private int glHPosition;
    private int glHTexture;
    private int glHCoordinate;
    private int glHMatrix;
    private int hIsHalf;
    private int glHUxy;
    private Bitmap mBitmap;  // 纹理图片

    private FloatBuffer bPos;
    private FloatBuffer bCoord;

    private int textureId;  // 纹理Id
    private boolean isHalf; // 是否处理一半

    private float uXY;

    private String vertex;
    private String fragment;
    private float[] mViewMatrix = new float[16];
    private float[] mProjectMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    private final float[] sPos = {  // 顶点坐标
            -1.0f, 1.0f,
            -1.0f, -1.0f,
            1.0f, 1.0f,
            1.0f, -1.0f
    };

    private final float[] sCoord = {  // 纹理坐标
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
    };

    /**
     * 构造函数
     *
     * @param context
     * @param vertex   顶点着色器
     * @param fragment 片元着色器
     */
    public AFilter(Context context, String vertex, String fragment) {
        mContext = context;
        this.vertex = vertex;
        this.fragment = fragment;

        ByteBuffer bb = ByteBuffer.allocateDirect(sPos.length * 4);
        bb.order(ByteOrder.nativeOrder());
        bPos = bb.asFloatBuffer();
        bPos.put(sPos);
        bPos.position(0);

        ByteBuffer cc = ByteBuffer.allocateDirect(sCoord.length * 4);
        cc.order(ByteOrder.nativeOrder());
        bCoord = cc.asFloatBuffer();
        bCoord.put(sCoord);
        bCoord.position(0);
    }


    public void setHalf(boolean half) {
        this.isHalf = half;
    }

    /**
     * 设置纹理图片
     *
     * @param buffer
     * @param width
     * @param height
     */
    public void setImageBuffer(int[] buffer, int width, int height) {
        mBitmap = Bitmap.createBitmap(buffer, width, height, Bitmap.Config.RGB_565);
    }

    public void setBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
    }

    public abstract void onDrawSet();

    public abstract void onDrawCreatedSet(int mProgram);


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // 设置背景色
        GLES30.glClearColor(1f, 1f, 1f, 1f);
        // 开启2D 纹理渲染
        GLES30.glEnable(GLES30.GL_TEXTURE_2D);
        // 获取着色器句柄
        mProgram = ShaderUtils.createProgram(mContext.getResources(), vertex, fragment);
        // 获取坐标点句柄
        glHPosition = GLES30.glGetAttribLocation(mProgram, "vPosition");
        // 获取纹理坐标句柄
        glHCoordinate = GLES30.glGetAttribLocation(mProgram, "vCoordinate");
        // 获取纹理句柄
        glHTexture = GLES30.glGetUniformLocation(mProgram, "vTexture");
        glHMatrix = GLES30.glGetUniformLocation(mProgram, "vMatrix");
        hIsHalf = GLES30.glGetUniformLocation(mProgram, "vIsHalf");
        glHUxy = GLES30.glGetUniformLocation(mProgram, "uXY");
        onDrawCreatedSet(mProgram);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
        int w = mBitmap.getWidth();
        int h = mBitmap.getHeight();

        float sWH = w / (float) h;  // 图片宽高比
        float sWidthHeight = width / (float) height; // 界面宽高比

        uXY = sWidthHeight;

        if (width > height) { // 横屏
            if (sWH > sWidthHeight) { // 图片宽高比大于界面宽高比，缩小图片显示比例
                Matrix.orthoM(mProjectMatrix, 0, -sWidthHeight * sWH, sWidthHeight * sWH, -1, 1, 3, 5);
            } else {
                Matrix.orthoM(mProjectMatrix, 0, -sWidthHeight / sWH, sWidthHeight / sWH, -1, 1, 3, 5);
            }
        } else {
            if (sWH > sWidthHeight) { // 图片宽高比大于界面宽高比，缩小图片显示比例
                Matrix.orthoM(mProjectMatrix, 0, -1, 1, -1 / sWidthHeight * sWH, 1 / sWidthHeight * sWH, 3, 5);
            } else {
                Matrix.orthoM(mProjectMatrix, 0, -1, 1, -sWH / sWidthHeight, sWH / sWidthHeight, 3, 5);
            }
        }

        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 5.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
        GLES30.glUseProgram(mProgram);
        onDrawSet();
        GLES30.glUniform1i(hIsHalf, isHalf ? 1 : 0);
        GLES30.glUniform1f(glHUxy, uXY);
        GLES30.glUniformMatrix4fv(glHMatrix, 1, false, mMVPMatrix, 0);
        GLES30.glEnableVertexAttribArray(glHPosition);
        GLES30.glEnableVertexAttribArray(glHCoordinate);
        GLES30.glUniform1i(glHTexture, 0);
        textureId = createTexture();
        //传入顶点坐标
        GLES30.glVertexAttribPointer(glHPosition, 2, GLES30.GL_FLOAT, false, 0, bPos);
        //传入纹理坐标
        GLES30.glVertexAttribPointer(glHCoordinate, 2, GLES30.GL_FLOAT, false, 0, bCoord);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4);
    }

    private int createTexture() {
        int[] texture = new int[1];
        if (mBitmap != null && !mBitmap.isRecycled()) {
            // 生成纹理
            GLES30.glGenTextures(1, texture, 0);
            // 绑定纹理
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texture[0]);
            //设置缩小过滤为使纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);

//            作为纹理向量时，用stpq表示分量，三维用stp表示分量，二维用st表示分量

            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
            //根据以上指定的参数，生成一个2D纹理
            GLUtils.texImage2D(GLES30.GL_TEXTURE_2D,0,mBitmap,0);
            return texture[0];
        }
        return 0;
    }
}
