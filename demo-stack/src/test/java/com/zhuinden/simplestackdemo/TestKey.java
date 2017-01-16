package com.zhuinden.simplestackdemo;

import android.os.Parcel;
import android.os.Parcelable;

class TestKey
        implements Parcelable {
    final String name;

    TestKey(String name) {
        this.name = name;
    }

    protected TestKey(Parcel in) {
        name = in.readString();
    }

    public static final Creator<TestKey> CREATOR = new Creator<TestKey>() {
        @Override
        public TestKey createFromParcel(Parcel in) {
            return new TestKey(in);
        }

        @Override
        public TestKey[] newArray(int size) {
            return new TestKey[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }
        TestKey key = (TestKey) o;
        return name.equals(key.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return String.format("%s{%h}", name, this);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
    }
}
