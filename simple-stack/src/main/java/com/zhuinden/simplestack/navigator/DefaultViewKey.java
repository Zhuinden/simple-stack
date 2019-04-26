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

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;

/**
 * This must be implemented by the key to represent state using {@link DefaultViewKeyChanger},
 * unless {@link DefaultViewKeyChanger.LayoutInflationStrategy} and {@link DefaultViewKeyChanger.GetViewChangeHandlerStrategy} are redefined.
 *
 * It is assumed that equals() and hashCode() are properly implemented.
 */
public interface DefaultViewKey {
    @LayoutRes
    int layout();

    @NonNull
    ViewChangeHandler viewChangeHandler();
}
