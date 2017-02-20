package com.zhuinden.simplestack;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * BETA: Nested Stack as managed service in view associated with a given key.
 */
public class NestedStack
        implements Bundleable {
    Backstack backstack;

    NestedStack parent;

    KeyParceler keyParceler;

    StateChanger stateChanger;

    NestedStack(Backstack root, KeyParceler keyParceler) {
        this.parent = null;
        this.keyParceler = keyParceler;
        this.backstack = root;
    }

    NestedStack(NestedStack parent, KeyParceler keyParceler) {
        this.parent = parent;
        this.keyParceler = keyParceler;
        this.backstack = new Backstack();
    }

    public void initialize(Object... initialKeys) {
        this.initialize(Arrays.asList(initialKeys));
    }

    public void initialize(List<?> initialKeys) {
        if(initialKeys.isEmpty()) {
            throw new IllegalArgumentException("To use nested stack, at least one initial key must be provided.");
        }
        if(parent != null) {
            if(this.backstack.getInitialParameters().isEmpty()) {
                this.backstack.setInitialParameters(initialKeys);
            }
        }
    }

    public void setStateChanger(StateChanger stateChanger) {
        this.stateChanger = stateChanger;
        if(stateChanger != null) {
            backstack.setStateChanger(stateChanger);
        } else {
            detachStateChanger();
        }
    }

    public void reattachStateChanger() {
        if(stateChanger != null && !backstack.hasStateChanger()) {
            backstack.setStateChanger(stateChanger, Backstack.REATTACH);
        }
    }

    public void detachStateChanger() {
        if(stateChanger != null && backstack.hasStateChanger()) {
            backstack.removeStateChanger();
        }
    }

    public void goTo(Object key) {
        backstack.goTo(key);
    }

    public boolean goBack() {
        if(backstack.goBack()) {
            return true;
        }
        if(parent != null) {
            return parent.goBack();
        }
        return false;
    }

    public void setHistory(List<Object> newHistory, @StateChange.StateChangeDirection int direction) {
        backstack.setHistory(newHistory, direction);
    }

    public void executePendingStateChange() {
        backstack.executePendingStateChange();
    }

    @Nullable
    public NestedStack getParent() {
        return parent;
    }

    @NonNull
    @Override
    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        if(parent != null) { // root is managed by delegate
            List<Object> history = backstack.getHistory();
            ArrayList<Parcelable> parcelledHistory = new ArrayList<>();
            for(Object key : history) {
                parcelledHistory.add(keyParceler.toParcelable(key));
            }

            List<Object> initialParameters = backstack.getInitialParameters();
            ArrayList<Parcelable> parcelledInitialParameters = new ArrayList<>();
            for(Object key : initialParameters) {
                parcelledInitialParameters.add(keyParceler.toParcelable(key));
            }

            bundle.putParcelableArrayList("HISTORY", parcelledHistory);
            bundle.putParcelableArrayList("INITIALPARAMS", parcelledInitialParameters);
        }
        return bundle;
    }

    @Override
    public void fromBundle(@Nullable Bundle bundle) {
        if(bundle != null) {
            if(parent != null) { // root is managed by delegate
                ArrayList<Parcelable> parcelledHistory = bundle.getParcelableArrayList("HISTORY");
                ArrayList<Parcelable> parcelledInitialParams = bundle.getParcelableArrayList("INITIALPARAMS");

                List<Object> history = new ArrayList<>();
                if(parcelledHistory != null) {
                    for(Parcelable key : parcelledHistory) {
                        history.add(keyParceler.fromParcelable(key));
                    }
                }
                List<Object> initialParams = new ArrayList<>();
                if(parcelledInitialParams != null) {
                    for(Parcelable key : parcelledInitialParams) {
                        initialParams.add(keyParceler.fromParcelable(key));
                    }
                }
                List<Object> keys;
                if(history.isEmpty()) {
                    keys = initialParams;
                } else {
                    keys = history;
                }
                backstack.setInitialParameters(keys);
            }
        }
    }
}
