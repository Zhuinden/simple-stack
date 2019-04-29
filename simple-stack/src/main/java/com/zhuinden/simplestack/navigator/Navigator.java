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
package com.zhuinden.simplestack.navigator;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.DefaultKeyFilter;
import com.zhuinden.simplestack.DefaultKeyParceler;
import com.zhuinden.simplestack.DefaultStateClearStrategy;
import com.zhuinden.simplestack.GlobalServices;
import com.zhuinden.simplestack.KeyFilter;
import com.zhuinden.simplestack.KeyParceler;
import com.zhuinden.simplestack.SavedState;
import com.zhuinden.simplestack.ScopeKey;
import com.zhuinden.simplestack.ScopeLookupMode;
import com.zhuinden.simplestack.ScopedServices;
import com.zhuinden.simplestack.StateChanger;

import java.util.LinkedList;
import java.util.List;

/**
 * Convenience class to hide lifecycle integration using retained fragment.
 * Essentially, a replacement for BackstackDelegate.
 *
 * It can be either configured via {@link Navigator#configure()}, or installed with default settings using {@link Navigator#install(Activity, ViewGroup, List)}.
 */
@TargetApi(11)
public class Navigator {
    private Navigator() {
    }

    /**
     * A configurer for {@link Navigator}.
     */
    @TargetApi(11)
    public static class Installer {
        StateChanger stateChanger;
        KeyFilter keyFilter = new DefaultKeyFilter();
        KeyParceler keyParceler = new DefaultKeyParceler();
        Backstack.StateClearStrategy stateClearStrategy = new DefaultStateClearStrategy();
        ScopedServices scopedServices = null;
        GlobalServices globalServices = null;
        boolean isInitializeDeferred = false;
        boolean shouldPersistContainerChild = false;
        List<Backstack.CompletionListener> stateChangeCompletionListeners = new LinkedList<>();

        /**
         * Sets the state changer used by the navigator's backstack.
         *
         * If not set, then {@link DefaultStateChanger} is used, which by default behavior requires keys to be {@link DefaultViewKey}.
         *
         * @param stateChanger if set, cannot be null.
         * @return the installer
         */
        @NonNull
        public Installer setStateChanger(@NonNull StateChanger stateChanger) {
            if(stateChanger == null) {
                throw new IllegalArgumentException("If set, StateChanger cannot be null!");
            }
            this.stateChanger = stateChanger;
            return this;
        }

        /**
         * Sets the key filter for filtering the state keys to be restored after process death.
         *
         * @param keyFilter cannot be null if set
         * @return the installer
         */
        @NonNull
        public Installer setKeyFilter(@NonNull KeyFilter keyFilter) {
            if(keyFilter == null) {
                throw new IllegalArgumentException("If set, KeyFilter cannot be null!");
            }
            this.keyFilter = keyFilter;
            return this;
        }

        /**
         * Sets the key parceler for parcelling state keys.
         *
         * @param keyParceler cannot be null if set
         * @return the installer
         */
        @NonNull
        public Installer setKeyParceler(@NonNull KeyParceler keyParceler) {
            if(keyParceler == null) {
                throw new IllegalArgumentException("If set, KeyParceler cannot be null!");
            }
            this.keyParceler = keyParceler;
            return this;
        }

        /**
         * Sets the state clear strategy used to clear the stored state in Backstack after there are no queued state changes left.
         *
         * @param stateClearStrategy if set, it cannot be null
         * @return the installer
         */
        @NonNull
        public Installer setStateClearStrategy(@NonNull Backstack.StateClearStrategy stateClearStrategy) {
            if(stateClearStrategy == null) {
                throw new IllegalArgumentException("If set, StateClearStrategy cannot be null!");
            }
            this.stateClearStrategy = stateClearStrategy;
            return this;
        }

        /**
         * Sets the scoped services.
         *
         * @param scopedServices the scoped services
         * @return the installer
         */
        @NonNull
        public Installer setScopedServices(@NonNull ScopedServices scopedServices) {
            if(scopedServices == null) {
                throw new IllegalArgumentException("If set, scoped services cannot be null!");
            }
            this.scopedServices = scopedServices;
            return this;
        }

        /**
         * Sets the global services.
         *
         * @param globalServices the global services
         * @return the installer
         */
        @NonNull
        public Installer setGlobalServices(@NonNull GlobalServices globalServices) {
            if(globalServices == null) {
                throw new IllegalArgumentException("If set, global services cannot be null!");
            }
            this.globalServices = globalServices;
            return this;
        }

