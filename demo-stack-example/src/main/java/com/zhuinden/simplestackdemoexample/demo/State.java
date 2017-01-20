package com.zhuinden.simplestackdemoexample.demo;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;

/**
 * Created by Owner on 2017. 01. 20..
 */

public class State implements Parcelable {
    private Key key;
    private SparseArray<Parcelable> viewHierarchyState;
    private Bundle bundle;

    State() {
    }

    public Key getKey() {
        return key;
    }

    public SparseArray<Parcelable> getViewHierarchyState() {
        return viewHierarchyState;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Key key;
        private SparseArray<Parcelable> viewHierarchyState;
        private Bundle bundle;

        Builder() {
        }

        public Builder setKey(Key key) {
            if(key == null) {
                throw new IllegalArgumentException("Key cannot be null");
            }
            this.key = key;
            return this;
        }

        public Builder setViewHierarchyState(SparseArray<Parcelable> viewHierarchyState) {
            this.viewHierarchyState = viewHierarchyState;
            return this;
        }

        public Builder setBundle(Bundle bundle) {
            this.bundle = bundle;
            return this;
        }

        public State build() {
            State state = new State();
            state.key = key;
            state.viewHierarchyState = viewHierarchyState;
            state.bundle = bundle;
            return state;
        }
    }

    protected State(Parcel in) {
        key = in.readParcelable(Key.class.getClassLoader());
        // noinspection unchecked
        viewHierarchyState = in.readSparseArray(getClass().getClassLoader());
        boolean hasBundle = in.readByte() > 0;
        if(hasBundle) {
            bundle = in.readBundle(getClass().getClassLoader());
        }
    }

    public static final Creator<State> CREATOR = new Creator<State>() {
        @Override
        public State createFromParcel(Parcel in) {
            return new State(in);
        }

        @Override
        public State[] newArray(int size) {
            return new State[size];
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
}
