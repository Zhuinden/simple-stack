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
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.annotation.Retention;
import java.util.LinkedList;
import java.util.List;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Contains the previous and the new state that represents the change in state.
 */
public class StateChange {
    @Retention(SOURCE)
    @IntDef({FORWARD, BACKWARD, REPLACE})
    @interface StateChangeDirection {
    }

    public static final int REPLACE = 0;
    public static final int BACKWARD = -1;
    public static final int FORWARD = 1;

    StateChange(Backstack backstack, List<Object> previousState, List<Object> newState, @StateChangeDirection int direction) {
        this.backstack = backstack;
        this.previousState = previousState;
        this.newState = newState;
        this.direction = direction;
    }

    Backstack backstack;
    List<Object> previousState;
    List<Object> newState;
    int direction;

    /**
     * The backstack this state change was executed by.
     *
     * @return the backstack
     */
    @NonNull
    public Backstack backstack() {
        return backstack;
    }

    // create a copy list where each item is casted to <T>
    private <T> History<T> createParametricCopyList(List<Object> list) {
        List<T> copyList = new LinkedList<>();
        for(Object key : list) {
            // noinspection unchecked
            copyList.add((T)key);
        }
        return History.from(copyList);
    }

    /**
     * The previous state from before the new keys were set.
     * If empty, then this is an initialize {@link StateChange}.
     *
     * @param <T> the type of the key
     *
     * @return the previous state.
     */
    @NonNull
    public <T> History<T> getPreviousState() {
        return createParametricCopyList(previousState);
    }

    /**
     * The new state after the state change is complete.
     *
     * @param <T> the type of the key
     *
     * @return the new state.
     */
    @NonNull
    public <T> History<T> getNewState() {
        return createParametricCopyList(newState);
    }

    /**
     * The direction of the state change.
     *
     * @return the direction: FORWARD, BACKWARD or REPLACE.
     */
    @StateChangeDirection
    public int getDirection() {
        return direction;
    }

    /**
     * Provides the top of the previous state.
     *
     * @return the last element in previous state, or null if empty.
     */
    @Nullable
    public <T> T topPreviousState() {
        if(previousState.size() > 0) {
            // noinspection unchecked
            return (T) previousState.get(previousState.size() - 1);
        } else {
            return null;
        }
    }

    /**
     * Provides the top of the new state.
     *
     * @return the last element in new state.
     */
    @NonNull
    public <T> T topNewState() {
        // noinspection unchecked
        return (T) newState.get(newState.size() - 1);
    }

    /**
     * Creates a {@link KeyContextWrapper} using the provided key.
     *
     * @param base the context used as base for the new context wrapper.
     * @param key  the key this context is associated with.
     * @return the context to use used with LayoutInflater.from().
     */
    @NonNull
    public Context createContext(@NonNull Context base, @NonNull Object key) {
        return new KeyContextWrapper(base, key);
    }
}
