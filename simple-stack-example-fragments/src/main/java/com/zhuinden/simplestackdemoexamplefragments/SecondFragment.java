package com.zhuinden.simplestackdemoexamplefragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhuinden.simplestack.Backstack;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Zhuinden on 2017.02.01..
 */

public class SecondFragment extends Fragment {
    @OnClick(R.id.second_go_to_third)
    public void goToThird() {
        BackstackService.getBackstack(getContext()).goTo(ThirdKey.create());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.path_second, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
