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

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

/**
 * The KeyChanger handles the {@link KeyChange}s that occur within the {@link Backstack}.
 *
 * {@link KeyChange} cannot be cancelled, a {@link KeyChange} set during an active {@link KeyChange} gets enqueued.
 */
public interface KeyChanger {
    /**
     * When the {@link KeyChange} is complete, then the callback must be called.
     *
     * The callback MUST be called on the thread where the backstack was created.
     */
    interface Callback {
        @MainThread
        void keyChangeComplete();
    }

    /**
     * This is called when a {@link KeyChange} occurs.
     * When the {@link KeyChange} is handled, {@link Callback#keyChangeComplete()} must be called.
     *
     * @param keyChange        the currently active key change in progress.
     * @param completionCallback the callback that must be called to signal that the key change is completed.
     */
    void handleKeyChange(@NonNull KeyChange keyChange, @NonNull Callback completionCallback);
}
