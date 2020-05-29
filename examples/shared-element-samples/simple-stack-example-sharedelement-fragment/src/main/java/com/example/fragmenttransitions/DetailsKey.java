package com.example.fragmenttransitions;

import android.support.annotation.IntRange;

import com.google.auto.value.AutoValue;

/**
 * Created by Owner on 2017. 08. 08..
 */
@AutoValue
public abstract class DetailsKey
        extends BaseKey
        implements HasSharedElement {
    public abstract SharedElement sharedElement();

    public abstract int kittenNumber();

    public static DetailsKey create(SharedElement sharedElement, @IntRange(from = 1, to = 6) int kittenNumber) {
        return new AutoValue_DetailsKey(sharedElement, kittenNumber);
    }

    @Override
    protected BaseFragment createFragment() {
        return new DetailsFragment();
    }
}
