package net.sxkeji.xddistance;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


/**
 * Created by sxzhang on 2016/2/25.
 * Codes can never be perfect!
 */
public class MainViewPagerAdapter extends FragmentPagerAdapter {
    private HomeFragment homeFragment;
    private FindFragment findFragment;
//    private PersonalFragment personalFragment;

    public MainViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    private final String[] titles = { "主页", "个人中心"};

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
                if (findFragment == null) {
                    findFragment = new FindFragment();
                }
                return findFragment;
            default:
                return null;
        }
    }
}
