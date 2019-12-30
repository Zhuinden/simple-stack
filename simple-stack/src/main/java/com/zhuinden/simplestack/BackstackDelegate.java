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

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.zhuinden.statebundle.StateBundle;

import java.util.LinkedList;
import java.util.List;

/**
 * A delegate class that manages the {@link Backstack}'s Activity lifecycle integration,
 * and provides view-state persistence for custom views that are associated with a key using {@link KeyContextWrapper}.
 *
 * This should be used in Activities to make sure that the {@link Backstack} survives both configuration changes and process death.
 */
@Deprecated
public class BackstackDelegate {
    private Backstack backstack;

    private static final String UNINITIALIZED = "";
    private String persistenceTag = UNINITIALIZED;

    private boolean activityWasDestroyed = false;

    /**
     * Specifies a custom {@link KeyFilter}, allowing keys to be filtered out if they should not be restored after process death.
     *
     * If used, this method must be called before {@link BackstackDelegate#onCreate(Bundle, Object, List)}.
     *
     * @param keyFilter The custom {@link KeyFilter}.
     */
    public void setKeyFilter(@NonNull KeyFilter keyFilter) {
        if(backstack != null && backstack.isInitialized()) {
            throw new IllegalStateException("If set, key filter must be set before calling `onCreate()`");
        }
        if(keyFilter == null) {
            throw new IllegalArgumentException("Specified custom key filter should not be null!");
        }
        this.keyFilter = keyFilter;
    }

    /**
     * Specifies a custom {@link KeyParceler}, allowing key parcellation strategies to be used for turning a key into Parcelable.
     *
     * If used, this method must be called before {@link BackstackDelegate#onCreate(Bundle, Object, List)}.
     *
     * @param keyParceler The custom {@link KeyParceler}.
     */
    public void setKeyParceler(@NonNull KeyParceler keyParceler) {
        if(backstack != null && backstack.isInitialized()) {
            throw new IllegalStateException("If set, key parceler must set before calling `onCreate()`");
        }
        if(keyParceler == null) {
            throw new IllegalArgumentException("Specified custom key parceler should not be null!");
        }
        this.keyParceler = keyParceler;
    }

    /**
     * Specifies a custom {@link Backstack.StateClearStrategy}, allowing a custom way of retaining saved state.
     * The {@link DefaultStateClearStrategy} clears saved state for keys not found in the new state.
     *
     * If used, this method must be called before {@link BackstackDelegate#onCreate(Bundle, Object, List)}.
     *
     * @param stateClearStrategy The custom {@link Backstack.StateClearStrategy}.
     */
    public void setStateClearStrategy(@NonNull Backstack.StateClearStrategy stateClearStrategy) {
        if(backstack != null && backstack.isInitialized()) {
            throw new IllegalStateException("If set, state clear strategy must be set before calling `onCreate()`");
        }
        if(stateClearStrategy == null) {
            throw new IllegalArgumentException("Specified state clear strategy should not be null!");
        }
        this.stateClearStrategy = stateClearStrategy;
    }

    /**
     * Specifies {@link ScopedServices}, allowing the configuration and creation of scoped services.
     *
     * If used, this method must be called before {@link BackstackDelegate#onCreate(Bundle, Object, List)}.
     *
     * @param activity the Activity to track whether it is finalized. If null, {@link BackstackDelegate#onDestroy()} will finalize scopes. Otherwise, only if the Activity is finishing.
     * @param scopedServices The {@link ScopedServices}.
     */
    public void setScopedServices(@Nullable Activity activity, @NonNull ScopedServices scopedServices) {
        this.activity = activity;

        if(scopedServices == null) {
            throw new IllegalArgumentException("Specified scoped services should not be null!");
        }
        if(!activityWasDestroyed && (backstack != null && backstack.isInitialized())) {
            throw new IllegalStateException("If set, scoped services must set before calling `onCreate()`");
        }
        activityWasDestroyed = false;
        this.scopedServices = scopedServices;
    }

    /**
     * Specifies {@link GlobalServices}, allowing the configuration and creation of global services.
     *
     * If used, this method must be called before {@link BackstackDelegate#onCreate(Bundle, Object, List)}.
     *
     * @param activity the Activity to track whether it is finalized. If null, {@link BackstackDelegate#onDestroy()} will finalize scopes. Otherwise, only if the Activity is finishing.
     * @param globalServices The {@link GlobalServices}.
     */
    public void setGlobalServices(@Nullable Activity activity, @NonNull GlobalServices globalServices) {
        this.activity = activity;

        if(globalServices == null) {
            throw new IllegalArgumentException("Specified global services should not be null!");
        }
        if(!activityWasDestroyed && (backstack != null && backstack.isInitialized())) {
            throw new IllegalStateException("If set, global services must set before calling `onCreate()`");
        }
        activityWasDestroyed = false;
        this.globalServices = globalServices;
    }

