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

import android.os.Parcelable;

/**
 * An interface to allow using any key in the backstack, as long as it is possible to persist and restore it as a Parcelable.
 */
public interface KeyParceler {
    /**
     * Transforms the input parameter into a Parcelable.
     *
     * @param object The key that is to be transformed into a Parcelable.
     * @return the Parcelable the key is transformed into.
     */
    Parcelable toParcelable(Object object);

    /**
     * Creates the original key based on the input Parcelable.
     *
     * @param parcelable the Parcelable the key was transformed into.
     * @return The key that was transformed into a Parcelable.
     */
    Object fromParcelable(Parcelable parcelable);
}
