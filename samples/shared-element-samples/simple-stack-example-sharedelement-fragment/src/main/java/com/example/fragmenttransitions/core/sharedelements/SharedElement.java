package com.example.fragmenttransitions.core.sharedelements;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author zhuinden
 */
public class SharedElement
        implements Parcelable {
    private final String sourceTransitionName;

    private final String targetTransitionName;

    public SharedElement(String sourceTransitionName, String targetTransitionName) {
        this.sourceTransitionName = sourceTransitionName;
        this.targetTransitionName = targetTransitionName;
    }

    protected SharedElement(Parcel in) {
        sourceTransitionName = in.readString();
        targetTransitionName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sourceTransitionName);
        dest.writeString(targetTransitionName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SharedElement> CREATOR = new Creator<SharedElement>() {
        @Override
        public SharedElement createFromParcel(Parcel in) {
            return new SharedElement(in);
        }

        @Override
        public SharedElement[] newArray(int size) {
            return new SharedElement[size];
        }
    };

    public String sourceTransitionName() {
        return sourceTransitionName;
    }

    public String targetTransitionName() {
        return targetTransitionName;
    }

    public static SharedElement create(String sourceTransitionName, String targetTransitionName) {
        return new SharedElement(sourceTransitionName, targetTransitionName);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        SharedElement that = (SharedElement) o;

        if(sourceTransitionName != null ? !sourceTransitionName.equals(that.sourceTransitionName) : that.sourceTransitionName != null) {
            return false;
        }
        return targetTransitionName != null ? targetTransitionName.equals(that.targetTransitionName) : that.targetTransitionName == null;
    }

    @Override
    public int hashCode() {
        int result = sourceTransitionName != null ? sourceTransitionName.hashCode() : 0;
        result = 31 * result + (targetTransitionName != null ? targetTransitionName.hashCode() : 0);
        return result;
    }
}
