package com.zhuinden.simplestack;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

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

    Object parentKey; // NULL for root!

    NestedStack parent; // NULL for root!

    KeyParceler keyParceler;

    NestedStack(BackstackManager backstackManager, KeyParceler keyParceler) {
        this.parentKey = null;
        this.parent = null;
        this.keyParceler = keyParceler;
        this.backstackManager = backstackManager;
        this.backstack = backstackManager.getBackstack();
    }

    NestedStack(Object parentKey, NestedStack parent, KeyParceler keyParceler) {
        this.parentKey = parentKey;
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
            if(this.backstack.getInitialParameters().isEmpty()) { // is first init
                this.backstack.setInitialParameters(initialKeys);
            }
            if(backstackManager.getServiceManager() == null) { // uninitialized
                ServiceManager parentServiceManager = null;
                NestedStack runningParent = parent;
                while(runningParent != null && parentServiceManager == null) {
                    parentServiceManager = runningParent.backstackManager == null ? null : runningParent.backstackManager.serviceManager;
                    runningParent = runningParent.parent;
                }
                backstackManager.setupServiceManager(parentServiceManager,
                        parentKey,
                        Collections.<ServiceFactory>emptyList(),
                        Collections.<String, Object>emptyMap());
            }
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

    /**
     * Returns a {@link SavedState} instance for the given key.
     * If the state does not exist, then a new associated state is created.
     *
     * @param key The key to which the {@link SavedState} belongs.
     * @return the saved state that belongs to the given key.
     */
    @NonNull
    public SavedState getSavedState(@NonNull Object key) {
        return backstackManager.getSavedState(key);
    }

    /**
     * Provides the means to save the provided view's hierarchy state, and its optional {@link StateBundle} via {@link Bundleable} into a {@link SavedState}.
     *
     * @param view the view that belongs to a certain key
     */
    public void persistViewToState(@Nullable View view) {
        backstackManager.persistViewToState(view);
    }

    /**
     * Restores the state of the view based on the currently stored {@link SavedState}, according to the view's key.
     *
     * @param view the view that belongs to a certain key
     */
    public void restoreViewFromState(@NonNull View view) {
        backstackManager.restoreViewFromState(view);
    }

    @Nullable
    public NestedStack getParent() {
        return parent;
    }

    /**
     * Finds the service specified by the given service tag for the given key.
     *
     * If the service is not found, an IllegalStateException is thrown.
     * If the key is not managed, an IllegalStateException is thrown.
     *
     * @param key        the key the given service belongs to.
     * @param serviceTag the tag that identifies the service.
     * @param <T>        the type of the service.
     * @return the service.
     */
    @NonNull
    public <T> T findService(Object key, String serviceTag) {
        return backstackManager.findService(key, serviceTag);
    }

    /**
     * Returns the {@link NestedStack} that belongs to the given child key.
     * Please note that they are treated like services, so inactive stacks are destroyed and parcelled until they become active again.
     * If a service is not found, an exception is thrown.
     *
     * @param key the key the child view is associated with
     * @return the nested stack that belongs to the child key.
     */
    @NonNull
    public NestedStack getChildStack(Object key) {
        return findService(key, BackstackManager.LOCAL_STACK);
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

            bundle.putParcelableArrayList(BackstackManager.HISTORY_TAG, parcelledHistory);
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
                ArrayList<Parcelable> parcelledHistory = bundle.getParcelableArrayList(BackstackManager.HISTORY_TAG);
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
