package net.sxkeji.xddistance.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 用于list中及时回收
 * Created by zhangshixin on 4/6/2016.
 */
public class RecyclerImageView extends ImageView {
    public RecyclerImageView(Context context) {
        super(context);
    }

    public RecyclerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setImageDrawable(null);
    }
}
