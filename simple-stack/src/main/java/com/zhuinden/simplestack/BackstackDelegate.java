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

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.zhuinden.statebundle.StateBundle;

import java.util.ArrayList;

/**
 * A delegate class that manages the {@link Backstack}'s Activity lifecycle integration,
 * and provides view-state persistence for custom views that are associated with a key using {@link KeyContextWrapper}.
 *
 * This should be used in Activities to make sure that the {@link Backstack} survives both configuration changes and process death.
 */
public class BackstackDelegate {
    private BackstackManager backstackManager;

    private static final String UNINITIALIZED = "";
    private String persistenceTag = UNINITIALIZED;

    /**
     * Specifies a custom {@link KeyParceler}, allowing key parcellation strategies to be used for turning a key into Parcelable.
     *
     * If used, this method must be called before {@link BackstackDelegate#onCreate(Bundle, Object, ArrayList)}.
     *
     * @param keyParceler The custom {@link KeyParceler}.
     */
    public void setKeyParceler(KeyParceler keyParceler) {
        if(keyParceler == null) {
            throw new IllegalArgumentException("Specified custom key parceler should not be null!");
        }
        this.keyParceler = keyParceler;
    }

    /**
     * Specifies a custom {@link BackstackManager.StateClearStrategy}, allowing a custom way of retaining saved state.
     * The {@link DefaultStateClearStrategy} clears saved state for keys not found in the new state.
     *
     * If used, this method must be called before {@link BackstackDelegate#onCreate(Bundle, Object, ArrayList)}.
     *
     * @param stateClearStrategy The custom {@link BackstackManager.StateClearStrategy}.
     */
    public void setStateClearStrategy(BackstackManager.StateClearStrategy stateClearStrategy) {
        if(stateClearStrategy == null) {
            throw new IllegalArgumentException("Specified state clear strategy should not be null!");
        }
        this.stateClearStrategy = stateClearStrategy;
    }

    private static final String HISTORY = "simplestack.HISTORY";

    private StateChanger stateChanger;

    private KeyParceler keyParceler = new DefaultKeyParceler();
    private BackstackManager.StateClearStrategy stateClearStrategy = new DefaultStateClearStrategy();

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
        if(backstackManager != null && backstackManager.getBackstack() != null) {
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
     * If {@link StateChanger} is null, then the initialize {@link StateChange} is postponed until it is explicitly set.
     * The {@link StateChanger} must be set at some point before {@link BackstackDelegate#onPostResume()}.
     *
     * @param stateChanger The {@link StateChanger} to be set. Allowed to be null at initialization.
     */
    public BackstackDelegate(@Nullable StateChanger stateChanger) {
        this.stateChanger = stateChanger;
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
        NonConfigurationInstance nonConfig = (NonConfigurationInstance) nonConfigurationInstance;
        if(nonConfig != null) {
            backstackManager = nonConfig.getBackstackManager();
        }
        if(backstackManager == null) {
            backstackManager = new BackstackManager();
            backstackManager.setKeyParceler(keyParceler);
            backstackManager.setStateClearStrategy(stateClearStrategy);
            backstackManager.setup(initialKeys);
            if(savedInstanceState != null) {
                backstackManager.fromBundle(savedInstanceState.<StateBundle>getParcelable(getHistoryTag()));
            }
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
        return new NonConfigurationInstance(backstackManager);
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
        if(backstackManager == null) {
            throw new IllegalStateException("You can call this method only after `onCreate()`");
        }
        outState.putParcelable(getHistoryTag(), backstackManager.toBundle());
    }

    /**
     * The onPostResume() delegate for the Activity.
     * It re-attaches the {@link StateChanger} if it is not already set.
     */
    public void onPostResume() {
        if(stateChanger == null) {
            throw new IllegalStateException("State changer is still not set in `onPostResume`!");
        }
        if(backstackManager == null) {
            throw new IllegalStateException("You can call this method only after `onCreate()`");
        }
        backstackManager.reattachStateChanger();
    }

    /**
     * The onPause() delegate for the Activity.
     * It removes the {@link StateChanger} if it is set.
     */
    public void onPause() {
        if(backstackManager == null) {
            throw new IllegalStateException("You can call this method only after `onCreate()`");
        }
        backstackManager.detachStateChanger();
    }

    /**
     * The onDestroy() delegate for the Activity.
     * Forces any pending state change to execute with {@link Backstack#executePendingStateChange()}.
     */
    public void onDestroy() {
        getBackstack().executePendingStateChange();
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
        if(backstackManager == null) {
            throw new IllegalStateException("The backstack within the delegate must be initialized by `onCreate()`");
        }
        return backstackManager.getBackstack();
    }

    // ----- viewstate persistence

    /**
     * Provides the means to save the provided view's hierarchy state, and its optional StateBundle via {@link Bundleable} into a {@link SavedState}.
     *
     * @param view the view that belongs to a certain key
     */
    public void persistViewToState(@Nullable View view) {
        if(backstackManager == null) {
            throw new IllegalStateException("You can call this method only after `onCreate()`");
        }
        backstackManager.persistViewToState(view);
    }

    /**
     * Restores the state of the view based on the currently stored {@link SavedState}, according to the view's key.
     *
     * @param view the view that belongs to a certain key
     */
    public void restoreViewFromState(@NonNull View view) {
        if(backstackManager == null) {
            throw new IllegalStateException("You can call this method only after `onCreate()`");
        }
        backstackManager.restoreViewFromState(view);
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
        if(backstackManager == null) {
            throw new IllegalStateException("You can call this method only after `onCreate()`");
        }
        return backstackManager.getSavedState(key);
    }

    /**
     * The class which stores the {@link BackstackManager} for surviving configuration change.
     */
    public static class NonConfigurationInstance {
        private BackstackManager backstackManager;

        NonConfigurationInstance(BackstackManager backstackManager) {
            this.backstackManager = backstackManager;
        }

        BackstackManager getBackstackManager() {
            return backstackManager;
        }
    }
}
