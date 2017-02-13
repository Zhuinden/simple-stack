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

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A delegate class that manages Activity lifecycle integration, and view-state persistence for custom viewgroups.
 *
 * Created by Zhuinden on 2017. 01. 22..
 */

public class BackstackDelegate
        implements Backstack.CompletionListener {
    private static final String UNINITIALIZED = "";
    private String persistenceTag = UNINITIALIZED;

    private static final String HISTORY = "simplestack.HISTORY";
    private static final String STATES = HISTORY + "_STATES";

    @SuppressWarnings("StringEquality")
    public void setPersistenceTag(@NonNull String persistenceTag) {
        if(backstack != null) {
            throw new IllegalStateException("Persistence tag should be set before calling `onCreate()`");
        }
        if(persistenceTag == null) {
            throw new IllegalArgumentException("Null persistence tag is not allowed!");
        }
        if(this.persistenceTag == UNINITIALIZED) {
            this.persistenceTag = persistenceTag;
        } else if(!this.persistenceTag.equals(persistenceTag)) {
            throw new IllegalStateException("The persistence tag cannot be set to a new value once it's already set!");
        }
    }

    private String getHistoryTag() {
        return "".equals(persistenceTag) ? HISTORY : HISTORY + persistenceTag;
    }

    private String getStateTag() {
        return "".equals(persistenceTag) ? STATES : STATES + persistenceTag;
    }

    Backstack backstack;

    Map<Parcelable, SavedState> keyStateMap = new HashMap<>();

    StateChanger stateChanger;

    public BackstackDelegate(@Nullable StateChanger stateChanger) {
        this.stateChanger = stateChanger;
    }

    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable Object nonConfigurationInstance, @NonNull ArrayList<Parcelable> initialKeys) {
        if(nonConfigurationInstance != null && !(nonConfigurationInstance instanceof NonConfigurationInstance)) {
            throw new IllegalArgumentException(
                    "The provided non configuration instance must be of type BackstackDelegate.NonConfigurationInstance!");
        }
        ArrayList<Parcelable> keys;
        if(savedInstanceState != null) {
            keys = savedInstanceState.getParcelableArrayList(getHistoryTag());
            List<SavedState> savedStates = savedInstanceState.getParcelableArrayList(getStateTag());
            if(savedStates != null) {
                for(SavedState savedState : savedStates) {
                    keyStateMap.put(savedState.getKey(), savedState);
                }
            }
        } else {
            keys = initialKeys;
        }
        NonConfigurationInstance nonConfig = (NonConfigurationInstance) nonConfigurationInstance;
        if(nonConfig != null) {
            backstack = nonConfig.getBackstack();
        } else {
            backstack = new Backstack(keys);
        }
        registerAsCompletionListener();
        initializeBackstack(stateChanger);
    }

    protected void registerAsCompletionListener() {
        backstack.addCompletionListener(this);
    }

    protected void unregisterAsCompletionListener() {
        backstack.removeCompletionListener(this);
    }

    public void setStateChanger(@Nullable StateChanger stateChanger) {
        if(backstack.hasStateChanger()) {
            backstack.removeStateChanger();
        }
        this.stateChanger = stateChanger;
        initializeBackstack(stateChanger);
    }

    private void initializeBackstack(StateChanger stateChanger) {
        if(stateChanger != null) {
            backstack.setStateChanger(stateChanger, Backstack.INITIALIZE);
        }
    }

    public NonConfigurationInstance onRetainCustomNonConfigurationInstance() {
        return new NonConfigurationInstance(backstack);
    }

    public boolean onBackPressed() {
        return backstack.goBack();
    }

    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList(getHistoryTag(), HistoryBuilder.from(backstack).build());
        outState.putParcelableArrayList(getStateTag(), new ArrayList<>(keyStateMap.values()));
    }

    public void onPostResume() {
        if(stateChanger == null) {
            throw new IllegalStateException("State changer is still not set in `onPostResume`!");
        }
        if(!backstack.hasStateChanger()) {
            backstack.setStateChanger(stateChanger, Backstack.REATTACH);
        }
    }

    public void onPause() {
        if(backstack.hasStateChanger()) {
            backstack.removeStateChanger();
        }
    }

    public void onDestroy() {
        backstack.executePendingStateChange();
        unregisterAsCompletionListener();
    }

    // ----- get backstack

    @NonNull
    public Backstack getBackstack() {
        if(backstack == null) {
            throw new IllegalStateException("The backstack within the delegate must be initialized by `onCreate()`");
        }
        return backstack;
    }

    // ----- viewstate persistence

    public void persistViewToState(@Nullable View view) {
        if(view != null) {
            Parcelable key = KeyContextWrapper.getKey(view.getContext());
            if(key == null) {
                throw new IllegalArgumentException("The view [" + view + "] contained no key!");
            }
            SparseArray<Parcelable> viewHierarchyState = new SparseArray<>();
            view.saveHierarchyState(viewHierarchyState);
            Bundle bundle = null;
            if(view instanceof Bundleable) {
                bundle = ((Bundleable) view).toBundle();
            }
            SavedState previousSavedState = SavedState.builder() //
                    .setKey(key) //
                    .setViewHierarchyState(viewHierarchyState) //
                    .setBundle(bundle) //
                    .build();
            keyStateMap.put(key, previousSavedState);
        }
    }

    public void restoreViewFromState(@NonNull View view) {
        if(view == null) {
            throw new IllegalArgumentException("You cannot restore state into null view!");
        }
        Parcelable newKey = KeyContextWrapper.getKey(view.getContext());
        SavedState savedState = getSavedState(newKey);
        view.restoreHierarchyState(savedState.getViewHierarchyState());
        if(view instanceof Bundleable) {
            ((Bundleable) view).fromBundle(savedState.getBundle());
        }
    }

    @NonNull
    public SavedState getSavedState(@NonNull Parcelable key) {
        if(key == null) {
            throw new IllegalArgumentException("Key cannot be null!");
        }
        if(!keyStateMap.containsKey(key)) {
            keyStateMap.put(key, SavedState.builder().setKey(key).build());
        }
        return keyStateMap.get(key);
    }

    protected void clearStatesNotIn(@NonNull Map<Parcelable, SavedState> keyStateMap, @NonNull StateChange stateChange) {
        keyStateMap.keySet().retainAll(stateChange.getNewState());
    }

    @Override
    public void stateChangeCompleted(@NonNull StateChange stateChange) {
        if(!backstack.isStateChangePending()) {
            clearStatesNotIn(keyStateMap, stateChange);
        }
    }

    public static class NonConfigurationInstance {
        private Backstack backstack;

        private NonConfigurationInstance(Backstack backstack) {
            this.backstack = backstack;
        }

        Backstack getBackstack() {
            return backstack;
        }
    }
}
