package com.wanchuan.opencvdemo.activity;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;

import com.wanchuan.opencvdemo.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 立方体绘制
 */
public class CubeActivity extends AppCompatActivity implements GLSurfaceView.Renderer {

    // 顶点着色器
    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "uniform mat4 vMatrix;" +
                    "varying  vec4 vColor;" +
                    "attribute vec4 aColor;" +
                    "void main() {" +
                    "  gl_Position = vMatrix*vPosition;" +
                    "  vColor=aColor;" +
                    "}";
    // 片元着色器
    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "varying vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    final float cubePositions[] = {
            -1.0f, 1.0f, 1.0f,    //正面左上0
            -1.0f, -1.0f, 1.0f,   //正面左下1
            1.0f, -1.0f, 1.0f,    //正面右下2
            1.0f, 1.0f, 1.0f,     //正面右上3
            -1.0f, 1.0f, -1.0f,    //反面左上4
            -1.0f, -1.0f, -1.0f,   //反面左下5
            1.0f, -1.0f, -1.0f,    //反面右下6
            1.0f, 1.0f, -1.0f,     //反面右上7
    };

    //    正面由032和021两个三角形组成，其他面诸如此类拆分，得到索引数组

    final short index[]={
            0,3,2,0,2,1,    //正面
            0,1,5,0,5,4,    //左面
            0,7,3,0,4,7,    //上面
            6,7,4,6,4,5,    //后面
            6,3,7,6,2,3,    //右面
            6,5,1,6,1,2     //下面
    };

    float color[] = {
            1f, 0f, 1f, 0.2f,
            1f, 1f, 0f, 0.2f,
            1f, 1f, 1f, 0.2f,
            1f, 0f, 1f, 0.2f,
            0f, 1f, 1f, 0.2f,
            0f, 0f, 1f, 0.2f,
            1f, 0f, 1f, 0.2f,
            1f, 1f, 0f, 0.2f,
    };


    static final int COORDS_PER_VERTEX = 3;

    private int mProgram;
    private FloatBuffer vertexBuffer;
    //顶点个数
    private final int vertexCount = cubePositions.length / COORDS_PER_VERTEX;

    //顶点之间的偏移量
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 每个顶点四个字节
    private GLSurfaceView gl_sv;

    private float[] mViewMatrix = new float[16];
    private float[] mProjectMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];
    private FloatBuffer colorBuffer;
    private ShortBuffer indexBuffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_gl_demo1);
        gl_sv = findViewById(R.id.gl_sv);
        init();
    }

    private void init() {
        gl_sv.setEGLContextClientVersion(3);
        gl_sv.setRenderer(this);
//        gl_sv.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        gl_sv.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gl_sv.onPause();
    }


    public int loadShader(int type, String shaderCode) {
        //根据type创建顶点着色器或者片元着色器
        int shader = GLES30.glCreateShader(type);
        //将资源加入到着色器中，并编译
        GLES30.glShaderSource(shader, shaderCode);
        GLES30.glCompileShader(shader);
        return shader;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //将背景设置为灰色
        GLES30.glClearColor(1f, 1f, 1f, 1.0f);
        GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT);

        //开启深度测试
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        // 下面将绘制半透明物体了，因此将深度缓冲设置为只读
        GLES30.glDepthMask(false);

        // 启动混合并设置混合因子，变成半透明
        GLES30.glEnable(GLES30.GL_BLEND);
        GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA);

        //申请顶点底层空间
        ByteBuffer bb = ByteBuffer.allocateDirect(
                cubePositions.length * 4);
        bb.order(ByteOrder.nativeOrder());
        //将坐标数据转换为FloatBuffer，用以传入给OpenGL ES程序
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(cubePositions);
        vertexBuffer.position(0);

        ByteBuffer dd = ByteBuffer.allocateDirect(
                color.length * 4);
        dd.order(ByteOrder.nativeOrder());
        colorBuffer = dd.asFloatBuffer();
        colorBuffer.put(color);
        colorBuffer.position(0);

        ByteBuffer cc = ByteBuffer.allocateDirect(index.length * 2);
        cc.order(ByteOrder.nativeOrder());
        indexBuffer = cc.asShortBuffer();
        indexBuffer.put(index);
        indexBuffer.position(0);

        int vertexShader = loadShader(GLES30.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = loadShader(GLES30.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        //创建一个空的OpenGLES程序
        mProgram = GLES30.glCreateProgram();
        //将顶点着色器加入到程序
        GLES30.glAttachShader(mProgram, vertexShader);
        //将片元着色器加入到程序中
        GLES30.glAttachShader(mProgram, fragmentShader);
        //连接到着色器程序
        GLES30.glLinkProgram(mProgram);


    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
        //计算宽高比
        float ratio = (float) width / height;
        //设置透视投影
        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 20);
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 5.0f, 5.0f, 10.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);
    }

    private float[] mRotationMatrix = new float[16];

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);


        //新的结合矩阵
        float[] scratch = new float[16];
        //旋转的角度
        long time = SystemClock.uptimeMillis() % 4000L;
        float angle = 0.09f * ((int) time);
        Matrix.setRotateM(mRotationMatrix, 0, angle, -1f, 0, 0);  // -1 为逆时针，+1 为顺时针

        //把旋转矩阵和之前的结合矩阵mMVPMatrix结合生成新的结合矩阵scratch
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);

        //将程序加入到OpenGLES2.0环境
        GLES30.glUseProgram(mProgram);
        //获取变换矩阵vMatrix成员句柄
        int mMatrixHandler = GLES30.glGetUniformLocation(mProgram, "vMatrix");
        //指定vMatrix的值
        GLES30.glUniformMatrix4fv(mMatrixHandler, 1, false, scratch, 0);
        //获取顶点着色器的vPosition成员句柄
        int mPositionHandle = GLES30.glGetAttribLocation(mProgram, "vPosition");

        //启用三角形顶点的句柄
        GLES30.glEnableVertexAttribArray(mPositionHandle);
        //准备三角形的坐标数据
        GLES30.glVertexAttribPointer(mPositionHandle, 3,
                GLES30.GL_FLOAT, false,
                0, vertexBuffer);

        //获取片元着色器的vColor成员的句柄
        int mColorHandle = GLES30.glGetAttribLocation(mProgram, "aColor");
//        //设置绘制三角形的颜色
//        GLES30.glUniform4fv(mColorHandle, 1, color, 0);
        GLES30.glEnableVertexAttribArray(mColorHandle);
        GLES30.glVertexAttribPointer(mColorHandle, 4,
                GLES30.GL_FLOAT, false,
                0, colorBuffer);

        GLES30.glDrawElements(GLES30.GL_TRIANGLES, index.length, GLES30.GL_UNSIGNED_SHORT, indexBuffer);

        GLES30.glDepthMask(true); // // 完成半透明物体的绘制，将深度缓冲区恢复为可读可写的形式

        //禁止顶点数组的句柄
        GLES30.glDisableVertexAttribArray(mPositionHandle);
    }
}
