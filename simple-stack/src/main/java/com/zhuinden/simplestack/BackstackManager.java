package com.zhuinden.simplestack;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Owner on 2017. 02. 22..
 */
class BackstackManager {
    private static final String TAG = "simplestack.BackstackManager";

    static final String ROOT_STACK = "simplestack.ROOT_STACK";
    static final String LOCAL_STACK = "simplestack.LOCAL_STACK";

    static final String PARENT_KEY = "simplestack.PARENT_KEY";
    static final String LOCAL_KEY = "simplestack.LOCAL_KEY";

    static final String HISTORY_TAG = "HISTORY";
    static final String STATES_TAG = "STATES";

    Backstack backstack;

    final KeyParceler keyParceler;

    ServiceManager serviceManager;

    Map<Object, SavedState> keyStateMap = new LinkedHashMap<>();

    StateChanger stateChanger;

    private final StateChanger managedStateChanger = new StateChanger() {
        @Override
        public final void handleStateChange(final StateChange stateChange, final Callback completionCallback) {
            if(SSLog.hasLoggers()) {
                SSLog.info(TAG,
                        Arrays.toString(stateChange.getPreviousState().toArray()) + " :: " + Arrays.toString(stateChange.getNewState()
                                .toArray())); //
            }
            serviceManager.dumpLogData();
            Object topNewKey = stateChange.topNewState();
            boolean isInitializeStateChange = stateChange.getPreviousState().isEmpty();
            boolean servicesUninitialized = (isInitializeStateChange && !serviceManager.hasServices(topNewKey));
            if(servicesUninitialized || !isInitializeStateChange) {
                serviceManager.setUp(BackstackManager.this, topNewKey);
            }
            for(int i = stateChange.getPreviousState().size() - 1; i >= 0; i--) {
                Object previousKey = stateChange.getPreviousState().get(i);
                if(serviceManager.hasServices(previousKey) && !stateChange.getNewState().contains(previousKey)) {
                    serviceManager.tearDown(BackstackManager.this, false, previousKey);
                }
            }
            Object topPreviousKey = stateChange.topPreviousState();
            if(topPreviousKey != null && stateChange.getNewState().contains(topPreviousKey)) {
                serviceManager.tearDown(BackstackManager.this, true, topPreviousKey);
            }
            serviceManager.dumpLogData();
            stateChanger.handleStateChange(stateChange, new StateChanger.Callback() {
                @Override
                public void stateChangeComplete() {
                    completionCallback.stateChangeComplete();
                    if(!backstack.isStateChangePending()) {
                        clearStatesNotIn(stateChange);
                    }
                }
            });
        }
    };

    public BackstackManager(KeyParceler keyParceler) {
        this.keyParceler = keyParceler;
    }

    public void setBackstack(Backstack backstack) {
        this.backstack = backstack;
    }