        /**
         * Sets if after initialization, the state changer should only be set when {@link Navigator#executeDeferredInitialization(Context)} is called.
         * Typically needed to setup the backstack for dependency injection module.
         *
         * @param isInitializeDeferred if call to executing deferred initialization is needed
         * @return the installer
         */
        @NonNull
        public Installer setDeferredInitialization(boolean isInitializeDeferred) {
            this.isInitializeDeferred = isInitializeDeferred;
            return this;
        }

        /**
         * Sets if the {@link BackstackHost} should persist the direct child of the provided container.
         *
         * @param shouldPersistContainerChild if the container's first child's state should be persisted
         * @return the installer
         */
        @NonNull
        public Installer setShouldPersistContainerChild(boolean shouldPersistContainerChild) {
            this.shouldPersistContainerChild = shouldPersistContainerChild;
            return this;
        }

        /**
         * Adds a {@link Backstack.CompletionListener}, which will be added to the {@link Backstack} when it is initialized.
         * As it is added only on initialization, these are added to the Backstack only once.
         *
         * @param stateChangeCompletionListener the state change completion listener
         * @return the installer
         */
        @NonNull
        public Installer addStateChangeCompletionListener(@NonNull Backstack.CompletionListener stateChangeCompletionListener) {
            if(stateChangeCompletionListener == null) {
                throw new IllegalArgumentException("If added, state change completion listener cannot be null!");
            }
            this.stateChangeCompletionListeners.add(stateChangeCompletionListener);
            return this;
        }

        /**
         * Installs the {@link BackstackHost}.
         *
         * @param activity    the activity
         * @param container   the container
         * @param initialKeys the initial keys.
         * @return
         */
        @NonNull
        public Backstack install(@NonNull Activity activity, @NonNull ViewGroup container, @NonNull List<?> initialKeys) {
            if(stateChanger == null) {
                shouldPersistContainerChild = true;
                stateChanger = DefaultStateChanger.create(activity, container);
            }
            return Navigator.install(this, activity, container, initialKeys);
        }
    }

    /**
     * Creates an {@link Installer} to configure the {@link Navigator}.
     *
     * @return the installer
     */
    @NonNull
    public static Installer configure() {
        return new Installer();
    }

    /**
     * Installs the {@link Navigator} with default parameters.
     *
     * This means that {@link DefaultStateChanger} and DefaultStateClearStrategy are used.
     *
     * @param activity    the activity which will host the backstack
     * @param container   the container in which custom viewgroups are hosted (to save its child's state in onSaveInstanceState())
     * @param initialKeys the keys used to initialize the backstack
     */
    @NonNull
    public static void install(@NonNull Activity activity, @NonNull ViewGroup container, @NonNull List<?> initialKeys) {
        configure().install(activity, container, initialKeys);
    }

    private static Backstack install(Installer installer, @NonNull Activity activity, @NonNull ViewGroup container, @NonNull List<?> initialKeys) {
        if(activity == null) {
            throw new IllegalArgumentException("Activity cannot be null!");
        }
        if(container == null) {
            throw new IllegalArgumentException("State changer cannot be null!");
        }
        if(initialKeys == null || initialKeys.isEmpty()) {
            throw new IllegalArgumentException("Initial keys cannot be null!");
        }
        BackstackHost backstackHost = findBackstackHost(activity);
        if(backstackHost == null) {
            backstackHost = new BackstackHost();
            activity.getFragmentManager().beginTransaction().add(backstackHost, "NAVIGATOR_BACKSTACK_HOST").commit();
            activity.getFragmentManager().executePendingTransactions();
        }
        backstackHost.stateChanger = installer.stateChanger;
        backstackHost.keyFilter = installer.keyFilter;
        backstackHost.keyParceler = installer.keyParceler;
        backstackHost.stateClearStrategy = installer.stateClearStrategy;
        backstackHost.scopedServices = installer.scopedServices;
        backstackHost.globalServices = installer.globalServices;
        backstackHost.stateChangeCompletionListeners = installer.stateChangeCompletionListeners;
        backstackHost.shouldPersistContainerChild = installer.shouldPersistContainerChild;
        backstackHost.container = container;
        backstackHost.initialKeys = initialKeys;
        return backstackHost.initialize(installer.isInitializeDeferred);
    }

    /**
     * If {@link Installer#setDeferredInitialization(boolean)} was set to true, then this will initialize the backstack using the state changer.
     *
     * @param context the context to which an activity belongs that hosts the backstack
     */
    public static void executeDeferredInitialization(Context context) {
        Activity activity = findActivity(context);
        BackstackHost backstackHost = findBackstackHost(activity);
        backstackHost.initialize(false);
    }

