package com.zhuinden.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhuinden.simplestackdemoexamplefragments.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Zhuinden on 2017.02.01..
 */

public class FirstFragment extends Fragment {
    @OnClick(R.id.first_button)
    public void goToNext() {
        BackstackService.getBackstack(getContext()).goTo(SecondKey.create());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.demo_path_first, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
