package com.zhuinden.simplestack;

import android.os.Parcelable;

/**
 * Created by Owner on 2017. 02. 28..
 */
public class DefaultKeyParceler
        implements KeyParceler {
    @Override
    public Parcelable toParcelable(Object object) {
        return (Parcelable) object;
    }

    @Override
    public Object fromParcelable(Parcelable parcelable) {
        return parcelable;
    }
}
