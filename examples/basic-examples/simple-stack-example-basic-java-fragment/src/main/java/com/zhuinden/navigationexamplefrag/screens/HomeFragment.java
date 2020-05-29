package com.zhuinden.navigationexamplefrag.screens;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhuinden.navigationexamplefrag.R;
import com.zhuinden.navigationexamplefrag.core.navigation.BaseFragment;
import com.zhuinden.simplestack.navigator.Navigator;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Owner on 2017. 06. 29..
 */

public class HomeFragment
        extends BaseFragment {
    @OnClick(R.id.home_button)
    public void goToOtherView(View view) {
        Navigator.getBackstack(getActivity()).goTo(OtherKey.create());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        HomeKey homeKey = getKey(); // get args
    }
}
