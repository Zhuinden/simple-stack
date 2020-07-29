package com.zhuinden.navigationexamplefrag.screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhuinden.navigationexamplefrag.R;
import com.zhuinden.simplestackextensions.fragments.KeyedFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.ButterKnife;

/**
 * Created by Owner on 2017. 06. 29..
 */

public class NotificationFragment
        extends KeyedFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.notification_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }
}
