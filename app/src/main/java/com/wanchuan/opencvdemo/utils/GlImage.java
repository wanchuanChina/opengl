package com.wanchuan.opencvdemo.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class GlImage {

    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    public GlImage(Context context) {
        mContext = context;
    }

    public static Bitmap createBitmap(int resId) {
        return BitmapFactory.decodeResource(mContext.getResources(), resId);
    }


}
