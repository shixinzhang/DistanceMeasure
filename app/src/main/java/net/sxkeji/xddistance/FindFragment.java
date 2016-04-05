package net.sxkeji.xddistance;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by sxzhang on 2016/2/25.
 * Codes can never be perfect!
 */
public class FindFragment extends Fragment {

    private static final String TAG = FindFragment.class.getName();

    private View view;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_find, null);
        return view;
    }
}
