/*
 * Copyright 2017 Gabor Varadi
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

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.annotation.Retention;
import java.util.List;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Provides the previous and the new state when the state changes within the backstack.
 *
 * Created by Zhuinden on 2017. 01. 12..
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

    public
    @NonNull
    List<Parcelable> getPreviousState() {
        return previousState;
    }

    public
    @NonNull
    List<Parcelable> getNewState() {
        return newState;
    }

    public @StateChangeDirection int getDirection() {
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
    public Context createContext(Context base, Parcelable key) {
        return new KeyContextWrapper(base, key);
    }
}
