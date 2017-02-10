/*
 * Copyright 2014 Square Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.zhuinden.simplestack;

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
