package net.sxkeji.xddistance.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.sxkeji.xddistance.BaseApplication;
import net.sxkeji.xddistance.PictureInfo;
import net.sxkeji.xddistance.R;
import net.sxkeji.xddistance.utils.Constant;
import net.sxkeji.xddistance.utils.FileUtils;
import net.sxkeji.xddistance.utils.SharedPreUtil;
import net.sxkeji.xddistance.views.CameraPreview;
import net.sxkeji.xddistance.views.RectControlView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 预览、拍照
 * Created by zhangshixin on 4/1/2016.
 * Update on 23/7/2016 , add measure height
 * Email : sxzhang2016@163.com
 */
public class CameraActivity extends Activity implements RectControlView.OnRulerHeightChangedListener {
    private final static String TAG = "CameraActivity";
    private final static int SP_WRITE = 1;
    private final static String TARGET_HEIGHT = "target_height_setting";
    @Bind(R.id.btn_measure_distance)
    Button btnMeasureDistance;
    @Bind(R.id.btn_measure_height)
    Button btnMeasureHeight;
    @Bind(R.id.tv_distance_title)
    TextView tvDistanceTitle;
    @Bind(R.id.et_target_distance)
    EditText etTargetDistance;
    @Bind(R.id.tv_height_title)
    TextView tvHeightTitle;

    private Camera camera;
    private CameraPreview cameraPreview;
    private RectControlView rectControlView;
    private FrameLayout rlCameraPreview;
    private ImageView ivTakePhoto;
    private EditText etTargetHeight;
    private float targetHeightFloat = -1f;       //用户输入的目标预计高度
    private float targetDistanceFloat = -1f;    //用户输入的目标预计距离
    private String targetDistance = "-1";   //测得的距离
    private String targetHeight = "-1";     //测得的高度
    private SharedPreferences mSharedPreferences;
    private int distanceX;
    private Bitmap mScreenCaptureBitmap;
    private View decorView;
    private boolean isMeasureDistance = true;

    Handler spHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SP_WRITE:
                    Bundle data = msg.getData();
                    float aFloat = data.getFloat(Constant.SP_HEIGHT, 0f);
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
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                bitmap = ThumbnailUtils.extractThumbnail(bitmap, 820, 480);

