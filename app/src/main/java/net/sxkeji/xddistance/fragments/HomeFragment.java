package net.sxkeji.xddistance.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.sxkeji.xddistance.BaseApplication;
import net.sxkeji.xddistance.DaoSession;
import net.sxkeji.xddistance.PictureInfo;
import net.sxkeji.xddistance.PictureInfoDao;
import net.sxkeji.xddistance.R;
import net.sxkeji.xddistance.activities.PhotoDetailActivity;
import net.sxkeji.xddistance.adapters.HomePicturesRecyclerAdapter;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.dao.query.Query;

/**
 * 主页的fragment
 * Created by sxzhang on 2016/2/25.
 * Updated at 1:02 2016/3/11
 * Codes can never be perfect!
 * Email : sxzhang2016@163.com
 */
public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = HomeFragment.class.getName();
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.recycler)
    RecyclerView recycler;
    @Bind(R.id.rl_empty)
    RelativeLayout rlEmpty;
    @Bind(R.id.img_empty)
    ImageView imgEmpty;
    @Bind(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    private View view;
    private StaggeredGridLayoutManager mStaggerGridManager;
    private HomePicturesRecyclerAdapter mRecyclerAdapter;
    private boolean couldClear = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home, null);
        ButterKnife.bind(this, view);
        initView();

        return view;
    }

    private void initView() {

        ArrayList<PictureInfo> picturesData = getPicturesData();
        if (picturesData == null || picturesData.size() == 0) {
            Log.e(TAG, "local db has no pictures!");
            rlEmpty.setVisibility(View.VISIBLE);
        } else {
            initRecyclesView(picturesData);
        }

    }

    private void initRecyclesView(ArrayList<PictureInfo> picsData) {
        swipeRefresh.setOnRefreshListener(this);
        swipeRefresh.setColorSchemeResources(android.R.color.holo_green_light, android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        swipeRefresh.setVisibility(View.VISIBLE);
        mStaggerGridManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recycler.setLayoutManager(mStaggerGridManager);
        mRecyclerAdapter = new HomePicturesRecyclerAdapter(getActivity());
        mRecyclerAdapter.setOnItemClickListener(new HomePicturesRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, PictureInfo pictureInfo) {
                Intent intent = new Intent(getActivity(), PhotoDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("photoBean", pictureInfo);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        mRecyclerAdapter.setmData(picsData);
        recycler.setAdapter(mRecyclerAdapter);
    }

    private ArrayList<PictureInfo> getPicturesData() {
        ArrayList<PictureInfo> queryList;
        DaoSession daoSession = BaseApplication.getDaoSession();
        PictureInfoDao pictureInfoDao = daoSession.getPictureInfoDao();
        Query<PictureInfo> query = pictureInfoDao.queryBuilder().orderAsc(PictureInfoDao.Properties.Time).build();
        queryList = (ArrayList<PictureInfo>) query.list();
        return queryList;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onRefresh() {
        ArrayList<PictureInfo> picturesData = getPicturesData();
        mRecyclerAdapter.clearData();
        mRecyclerAdapter.setmData(picturesData);
        swipeRefresh.setRefreshing(false);
    }

//    public interface

}

