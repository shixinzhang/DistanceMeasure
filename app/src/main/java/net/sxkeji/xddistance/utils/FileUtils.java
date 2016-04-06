package net.sxkeji.xddistance.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 文件操作工具类
 * Created by zhangshixin on 4/2/2016.
 */
public class FileUtils {
    private static final String TAG = "FileUtils";
    public static final int MEDIA_TYPE_IMG = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final int MEDIA_TYPE_SCREEN_CAPTURE = 3;

    public static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    public static File getOutputMediaFile(int type) {
        // TODO : check if there has a SD card
//        if (Environment.getExternalStorageState() == Environment.)

        File mediaDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "XDDistance");

        if (!mediaDir.exists()) {
            if (!mediaDir.mkdirs()) {
                Log.e(TAG, "Error: failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;

        switch (type) {
            case MEDIA_TYPE_IMG:
                mediaFile = new File(mediaDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
                break;
            case MEDIA_TYPE_VIDEO:
                mediaFile = new File(mediaDir.getPath() + File.separator + "VIDEO_" + timeStamp + ".mp4");
                break;
            case MEDIA_TYPE_SCREEN_CAPTURE:
                mediaFile = new File(mediaDir.getPath() + File.separator + "SCREEN_" + timeStamp + ".png");
                break;
            default:
                mediaFile = null;
                break;
        }


        return mediaFile;
    }

    /**
     * 保存截图
     * @param activity 要截图的activity
     * @return  true if success
     */
    public static boolean saveScreenCaptureFile(Activity activity) {
        boolean saveSuccess = false;
        View decorView = activity.getWindow().getDecorView();
        decorView.setDrawingCacheEnabled(true);
        decorView.buildDrawingCache();
        Bitmap drawingCacheBitmap = decorView.getDrawingCache(true);

        Rect frame = new Rect();
        decorView.getWindowVisibleDisplayFrame(frame);

        Bitmap bitmap = Bitmap.createBitmap(drawingCacheBitmap, 0, frame
                .top, frame.width(), frame.height());
        decorView.destroyDrawingCache();

        saveSuccess = saveBitmap2File(bitmap);
        return saveSuccess;
    }

    /**
     * 保存指定的bitmap
     * @param bitmap
     * @return true if success
     */
    public static boolean saveBitmap2File(Bitmap bitmap) {
        if (bitmap == null){
            return false;
        }
        File screenFile = getOutputMediaFile(MEDIA_TYPE_SCREEN_CAPTURE);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(screenFile);
            if (fos != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
                Log.e(TAG, "截图保存到 " + screenFile);

                return true;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "FileNotFoundException " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "IOException " + e.getMessage());
        }
        return false;
    }

    /**
     * 获取屏幕参数
     *
     * @return
     */
    public static Display getWindowDisplay(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        return display;
    }
}
