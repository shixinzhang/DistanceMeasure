package net.sxkeji.xddistance;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * 预览、拍照
 * Created by zhangshixin on 4/1/2016.
 * Email : sxzhang2016@163.com
 */
public class CameraActivity extends Activity implements RectControlView.OnRulerHeightChangedListener {
    private final String TAG = "CameraActivity";
    private final String SP_NAME = "xddistance";
    private final String SP_HEIGHT = "target_height";
    private final int SP_WRITE = 1;
    private final String TARGET_HEIGHT = "target_height_setting";

    private Camera camera;
    private CameraPreview cameraPreview;
    private RectControlView rectControlView;
    private FrameLayout rlCameraPreview;
    private ImageView ivTakePhoto;
    private TextView tvDistance;
    private EditText etTargetHeight;
    private float targetHeight = -1f;
    private SharedPreferences mSharedPreferences;
    private int distanceX = 34;
    private Bitmap mScreenCaptureBitmap;
    private View decorView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        initViews();
        setListeners();
    }

    private void setListeners() {
        ivTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (camera != null) {
                            camera.takePicture(null, null, pictureCallback);
                        }
//                        boolean saveSuccess = FileUtils.saveScreenCaptureFile(CameraActivity.this);

                        boolean saveSuccess = FileUtils.saveBitmap2File(mScreenCaptureBitmap);

                        if (!saveSuccess) {
                            Toast.makeText(CameraActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).start();

                //目标高度写入SP，下次读取
                Message msg = new Message();
                msg.what = SP_WRITE;
                Bundle bundle = new Bundle();
                bundle.putFloat(SP_HEIGHT, targetHeight);
                msg.setData(bundle);
                spHandler.handleMessage(msg);

            }
        });

        etTargetHeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String replaceStr = etTargetHeight.getText().toString();
                if (replaceStr.contains("请输入") || TextUtils.isEmpty(replaceStr)) {
                    targetHeight = 0f;
                } else {
                    targetHeight = Float.parseFloat(replaceStr);
                }
//

            }
        });
    }

    private void initViews() {
        initCamera();

        decorView = this.getWindow().getDecorView();
        rectControlView = (RectControlView) findViewById(R.id.rectControlView);
        tvDistance = (TextView) findViewById(R.id.tv_distance);
        etTargetHeight = (EditText) findViewById(R.id.et_target_height);
        ivTakePhoto = (ImageView) findViewById(R.id.iv_take_photo);

        mSharedPreferences = getSharedPreferences(SP_NAME, MODE_PRIVATE);
        targetHeight = mSharedPreferences.getFloat(TARGET_HEIGHT, -1f);
        if (targetHeight != -1f) {
            etTargetHeight.setText(targetHeight + "");
        }

    }

    private void initCameraPreview() {
        cameraPreview = new CameraPreview(this, camera);
        rlCameraPreview = (FrameLayout) findViewById(R.id.fl_camera_preview);
        rlCameraPreview.addView(cameraPreview);

    }

    private void initCamera() {
        camera = getCameraInstance();
        if (camera != null) {
            Camera.Parameters parameters = camera.getParameters();
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            if (parameters.getMaxNumMeteringAreas() > 0) {
                ArrayList<Camera.Area> meteringAreas = new ArrayList<>();
                Rect areaRect = new Rect(-1000, -1000, 1000, 1000);
                meteringAreas.add(new Camera.Area(areaRect, 600));
                parameters.setMeteringAreas(meteringAreas);
            } else {
                Log.e(TAG, "metering or focusing area selection is unsupported");
            }

            camera.setParameters(parameters);

            initCameraPreview();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCamera();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initCamera();
    }

    private void releaseCamera() {
        if (camera != null) {
            camera.release();
            camera = null;
            Log.e(TAG, "releaseCamera");
        }
    }

    private Camera getCameraInstance() {
        Camera mCamera = null;
        if (checkHasCameraOrNot(CameraActivity.this)) {
            try {
                mCamera = Camera.open();
            } catch (Exception e) {
                Log.e("getCameraInstance ", "error : Camera is not available !");
            }
        }
        return mCamera;
    }

    /**
     * check whether the device has camera or not
     *
     * @param context
     * @return true if has
     */
    private boolean checkHasCameraOrNot(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    Handler spHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SP_WRITE:
                    Bundle data = msg.getData();
                    float aFloat = data.getFloat(SP_HEIGHT, 0f);
                    mSharedPreferences.edit().putFloat(TARGET_HEIGHT, aFloat).apply();
                    break;
            }
            super.handleMessage(msg);

        }
    };

    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            camera.stopPreview();
            File pickFile = FileUtils.getOutputMediaFile(FileUtils.MEDIA_TYPE_IMG);
            if (pickFile == null) {
                Log.e(TAG, "Error : failed to create media file , check storage permissions ");
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pickFile);
                fos.write(data);
                fos.close();
                camera.startPreview();
                Log.e(TAG, "Finish writing , path is " + pickFile);
                Toast.makeText(CameraActivity.this, "图片保存到 " + pickFile, Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.e(TAG, "FileNotFoundException " + e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Error : failed  to access file " + e.getMessage());
            }
        }
    };

    /**
     * Ruler高度改变回调
     *
     * @param rulerHeight
     * @param bitmap
     */
    @Override
    public void onRulerHeightChanged(float rulerHeight, Bitmap bitmap) {
        Log.e(TAG, "targetHeight: " + targetHeight + " , rulerHeight: " + rulerHeight);
        mScreenCaptureBitmap = bitmap;
        double distance = targetHeight / rulerHeight * Math.pow(distanceX, 2);
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.applyPattern("0.00");
        tvDistance.setText(decimalFormat.format(distance));
    }
}
