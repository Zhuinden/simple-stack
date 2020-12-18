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

import java.util.List;

import javax.annotation.Nonnull;

/**
 * This class allows you to clear keys from your history to be restored, that the app does not need to restore.
 */
public interface KeyFilter {
    /**
     * The method used to filter the history before setting it back into the backstack.
     *
     * @param restoredKeys the keys that were originally restored
     * @return the filtered history
     */
    @Nonnull
    List<Object> filterHistory(@Nonnull List<Object> restoredKeys);
}