    /**
     * Specifies {@link GlobalServices.Factory}, allowing the configuration and deferred creation of global services.
     *
     * If used, this method must be called before {@link BackstackDelegate#onCreate(Bundle, Object, List)}.
     *
     * @param activity             the Activity to track whether it is finalized. If null, {@link BackstackDelegate#onDestroy()} will finalize scopes. Otherwise, only if the Activity is finishing.
     * @param globalServiceFactory The {@link GlobalServices.Factory}.
     */
    public void setGlobalServices(@Nullable Activity activity, @NonNull GlobalServices.Factory globalServiceFactory) {
        this.activity = activity;

        if(globalServiceFactory == null) {
            throw new IllegalArgumentException("Specified global service factory should not be null!");
        }
        if(!activityWasDestroyed && (backstack != null && backstack.isInitialized())) {
            throw new IllegalStateException("If set, global service factory must set before calling `onCreate()`");
        }
        activityWasDestroyed = false;
        this.globalServiceFactory = globalServiceFactory;
    }

    /**
     * Adds a {@link Backstack.CompletionListener}, which will be added to the {@link Backstack} when it is initialized.
     * As it is added only on initialization, these are added to the Backstack only once.
     *
     * Please note that this should not be an anonymous inner class, because this is kept across configuration changes.
     *
     * This can only be called before {@link BackstackDelegate#onCreate(Bundle, Object, List)}.
     *
     * @param stateChangeCompletionListener the state change completion listener
     */
    public void addStateChangeCompletionListener(@NonNull Backstack.CompletionListener stateChangeCompletionListener) {
        if(backstack != null && backstack.isInitialized()) {
            throw new IllegalStateException("If adding, completion listener must be added before calling `onCreate()`");
        }
        if(stateChangeCompletionListener == null) {
            throw new IllegalArgumentException("Specified state change completion listener should not be null!");
        }
        this.stateChangeCompletionListeners.add(stateChangeCompletionListener);
    }

    private static final String HISTORY = "simplestack.HISTORY";

    private Activity activity;

    private StateChanger stateChanger;

    private KeyFilter keyFilter = new DefaultKeyFilter();
    private KeyParceler keyParceler = new DefaultKeyParceler();
    private ScopedServices scopedServices = null;
    private GlobalServices globalServices = null;
    private GlobalServices.Factory globalServiceFactory = null;
    private Backstack.StateClearStrategy stateClearStrategy = new DefaultStateClearStrategy();
    private List<Backstack.CompletionListener> stateChangeCompletionListeners = new LinkedList<>();

