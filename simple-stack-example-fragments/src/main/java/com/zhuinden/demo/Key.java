package com.zhuinden.demo;

import android.os.Parcelable;
import android.support.v4.app.Fragment;

/**
 * Created by Zhuinden on 2017.02.01..
 */
public interface Key extends Parcelable {
    Fragment createFragment();

    String getFragmentTag();
}
