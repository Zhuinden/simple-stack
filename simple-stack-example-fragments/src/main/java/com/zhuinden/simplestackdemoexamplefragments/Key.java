package com.zhuinden.simplestackdemoexamplefragments;

import android.os.Parcelable;
import android.support.v4.app.Fragment;

import com.google.auto.value.AutoValue;

/**
 * Created by Zhuinden on 2017.02.01..
 */
public interface Key extends Parcelable {
    Fragment createFragment();

    Class<? extends Fragment> getFragmentClass();

    String getFragmentTag();
}
