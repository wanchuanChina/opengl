package com.wanchuan.opencvdemo.image.render;

import android.content.Context;
import android.opengl.GLES30;


public class ColorFilter extends AFilter {


    private final Filter mFilter;
    private int mVChangeType;
    private int mVChangeColor;

    public ColorFilter(Context context, Filter filter) {
        super(context, "filter/default_vertex.sh", "filter/color_fragment.sh");
        mFilter = filter;
    }

    @Override
    public void onDrawSet() {

        GLES30.glUniform1i(mVChangeType, mFilter.getType());
        GLES30.glUniform3fv(mVChangeColor, 1, mFilter.data(), 0);

    }

    @Override
    public void onDrawCreatedSet(int mProgram) {

        mVChangeType = GLES30.glGetUniformLocation(mProgram, "vChangeType");
        mVChangeColor = GLES30.glGetUniformLocation(mProgram, "vChangeColor");

    }

    public enum Filter {

        NONE(0, new float[]{0.0f, 0.0f, 0.0f}),
        GRAY(1, new float[]{0.299f, 0.587f, 0.114f}),
        COOL(2, new float[]{0.0f, 0.0f, 0.1f}),
        WARM(2, new float[]{0.1f, 0.1f, 0.0f}),
        BLUR(3, new float[]{0.006f, 0.006f, 0.006f}),
//        BLUR(3, new float[]{0.006f, 0.004f, 0.002f}),
        MAGN(4, new float[]{0.0f, 0.0f, 0.4f});


        private int vChangeType;
        private float[] data;

        Filter(int vChangeType, float[] data) {
            this.vChangeType = vChangeType;
            this.data = data;
        }

        public int getType() {
            return vChangeType;
        }

        public float[] data() {
            return data;
        }

    }
}
