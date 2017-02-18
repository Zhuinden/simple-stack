package com.zhuinden.simplestack;

import android.os.Parcelable;

/**
 * An interface to allow using any key in the backstack, as long as it is possible to persist and restore it as a Parcelable.
 */
public interface KeyParceler {
    /**
     * Transforms the input parameter into a Parcelable.
     *
     * @param object The key that is to be transformed into a Parcelable.
     * @return the Parcelable the key is transformed into.
     */
    Parcelable toParcelable(Object object);

    /**
     * Creates the original key based on the input Parcelable.
     *
     * @param parcelable the Parcelable the key was transformed into.
     * @return The key that was transformed into a Parcelable.
     */
    Object fromParcelable(Parcelable parcelable);
}
