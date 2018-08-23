package com.zhuinden.navigationexamplefrag;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Owner on 2017. 06. 29..
 */

public class HomeFragment
        extends BaseFragment {
    @OnClick(R.id.home_button)
    public void goToOtherView(View view) {
        MainActivity.get(view.getContext()).navigateTo(OtherKey.create());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        HomeKey homeKey = getKey();
    }
}