    public void setServiceManager(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    public void setStateChanger(StateChanger stateChanger) {
        if(backstack.hasStateChanger()) {
            backstack.removeStateChanger();
        }
        this.stateChanger = stateChanger;
        initializeBackstack(stateChanger);
    }

    private void initializeBackstack(StateChanger stateChanger) {
        if(stateChanger != null) {
            backstack.setStateChanger(managedStateChanger);
        }
    }

    public void clearStatesNotIn(StateChange stateChange) {
        if(!backstack.isStateChangePending()) {
            clearStatesNotIn(keyStateMap, stateChange);
        }
    }

    private void clearStatesNotIn(@NonNull Map<Object, SavedState> keyStateMap, @NonNull StateChange stateChange) {
        Set<Object> retainedKeys = new LinkedHashSet<>();
        retainedKeys.add(ServiceManager.ROOT_KEY);
        for(Object key : stateChange.getNewState()) {
            Object stateRoot = key;
            if(serviceManager.hasServices(stateRoot)) {
                Object parentKey = serviceManager.findServices(stateRoot).getService(PARENT_KEY);
                while(parentKey instanceof Services.Composite) { // TODO: there NEEDS to be a unit test for this!
                    stateRoot = parentKey;
                    if(serviceManager.hasServices(stateRoot)) {
                        parentKey = serviceManager.findServices(stateRoot).getService(PARENT_KEY);
                    }
                }
            }
            buildKeysToKeep(stateRoot, retainedKeys);
        }
        keyStateMap.keySet().retainAll(retainedKeys);
    }

    private void buildKeysToKeep(Object key, Set<Object> retainedKeys) {
        retainedKeys.add(key);
        if(key instanceof Services.Composite) {
            List<? extends Object> children = ((Services.Composite) key).keys();
            for(Object childKey : children) {
                buildKeysToKeep(childKey, retainedKeys);
            }
        }
    }

    public Context createContext(Context base, Object key) {
        return serviceManager.createContext(base, key);
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

    void setupBackstack(StateBundle stateBundle, List<?> initialKeys) {
        ArrayList<Object> keys = new ArrayList<>();
        if(stateBundle != null) {
            List<Parcelable> parcelledKeys = stateBundle.getParcelableArrayList(HISTORY_TAG);
            if(parcelledKeys != null) {
                for(Parcelable parcelledKey : parcelledKeys) {
                    keys.add(keyParceler.fromParcelable(parcelledKey));
                }
            }
        }
        if(keys.isEmpty()) {
            keys = new ArrayList<>(initialKeys);
        }
        backstack = new Backstack(keys);
    }

    public void initializeRoot(List<ServiceFactory> servicesFactories, Map<String, Object> _rootServices, StateBundle stateBundle, List<?> initialKeys) {
        Map<String, Object> rootServices = new LinkedHashMap<>(_rootServices);
        restoreStates(stateBundle);
        setupBackstack(stateBundle, initialKeys);
        rootServices.put(ROOT_STACK, new NestedStack(this, keyParceler)); // This can only be done here.
        setupServiceManager(null, null, servicesFactories, rootServices);
    }

    void setupServiceManager(@Nullable ServiceManager parentServiceManager, @Nullable Object parentKey, List<ServiceFactory> servicesFactories, Map<String, Object> rootServices) {
        serviceManager = new ServiceManager(servicesFactories,
                rootServices, parentServiceManager, parentKey, keyParceler);
        serviceManager.restoreServicesForKey(this, ServiceManager.ROOT_KEY);
    }

    public Backstack getBackstack() {
        return backstack;
    }

    public void reattachStateChanger() {
        if(!backstack.hasStateChanger()) {
            backstack.setStateChanger(managedStateChanger, Backstack.REATTACH);
        }
    }

    public void detachStateChanger() {
        if(backstack.hasStateChanger()) {
            backstack.removeStateChanger();
        }
    }

    public void executePendingStateChange() {
        backstack.executePendingStateChange();
    }

    public ServiceManager getServiceManager() {
        return serviceManager;
    }

    public void persistViewToState(@Nullable View view) {
        if(view != null) {
            Object key = ManagedContextWrapper.getKey(view.getContext());
            if(key == null) {
                throw new IllegalArgumentException("The view [" + view + "] contained no key!");
            }
            SparseArray<Parcelable> viewHierarchyState = new SparseArray<>();
            view.saveHierarchyState(viewHierarchyState);
            StateBundle bundle = null;
            if(view instanceof Bundleable) {
                bundle = ((Bundleable) view).toBundle();
            }
            SavedState previousSavedState = getSavedState(key);
            previousSavedState.setViewHierarchyState(viewHierarchyState);
            previousSavedState.setViewBundle(bundle);
            keyStateMap.put(key, previousSavedState);
        }
    }

    public void restoreViewFromState(@NonNull View view) {
        if(view == null) {
            throw new IllegalArgumentException("You cannot restore state into null view!");
        }
        Object newKey = ManagedContextWrapper.getKey(view.getContext());
        SavedState savedState = getSavedState(newKey);
        view.restoreHierarchyState(savedState.getViewHierarchyState());
        if(view instanceof Bundleable) {
            ((Bundleable) view).fromBundle(savedState.getViewBundle());
        }
    }

    public <T> T findService(Object key, String serviceTag) {
        Object service = serviceManager.findServices(key).getService(serviceTag);
        if(service == null) {
            throw new IllegalStateException("The specified service [" + serviceTag + "] does not exist for key [" + key + "]!");
        }
        //noinspection unchecked
        return (T) service;
    }

    public void persistStates() {
        List<Object> history = backstack.getHistory();
        serviceManager.persistServicesForKey(this, ServiceManager.ROOT_KEY);
        if(!history.isEmpty()) {
            serviceManager.persistServicesForKeyHierarchy(this, history.get(history.size() - 1));
        }
    }

    public void restoreStates(StateBundle stateBundle) {
        if(stateBundle != null) {
            List<ParcelledState> savedStates = stateBundle.getParcelableArrayList(STATES_TAG);
            if(savedStates != null) {
                for(ParcelledState parcelledState : savedStates) {
                    SavedState savedState = getSavedState(keyParceler.fromParcelable(parcelledState.parcelableKey));
                    savedState.setViewHierarchyState(parcelledState.viewHierarchyState);
                    savedState.setViewBundle(parcelledState.viewBundle);
                    savedState.setServiceBundle(parcelledState.serviceBundle);
                    keyStateMap.put(savedState.getKey(), savedState);
                }
            }
        }
    }

    public StateBundle toBundle() {
        StateBundle outState = new StateBundle();
        List<Object> history = backstack.getHistory();
        ArrayList<Parcelable> parcelledHistory = new ArrayList<>();
        for(Object key : history) {
            parcelledHistory.add(keyParceler.toParcelable(key));
        }
        outState.putParcelableArrayList(HISTORY_TAG, parcelledHistory);

        ArrayList<ParcelledState> states = new ArrayList<>();
        for(SavedState savedState : keyStateMap.values()) {
            ParcelledState parcelledState = new ParcelledState();
            parcelledState.parcelableKey = keyParceler.toParcelable(savedState.getKey());
            parcelledState.viewHierarchyState = savedState.getViewHierarchyState();
            parcelledState.viewBundle = savedState.getViewBundle() != null ? savedState.getViewBundle() : null;
            parcelledState.serviceBundle = savedState.getServiceBundle();
            states.add(parcelledState);
        }
        outState.putParcelableArrayList(STATES_TAG, states);
        return outState;
    }

    StateChanger getStateChanger() {
        return stateChanger;
    }
}
