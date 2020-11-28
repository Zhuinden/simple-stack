package com.zhuinden.navigationexamplecond.screens;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhuinden.navigationexamplecond.R;
import com.zhuinden.navigationexamplecond.core.navigation.BaseController;

import androidx.annotation.Nullable;

/**
 * Created by Owner on 2017. 06. 29..
 */

public class OtherController
        extends BaseController {
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container) {
        View view = inflater.inflate(R.layout.other_view, container, false);
        return view;
    }
}
