/*
 * Copyright 2018 Gabor Varadi
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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.zhuinden.statebundle.StateBundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class ScopeManager {
    static final String GLOBAL_SCOPE_TAG = "__SIMPLE_STACK_INTERNAL_GLOBAL_SCOPE__";

    private static final GlobalServices EMPTY_GLOBAL_SERVICES = GlobalServices.builder().build();

    private final IdentityHashMap<Object, Set<String>> scopeEnteredServices = new IdentityHashMap<>();
    private final IdentityHashMap<Object, Set<String>> scopeActivatedServices = new IdentityHashMap<>();

    private final IdentityHashMap<Object, Integer> untrackEventInvocationTracker = new IdentityHashMap<>(); // call unregister/inactivated only once!

    private boolean isGlobalScopePendingActivation = true;

    void activateGlobalScope() {
        notifyScopeActivation(GLOBAL_SCOPE_TAG, globalServices.getServices());
    }

    void deactivateGlobalScope() {
        notifyScopeDeactivation(GLOBAL_SCOPE_TAG, globalServices.getServices());
    }

    static class AssertingScopedServices
            implements ScopedServices {

        @Override
        public void bindServices(@NonNull ServiceBinder serviceBinder) {
            throw new IllegalStateException(
                    "No scoped services are defined. To create scoped services, an instance of ScopedServices must be provided to configure the services that are available in a given scope.");
        }
    }

    private GlobalServices globalServices = EMPTY_GLOBAL_SERVICES;
    private ScopedServices scopedServices = new AssertingScopedServices();

    ScopeManager() {
    }

    private Backstack backstack;

    void setBackstack(Backstack backstack) {
        this.backstack = backstack;
    }

    Backstack getBackstack() {
        return backstack;
    }

    private final Map<String, Map<String, Object>> scopes = new LinkedHashMap<>();

    private final StateBundle rootBundle = new StateBundle();

    private List<String> getActiveScopesReverse() {
        List<String> activeScopes = new ArrayList<>(scopes.keySet());
        Collections.reverse(activeScopes);
        return activeScopes;
    }

    void setScopedServices(ScopedServices scopedServices) {
        this.scopedServices = scopedServices;
    }

    void setGlobalServices(GlobalServices globalServices) {
        this.globalServices = globalServices;
    }


    private void buildGlobalScope() {
        if(!scopes.containsKey(GLOBAL_SCOPE_TAG)) {
            Map<String, Object> scope = globalServices.getServices();
            scopes.put(GLOBAL_SCOPE_TAG, scope);

            restoreAndNotifyServices(GLOBAL_SCOPE_TAG, scope);
        }
    }

    private void buildScope(Object key, String scopeTag) {
        //noinspection ConstantConditions
        if(scopeTag == null) {
            throw new IllegalArgumentException("Scope tag provided by scope key cannot be null!");
        }
        if(!scopes.containsKey(scopeTag)) {
            Map<String, Object> scope = new LinkedHashMap<>();
            scopes.put(scopeTag, scope);

            scopedServices.bindServices(new ServiceBinder(this, key, scopeTag, scope));

            restoreAndNotifyServices(scopeTag, scope);
        }
    }

    private void restoreAndNotifyServices(String scopeTag, Map<String, Object> scope) {
        for(Map.Entry<String, Object> serviceEntry : scope.entrySet()) {
            String serviceTag = serviceEntry.getKey();
            Object service = serviceEntry.getValue();

            if(isServiceNotRegistered(service)) {
                if(rootBundle.containsKey(scopeTag)) {
                    if(service instanceof Bundleable) {
                        StateBundle scopeBundle = rootBundle.getBundle(scopeTag);
                        if(scopeBundle != null && scopeBundle.containsKey(serviceTag)) {
                            ((Bundleable) service).fromBundle(scopeBundle.getBundle(serviceTag));
                        }
                    }
                }

                if(service instanceof ScopedServices.Registered) {
                    ((ScopedServices.Registered) service).onServiceRegistered();
                }
            }

            if(isServiceNotTrackedInScope(scopeEnteredServices, service, scopeTag)) {
                trackServiceInScope(scopeEnteredServices, service, scopeTag);
            }
        }
    }

    private boolean isServiceNotRegistered(Object service) {
        return !scopeEnteredServices.containsKey(service) || scopeEnteredServices.get(service).isEmpty();
    }

    private boolean isServiceNotActivated(Object service) {
        return !scopeActivatedServices.containsKey(service) || scopeActivatedServices.get(service).isEmpty();
    }

    private boolean isServiceNotTrackedInScope(Map<Object, Set<String>> scopeEventTracker, Object service, String scopeTag) {
        return !scopeEventTracker.containsKey(service) || !scopeEventTracker.get(service).contains(scopeTag);
    }

    private void trackServiceInScope(Map<Object, Set<String>> scopeEventTracker, Object service, String scopeTag) {
        Set<String> trackedScopes = scopeEventTracker.get(service);
        if(trackedScopes == null) {
            trackedScopes = new LinkedHashSet<>();
            scopeEventTracker.put(service, trackedScopes);
        }
        trackedScopes.add(scopeTag);
    }

    private void untrackServiceInScope(Map<Object, Set<String>> scopeEventTracker, Object service, String scopeTag) {
        Set<String> trackedScopes = scopeEventTracker.get(service);
        trackedScopes.remove(scopeTag);
        if(trackedScopes.isEmpty()) {
            scopeEventTracker.remove(service);
        }
    }

    private List<Object> latestKeys = null;

    private boolean isFinalized = false;

    boolean isFinalized() {
        return isFinalized;
    }

    void finalizeScopes() {
        this.isFinalized = true;

        // this logic is actually mostly inside Backstack for some reason
        destroyScope(GLOBAL_SCOPE_TAG);

        this.latestKeys = null; // don't use `emptyList()` so that Globals can be re-initialized.
    }

    void buildScopes(List<Object> newState) {
        if(isFinalized) {
            this.isFinalized = false; // reset this for future travellers, I guess.
            this.isGlobalScopePendingActivation = true; // if we allow scopes to be rebuilt once finalized, we need to enable activation of globals.
        }

        if(this.latestKeys == null) { // this seems to be the best way to track that we can safely register and initialize the global scope.
            buildGlobalScope();
        }
        this.latestKeys = newState;

        for(Object key : newState) {
            if(key instanceof ScopeKey.Child) {
                ScopeKey.Child child = (ScopeKey.Child) key;
                checkParentScopes(child);
                for(String parent : child.getParentScopes()) {
                    buildScope(key, parent);
                }
            }
            if(key instanceof ScopeKey) {
                ScopeKey scopeKey = (ScopeKey) key;
                String scopeTag = scopeKey.getScopeTag();
                buildScope(key, scopeTag);
            }
        }
    }

    void clearScopesNotIn(List<Object> newState) {
        Set<String> currentScopes = new LinkedHashSet<>();
        currentScopes.add(GLOBAL_SCOPE_TAG); // prevent global scope from being destroyed

        for(Object key : newState) {
            if(key instanceof ScopeKey.Child) {
                ScopeKey.Child child = (ScopeKey.Child) key;
                checkParentScopes(child);
                currentScopes.addAll(child.getParentScopes());
            }
            if(key instanceof ScopeKey) {
                ScopeKey scopeKey = (ScopeKey) key;
                currentScopes.add(scopeKey.getScopeTag());
            }
        }

        List<String> scopeSet = getActiveScopesReverse();
        for(String activeScope : scopeSet) {
            if(!currentScopes.contains(activeScope)) {
                destroyScope(activeScope);
            }
        }
    }

    void destroyScope(String scopeTag) {
        if(scopes.containsKey(scopeTag)) {
            Map<String, Object> serviceMap = scopes.remove(scopeTag);
            destroyServicesAndRemoveState(scopeTag, serviceMap);
        }
    }

    private void destroyServicesAndRemoveState(String scopeTag, Map<String, Object> serviceMap) {
        List<Object> services = new ArrayList<>(serviceMap.values());
        Collections.reverse(services);

        untrackEventInvocationTracker.clear();

        for(Object service : services) {
            if(!isServiceNotTrackedInScope(scopeEnteredServices, service, scopeTag)) {
                untrackServiceInScope(scopeEnteredServices, service, scopeTag);
            }

            if(isServiceNotRegistered(service)) {
                if(service instanceof ScopedServices.Registered && !untrackEventInvocationTracker.containsKey(service)) {
                    untrackEventInvocationTracker.put(service, 1);
                    ((ScopedServices.Registered) service).onServiceUnregistered();
                }
            }
        }

        rootBundle.remove(scopeTag);
    }

    void dispatchActivation(@NonNull Set<String> scopesToDeactivate, @NonNull Set<String> scopesToActivate) {
        if(isGlobalScopePendingActivation) {
            isGlobalScopePendingActivation = false;
            activateGlobalScope();
        }

        for(String newScopeTag : scopesToActivate) {
            if(!scopes.containsKey(newScopeTag)) {
                throw new AssertionError(
                        "The new scope should exist, but it doesn't! This shouldn't happen. If you see this error, this functionality is broken.");
            }

            Map<String, Object> newServiceMap = scopes.get(newScopeTag);
            notifyScopeActivation(newScopeTag, newServiceMap);
        }

        for(String previousScopeTag : scopesToDeactivate) {
            if(!scopes.containsKey(previousScopeTag)) {
                throw new AssertionError(
                        "The previous scope should exist, but it doesn't! This shouldn't happen. If you see this error, this functionality is broken.");
            }

            Map<String, Object> previousServiceMap = scopes.get(previousScopeTag);
            notifyScopeDeactivation(previousScopeTag, previousServiceMap);
        }
    }

    private void notifyScopeActivation(String newScopeTag, Map<String, Object> newServiceMap) {
        for(Object service : newServiceMap.values()) {
            if(isServiceNotActivated(service) && service instanceof ScopedServices.Activated) {
                ((ScopedServices.Activated) service).onServiceActive();
            }

            if(isServiceNotTrackedInScope(scopeActivatedServices, service, newScopeTag)) {
                trackServiceInScope(scopeActivatedServices, service, newScopeTag);
            }
        }
    }

    private void notifyScopeDeactivation(String previousScopeTag, Map<String, Object> previousServiceMap) {
        List<Object> previousServices = new ArrayList<>(previousServiceMap.values());
        Collections.reverse(previousServices);

        untrackEventInvocationTracker.clear();

        for(Object service : previousServices) {
            if(!isServiceNotTrackedInScope(scopeActivatedServices, service, previousScopeTag)) {
                untrackServiceInScope(scopeActivatedServices, service, previousScopeTag);
            }

            if(isServiceNotActivated(service)
                    && service instanceof ScopedServices.Activated
                    && !untrackEventInvocationTracker.containsKey(service)
            ) {
                untrackEventInvocationTracker.put(service, 1);
                ((ScopedServices.Activated) service).onServiceInactive();
            }
        }
    }

    StateBundle saveStates() {
        StateBundle rootBundle = new StateBundle();
        for(Map.Entry<String, Map<String, Object>> scopeSet : scopes.entrySet()) {
            String scopeKey = scopeSet.getKey();
            Map<String, Object> services = scopeSet.getValue();

            StateBundle scopeBundle = new StateBundle();
            for(Map.Entry<String, Object> serviceEntry : services.entrySet()) {
                String serviceTag = serviceEntry.getKey();
                Object service = serviceEntry.getValue();
                if(service instanceof Bundleable) {
                    scopeBundle.putBundle(serviceTag, ((Bundleable) service).toBundle());
                }
            }
            rootBundle.putBundle(scopeKey, scopeBundle);
        }
        return rootBundle;
    }

    void setRestoredStates(StateBundle rootBundle) {
        if(rootBundle != null) {
            this.rootBundle.putAll(rootBundle);
        }
    }

    boolean hasService(@NonNull String scopeTag, @NonNull String serviceTag) {
        checkScopeTag(scopeTag);
        checkServiceTag(serviceTag);
        if(!scopes.containsKey(scopeTag)) {
            return false;
        }

        Map<String, Object> services = scopes.get(scopeTag);
        return services.containsKey(serviceTag);
    }

    @NonNull
    <T> T getService(@NonNull String scopeTag, @NonNull String serviceTag) {
        checkScopeTag(scopeTag);
        checkServiceTag(serviceTag);

        if(!scopes.containsKey(scopeTag)) {
            throw new IllegalArgumentException("The specified scope with tag [" + scopeTag + "] does not exist!");
        }

        Map<String, Object> services = scopes.get(scopeTag);
        if(!services.containsKey(serviceTag)) {
            throw new IllegalArgumentException("The specified service with tag [" + serviceTag + "] does not exist in scope [" + scopeTag + "]! Did you accidentally try to use the same scope tag with different services?");
        }
        //noinspection unchecked
        return (T) services.get(serviceTag);
    }

    boolean hasScope(@NonNull String scopeTag) {
        checkScopeTag(scopeTag);
        return scopes.containsKey(scopeTag);
    }

    @NonNull
    Set<String> findScopesForKey(@NonNull Object key, @NonNull ScopeLookupMode lookupMode) {
        checkKey(key);
        checkScopeLookupMode(lookupMode);

        return lookupMode.executeFindScopesForKey(this, key);
    }

    @NonNull
    private LinkedHashSet<String> _findScopesForKey(@NonNull Object targetKey, boolean explicitOnly) {
        LinkedHashSet<String> activeScopes = new LinkedHashSet<>();

        boolean isKeyFound = false;
        for(int i = latestKeys.size() - 1; i >= 0; i--) {
            Object key = latestKeys.get(i);
            if(targetKey.equals(key)) {
                isKeyFound = true;
            }
            if(!isKeyFound) {
                continue;
            }
            if(key instanceof ScopeKey) {
                ScopeKey scopeKey = (ScopeKey) key;
                String currentScope = scopeKey.getScopeTag();
                activeScopes.add(currentScope);
            }
            if(key instanceof ScopeKey.Child) {
                ScopeKey.Child child = (ScopeKey.Child) key;
                checkParentScopes(child);
                List<String> parentScopes = child.getParentScopes();
                for(int j = parentScopes.size() - 1; j >= 0; j--) {
                    String currentScope = parentScopes.get(j);
                    activeScopes.add(currentScope);
                }
            }

            if(explicitOnly) {
                // force explicit only in this mode.
                break;
            }
        }

        return activeScopes;
    }

    @NonNull
    private LinkedHashSet<String> _findScopesForScopeTag(@Nullable String scopeTag, boolean explicitOnly) {
        LinkedHashSet<String> activeScopes = new LinkedHashSet<>();

        if(this.latestKeys == null) {
            return activeScopes;
        }

        List<Object> latestState = this.latestKeys;

        boolean isScopeFound = scopeTag == null;
        for(int i = latestState.size() - 1; i >= 0; i--) {
            Object key = latestState.get(i);
            if(key instanceof ScopeKey) {
                ScopeKey scopeKey = (ScopeKey) key;
                String currentScope = scopeKey.getScopeTag();
                if(!isScopeFound && currentScope.equals(scopeTag)) {
                    isScopeFound = true;
                }
                if(isScopeFound) {
                    activeScopes.add(currentScope);
                }
            }
            if(key instanceof ScopeKey.Child) {
                ScopeKey.Child child = (ScopeKey.Child) key;
                checkParentScopes(child);
                List<String> parentScopes = child.getParentScopes();
                for(int j = parentScopes.size() - 1; j >= 0; j--) {
                    String currentScope = parentScopes.get(j);
                    if(!isScopeFound && currentScope.equals(scopeTag)) {
                        isScopeFound = true;
                    }
                    if(isScopeFound) {
                        activeScopes.add(currentScope);
                    }
                }
            }

            if(explicitOnly && isScopeFound) { // force explicit only in this mode.
                break;
            }
        }

        return activeScopes;
    }

    @NonNull
    Set<String> findScopesForKeyAll(Object targetKey) {
        if(this.latestKeys == null) {
            return Collections.emptySet();
        }

        LinkedHashSet<String> activeScopes = _findScopesForKey(targetKey, false);

        if(!isFinalized && !globalServices.getServices().isEmpty()) {
            activeScopes.add(GLOBAL_SCOPE_TAG);
        }

        return Collections.unmodifiableSet(activeScopes);
    }

    @NonNull
    Set<String> findScopesForKeyExplicit(Object targetKey) {
        if(this.latestKeys == null) {
            return Collections.emptySet();
        }

        LinkedHashSet<String> activeScopes = _findScopesForKey(targetKey, true);

        if(!isFinalized && !globalServices.getServices().isEmpty()) {
            activeScopes.add(GLOBAL_SCOPE_TAG);
        }

        return Collections.unmodifiableSet(activeScopes);
    }

    boolean canFindFromScope(String scopeTag, String serviceTag, ScopeLookupMode lookupMode) {
        checkServiceTag(serviceTag);
        checkScopeTag(scopeTag);
        checkScopeLookupMode(lookupMode);

        return lookupMode.executeCanFindFromService(this, scopeTag, serviceTag);
    }

    boolean canFindFromScopeExplicit(String scopeTag, String serviceTag) {
        if(this.latestKeys == null) {
            return false;
        }

        Set<String> activeScopes = _findScopesForScopeTag(scopeTag, true);

        for(String scope : activeScopes) {
            if(hasService(scope, serviceTag)) {
                return true;
            }
        }

        //noinspection RedundantIfStatement
        if(!isFinalized && globalServices.hasService(serviceTag)) {
            return true;
        }

        return false;
    }

    boolean canFindFromScopeAll(String scopeTag, String serviceTag) {
        if(this.latestKeys == null) {
            return false;
        }

        LinkedHashSet<String> activeScopes = _findScopesForScopeTag(scopeTag, false);

        for(String scope : activeScopes) {
            if(hasService(scope, serviceTag)) {
                return true;
            }
        }

        //noinspection RedundantIfStatement
        if(!isFinalized && globalServices.hasService(serviceTag)) {
            return true;
        }

        return false;
    }

    <T> T lookupFromScope(String scopeTag, String serviceTag, ScopeLookupMode lookupMode) {
        checkScopeTag(scopeTag);
        checkServiceTag(serviceTag);
        checkScopeLookupMode(lookupMode);

        return lookupMode.executeLookupFromScope(this, scopeTag, serviceTag);
    }

    <T> T lookupFromScopeExplicit(String scopeTag, String serviceTag) {
        verifyStackIsInitialized();

        LinkedHashSet<String> activeScopes = _findScopesForScopeTag(scopeTag, true);

        for(String scope : activeScopes) {
            if(hasService(scope, serviceTag)) {
                return getService(scope, serviceTag);
            }
        }

        if(!isFinalized && globalServices.hasService(serviceTag)) {
            return globalServices.getService(serviceTag);
        }

        throw new IllegalStateException("The service [" + serviceTag + "] does not exist in any scope that is accessible from [" + scopeTag + "], scopes are [" + Arrays.toString(
                activeScopes.toArray()) + "]!");
    }

    <T> T lookupFromScopeAll(String scopeTag, String serviceTag) {
        verifyStackIsInitialized();

        LinkedHashSet<String> activeScopes = _findScopesForScopeTag(scopeTag, false);

        for(String scope : activeScopes) {
            if(hasService(scope, serviceTag)) {
                return getService(scope, serviceTag);
            }
        }

        if(!isFinalized && globalServices.hasService(serviceTag)) {
            return globalServices.getService(serviceTag);
        }

        throw new IllegalStateException("The service [" + serviceTag + "] does not exist in any scope that is accessible from [" + scopeTag + "], scopes are [" + Arrays.toString(
                activeScopes.toArray()) + "]!");
    }

    boolean canFindService(@NonNull String serviceTag) {
        checkServiceTag(serviceTag);
        List<String> activeScopes = getActiveScopesReverse();
        for(String scopeTag : activeScopes) {
            if(hasService(scopeTag, serviceTag)) {
                return true;
            }
        }

        //noinspection RedundantIfStatement
        if(!isFinalized && globalServices.hasService(serviceTag)) {
            return true;
        }

        return false;
    }

    @NonNull
    <T> T lookupService(@NonNull String serviceTag) {
        checkServiceTag(serviceTag);

        verifyStackIsInitialized();

        LinkedHashSet<String> activeScopes = _findScopesForScopeTag(null, false);

        for(String scopeTag : activeScopes) {
            if(hasService(scopeTag, serviceTag)) {
                return getService(scopeTag, serviceTag);
            }
        }

        if(!isFinalized && globalServices.hasService(serviceTag)) {
            return globalServices.getService(serviceTag);
        }

        throw new IllegalStateException("The service [" + serviceTag + "] does not exist in any scopes, which are " + Arrays.toString(
                activeScopes.toArray()) + "! " +
                                                "Is the scope tag registered via a ScopeKey? " +
                                                "If yes, make sure the StateChanger has been set by this time, " +
                                                "and that you've bound and are trying to lookup the service with the correct service tag. " +
                                                "Otherwise, it is likely that the scope you intend to inherit the service from does not exist.");
    }

    private void verifyStackIsInitialized() {
        if(this.latestKeys == null) {
            throw new IllegalStateException("Cannot lookup from an empty stack.");
        }
    }


    static private void checkKey(@NonNull Object key) {
        if(key == null) {
            throw new IllegalArgumentException("Key cannot be null!");
        }
    }

    static private void checkScopeTag(@NonNull String scopeTag) {
        if(scopeTag == null) {
            throw new IllegalArgumentException("Scope tag cannot be null!");
        }
    }

    static private void checkServiceTag(@NonNull String serviceTag) {
        if(serviceTag == null) {
            throw new IllegalArgumentException("Service tag cannot be null!");
        }
    }

    static void checkParentScopes(ScopeKey.Child child) {
        //noinspection ConstantConditions
        if(child.getParentScopes() == null) {
            throw new IllegalArgumentException("Parent scopes cannot be null!");
        }
    }

    private static void checkScopeLookupMode(ScopeLookupMode mode) {
        if(mode == null) {
            throw new IllegalArgumentException("Mode cannot be null!");
        }
    }
}