package net.sxkeji.xddistance.activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import net.sxkeji.xddistance.PictureInfo;
import net.sxkeji.xddistance.R;
import net.sxkeji.xddistance.utils.FileUtils;
import net.sxkeji.xddistance.views.RecyclerImageView;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * 查看图片
 * Created by zhangshixin on 4/6/2016.
 */
public class PhotoDetailActivity extends Activity {
    @Bind(R.id.tv_distance)
    TextView tvDistance;
    @Bind(R.id.iv_photo)
    RecyclerImageView ivPhoto;
    private PhotoViewAttacher mAttacher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        PictureInfo pictureInfo = (PictureInfo) getIntent().getSerializableExtra("photoBean");
        String path = pictureInfo.getPath();
        String distance = pictureInfo.getDistance();

        if (!TextUtils.isEmpty(distance)) {
            tvDistance.setText(distance + " 米");
        }

        Picasso.with(this)
                .load(new File(path))
                .placeholder(R.mipmap.default_image)
                .error(R.mipmap.default_image)
                .resize(FileUtils.getWindowDisplay(this).getHeight(),FileUtils.getWindowDisplay(this).getWidth())   //之前是横屏拍的，所以竖屏时相反
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(ivPhoto);

        mAttacher = new PhotoViewAttacher(ivPhoto);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ivPhoto != null){
            ivPhoto = null;
        }
        if (mAttacher != null){
            mAttacher.cleanup();
            mAttacher = null;
        }
    }
}
