package com.example.fragmenttransitions.features.kitten.grid;

import com.example.fragmenttransitions.core.navigation.BaseFragment;
import com.example.fragmenttransitions.core.navigation.BaseKey;
import com.google.auto.value.AutoValue;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Zhuinden on 2020. 12. 18..
 */
@AutoValue
public abstract class KittenGridKey
        extends BaseKey {
    @NotNull
    @Override
    public BaseFragment instantiateFragment() {
        return new KittenGridFragment();
    }

    public static KittenGridKey create() {
        return new AutoValue_KittenGridKey();
    }
}
