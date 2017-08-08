package com.example.fragmenttransitions;

import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.view.View;

import com.google.auto.value.AutoValue;

/**
 * Created by Owner on 2017. 08. 08..
 */
@AutoValue
public abstract class DetailsKey
        extends BaseKey
        implements HasSharedElement {
    private transient Pair<View, String> sharedElement;

    @Override
    @Nullable
    public Pair<View, String> sharedElement() {
        return sharedElement;
    }

    @Override
    public abstract String transitionName();

    public abstract int kittenNumber();

    public static DetailsKey create(Pair<View, String> sharedElement, @IntRange(from = 1, to = 6) int kittenNumber) {
        DetailsKey detailsKey = new AutoValue_DetailsKey(ViewCompat.getTransitionName(sharedElement.first), kittenNumber);
        detailsKey.sharedElement = sharedElement; // see https://github.com/google/auto/blob/4016d8dbbe4de668a1849e95223d1f2447b647bc/value/userguide/howto.md#ignore
        return detailsKey;
    }

    @Override
    protected BaseFragment createFragment() {
        return new DetailsFragment();
    }
}
