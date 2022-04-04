package com.example.fragmenttransitions.features.kitten.details;

import android.os.Parcel;

import com.example.fragmenttransitions.core.navigation.BaseFragment;
import com.example.fragmenttransitions.core.navigation.BaseKey;
import com.example.fragmenttransitions.core.sharedelements.HasSharedElement;
import com.example.fragmenttransitions.core.sharedelements.SharedElement;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.IntRange;

/**
 * Created by Zhuinden on 2020. 12. 18..
 */
public class KittenDetailsKey
        extends BaseKey
        implements HasSharedElement {
    private final SharedElement sharedElement;
    private final int kittenNumber;

    public KittenDetailsKey(SharedElement sharedElement, int kittenNumber) {
        this.sharedElement = sharedElement;
        this.kittenNumber = kittenNumber;
    }

    protected KittenDetailsKey(Parcel in) {
        sharedElement = in.readParcelable(SharedElement.class.getClassLoader());
        kittenNumber = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(sharedElement, flags);
        dest.writeInt(kittenNumber);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<KittenDetailsKey> CREATOR = new Creator<KittenDetailsKey>() {
        @Override
        public KittenDetailsKey createFromParcel(Parcel in) {
            return new KittenDetailsKey(in);
        }

        @Override
        public KittenDetailsKey[] newArray(int size) {
            return new KittenDetailsKey[size];
        }
    };

    public SharedElement sharedElement() {
        return sharedElement;
    }

    public int kittenNumber() {
        return kittenNumber;
    }

    public static KittenDetailsKey create(SharedElement sharedElement, @IntRange(from = 1, to = 6) int kittenNumber) {
        return new KittenDetailsKey(sharedElement, kittenNumber);
    }

    @NotNull
    @Override
    public BaseFragment instantiateFragment() {
        return new KittenDetailsFragment();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        KittenDetailsKey that = (KittenDetailsKey) o;

        if(kittenNumber != that.kittenNumber) {
            return false;
        }
        return sharedElement != null ? sharedElement.equals(that.sharedElement) : that.sharedElement == null;
    }

    @Override
    public int hashCode() {
        int result = sharedElement != null ? sharedElement.hashCode() : 0;
        result = 31 * result + kittenNumber;
        return result;
    }
}
