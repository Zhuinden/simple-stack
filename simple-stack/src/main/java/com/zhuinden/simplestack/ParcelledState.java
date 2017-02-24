package com.zhuinden.simplestack;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;

/**
 * Created by Owner on 2017. 02. 22..
 */


class ParcelledState
        implements Parcelable {
    Parcelable parcelableKey;
    SparseArray<Parcelable> viewHierarchyState;
    Bundle viewBundle;
    Bundle serviceBundle;

    ParcelledState() {
    }

    protected ParcelledState(Parcel in) {
        parcelableKey = in.readParcelable(getClass().getClassLoader());
        // noinspection unchecked
        viewHierarchyState = in.readSparseArray(getClass().getClassLoader());
        boolean hasViewBundle = in.readByte() > 0;
        if(hasViewBundle) {
            viewBundle = in.readBundle(getClass().getClassLoader());
        }
        boolean hasStateBundle = in.readByte() > 0;
        if(hasStateBundle) {
            serviceBundle = in.readBundle(getClass().getClassLoader());
        }
    }

    public static final Creator<ParcelledState> CREATOR = new Creator<ParcelledState>() {
        @Override
        public ParcelledState createFromParcel(Parcel in) {
            return new ParcelledState(in);
        }

        @Override
        public ParcelledState[] newArray(int size) {
            return new ParcelledState[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(parcelableKey, flags);
        // noinspection unchecked
        SparseArray<Object> sparseArray = (SparseArray) viewHierarchyState;
        dest.writeSparseArray(sparseArray);
        dest.writeByte(viewBundle != null ? (byte) 0x01 : 0x00);
        if(viewBundle != null) {
            dest.writeBundle(viewBundle);
        }
        dest.writeByte(serviceBundle != null ? (byte) 0x01 : 0x00);
        if(serviceBundle != null) {
            dest.writeBundle(serviceBundle);
        }
    }
}