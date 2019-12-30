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

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Bundle;
import android.view.ViewGroup;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.GlobalServices;
import com.zhuinden.simplestack.KeyFilter;
import com.zhuinden.simplestack.KeyParceler;
import com.zhuinden.simplestack.ScopedServices;
import com.zhuinden.simplestack.StateChanger;
import com.zhuinden.statebundle.StateBundle;

import java.util.Collections;
import java.util.List;

/**
 * This is public because it has to be. It is responsible for the lifecycle integration of the Backstack.
 */
@TargetApi(11)
public final class BackstackHost
        extends Fragment {

    public BackstackHost() {
        setRetainInstance(true);
    }

    StateChanger stateChanger;
    KeyFilter keyFilter;
    KeyParceler keyParceler;
    Backstack.StateClearStrategy stateClearStrategy;
    ScopedServices scopedServices;
    GlobalServices globalServices;
    GlobalServices.Factory globalServiceFactory;
    List<Backstack.CompletionListener> stateChangeCompletionListeners;

    boolean shouldPersistContainerChild;

    Backstack backstack;

    List<?> initialKeys = Collections.emptyList(); // should not stay empty list
    ViewGroup container;

    Bundle savedInstanceState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
    }

    Backstack initialize(boolean isInitializeDeferred) {
        if(backstack == null) {
            backstack = new Backstack();
            backstack.setKeyFilter(keyFilter);
            backstack.setKeyParceler(keyParceler);
            backstack.setStateClearStrategy(stateClearStrategy);
            if(scopedServices != null) {
                backstack.setScopedServices(scopedServices);
            }
            if(globalServices != null) {
                backstack.setGlobalServices(globalServices);
            }
            if(globalServiceFactory != null) {
                backstack.setGlobalServices(globalServiceFactory);
            }
            backstack.setup(initialKeys);
            for(Backstack.CompletionListener completionListener : stateChangeCompletionListeners) {
                backstack.addStateChangeCompletionListener(completionListener);
            }
            if(savedInstanceState != null) {
                backstack.fromBundle(savedInstanceState.<StateBundle>getParcelable("NAVIGATOR_STATE_BUNDLE"));
            }
        }
        if(!isInitializeDeferred) {
            backstack.setStateChanger(stateChanger);
        }
        return backstack;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(shouldPersistContainerChild) {
            Navigator.persistViewToState(container.getChildAt(0));
        }
        outState.putParcelable("NAVIGATOR_STATE_BUNDLE", backstack.toBundle());
    }

    @Override
    public void onResume() {
        super.onResume();
        backstack.reattachStateChanger();
    }

    @Override
    public void onPause() {
        backstack.detachStateChanger();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        backstack.executePendingStateChange();

        stateChanger = null;
        container = null;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        backstack.finalizeScopes();
    }

    public Backstack getBackstack() {
        return backstack;
    }
}
