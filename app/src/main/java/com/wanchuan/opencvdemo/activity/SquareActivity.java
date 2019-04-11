package com.wanchuan.opencvdemo.activity;

import android.opengl.GLES20;
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

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 等腰直角三角形绘制
 */
public class SquareActivity extends AppCompatActivity implements GLSurfaceView.Renderer {

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
//    private String fragmentShaderCode = "precision mediump float;\n" +
//            " uniform vec4 vColor;\n" +
//            " void main() {\n" +
//            "     gl_FragColor = vColor;\n" +
//            " }";
    private String fragmentShaderCode = "precision mediump float;\n" +
            " varying vec4 vColor;\n" +
            " void main() {\n" +
            "     gl_FragColor = vColor;\n" +
            " }";

    private float triangleCoords[] = {
            -0.5f, 0.5f, 0.0f, // top left
            -0.5f, -0.5f, 0.0f, // bottom left
            0.5f, -0.5f, 0.0f, // bottom right
            0.5f, 0.5f, 0.0f  // top right
    };


    static final int COORDS_PER_VERTEX = 3;

//    float color[] = {1.0f, 1.0f, 1.0f, 1.0f}; //白色
    float color[] = {  // 每个顶点的颜色
            0.0f, 1.0f, 0.0f, 1.0f, // 绿色
            1.0f, 0.0f, 0.0f, 1.0f,  // 红色
            0.0f, 0.0f, 1.0f, 1.0f,  // 蓝色
            1.0f, 0.0f, 1.0f, 1.0f
    };

    static short index[] = {
            0, 1, 2, 0, 2, 3
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
    private ShortBuffer indexBuffer;

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
        // 注释掉之后就会不停渲染，就会出现动画效果
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
        GLES30.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        //申请顶点底层空间
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

        ByteBuffer cc = ByteBuffer.allocateDirect(index.length * 2); // 因为是short，所以*2
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
        GLES30.glViewport(0,0,width,height);
        //计算宽高比
        float ratio = (float) width / height;
        //设置透视投影
        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 7.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);
    }



    private float[] mRotationMatrix = new float[16];
    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT|GLES30.GL_DEPTH_BUFFER_BIT);


        //新的结合矩阵
        float[] scratch = new float[16];

        //旋转的角度
        long time = SystemClock.uptimeMillis() % 4000L;
        float angle = 0.09f * ((int) time);
        Matrix.setRotateM(mRotationMatrix, 0, angle, 0, 0, -1.0f);

        //把旋转矩阵和之前的结合矩阵mMVPMatrix结合生成新的结合矩阵scratch
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
        // 上面的代码为自动旋转

        //将程序加入到OpenGLES2.0环境
        GLES30.glUseProgram(mProgram);
        //获取变换矩阵vMatrix成员句柄
        int mMatrixHandler = GLES30.glGetUniformLocation(mProgram, "vMatrix");
        //指定vMatrix的值
        GLES30.glUniformMatrix4fv(mMatrixHandler, 1, false, scratch, 0);


//        GLES30.glUniformMatrix4fv(mMatrixHandler, 1, false, mMVPMatrix, 0);
        //获取顶点着色器的vPosition成员句柄
        int mPositionHandle = GLES30.glGetAttribLocation(mProgram, "vPosition");

        //启用三角形顶点的句柄
        GLES30.glEnableVertexAttribArray(mPositionHandle);
        //准备三角形的坐标数据
        GLES30.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES30.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // 获取片元着色器的句柄
        int aColorHandler = GLES30.glGetAttribLocation(mProgram, "aColor");

        //启用颜色的句柄
        GLES30.glEnableVertexAttribArray(aColorHandler);
        //准备颜色数据
        GLES30.glVertexAttribPointer(aColorHandler, 4,
                GLES30.GL_FLOAT, false,
                0, colorBuffer);
        //获取片元着色器的vColor成员的句柄
//        int mColorHandle = GLES30.glGetUniformLocation(mProgram, "vColor");
//        //设置绘制三角形的颜色
//        GLES30.glUniform4fv(mColorHandle, 1, color, 0);
        //索引法绘制正方形
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, index.length, GLES30.GL_UNSIGNED_SHORT, indexBuffer);
        //禁止顶点数组的句柄
        GLES30.glDisableVertexAttribArray(mPositionHandle);
    }
}
