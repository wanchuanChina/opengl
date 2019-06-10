/*
 *
 * TextureUtils.java
 * 
 * Created by Wuwang on 2016/12/23
 * Copyright © 2016年 深圳哎吖科技. All rights reserved.
 */
package com.wanchuan.opencvdemo.utils;

import android.graphics.Bitmap;
import android.opengl.GLES30;

/**
 * Description:
 */
public enum EasyGlUtils {
    ;
    EasyGlUtils(){

    }

    public static void useTexParameter(){
        //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST);
        //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);
        //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_CLAMP_TO_EDGE);
        //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE);
    }

    public static void useTexParameter(int gl_wrap_s,int gl_wrap_t,int gl_min_filter,
                                       int gl_mag_filter){
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S,gl_wrap_s);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T,gl_wrap_t);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER,gl_min_filter);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER,gl_mag_filter);
    }

    public static void genTexturesWithParameter(int size,int[] textures,int start,
                             int gl_format,int width,int height){
        GLES30.glGenTextures(size, textures, start);
        for (int i = 0; i < size; i++) {
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textures[i]);
            GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0,gl_format, width, height,
                0, gl_format, GLES30.GL_UNSIGNED_BYTE, null);
            useTexParameter();
        }
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,0);
    }

    public static void bindFrameTexture(int frameBufferId,int textureId){
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBufferId);
        GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0,
            GLES30.GL_TEXTURE_2D, textureId, 0);
    }

    public static void unBindFrameBuffer(){
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER,0);
    }

}
