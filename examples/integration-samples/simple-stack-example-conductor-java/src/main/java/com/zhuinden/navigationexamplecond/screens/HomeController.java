package com.zhuinden.navigationexamplecond.screens;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhuinden.navigationexamplecond.R;
import com.zhuinden.navigationexamplecond.application.MainActivity;
import com.zhuinden.navigationexamplecond.core.navigation.BaseController;
import com.zhuinden.navigationexamplecond.utils.ContextUtils;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Owner on 2017. 06. 29..
 */

public class HomeController
        extends BaseController {
    @OnClick(R.id.home_button)
    public void goToOtherView(View view) {
        ((MainActivity) ContextUtils.findActivity(view.getContext())).getBackstack().goTo(OtherKey.create());
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        View view = inflater.inflate(R.layout.home_view, container, false);
        ButterKnife.bind(this, view);

        HomeKey homeKey = getKey(); // args

        return view;
    }
}
