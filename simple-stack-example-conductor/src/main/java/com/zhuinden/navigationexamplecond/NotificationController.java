package com.zhuinden.navigationexamplecond;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * Created by Owner on 2017. 06. 29..
 */

public class NotificationController
        extends BaseController {
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container) {
        View view = inflater.inflate(R.layout.notification_view, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
