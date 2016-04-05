package net.sxkeji.xddistance;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 主页的fragment
 * Created by sxzhang on 2016/2/25.
 * Updated at 1:02 2016/3/11
 * Codes can never be perfect!
 * Email : sxzhang2016@163.com
 */
public class HomeFragment extends Fragment {
    private static final String TAG = HomeFragment.class.getName();
    private View view;
    private LinearLayoutManager mLinearManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home, null);
        ButterKnife.bind(this, view);
        initView(view);

        return view;
    }

    private void initView(View view) {


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}

