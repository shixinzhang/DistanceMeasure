package net.sxkeji.xddistance.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import net.sxkeji.xddistance.PictureInfo;
import net.sxkeji.xddistance.R;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * 首页图片recycler adapter
 * Created by zhangshixin on 3/11/2016.
 */
public class HomePicturesRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<PictureInfo> mData = new ArrayList<>();
    private OnItemClickListener onItemClickListener;
    private Context mContext;

    public HomePicturesRecyclerAdapter(Context context) {
        mContext = context;
    }

    public void setmData(List<PictureInfo> data) {
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void clearData(){
        mData.clear();
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onClickListener) {
        this.onItemClickListener = onClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_recycler_picture_info, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolder) {
            final PictureInfo pictureInfo = mData.get(position);
            String path = pictureInfo.getPath();
            String distance = pictureInfo.getDistance();
            int random = 1 + (int) (Math.random() * 7);
            int imgWidth = 200 + random * 10;
            int imgHeight = 100 + random * 5;
            Log.e("Adapter ", random + " :" + pictureInfo.getPath() + " / " + pictureInfo.getDistance() + " / " + pictureInfo.getTime());


            Picasso.with(mContext)
                    .load(new File(path))
                    .placeholder(R.mipmap.default_image)
                    .error(R.mipmap.default_image)
                    .resize(imgWidth, imgHeight)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(((ViewHolder) holder).imgHeader);
            ((ViewHolder) holder).tvDistance.setText(distance + " 米");

            //set click listener here
            if (onItemClickListener == null) return;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(position, pictureInfo);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgHeader;
        public TextView tvDistance;

        public ViewHolder(View itemView) {
            super(itemView);
            imgHeader = (ImageView) itemView.findViewById(R.id.img_pic);
            tvDistance = (TextView) itemView.findViewById(R.id.tv_distance);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, PictureInfo pictureInfo);
    }
}
