package com.zhuinden.simplestack;

import android.content.Context;
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

    Object localKey; // NULL for root?

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

    NestedStack(Object localKey, Object parentKey, NestedStack parent, KeyParceler keyParceler) {
        this.localKey = localKey;
        this.parentKey = parentKey;
        this.parent = parent;
        this.keyParceler = keyParceler;
        this.backstack = new Backstack();
        this.backstackManager = new BackstackManager(keyParceler);
        this.backstackManager.setBackstack(backstack);
    }

    /**
     * Initializes the Nested Stack that belongs to a given key.
     * This method MUST be called before using the nested stack.
     *
     * The parameters provided are used as the initial keys for the first initialize state change.
     * If the stack is already initialized, then the initial keys are ignored.
     *
     * Intended to be used in onFinishInflate().
     *
     * @param initialKeys the keys used to initialize the stack if no state is restored. Cannot be empty.
     */
    public void initialize(@NonNull Object... initialKeys) {
        if(initialKeys == null || initialKeys.length == 0) {
            throw new IllegalArgumentException("To use nested stack, at least one initial key must be provided.");
        }
        this.initialize(Arrays.asList(initialKeys));
    }

    /**
     * Initializes the Nested Stack that belongs to a given key.
     * This method MUST be called before using the nested stack.
     *
     * The parameters provided are used as the initial keys for the first initialize state change.
     * If the stack is already initialized, then the initial keys are ignored.
     *
     * Intended to be used in onFinishInflate().
     *
     * @param initialKeys the keys used to initialize the stack if no state is restored. Cannot be empty.
     */
    public void initialize(@NonNull List<?> initialKeys) {
        if(initialKeys == null || initialKeys.isEmpty()) {
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
                backstackManager.setupServiceManager(localKey, parentServiceManager,
                        parentKey,
                        Collections.<ServiceFactory>emptyList(),
                        Collections.<String, Object>emptyMap());
            }
        }
    }

    /**
     * Sets the {@link StateChanger} to the {@link NestedStack}. Removes the previous one if it there was already one set.
     * This call begins an initialize {@link StateChange}.
     *
     * @param stateChanger The {@link StateChanger} to be set.
     */
    public void setStateChanger(StateChanger stateChanger) {
        if(parent != null) {
            checkInitialized();
            backstackManager.setStateChanger(stateChanger);
        }
    }

    /**
     * Re-attaches the {@link StateChanger} if it is not already set.
     */
    public void reattachStateChanger() {
        if(parent != null) {
            backstackManager.reattachStateChanger();
        }
    }

    /**
     * Removes the {@link StateChanger} if it is set.
     */
    public void detachStateChanger() {
        if(parent != null) {
            backstackManager.detachStateChanger();
        }
    }

    /**
     * Goes to the new key.
     * If the key is found, then it goes backward to the existing key.
     * If the key is not found, then it goes forward to the newly added key.
     * Uses the behavior of {@link Backstack}.
     *
     * @param newKey the target state.
     */
    public void goTo(Object newKey) {
        backstackManager.getBackstack().goTo(newKey);
    }

    /**
     * Goes back in the history.
     * If the current nested stack does not handle the back, then it is delegated to the parent and so on.
     *
     * @return true if a state change is pending or is handled with a state change, false if there is only one state left.
     */
    public boolean goBack() {
        if(backstackManager.getBackstack().goBack()) {
            return true;
        }
        if(parent != null) {
            return parent.goBack();
        }
        return false;
    }

    /**
     * Registers the {@link Backstack.CompletionListener}.
     *
     * @param completionListener The non-null completion listener to be registered.
     */
    public void addCompletionListener(Backstack.CompletionListener completionListener) {
        this.backstack.addCompletionListener(completionListener);
    }

    /**
     * Unregisters the {@link Backstack.CompletionListener}.
     *
     * @param completionListener The non-null completion listener to be unregistered.
     */
    public void removeCompletionListener(Backstack.CompletionListener completionListener) {
        this.backstack.removeCompletionListener(completionListener);
    }

    /**
     * Returns an unmodifiable copy of the current history.
     *
     * @return the unmodifiable copy of history.
     */
    public List<Object> getHistory() {
        return backstack.getHistory();
    }

    /**
     * Sets the provided state list as the new active history.
     *
     * @param newHistory the new active history.
     * @param direction  The direction of the state change: BACKWARD, FORWARD or REPLACE.
     */
    public void setHistory(List<Object> newHistory, @StateChange.StateChangeDirection int direction) {
        backstackManager.getBackstack().setHistory(newHistory, direction);
    }

    /**
     * If there is a state change in progress, then calling this method will force it to be completed immediately.
     * Any future calls to {@link StateChanger.Callback#stateChangeComplete()} for that given state change are ignored.
     */
    public void executePendingStateChange() {
        backstack.executePendingStateChange();
    }

    /**
     * Creates a {@link ManagedContextWrapper} using the provided key.
     *
     * If called before {@link NestedStack#initialize(Object...)}, then an exception is thrown.
     *
     * @param base the context used as base for the new context wrapper.
     * @param key  the key this context is associated with.
     * @return the context to use used with LayoutInflater.from().
     */
    @NonNull
    public Context createContext(Context base, Object key) {
        checkInitialized();
        return backstackManager.createContext(base, key);
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

    /**
     * Returns the parent nested stack of this nested stack.
     *
     * @return null if this is the root, otherwise the parent stack.
     */
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
        checkInitialized();
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


    // assertion
    private void checkInitialized() {
        if(backstackManager.getServiceManager() == null) {
            throw new IllegalStateException(
                    "You cannot set the state changer on an uninitalized nested stack, did you forget to call `initialize()`?");
        }
    }

    // state persistence

    /**
     * Used by {@link Bundleable} to save the state of {@link NestedStack} as a service.
     *
     * Users typically have no need for this.
     *
     * @return the state of the nested stack as a {@link StateBundle}.
     */
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

    /**
     * Used by {@link Bundleable} to restore the state of {@link NestedStack} as a service.
     *
     * Users typically have no need for this.
     */
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
