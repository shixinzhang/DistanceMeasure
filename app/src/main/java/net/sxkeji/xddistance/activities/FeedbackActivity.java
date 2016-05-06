package net.sxkeji.xddistance.activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.sxkeji.xddistance.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 问题反馈
 * Created by zhangshixin on 4/6/2016.
 */
public class FeedbackActivity extends Activity {
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.ll_feedback)
    LinearLayout llFeedback;
    @Bind(R.id.et_input)
    EditText etInput;
    @Bind(R.id.iv_send)
    ImageView ivSend;
    private String feedBackStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);

        setListener();
    }

    private void setListener() {
        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedBackStr = etInput.getText().toString();
                if (TextUtils.isEmpty(feedBackStr)) {
                    showToast("请输入要反馈的内容");
                }else {
                    etInput.setText("");
                    TextView textView = new TextView(FeedbackActivity.this);
                    textView.setText(feedBackStr);
                    llFeedback.addView(textView);
                    showToast("反馈成功");
                }
            }
        });
    }

    void showToast(String str){
        if (!TextUtils.isEmpty(str)){
            Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
        }
    }
}
