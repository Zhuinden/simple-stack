package com.zhuinden.simplestack;

import android.os.Parcelable;

/**
 * Default key parceler implementation that assumes your keys are Parcelable.
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
