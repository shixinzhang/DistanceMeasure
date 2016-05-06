package net.sxkeji.xddistance.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.sxkeji.xddistance.R;
import net.sxkeji.xddistance.activities.AboutAppActivity;
import net.sxkeji.xddistance.activities.FeedbackActivity;
import net.sxkeji.xddistance.activities.HelpActivity;
import net.sxkeji.xddistance.utils.Constant;
import net.sxkeji.xddistance.utils.SharedPreUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 设置
 * Created by sxzhang on 2016/2/25.
 * Codes can never be perfect!
 * Email : sxzhang2016@163.com
 */
public class SettingFragment extends Fragment {

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
    private int distanceFactor;
    private int factor; //距离因子

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_setting, null);
        mContext = getContext();
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        initData();
        addListeners();
    }

    private void initData() {
    }

    private void addListeners() {
        rlCorrect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog(true);
            }
        });
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

    /**
     * 显示底部提示框
     *
     * @param isCalibration 是否为精度校准
     */
    private void showBottomSheetDialog(boolean isCalibration) {
        factor = SharedPreUtil.readInt(Constant.FACTOR, 34);

        final BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        View view = View.inflate(getContext(), R.layout.dialog_calibration, null);
        dialog.setContentView(view);

        //********************解决BottomSheetDialog默认显示不正确的bug ↓
        view.measure(0, 0);
        float peekHeight = view.getMeasuredHeight();
        View parent = (View) view.getParent();
        final BottomSheetBehavior behavior = BottomSheetBehavior.from(view.findViewById(R.id.design_bottom_sheet));
        behavior.setPeekHeight((int) peekHeight);
        CoordinatorLayout.LayoutParams layoutParams =
                (CoordinatorLayout.LayoutParams) parent.getLayoutParams();
        layoutParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        parent.setLayoutParams(layoutParams);
        //********************解决BottomSheetDialog默认显示不正确的bug ↑

        dialog.show();

        if (isCalibration) {
            final LinearLayout llCalibration = (LinearLayout) view.findViewById(R.id.ll_calibration);
            llCalibration.setVisibility(View.VISIBLE);
            TextView tvFactor = (TextView) view.findViewById(R.id.tv_factor);
            final TextView tvFactorChange = (TextView) view.findViewById(R.id.tv_factor_change);
            Button btnSave = (Button) view.findViewById(R.id.btn_save);
            ImageView ivDecrease = (ImageView) view.findViewById(R.id.iv_decrease);
            ImageView ivIncrease = (ImageView) view.findViewById(R.id.iv_increase);

            tvFactor.setText(String.valueOf(factor));
            tvFactorChange.setText(String.valueOf(factor));
            ivDecrease.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    factor--;
                    tvFactorChange.setText(String.valueOf(factor));
                }
            });
            ivIncrease.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    factor++;
                    tvFactorChange.setText(String.valueOf(factor));
                }
            });
            btnSave.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreUtil.writeInt(Constant.FACTOR, factor);
                    Log.e(TAG, "save factor " + factor);
                    dialog.cancel();
                }
            });
        } else {

        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
