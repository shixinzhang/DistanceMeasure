package net.sxkeji.xddistance;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by zhangshixin on 4/2/2016.
 * Email : sxzhang2016@163.com
 */
public class BaseApplication extends Application {
    private static  BaseApplication application ;
    private static SQLiteDatabase db;
    private static DaoMaster daoMaster;
    private static DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        initDataBase();
    }

    private void initDataBase() {
        DaoMaster.DevOpenHelper openHelper = new DaoMaster.DevOpenHelper(this, "distance_db", null);
        db = openHelper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public static Application getInstance() {
        return application;
    }

    public static SQLiteDatabase getDb() {
        return db;
    }

    public static DaoMaster getDaoMaster() {
        return daoMaster;
    }

    public static DaoSession getDaoSession() {
        return daoSession;
    }
}
