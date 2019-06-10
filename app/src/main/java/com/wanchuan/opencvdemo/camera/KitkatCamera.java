package com.wanchuan.opencvdemo.camera;

import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import org.opencv.core.Mat;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class KitkatCamera implements ICamera {

    private Config mConfig;
    private CameraSizeComparator mComparator;
    private Camera mCamera;
    private Camera.Size mPictureSize;
    private Camera.Size mPreviewSize;
    private Point mPrePoint;
    private Point mPicPoint;

    public KitkatCamera() {
        mConfig = new Config();
        mConfig.minPictureWidth = 720;
        mConfig.minPreviewWidth = 720;
        mConfig.rate = 1.778f;
        mComparator = new CameraSizeComparator();

    }

    /**
     * 打开指定摄像头
     * @param cameraId
     * @return
     */
    @Override
    public boolean open(int cameraId) {
        mCamera = Camera.open(cameraId);
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            mPictureSize = getPropPictureSize(parameters.getSupportedPictureSizes(), mConfig.rate, mConfig.minPictureWidth);
            mPreviewSize = getPropPreviewSize(parameters.getSupportedPreviewSizes(), mConfig.rate, mConfig.minPreviewWidth);

            parameters.setPictureSize(mPictureSize.width, mPictureSize.height);
            parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
            mCamera.setParameters(parameters);

            Camera.Size pictureSize = parameters.getPictureSize();
            Camera.Size previewSize = parameters.getPreviewSize();

            mPicPoint = new Point(pictureSize.height, pictureSize.width);
            mPrePoint = new Point(previewSize.height, previewSize.width);
            return true;
        }
        return false;
    }

    @Override
    public void setConfig(Config config) {
        this.mConfig = config;
    }

    @Override
    public boolean preview() {
        if (mCamera != null) {
            mCamera.startPreview();
        }
        return false;
    }

    @Override
    public boolean switchTo(int cameraId) {
        close();
        open(cameraId);
        return false;
    }

    @Override
    public void takePhoto(TakePhotoCallback callback) {

    }

    @Override
    public boolean close() {
        if (mCamera != null) {
            try {
                mCamera.stopPreview();
                mCamera.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    /**
     * 设置预览纹理
     *
     * @param texture
     */
    public void setPreviewTexture(SurfaceTexture texture) {
        if (mCamera != null) {
            try {
                mCamera.setPreviewTexture(texture);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Point getPreviewSize() {
        return mPrePoint;
    }

    @Override
    public Point getPictureSize() {
        return mPicPoint;
    }

    @Override
    public void setOnPreviewFrameCallback(final PreviewFrameCallback callback) {
        if (mCamera != null) {
            mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    callback.onPreviewFrame(data, mPrePoint.x, mPrePoint.y);
                }
            });
        }
    }


    public void addBuffer(byte[] buffer) {
        if (mCamera != null) {
            mCamera.addCallbackBuffer(buffer);
        }
    }

    public void setOnPreviewFrameCallbackWithBuffer(final PreviewFrameCallback callback){
        if (mCamera != null) {
            mCamera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    callback.onPreviewFrame(data,mPrePoint.x,mPrePoint.y);
                }
            });
        }
    }


    private Camera.Size getPropPreviewSize(List<Camera.Size> list, float th, int minWidth) {
        Collections.sort(list, mComparator);
        int i = 0;
        for (Camera.Size size : list) {
            if ((size.height >= minWidth) && equalRate(size, th)) {
                break;
            }
            i++;
        }
        if (i == list.size()) {
            i = 0;
        }
        return list.get(i);
    }

    private Camera.Size getPropPictureSize(List<Camera.Size> list, float th, int minWidth) {
        Collections.sort(list, mComparator);
        int i = 0;
        for (Camera.Size size : list) {
            if ((size.height >= minWidth) && equalRate(size, th)) {
                break;
            }
            i++;
        }
        if (i == list.size()) {
            i = 0;
        }
        return list.get(i);
    }


    private boolean equalRate(Camera.Size size, float rate) {
        float r = (float) (size.width) / (float) (size.height);
        if (Math.abs(r - rate) <= 0.03) {
            return true;
        } else {
            return false;
        }
    }


    private class CameraSizeComparator implements Comparator<Camera.Size> {

        @Override
        public int compare(Camera.Size o1, Camera.Size o2) {
            if (o1.height == o2.height) {
                return 0;
            } else if (o1.height > o2.height) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}
