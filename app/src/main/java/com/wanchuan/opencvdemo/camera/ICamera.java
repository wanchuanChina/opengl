package com.wanchuan.opencvdemo.camera;

import android.graphics.Point;
import android.graphics.SurfaceTexture;

public interface ICamera {

    boolean open(int cameraId);

    void setConfig(Config config);

    boolean preview();

    boolean switchTo(int cameraId);

    void takePhoto(TakePhotoCallback callback);

    boolean close();

    void setPreviewTexture(SurfaceTexture texture);

    Point getPreviewSize();

    Point getPictureSize();

    void setOnPreviewFrameCallback(PreviewFrameCallback callback);

    /**
     * 配置信息类
     */
    class Config {
        float rate; // 宽高比
        int minPreviewWidth;
        int minPictureWidth;
    }


    /**
     * 获取照片的回调
     */
    interface TakePhotoCallback {
        void onTakePhoto(byte[] bytes, int width, int height);
    }

    /**
     * 相机预览帧回调
     */
    interface PreviewFrameCallback {
        void onPreviewFrame(byte[] bytes, int width, int height);
    }

}
