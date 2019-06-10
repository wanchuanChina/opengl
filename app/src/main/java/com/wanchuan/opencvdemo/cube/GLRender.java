package com.wanchuan.opencvdemo.cube;

import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.view.KeyEvent;

import com.wanchuan.opencvdemo.R;
import com.wanchuan.opencvdemo.utils.GlImage;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLRender implements GLSurfaceView.Renderer {

    private boolean key = true;
    public float xrot = 0.0f;
    public float yrot = 0.0f;
    private float xspeed, yspeed;
    private float z = -5.0f;
    private int one = 0x10000;//void glVertexPointer(GLint size,
    //GLenum type,
    //GLsizei stride,
    //const GLvoid * pointer)这里不同的数据类型含义不同，如果选择GL_FIXED，那么0x10000表示单位长度，如果选择GL_FLOAT那么1.0f表示单位度。)

    // 光线参数
    private FloatBuffer lightAmbient;
    private FloatBuffer lightDiffuse;
    private FloatBuffer lightPosition;

    private float[] light1 = new float[]{0.5f, 0.5f, 0.5f, 1.0f};
    private float[] light2 = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
    private float[] light3 = new float[]{0.0f, 0.0f, 2.0f, 1.0f};
    private int[] texture;
    private IntBuffer vertices;
    private IntBuffer normals;
    private IntBuffer texCoords;
    private int[] verticesdata = new int[]{
            -one, -one, one,
            one, -one, one,
            one, one, one,
            -one, one, one,

            -one, -one, -one,
            -one, one, -one,
            one, one, -one,
            one, -one, -one,

            -one, one, -one,
            -one, one, one,
            one, one, one,
            one, one, -one,

            -one, -one, -one,
            one, -one, -one,
            one, -one, one,
            -one, -one, one,

            one, -one, -one,
            one, one, -one,
            one, one, one,
            one, -one, one,

            -one, -one, -one,
            -one, -one, one,
            -one, one, one,
            -one, one, -one,
    };

    private int[] normalsdata = new int[]{
            0, 0, one,
            0, 0, one,
            0, 0, one,
            0, 0, one,

            0, 0, one,
            0, 0, one,
            0, 0, one,
            0, 0, one,

            0, one, 0,
            0, one, 0,
            0, one, 0,
            0, one, 0,

            0, -one, 0,
            0, -one, 0,
            0, -one, 0,
            0, -one, 0,

            one, 0, 0,
            one, 0, 0,
            one, 0, 0,
            one, 0, 0,

            -one, 0, 0,
            -one, 0, 0,
            -one, 0, 0,
            -one, 0, 0,};

    private int[] texCoordsdata = new int[]{
            one, 0, 0, 0, 0, one, one, one,
            0, 0, 0, one, one, one, one, 0,
            one, one, one, 0, 0, 0, 0, one,
            0, one, one, one, one, 0, 0, 0,
            0, 0, 0, one, one, one, one, 0,
            one, 0, 0, 0, 0, one, one, one,};
    private ByteBuffer indices1 = ByteBuffer.wrap(new byte[]{
            0, 1, 3, 2,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
    });
    private ByteBuffer indices2 = ByteBuffer.wrap(new byte[]{
            0, 0, 0, 0,
            4, 5, 7, 6,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,});
    private ByteBuffer indices3 = ByteBuffer.wrap(new byte[]{
            0, 0, 0, 0,
            0, 0, 0, 0,
            8, 9, 11, 10,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,});
    private ByteBuffer indices4 = ByteBuffer.wrap(new byte[]{
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            12, 13, 15, 14,
            0, 0, 0, 0,
            0, 0, 0, 0,});
    private ByteBuffer indices5 = ByteBuffer.wrap(new byte[]{
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            16, 17, 19, 18,
            0, 0, 0, 0,});
    private ByteBuffer indices6 = ByteBuffer.wrap(new byte[]{
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            20, 21, 23, 22,});


    public GLRender() {
        // 注意构造函数中那些Buffer的创建方式。在这个地方，
        // 不能直接使用FloatBuffer/IntBuffer 的wrap() method。
        // 直接用这个method创建出来的buffer会导致JE:
        vertices = bufferUtil(verticesdata);
        texCoords = bufferUtil(texCoordsdata);
        normals = bufferUtil(normalsdata);
        lightAmbient = bufferUtil(light1);
        lightDiffuse = bufferUtil(light2);
        lightPosition = bufferUtil(light3);
    }


    // 通过事件分发，提供方法给View层，动态onDrawFram(),就是3D物体跟着转
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        key = !key;
        return false;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // 关闭在写入颜色缓冲区之前抖动颜色组件。
        gl.glDisable(GL10.GL_DITHER);
        // 告诉系统对透视进行修正
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
        // 黑色背景
        gl.glClearColor(0, 0, 0, 0);
        // 根据在窗口坐标中的弯曲来剔除多边形。
        gl.glEnable(GL10.GL_CULL_FACE);
        // 启用阴影平滑
        gl.glShadeModel(GL10.GL_SMOOTH);
        // 启用深度测试
        gl.glEnable(GL10.GL_DEPTH_TEST);

        // 设置光线,,1.0f为全光线，a=50%
        gl.glColor4f(1.0f, 1.0f, 1.0f, 0.5f);
        // 基于源象素alpha通道值的半透明混合函数
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE);

        // 纹理相关
        GLTextureUtil(gl);

        // 深度测试相关
        gl.glClearDepthf(1.0f);
        gl.glDepthFunc(GL10.GL_LEQUAL);
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
        gl.glEnable(GL10.GL_TEXTURE_2D);

        // 设置环境光
        gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_AMBIENT, lightAmbient);

        // 设置漫射光
        gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_DIFFUSE, lightDiffuse);

        // 设置光源位置
        gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_POSITION, lightPosition);

        // 开启一号光源
        gl.glEnable(GL10.GL_LIGHT1);

        // 开启混合
        gl.glEnable(GL10.GL_BLEND);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        float ratio = (float) width / height;
        // 设置OpenGL场景的大小
        gl.glViewport(0, 0, width, height);
        // 设置投影矩阵
        gl.glMatrixMode(GL10.GL_PROJECTION);
        // 重置投影矩阵
        gl.glLoadIdentity();
        // 设置视口的大小
        gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
        // 选择模型观察矩阵
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        // 重置模型观察矩阵
        gl.glLoadIdentity();

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // 清除屏幕和深度缓存
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        // 重置当前的模型观察矩阵
        gl.glLoadIdentity();

        gl.glEnable(GL10.GL_LIGHTING);

        gl.glTranslatef(0.0f, 0.0f, z);

        // 设置旋转
        gl.glRotatef(xrot, 0.0f, 1.0f, 0.0f);
        gl.glRotatef(yrot, 1.0f, 0.0f, 0.0f);
        // 设置法线
        gl.glNormalPointer(GL10.GL_FIXED, 0, normals);
        // size：指定了每个顶点对应的坐标个数，只能是2,3,4中的一个，默认值是4
        // type：指定了数组中每个顶点坐标的数据类型，可取常量:GL_BYTE, GL_SHORT,GL_FIXED,GL_FLOAT;
        // stride:指定了连续顶点间的字节排列方式，如果为0，数组中的顶点就会被认为是按照紧凑方式排列的，默认值为0
        // pointer:制订了数组中第一个顶点的首地址，默认值为0，对于我们的android，大家可以不用去管什么地址的，一般给一个int就可以了
        gl.glVertexPointer(3, GL10.GL_FIXED, 0, vertices);
        // 设置顶点数组为纹理坐标缓存
        gl.glTexCoordPointer(2, GL10.GL_FIXED, 0, texCoords);

        // glEnableClientState - glDisableClientState这对。
        // 它们的区别是通知的具体对象在概念上不一样——分别是服务端和客户端。
        // 事实上我也无法很清楚地解释清楚，反正电脑上的具体程序，包括它用到
        // 的内存等等看作客户端，把你电脑里面的——显卡里的OpenGL“模块”，乃
        // 至整张拥有OpenGL流水线、硬件实现OpenGL功能的显卡，作为服务端。
        // 它们各自维护一些“状态”，glEnable 等是直接维护流水线处理相关的状态的，
        // glEnableClientState 维护的则是进入流水线前的状态
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        // 绘制四边形

        BindElementsWithTexture(gl);
        // 修改旋转角度
        // xrot+=0.3f;
        // yrot+=0.2f;

        // 混合开关
        if (key) {
            gl.glEnable(GL10.GL_BLEND); // 打开混合
            gl.glDisable(GL10.GL_DEPTH_TEST); // 关闭深度测试
        } else {
            gl.glDisable(GL10.GL_BLEND); // 关闭混合
            gl.glEnable(GL10.GL_DEPTH_TEST); // 打开深度测试
        }

    }

    // 绘制四边形和Texture元素绑定
    public void BindElementsWithTexture(GL10 gl) {
        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[0]);
        gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 4, GL10.GL_UNSIGNED_BYTE,
                indices1);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[1]);
        gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 8, GL10.GL_UNSIGNED_BYTE,
                indices2);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[2]);
        gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 12, GL10.GL_UNSIGNED_BYTE,
                indices3);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[3]);
        gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 16, GL10.GL_UNSIGNED_BYTE,
                indices4);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[4]);
        gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 20, GL10.GL_UNSIGNED_BYTE,
                indices5);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[5]);
        gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 24, GL10.GL_UNSIGNED_BYTE,
                indices6);

        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
    }


    // Texture纹理初始化
    public void GLTextureUtil(GL10 gl) {

        IntBuffer textureBuffer = IntBuffer.allocate(6);
        gl.glGenTextures(6, textureBuffer);
        texture = textureBuffer.array();

        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[0]);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, GlImage.createBitmap(R.mipmap.a), 0);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
                GL10.GL_NEAREST);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
                GL10.GL_NEAREST);

        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[1]);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, GlImage.createBitmap(R.mipmap.b), 0);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
                GL10.GL_NEAREST);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
                GL10.GL_NEAREST);

        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[2]);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, GlImage.createBitmap(R.mipmap.c), 0);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
                GL10.GL_NEAREST);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
                GL10.GL_NEAREST);

        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[3]);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, GlImage.createBitmap(R.mipmap.d), 0);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
                GL10.GL_NEAREST);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
                GL10.GL_NEAREST);

        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[4]);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, GlImage.createBitmap(R.mipmap.e), 0);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
                GL10.GL_NEAREST);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
                GL10.GL_NEAREST);

        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[5]);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, GlImage.createBitmap(R.mipmap.f), 0);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
                GL10.GL_NEAREST);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
                GL10.GL_NEAREST);
    }


    public FloatBuffer bufferUtil(float[] arr) {
        FloatBuffer buffer;

        ByteBuffer qbb = ByteBuffer.allocateDirect(arr.length * 6);
        qbb.order(ByteOrder.nativeOrder());

        buffer = qbb.asFloatBuffer();
        buffer.put(arr);
        buffer.position(0);

        return buffer;
    }

    public IntBuffer bufferUtil(int[] arr) {
        IntBuffer buffer;

        ByteBuffer qbb = ByteBuffer.allocateDirect(arr.length * 6);
        qbb.order(ByteOrder.nativeOrder());

        buffer = qbb.asIntBuffer();
        buffer.put(arr);
        buffer.position(0);

        return buffer;
    }

}