                FileOutputStream fos = new FileOutputStream(pickFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
//                fos.write(data);
                fos.close();
                camera.startPreview();
                Log.e(TAG, "Finish writing , path is " + pickFile);
                Toast.makeText(CameraActivity.this, "图片保存到 " + pickFile, Toast.LENGTH_SHORT).show();
                //save to db
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                PictureInfo pictureInfo = new PictureInfo(null, pickFile.getPath(), targetDistance, timeStamp, "notips");
                savePic2DB(pictureInfo);

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
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
//    private GoogleApiClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);
        initViews();
        setListeners();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void setListeners() {
        //选择测量距离
        btnMeasureDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isMeasureDistance) {
                    isMeasureDistance = !isMeasureDistance;
                    switchMeasureMode();
                }
            }
        });
        //选择测量高度
        btnMeasureHeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMeasureDistance) {
                    isMeasureDistance = !isMeasureDistance;
                    switchMeasureMode();
                }
            }
        });

        ivTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (camera != null) {
                            camera.takePicture(null, null, pictureCallback);
                        }
                    }
                }).start();

                //目标高度写入SP，下次读取
                Message msg = new Message();
                msg.what = SP_WRITE;
                Bundle bundle = new Bundle();
                bundle.putFloat(Constant.SP_HEIGHT, targetHeightFloat);
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
                    targetHeightFloat = 0f;
                } else {
                    targetHeightFloat = Float.parseFloat(replaceStr);
                }
            }
        });

        etTargetDistance.addTextChangedListener(new TextWatcher() {
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
                    targetDistanceFloat = 0f;
                } else {
                    targetDistanceFloat = Float.parseFloat(replaceStr);
                }
            }
        });
    }

    /**
     * 切换当前测量状态
     */
    private void switchMeasureMode() {
        // 设置测量方式按钮状态
        if (isMeasureDistance) {
            btnMeasureDistance.setBackgroundResource(R.drawable.bg_save_selector);
            btnMeasureHeight.setBackground(null);
            tvDistanceTitle.setText("测得距离");
            tvDistanceTitle.setEnabled(false);
            tvHeightTitle.setText("输入预计高度(米)");
        } else {
            btnMeasureHeight.setBackgroundResource(R.drawable.bg_save_selector);
            btnMeasureDistance.setBackground(null);
            tvHeightTitle.setText("测得高度");
            tvHeightTitle.setEnabled(false);
            tvDistanceTitle.setText("输入预计距离(米)");

        }
    }

    private void initViews() {
        initCamera();

        distanceX = SharedPreUtil.readInt(Constant.FACTOR, 34);
        decorView = this.getWindow().getDecorView();
        rectControlView = (RectControlView) findViewById(R.id.rectControlView);
        etTargetHeight = (EditText) findViewById(R.id.et_target_height);
        ivTakePhoto = (ImageView) findViewById(R.id.iv_take_photo);

        mSharedPreferences = getSharedPreferences(Constant.SP_NAME, MODE_PRIVATE);
        targetHeightFloat = mSharedPreferences.getFloat(TARGET_HEIGHT, -1f);
        if (targetHeightFloat != -1f) {
            etTargetHeight.setText(Float.toString(targetHeightFloat));
        } else {
            targetHeightFloat = Float.parseFloat(etTargetHeight.getText().toString());
        }

        switchMeasureMode();
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
        Log.e(TAG, "onDestroy");
        releaseCamera();
        if (spHandler != null) {
            spHandler.removeCallbacksAndMessages(null);
            spHandler = null;
        }
        if (decorView != null) {
            decorView = null;
        }
        if (mSharedPreferences != null) {
            mSharedPreferences = null;
        }
        Process.killProcess(Process.myPid());
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
        if (cameraPreview != null) {
            cameraPreview = null;
        }
        if (rectControlView != null) {
            rectControlView = null;
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
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    /**
     * 保存到数据库中
     *
     * @param pictureInfo
     */
    private void savePic2DB(final PictureInfo pictureInfo) {
        Log.e(TAG, "" + pictureInfo.getPath() + " / " + pictureInfo.getDistance() + " / " + pictureInfo.getTime());
        new Thread(new Runnable() {
            @Override
            public void run() {
                long insert = BaseApplication.getDaoSession().getPictureInfoDao().insert(pictureInfo);
                Log.e(TAG, insert + "号图片插入到DB");
            }
        }).start();

    }

    /**
     * Ruler高度改变回调
     *
     * [update on 7/24/2016 add measure height ]
     *
     * @param rulerHeight
     * @param bitmap
     */
    @Override
    public void onRulerHeightChanged(float rulerHeight, Bitmap bitmap) {
        Log.e(TAG, "targetHeight: " + targetHeightFloat + " , rulerHeight: " + rulerHeight);
        mScreenCaptureBitmap = bitmap;

        if (isMeasureDistance) {
            double distance = targetHeightFloat / rulerHeight * Math.pow(distanceX, 2);
            DecimalFormat decimalFormat = new DecimalFormat();
            decimalFormat.applyPattern("0.00");
            targetDistance = decimalFormat.format(distance);
            etTargetDistance.setText(targetDistance);
        } else {
            double height = targetDistanceFloat * rulerHeight / Math.pow(distanceX, 2);
            DecimalFormat decimalFormat = new DecimalFormat();
            decimalFormat.applyPattern("0.00");
            targetHeight = decimalFormat.format(height);
            etTargetHeight.setText(targetHeight);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client.connect();
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "Camera Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app URL is correct.
//                Uri.parse("android-app://net.sxkeji.xddistance.activities/http/host/path")
//        );
//        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "Camera Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app URL is correct.
//                Uri.parse("android-app://net.sxkeji.xddistance.activities/http/host/path")
//        );
//        AppIndex.AppIndexApi.end(client, viewAction);
//        client.disconnect();
    }
}
