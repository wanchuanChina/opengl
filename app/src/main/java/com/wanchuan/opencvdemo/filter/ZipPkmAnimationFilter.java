package com.wanchuan.opencvdemo.filter;

import android.content.res.Resources;
import android.opengl.ETC1;
import android.opengl.ETC1Util;
import android.opengl.GLES30;
import android.util.Log;

import com.wanchuan.opencvdemo.etc.ZipPkmReader;
import com.wanchuan.opencvdemo.utils.MatrixUtils;

import java.nio.ByteBuffer;

public class ZipPkmAnimationFilter extends AFilter {

    private boolean isPlay=false;
    private ByteBuffer emptyBuffer;
    private int width,height;
    private int type=MatrixUtils.TYPE_CENTERINSIDE;
    public static final int TYPE=0x01;

    private NoFilter mBaseFilter;

    private int[] texture;

    private ZipPkmReader mPkmReader;
    private int mGlHAlpha;

    public ZipPkmAnimationFilter(Resources mRes) {
        super(mRes);
        mBaseFilter=new NoFilter(mRes);
        mPkmReader=new ZipPkmReader(mRes.getAssets());
    }

    @Override
    protected void onCreate() {
        createProgramByAssetsFile("shader/pkm_mul.vert","shader/pkm_mul.frag");
        texture=new int[2];
        createEtcTexture(texture);
        setTextureId(texture[0]);
        mGlHAlpha= GLES30.glGetUniformLocation(mProgram,"vTextureAlpha");
        mBaseFilter.create();
    }

    @Override
    protected void onClear() {

    }

    @Override
    protected void onSizeChanged(int width, int height) {
        emptyBuffer=ByteBuffer.allocateDirect(ETC1.getEncodedDataSize(width,height));
        this.width=width;
        this.height=height;
        GLES30.glEnable(GLES30.GL_BLEND);
        GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA,GLES30.GL_ONE_MINUS_SRC_ALPHA);
        mBaseFilter.setSize(width, height);
    }

    @Override
    public float[] getMatrix() {
        return mBaseFilter.getMatrix();
    }

    @Override
    protected void onBindTexture() {
        ETC1Util.ETC1Texture t=mPkmReader.getNextTexture();
        ETC1Util.ETC1Texture tAlpha=mPkmReader.getNextTexture();
        Log.e("wuwang","is ETC null->"+(t==null));
        if(t!=null&&tAlpha!=null){
            MatrixUtils.getMatrix(super.getMatrix(),MatrixUtils.TYPE_FITEND,t.getWidth(),t.getHeight(),width,height);
            MatrixUtils.flip(super.getMatrix(),false,true);
            onSetExpandData();
            GLES30.glActiveTexture(GLES30.GL_TEXTURE0+getTextureType());
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,texture[0]);
            ETC1Util.loadTexture(GLES30.GL_TEXTURE_2D,0,0,GLES30.GL_RGB,GLES30
                    .GL_UNSIGNED_SHORT_5_6_5,t);
            GLES30.glUniform1i(mHTexture,getTextureType());

            GLES30.glActiveTexture(GLES30.GL_TEXTURE1+getTextureType());
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,texture[1]);
            ETC1Util.loadTexture(GLES30.GL_TEXTURE_2D,0,0,GLES30.GL_RGB,GLES30
                    .GL_UNSIGNED_SHORT_5_6_5,tAlpha);
            GLES30.glUniform1i(mGlHAlpha,1+getTextureType());
        }else{
            if(mPkmReader!=null){
                mPkmReader.close();
                mPkmReader.open();
            }
            onSetExpandData();
            GLES30.glActiveTexture(GLES30.GL_TEXTURE0+getTextureType());
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,texture[0]);
            ETC1Util.loadTexture(GLES30.GL_TEXTURE_2D,0,0,GLES30.GL_RGB,GLES30
                    .GL_UNSIGNED_SHORT_5_6_5,new ETC1Util.ETC1Texture(width,height,emptyBuffer));
            GLES30.glUniform1i(mHTexture,getTextureType());

            GLES30.glActiveTexture(GLES30.GL_TEXTURE1+getTextureType());
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,texture[1]);
            ETC1Util.loadTexture(GLES30.GL_TEXTURE_2D,0,0,GLES30.GL_RGB,GLES30
                    .GL_UNSIGNED_SHORT_5_6_5,new ETC1Util.ETC1Texture(width,height,emptyBuffer));
            GLES30.glUniform1i(mGlHAlpha,1+getTextureType());
            isPlay=false;
        }
    }

    @Override
    public void draw() {
        if(getTextureId()!=0){
            mBaseFilter.setTextureId(getTextureId());
            mBaseFilter.draw();
        }
        GLES30.glViewport(100,0,width/6,height/6);
        super.draw();
        GLES30.glViewport(0,0,width,height);
    }

    @Override
    public void setInt(int type, int... params) {
        if(type==TYPE){
            this.type=params[0];
        }
        super.setInt(type, params);
    }

    public void setAnimation(String path){
        mPkmReader.setZipPath(path);
        mPkmReader.open();
    }


    @Override
    protected void finalize() throws Throwable {
        if(mPkmReader!=null){
            mPkmReader.close();
        }
        super.finalize();
    }

    private void createEtcTexture(int[] texture){
        //生成纹理
        GLES30.glGenTextures(2,texture,0);
        for (int i=0;i<texture.length;i++){
            //生成纹理
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,texture[i]);
            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST);
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_CLAMP_TO_EDGE);
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE);
            //根据以上指定的参数，生成一个2D纹理
        }
    }

}
