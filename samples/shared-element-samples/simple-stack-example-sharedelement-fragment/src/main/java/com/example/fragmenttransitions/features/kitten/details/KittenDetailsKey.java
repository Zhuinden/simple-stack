package com.example.fragmenttransitions.features.kitten.details;

import androidx.annotation.IntRange;

import com.example.fragmenttransitions.core.navigation.BaseFragment;
import com.example.fragmenttransitions.core.navigation.BaseKey;
import com.example.fragmenttransitions.core.sharedelements.HasSharedElement;
import com.example.fragmenttransitions.core.sharedelements.SharedElement;
import com.google.auto.value.AutoValue;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Zhuinden on 2020. 12. 18..
 */
@AutoValue
public abstract class KittenDetailsKey
        extends BaseKey
        implements HasSharedElement {
    public abstract SharedElement sharedElement();

    public abstract int kittenNumber();

    public static KittenDetailsKey create(SharedElement sharedElement, @IntRange(from = 1, to = 6) int kittenNumber) {
        return new AutoValue_KittenDetailsKey(sharedElement, kittenNumber);
    }

    @NotNull
    @Override
    public BaseFragment instantiateFragment() {
        return new KittenDetailsFragment();
    }
}
