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

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.View;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A delegate class that manages the {@link Backstack}'s Activity lifecycle integration,
 * and provides view-state persistence for custom views that are associated with a key using {@link ManagedContextWrapper}.
 *
 * This should be used in Activities to make sure that the {@link Backstack} survives both configuration changes and process death.
 */
public class BackstackDelegate {
    static final String ROOT_STACK = "simplestack.ROOT_STACK";
    static final String LOCAL_STACK = "simplestack.LOCAL_STACK";

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

    private final StateChanger managedStateChanger = new StateChanger() {
        @Override
        public final void handleStateChange(StateChange stateChange, Callback completionCallback) {
            //Log.i("ServiceManager", Arrays.toString(stateChange.getPreviousState().toArray()) + " :: " + Arrays.toString(stateChange.getNewState().toArray())); //
            //serviceManager.dumpLogData(); //
            Object topNewKey = stateChange.topNewState();
            boolean isInitializeStateChange = stateChange.getPreviousState().isEmpty();
            boolean servicesUninitialized = (isInitializeStateChange && !serviceManager.hasServices(topNewKey));
            if(servicesUninitialized || !isInitializeStateChange) {
                serviceManager.setUp(BackstackDelegate.this, topNewKey);
            }
            for(int i = stateChange.getPreviousState().size() - 1; i >= 0; i--) {
                Object previousKey = stateChange.getPreviousState().get(i);
                if(serviceManager.hasServices(previousKey) && !stateChange.getNewState().contains(previousKey)) {
                    serviceManager.tearDown(BackstackDelegate.this, false, previousKey);
                }
            }
            Object topPreviousKey = stateChange.topPreviousState();
            if(topPreviousKey != null && stateChange.getNewState().contains(topPreviousKey)) {
                serviceManager.tearDown(BackstackDelegate.this, true, topPreviousKey);
            }
            //serviceManager.dumpLogData(); //
            stateChanger.handleStateChange(stateChange, completionCallback);
        }
    };

    private static final String UNINITIALIZED = "";
    private String persistenceTag = UNINITIALIZED;

    private static final KeyParceler DEFAULT_KEYPARCELER = new KeyParceler() {
        @Override
        public Parcelable toParcelable(Object object) {
            return (Parcelable) object;
        }

        @Override
        public Object fromParcelable(Parcelable parcelable) {
            return parcelable;
        }
    };

    private final KeyParceler keyParceler;

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

    List<ServiceFactory> servicesFactories;
    Map<String, Object> rootServices;
    ServiceManager serviceManager;

    protected BackstackDelegate(@Nullable StateChanger stateChanger, @NonNull List<ServiceFactory> servicesFactories, @NonNull Map<String, Object> rootServices, KeyParceler keyParceler) {
        this.stateChanger = stateChanger;
        this.servicesFactories = servicesFactories;
        this.rootServices = rootServices;
        this.keyParceler = keyParceler;
    }

    /**
     * Obtain a {@link Builder} that allows setting initial state changer, and service factories for scoped services.
     *
     * @return the builder that configures the initial state changer, and services.
     */
    public static Builder configure() {
        return new Builder();
    }

    /**
     * Creates a {@link BackstackDelegate} that has no initial state changer, and no service factories.
     * The initialize {@link StateChange} is postponed until {@link Backstack#setStateChanger(StateChanger, int)} is called.
     * The {@link StateChanger} must be set before {@link BackstackDelegate#onPostResume()}.
     *
     * @return {@link BackstackDelegate} with default configuration.
     */
    public static BackstackDelegate create() {
        return configure().build();
    }

    /**
     * Allows the configuration of the initial state changer, the scoped service factories, and the root services.
     */
    public static class Builder {
        public interface DelegateProvider {
            BackstackDelegate create(StateChanger stateChanger, List<ServiceFactory> serviceFactories, Map<String, Object> rootServices, KeyParceler keyParceler);
        }

        DelegateProvider delegateProvider = new DelegateProvider() {
            @Override
            public BackstackDelegate create(StateChanger stateChanger, List<ServiceFactory> serviceFactories, Map<String, Object> rootServices, KeyParceler keyParceler) {
                return new BackstackDelegate(stateChanger, serviceFactories, rootServices, keyParceler);
            }
        };

        List<ServiceFactory> servicesFactories = new LinkedList<>();
        Map<String, Object> rootServices = new LinkedHashMap<>();
        StateChanger stateChanger;
        KeyParceler keyParceler = DEFAULT_KEYPARCELER;

