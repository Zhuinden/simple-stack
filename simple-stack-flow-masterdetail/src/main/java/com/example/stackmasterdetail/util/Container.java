package com.example.stackmasterdetail.util;

import android.view.ViewGroup;

import com.zhuinden.simplestack.StateChanger;

/**
 * Created by Zhuinden on 2017.02.07..
 */

public interface Container {
    ViewGroup getCurrentChild();

    ViewGroup getContainerView();

    StateChanger createContainer();
}
