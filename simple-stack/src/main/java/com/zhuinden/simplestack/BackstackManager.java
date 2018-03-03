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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.View;

import com.zhuinden.statebundle.StateBundle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The backstack manager manages a {@link Backstack} internally, and wraps it with the ability of persisting view state and the backstack history itself.
 *
 * The backstack is created by {@link BackstackManager#setup(List)}, and initialized by {@link BackstackManager#setStateChanger(StateChanger)}.
 */
public class BackstackManager
        implements Bundleable {
    /**
     * Specifies the strategy to be used in order to delete {@link SavedState}s that are no longer needed after a {@link StateChange}, when there is no pending {@link StateChange} left.
     */
    public interface StateClearStrategy {
        /**
         * Allows a hook to clear the {@link SavedState} for obsolete keys.
         *
         * @param keyStateMap the map that contains the keys and their corresponding retained saved state.
         * @param stateChange the last state change
         */
        void clearStatesNotIn(@NonNull Map<Object, SavedState> keyStateMap, @NonNull StateChange stateChange);
    }

    private static final String HISTORY_TAG = "HISTORY";
    private static final String STATES_TAG = "STATES";

    static String getHistoryTag() {
        return HISTORY_TAG;
    }

    static String getStatesTag() {
        return STATES_TAG;
    }

    private final StateChanger managedStateChanger = new StateChanger() {
        @Override
        public void handleStateChange(@NonNull final StateChange stateChange, @NonNull final Callback completionCallback) {
            stateChanger.handleStateChange(stateChange, new Callback() {
                @Override
                public void stateChangeComplete() {
                    completionCallback.stateChangeComplete();
                    if(!backstack.isStateChangePending()) {
                        stateClearStrategy.clearStatesNotIn(keyStateMap, stateChange);
                    }
                }
            });
        }
    };

    private KeyFilter keyFilter = new DefaultKeyFilter();
    private KeyParceler keyParceler = new DefaultKeyParceler();
    private StateClearStrategy stateClearStrategy = new DefaultStateClearStrategy();

    /**
     * Specifies a custom {@link KeyFilter}, allowing keys to be filtered out if they should not be restored after process death.
     *
     * If used, this method must be called before {@link BackstackManager#setup(List)} .
     *
     * @param keyFilter The custom {@link KeyFilter}.
     */
    public void setKeyFilter(@NonNull KeyFilter keyFilter) {
        if(backstack != null) {
            throw new IllegalStateException("Custom key filter should be set before calling `setup()`");
        }
        if(keyFilter == null) {
            throw new IllegalArgumentException("The key filter cannot be null!");
        }
        this.keyFilter = keyFilter;
    }

    /**
     * Specifies a custom {@link KeyParceler}, allowing key parcellation strategies to be used for turning a key into Parcelable.
     *
     * If used, this method must be called before {@link BackstackManager#setup(List)} .
     *
     * @param keyParceler The custom {@link KeyParceler}.
     */
    public void setKeyParceler(@NonNull KeyParceler keyParceler) {
        if(backstack != null) {
            throw new IllegalStateException("Custom key parceler should be set before calling `setup()`");
        }
        if(keyParceler == null) {
            throw new IllegalArgumentException("The key parceler cannot be null!");
        }
        this.keyParceler = keyParceler;
    }

    /**
     * Specifies a custom {@link StateClearStrategy}, allowing a custom strategy for clearing the retained state of keys.
     * The {@link DefaultStateClearStrategy} clears the {@link SavedState} for keys that are not found in the new state.
     *
     * If used, this method must be called before {@link BackstackManager#setup(List)} .
     *
     * @param stateClearStrategy The custom {@link StateClearStrategy}.
     */
    public void setStateClearStrategy(@NonNull StateClearStrategy stateClearStrategy) {
        if(backstack != null) {
            throw new IllegalStateException("Custom state clear strategy should be set before calling `setup()`");
        }
        if(stateClearStrategy == null) {
            throw new IllegalArgumentException("The state clear strategy cannot be null!");
        }
        this.stateClearStrategy = stateClearStrategy;
    }

    Backstack backstack;

    Map<Object, SavedState> keyStateMap = new HashMap<>();

    StateChanger stateChanger;

    /**
     * Setup creates the {@link Backstack} with the specified initial keys.
     *
     * @param initialKeys the initial keys of the backstack
     */
    public void setup(@NonNull List<?> initialKeys) {
        backstack = new Backstack(initialKeys);
    }

    /**
     * Gets the managed {@link Backstack}. It can only be called after {@link BackstackManager#setup(List)}.
     *
     * @return the backstack
     */
    public Backstack getBackstack() {
        checkBackstack("You must call `setup()` before calling `getBackstack()`");
        return backstack;
    }

    private void initializeBackstack(StateChanger stateChanger) {
        if(stateChanger != null) {
            backstack.setStateChanger(managedStateChanger);
        }
    }

    /**
     * Sets the {@link StateChanger} for the given {@link Backstack}. This can only be called after {@link BackstackManager#setup(List)}.
     *
     * @param stateChanger the state changer
     */
    public void setStateChanger(@Nullable StateChanger stateChanger) {
        checkBackstack("You must call `setup()` before calling `setStateChanger().");
        if(backstack.hasStateChanger()) {
            backstack.removeStateChanger();
        }
        this.stateChanger = stateChanger;
        initializeBackstack(stateChanger);
    }

    /**
     * Detaches the {@link StateChanger} from the {@link Backstack}. This can only be called after {@link BackstackManager#setup(List)}.
     */
    public void detachStateChanger() {
        checkBackstack("You must call `setup()` before calling `detachStateChanger().`");
        if(backstack.hasStateChanger()) {
            backstack.removeStateChanger();
        }
    }

    /**
     * Reattaches the {@link StateChanger} to the {@link Backstack}. This can only be called after {@link BackstackManager#setup(List)}.
     */
    public void reattachStateChanger() {
        checkBackstack("You must call `setup()` before calling `reattachStateChanger().`");
        if(!backstack.hasStateChanger()) {
            backstack.setStateChanger(managedStateChanger, Backstack.REATTACH);
        }
    }

    /**
     * Returns a {@link SavedState} instance for the given key.
     * If the state does not exist, then a new associated state is created.
     *
     * @param key The key to which the {@link SavedState} belongs.
     * @return the saved state that belongs to the given key.
     */
    @NonNull
    public SavedState getSavedState(@NonNull Object key) {
        if(key == null) {
            throw new IllegalArgumentException("Key cannot be null!");
        }
        if(!keyStateMap.containsKey(key)) {
            keyStateMap.put(key, SavedState.builder().setKey(key).build());
        }
        return keyStateMap.get(key);
    }

    // ----- viewstate persistence

    /**
     * Provides the means to save the provided view's hierarchy state
     * and its optional StateBundle via {@link Bundleable} into a {@link SavedState}.
     *
     * @param view the view that belongs to a certain key
     */
    public void persistViewToState(@Nullable View view) {
        if(view != null) {
            Object key = KeyContextWrapper.getKey(view.getContext());
            if(key == null) {
                throw new IllegalArgumentException("The view [" + view + "] contained no key in its context hierarchy. The view or its parent hierarchy should be inflated by a layout inflater from `stateChange.createContext(baseContext, key)`, or a KeyContextWrapper.");
            }
            SparseArray<Parcelable> viewHierarchyState = new SparseArray<>();
            view.saveHierarchyState(viewHierarchyState);
            StateBundle bundle = null;
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

    /**
     * Restores the state of the view based on the currently stored {@link SavedState}, according to the view's key.
     *
     * @param view the view that belongs to a certain key
     */
    public void restoreViewFromState(@NonNull View view) {
        if(view == null) {
            throw new IllegalArgumentException("You cannot restore state into null view!");
        }
        Object newKey = KeyContextWrapper.getKey(view.getContext());
        SavedState savedState = getSavedState(newKey);
        view.restoreHierarchyState(savedState.getViewHierarchyState());
        if(view instanceof Bundleable) {
            ((Bundleable) view).fromBundle(savedState.getBundle());
        }
    }

    /**
     * Allows adding a {@link Backstack.CompletionListener} to the internal {@link Backstack} that is called when the state change is completed, but before the state is cleared.
     *
     * Please note that a strong reference is kept to the listener, and the {@link Backstack} is typically preserved across configuration change.
     * It is recommended that it is NOT an anonymous inner class or normal inner class in an Activity,
     * because that could cause memory leaks.
     *
     * Instead, it should be a class, or a static inner class.
     *
     * @param stateChangeCompletionListener the state change completion listener.
     */
    public void addStateChangeCompletionListener(@NonNull Backstack.CompletionListener stateChangeCompletionListener) {
        checkBackstack("A backstack must be set up before a state change completion listener is added to it.");
        if(stateChangeCompletionListener == null) {
            throw new IllegalArgumentException("StateChangeCompletionListener cannot be null!");
        }
        this.backstack.addCompletionListener(stateChangeCompletionListener);
    }

    /**
     * Removes the provided {@link Backstack.CompletionListener}.
     *
     * @param stateChangeCompletionListener the state change completion listener.
     */
    public void removeStateChangeCompletionListener(@NonNull Backstack.CompletionListener stateChangeCompletionListener) {
        checkBackstack("A backstack must be set up before a state change completion listener is removed from it.");
        if(stateChangeCompletionListener == null) {
            throw new IllegalArgumentException("StateChangeCompletionListener cannot be null!");
        }
        this.backstack.removeCompletionListener(stateChangeCompletionListener);
    }

    /**
     * Removes all {@link Backstack.CompletionListener}s added to the {@link Backstack}.
     */
    public void removeAllStateChangeCompletionListeners() {
        checkBackstack("A backstack must be set up before state change completion listeners are removed from it.");
        this.backstack.removeCompletionListeners();
    }

    /**
     * Restores the BackstackManager from a StateBundle.
     * This can only be called after {@link BackstackManager#setup(List)}.
     *
     * @param stateBundle the state bundle obtained via {@link BackstackManager#toBundle()}
     */
    @Override
    public void fromBundle(@Nullable StateBundle stateBundle) {
        checkBackstack("A backstack must be set up before it is restored!");
        if(stateBundle != null) {
            List<Object> keys = new ArrayList<>();
            List<Parcelable> parcelledKeys = stateBundle.getParcelableArrayList(getHistoryTag());
            if(parcelledKeys != null) {
                for(Parcelable parcelledKey : parcelledKeys) {
                    keys.add(keyParceler.fromParcelable(parcelledKey));
                }
            }
            keys = keyFilter.filterHistory(new ArrayList<>(keys));
            if(keys == null) {
                keys = Collections.emptyList(); // lenient against null
            }
            if(!keys.isEmpty()) {
                backstack.setInitialParameters(keys);
            }
            List<ParcelledState> savedStates = stateBundle.getParcelableArrayList(getStatesTag());
            if(savedStates != null) {
                for(ParcelledState parcelledState : savedStates) {
                    Object key = keyParceler.fromParcelable(parcelledState.parcelableKey);
                    if(!keys.contains(key)) {
                        continue;
                    }
                    SavedState savedState = SavedState.builder().setKey(key)
                            .setViewHierarchyState(parcelledState.viewHierarchyState)
                            .setBundle(parcelledState.bundle)
                            .build();
                    keyStateMap.put(savedState.getKey(), savedState);
                }
            }
        }
    }

    private void checkBackstack(String message) {
        if(backstack == null) {
            throw new IllegalStateException(message);
        }
    }

    /**
     * Persists the backstack history and view state into a StateBundle.
     *
     * @return the state bundle
     */
    @NonNull
    @Override
    public StateBundle toBundle() {
        StateBundle stateBundle = new StateBundle();
        ArrayList<Parcelable> history = new ArrayList<>();
        for(Object key : backstack.getHistory()) {
            history.add(keyParceler.toParcelable(key));
        }
        stateBundle.putParcelableArrayList(getHistoryTag(), history);

        ArrayList<ParcelledState> states = new ArrayList<>();
        for(SavedState savedState : keyStateMap.values()) {
            ParcelledState parcelledState = new ParcelledState();
            parcelledState.parcelableKey = keyParceler.toParcelable(savedState.getKey());
            parcelledState.viewHierarchyState = savedState.getViewHierarchyState();
            parcelledState.bundle = savedState.getBundle();
            states.add(parcelledState);
        }
        stateBundle.putParcelableArrayList(getStatesTag(), states);
        return stateBundle;
    }
}