        private Builder() {
        }

        /**
         * Sets the {@link StateChanger}.
         * If {@link StateChanger} is null, then the initialize {@link StateChange} is postponed until it is explicitly set.
         * The {@link StateChanger} must be set at some point before {@link BackstackDelegate#onPostResume()}.
         *
         * @param stateChanger The {@link StateChanger} to be set. Allowed to be null at initialization.
         * @return the builder.
         */
        public Builder setStateChanger(@Nullable StateChanger stateChanger) {
            this.stateChanger = stateChanger;
            return this;
        }

        /**
         * Adds the {@link ServiceFactory}.
         * When a new key is set up, then the added {@link ServiceFactory}s bind the specified services into the given key's scope.
         *
         * @param serviceFactory The {@link ServiceFactory} that creates or destroys services.
         * @return the builder.
         */
        public Builder addServiceFactory(@NonNull ServiceFactory serviceFactory) {
            if(serviceFactory == null) {
                throw new IllegalArgumentException("Service factory cannot be null!");
            }
            this.servicesFactories.add(serviceFactory);
            return this;
        }

        /**
         * Adds the {@link ServiceFactory}s.
         * When a new key is set up, then the added {@link ServiceFactory}s bind the specified services into the given key's scope.
         *
         * @param serviceFactories The {@link ServiceFactory}s that create or destroy services.
         * @return the builder.
         */
        public Builder addServiceFactories(@NonNull Collection<? extends ServiceFactory> serviceFactories) {
            if(serviceFactories == null) {
                throw new IllegalArgumentException("Service factories cannot be null!");
            }
            this.servicesFactories.addAll(serviceFactories);
            return this;
        }

        /**
         * Binds a root service that is visible for all child scopes.
         *
         * @param serviceTag the tag that identifies the service within its scope.
         * @param service
         * @return
         */
        public Builder withRootService(@NonNull String serviceTag, @NonNull Object service) {
            if(serviceTag == null) {
                throw new IllegalArgumentException("Service tag cannot be null!");
            }
            if(service == null) {
                throw new IllegalArgumentException("Service cannot be null!");
            }
            this.rootServices.put(serviceTag, service);
            return this;
        }

        /**
         * Provides a hook to replace the BackstackDelegate implementation if necessary.
         *
         * @param delegateProvider The provider that instantiates the BackstackDelegate.
         * @return the builder.
         */
        public Builder setDelegateProvider(@NonNull DelegateProvider delegateProvider) {
            if(delegateProvider == null) {
                throw new IllegalArgumentException("If specified, then the delegate provider must not be null!");
            }
            this.delegateProvider = delegateProvider;
            return this;
        }

        /**
         * Specifies a custom {@link KeyParceler}, allowing key parcellation strategies to be used for turning a key into Parcelable.
         *
         * @param keyParceler The custom {@link KeyParceler}.
         */
        public void setKeyParceler(KeyParceler keyParceler) {
            if(keyParceler == null) {
                throw new IllegalArgumentException("The key parceler cannot be null!");
            }
            this.keyParceler = keyParceler;
        }

