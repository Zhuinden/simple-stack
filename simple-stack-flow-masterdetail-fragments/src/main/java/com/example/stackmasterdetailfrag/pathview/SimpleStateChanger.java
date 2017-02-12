/*
 * Copyright 2014 Square Inc.
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

package com.example.stackmasterdetailfrag.pathview;

import android.os.Parcelable;
import android.view.ViewGroup;

import com.example.stackmasterdetailfrag.SinglePaneFragmentStateChanger;
import com.example.stackmasterdetailfrag.util.FragmentManagerService;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Provides basic right-to-left transitions. Saves and restores view state.
 */
public class SimpleStateChanger
        implements StateChanger {
    private static final Map<Class, Integer> PATH_LAYOUT_CACHE = new LinkedHashMap<>();

    private final ViewGroup root;

    public SimpleStateChanger(ViewGroup root) {
        this.root = root;
    }

    @Override
    public void handleStateChange(final StateChange stateChange, final StateChanger.Callback callback) {
        if(stateChange.topNewState().equals(stateChange.topPreviousState())) {
            callback.stateChangeComplete();
            return;
        }
        ((StateChanger)root).handleStateChange(stateChange, callback);
    }
}
