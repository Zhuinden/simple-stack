package com.zhuinden.simplestack;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;

import com.zhuinden.statebundle.StateBundle;

/**
 * Created by Owner on 2017. 02. 28..
 */

class ParcelledState
        implements Parcelable {
    Parcelable parcelableKey;
    SparseArray<Parcelable> viewHierarchyState;
    StateBundle bundle;
    StateBundle viewBundle;

    ParcelledState() {
    }

    protected ParcelledState(Parcel in) {
        parcelableKey = in.readParcelable(getClass().getClassLoader());
        // noinspection unchecked
        viewHierarchyState = in.readSparseArray(getClass().getClassLoader());
        boolean hasBundle = in.readByte() > 0;
        if(hasBundle) {
            bundle = in.readParcelable(getClass().getClassLoader());
        }
        boolean hasViewBundle = in.readByte() > 0;
        if(hasViewBundle) {
            viewBundle = in.readParcelable(getClass().getClassLoader());
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
        dest.writeByte(bundle != null ? (byte) 0x01 : 0x00);
        if(bundle != null) {
            dest.writeParcelable(bundle, 0);
        }
        dest.writeByte(viewBundle != null ? (byte) 0x01 : 0x00);
        if(viewBundle != null) {
            dest.writeParcelable(viewBundle, 0);
        }
    }
}