        public BackstackDelegate build() {
            servicesFactories.add(0, new ServiceFactory() {
                @Override
                public void bindServices(@NonNull Services.Builder builder) {
                    NestedStack parentStack = builder.getService(LOCAL_STACK);
                    if(parentStack == null) {
                        parentStack = builder.getService(ROOT_STACK);
                    }
                    builder.withService(LOCAL_STACK, new NestedStack(parentStack, keyParceler));
                }
            });
            return delegateProvider.create(stateChanger, servicesFactories, rootServices, keyParceler);
        }
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
                    SavedState savedState = getSavedState(keyParceler.fromParcelable(parcelledState.parcelableKey));
                    savedState.setViewHierarchyState(parcelledState.viewHierarchyState);
                    Bundle bundle = parcelledState.bundle;
                    savedState.setViewBundle(StateBundle.from(bundle.getBundle("VIEW_BUNDLE")));
                    savedState.setServiceBundle(StateBundle.from(bundle.getBundle("SERVICE_BUNDLE")));
                    keyStateMap.put(savedState.getKey(), savedState);
                }
            }
        } else {
            keys = initialKeys;
        }
        NonConfigurationInstance nonConfig = (NonConfigurationInstance) nonConfigurationInstance;
        if(nonConfig != null) {
            backstack = nonConfig.getBackstack();
            serviceManager = nonConfig.getServiceManager();
        } else {
            backstack = new Backstack(keys);
            rootServices.put(ROOT_STACK, new NestedStack(backstack, keyParceler)); // This can only be done here.
            serviceManager = new ServiceManager(servicesFactories, rootServices);
            serviceManager.restoreServicesForKey(this, ServiceManager.ROOT_KEY);
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

    private void initializeBackstack(StateChanger stateChanger) {
        if(stateChanger != null) {
            backstack.setStateChanger(managedStateChanger);
        }
    }

    /**
     * The onRetainCustomNonConfigurationInstance() delegate for the Activity.
     * This is required to make sure that the Backstack survives configuration change.
     *
     * @return a {@link NonConfigurationInstance} that contains the internal backstack instance.
     */
    public NonConfigurationInstance onRetainCustomNonConfigurationInstance() {
        return new NonConfigurationInstance(backstack, serviceManager);
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
        List<Object> history = backstack.getHistory();
        ArrayList<Parcelable> parcelledHistory = new ArrayList<>();
        for(Object key : history) {
            parcelledHistory.add(keyParceler.toParcelable(key));
        }
        outState.putParcelableArrayList(getHistoryTag(), parcelledHistory);

        serviceManager.persistServicesForKey(this, ServiceManager.ROOT_KEY);
        if(!history.isEmpty()) {
            serviceManager.persistServicesForKeyHierarchy(this, history.get(history.size() - 1));
        }
        ArrayList<ParcelledState> states = new ArrayList<>();
        for(SavedState savedState : keyStateMap.values()) {
            ParcelledState parcelledState = new ParcelledState();
            parcelledState.parcelableKey = keyParceler.toParcelable(savedState.getKey());
            parcelledState.viewHierarchyState = savedState.getViewHierarchyState();
            Bundle bundle = new Bundle();
            bundle.putBundle("VIEW_BUNDLE", savedState.getViewBundle() != null ? savedState.getViewBundle().toBundle() : null);
            bundle.putBundle("SERVICE_BUNDLE", savedState.getServiceBundle().toBundle());
            parcelledState.bundle = bundle;
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
            backstack.setStateChanger(managedStateChanger, Backstack.REATTACH);
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
     * Provides the means to save the provided view's hierarchy state, and its optional {@link StateBundle} via {@link Bundleable} into a {@link SavedState}.
     *
     * @param view the view that belongs to a certain key
     */
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

    /**
     * Restores the state of the view based on the currently stored {@link SavedState}, according to the view's key.
     *
     * @param view the view that belongs to a certain key
     */
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

    private void stateChangeCompleted(StateChange stateChange) {
        if(!backstack.isStateChangePending()) {
            clearStatesNotIn(keyStateMap, stateChange);
        }
    }

    private void clearStatesNotIn(@NonNull Map<Object, SavedState> keyStateMap, @NonNull StateChange stateChange) {
        Set<Object> retainedKeys = new LinkedHashSet<>();
        retainedKeys.add(ServiceManager.ROOT_KEY);
        for(Object key : stateChange.getNewState()) {
            buildKeysToKeep(key, retainedKeys);
        }
        retainedKeys.addAll(getAdditionalRetainedKeys(stateChange));
        keyStateMap.keySet().retainAll(retainedKeys);
    }

    protected Collection<? extends Object> getAdditionalRetainedKeys(@NonNull StateChange stateChange) {
        return Collections.emptySet();
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

    // Context sharing

    /**
     * Creates a {@link ManagedContextWrapper} using the provided key.
     *
     * @param base the context used as base for the new context wrapper.
     * @param key  the key this context is associated with.
     * @return the context to use used with LayoutInflater.from().
     */
    @NonNull
    public Context createContext(Context base, Object key) {
        return serviceManager.createContext(base, key);
    }

    /**
     * The class which stores the {@link Backstack} for surviving configuration change.
     */
    public static class NonConfigurationInstance {
        private final Backstack backstack;
        private final ServiceManager serviceManager;

        private NonConfigurationInstance(Backstack backstack, ServiceManager serviceManager) {
            this.backstack = backstack;
            this.serviceManager = serviceManager;
        }

        Backstack getBackstack() {
            return backstack;
        }

        ServiceManager getServiceManager() {
            return serviceManager;
        }
    }
}
