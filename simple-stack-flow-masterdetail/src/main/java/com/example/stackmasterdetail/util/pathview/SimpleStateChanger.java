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

package com.example.stackmasterdetail.util.pathview;

import android.view.ViewGroup;

import com.example.stackmasterdetail.application.Path;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;
import com.zhuinden.simplestack.navigator.DefaultStateChanger;

/**
 * Provides basic right-to-left transitions. Saves and restores view state.
 */
public class SimpleStateChanger
        implements StateChanger {
    private DefaultStateChanger defaultStateChanger;

    public SimpleStateChanger(ViewGroup root) {
        this.defaultStateChanger = DefaultStateChanger.create(root.getContext(), root);
    }

    @Override
    public void handleStateChange(final StateChange stateChange, final StateChanger.Callback callback) {
        if(stateChange.topNewState().equals(stateChange.topPreviousState())) {
            callback.stateChangeComplete();
            return;
        }

        Path newKey = stateChange.topNewState();
        newKey = getActiveKey(newKey);
        defaultStateChanger.performViewChange(stateChange.<Path>topPreviousState(), newKey, stateChange, callback);
    }

    protected Path getActiveKey(Path path) {
        return path;
    }
}
