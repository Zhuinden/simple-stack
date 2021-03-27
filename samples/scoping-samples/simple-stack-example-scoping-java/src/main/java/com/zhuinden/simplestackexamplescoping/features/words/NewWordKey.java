package com.zhuinden.simplestackexamplescoping.features.words;

import com.google.auto.value.AutoValue;
import com.zhuinden.simplestackexamplescoping.core.navigation.BaseKey;

import javax.annotation.Nonnull;

import androidx.fragment.app.Fragment;

@AutoValue
public abstract class NewWordKey
        extends BaseKey {
    public static NewWordKey create() {
        return new AutoValue_NewWordKey();
    }

    @Nonnull
    @Override
    protected Fragment instantiateFragment() {
        return new NewWordFragment();
    }
}
