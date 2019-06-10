package com.wanchuan.opencvdemo.filter;

import android.content.res.Resources;
import android.opengl.GLES11Ext;
import android.opengl.GLES30;

import java.util.Arrays;

public class OesFilter extends AFilter {


    private int mHCoordMatrix;
    private float[] mCoordMatrix = Arrays.copyOf(OM, 16);

    public OesFilter(Resources mRes) {
        super(mRes);
    }


    @Override
    protected void onCreate() {
        // 获取program 句柄
        createProgramByAssetsFile("shader/oes_base_vertex.sh", "shader/oes_base_fragment.sh");
        mHCoordMatrix = GLES30.glGetUniformLocation(mProgram, "vCoordMatrix");
    }

    public void setCoordMatrix(float[] matrix) {
        mCoordMatrix = matrix;
    }

    @Override
    protected void onSetExpandData() {
        super.onSetExpandData();
        GLES30.glUniformMatrix4fv(mHCoordMatrix, 1, false, mCoordMatrix, 0);
    }

    @Override
    protected void onBindTexture() {
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0 + getTextureType());
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, getTextureId());
        GLES30.glUniform1i(mHTexture, getTextureType());
    }

    @Override
    protected void onSizeChanged(int width, int height) {

    }
}