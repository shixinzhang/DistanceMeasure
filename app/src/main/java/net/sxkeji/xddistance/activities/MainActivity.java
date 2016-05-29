package net.sxkeji.xddistance.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Toast;


import net.sxkeji.xddistance.views.ChangeColorIconWithTextView;
import net.sxkeji.xddistance.utils.FileUtils;
import net.sxkeji.xddistance.adapters.MainViewPagerAdapter;
import net.sxkeji.xddistance.R;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
    private View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState != null){
            clickCount = savedInstanceState.getInt(COUNT);
        }
        initView();
        setListener();

    }

    private void setListener() {
        mViewPager.setOnPageChangeListener(this);
    }

    private void initView() {
        setOverflowShowingAlways();
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
        mViewPager.setAdapter(new MainViewPagerAdapter(getSupportFragmentManager()));
        initTabIndicator();

//        testMyReflect();
    }

    /**
     * 测试一下反射调用截图
     * TODO : 记得删除
     */
    private void testMyReflect() {
        decorView = this.getWindow().getDecorView();
        boolean saveSuccess = false;
        try {
            Class<?> viewClass = Class.forName("android.view.View");
            Method snapshot = viewClass.getDeclaredMethod("createSnapshot", new Class[]{Bitmap.Config.class,
                    int.class, boolean.class});
            snapshot.setAccessible(true);
            Bitmap bitmap = (Bitmap) snapshot.invoke(decorView, Bitmap.Config.RGB_565, 0xffff0000, false);
            saveSuccess = FileUtils.saveBitmap2File(bitmap);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (!saveSuccess) {
            Toast.makeText(MainActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
        }
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
//                Toast.makeText(MainActivity.this, "拍照" + clickCount, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, CameraActivity.class));
            }
        });

    }

    @Override
    public void onPageSelected(int position) {
    }

    /**
     * 随着滑动百分比渐变tab背景
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
        outState.putInt(COUNT,clickCount);

    }
}
