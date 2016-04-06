package net.sxkeji.xddistance.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.sxkeji.xddistance.R;
import net.sxkeji.xddistance.activities.AboutAppActivity;
import net.sxkeji.xddistance.activities.FeedbackActivity;
import net.sxkeji.xddistance.activities.HelpActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 设置
 * Created by sxzhang on 2016/2/25.
 * Codes can never be perfect!
 * Email : sxzhang2016@163.com
 */
public class SettingFragment extends Fragment{

    private static final String TAG = SettingFragment.class.getName();
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.rl_correct)
    RelativeLayout rlCorrect;
    @Bind(R.id.rl_save_path)
    RelativeLayout rlSavePath;
    @Bind(R.id.rl_feedback)
    RelativeLayout rlFeedback;
    @Bind(R.id.rl_help)
    RelativeLayout rlHelp;
    @Bind(R.id.rl_about)
    RelativeLayout rlAbout;

    private View view;
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_setting, null);
        mContext = getContext();
        ButterKnife.bind(this, view);
        addListeners();
        return view;
    }


    private void addListeners() {
        rlFeedback.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, FeedbackActivity.class));
            }
        });
        rlHelp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, HelpActivity.class));
            }
        });
        rlAbout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, AboutAppActivity.class));
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
