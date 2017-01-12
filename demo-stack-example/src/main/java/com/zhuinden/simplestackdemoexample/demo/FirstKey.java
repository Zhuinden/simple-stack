package com.zhuinden.simplestackdemoexample.demo;

import android.os.Parcel;
import android.os.Parcelable;
import com.zhuinden.simplestackdemoexample.R;
/**
 * Created by Owner on 2017. 01. 12..
 */

public class FirstKey implements Parcelable, Key {
    public FirstKey() {
    }

    protected FirstKey(Parcel in) {
    }

    public static final Creator<FirstKey> CREATOR = new Creator<FirstKey>() {
        @Override
        public FirstKey createFromParcel(Parcel in) {
            return new FirstKey(in);
        }

        @Override
        public FirstKey[] newArray(int size) {
            return new FirstKey[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public int layout() {
        return R.layout.path_first;
    }

    @Override
    public int hashCode() {
        return FirstKey.class.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        return obj instanceof FirstKey;
    }
}
