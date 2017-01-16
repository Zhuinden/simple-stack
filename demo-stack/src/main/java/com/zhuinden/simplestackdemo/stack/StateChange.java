package com.zhuinden.simplestackdemo.stack;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;
/**
 * Created by Owner on 2017. 01. 12..
 */

public class StateChange {
    public enum Direction {
        FORWARD,
        BACKWARD,
        REPLACE
    }

    StateChange(List<Parcelable> previousState, List<Parcelable> newState, Direction direction) {
        this.previousState = previousState;
        this.newState = newState;
        this.direction = direction;
    }

    List<Parcelable> previousState;
    List<Parcelable> newState;
    Direction direction;

    public List<Parcelable> getPreviousState() {
        return previousState;
    }

    public List<Parcelable> getNewState() {
        return newState;
    }

    public Direction getDirection() {
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
}
