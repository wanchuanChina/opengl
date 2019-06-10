package com.wanchuan.opencvdemo.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Toast;

import com.wanchuan.opencvdemo.R;
import com.wanchuan.opencvdemo.utils.PermissionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CameraUtilsActivity extends AppCompatActivity {


    private CameraManager mCameraManager;
    private TextureView mTtv_camera;
    private CameraDevice mCameraDevice;
    private Size mPreviewSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PermissionUtils.askPermission(this,new String[]{Manifest.permission.CAMERA,Manifest
                .permission.WRITE_EXTERNAL_STORAGE},10,initViewRunnable);

    }

    private Runnable initViewRunnable=new Runnable() {

        @Override
        public void run() {
            setContentView(R.layout.activity_camera_utils);
            mTtv_camera = findViewById(R.id.ttv_camera);
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        //设置 TextureView 的状态监听
        mTtv_camera.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            //TextureView 可用时调用改回调方法
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                //TextureView 可用，启动相机
                setupCamera();
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


    private void setupCamera() {
        //配置相机参数（cameraId，previewSize）
        configCamera();
        //打开相机
        openCamera();
    }

    @SuppressLint("MissingPermission")
    private void openCamera() {
        // 打开相机
        try {
            mCameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened( CameraDevice camera) {
//                    相机打开时会调用 onOpened() 回调方法。
//                    在 onOpened() 回调方法中创建预览会话
//                    根据TextureView 和 选定的 previewSize 创建用于显示预览数据的Surface
//                    调用 CameraDevice.createCaptureSession() 方法创建捕获会话，第一个参数是捕获数据的输出Surface列表，
//                          第二个参数是CameraCaptureSession的状态回调接口，当它创建好后会回调onConfigured方法，
//                          第三个参数用来确定Callback在哪个线程执行，为null的话就在当前线程执行。
//                    创建预览捕获请求，并设置会话进行重复请求，以获取连续的预览数据
                    mCameraDevice = camera;
                    // 创建相机预览session
                    createPreviewSession();


                }

                @Override
                public void onDisconnected(CameraDevice camera) {
                    //释放相机资源
                    releseCamera();
                }

                @Override
                public void onError( CameraDevice camera, int error) {
                    //释放相机资源
                    releseCamera();
                }
            },null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    private void createPreviewSession() {
        //根据TextureView 和 选定的 previewSize 创建用于显示预览数据的Surface
        SurfaceTexture surfaceTexture = mTtv_camera.getSurfaceTexture();
        surfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(),mPreviewSize.getHeight()); // 设置surfaceTexture缓冲区大小
        final Surface surface = new Surface(surfaceTexture);
        try {
            mCameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured( CameraCaptureSession session) {
                    //构建预览捕获请求
                    try {
                        CaptureRequest.Builder builder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                        builder.addTarget(surface); // 设置surface 作为预览数据的显示界面
                        CaptureRequest captureRequest = builder.build();
                        // 设置重复请求，以获取连续预览数据
                        session.setRepeatingRequest(captureRequest, new CameraCaptureSession.CaptureCallback() {
                            @Override
                            public void onCaptureProgressed( CameraCaptureSession session, CaptureRequest request, CaptureResult partialResult) {
                                super.onCaptureProgressed(session, request, partialResult);
                            }

                            @Override
                            public void onCaptureCompleted( CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                                super.onCaptureCompleted(session, request, result);
                            }
                        },null);


                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {

                }
            },null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void releseCamera() {
        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
    }


    private String cameraId;
    private void configCamera() {
        try {
            //遍历相机列表，使用前置相机
            for (String cid : mCameraManager.getCameraIdList()) {
                // 获取相机配置
                CameraCharacteristics cameraCharacteristics = mCameraManager.getCameraCharacteristics(cid);
                // 使用后置相机
                Integer facing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);// 获取相机朝向
                if (facing == CameraCharacteristics.LENS_FACING_FRONT){
                    continue;
                }
                // 获取相机输出格式、尺寸参数
                StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                // 打印相关参数（相机id，相机输出尺寸，设备屏幕尺寸，previewView尺寸
                printSizes(cid,streamConfigurationMap);
                // 设置最佳预览尺寸
                mPreviewSize = setOptimalPreviewSize(streamConfigurationMap.getOutputSizes(SurfaceTexture.class), mTtv_camera.getMeasuredWidth(), mTtv_camera.getMeasuredHeight());
                // 打印最佳预览尺寸
                Log.d("---", "最佳预览尺寸（w-h）：" + mPreviewSize.getWidth() + "-" + mPreviewSize.getHeight());
                cameraId = cid;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }


    }

    private Size setOptimalPreviewSize(Size[] sizes, int previewViewWidth, int previewViewHeight) {
        List<Size> bigEnoughSizes = new ArrayList<>();
        List<Size> notBigEnoughSizes = new ArrayList<>();
        for (Size size : sizes) {
            if (size.getWidth() >= previewViewWidth && size.getHeight() >= previewViewHeight) {
                bigEnoughSizes.add(size);
            } else {
                notBigEnoughSizes.add(size);
            }
        }

        if (bigEnoughSizes.size() > 0) {
            return Collections.min(bigEnoughSizes, new CompareSizesByArea());
        } else if (notBigEnoughSizes.size() > 0) {
            return Collections.max(notBigEnoughSizes, new CompareSizesByArea());
        } else {
            Log.d("---", "未找到合适的预览尺寸");
            return sizes[0];
        }
    }

    private void printSizes(String cid, StreamConfigurationMap streamConfigurationMap) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.onRequestPermissionsResult(requestCode == 10, grantResults, initViewRunnable,
                new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CameraUtilsActivity.this, "没有获得必要的权限", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }


    public static class CompareSizesByArea implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }
    }
}
