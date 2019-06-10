package com.wanchuan.opencvdemo.camera;

public interface FrameCallback {
    void onFrame(byte[] bytes, long time);
}
