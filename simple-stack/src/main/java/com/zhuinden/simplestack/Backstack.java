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
import android.os.Parcelable;
import android.util.SparseArray;
import android.view.View;

import com.zhuinden.statebundle.StateBundle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * The backstack manages the navigation history internally, and wraps it with the ability of persisting view state.
 * <p>
 * Based on the configuration of {@link ScopedServices}, it also holds and saves/restores the state of services bound to scopes.
 * <p>
 * The backstack is initialized with {@link Backstack#setup(List)}, and {@link Backstack#setStateChanger(StateChanger)}.
 */
public class Backstack
        implements Bundleable {
    private final long threadId = Thread.currentThread().getId();

    /**
     * Retrieves the key from the provided Context. This relies on that within the base context chain, there is at least one {@link KeyContextWrapper}.
     *
     * The {@link KeyContextWrapper} is generally created via {@link StateChange#createContext(Context, Object)}.
     *
     * @param context the context
     * @param <K>     the type of the key
     * @return the key
     */
    @Nonnull
    public static <K> K getKey(@Nonnull Context context) {
        return KeyContextWrapper.getKey(context);
    }

    /**
     * Specifies the strategy to be used in order to delete {@link SavedState}s that are no longer needed after a {@link StateChange}, when there is no pending {@link StateChange} left.
     */
    public interface StateClearStrategy {
        /**
         * Allows a hook to clear the {@link SavedState} for obsolete keys.
         *
         * @param keyStateMap the map that contains the keys and their corresponding retained saved state.
         * @param stateChange the last state change
         */
        void clearStatesNotIn(@Nonnull Map<Object, SavedState> keyStateMap, @Nonnull StateChange stateChange);
    }

    private static final String HISTORY_TAG = "HISTORY";
    private static final String STATES_TAG = "STATES";
    private static final String SCOPES_TAG = "SCOPES";
    private static final String RETAINED_OBJECT_STATES_TAG = "RETAINED_OBJECT_STATES_TAG";

    static String getHistoryTag() {
        return HISTORY_TAG;
    }

    static String getStatesTag() {
        return STATES_TAG;
    }

    static String getScopesTag() {
        return SCOPES_TAG;
    }

    static String getRetainedObjectStatesTag() {
        return RETAINED_OBJECT_STATES_TAG;
    }

    private Object previousTopKeyWithAssociatedScope = null;

    private final StateChanger managedStateChanger = new StateChanger() {
        @Override
        public void handleStateChange(@Nonnull final StateChange stateChange, @Nonnull final Callback completionCallback) {
            scopeManager.buildScopes(stateChange.getNewKeys()); // always create scopes before a state change occurs
            stateChanger.handleStateChange(stateChange, completionCallback);
        }
    };

    // fix #220: this cannot be inside StateChanger.Callback, to ensure subsequent `stateChangeComplete()` call doesn't trigger recursive activation dispatch, only once
    private final CompletionListener managedStateChangerCompletionListener = new CompletionListener() {
        @Override
        public void stateChangeCompleted(@Nonnull StateChange stateChange) {
            if(!isStateChangePending()) {
                if(isStateChangerAttached) { // ensure enqueue behavior during activation dispatch, #215
                    core.removeStateChanger();
                }

                stateClearStrategy.clearStatesNotIn(keyStateMap, stateChange);

                History<Object> newState = stateChange.getNewKeys();

                // activation/deactivation
                Object newTopKeyWithAssociatedScope = null;
                for(int i = 0, size = newState.size(); i < size; i++) {
                    Object key = newState.fromTop(i);
                    if(key instanceof ScopeKey || key instanceof ScopeKey.Child) {
                        newTopKeyWithAssociatedScope = key;
                        break;
                    }
                }

                Set<String> scopesToDeactivate = new LinkedHashSet<>();
                Set<String> scopesToActivate = new LinkedHashSet<>();

                if(previousTopKeyWithAssociatedScope != null) {
                    if(previousTopKeyWithAssociatedScope instanceof ScopeKey) {
                        ScopeKey scopeKey = (ScopeKey) previousTopKeyWithAssociatedScope;
                        scopesToDeactivate.add(scopeKey.getScopeTag());
                    }

                    if(previousTopKeyWithAssociatedScope instanceof ScopeKey.Child) {
                        ScopeKey.Child child = (ScopeKey.Child) previousTopKeyWithAssociatedScope;
                        ScopeManager.checkParentScopes(child);
                        List<String> parentScopes = child.getParentScopes();

                        for(int i = parentScopes.size() - 1; i >= 0; i--) {
                            scopesToDeactivate.add(parentScopes.get(i));
                        }
                    }
                }

                if(newTopKeyWithAssociatedScope != null) {
                    if(newTopKeyWithAssociatedScope instanceof ScopeKey.Child) {
                        ScopeKey.Child child = (ScopeKey.Child) newTopKeyWithAssociatedScope;
                        ScopeManager.checkParentScopes(child);
                        scopesToActivate.addAll(child.getParentScopes());
                    }
                    if(newTopKeyWithAssociatedScope instanceof ScopeKey) {
                        ScopeKey scopeKey = (ScopeKey) newTopKeyWithAssociatedScope;
                        scopesToActivate.add(scopeKey.getScopeTag());
                    }
                }

                previousTopKeyWithAssociatedScope = newTopKeyWithAssociatedScope;

                // do not deactivate scopes that exist at this time
                Iterator<String> scopeToActivate = scopesToActivate.iterator();
                while(scopeToActivate.hasNext()) {
                    String activeScope = scopeToActivate.next();
                    if(scopesToDeactivate.contains(activeScope)) {
                        scopesToDeactivate.remove(activeScope); // do not deactivate an already active scope
                        scopeToActivate.remove(); // if the previous already contains it, then it is already activated.
                        // we should make sure we never activate the same service twice.
                    }
                }

                if(!scopesToActivate.isEmpty() || !scopesToDeactivate.isEmpty()) { // de-morgan is an ass, but the unit tests don't lie
                    scopeManager.dispatchActivation(scopesToDeactivate, scopesToActivate);
                }

                // scope eviction + scoped + re-order scope hierarchy
                scopeManager.cleanupScopesBy(newState);

                if(isStateChangerAttached) { // ensure enqueue behavior during activation dispatch, #215
                    core.setStateChanger(managedStateChanger, NavigationCore.REATTACH);
                }
            }
        }
    };

    private KeyFilter keyFilter = new DefaultKeyFilter();
    private KeyParceler keyParceler = new DefaultKeyParceler();
    private StateClearStrategy stateClearStrategy = new DefaultStateClearStrategy();

    /**
     * Specifies a custom {@link KeyFilter}, allowing keys to be filtered out if they should not be restored after process death.
     *
     * If used, this method must be called before {@link Backstack#setup(List)} .
     *
     * @param keyFilter The custom {@link KeyFilter}.
     */
    public void setKeyFilter(@Nonnull KeyFilter keyFilter) {
        if(core != null) {
            throw new IllegalStateException("Custom key filter should be set before calling `setup()`");
        }
        if(keyFilter == null) {
            throw new IllegalArgumentException("The key filter cannot be null!");
        }
        this.keyFilter = keyFilter;
    }

    /**
     * Specifies a custom {@link KeyParceler}, allowing key parcellation strategies to be used for turning a key into Parcelable.
     *
     * If used, this method must be called before {@link Backstack#setup(List)} .
     *
     * @param keyParceler The custom {@link KeyParceler}.
     */
    public void setKeyParceler(@Nonnull KeyParceler keyParceler) {
        if(core != null) {
            throw new IllegalStateException("Custom key parceler should be set before calling `setup()`");
        }
        if(keyParceler == null) {
            throw new IllegalArgumentException("The key parceler cannot be null!");
        }
        this.keyParceler = keyParceler;
    }

    /**
     * Specifies a custom {@link StateClearStrategy}, allowing a custom strategy for clearing the retained state of keys.
     * The {@link DefaultStateClearStrategy} clears the {@link SavedState} for keys that are not found in the new state.
     *
     * If used, this method must be called before {@link Backstack#setup(List)} .
     *
     * @param stateClearStrategy The custom {@link StateClearStrategy}.
     */
    public void setStateClearStrategy(@Nonnull StateClearStrategy stateClearStrategy) {
        if(core != null) {
            throw new IllegalStateException("Custom state clear strategy should be set before calling `setup()`");
        }
        if(stateClearStrategy == null) {
            throw new IllegalArgumentException("The state clear strategy cannot be null!");
        }
        this.stateClearStrategy = stateClearStrategy;
    }

    /**
     * Specifies a {@link ScopedServices} to allow handling the creation of scoped services.
     * <p>
     * Must be called before the initial state change.
     *
     * @param scopedServices the {@link ScopedServices}.
     */
    public void setScopedServices(@Nonnull ScopedServices scopedServices) {
        if(didRunInitialStateChange) {
            throw new IllegalStateException("Scope provider should be set before the initial state change!");
        }
        if(scopedServices == null) {
            throw new IllegalArgumentException("The scope provider cannot be null!");
        }
        this.scopeManager.setScopedServices(scopedServices);
    }

    /**
     * Specifies a {@link GlobalServices} that describes the services of the global scope.
     *
     * Must be called before the initial state change.
     *
     * Please note that setting a {@link GlobalServices.Factory} overrides this configuration option.
     *
     * @param globalServices the {@link GlobalServices}.
     */
    public void setGlobalServices(@Nonnull GlobalServices globalServices) {
        if(didRunInitialStateChange) {
            throw new IllegalStateException("Scope provider should be set before the initial state change!");
        }
        if(globalServices == null) {
            throw new IllegalArgumentException("The global services cannot be null!");
        }
        this.scopeManager.setGlobalServices(globalServices);
    }

    /**
     * Specifies a {@link GlobalServices.Factory} that describes the services of the global scope that are deferred until first creation.
     *
     * Must be called before the initial state change.
     *
     * Please note that a strong reference is kept to the factory, and the {@link Backstack} is typically preserved across configuration change.
     * It is recommended that it is NOT an anonymous inner class or normal inner class in an Activity,
     * because that could cause memory leaks.
     *
     * Instead, it should be a class, or a static inner class.
     *
     * @param globalServiceFactory the {@link GlobalServices.Factory}.
     */
    public void setGlobalServices(@Nonnull GlobalServices.Factory globalServiceFactory) {
        if(didRunInitialStateChange) {
            throw new IllegalStateException("Scope provider should be set before the initial state change!");
        }
        if(globalServiceFactory == null) {
            throw new IllegalArgumentException("The global service factory cannot be null!");
        }
        this.scopeManager.setGlobalServices(globalServiceFactory);
    }


    NavigationCore core;

    Map<Object, SavedState> keyStateMap = new HashMap<>();
    ScopeManager scopeManager = new ScopeManager();

    /* init */ {
        scopeManager.setBackstack(this);
    }

    StateChanger stateChanger;

    private boolean isStateChangerAttached = false; // tracked to ensure enqueue behavior during activation dispatch.

    private boolean didRunInitialStateChange = false;

    /**
     * Setup creates the {@link Backstack} with the specified initial keys.
     *
     * @param initialKeys the initial keys of the backstack
     */
    public void setup(@Nonnull List<?> initialKeys) {
        core = new NavigationCore(initialKeys);
        core.setBackstack(this);
        core.addCompletionListener(managedStateChangerCompletionListener); // fix #220
    }

    /**
     * Returns whether {@link Backstack#setup(List)} has been called.
     *
     * @return if setup has been called
     */
    public boolean isInitialized() {
        return core != null;
    }

    private void initializeBackstack(StateChanger stateChanger) {
        if(stateChanger != null) {
            if(!didRunInitialStateChange) {
                didRunInitialStateChange = true;
            }

            isStateChangerAttached = true;
            core.setStateChanger(managedStateChanger);
        }
    }

    /**
     * Sets the {@link StateChanger} for the given {@link Backstack}. This can only be called after {@link Backstack#setup(List)}.
     *
     * @param stateChanger the state changer
     */
    public void setStateChanger(@Nullable StateChanger stateChanger) {
        checkBackstack("You must call `setup()` before calling `setStateChanger()`.");
        if(core.hasStateChanger()) {
            core.removeStateChanger();
        }
        this.stateChanger = stateChanger;
        initializeBackstack(stateChanger);
    }

    /**
     * Detaches the {@link StateChanger} from the {@link Backstack}. This can only be called after {@link Backstack#setup(List)}.
     */
    public void detachStateChanger() {
        checkBackstack("You must call `setup()` before calling `detachStateChanger()`.");
        if(core.hasStateChanger()) {
            core.removeStateChanger();
            isStateChangerAttached = false;
        }
    }

    /**
     * Reattaches the {@link StateChanger} to the {@link Backstack}. This can only be called after {@link Backstack#setup(List)}.
     */
    public void reattachStateChanger() {
        checkBackstack("You must call `setup()` before calling `reattachStateChanger()`.");
        if(!core.hasStateChanger()) {
            isStateChangerAttached = true;
            core.setStateChanger(managedStateChanger, NavigationCore.REATTACH);
        }
    }

    /**
     * Typically called when Activity is finishing, and the remaining scopes should be destroyed for proper clean-up.
     *
     * Note that if you use {@link BackstackDelegate} or {@link com.zhuinden.simplestack.navigator.Navigator}, then there is no need to call this method manually.
     *
     * If the scopes are already finalized, then calling this method has no effect (until scopes are re-built by any future navigation events).
     */
    public void finalizeScopes() {
        if(scopeManager.isFinalized()) {
            return;
        }

        if(previousTopKeyWithAssociatedScope != null) {
            Set<String> scopesToDeactivate = new LinkedHashSet<>();

            if(previousTopKeyWithAssociatedScope instanceof ScopeKey.Child) {
                ScopeKey.Child child = (ScopeKey.Child) previousTopKeyWithAssociatedScope;
                ScopeManager.checkParentScopes(child);
                List<String> parentScopes = new ArrayList<>(child.getParentScopes());
                scopesToDeactivate.addAll(parentScopes);
            }
            if(previousTopKeyWithAssociatedScope instanceof ScopeKey) {
                ScopeKey scopeKey = (ScopeKey) previousTopKeyWithAssociatedScope;
                scopesToDeactivate.add(scopeKey.getScopeTag());
            }

            List<String> scopesToDeactivateList = new ArrayList<>(scopesToDeactivate);
            Collections.reverse(scopesToDeactivateList);
            scopeManager.dispatchActivation(new LinkedHashSet<>(scopesToDeactivateList), Collections.<String>emptySet());
        }

        scopeManager.deactivateGlobalScope();

        History<Object> history = getHistory();
        Set<String> scopeSet = new LinkedHashSet<>();
        for(int i = 0, size = history.size(); i < size; i++) {
            Object key = history.fromTop(i);
            if(key instanceof ScopeKey) {
                scopeSet.add(((ScopeKey) key).getScopeTag());
            }
            if(key instanceof ScopeKey.Child) {
                ScopeKey.Child child = (ScopeKey.Child) key;
                List<String> parentScopes = new ArrayList<>(child.getParentScopes());
                Collections.reverse(parentScopes);
                for(String parent : parentScopes) {
                    //noinspection RedundantCollectionOperation
                    if(scopeSet.contains(parent)) {
                        scopeSet.remove(parent); // needed to setup the proper order
                    }
                    scopeSet.add(parent);
                }
            }
        }

        List<String> scopes = new ArrayList<>(scopeSet);
        for(String scope : scopes) {
            scopeManager.destroyScope(scope);
        }
        scopeManager.finalizeScopes();

        previousTopKeyWithAssociatedScope = null; // this enables activation after finalization.
    }

    /**
     * Returns if a service is bound to the scope of the {@link ScopeKey} by the provided tag.
     *
     * @param scopeKey   the scope key
     * @param serviceTag the service tag
     * @return whether the service is bound in the given scope
     */
    public boolean hasService(@Nonnull ScopeKey scopeKey, @Nonnull String serviceTag) {
        return hasService(scopeKey.getScopeTag(), serviceTag);
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
    public <T> T getService(@Nonnull ScopeKey scopeKey, @Nonnull String serviceTag) {
        return getService(scopeKey.getScopeTag(), serviceTag);
    }

    /**
     * Returns if a service is bound to the scope specified by the provided tag for the provided service tag.
     *
     * @param scopeTag   the scope tag
     * @param serviceTag the service tag
     * @return whether the service is bound in the given scope
     */
    public boolean hasService(@Nonnull String scopeTag, @Nonnull String serviceTag) {
        return scopeManager.hasService(scopeTag, serviceTag);
    }

    /**
     * Returns the service bound to the scope specified by the provided tag for the provided service tag.
     *
     * @param scopeTag   the scope tag
     * @param serviceTag the service tag
     * @param <T>        the type of the service
     * @return the service
     */
    @Nonnull
    public <T> T getService(@Nonnull String scopeTag, @Nonnull String serviceTag) {
        return scopeManager.getService(scopeTag, serviceTag);
    }

    /**
     * Returns if a given scope exists.
     *
     * @param scopeTag the scope tag
     * @return whether the scope exists
     */
    public boolean hasScope(@Nonnull String scopeTag) {
        return scopeManager.hasScope(scopeTag);
    }

    /**
     * Attempts to look-up the service in all currently existing scopes, starting from the last added scope.
     * Returns whether the service exists in any scopes.
     *
     * @param serviceTag the tag of the service
     * @return whether the service exists in any active scopes
     */
    public boolean canFindService(@Nonnull String serviceTag) {
        return scopeManager.canFindService(serviceTag);
    }

    /**
     * Attempts to look-up the service in the provided scope and all its parents, starting from the provided scope.
     * Returns whether the service exists in any of these scopes.
     *
     * @param scopeTag   the tag of the scope to look up from
     * @param serviceTag the tag of the service
     * @return whether the service exists in any scopes from the current scope or its parents
     */
    public boolean canFindFromScope(@Nonnull String scopeTag, @Nonnull String serviceTag) {
        return scopeManager.canFindFromScope(scopeTag, serviceTag, ScopeLookupMode.ALL);
    }

    /**
     * Attempts to look-up the service in the provided scope and the specified type of parents, starting from the provided scope.
     * Returns whether the service exists in any of these scopes.
     *
     * @param scopeTag   the tag of the scope to look up from
     * @param serviceTag the tag of the service
     * @param lookupMode determine what type of parents are checked during the lookup
     * @return whether the service exists in any scopes from the current scope or its parents
     */
    public boolean canFindFromScope(@Nonnull String scopeTag, @Nonnull String serviceTag, @Nonnull ScopeLookupMode lookupMode) {
        return scopeManager.canFindFromScope(scopeTag, serviceTag, lookupMode);
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
    public <T> T lookupService(@Nonnull String serviceTag) {
        return scopeManager.lookupService(serviceTag);
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
    public List<String> findScopesForKey(@Nonnull Object key, @Nonnull ScopeLookupMode lookupMode) {
        Set<String> scopes = scopeManager.findScopesForKey(key, lookupMode);
        return Collections.unmodifiableList(new ArrayList<>(scopes));
    }

    /**
     * Attempts to look-up the service in the provided scope and its parents, starting from the provided scope.
     * If the service is not found, an exception is thrown.
     *
     * @param serviceTag the tag of the service
     * @param <T>        the type of the service
     * @return the service
     * @throws IllegalStateException if the service doesn't exist in any of the scopes
     */
    @Nonnull
    public <T> T lookupFromScope(String scopeTag, String serviceTag) {
        return scopeManager.lookupFromScope(scopeTag, serviceTag, ScopeLookupMode.ALL);
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
    public <T> T lookupFromScope(String scopeTag, String serviceTag, ScopeLookupMode lookupMode) {
        return scopeManager.lookupFromScope(scopeTag, serviceTag, lookupMode);
    }

    /**
     * Returns a {@link SavedState} instance for the given key.
     * If the state does not exist, then a new associated state is created.
     *
     * @param key The key to which the {@link SavedState} belongs.
     * @return the saved state that belongs to the given key.
     */
    @Nonnull
    public SavedState getSavedState(@Nonnull Object key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null!");
        }
        if (!keyStateMap.containsKey(key)) {
            keyStateMap.put(key, SavedState.builder().setKey(key).build());
        }
        return keyStateMap.get(key);
    }

    // ----- retained objects

    private final Map<String, Object> retainedObjects = new LinkedHashMap<>();
    private final StateBundle pendingRestoredRetainedObjectStates = new StateBundle();

    /**
     * Returns if a retained object is contained for a given tag.
     *
     * @param objectTag the object tag
     * @return if there is a retained object for the given tag
     */
    public boolean hasRetainedObject(@Nonnull String objectTag) {
        assertCorrectThread();

        return retainedObjects.containsKey(objectTag);
    }

    /**
     * Gets the retained object with the given tag. Throws if does not exist.
     *
     * @param objectTag the object tag
     * @param <T>       the type of the retained object
     * @return the retained object
     */
    @Nonnull
    public <T> T getRetainedObject(@Nonnull String objectTag) {
        assertCorrectThread();

        if (!retainedObjects.containsKey(objectTag)) {
            throw new IllegalArgumentException("Retained object with tag [" + objectTag + "] was not found.!");
        }

        //noinspection unchecked
        return (T) retainedObjects.get(objectTag);
    }

    /**
     * Add an object as a retained object. This will make it persist across configuration changes.
     * <p>
     * Retained objects that implement Bundleable will receive appropriate state registration callbacks when they're re-added.
     * <p>
     * Throws if an object is already found for that given object tag.
     *
     * @param objectTag      the object tag
     * @param retainedObject the retained object
     */
    public void addRetainedObject(@Nonnull String objectTag, @Nonnull Object retainedObject) {
        //noinspection ConstantConditions
        if (objectTag == null) {
            throw new NullPointerException("objectTag cannot be null!");
        }

        //noinspection ConstantConditions
        if (retainedObject == null) {
            throw new NullPointerException("retainedObject cannot be null!");
        }

        assertCorrectThread();

        if (retainedObjects.containsKey(objectTag)) {
            throw new IllegalArgumentException("A retained object is already added with the object tag [" + objectTag + "]");
        }

        if (pendingRestoredRetainedObjectStates.containsKey(objectTag)) {
            if (!(retainedObject instanceof Bundleable)) {
                throw new IllegalStateException("State restoration mismatch: expected [" + objectTag + "] to be restored, but was not actually Bundleable anymore.");
            }

            ((Bundleable) retainedObject).fromBundle(pendingRestoredRetainedObjectStates.getBundle(objectTag));
            pendingRestoredRetainedObjectStates.remove(objectTag);
        }

        retainedObjects.put(objectTag, retainedObject);
    }

    /**
     * Removes the retained object registered with the given object tag.
     * <p>
     * This also clears pending restored state for the given tag.
     *
     * @param objectTag the object tag
     */
    @Nullable
    public <T> T removeRetainedObject(@Nonnull String objectTag) {
        //noinspection ConstantConditions
        if (objectTag == null) {
            throw new NullPointerException("objectTag cannot be null!");
        }

        assertCorrectThread();

        pendingRestoredRetainedObjectStates.remove(objectTag);
        //noinspection unchecked
        return (T) retainedObjects.remove(objectTag);
    }

    // ----- viewstate persistence

    /**
     * Provides the means to save the provided view's hierarchy state
     * and its optional StateBundle via {@link Bundleable} into a {@link SavedState}.
     *
     * @param view the view that belongs to a certain key
     */
    public void persistViewToState(@Nullable View view) {
        assertCorrectThread();

        if (view != null) {
            Object key = KeyContextWrapper.getKey(view.getContext());
            if (key == null) {
                throw new IllegalArgumentException("The view [" + view + "] contained no key in its context hierarchy. The view or its parent hierarchy should be inflated by a layout inflater from `stateChange.createContext(baseContext, key)`, or a KeyContextWrapper.");
            }
            SparseArray<Parcelable> viewHierarchyState = new SparseArray<>();
            view.saveHierarchyState(viewHierarchyState);
            StateBundle bundle = null;
            if (view instanceof Bundleable) {
                bundle = ((Bundleable) view).toBundle();
            }
            SavedState previousSavedState = getSavedState(key);
            previousSavedState.setViewHierarchyState(viewHierarchyState);
            previousSavedState.setViewBundle(bundle);
        }
    }

    /**
     * Restores the state of the view based on the currently stored {@link SavedState}, according to the view's key.
     *
     * @param view the view that belongs to a certain key
     */
    public void restoreViewFromState(@Nonnull View view) {
        assertCorrectThread();

        if (view == null) {
            throw new IllegalArgumentException("You cannot restore state into null view!");
        }
        Object newKey = KeyContextWrapper.getKey(view.getContext());
        SavedState savedState = getSavedState(newKey);
        view.restoreHierarchyState(savedState.getViewHierarchyState());
        if (view instanceof Bundleable) {
            ((Bundleable) view).fromBundle(savedState.getViewBundle());
        }
    }

    /**
     * Allows adding a {@link Backstack.CompletionListener} to the internal {@link Backstack} that is called when the state change is completed, but before the state is cleared.
     *
     * Please note that a strong reference is kept to the listener, and the {@link Backstack} is typically preserved across configuration change.
     * It is recommended that it is NOT an anonymous inner class or normal inner class in an Activity,
     * because that could cause memory leaks.
     *
     * Instead, it should be a class, or a static inner class.
     *
     * @param stateChangeCompletionListener the state change completion listener.
     */
    public void addStateChangeCompletionListener(@Nonnull Backstack.CompletionListener stateChangeCompletionListener) {
        checkBackstack("A backstack must be set up before a state change completion listener is added to it.");
        if(stateChangeCompletionListener == null) {
            throw new IllegalArgumentException("StateChangeCompletionListener cannot be null!");
        }
        this.core.addCompletionListener(stateChangeCompletionListener);
    }

    /**
     * Removes the provided {@link Backstack.CompletionListener}.
     *
     * @param stateChangeCompletionListener the state change completion listener.
     */
    public void removeStateChangeCompletionListener(@Nonnull Backstack.CompletionListener stateChangeCompletionListener) {
        checkBackstack("A backstack must be set up before a state change completion listener is removed from it.");
        if(stateChangeCompletionListener == null) {
            throw new IllegalArgumentException("StateChangeCompletionListener cannot be null!");
        }
        this.core.removeCompletionListener(stateChangeCompletionListener);
    }

    /**
     * Removes all {@link Backstack.CompletionListener}s added to the {@link Backstack}.
     */
    @Deprecated
    public void removeAllStateChangeCompletionListeners() {
        checkBackstack("A backstack must be set up before state change completion listeners are removed from it.");
        this.core.removeCompletionListeners();
        this.core.addCompletionListener(managedStateChangerCompletionListener); // #221
    }

    /**
     * Restores the Backstack from a StateBundle.
     * This can only be called after {@link Backstack#setup(List)}.
     *
     * @param stateBundle the state bundle obtained via {@link Backstack#toBundle()}
     */
    @Override
    public void fromBundle(@Nullable StateBundle stateBundle) {
        checkBackstack("A backstack must be set up before it is restored!");

        assertCorrectThread();

        if (stateBundle != null) {
            List<Object> keys = new ArrayList<>();
            List<Parcelable> parcelledKeys = stateBundle.getParcelableArrayList(getHistoryTag());
            if (parcelledKeys != null) {
                for (Parcelable parcelledKey : parcelledKeys) {
                    keys.add(keyParceler.fromParcelable(parcelledKey));
                }
            }
            keys = keyFilter.filterHistory(new ArrayList<>(keys));
            if(keys == null) {
                keys = Collections.emptyList(); // lenient against null
            }
            if(!keys.isEmpty()) {
                core.setInitialParameters(keys);
            }
            List<ParcelledState> savedStates = stateBundle.getParcelableArrayList(getStatesTag());
            if(savedStates != null) {
                for(ParcelledState parcelledState : savedStates) {
                    Object key = keyParceler.fromParcelable(parcelledState.parcelableKey);
                    if(!keys.contains(key)) {
                        continue;
                    }
                    SavedState savedState = SavedState.builder().setKey(key)
                            .setViewHierarchyState(parcelledState.viewHierarchyState)
                            .setBundle(parcelledState.bundle)
                            .setViewBundle(parcelledState.viewBundle)
                            .build();
                    keyStateMap.put(savedState.getKey(), savedState);
                }
            }

            scopeManager.setRestoredStates(stateBundle.getBundle(SCOPES_TAG));

            StateBundle retainedStates = stateBundle.getBundle(RETAINED_OBJECT_STATES_TAG);
            if (retainedStates != null) {
                pendingRestoredRetainedObjectStates.putAll(retainedStates);

                for (Map.Entry<String, Object> retainedEntry : retainedObjects.entrySet()) {
                    String objectTag = retainedEntry.getKey();
                    Object retainedObject = retainedEntry.getValue();

                    if (pendingRestoredRetainedObjectStates.containsKey(objectTag)) {
                        if (!(retainedObject instanceof Bundleable)) {
                            throw new IllegalStateException("State restoration mismatch: expected [" + objectTag + "] to be restored, but was not actually Bundleable anymore.");
                        }
                        ((Bundleable) retainedObject).fromBundle(pendingRestoredRetainedObjectStates.getBundle(objectTag));
                        pendingRestoredRetainedObjectStates.remove(objectTag);
                    }
                }
            }
        }
    }

    private void assertCorrectThread() {
        if (Thread.currentThread().getId() != threadId) {
            throw new IllegalStateException(
                    "The backstack is not thread-safe, and must be manipulated only from the thread where it was originally created.");
        }
    }

    private void checkBackstack(String message) {
        if (core == null) {
            throw new IllegalStateException(message);
        }
    }

    /**
     * Persists the backstack history and view state into a StateBundle.
     *
     * @return the state bundle
     */
    @Nonnull
    @Override
    public StateBundle toBundle() {
        assertCorrectThread();

        StateBundle stateBundle = new StateBundle();
        ArrayList<Parcelable> history = new ArrayList<>();
        for (Object key : getHistory()) {
            history.add(keyParceler.toParcelable(key));
        }
        stateBundle.putParcelableArrayList(getHistoryTag(), history);

        ArrayList<ParcelledState> parcelledStates = new ArrayList<>();
        for (SavedState savedState : keyStateMap.values()) {
            ParcelledState parcelledState = new ParcelledState();
            parcelledState.parcelableKey = keyParceler.toParcelable(savedState.getKey());
            parcelledState.viewHierarchyState = savedState.getViewHierarchyState();
            parcelledState.bundle = savedState.getBundle();
            parcelledState.viewBundle = savedState.getViewBundle();
            parcelledStates.add(parcelledState);
        }
        stateBundle.putParcelableArrayList(getStatesTag(), parcelledStates);

        stateBundle.putParcelable(getScopesTag(), scopeManager.saveStates());

        StateBundle retainedObjectStates = new StateBundle();
        for (Map.Entry<String, Object> entry : retainedObjects.entrySet()) {
            final String objectTag = entry.getKey();
            final Object retainedObject = entry.getValue();

            if (retainedObject instanceof Bundleable) {
                StateBundle retainedBundle = ((Bundleable) retainedObject).toBundle();
                retainedObjectStates.putParcelable(objectTag, retainedBundle);
            }
        }

        stateBundle.putParcelable(getRetainedObjectStatesTag(), retainedObjectStates);

        return stateBundle;
    }

    /**
     * CompletionListener allows you to listen to when a {@link StateChange} has been completed.
     * They are registered to the {@link Backstack} with {@link Backstack#addCompletionListener(CompletionListener)}.
     * They are unregistered from the {@link Backstack} with {@link Backstack#removeCompletionListener(CompletionListener)} methods.
     */
    public interface CompletionListener {
        /**
         * Callback method that is called when a {@link StateChange} is complete.
         *
         * @param stateChange the state change that has been completed.
         */
        void stateChangeCompleted(@Nonnull StateChange stateChange);
    }

    // Navigation Core wrappers

    /**
     * Indicates whether a {@link StateChanger} is set.
     *
     * @return true if a {@link StateChanger} is set, false otherwise.
     */
    // @MainThread // removed android.support.annotation
    public boolean hasStateChanger() {
        checkBackstack("A backstack must be set up before checking state changer.");
        return core.hasStateChanger();
    }

    /**
     * Removes the {@link StateChanger}.
     */
    // @MainThread // removed android.support.annotation
    public void removeStateChanger() {
        checkBackstack("A backstack must be set up before removing state changer.");
        core.removeStateChanger();
    }

    /**
     * Goes to the new key.
     * If the key is found, then it goes backward to the existing key.
     * If the key is not found, then it goes forward to the newly added key.
     *
     * @param newKey the target state.
     */
    // @MainThread // removed android.support.annotation
    public void goTo(@Nonnull Object newKey) {
        checkBackstack("A backstack must be set up before navigation.");
        core.goTo(newKey);
    }

    /**
     * Replaces the current top with the provided key.
     * This means removing the current last element, and then adding the new element.
     *
     * @param newTop the new top key
     * @param direction The direction of the {@link StateChange}: {@link StateChange#BACKWARD}, {@link StateChange#FORWARD} or {@link StateChange#REPLACE}.
     */
    // @MainThread // removed android.support.annotation
    public void replaceTop(@Nonnull Object newTop, @StateChange.StateChangeDirection int direction) {
        checkBackstack("A backstack must be set up before navigation.");
        core.replaceTop(newTop, direction);
    }

    /**
     * Goes "up" to the provided element.
     * This means that if the provided element is found anywhere in the history, then the history goes to it.
     * If not found, then the current top is replaced with the provided element.
     *
     * Going up occurs in {@link StateChange#BACKWARD} direction.
     *
     * @param newKey the new key to go up to
     */
    // @MainThread // removed android.support.annotation
    public void goUp(@Nonnull Object newKey) {
        checkBackstack("A backstack must be set up before navigation.");
        core.goUp(newKey);
    }

    /**
     * Goes "up" to the provided element.
     * This means that if the provided element is found anywhere in the history, then the history goes to it (unless specified otherwise).
     * If not found, then the current top is replaced with the provided element.
     *
     * Going up occurs in {@link StateChange#BACKWARD} direction.
     *
     * @param newKey the new key to go up to
     * @param fallbackToBack specifies that if the key is found in the backstack, then the navigation defaults to going back to previous, instead of clearing all keys on top of it to the target.
     */
    // @MainThread // removed android.support.annotation
    public void goUp(@Nonnull Object newKey, boolean fallbackToBack) {
        checkBackstack("A backstack must be set up before navigation.");
        core.goUp(newKey, fallbackToBack);
    }

    /**
     * Moves the provided new key to the top of the backstack.
     * If the key already exists, then it is first removed, and added as the last element.
     * If it doesn't exist, then it is just added as the last element.
     *
     * The used direction is {@link StateChange#FORWARD}.
     *
     * @param newKey the new key
     */
    // @MainThread // removed android.support.annotation
    public void moveToTop(@Nonnull Object newKey) {
        checkBackstack("A backstack must be set up before navigation.");
        core.moveToTop(newKey);
    }

    /**
     * Moves the provided new key to the top of the backstack.
     * If the key already exists, then it is first removed, and added as the last element.
     * If it doesn't exist, then it is just added as the last element.
     *
     * @param newKey the new key
     * @param asReplace specifies if the direction is {@link StateChange#REPLACE} or {@link StateChange#FORWARD}.
     */
    // @MainThread // removed android.support.annotation
    public void moveToTop(@Nonnull Object newKey, boolean asReplace) {
        checkBackstack("A backstack must be set up before navigation.");
        core.moveToTop(newKey, asReplace);
    }

    /**
     * Jumps to the root of the backstack.
     *
     * This operation counts as a {@link StateChange#BACKWARD} navigation.
     */
    // @MainThread // removed android.support.annotation
    public void jumpToRoot() {
        checkBackstack("A backstack must be set up before navigation.");
        core.jumpToRoot();
    }

    /**
     * Jumps to the root of the backstack.
     *
     * @param direction The direction of the {@link StateChange}: {@link StateChange#BACKWARD}, {@link StateChange#FORWARD} or {@link StateChange#REPLACE}.
     */
    // @MainThread // removed android.support.annotation
    public void jumpToRoot(@StateChange.StateChangeDirection int direction) {
        checkBackstack("A backstack must be set up before navigation.");
        core.jumpToRoot(direction);
    }

    /**
     * Goes "up" once to the provided chain of parents.
     * If the chain of parents is found as previous elements, then it works as back navigation to that chain,, removing all other elements on top of it.
     * If the whole chain is not found, but at least one element of it is found, then the history is kept up to that point, then the chain is added, any duplicate element in the chain is added to the end as part of the chain.
     * If no element of the chain is found in the history, then the current top is removed, and the provided parent chain is added in its place.
     *
     * Going up the chain occurs in {@link StateChange#BACKWARD} direction.
     *
     * @param parentChain the chain of parents, from oldest to newest.
     */
    // @MainThread // removed android.support.annotation
    public void goUpChain(@Nonnull List<?> parentChain) {
        checkBackstack("A backstack must be set up before navigation.");
        core.goUpChain(parentChain);
    }

    /**
     * Exits the provided scope, removing all keys that exist that include the given scope.
     *
     * @throws IllegalArgumentException when the scope does not exist.
     * @throws IllegalStateException when the backstack is still empty.
     *
     * @param scopeTag the scope to exit from
     */
    public void exitScope(@Nonnull String scopeTag) {
        exitScope(scopeTag, StateChange.BACKWARD);
    }

    /**
     * Exits the provided scope, removing all keys that exist that include the given scope.
     *
     * If the scope is provided by the first key in the history, then it works as {@link Backstack#jumpToRoot(int direction)}.
     *
     * @throws IllegalArgumentException when the scope does not exist.
     * @throws IllegalStateException when the backstack is still empty.
     *
     * @param scopeTag the scope to exit from
     * @param direction the direction
     */
    public void exitScope(@Nonnull String scopeTag, @StateChange.StateChangeDirection int direction) {
        checkBackstack("A backstack must be set up before navigation.");

        assertCorrectThread();

        //noinspection ConstantConditions
        if (scopeTag == null) {
            throw new NullPointerException("scopeTag must not be null!");
        }

        History<Object> keys = getHistory();

        if (keys.isEmpty()) {
            throw new IllegalStateException("Cannot exit scope [" + scopeTag + "] within an empty backstack.");
        }

        if(!scopeManager.hasScope(scopeTag)) {
            throw new IllegalArgumentException("Cannot exit scope [" + scopeTag + "] as it does not exist.");
        }

        Object candidateKey = keys.get(0);

        for(Object key: keys) {
            if(scopeManager.canFindScope(key, scopeTag, ScopeLookupMode.EXPLICIT)) {
                break;
            }

            candidateKey = key;
        }

        core.setHistory(History.builderFrom(keys).removeUntil(candidateKey).build(), direction);
    }

    /**
     * Exits the provided scope, removing all keys that exist that include the given scope.
     * During the exit, the provided new key will be appended to the history if it's not yet added, otherwise, it'll go to it.
     *
     * @throws IllegalArgumentException when the scope does not exist.
     * @throws IllegalStateException when the backstack is still empty.
     *
     * @param scopeTag the scope to exit from
     * @param targetKey the key to exit to, inclusive if found, appended if not found
     * @param direction the direction
     */
    public void exitScopeTo(@Nonnull String scopeTag, @Nonnull Object targetKey, @StateChange.StateChangeDirection int direction) {
        checkBackstack("A backstack must be set up before navigation.");

        //noinspection ConstantConditions
        if(scopeTag == null) {
            throw new NullPointerException("scopeTag must not be null!");
        }

        //noinspection ConstantConditions
        if(targetKey == null) {
            throw new NullPointerException("newKey must not be null!");
        }

        History<Object> keys = getHistory();

        if(keys.isEmpty()) {
            throw new IllegalStateException("Cannot exit scope [" + scopeTag + "] within an empty backstack.");
        }

        if(!scopeManager.hasScope(scopeTag)) {
            throw new IllegalArgumentException("Cannot exit scope [" + scopeTag + "] as it does not exist.");
        }

        Object candidateKey = keys.get(0);

        for(Object key: keys) {
            if(scopeManager.canFindScope(key, scopeTag, ScopeLookupMode.EXPLICIT)) {
                break;
            }

            candidateKey = key;
        }

        History.Builder builder = History.builderFrom(keys).removeUntil(candidateKey);

        if(scopeManager.canFindScope(builder.get(0), scopeTag, ScopeLookupMode.EXPLICIT)) { // root had the scope
            builder.removeAt(0);
        }

        if(!builder.contains(targetKey)) {
            builder.add(targetKey);
        } else {
            builder.removeUntil(targetKey);
        }

        core.setHistory(builder.build(), direction);
    }

    /**
     * Goes "up" once to the provided chain of parents.
     * If the chain of parents is found as previous elements, then it works as back navigation to that chain, removing all other elements on top of it (unless specified otherwise).
     * If the whole chain is not found, but at least one element of it is found, then the history is kept up to that point, then the chain is added, any duplicate element in the chain is added to the end as part of the chain.
     * If no element of the chain is found in the history, then the current top is removed, and the provided parent chain is added in its place.
     *
     * Going up the chain occurs in {@link StateChange#BACKWARD} direction.
     *
     * @param parentChain the chain of parents, from oldest to newest.
     * @param fallbackToBack determines that if the chain is fully found in the backstack, then the navigation will default to regular "back" to the previous element, instead of clearing the top elements.
     */
    // @MainThread // removed android.support.annotation
    public void goUpChain(@Nonnull List<?> parentChain, boolean fallbackToBack) {
        checkBackstack("A backstack must be set up before navigation.");
        core.goUpChain(parentChain, fallbackToBack);
    }

    /**
     * Goes back in the history.
     * If the key is found, then it goes backward to the existing key.
     * If the key is not found, then it goes forward to the newly added key.
     *
     * Before navigating back in the history, it attempts to dispatch {@link ScopedServices.HandlesBack#onBackEvent()} to scoped services in the active scope chain.
     *
     * @return true if the back event was handled, false if there is only one key left.
     */
    // @MainThread // removed android.support.annotation
    public boolean goBack() {
        checkBackstack("A backstack must be set up before navigation.");

        if(isStateChangePending()) {
            return true;
        }

        Object topKey = getHistory().top();

        if(topKey == null) {
            return false;
        }

        boolean handled = scopeManager.dispatchBack(topKey);

        if(handled) {
            return true;
        }

        return core.goBack();
    }

    /**
     * Immediately clears the backstack, it is NOT enqueued as a state change.
     *
     * If there are pending state changes, then it throws an exception.
     *
     * You should generally not need to use this method.
     */
    // @MainThread // removed android.support.annotation
    public void forceClear() {
        checkBackstack("A backstack must be set up before navigation.");

        finalizeScopes();

        core.forceClear();
    }

    /**
     * Sets the provided state list as the new active history.
     *
     * @param newHistory the new active history.
     * @param direction  The direction of the {@link StateChange}: {@link StateChange#BACKWARD}, {@link StateChange#FORWARD} or {@link StateChange#REPLACE}.
     */
    // @MainThread // removed android.support.annotation
    public void setHistory(@Nonnull List<?> newHistory, @StateChange.StateChangeDirection int direction) {
        checkBackstack("A backstack must be set up before navigation.");

        core.setHistory(newHistory, direction);
    }

    /**
     * Returns the root (first) element of this history, or null if the history is empty.
     *
     * @throws IllegalStateException if the history doesn't contain any elements yet.
     *
     * @param <K> the type of the key
     * @return the root (first) key
     */
    @Nonnull
    public <K> K root() {
        checkBackstack("A backstack must be set up before getting keys from it.");
        return core.root();
    }

    /**
     * Returns the last element in the list, or null if the history is empty.
     *
     * @throws IllegalStateException if the history doesn't contain any elements yet.
     *
     * @param <K> the type of the key
     * @return the top key
     */
    @Nonnull
    public <K> K top() {
        checkBackstack("A backstack must be set up before getting keys from it.");
        return core.top();
    }

    /**
     * Returns the element indexed from the top.
     *
     * Offset value `0` behaves the same as {@link History#top()}, while `1` returns the one before it.
     * Negative indices are wrapped around, for example `-1` is the first element of the stack, `-2` the second, and so on.
     *
     * Accepted values are in range of [-size, size).
     *
     * @throws IllegalStateException if the history doesn't contain any elements yet.
     * @throws IllegalArgumentException if the provided offset is outside the range of [-size, size).
     *
     * @param offset the offset from the top
     * @param <K> the type of the key
     * @return the key from the top with offset
     */
    @Nonnull
    public <K> K fromTop(int offset) {
        checkBackstack("A backstack must be set up before getting keys from it.");
        return core.fromTop(offset);
    }

    /**
     * Returns an unmodifiable copy of the current history.
     *
     * @return the unmodifiable copy of history.
     */
    @Nonnull
    public <K> History<K> getHistory() {
        checkBackstack("A backstack must be set up before getting keys from it.");
        return core.getHistory();
    }

    /**
     * Returns an unmodifiable list that contains the keys this backstack is initialized with.
     *
     * @return the list of keys used at first initialization
     */
    @Nonnull
    public <K> History<K> getInitialKeys() {
        checkBackstack("A backstack must be set up before getting keys from it.");
        return core.getInitialKeys();
    }

    /**
     * Returns whether there is at least one queued {@link StateChange}.
     *
     * @return true if there is at least one enqueued {@link StateChange}.
     */
    public boolean isStateChangePending() {
        checkBackstack("A backstack must be set up before navigation.");
        return core.isStateChangePending();
    }

    /**
     * Registers the {@link Backstack.CompletionListener}.
     *
     * @param completionListener The non-null completion listener to be registered.
     *
     * @deprecated use {@link Backstack#addStateChangeCompletionListener(CompletionListener)}.
     */
    @Deprecated
    public void addCompletionListener(@Nonnull Backstack.CompletionListener completionListener) {
        addStateChangeCompletionListener(completionListener);
    }

    /**
     * Unregisters the {@link Backstack.CompletionListener}.
     *
     * @param completionListener The non-null completion listener to be unregistered.
     *
     * @deprecated use {@link Backstack#removeStateChangeCompletionListener(CompletionListener)}.
     */
    @Deprecated
    public void removeCompletionListener(@Nonnull Backstack.CompletionListener completionListener) {
        removeStateChangeCompletionListener(completionListener);
    }

    /**
     * Unregisters all {@link Backstack.CompletionListener}s.
     *
     * @deprecated use {@link Backstack#removeStateChangeCompletionListener(CompletionListener)}.
     */
    @Deprecated
    public void removeCompletionListeners() {
        //noinspection deprecation
        removeAllStateChangeCompletionListeners();
    }

    /**
     * If there is a state change in progress, then calling this method will force it to be completed immediately.
     * Any future calls to {@link StateChanger.Callback#stateChangeComplete()} for that given state change are ignored.
     */
    // @MainThread // removed android.support.annotation
    public void executePendingStateChange() {
        checkBackstack("A backstack must be set up before navigation.");
        core.executePendingStateChange();
    }
}