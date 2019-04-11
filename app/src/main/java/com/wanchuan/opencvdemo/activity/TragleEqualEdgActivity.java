package com.wanchuan.opencvdemo.activity;

import android.opengl.GLES30;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wanchuan.opencvdemo.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 等腰直角三角形绘制
 */
public class TragleEqualEdgActivity extends AppCompatActivity implements GLSurfaceView.Renderer {

    // 顶点着色器
//    private String vertexShaderCode = "attribute vec4 vPosition;\n" +
//            "uniform mat4 vMatrix;\n" +
//            "void main() {\n" +
//            "    gl_Position = vMatrix*vPosition;\n" +
//            "}";
    // 顶点着色器,带点颜色
    private String vertexShaderCode = "attribute vec4 vPosition;\n" +
            "uniform mat4 vMatrix;\n" +
            "varying  vec4 vColor;\n" +
            "attribute vec4 aColor;\n" +
            "void main() {\n" +
            "  gl_Position = vMatrix*vPosition;\n" +
            "  vColor=aColor;\n" +
            "}";
    // 片元着色器
    private String fragmentShaderCode = "precision mediump float;\n" +
            " varying vec4 vColor;\n" +
            " void main() {\n" +
            "     gl_FragColor = vColor;\n" +
            " }";

    private float triangleCoords[] = {
            0.5f, 0.5f, 0.0f, // top
            -0.5f, -0.5f, 0.0f, // bottom left
            0.5f, -0.5f, 0.0f  // bottom right
    };


    static final int COORDS_PER_VERTEX = 3;

    //    float color[] = {1.0f, 1.0f, 1.0f, 1.0f}; //白色
    float color[] = {  // 每个顶点的颜色
            0.0f, 1.0f, 0.0f, 1.0f, // 绿色
            1.0f, 0.0f, 0.0f, 1.0f,  // 红色
            0.0f, 0.0f, 1.0f, 1.0f  // 蓝色
    };

    private int mProgram;
    private FloatBuffer vertexBuffer;
    //顶点个数
    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    //顶点之间的偏移量
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 每个顶点四个字节
    private GLSurfaceView gl_sv;

    private float[] mViewMatrix = new float[16];
    private float[] mProjectMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];
    private FloatBuffer colorBuffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_gl_demo1);
        gl_sv = findViewById(R.id.gl_sv);
        init();
    }

    private void init() {
        gl_sv.setEGLContextClientVersion(2);
        gl_sv.setRenderer(this);
        gl_sv.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
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
        GLES30.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        //申请底层空间
        ByteBuffer bb = ByteBuffer.allocateDirect(
                triangleCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        //将坐标数据转换为FloatBuffer，用以传入给OpenGL ES程序
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(triangleCoords);
        vertexBuffer.position(0);
        // 申请底层空间
        ByteBuffer dd = ByteBuffer.allocateDirect(color.length * 4);
        dd.order(ByteOrder.nativeOrder());

        colorBuffer = dd.asFloatBuffer();
        colorBuffer.put(color);
        colorBuffer.position(0);

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
        //计算宽高比
        float ratio = (float) width / height;
        //设置透视投影
        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 7.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //将程序加入到OpenGLES2.0环境
        GLES30.glUseProgram(mProgram);
        //获取变换矩阵vMatrix成员句柄
        int mMatrixHandler = GLES30.glGetUniformLocation(mProgram, "vMatrix");
        //指定vMatrix的值
        GLES30.glUniformMatrix4fv(mMatrixHandler, 1, false, mMVPMatrix, 0);
        //获取顶点着色器的vPosition成员句柄
        int mPositionHandle = GLES30.glGetAttribLocation(mProgram, "vPosition");
        // 获取片元着色器的句柄
        int aColorHandler = GLES30.glGetAttribLocation(mProgram, "aColor");

        //启用三角形顶点的句柄
        GLES30.glEnableVertexAttribArray(mPositionHandle);
        //准备三角形的坐标数据
        GLES30.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES30.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        //启用三角形颜色的句柄
        GLES30.glEnableVertexAttribArray(aColorHandler);
        //准备三角形的颜色数据
        GLES30.glVertexAttribPointer(aColorHandler, 4,
                GLES30.GL_FLOAT, false,
                0, colorBuffer);
        //获取片元着色器的vColor成员的句柄
//        int mColorHandle = GLES30.glGetUniformLocation(mProgram, "vColor");
        //设置绘制三角形的颜色
//        GLES30.glUniform4fv(mColorHandle, 1, color, 0);
        //绘制三角形
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vertexCount);
        //禁止顶点数组的句柄
        GLES30.glDisableVertexAttribArray(mPositionHandle);
    }
}
