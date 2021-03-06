package net.sxkeji.xddistance.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import net.sxkeji.xddistance.fragments.HomeFragment;
import net.sxkeji.xddistance.fragments.SettingFragment;


/**
 * Created by sxzhang on 2016/2/25.
 * Codes can never be perfect!
 * Email : sxzhang2016@163.com
 */
public class MainViewPagerAdapter extends FragmentPagerAdapter {
    private HomeFragment homeFragment;
    private SettingFragment settingFragment;
//    private PersonalFragment personalFragment;

    private static final String[] titles = { "主页", "个人中心"};

    public MainViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if (homeFragment == null) {
                    homeFragment = new HomeFragment();
                }
                return homeFragment;
            case 1:
                if (settingFragment == null) {
                    settingFragment = new SettingFragment();
                }
                return settingFragment;
            default:
                return null;
        }
    }
}
