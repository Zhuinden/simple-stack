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
 * Created by Owner on 2017. 05. 03..
 */

import java.util.List;

import javax.annotation.Nonnull;

/**
 * The default {@link KeyFilter} which does not remove any keys, just restores all provided keys.
 */
public class DefaultKeyFilter
        implements KeyFilter {
    @Override
    @Nonnull
    public List<Object> filterHistory(@Nonnull List<Object> restoredKeys) {
        return restoredKeys;
    }
}
