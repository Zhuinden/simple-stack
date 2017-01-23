package com.zhuinden.simplestack;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;

/**
 * Created by Owner on 2017. 01. 20..
 */

public class SavedState
        implements Parcelable {
    private Parcelable key;
    private SparseArray<Parcelable> viewHierarchyState;
    private Bundle bundle;

    private SavedState() {
    }

    public Parcelable getKey() {
        return key;
    }

    public SparseArray<Parcelable> getViewHierarchyState() {
        return viewHierarchyState;
    }

    public void setViewHierarchyState(SparseArray<Parcelable> viewHierarchyState) {
        this.viewHierarchyState = viewHierarchyState;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Parcelable key;
        private SparseArray<Parcelable> viewHierarchyState = new SparseArray<>();
        private Bundle bundle;

        Builder() {
        }

        public Builder setKey(Parcelable key) {
            if(key == null) {
                throw new IllegalArgumentException("Key cannot be null");
            }
            this.key = key;
            return this;
        }

        public Builder setViewHierarchyState(SparseArray<Parcelable> viewHierarchyState) {
            if(viewHierarchyState == null) {
                throw new IllegalArgumentException("Provided sparse array for view hierarchy state cannot be null");
            }
            this.viewHierarchyState = viewHierarchyState;
            return this;
        }

        public Builder setBundle(Bundle bundle) {
            this.bundle = bundle;
            return this;
        }

        public SavedState build() {
            if(key == null) {
                throw new IllegalStateException("You cannot create a SavedState without associating a Key with it.");
            }
            SavedState savedState = new SavedState();
            savedState.key = key;
            savedState.viewHierarchyState = viewHierarchyState;
            savedState.bundle = bundle;
            return savedState;
        }
    }

    protected SavedState(Parcel in) {
        key = in.readParcelable(getClass().getClassLoader());
        // noinspection unchecked
        viewHierarchyState = in.readSparseArray(getClass().getClassLoader());
        boolean hasBundle = in.readByte() > 0;
        if(hasBundle) {
            bundle = in.readBundle(getClass().getClassLoader());
        }
    }

    public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
        @Override
        public SavedState createFromParcel(Parcel in) {
            return new SavedState(in);
        }

        @Override
        public SavedState[] newArray(int size) {
            return new SavedState[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(key, flags);
        // noinspection unchecked
        SparseArray<Object> sparseArray = (SparseArray)viewHierarchyState;
        dest.writeSparseArray(sparseArray);
        dest.writeByte(bundle != null ? (byte)0x01 : 0x00);
        if(bundle != null) {
            dest.writeBundle(bundle);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(!(obj instanceof SavedState)) {
            return false;
        }
        return ((SavedState)obj).getKey().equals(this.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
