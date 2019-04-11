package com.wanchuan.opencvdemo.render;

import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.view.View;

import org.opencv.video.Video;

public abstract class Shape implements GLSurfaceView.Renderer {

    protected View mView;

    public Shape(View view){
        mView = view;
    }

    public int loadShader(int type, String shaderCode){
        //根据type创建顶点着色器或者片元着色器
        int shader = GLES30.glCreateShader(type);
        //将资源加入到着色器中，并编译
        GLES30.glShaderSource(shader, shaderCode);
        GLES30.glCompileShader(shader);
        return shader;
    }

}
