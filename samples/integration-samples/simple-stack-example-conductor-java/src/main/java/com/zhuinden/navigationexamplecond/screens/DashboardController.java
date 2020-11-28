package com.zhuinden.navigationexamplecond.screens;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhuinden.navigationexamplecond.R;
import com.zhuinden.navigationexamplecond.core.navigation.BaseController;

import androidx.annotation.NonNull;

/**
 * Created by Owner on 2017. 06. 29..
 */

public class DashboardController
        extends BaseController {
    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.dashboard_view, container, false);
        return view;
    }
}
