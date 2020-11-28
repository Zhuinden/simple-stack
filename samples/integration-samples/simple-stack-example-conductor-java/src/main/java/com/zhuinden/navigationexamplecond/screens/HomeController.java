package com.zhuinden.navigationexamplecond.screens;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhuinden.navigationexamplecond.R;
import com.zhuinden.navigationexamplecond.application.MainActivity;
import com.zhuinden.navigationexamplecond.core.navigation.BaseController;
import com.zhuinden.navigationexamplecond.utils.ContextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by Owner on 2017. 06. 29..
 */

public class HomeController
        extends BaseController {

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        View view = inflater.inflate(R.layout.home_view, container, false);

        view.findViewById(R.id.home_button).setOnClickListener(v -> {
            ((MainActivity) ContextUtils.findActivity(view.getContext())).getBackstack().goTo(
                    // TODO: WTF is this sample?
                    OtherKey.create());
        });

        HomeKey homeKey = getKey(); // args

        return view;
    }
}
