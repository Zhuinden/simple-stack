package com.example.fragmenttransitions.features.kitten.grid;

import android.os.Parcel;

import com.example.fragmenttransitions.core.navigation.BaseFragment;
import com.example.fragmenttransitions.core.navigation.BaseKey;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.Nullable;

/**
 * Created by Zhuinden on 2020. 12. 18..
 */
public class KittenGridKey
        extends BaseKey {
    public KittenGridKey() {
    }

    protected KittenGridKey(Parcel in) {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<KittenGridKey> CREATOR = new Creator<KittenGridKey>() {
        @Override
        public KittenGridKey createFromParcel(Parcel in) {
            return new KittenGridKey(in);
        }

        @Override
        public KittenGridKey[] newArray(int size) {
            return new KittenGridKey[size];
        }
    };

    @NotNull
    @Override
    public BaseFragment instantiateFragment() {
        return new KittenGridFragment();
    }

    public static KittenGridKey create() {
        return new KittenGridKey();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj != null && obj instanceof KittenGridKey;
    }

    @Override
    public int hashCode() {
        return KittenGridKey.class.hashCode();
    }
}
