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
package com.zhuinden.simplestack.navigator;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

/**
 * An interface that represents the view change when a state change occurs.
 *
 * Specified in {@link DefaultViewKey}, if {@link DefaultStateChanger} is used with default {@link DefaultStateChanger.GetViewChangeHandlerStrategy}.
 */
public interface ViewChangeHandler {
    /**
     * It must be called to signal that the view change is complete.
     */
    interface ViewChangeCallback {
        void onCompleted();
    }

    /**
     * Perform the view change. The previous view must be removed from the container, and the new view must be added to the container.
     * When complete, the completion callback must be called.
     *
     * @param container          the container for the views
     * @param previousView       the previous view
     * @param newView            the new view
     * @param direction          the direction (from the StateChange)
     * @param viewChangeCallback the callback that must be called when the view change is complete.
     */
    void performViewChange(@NonNull final ViewGroup container, @NonNull final View previousView, @NonNull final View newView, final int direction, @NonNull final ViewChangeCallback viewChangeCallback);
}