    /**
     * Delegates back press call to the backstack of the navigator.
     *
     * @param context the Context that belongs to an Activity which hosts the backstack.
     * @return true if a state change was handled or is in progress, false otherwise
     */
    public static boolean onBackPressed(Context context) {
        return getBackstack(context).goBack();
    }


    /**
     * Returns if a given scope exists.
     *
     * @param scopeTag   the scope tag
     *
     * @return whether the scope exists
     */
    public static boolean hasScope(@NonNull Context context, @NonNull String scopeTag) {
        return getBackstack(context).hasScope(scopeTag);
    }

    /**
     * Returns if a service is bound to the scope of the {@link ScopeKey} by the provided tag.
     *
     * @param scopeKey   the scope key
     * @param serviceTag the service tag
     * @return whether the service is bound in the given scope
     */
    public static boolean hasService(@NonNull Context context, @NonNull ScopeKey scopeKey, @NonNull String serviceTag) {
        return getBackstack(context).hasService(scopeKey, serviceTag);
    }

    /**
     * Returns if a service is bound to the scope identified by the provided tag.
     *
     * @param scopeTag   the scope tag
     * @param serviceTag the service tag
     * @return whether the service is bound in the given scope
     */
    public static boolean hasService(@NonNull Context context, @NonNull String scopeTag, @NonNull String serviceTag) {
        return getBackstack(context).hasService(scopeTag, serviceTag);
    }

    /**
     * Returns the service bound to the scope of the {@link ScopeKey} by the provided tag.
     *
     * @param scopeKey   the scope key
     * @param serviceTag the service tag
     * @param <T>        the type of the service
     * @return the service
     */
    @NonNull
    public static <T> T getService(@NonNull Context context, @NonNull ScopeKey scopeKey, @NonNull String serviceTag) {
        return getBackstack(context).getService(scopeKey, serviceTag);
    }

    /**
     * Returns the service bound to the scope identified by the provided tag.
     *
     * @param scopeTag   the scope tag
     * @param serviceTag the service tag
     * @param <T>        the type of the service
     * @return the service
     */
    @NonNull
    public static <T> T getService(@NonNull Context context, @NonNull String scopeTag, @NonNull String serviceTag) {
        return getBackstack(context).getService(scopeTag, serviceTag);
    }

    /**
     * Attempts to look-up the service in all currently existing scopes, starting from the last added scope.
     * Returns whether the service exists in any scopes.
     *
     * @param serviceTag the tag of the service
     * @return whether the service exists in any active scopes
     * @throws IllegalStateException if the service doesn't exist in any scope
     */
    public static boolean canFindService(@NonNull Context context, @NonNull String serviceTag) {
        return getBackstack(context).canFindService(serviceTag);
    }

    /**
     * Attempts to look-up the service in all currently existing scopes, starting from the last added scope.
     * If the service is not found, an exception is thrown.
     *
     * @param serviceTag the tag of the service
     * @param <T>        the type of the service
     * @return the service
     * @throws IllegalStateException if the service doesn't exist in any scope
     */
    @NonNull
    public static <T> T lookupService(@NonNull Context context, @NonNull String serviceTag) {
        return getBackstack(context).lookupService(serviceTag);
    }

    /**
     * Returns a list of the scopes accessible from the given key.
     *
     * The order of the scopes is that the 0th index is the current scope (if available), followed by parents.
     *
     * @param key        the key
     * @param lookupMode determine what type of parents are checked during the lookup
     * @return the list of scope tags
     */
    @NonNull
    public static List<String> findScopesForKey(@NonNull Context context, @NonNull Object key, @NonNull ScopeLookupMode lookupMode) {
        return getBackstack(context).findScopesForKey(key, lookupMode);
    }

    /**
     * Attempts to look-up the service in the scopes accessible from the provided scope tag.
     * Returns whether the service exists in any of these scopes.
     *
     * @param scopeTag   the tag of the scope to start the lookup from
     * @param serviceTag the tag of the service
     * @return whether the service exists in any of the accessed scopes
     */
    public static boolean canFindFromScope(@NonNull Context context, @NonNull String scopeTag, @NonNull String serviceTag) {
        return getBackstack(context).canFindFromScope(scopeTag, serviceTag);
    }

    /**
     * Attempts to look-up the service in the provided scope and the specified type of parents, starting from the provided scope.
     * Returns whether the service exists in any of these scopes.
     *
     * @param scopeTag the tag of the scope to look up from
     * @param serviceTag the tag of the service
     * @param lookupMode determine what type of parents are checked during the lookup
     *
     * @return whether the service exists in any scopes from the current scope or its parents
     */
    public static boolean canFindFromScope(@NonNull Context context, @NonNull String scopeTag, @NonNull String serviceTag, @NonNull ScopeLookupMode lookupMode) {
        return getBackstack(context).canFindFromScope(scopeTag, serviceTag, lookupMode);
    }

