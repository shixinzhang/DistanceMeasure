package net.sxkeji.xddistance.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.sxkeji.xddistance.R;

/**
 * 设置
 * Created by sxzhang on 2016/2/25.
 * Codes can never be perfect!
 * Email : sxzhang2016@163.com
 */
public class SettingFragment extends Fragment {

    private static final String TAG = SettingFragment.class.getName();

    private View view;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_setting, null);
        return view;
    }
}
