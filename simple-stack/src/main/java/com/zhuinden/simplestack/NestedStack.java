package com.zhuinden.simplestack;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * BETA: Nested Stack as managed service in view associated with a given key.
 */
public class NestedStack
        implements Bundleable {
    BackstackManager backstackManager;

    Backstack backstack;

    NestedStack parent;

    KeyParceler keyParceler;

    StateChanger stateChanger;

    NestedStack(BackstackManager backstackManager, KeyParceler keyParceler) {
        this.parent = null;
        this.keyParceler = keyParceler;
        this.backstackManager = backstackManager;
        this.backstack = backstackManager.getBackstack();
    }

    NestedStack(NestedStack parent, KeyParceler keyParceler) {
        this.parent = parent;
        this.keyParceler = keyParceler;
        this.backstack = new Backstack();
        this.backstackManager = new BackstackManager(keyParceler);
        this.backstackManager.setBackstack(backstack);
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
            backstackManager.setupServiceManager(Collections.<ServiceFactory>emptyList(), Collections.<String, Object>emptyMap());
        }
    }

    public void setStateChanger(StateChanger stateChanger) {
        if(parent != null) {
            backstackManager.setStateChanger(stateChanger);
        }
    }

    public void reattachStateChanger() {
        if(parent != null) {
            backstackManager.reattachStateChanger();
        }
    }

    public void detachStateChanger() {
        if(parent != null) {
            backstackManager.detachStateChanger();
        }
    }

    public void goTo(Object key) {
        backstackManager.getBackstack().goTo(key);
    }

    public boolean goBack() {
        if(backstackManager.getBackstack().goBack()) {
            return true;
        }
        if(parent != null) {
            return parent.goBack();
        }
        return false;
    }

    public List<Object> getHistory() {
        return backstack.getHistory();
    }

    public void setHistory(List<Object> newHistory, @StateChange.StateChangeDirection int direction) {
        backstackManager.getBackstack().setHistory(newHistory, direction);
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
    public StateBundle toBundle() {
        StateBundle bundle = new StateBundle();
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

            if(backstackManager.serviceManager != null) { // TODO: there must be a better way to keep track of whether a NestedStack is initialized...
                backstackManager.persistStates();
            }
            bundle.putBundle("MANAGER_STATES", backstackManager.toBundle());
        }
        return bundle;
    }

    @Override
    public void fromBundle(@Nullable StateBundle bundle) {
        if(bundle != null) {
            if(parent != null) { // root is managed by delegate
                backstackManager.restoreStates(bundle.getBundle("MANAGER_STATES"));
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
