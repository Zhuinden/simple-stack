package com.example.fragmenttransitions.core.sharedelements;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;

/**
 * @author zhuinden
 */
@AutoValue
public abstract class SharedElement implements Parcelable {
    public abstract String sourceTransitionName();

    public abstract String targetTransitionName();

    public static SharedElement create(String sourceTransitionName, String targetTransitionName) {
        return new AutoValue_SharedElement(sourceTransitionName, targetTransitionName);
    }
}
