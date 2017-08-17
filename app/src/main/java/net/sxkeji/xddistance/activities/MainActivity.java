package net.sxkeji.xddistance.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewConfiguration;

import net.sxkeji.xddistance.R;
import net.sxkeji.xddistance.adapters.MainViewPagerAdapter;
import net.sxkeji.xddistance.views.ChangeColorIconWithTextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 主页
 * Created by sxzhang on 2016/2/23.
 * Codes can never be perfect!
 * Email : sxzhang2016@163.com
 * Blog : http://blog.csdn.net/u011240877
 */
public class MainActivity extends FragmentActivity implements
        ViewPager.OnPageChangeListener, View.OnClickListener {
    private ViewPager mViewPager;
    private List<ChangeColorIconWithTextView> mTabIndicator = new ArrayList<ChangeColorIconWithTextView>();
    private int clickCount = 0;
    private static final String COUNT = "clickCount";
    private String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private String permissionMessage = "拍照测距需要使用\"相机\" 和 \"外部存储器\"权限，请授予！";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            clickCount = savedInstanceState.getInt(COUNT);
        }
        initView();
        setListener();
        checkPermission();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        if (!checkPermissionAllGranted(permissions)) { //权限没允许
            new AlertDialog.Builder(this)
                    .setTitle("注意")
                    .setMessage(permissionMessage)
                    .setCancelable(false)
                    .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, final int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
                        }
                    })
                    .show();
        }
    }

    private void setListener() {
        mViewPager.setOnPageChangeListener(this);
    }

    private void initView() {
        setOverflowShowingAlways();
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
        mViewPager.setAdapter(new MainViewPagerAdapter(getSupportFragmentManager()));
        initTabIndicator();
    }

    private void initTabIndicator() {
        ChangeColorIconWithTextView home = (ChangeColorIconWithTextView) findViewById(R.id.id_home);
        ChangeColorIconWithTextView takePhoto = (ChangeColorIconWithTextView) findViewById(R.id.takePhoto);
        ChangeColorIconWithTextView personal = (ChangeColorIconWithTextView) findViewById(R.id.id_personal);
        mTabIndicator.add(home);
        mTabIndicator.add(personal);
        home.setIconAlpha(1.0f);

        home.setOnClickListener(this);
        personal.setOnClickListener(this);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCount++;
                checkPermissionAndOpen();
            }
        });

    }

    private void checkPermissionAndOpen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkPermissionAllGranted(permissions)) {
            checkPermission();
        } else {
            openCamera();
        }

    }

    private void openCamera() {
        startActivity(new Intent(MainActivity.this, CameraActivity.class));
    }

    /**
     * 检查所有权限是否允许
     *
     * @param permissions
     * @return
     */
    private boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            boolean grantResult = true;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissions[i])) {
                        openSettingOpenPermission();
                    }
                    grantResult = false;
                }
            }
            if (grantResult) {
                openCamera();
            }

        }
    }

    private void openSettingOpenPermission() {
        new AlertDialog.Builder(this)
                .setTitle("注意")
                .setMessage(permissionMessage)
                .setPositiveButton("去手动授权", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        startActivity(intent);
                    }
                }).setNegativeButton("取消", null)
                .show();
    }

    @Override
    public void onPageSelected(int position) {
    }

    /**
     * 随着滑动百分比渐变tab背景
     *
     * @param position
     * @param positionOffset
     * @param positionOffsetPixels
     */
    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
        if (positionOffset > 0) {
            ChangeColorIconWithTextView left = mTabIndicator.get(position);
            ChangeColorIconWithTextView right = mTabIndicator.get(position + 1);

            left.setIconAlpha(1 - positionOffset);
            right.setIconAlpha(positionOffset);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onClick(View v) {
        resetOtherTabs();

        switch (v.getId()) {
            case R.id.id_home:
                mTabIndicator.get(0).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(0, true);
                break;
            case R.id.id_personal:
                mTabIndicator.get(1).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(1, true);
                break;
        }

    }

    /**
     * 重置其他的Tab
     */
    private void resetOtherTabs() {
        for (int i = 0; i < mTabIndicator.size(); i++) {
            mTabIndicator.get(i).setIconAlpha(0);
        }
    }

    private void setOverflowShowingAlways() {
        try {
            // true if a permanent menu key is present, false otherwise.
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            menuKeyField.setAccessible(true);
            menuKeyField.setBoolean(config, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(COUNT, clickCount);

    }
}
