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
import android.os.Parcel;
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
 * A delegate class that manages the {@link Backstack}'s Activity lifecycle integration,
 * and provides view-state persistence for custom views that are associated with a key using {@link KeyContextWrapper}.
 *
 * This should be used in Activities to make sure that the {@link Backstack} survives both configuration changes and process death.
 */
public class BackstackDelegate {
    private static class ParcelledState
            implements Parcelable {
        Parcelable parcelableKey;
        SparseArray<Parcelable> viewHierarchyState;
        Bundle bundle;

        private ParcelledState() {
        }

        protected ParcelledState(Parcel in) {
            parcelableKey = in.readParcelable(getClass().getClassLoader());
            // noinspection unchecked
            viewHierarchyState = in.readSparseArray(getClass().getClassLoader());
            boolean hasBundle = in.readByte() > 0;
            if(hasBundle) {
                bundle = in.readBundle(getClass().getClassLoader());
            }
        }

        public static final Creator<ParcelledState> CREATOR = new Creator<ParcelledState>() {
            @Override
            public ParcelledState createFromParcel(Parcel in) {
                return new ParcelledState(in);
            }

            @Override
            public ParcelledState[] newArray(int size) {
                return new ParcelledState[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(parcelableKey, flags);
            // noinspection unchecked
            SparseArray<Object> sparseArray = (SparseArray) viewHierarchyState;
            dest.writeSparseArray(sparseArray);
            dest.writeByte(bundle != null ? (byte) 0x01 : 0x00);
            if(bundle != null) {
                dest.writeBundle(bundle);
            }
        }

    }

    private final Backstack.CompletionListener completionListener = new Backstack.CompletionListener() {
        @Override
        public void stateChangeCompleted(@NonNull StateChange stateChange) {
            BackstackDelegate.this.stateChangeCompleted(stateChange);
        }
    };

    private static final String UNINITIALIZED = "";
    private String persistenceTag = UNINITIALIZED;

    private KeyParceler keyParceler = new KeyParceler() {
        @Override
        public Parcelable toParcelable(Object object) {
            return (Parcelable) object;
        }

        @Override
        public Object fromParcelable(Parcelable parcelable) {
            return parcelable;
        }
    };

    /**
     * Specifies a custom {@link KeyParceler}, allowing key parcellation strategies to be used for turning a key into Parcelable.
     *
     * If used, this method must be called before {@link BackstackDelegate#onCreate(Bundle, Object, ArrayList)}.
     *
     * @param keyParceler The custom {@link KeyParceler}.
     */
    public void setKeyParceler(KeyParceler keyParceler) {
        if(backstack != null) {
            throw new IllegalStateException("Custom key parceler should be set before calling `onCreate()`");
        }
        if(keyParceler == null) {
            throw new IllegalArgumentException("The key parceler cannot be null!");
        }
        this.keyParceler = keyParceler;
    }

    private static final String HISTORY = "simplestack.HISTORY";
    private static final String STATES = HISTORY + "_STATES";

    /**
     * Persistence tag allows you to have multiple {@link BackstackDelegate}s in the same activity.
     * This is required to make sure that the {@link Backstack} states do not overwrite each other in the saved instance state bundle.
     * If used, this method must be called before {@link BackstackDelegate#onCreate(Bundle, Object, ArrayList)}.
     * A persistence tag can only be set once on a given BackstackDelegate instance.
     *
     * @param persistenceTag a non-null persistence tag that uniquely identifies this {@link BackstackDelegate} inside the Activity.
     */
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

    Map<Object, SavedState> keyStateMap = new HashMap<>();

    StateChanger stateChanger;

    /**
     * Creates the {@link BackstackDelegate}.
     * If {@link StateChanger} is null, then the initialize {@link StateChange} is postponed until it is explicitly set.
     * The {@link StateChanger} must be set at some point before {@link BackstackDelegate#onPostResume()}.
     *
     * @param stateChanger The {@link StateChanger} to be set. Allowed to be null at initialization.
     */
    public BackstackDelegate(@Nullable StateChanger stateChanger) {
        this.stateChanger = stateChanger;
    }

    /**
     * The onCreate() delegate for the Activity.
     * It initializes the backstack from either the non-configuration instance, the saved state, or creates a new one.
     * Restores the {@link SavedState} that belongs to persisted view state.
     * Begins an initialize {@link StateChange} if the {@link StateChanger} is set.
     * Also registers a {@link Backstack.CompletionListener} that must be unregistered with {@link BackstackDelegate#onDestroy()}.
     *
     * @param savedInstanceState       The Activity saved instance state bundle.
     * @param nonConfigurationInstance The {@link NonConfigurationInstance} that is typically obtained with getLastCustomNonConfigurationInstance().
     * @param initialKeys              A list of the keys that are used to set as initial history of the backstack.
     */
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable Object nonConfigurationInstance, @NonNull ArrayList<Object> initialKeys) {
        if(nonConfigurationInstance != null && !(nonConfigurationInstance instanceof NonConfigurationInstance)) {
            throw new IllegalArgumentException(
                    "The provided non configuration instance must be of type BackstackDelegate.NonConfigurationInstance!");
        }
        ArrayList<Object> keys;
        if(savedInstanceState != null) {
            List<Parcelable> parcelledKeys = savedInstanceState.getParcelableArrayList(getHistoryTag());
            keys = new ArrayList<>();
            if(parcelledKeys != null) {
                for(Parcelable parcelledKey : parcelledKeys) {
                    keys.add(keyParceler.fromParcelable(parcelledKey));
                }
            }
            if(keys.isEmpty()) {
                keys = initialKeys;
            }
            List<ParcelledState> savedStates = savedInstanceState.getParcelableArrayList(getStateTag());
            if(savedStates != null) {
                for(ParcelledState parcelledState : savedStates) {
                    SavedState savedState = SavedState.builder()
                            .setKey(keyParceler.fromParcelable(parcelledState.parcelableKey))
                            .setViewHierarchyState(parcelledState.viewHierarchyState)
                            .setBundle(parcelledState.bundle)
                            .build();
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
        backstack.addCompletionListener(completionListener);
    }

    protected void unregisterAsCompletionListener() {
        backstack.removeCompletionListener(completionListener);
    }

    /**
     * Sets the {@link StateChanger} to the {@link Backstack}. Removes the previous one if it there was already one set.
     * This call begins an initialize {@link StateChange}.
     *
     * @param stateChanger The {@link StateChanger} to be set.
     */
    public void setStateChanger(@Nullable StateChanger stateChanger) {
        if(backstack.hasStateChanger()) {
            backstack.removeStateChanger();
        }
        this.stateChanger = stateChanger;
        initializeBackstack(stateChanger);
    }

    protected void initializeBackstack(StateChanger stateChanger) {
        if(stateChanger != null) {
            backstack.setStateChanger(stateChanger, Backstack.INITIALIZE);
        }
    }

    /**
     * The onRetainCustomNonConfigurationInstance() delegate for the Activity.
     * This is required to make sure that the Backstack survives configuration change.
     *
     * @return a {@link NonConfigurationInstance} that contains the internal backstack instance.
     */
    public NonConfigurationInstance onRetainCustomNonConfigurationInstance() {
        return new NonConfigurationInstance(backstack);
    }

    /**
     * The onBackPressed() delegate for the Activity.
     * The call is delegated to {@link Backstack#goBack()}'.
     *
     * @return true if the {@link Backstack} handled the back press
     */
    public boolean onBackPressed() {
        return backstack.goBack();
    }

    /**
     * The onSaveInstanceState() delegate for the Activity.
     * This is required in order to save the {@link Backstack} and the {@link SavedState} persisted with {@link BackstackDelegate#persistViewToState(View)}
     * into the Activity saved instance state bundle.
     *
     * @param outState the Bundle into which the backstack history and view states are saved.
     */
    public void onSaveInstanceState(@NonNull Bundle outState) {
        ArrayList<Parcelable> history = new ArrayList<>();
        for(Object key : backstack.getHistory()) {
            history.add(keyParceler.toParcelable(key));
        }
        outState.putParcelableArrayList(getHistoryTag(), history);

        ArrayList<ParcelledState> states = new ArrayList<>();
        for(SavedState savedState : keyStateMap.values()) {
            ParcelledState parcelledState = new ParcelledState();
            parcelledState.parcelableKey = keyParceler.toParcelable(savedState.getKey());
            parcelledState.viewHierarchyState = savedState.getViewHierarchyState();
            parcelledState.bundle = savedState.getBundle();
            states.add(parcelledState);
        }
        outState.putParcelableArrayList(getStateTag(), states);
    }

    /**
     * The onPostResume() delegate for the Activity.
     * It re-attaches the {@link StateChanger} if it is not already set.
     */
    public void onPostResume() {
        if(stateChanger == null) {
            throw new IllegalStateException("State changer is still not set in `onPostResume`!");
        }
        if(!backstack.hasStateChanger()) {
            backstack.setStateChanger(stateChanger, Backstack.REATTACH);
        }
    }

    /**
     * The onPause() delegate for the Activity.
     * It removes the {@link StateChanger} if it is set.
     */
    public void onPause() {
        if(backstack.hasStateChanger()) {
            backstack.removeStateChanger();
        }
    }

    /**
     * The onDestroy() delegate for the Activity.
     * Forces any pending state change to execute with {@link Backstack#executePendingStateChange()},
     * and unregisters the delegate's {@link Backstack.CompletionListener} that was registered in {@link BackstackDelegate#onCreate(Bundle, Object, ArrayList)}.
     */
    public void onDestroy() {
        backstack.executePendingStateChange();
        unregisterAsCompletionListener();
    }

    // ----- get backstack

    /**
     * Returns the {@link Backstack} that belongs to this delegate.
     * This method can only be invoked after {@link BackstackDelegate#onCreate(Bundle, Object, ArrayList)} has been called.
     *
     * @return the {@link Backstack} managed by this delegate.
     */
    @NonNull
    public Backstack getBackstack() {
        if(backstack == null) {
            throw new IllegalStateException("The backstack within the delegate must be initialized by `onCreate()`");
        }
        return backstack;
    }

    // ----- viewstate persistence

    /**
     * Provides the means to save the provided view's hierarchy state, and its optional bundle via {@link Bundleable} into a {@link SavedState}.
     *
     * @param view the view that belongs to a certain key
     */
    public void persistViewToState(@Nullable View view) {
        if(view != null) {
            Object key = KeyContextWrapper.getKey(view.getContext());
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

    protected void stateChangeCompleted(StateChange stateChange) {
        if(!backstack.isStateChangePending()) {
            clearStatesNotIn(keyStateMap, stateChange);
        }
    }

    protected void clearStatesNotIn(@NonNull Map<Object, SavedState> keyStateMap, @NonNull StateChange stateChange) {
        keyStateMap.keySet().retainAll(stateChange.getNewState());
    }

    /**
     * The class which stores the {@link Backstack} for surviving configuration change.
     */
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
