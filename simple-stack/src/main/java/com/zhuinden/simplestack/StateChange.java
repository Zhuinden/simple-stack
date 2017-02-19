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

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.annotation.Retention;
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

    StateChange(List<Object> previousState, List<Object> newState, @StateChangeDirection int direction) {
        this.previousState = previousState;
        this.newState = newState;
        this.direction = direction;
    }

    List<Object> previousState;
    List<Object> newState;
    int direction;

    /**
     * The previous state from before the new keys were set.
     * If empty, then this is an initialize {@link StateChange}.
     *
     * @return the previous state.
     */
    @NonNull
    public List<Object> getPreviousState() {
        return previousState;
    }

    /**
     * The new state after the state change is complete.
     *
     * @return the new state.
     */
    @NonNull
    public List<Object> getNewState() {
        return newState;
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
    public <T extends Object> T topPreviousState() {
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
    public <T extends Object> T topNewState() {
        // noinspection unchecked
        return (T) newState.get(newState.size() - 1);
    }
}
