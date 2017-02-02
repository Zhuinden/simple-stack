package com.zhuinden.simplestack;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.annotation.Retention;
import java.util.List;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Created by Owner on 2017. 01. 12..
 */

public class StateChange {
    @Retention(SOURCE)
    @IntDef({FORWARD, BACKWARD, REPLACE})
    @interface StateChangeDirection {
    }

    public static final int REPLACE = 0;
    public static final int BACKWARD = -1;
    public static final int FORWARD = 1;

    StateChange(List<Parcelable> previousState, List<Parcelable> newState, @StateChangeDirection int direction) {
        this.previousState = previousState;
        this.newState = newState;
        this.direction = direction;
    }

    List<Parcelable> previousState;
    List<Parcelable> newState;
    int direction;

    public List<Parcelable> getPreviousState() {
        return previousState;
    }

    public List<Parcelable> getNewState() {
        return newState;
    }

    public int getDirection() {
        return direction;
    }

    @Nullable
    public <T extends Parcelable> T topPreviousState() {
        if(previousState.size() > 0) {
            // noinspection unchecked
            return (T)previousState.get(previousState.size()-1);
        } else {
            return null;
        }
    }

    @NonNull
    public <T extends Parcelable> T topNewState() {
        // noinspection unchecked
        return (T)newState.get(newState.size()-1);
    }

    @NonNull
    public KeyContextWrapper createContext(Context base, Parcelable key) {
        return new KeyContextWrapper(base, key);
    }
}
