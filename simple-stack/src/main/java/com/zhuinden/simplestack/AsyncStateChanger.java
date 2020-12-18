/*
 * Copyright 2020 Gabor Varadi
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

import javax.annotation.Nonnull;

/**
 * The {@link AsyncStateChanger} is a {@link StateChanger} that consumes {@link StateChange#isTopNewKeyEqualToPrevious()} as a no-op,
 * but still exposes {@link Callback}, expecting the handling of the state change to be asynchronous.
 *
 * Whenever a {@link StateChange} happens with a new top key, the {@link NavigationHandler} is called.
 */
public class AsyncStateChanger
        implements StateChanger {
    private final NavigationHandler navigationHandler;

    public AsyncStateChanger(@Nonnull NavigationHandler navigationHandler) {
        //noinspection ConstantConditions
        if(navigationHandler == null) {
            throw new NullPointerException("navigationHandler should not be null!");
        }
        this.navigationHandler = navigationHandler;
    }

    @Override
    public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
        if(stateChange.isTopNewKeyEqualToPrevious()) {
            completionCallback.stateChangeComplete();
            return;
        }
        navigationHandler.onNavigationEvent(stateChange, completionCallback);
    }

    /**
     * Invoked whenever a navigation action happens.
     */
    public interface NavigationHandler {
        /**
         * Triggered when a {@link StateChange} happens.
         *
         * @param stateChange the state change
         * @param completionCallback the completion callback
         */
        void onNavigationEvent(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback);
    }
}
