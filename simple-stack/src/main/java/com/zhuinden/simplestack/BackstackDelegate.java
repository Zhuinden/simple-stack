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
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A delegate class that manages the {@link Backstack}'s Activity lifecycle integration,
 * and provides view-state persistence for custom views that are associated with a key using {@link ManagedContextWrapper}.
 *
 * This should be used in Activities to make sure that the {@link Backstack} survives both configuration changes and process death.
 */
public final class BackstackDelegate {
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

    final BackstackManager backstackManager;

    private static final String HISTORY = "simplestack.HISTORY";

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
        if(backstackManager.getBackstack() != null) {
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

    String getHistoryTag() {
        return "".equals(persistenceTag) ? HISTORY : HISTORY + persistenceTag;
    }

    private final KeyParceler keyParceler;

    StateChanger stateChanger;

    List<ServiceFactory> servicesFactories;
    Map<String, Object> rootServices;

    protected BackstackDelegate(@Nullable StateChanger stateChanger, @NonNull List<ServiceFactory> servicesFactories, @NonNull Map<String, Object> rootServices, KeyParceler keyParceler, BackstackManager backstackManager) {
        this.stateChanger = stateChanger;
        this.servicesFactories = servicesFactories;
        this.rootServices = rootServices;
        this.backstackManager = backstackManager;
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
     * Allows the configuration of the initial state changer, the scoped service factories, and the root services.
     */
    public static class Builder {
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
            return new BackstackDelegate(stateChanger, servicesFactories, rootServices, keyParceler, new BackstackManager(keyParceler));
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
        StateBundle stateBundle = null;
        if(savedInstanceState != null) {
            stateBundle = StateBundle.from(savedInstanceState.getBundle(getHistoryTag()));
            backstackManager.restoreStates(stateBundle);
        }
        NonConfigurationInstance nonConfig = (NonConfigurationInstance) nonConfigurationInstance;
        if(nonConfig != null) {
            backstackManager.setBackstack(nonConfig.getBackstack());
            backstackManager.setServiceManager(nonConfig.getServiceManager());
        } else {
            servicesFactories.add(0, new ServiceFactory() {
                @Override
                public void bindServices(@NonNull Services.Builder builder) {
                    Object parentKey = builder.getService(BackstackManager.LOCAL_KEY);
                    if(parentKey != null) {
                        builder.withService(BackstackManager.PARENT_KEY, parentKey);
                    }
                    builder.withService(BackstackManager.LOCAL_KEY, builder.getKey());
                    NestedStack parentStack = builder.getService(BackstackManager.LOCAL_STACK);
                    if(parentStack == null) {
                        parentStack = builder.getService(BackstackManager.ROOT_STACK);
                    }
                    builder.withService(BackstackManager.LOCAL_STACK, new NestedStack(parentStack, keyParceler));
                }
            });
            backstackManager.initialize(servicesFactories, rootServices, stateBundle, initialKeys);
        }
        backstackManager.setStateChanger(stateChanger);
    }

    /**
     * Sets the {@link StateChanger} to the {@link Backstack}. Removes the previous one if it there was already one set.
     * This call begins an initialize {@link StateChange}.
     *
     * @param stateChanger The {@link StateChanger} to be set.
     */
    public void setStateChanger(@Nullable StateChanger stateChanger) {
        this.stateChanger = stateChanger;
        backstackManager.setStateChanger(stateChanger);
    }


    /**
     * The onRetainCustomNonConfigurationInstance() delegate for the Activity.
     * This is required to make sure that the Backstack survives configuration change.
     *
     * @return a {@link NonConfigurationInstance} that contains the internal backstack instance.
     */
    public NonConfigurationInstance onRetainCustomNonConfigurationInstance() {
        return new NonConfigurationInstance(backstackManager.getBackstack(), backstackManager.getServiceManager());
    }

    /**
     * The onBackPressed() delegate for the Activity.
     * The call is delegated to {@link Backstack#goBack()}'.
     *
     * @return true if the {@link Backstack} handled the back press
     */
    public boolean onBackPressed() {
        return backstackManager.getBackstack().goBack();
    }

    /**
     * The onSaveInstanceState() delegate for the Activity.
     * This is required in order to save the {@link Backstack} and the {@link SavedState} persisted with {@link BackstackDelegate#persistViewToState(View)}
     * into the Activity saved instance state bundle.
     *
     * @param outState the Bundle into which the backstack history and view states are saved.
     */
    public void onSaveInstanceState(@NonNull Bundle outState) {
        StateBundle stateBundle = backstackManager.toBundle();
        outState.putBundle(getHistoryTag(), stateBundle.toBundle());
    }

    /**
     * The onPostResume() delegate for the Activity.
     * It re-attaches the {@link StateChanger} if it is not already set.
     */
    public void onPostResume() {
        if(stateChanger == null) {
            throw new IllegalStateException("State changer is still not set in `onPostResume`!");
        }
        backstackManager.reattachStateChanger();
    }

    /**
     * The onPause() delegate for the Activity.
     * It removes the {@link StateChanger} if it is set.
     */
    public void onPause() {
        backstackManager.detachStateChanger();
    }

    /**
     * The onDestroy() delegate for the Activity.
     * Forces any pending state change to execute with {@link Backstack#executePendingStateChange()},
     * and unregisters the delegate's {@link Backstack.CompletionListener} that was registered in {@link BackstackDelegate#onCreate(Bundle, Object, ArrayList)}.
     */
    public void onDestroy() {
        backstackManager.executePendingStateChange();
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
        if(backstackManager.getBackstack() == null) {
            throw new IllegalStateException("The backstack within the delegate must be initialized by `onCreate()`");
        }
        return backstackManager.getBackstack();
    }

    // ----- viewstate persistence

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
        return backstackManager.createContext(base, key);
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
     * The class which stores the {@link Backstack} for surviving configuration change.
     */
    public static class NonConfigurationInstance {
        private final Backstack backstack;
        private final ServiceManager serviceManager;

        NonConfigurationInstance(Backstack backstack, ServiceManager serviceManager) {
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
