package net.sxkeji.xddistance.views;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * 底层相机预览界面
 * Created by zhangshixin on 4/2/2016.
 * Email : sxzhang2016@163.com
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private final String TAG = "CameraPreview";
    private Camera mCamera;
    private SurfaceHolder mHolder;
    private boolean isPreviewing = false;

    public CameraPreview(Context context) {
        super(context);
    }

    public CameraPreview(Context context, Camera camera) {
        super(context);
        this.mCamera = camera;
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); //3.0以前要求

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (isPreviewing) {
                mCamera.stopPreview();
            }
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
            isPreviewing = true;
        } catch (IOException e) {
            Log.e(TAG, "Error : failed to set camera preview " + e.getMessage() + "check if you has release the camera while finishing");
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        if (mHolder.getSurface() == null) {
            // preview surface 不存在
            return;
        }
        try {
            if (isPreviewing) {
                mCamera.stopPreview();
            }
        } catch (Exception e) {
            Log.e(TAG, "failed to stop a non-existent preview !");
        }

        // start a new preview with new setting
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.e(TAG, "Error : failed to set camera preview " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //销毁时记得释放Camera
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
            isPreviewing = false;
        }
    }


}
