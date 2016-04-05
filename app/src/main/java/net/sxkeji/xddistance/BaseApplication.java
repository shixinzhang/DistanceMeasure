package net.sxkeji.xddistance;

import android.app.Application;

/**
 * Created by zhangshixin on 4/2/2016.
 * Email : sxzhang2016@163.com
 */
public class BaseApplication extends Application {
    private static BaseApplication application;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static Application getInstance(){
        return application;
    }
}
