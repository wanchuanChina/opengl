package com.wanchuan.opencvdemo.activity;

import android.annotation.SuppressLint;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;

import com.wanchuan.opencvdemo.R;
import com.wanchuan.opencvdemo.camera.CameraUtils;
import com.wanchuan.opencvdemo.view.AutoFitTextureView;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CameraUtils2Activity extends AppCompatActivity {

    private AutoFitTextureView mCamera;
    private CameraManager cameraManager;
    private String cameraId;
    private List<Size> outputSizes;
    private Size previewSize;
    private Button mBtn;
    private CameraDevice cameraDevice;
    private  ImageReader previewReader;
    private CameraCaptureSession cameraCaptureSession;//相机会话类

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_utils2);
        mCamera = findViewById(R.id.camera);
        mBtn = findViewById(R.id.btn);
        initCamera();
        mCamera.setAspectRation(mCamera.getWidth(),mCamera.getHeight());
    }

    private void initCamera(){
        CameraUtils.init(this);
        cameraManager = CameraUtils.getInstance().getCameraManager();
        cameraId = CameraUtils.getInstance().getCameraId(false);//默认使用后置相机
        //获取指定相机的输出尺寸列表
        outputSizes = CameraUtils.getInstance().getCameraOutputSizes(cameraId, SurfaceTexture.class);
        //初始化预览尺寸
        previewSize = outputSizes.get(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCamera.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                openCamera();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
    }

    @SuppressLint("MissingPermission")
    private void openCamera() {
        try {
            //打开相机
            cameraManager.openCamera(cameraId,
                    new CameraDevice.StateCallback() {
                        @Override
                        public void onOpened(CameraDevice camera) {
                            if (camera == null) {
                                return;
                            }
                            cameraDevice = camera;
                            //创建相机预览 session
                            createPreviewSession();
                        }

                        @Override
                        public void onDisconnected(CameraDevice camera) {
                            //释放相机资源
                            releaseCamera();
                        }

                        @Override
                        public void onError(CameraDevice camera, int error) {
                            //释放相机资源
                            releaseCamera();
                        }
                    },
                    null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }



    public void onClick(View view) {
        //切换预览分辨率
        updateCameraPreview();
        setButtonText();

    }

    private void createPreviewSession() {
        //关闭之前的会话
        CameraUtils.getInstance().releaseImageReader(previewReader);
        CameraUtils.getInstance().releaseCameraSession(cameraCaptureSession);
        //根据TextureView 和 选定的 previewSize 创建用于显示预览数据的Surface
        SurfaceTexture surfaceTexture = mCamera.getSurfaceTexture();
        surfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());//设置SurfaceTexture缓冲区大小
        final Surface previewSurface = new Surface(surfaceTexture);
        //获取 ImageReader 和 surface
        previewReader = ImageReader.newInstance(previewSize.getWidth(), previewSize.getHeight(), ImageFormat.YV12, 2);
//        将YuvImage中的矩形区域压缩为jpeg.目前仅支持ImageFormat.NV21和ImageFormat.YUY2
        previewReader.setOnImageAvailableListener(
                new ImageReader.OnImageAvailableListener() {
                    @Override
                    public void onImageAvailable(ImageReader reader) {
                        Image image = reader.acquireLatestImage();
                        if (image != null) {
                            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                            byte[] data = new byte[buffer.remaining()];
                            Log.e("---", "data-size=" + data.length);
                            buffer.get(data);
                            image.close();
                        }
                    }
                },
                null);
        final Surface readerSurface = previewReader.getSurface();

        try {
            //创建预览session
            cameraDevice.createCaptureSession(Arrays.asList(previewSurface, readerSurface),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(CameraCaptureSession session) {

                            cameraCaptureSession = session;

                            try {
                                //构建预览捕获请求
                                CaptureRequest.Builder builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                                builder.addTarget(previewSurface);//设置 previewSurface 作为预览数据的显示界面
                                builder.addTarget(readerSurface);
                                CaptureRequest captureRequest = builder.build();
                                //设置重复请求，以获取连续预览数据
                                session.setRepeatingRequest(captureRequest, new CameraCaptureSession.CaptureCallback() {
                                            @Override
                                            public void onCaptureProgressed(CameraCaptureSession session, CaptureRequest request, CaptureResult partialResult) {
                                                super.onCaptureProgressed(session, request, partialResult);
                                            }

                                            @Override
                                            public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                                                super.onCaptureCompleted(session, request, result);
                                            }
                                        },
                                        null);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(CameraCaptureSession session) {

                        }
                    },
                    null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }


    private void updateCameraPreviewWithImageMode() {
        previewSize = outputSizes.get(0);
        mCamera.setAspectRation(previewSize.getWidth(), previewSize.getHeight());
        createPreviewSession();
    }

    private static final long PREVIEW_SIZE_MIN = 720 * 480;
    private void updateCameraPreviewWithVideoMode() {
        List<Size> sizes = new ArrayList<>();
        //计算预览窗口高宽比，高宽比，高宽比
        float ratio = ((float) mCamera.getHeight() / mCamera.getWidth());
        //首先选取宽高比与预览窗口高宽比一致且最大的输出尺寸
        for (int i = 0; i < outputSizes.size(); i++) {
            if (((float) outputSizes.get(i).getWidth()) / outputSizes.get(i).getHeight() == ratio) {
                sizes.add(outputSizes.get(i));
            }
        }
        if (sizes.size() > 0) {
            previewSize = Collections.max(sizes, new CameraUtils.CompareSizesByArea());
            mCamera.setAspectRation(previewSize.getWidth(), previewSize.getHeight());
            createPreviewSession();
            return;
        }
        //如果不存在宽高比与预览窗口高宽比一致的输出尺寸，则选择与其高宽比最接近的输出尺寸
        sizes.clear();
        float detRatioMin = Float.MAX_VALUE;
        for (int i = 0; i < outputSizes.size(); i++) {
            Size size = outputSizes.get(i);
            float curRatio = ((float) size.getWidth()) / size.getHeight();
            if (Math.abs(curRatio - ratio) < detRatioMin) {
                detRatioMin = curRatio;
                previewSize = size;
            }
        }
        if (previewSize.getWidth() * previewSize.getHeight() > PREVIEW_SIZE_MIN) {
            mCamera.setAspectRation(previewSize.getWidth(), previewSize.getHeight());
            createPreviewSession();
        }
        //如果宽高比最接近的输出尺寸太小，则选择与预览窗口面积最接近的输出尺寸
        long area = mCamera.getWidth() * mCamera.getHeight();
        long detAreaMin = Long.MAX_VALUE;
        for (int i = 0; i < outputSizes.size(); i++) {
            Size size = outputSizes.get(i);
            long curArea = size.getWidth() * size.getHeight();
            if (Math.abs(curArea - area) < detAreaMin) {
                detAreaMin = curArea;
                previewSize = size;
            }
        }
        mCamera.setAspectRation(previewSize.getWidth(), previewSize.getHeight());
        createPreviewSession();
    }

    private int sizeIndex;
    private void updateCameraPreview(){
        if (sizeIndex + 1 < outputSizes.size()){
            sizeIndex++;
        }else {
            sizeIndex = 0;
        }
        previewSize = outputSizes.get(sizeIndex);
        //重新创建会话
        createPreviewSession();
    }

    private void setButtonText(){
        mBtn.setText(previewSize.getWidth() + "-" + previewSize.getHeight());
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseCamera();
    }

    private void releaseCamera() {
        CameraUtils.getInstance().releaseImageReader(previewReader);
        CameraUtils.getInstance().releaseCameraSession(cameraCaptureSession);
        CameraUtils.getInstance().releaseCamera(cameraDevice);
    }
}