    /**
     * Attempts to look-up the service in the scopes accessible from the provided scope tag.
     * If the service is not found, an exception is thrown.
     *
     * @param scopeTag   the tag of the scope to start the lookup from
     * @param serviceTag the tag of the service
     * @param <T>        the type of the service
     * @return the service
     * @throws IllegalStateException if the service doesn't exist in any accessed scopes
     */
    @NonNull
    public static <T> T lookupFromScope(@NonNull Context context, @NonNull String scopeTag, @NonNull String serviceTag) {
        return getBackstack(context).lookupFromScope(scopeTag, serviceTag);
    }


    /**
     * Attempts to look-up the service in the provided scope and its parents, starting from the provided scope.
     * If the service is not found, an exception is thrown.
     *
     * @param serviceTag the tag of the service
     * @param <T>        the type of the service
     * @param lookupMode determine what type of parents are checked during the lookup
     * @return the service
     * @throws IllegalStateException if the service doesn't exist in any of the scopes
     */
    @NonNull
    public static <T> T lookupFromScope(@NonNull Context context, @NonNull String scopeTag, @NonNull String serviceTag, @NonNull ScopeLookupMode lookupMode) {
        return getBackstack(context).lookupFromScope(scopeTag, serviceTag, lookupMode);
    }

    /**
     * Gets the backstack that belongs to the Activity which hosts the backstack.
     *
     * @param context the context
     * @return the backstack
     */
    @NonNull
    public static Backstack getBackstack(Context context) {
        BackstackHost backstackHost = getBackstackHost(context);
        return backstackHost.getBackstack();
    }

    /**
     * Persists the view hierarchy state and optional StateBundle.
     *
     * @param view the view (can be Bundleable)
     */
    public static void persistViewToState(@Nullable View view) {
        if(view != null) {
            Context context = view.getContext();
            BackstackHost backstackHost = getBackstackHost(context);
            backstackHost.getBackstack().persistViewToState(view);
        }
    }

    /**
     * Restores the view hierarchy state and optional StateBundle.
     *
     * @param view the view (can be Bundleable)
     */
    public static void restoreViewFromState(@NonNull View view) {
        if(view == null) {
            throw new NullPointerException("You cannot restore state into null view!");
        }
        Context context = view.getContext();
        BackstackHost backstackHost = getBackstackHost(context);
        backstackHost.getBackstack().restoreViewFromState(view);
    }

    /**
     * Get the saved state for a given key.
     *
     * @param context the context to which an Activity belongs that hosts a backstack
     * @param key     the key
     * @return the saved state
     */
    @NonNull
    public static SavedState getSavedState(@NonNull Context context, @NonNull Object key) {
        if(context == null) {
            throw new NullPointerException("context cannot be null");
        }
        if(key == null) {
            throw new NullPointerException("key cannot be null");
        }
        BackstackHost backstackHost = getBackstackHost(context);
        return backstackHost.getBackstack().getSavedState(key);
    }

    /**
     * Returns if the BackstackHost is properly added to the Activity.
     *
     * @param activity  the activity
     * @return whether navigator is available
     */
    public static boolean isNavigatorAvailable(@NonNull Activity activity) {
        return findBackstackHost(activity) != null;
    }

    private static BackstackHost findBackstackHost(Activity activity) {
        return (BackstackHost) activity.getFragmentManager().findFragmentByTag("NAVIGATOR_BACKSTACK_HOST");
    }

    /**
     * Attempt to find the Activity in the Context through the chain of its base contexts.
     *
     * @throws IllegalArgumentException if context is null
     * @throws IllegalStateException if the context's base context hierarchy doesn't contain an Activity
     *
     * @param context the context
     * @param <T> the type of the Activity
     * @return the Activity
     */
    @NonNull
    public static <T extends Activity> T findActivity(@NonNull Context context) {
        if(context == null) {
            throw new IllegalArgumentException("Context cannot be null!");
        }
        if(context instanceof Activity) {
            // noinspection unchecked
            return (T) context;
        } else {
            ContextWrapper contextWrapper = (ContextWrapper) context;
            Context baseContext = contextWrapper.getBaseContext();
            if(baseContext == null) {
                throw new IllegalStateException("Activity was not found as base context of view!");
            }
            return findActivity(baseContext);
        }
    }

    private static BackstackHost getBackstackHost(Context context) {
        Activity activity = findActivity(context);
        return findBackstackHost(activity);
    }
}
