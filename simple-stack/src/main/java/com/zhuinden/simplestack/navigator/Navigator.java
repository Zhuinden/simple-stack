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
import android.view.View;
import android.view.ViewGroup;

import com.zhuinden.simplestack.AheadOfTimeBackCallback;
import com.zhuinden.simplestack.AheadOfTimeBackCallbackRegistry;
import com.zhuinden.simplestack.AheadOfTimeWillHandleBackChangedListener;
import com.zhuinden.simplestack.BackHandlingModel;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
        BackHandlingModel backHandlingModel = BackHandlingModel.EVENT_BUBBLING;
        KeyFilter keyFilter = new DefaultKeyFilter();
        KeyParceler keyParceler = new DefaultKeyParceler();
        Backstack.StateClearStrategy stateClearStrategy = new DefaultStateClearStrategy();
        ScopedServices scopedServices = null;
        GlobalServices globalServices = null;
        GlobalServices.Factory globalServiceFactory = null;
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
        @Nonnull
        public Installer setStateChanger(@Nonnull StateChanger stateChanger) {
            if(stateChanger == null) {
                throw new IllegalArgumentException("If set, StateChanger cannot be null!");
            }
            this.stateChanger = stateChanger;
            return this;
        }

        /**
         * Specifies the {@link BackHandlingModel}. This allows switching between back handling models.
         * <p>
         * {@link BackHandlingModel#EVENT_BUBBLING} is used with onBackPressed()/{@link ScopedServices.HandlesBack}.
         * <p>
         * {@link BackHandlingModel#AHEAD_OF_TIME} is used with onBackPressedDispatcher/{@link AheadOfTimeBackCallbackRegistry} and {@link Backstack#addAheadOfTimeWillHandleBackChangedListener(AheadOfTimeWillHandleBackChangedListener)}}.
         *
         * @param backHandlingModel the back handling model
         * @return the installer
         */
        @Nonnull
        public Installer setBackHandlingModel(@Nonnull BackHandlingModel backHandlingModel) {
            if(backHandlingModel == null) {
                throw new IllegalArgumentException("If set, BackHandlingModel cannot be null!");
            }
            this.backHandlingModel = backHandlingModel;
            return this;
        }

        /**
         * Sets the key filter for filtering the state keys to be restored after process death.
         *
         * @param keyFilter cannot be null if set
         * @return the installer
         */
        @Nonnull
        public Installer setKeyFilter(@Nonnull KeyFilter keyFilter) {
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
        @Nonnull
        public Installer setKeyParceler(@Nonnull KeyParceler keyParceler) {
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
        @Nonnull
        public Installer setStateClearStrategy(@Nonnull Backstack.StateClearStrategy stateClearStrategy) {
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
        @Nonnull
        public Installer setScopedServices(@Nonnull ScopedServices scopedServices) {
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
        @Nonnull
        public Installer setGlobalServices(@Nonnull GlobalServices globalServices) {
            if(globalServices == null) {
                throw new IllegalArgumentException("If set, global services cannot be null!");
            }
            this.globalServices = globalServices;
            return this;
        }

        /**
         * Sets a global service factory.
         *
         * @param globalServiceFactory the global service factory
         * @return the installer
         */
        @Nonnull
        public Installer setGlobalServices(@Nonnull GlobalServices.Factory globalServiceFactory) {
            if(globalServiceFactory == null) {
                throw new IllegalArgumentException("If set, global service factory cannot be null!");
            }
            this.globalServiceFactory = globalServiceFactory;
            return this;
        }

        /**
         * Sets if after initialization, the state changer should only be set when {@link Navigator#executeDeferredInitialization(Context)} is called.
         * Typically needed to setup the backstack for dependency injection module.
         *
         * @param isInitializeDeferred if call to executing deferred initialization is needed
         * @return the installer
         */
        @Nonnull
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
        @Nonnull
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
        @Nonnull
        public Installer addStateChangeCompletionListener(@Nonnull Backstack.CompletionListener stateChangeCompletionListener) {
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
        @Nonnull
        public Backstack install(@Nonnull Activity activity, @Nonnull ViewGroup container, @Nonnull List<?> initialKeys) {
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
    @Nonnull
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
    @Nonnull
    public static void install(@Nonnull Activity activity, @Nonnull ViewGroup container, @Nonnull List<?> initialKeys) {
        configure().install(activity, container, initialKeys);
    }

    private static Backstack install(Installer installer, @Nonnull Activity activity, @Nonnull ViewGroup container, @Nonnull List<?> initialKeys) {
        if(activity == null) {
            throw new IllegalArgumentException("Activity cannot be null!");
        }
        if(container == null) {
            throw new IllegalArgumentException("Container cannot be null!");
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
        backstackHost.backHandlingModel = installer.backHandlingModel;
        backstackHost.keyFilter = installer.keyFilter;
        backstackHost.keyParceler = installer.keyParceler;
        backstackHost.stateClearStrategy = installer.stateClearStrategy;
        backstackHost.scopedServices = installer.scopedServices;
        backstackHost.globalServices = installer.globalServices;
        backstackHost.globalServiceFactory = installer.globalServiceFactory;
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
    public static void executeDeferredInitialization(@Nonnull Context context) {
        Activity activity = findActivity(context);
        BackstackHost backstackHost = findBackstackHost(activity);
        backstackHost.initialize(false);
    }

    /**
     * Delegates back press call to the backstack of the navigator.
     *
     * @deprecated Same behavior as calling {@link Backstack#goBack()} on {@link #getBackstack(Context)}, but as Activity's onBackPressed is deprecated, so is this.
     *
     * @param context the Context that belongs to an Activity which hosts the backstack.
     * @return true if a state change was handled or is in progress, false otherwise
     */
    @Deprecated
    public static boolean onBackPressed(@Nonnull Context context) {
        return getBackstack(context).goBack();
    }

    /**
     * Returns if a given scope exists.
     *
     * @param scopeTag   the scope tag
     *
     * @return whether the scope exists
     */
    public static boolean hasScope(@Nonnull Context context, @Nonnull String scopeTag) {
        return getBackstack(context).hasScope(scopeTag);
    }

    /**
     * Returns if a service is bound to the scope of the {@link ScopeKey} by the provided tag.
     *
     * @param scopeKey   the scope key
     * @param serviceTag the service tag
     * @return whether the service is bound in the given scope
     */
    public static boolean hasService(@Nonnull Context context, @Nonnull ScopeKey scopeKey, @Nonnull String serviceTag) {
        return getBackstack(context).hasService(scopeKey, serviceTag);
    }

    /**
     * Returns if a service is bound to the scope identified by the provided tag.
     *
     * @param scopeTag   the scope tag
     * @param serviceTag the service tag
     * @return whether the service is bound in the given scope
     */
    public static boolean hasService(@Nonnull Context context, @Nonnull String scopeTag, @Nonnull String serviceTag) {
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
    @Nonnull
    public static <T> T getService(@Nonnull Context context, @Nonnull ScopeKey scopeKey, @Nonnull String serviceTag) {
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
    @Nonnull
    public static <T> T getService(@Nonnull Context context, @Nonnull String scopeTag, @Nonnull String serviceTag) {
        return getBackstack(context).getService(scopeTag, serviceTag);
    }

    /**
     * Attempts to look-up the service in all currently existing scopes, starting from the last added scope.
     * Returns whether the service exists in any scopes.
     *
     * @param serviceTag the tag of the service
     * @return whether the service exists in any active scopes
     */
    public static boolean canFindService(@Nonnull Context context, @Nonnull String serviceTag) {
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
    @Nonnull
    public static <T> T lookupService(@Nonnull Context context, @Nonnull String serviceTag) {
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
    @Nonnull
    public static List<String> findScopesForKey(@Nonnull Context context, @Nonnull Object key, @Nonnull ScopeLookupMode lookupMode) {
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
    public static boolean canFindFromScope(@Nonnull Context context, @Nonnull String scopeTag, @Nonnull String serviceTag) {
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
    public static boolean canFindFromScope(@Nonnull Context context, @Nonnull String scopeTag, @Nonnull String serviceTag, @Nonnull ScopeLookupMode lookupMode) {
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
    @Nonnull
    public static <T> T lookupFromScope(@Nonnull Context context, @Nonnull String scopeTag, @Nonnull String serviceTag) {
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
    @Nonnull
    public static <T> T lookupFromScope(@Nonnull Context context, @Nonnull String scopeTag, @Nonnull String serviceTag, @Nonnull ScopeLookupMode lookupMode) {
        return getBackstack(context).lookupFromScope(scopeTag, serviceTag, lookupMode);
    }

    /**
     * Gets the backstack that belongs to the Activity which hosts the backstack.
     *
     * @param context the context
     * @return the backstack
     */
    @Nonnull
    public static Backstack getBackstack(@Nonnull Context context) {
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
    public static void restoreViewFromState(@Nonnull View view) {
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
    @Nonnull
    public static SavedState getSavedState(@Nonnull Context context, @Nonnull Object key) {
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
    public static boolean isNavigatorAvailable(@Nonnull Activity activity) {
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
    @Nonnull
    public static <T extends Activity> T findActivity(@Nonnull Context context) {
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
