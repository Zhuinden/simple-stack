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
 * Created by Zhuinden on 2017.02.28..
 */

public class BackstackManager
        implements Bundleable {
    static final String HISTORY_TAG = "HISTORY";
    static final String STATES_TAG = "STATES";

    private final StateChanger managedStateChanger = new StateChanger() {
        @Override
        public void handleStateChange(final StateChange stateChange, final Callback completionCallback) {
            stateChanger.handleStateChange(stateChange, new Callback() {
                @Override
                public void stateChangeComplete() {
                    completionCallback.stateChangeComplete();
                    if(!backstack.isStateChangePending()) {
                        clearStatesNotIn(keyStateMap, stateChange);
                    }
                }
            });
        }
    };

    private KeyParceler keyParceler = new DefaultKeyParceler();

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


    Backstack backstack;

    Map<Object, SavedState> keyStateMap = new HashMap<>();

    StateChanger stateChanger;

    public void setup(@NonNull List<?> initialKeys) {
        backstack = new Backstack(initialKeys);
    }

    protected void clearStatesNotIn(@NonNull Map<Object, SavedState> keyStateMap, @NonNull StateChange stateChange) {
        keyStateMap.keySet().retainAll(stateChange.getNewState());
    }

    public Backstack getBackstack() {
        if(backstack == null) {
            throw new IllegalStateException("You must call `initializeOrRestore()` before calling `getBackstack()`");
        }
        return backstack;
    }

    private void initializeBackstack(StateChanger stateChanger) {
        if(stateChanger != null) {
            backstack.setStateChanger(managedStateChanger, Backstack.INITIALIZE);
        }
    }

    public void setStateChanger(StateChanger stateChanger) {
        if(backstack.hasStateChanger()) {
            backstack.removeStateChanger();
        }
        this.stateChanger = stateChanger;
        initializeBackstack(stateChanger);
    }

    public void detachStateChanger() {
        if(backstack.hasStateChanger()) {
            backstack.removeStateChanger();
        }
    }

    public void reattachStateChanger() {
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
     * Provides the means to save the provided view's hierarchy state, and its optional {@link StateBundle} via {@link Bundleable} into a {@link SavedState}.
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

    @Override
    public void fromBundle(@Nullable StateBundle stateBundle) {
        if(backstack == null) {
            throw new IllegalStateException("A backstack must be set up before it is restored!");
        }
        if(stateBundle != null) {
            List<Object> keys = new ArrayList<>();
            List<Parcelable> parcelledKeys = stateBundle.getParcelableArrayList(HISTORY_TAG);
            if(parcelledKeys != null) {
                for(Parcelable parcelledKey : parcelledKeys) {
                    keys.add(keyParceler.fromParcelable(parcelledKey));
                }
            }
            if(!keys.isEmpty()) {
                backstack.setInitialParameters(keys);
            }
            List<ParcelledState> savedStates = stateBundle.getParcelableArrayList(STATES_TAG);
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
        }
    }

    @NonNull
    @Override
    public StateBundle toBundle() {
        StateBundle stateBundle = new StateBundle();
        ArrayList<Parcelable> history = new ArrayList<>();
        for(Object key : backstack.getHistory()) {
            history.add(keyParceler.toParcelable(key));
        }
        stateBundle.putParcelableArrayList(HISTORY_TAG, history);

        ArrayList<ParcelledState> states = new ArrayList<>();
        for(SavedState savedState : keyStateMap.values()) {
            ParcelledState parcelledState = new ParcelledState();
            parcelledState.parcelableKey = keyParceler.toParcelable(savedState.getKey());
            parcelledState.viewHierarchyState = savedState.getViewHierarchyState();
            parcelledState.bundle = savedState.getBundle();
            states.add(parcelledState);
        }
        stateBundle.putParcelableArrayList(STATES_TAG, states);
        return stateBundle;
    }
}
