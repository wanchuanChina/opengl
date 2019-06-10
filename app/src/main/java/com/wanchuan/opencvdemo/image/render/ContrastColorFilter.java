package com.wanchuan.opencvdemo.image.render;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;

public class ContrastColorFilter extends AFilter {

    private ColorFilter.Filter filter;

    private int hChangeType;
    private int hChangeColor;

    public ContrastColorFilter(Context context,  ColorFilter.Filter filter) {
        super(context, "filter/half_color_vertex.sh", "filter/half_color_fragment.sh");
        this.filter = filter;
    }

    @Override
    public void onDrawSet() {
        GLES30.glUniform1i(hChangeType,filter.getType());
        GLES30.glUniform3fv(hChangeColor,1,filter.data(),0);
    }

    @Override
    public void onDrawCreatedSet(int mProgram) {
        hChangeType=GLES30.glGetUniformLocation(mProgram,"vChangeType");
        hChangeColor=GLES30.glGetUniformLocation(mProgram,"vChangeColor");
    }

}
