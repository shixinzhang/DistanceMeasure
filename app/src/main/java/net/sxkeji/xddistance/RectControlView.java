package net.sxkeji.xddistance;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;


/**
 * 顶层绘制矩形、用户操作界面
 * Created by zhangshixin on 4/2/2016.
 */
public class RectControlView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private final String TAG = "RectControlView";

    private SurfaceHolder mHolder;
    private Context mContext;
    private final int centerSize = 20;
    private final int rulerPaddingX = 20;   //尺子距离X轴边距
    private final float rulerLineWidth = 5;
    private float mScreenWidth;
    private float mScreenHeight;
    private float mHalfScreenWidth;   //屏幕宽度的一半
    private float mHalfScreenHeight;
    private OnRulerHeightChangedListener mListener;

    public RectControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        initRulerChangedListener(mContext);
        mHolder = getHolder();
        mHolder.addCallback(this);
        //关键的两步 : 设置透明、放到顶部
        mHolder.setFormat(PixelFormat.TRANSPARENT);
        this.setZOrderOnTop(true);
    }

    private void initRulerChangedListener(Context context) {
        try {
            mListener = (OnRulerHeightChangedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(mContext.toString()
                    + "must implement OnRulerHeightChangedListener !");
        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mScreenWidth = getWindowDisplay().getWidth();
        mScreenHeight = getWindowDisplay().getHeight();
        mHalfScreenWidth = mScreenWidth / 2;
        mHalfScreenHeight = mScreenHeight / 2;
        Log.e(TAG, "center : (" + mHalfScreenWidth + " , " + mHalfScreenHeight + ")");
        drawRulerLine((int) mHalfScreenHeight);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }


    /**
     * 绘制尺子的那两条线
     *
     * @param yDistance y轴坐标
     */
    private void drawRulerLine(int yDistance) {
        Canvas canvas = mHolder.lockCanvas();
//        canvas.drawColor(Color.BLACK);

        if (canvas == null) {
            Log.e(TAG, "failed to get canvas");
            return;
        }
        //清屏
        Paint p = new Paint();
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPaint(p);

        drawRulerLineOnCanvas(canvas, yDistance);

        mHolder.unlockCanvasAndPost(canvas);

        //绘制到截图用的bitmap上
        Bitmap screenBitmap = Bitmap.createBitmap((int) mScreenWidth, (int) mScreenHeight, Bitmap.Config.RGB_565);
        Canvas screenCaptureCanvas = new Canvas(screenBitmap);
        drawRulerLineOnCanvas(screenCaptureCanvas, yDistance);

        mListener.onRulerHeightChanged(Math.abs(mScreenHeight - yDistance * 2), screenBitmap);
    }

    /**
     * 在指定的Canvas绘制
     * @param canvas
     * @param yDistance
     */
    private void drawRulerLineOnCanvas(Canvas canvas, int yDistance) {
        Paint paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);  //抗锯齿
        paint.setColor(Color.RED);
        paint.setStrokeWidth(rulerLineWidth);
        canvas.drawLine(mHalfScreenWidth - centerSize,
                mHalfScreenHeight,
                mHalfScreenWidth + centerSize,
                mHalfScreenHeight,
                paint);
        canvas.drawLine(mHalfScreenWidth,
                mHalfScreenHeight - centerSize,
                mHalfScreenWidth,
                mHalfScreenHeight + centerSize,
                paint);

        //画2条ruler线
        Paint rulerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rulerPaint.setColor(Color.GREEN);
        rulerPaint.setStrokeWidth(rulerLineWidth);

        canvas.drawLine(rulerPaddingX,
                yDistance,
                mScreenWidth - rulerPaddingX,
                yDistance,
                rulerPaint);

        canvas.drawLine(rulerPaddingX,
                mScreenHeight - yDistance,
                mScreenWidth - rulerPaddingX,
                mScreenHeight - yDistance,
                rulerPaint);

        canvas.drawLine(rulerPaddingX * 2,
                yDistance,
                rulerPaddingX * 2,
                mScreenHeight - yDistance,
                rulerPaint);
    }

    /**
     * 获取屏幕参数
     *
     * @return
     */
    private Display getWindowDisplay() {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        return display;
    }


    float startX;
    float startY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = y;
                drawRulerLine((int) event.getY());
//                Log.e(TAG, "DOWN :" + x + " , " + y);
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaY = Math.abs(startY - y);
                if (deltaY > 0) {
                    drawRulerLine((int) startY);
                    startY = y;
                }
//                Log.e(TAG, "MOVE :" + startY);
                break;
            case MotionEvent.ACTION_UP:
                drawRulerLine((int) event.getY());
//                Log.e(TAG, "UP :" + event.getX() + " , " + event.getY());
                break;
        }
//        invalidate();
        return true;
    }

    @Override
    public void run() {

    }

    /**
     * ruler高度变化回调接口
     */
    public interface OnRulerHeightChangedListener {
        void onRulerHeightChanged(float rulerHeight, Bitmap bitmap);
    }
}
