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

/**
 * The StateChanger handles the {@link StateChange}s that occur within the {@link Backstack}.
 *
 * {@link StateChange} cannot be cancelled, a {@link StateChange} set during an active {@link StateChange} gets enqueued.
 */
public interface StateChanger {
    /**
     * When the {@link StateChange} is complete, then the callback must be called.
     */
    interface Callback {
        void stateChangeComplete();
    }

    /**
     * This is called when a {@link StateChange} occurs.
     * When the {@link StateChange} is handled, {@link Callback#stateChangeComplete()} must be called.
     *
     * @param stateChange        the currently active state change in progress.
     * @param completionCallback the callback that must be called to signal that the state change is completed.
     */
    void handleStateChange(StateChange stateChange, Callback completionCallback);
}
