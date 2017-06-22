package com.example.a0_0.app_shixun.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.a0_0.app_shixun.MainActivity;
import com.example.a0_0.app_shixun.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_wangluo extends Fragment implements View.OnClickListener{
    protected static final String TAG=Fragment_wangluo.class.getSimpleName();

    private MainActivity mActivity;

    private LinearLayout searchShowLinearLayout;
    public Fragment_wangluo() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_wangluo, container, false);
    }

    @Override
    public void onClick(View v) {

    }
}