    /**
     * Persistence tag allows you to have multiple {@link BackstackDelegate}s in the same activity.
     * This is required to make sure that the {@link Backstack} states do not overwrite each other in the saved instance state bundle.
     * If used, this method must be called before {@link BackstackDelegate#onCreate(Bundle, Object, List)}.
     * A persistence tag can only be set once on a given BackstackDelegate instance.
     *
     * @param persistenceTag a non-null persistence tag that uniquely identifies this {@link BackstackDelegate} inside the Activity.
     */
    @SuppressWarnings("StringEquality")
    public void setPersistenceTag(@NonNull String persistenceTag) {
        if(backstack != null && backstack.isInitialized()) {
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

    /**
     * Creates the {@link BackstackDelegate}.
     *
     * The {@link StateChanger} must be set at some point before {@link BackstackDelegate#onPostResume()}.
     */
    public BackstackDelegate() {
        this(null);
    }

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
     * Convenience method that automatically handles calling the following methods:
     * - {@link BackstackDelegate#onPostResume()}
     * - {@link BackstackDelegate#onPause()}
     * - {@link BackstackDelegate#onSaveInstanceState(Bundle)}
     * - {@link BackstackDelegate#onDestroy()}.
     *
     * This method can only be called after {@link BackstackDelegate#onCreate(Bundle, Object, List)}.
     *
     * Note: This method cannot handle {@link BackstackDelegate#onRetainCustomNonConfigurationInstance()}, so that must still be called manually.
     *
     * @param activity the Activity whose callbacks we register for.
     */
    @TargetApi(14)
    public void registerForLifecycleCallbacks(@NonNull final Activity activity) {
        if(activity == null) {
            throw new NullPointerException("Activity is null");
        }
        @SuppressWarnings("unused") Backstack backstack = getBackstack();
        final Application application = activity.getApplication();
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity a, Bundle savedInstanceState) {
                // this is executed too late. do nothing.
            }

            @Override
            public void onActivityStarted(Activity a) {
                if(activity == a) {
                    // do nothing
                }
            }

            @Override
            public void onActivityResumed(Activity a) {
                if(activity == a) {
                    onPostResume();
                }
            }

            @Override
            public void onActivityPaused(Activity a) {
                if(activity == a) {
                    onPause();
                }
            }

            @Override
            public void onActivityStopped(Activity a) {
                if(activity == a) {
                    // do nothing
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity a, Bundle outState) {
                if(activity == a) {
                    onSaveInstanceState(outState);
                }
            }

            @Override
            public void onActivityDestroyed(Activity a) {
                if(activity == a) {
                    onDestroy();
                    application.unregisterActivityLifecycleCallbacks(this);
                }
            }
        });
    }

    /**
     * The onCreate() delegate for the Activity.
     * It initializes the backstack from either the non-configuration instance, the saved state, or creates a new one.
     * Restores the {@link SavedState} that belongs to persisted view state.
     * Begins an initialize {@link StateChange} if the {@link StateChanger} is set.
     *
     * @param savedInstanceState       The Activity saved instance state bundle.
     * @param nonConfigurationInstance The {@link NonConfigurationInstance} that is typically obtained with getLastCustomNonConfigurationInstance().
     * @param initialKeys              A list of the keys that are used to set as initial history of the backstack.
     */
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable Object nonConfigurationInstance, @NonNull List<?> initialKeys) {
        if(nonConfigurationInstance != null && !(nonConfigurationInstance instanceof NonConfigurationInstance)) {
            throw new IllegalArgumentException(
                    "The provided non configuration instance must be of type BackstackDelegate.NonConfigurationInstance!");
        }
        NonConfigurationInstance nonConfig = (NonConfigurationInstance) nonConfigurationInstance;
        if(nonConfig != null) {
            backstack = nonConfig.getBackstack();
        }
        if(backstack == null) {
            backstack = new Backstack();
            backstack.setKeyFilter(keyFilter);
            backstack.setKeyParceler(keyParceler);
            backstack.setStateClearStrategy(stateClearStrategy);
            if(scopedServices != null) {
                backstack.setScopedServices(scopedServices);
            }
            if(globalServices != null) {
                backstack.setGlobalServices(globalServices);
            }
            if(globalServiceFactory != null) {
                backstack.setGlobalServices(globalServiceFactory);
            }
            backstack.setup(initialKeys);
            for(Backstack.CompletionListener completionListener : stateChangeCompletionListeners) {
                backstack.addStateChangeCompletionListener(completionListener);
            }
            if(savedInstanceState != null) {
                backstack.fromBundle(savedInstanceState.<StateBundle>getParcelable(getHistoryTag()));
            }
        }
        backstack.setStateChanger(stateChanger);
    }

    /**
     * Sets the {@link StateChanger} to the {@link Backstack}. Removes the previous one if it there was already one set.
     * This call begins an initialize {@link StateChange}.
     *
     * @param stateChanger The {@link StateChanger} to be set.
     */
    public void setStateChanger(@Nullable StateChanger stateChanger) {
        this.stateChanger = stateChanger;
        if(backstack != null) { // allowed before `onCreate()`
            backstack.setStateChanger(stateChanger);
        }
    }

    /**
     * The onRetainCustomNonConfigurationInstance() delegate for the Activity.
     * This is required to make sure that the Backstack survives configuration change.
     *
     * @return a {@link NonConfigurationInstance} that contains the internal {@link Backstack}.
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
        return getBackstack().goBack();
    }

    /**
     * The onSaveInstanceState() delegate for the Activity.
     * This is required in order to save the {@link Backstack} and the {@link SavedState} persisted with {@link BackstackDelegate#persistViewToState(View)}
     * into the Activity saved instance state bundle.
     *
     * @param outState the Bundle into which the backstack history and view states are saved.
     */
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Backstack backstack = getBackstack(); // assertion!
        outState.putParcelable(getHistoryTag(), backstack.toBundle());
    }

    /**
     * The onPostResume() delegate for the Activity.
     * It re-attaches the {@link StateChanger} if it is not already set.
     */
    public void onPostResume() {
        if(stateChanger == null) {
            throw new IllegalStateException("State changer is still not set in `onPostResume`!");
        }
        getBackstack().reattachStateChanger();
    }

    /**
     * The onPause() delegate for the Activity.
     * It removes the {@link StateChanger} if it is set.
     */
    public void onPause() {
        getBackstack().detachStateChanger();
    }

    /**
     * The onDestroy() delegate for the Activity.
     *
     * Forces any pending state change to execute with {@link Backstack#executePendingStateChange()}.
     *
     * Also, if {@link ScopedServices} are used, this is what destroys the remaining scopes if the Activity is finishing.
     *
     * NOTE: if the Activity is not set, then the scopes are finalized.
     */
    public void onDestroy() {
        activityWasDestroyed = true;
        getBackstack().executePendingStateChange();

        //noinspection ConstantConditions
        if(activity == null || (activity != null && activity.isFinishing())) {
            getBackstack().finalizeScopes();
        }
        activity = null;
    }

    // ----- viewstate persistence

    /**
     * Provides the means to save the provided view's hierarchy state, and its optional StateBundle via {@link Bundleable} into a {@link SavedState}.
     *
     * @param view the view that belongs to a certain key
     */
    public void persistViewToState(@Nullable View view) {
        getBackstack().persistViewToState(view);
    }

    /**
     * Restores the state of the view based on the currently stored {@link SavedState}, according to the view's key.
     *
     * @param view the view that belongs to a certain key
     */
    public void restoreViewFromState(@NonNull View view) {
        getBackstack().restoreViewFromState(view);
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
        return getBackstack().getSavedState(key);
    }

    /**
     * Returns if a given scope exists.
     *
     * @param scopeTag   the scope tag
     *
     * @return whether the scope exists
     */
    public boolean hasScope(@NonNull String scopeTag) {
        return getBackstack().hasScope(scopeTag);
    }

    /**
     * Returns if a service is bound to the scope of the {@link ScopeKey} by the provided tag.
     *
     * @param scopeKey   the scope key
     * @param serviceTag the service tag
     * @return whether the service is bound in the given scope
     */
    public boolean hasService(@NonNull ScopeKey scopeKey, @NonNull String serviceTag) {
        return getBackstack().hasService(scopeKey, serviceTag);
    }

    /**
     * Returns if a service is bound to the scope identified by the provided tag.
     *
     * @param scopeTag   the tag of the scope
     * @param serviceTag the service tag
     * @return whether the service is bound in the given scope
     */
    public boolean hasService(@NonNull String scopeTag, @NonNull String serviceTag) {
        return getBackstack().hasService(scopeTag, serviceTag);
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
    public <T> T getService(@NonNull ScopeKey scopeKey, @NonNull String serviceTag) {
        return getBackstack().getService(scopeKey, serviceTag);
    }

    /**
     * Returns the service bound to the scope identified by the provided tag.
     *
     * @param scopeTag   the tag of the scope
     * @param serviceTag the service tag
     * @param <T>        the type of the service
     * @return the service
     */
    @NonNull
    public <T> T getService(@NonNull String scopeTag, @NonNull String serviceTag) {
        return getBackstack().getService(scopeTag, serviceTag);
    }

    /**
     * Attempts to look-up the service in all currently existing scopes, starting from the last added scope.
     * Returns whether the service exists in any scopes.
     *
     * @param serviceTag the tag of the service
     * @return whether the service exists in any active scopes
     */
    public boolean canFindService(@NonNull String serviceTag) {
        return getBackstack().canFindService(serviceTag);
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
    public <T> T lookupService(@NonNull String serviceTag) {
        return getBackstack().lookupService(serviceTag);
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
    public List<String> findScopesForKey(@NonNull Object key, @NonNull ScopeLookupMode lookupMode) {
        return getBackstack().findScopesForKey(key, lookupMode);
    }

    /**
     * Attempts to look-up the service in the scopes accessible from the provided scope tag.
     * Returns whether the service exists in any of these scopes.
     *
     * @param scopeTag   the tag of the scope to start the lookup from
     * @param serviceTag the tag of the service
     * @return whether the service exists in any of the accessed scopes
     */
    public boolean canFindFromScope(@NonNull String scopeTag, @NonNull String serviceTag) {
        return getBackstack().canFindFromScope(scopeTag, serviceTag);
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
    public boolean canFindFromScope(@NonNull String scopeTag, @NonNull String serviceTag, @NonNull ScopeLookupMode lookupMode) {
        return getBackstack().canFindFromScope(scopeTag, serviceTag, lookupMode);
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
    public <T> T lookupFromScope(@NonNull String scopeTag, @NonNull String serviceTag) {
        return getBackstack().lookupFromScope(scopeTag, serviceTag);
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
    public <T> T lookupFromScope(@NonNull String scopeTag, @NonNull String serviceTag, @NonNull ScopeLookupMode lookupMode) {
        return getBackstack().lookupFromScope(scopeTag, serviceTag, lookupMode);
    }

    /**
     * Returns the {@link Backstack} that belongs to this delegate.
     * This method can only be invoked after {@link BackstackDelegate#onCreate(Bundle, Object, List)} has been called.
     *
     * @return the {@link Backstack} managed by this delegate.
     */
    @NonNull
    public Backstack getBackstack() {
        if(backstack == null) {
            throw new IllegalStateException("This method can only be called after calling `onCreate()`");
        }
        return backstack;
    }

    /**
     * The class which stores the {@link Backstack} for surviving configuration change.
     */
    public static class NonConfigurationInstance {
        private Backstack backstack;

        NonConfigurationInstance(Backstack backstack) {
            this.backstack = backstack;
        }

        Backstack getBackstack() {
            return backstack;
        }
    }
}
