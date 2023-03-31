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

import com.zhuinden.statebundle.StateBundle;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

class ScopeManager {
    private boolean willHandleAheadOfTimeBackEvent;

    private List<AheadOfTimeWillHandleBackChangedListener> aheadOfTimeWillHandleBackChangedListeners = new ArrayList<>();

    public void addAheadOfTimeWillHandleBackChangedListener(AheadOfTimeWillHandleBackChangedListener aheadOfTimeWillHandleBackChangedListener) {
        aheadOfTimeWillHandleBackChangedListeners.add(aheadOfTimeWillHandleBackChangedListener);
    }

    public void removeAheadOfTimeWillHandleBackChangedListener(AheadOfTimeWillHandleBackChangedListener aheadOfTimeWillHandleBackChangedListener) {
        aheadOfTimeWillHandleBackChangedListeners.remove(aheadOfTimeWillHandleBackChangedListener);
    }

    private final AheadOfTimeBackCallback.EnabledChangedListener innerEnabledChangedListener = new AheadOfTimeBackCallback.EnabledChangedListener() {
        @Override
        public void onEnabledChanged(boolean isEnabled) {
            if(backstack.previousTopKeyWithAssociatedScope != null) {
                updateWillHandleAheadOfTimeBackEvent(backstack.previousTopKeyWithAssociatedScope);
            }
        }
    };

    private static class ScopeRegistrations {
        public static class ScopeInternals {
            public final ScopeNode scopeNode;
            public final AheadOfTimeBackCallbackRegistry aheadOfTimeBackCallbackRegistry;

            public ScopeInternals(
                ScopeNode scopeNode,
                AheadOfTimeBackCallbackRegistry aheadOfTimeBackCallbackRegistry
            ) {
                this.scopeNode = scopeNode;
                this.aheadOfTimeBackCallbackRegistry = aheadOfTimeBackCallbackRegistry;
            }
        }

        private final Map<ScopeRegistration, ScopeInternals> scopeRegistrations = new LinkedHashMap<>();

        public boolean containsKey(String scopeTag) {
            for(ScopeRegistration registration : scopeRegistrations.keySet()) {
                if(registration.scopeTag.equals(scopeTag)) {
                    return true;
                }
            }
            return false;
        }

        public Set<String> keySet() {
            LinkedHashSet<String> scopes = new LinkedHashSet<>();
            for(ScopeRegistration registration : this.scopeRegistrations.keySet()) {
                scopes.add(registration.scopeTag);
                scopes.addAll(registration.explicitParentScopes);
            }
            return Collections.unmodifiableSet(scopes);
        }

        public Set<Map.Entry<String, ScopeInternals>> entrySet() {
            LinkedHashSet<Map.Entry<String, ScopeInternals>> set = new LinkedHashSet<>();
            for(Map.Entry<ScopeRegistration, ScopeInternals> entry : scopeRegistrations.entrySet()) {
                Map.Entry<String, ScopeInternals> mappedEntry = new AbstractMap.SimpleEntry<>(entry.getKey().scopeTag,
                                                                                              entry.getValue());
                set.add(mappedEntry);
            }
            return Collections.unmodifiableSet(set);
        }

        public void putKey(Object key, String scopeTag, ScopeInternals scopeInternals, boolean isExplicitParent, boolean isGlobalScope, boolean isDummyScope) {
            final List<String> explicitParentScopes;
            if(key instanceof ScopeKey.Child) {
                explicitParentScopes = ((ScopeKey.Child) key).getParentScopes();
            } else {
                explicitParentScopes = Collections.emptyList();
            }
            ScopeRegistration scopeRegistration = new ScopeRegistration(key,
                                                                        scopeTag,
                                                                        explicitParentScopes,
                                                                        isExplicitParent,
                                                                        isGlobalScope,
                                                                        isDummyScope);
            put(scopeRegistration, scopeInternals);
        }

        @Nullable
        public ScopeInternals get(String scopeTag) {
            for(ScopeRegistration registration : scopeRegistrations.keySet()) {
                if(registration.scopeTag.equals(scopeTag)) {
                    return scopeRegistrations.get(registration);
                }
            }
            return null;
        }

        public void put(ScopeRegistration scopeRegistration, ScopeInternals scopeInternals) {
            scopeRegistrations.put(scopeRegistration, scopeInternals);
        }

        @Nullable
        public ScopeInternals remove(String scopeTag) {
            Iterator<Map.Entry<ScopeRegistration, ScopeInternals>> iterator = scopeRegistrations.entrySet().iterator();
            while(iterator.hasNext()) {
                Map.Entry<ScopeRegistration, ScopeInternals> entry = iterator.next();
                String currentScopeTag = entry.getKey().scopeTag;
                if(currentScopeTag.equals(scopeTag)) {
                    ScopeInternals scopeInternals = entry.getValue();
                    iterator.remove();
                    return scopeInternals;
                }
            }
            return null;
        }

        public List<String> getScopeTagsInTraversalOrder() {
            LinkedHashSet<String> scopeTags = new LinkedHashSet<>();
            List<ScopeRegistration> registrations = new ArrayList<>(scopeRegistrations.keySet());
            for(int i = registrations.size() - 1; i >= 0; i--) {
                ScopeRegistration registration = registrations.get(i);
                if(!registration.isDummyScope) {
                    scopeTags.add(registration.scopeTag);
                }
                for(int j = registration.explicitParentScopes.size() - 1; j >= 0; j--) {
                    scopeTags.add(registration.explicitParentScopes.get(j));
                }
            }

            return Collections.unmodifiableList(new ArrayList<>(scopeTags));
        }

        public LinkedHashSet<String> findScopesForKey(@Nonnull Object targetKey, boolean explicitOnly) {
            LinkedHashSet<String> scopeTags = new LinkedHashSet<>();

            int indexInRegistrations = -1;
            List<ScopeRegistration> registrations = new ArrayList<>(scopeRegistrations.keySet());
            for(int i = registrations.size() - 1; i >= 0; i--) {
                ScopeRegistration registration = registrations.get(i);
                if(registration.key != null && registration.key.equals(targetKey)) {
                    indexInRegistrations = i;
                    break;
                }
            }

            if(indexInRegistrations >= 0) {
                int initialIndex = explicitOnly ? indexInRegistrations : 0;

                for(int i = indexInRegistrations; i >= initialIndex; i--) {
                    ScopeRegistration currentRegistration = registrations.get(i);
                    if(!currentRegistration.isGlobalScope) {
                        if(!currentRegistration.isDummyScope) {
                            scopeTags.add(currentRegistration.scopeTag);
                        }

                        List<String> explicitParents = new ArrayList<>(currentRegistration.explicitParentScopes);
                        Collections.reverse(explicitParents);
                        scopeTags.addAll(explicitParents);
                    }
                }
            }

            return scopeTags;
        }

        public LinkedHashSet<String> findScopesForScopeTag(@Nonnull String scopeTag, boolean explicitOnly) {
            LinkedHashSet<String> scopeTags = new LinkedHashSet<>();

            int indexInRegistrations = -1;
            List<ScopeRegistration> registrations = new ArrayList<>(scopeRegistrations.keySet());
            for(int i = registrations.size() - 1; i >= 0; i--) {
                ScopeRegistration registration = registrations.get(i);
                if(scopeTag.equals(registration.scopeTag)) {
                    indexInRegistrations = i;
                    break;
                }
            }

            if(indexInRegistrations >= 0) {
                int initialIndex = explicitOnly ? indexInRegistrations : 0;
                for(int x = indexInRegistrations; x >= initialIndex; x--) {
                    ScopeRegistration registration = registrations.get(x);
                    int indexOfParentScope = registration.explicitParentScopes.indexOf(scopeTag);
                    if(indexOfParentScope != -1) { // scopeTag is an explicit parent
                        for(int i = indexOfParentScope; i >= 0; i--) {
                            scopeTags.add(registration.explicitParentScopes.get(i));
                        }
                    } else {
                        if(!registration.isDummyScope) {
                            scopeTags.add(registration.scopeTag);
                        }

                        List<String> explicitParents = new ArrayList<>(registration.explicitParentScopes);
                        Collections.reverse(explicitParents);
                        scopeTags.addAll(explicitParents);
                    }
                }
            }

            return scopeTags;
        }

        public ScopeRegistration findScopeRegistrationForScopeTag(@Nonnull String scopeTag) {
            for(ScopeRegistration scopeRegistration : scopeRegistrations.keySet()) {
                if(scopeTag.equals(scopeRegistration.scopeTag)) {
                    return scopeRegistration;
                }
            }
            return null;
        }

        void reorderToEnd(@Nonnull String scopeTag) {
            ScopeRegistration scopeRegistration = findScopeRegistrationForScopeTag(scopeTag);
            if(scopeRegistration != null) {
                ScopeInternals scopeInternals = scopeRegistrations.remove(scopeRegistration);
                //noinspection ConstantConditions
                scopeRegistrations.put(scopeRegistration, scopeInternals);
            }
        }
    }

    private static class ScopeRegistration {
        private Object key; // null if GlobalScope
        private String scopeTag;
        private List<String> explicitParentScopes;
        private boolean isExplicitParent;
        private boolean isGlobalScope;
        private boolean isDummyScope;

        public ScopeRegistration(
            @Nullable Object key, // key is null if global scope
            @Nonnull String scopeTag,
            @Nonnull List<String> explicitParentScopes,
            boolean isExplicitParent,
            boolean isGlobalScope,
            boolean isDummyScope // no ScopeKey internal scope registrations
        ) {

            //noinspection ConstantConditions
            if(scopeTag == null) {
                throw new NullPointerException("scopeTag must not be null!");
            }
            //noinspection ConstantConditions
            if(explicitParentScopes == null) {
                throw new NullPointerException("explicitParentScopes must not be null!");
            }
            this.key = key;
            this.scopeTag = scopeTag;
            this.explicitParentScopes = explicitParentScopes;
            this.isExplicitParent = isExplicitParent;
            this.isGlobalScope = isGlobalScope;
            this.isDummyScope = isDummyScope;
        }

        @Override
        public int hashCode() {
            return scopeTag.hashCode();
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            return obj instanceof ScopeRegistration && ((ScopeRegistration) obj).scopeTag.equals(scopeTag);
        }

        @Nonnull
        @Override
        public String toString() {
            return "ScopeRegistration[scopeTag=[" + scopeTag + "], explicitParents=[" + Arrays.toString(
                explicitParentScopes.toArray()) + "]]";
        }
    }

    private static final String GLOBAL_SCOPE_TAG = GlobalServices.SCOPE_TAG;
    private final ScopeRegistration globalScopeRegistration = new ScopeRegistration(null,
                                                                                    GLOBAL_SCOPE_TAG,
                                                                                    Collections.<String>emptyList(),
                                                                                    true,
                                                                                    true,
                                                                                    false);

    private static final GlobalServices EMPTY_GLOBAL_SERVICES = GlobalServices.builder().build();

    private final ScopeRegistrations scopes = new ScopeRegistrations();

    private final IdentityHashMap<ScopedServices.HandlesBack, Boolean> backDispatchedServices = new IdentityHashMap<>();

    private final LinkedHashSet<Object> trackedKeys = new LinkedHashSet<>();

    private final IdentityHashMap<Object, Set<String>> scopeEnteredServices = new IdentityHashMap<>();
    private final IdentityHashMap<Object, Set<String>> scopeActivatedServices = new IdentityHashMap<>();

    private final IdentityHashMap<Object, Integer> untrackEventInvocationTracker = new IdentityHashMap<>(); // call unregister/inactivated only once!

    private boolean isGlobalScopePendingActivation = true;

    void activateGlobalScope() {
        notifyScopeActivation(GLOBAL_SCOPE_TAG, globalServices.getScope());
    }

    void deactivateGlobalScope() {
        notifyScopeDeactivation(GLOBAL_SCOPE_TAG, globalServices.getScope());
    }

    static class AssertingScopedServices
        implements ScopedServices {

        @Override
        public void bindServices(@Nonnull ServiceBinder serviceBinder) {
            throw new IllegalStateException(
                "No scoped services are defined. To create scoped services, an instance of ScopedServices must be provided to configure the services that are available in a given scope.");
        }

    }

    private GlobalServices globalServices = EMPTY_GLOBAL_SERVICES;
    private GlobalServices.Factory globalServiceFactory = null;
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

    private final StateBundle rootBundle = new StateBundle();

    void setScopedServices(ScopedServices scopedServices) {
        this.scopedServices = scopedServices;
    }

    void setGlobalServices(GlobalServices globalServices) {
        this.globalServices = globalServices;
    }

    void setGlobalServices(GlobalServices.Factory globalServiceFactory) {
        this.globalServiceFactory = globalServiceFactory;
    }


    private void buildGlobalScope() {
        if(!scopes.containsKey(GLOBAL_SCOPE_TAG)) {
            if(globalServiceFactory != null) {
                globalServices = globalServiceFactory.create(backstack);

                for(Map.Entry<String, Object> entry : globalServices.services()) {
                    if(entry.getValue() == backstack) {
                        throw new IllegalArgumentException(
                            "The root backstack should not be added as a service, as it would cause a circular save-state loop. Adding it as an alias would work, but should typically not be necessary because of `serviceBinder.getBackstack()`.");
                    }
                }
            }

            ScopeNode scope = globalServices.getScope();
            scopes.put(globalScopeRegistration,
                       new ScopeRegistrations.ScopeInternals(scope, new AheadOfTimeBackCallbackRegistry()));

            restoreAndNotifyServices(GLOBAL_SCOPE_TAG, scope);
        }
    }

    private void buildScope(Object key, String scopeTag, boolean isExplicitParent, boolean isDummyScope) {
        //noinspection ConstantConditions
        if(scopeTag == null) {
            throw new IllegalArgumentException("Scope tag provided by scope key cannot be null!");
        }
        if(!scopes.containsKey(scopeTag)) {
            ScopeRegistrations.ScopeInternals scopeInternals = new ScopeRegistrations.ScopeInternals(
                new ScopeNode(),
                new AheadOfTimeBackCallbackRegistry()
            );
            scopes.putKey(key, scopeTag, scopeInternals, isExplicitParent, false, isDummyScope);

            scopeInternals.aheadOfTimeBackCallbackRegistry.addEnabledChangedListener(innerEnabledChangedListener);

            if(!isDummyScope) {
                scopedServices.bindServices(new ServiceBinder(this,
                                                              key,
                                                              scopeTag,
                                                              scopeInternals.scopeNode,
                                                              scopeInternals.aheadOfTimeBackCallbackRegistry));

                for(Map.Entry<String, Object> entry : scopeInternals.scopeNode.services()) {
                    if(entry.getValue() == backstack) {
                        throw new IllegalArgumentException(
                            "The root backstack should not be added as a service, as it would cause a circular save-state loop. Adding it as an alias would work, but should typically not be necessary because of `serviceBinder.getBackstack()`.");
                    }
                }

                restoreAndNotifyServices(scopeTag, scopeInternals.scopeNode);
            }
        }
    }

    private void restoreAndNotifyServices(String scopeTag, ScopeNode scope) {
        for(Map.Entry<String, Object> serviceEntry : scope.services()) {
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

    // EventBubbling Mode
    public boolean dispatchBack(@Nonnull Object currentTop) {
        backDispatchedServices.clear();

        List<String> scopeTags = new ArrayList<>(scopes.findScopesForKey(currentTop, true));

        try {
            for(String scopeTag : scopeTags) {
                //noinspection ConstantConditions
                ScopeNode scopeNode = scopes.get(scopeTag).scopeNode;
                List<Map.Entry<String, Object>> services = new ArrayList<>(scopeNode.services());
                for(int i = services.size() - 1; i >= 0; i--) {
                    Object service = services.get(i).getValue();
                    if(service instanceof ScopedServices.HandlesBack) {
                        ScopedServices.HandlesBack handlesBack = (ScopedServices.HandlesBack) service;
                        if(backDispatchedServices.containsKey(handlesBack)) {
                            continue; // skip if already attempted to dispatch back
                        }

                        backDispatchedServices.put(handlesBack, true);

                        boolean handled = handlesBack.onBackEvent();
                        if(handled) {
                            return true;
                        }
                    }
                }
            }

            return false;
        } finally {
            backDispatchedServices.clear();
        }
    }

    public boolean willHandleAheadOfTimeBackEvent() {
        return willHandleAheadOfTimeBackEvent;
    }

    private void updateWillHandleAheadOfTimeBackEventAndNotifyListeners(boolean willHandleAheadOfTimeBackEvent) {
        boolean previousWillHandleAheadOfTimeBackEvent = this.willHandleAheadOfTimeBackEvent;
        this.willHandleAheadOfTimeBackEvent = willHandleAheadOfTimeBackEvent;

        if(previousWillHandleAheadOfTimeBackEvent != willHandleAheadOfTimeBackEvent) {
            notifyWillHandleAheadOfTimeChangedListeners(willHandleAheadOfTimeBackEvent);
        }
    }

    private void notifyWillHandleAheadOfTimeChangedListeners(boolean willHandleAheadOfTimeBackEvent) {
        List<AheadOfTimeWillHandleBackChangedListener> copy = new ArrayList<>(aheadOfTimeWillHandleBackChangedListeners);
        for(AheadOfTimeWillHandleBackChangedListener listener : copy) {
            listener.willHandleBackChanged(willHandleAheadOfTimeBackEvent);
        }
    }

    // AheadOfTime Mode
    public void updateWillHandleAheadOfTimeBackEvent(@Nonnull Object keyWithAssociatedScope) {
        List<String> scopeTags = new ArrayList<>(scopes.findScopesForKey(keyWithAssociatedScope, true));

        for(String scopeTag : scopeTags) {
            //noinspection ConstantConditions
            AheadOfTimeBackCallbackRegistry aheadOfTimeBackCallbackRegistry = scopes.get(scopeTag).aheadOfTimeBackCallbackRegistry;
            if(aheadOfTimeBackCallbackRegistry.isEnabled()) {
                updateWillHandleAheadOfTimeBackEventAndNotifyListeners(true);
                return;
            }
        }

        updateWillHandleAheadOfTimeBackEventAndNotifyListeners(false);
    }

    public void handleAheadOfTimeBackEvent(@Nonnull Object currentTop) {
        List<String> scopeTags = new ArrayList<>(scopes.findScopesForKey(currentTop, true));

        for(String scopeTag : scopeTags) {
            //noinspection ConstantConditions
            AheadOfTimeBackCallbackRegistry aheadOfTimeBackCallbackRegistry = scopes.get(scopeTag).aheadOfTimeBackCallbackRegistry;
            if(aheadOfTimeBackCallbackRegistry.isEnabled()) {
                aheadOfTimeBackCallbackRegistry.onBackReceived();
                return;
            }
        }

        throw new AheadOfTimeBackProcessingContractViolationException(
            "ScopeManager attempted to dispatch back, even though no enabled registration was present. This is most likely an error, and shouldn't have happened.");
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

    private boolean isInitialized = false;

    private boolean isFinalized = false;

    boolean isFinalized() {
        return isFinalized;
    }

    void finalizeScopes() {
        this.isFinalized = true;

        // this logic is actually mostly inside Backstack for some reason
        destroyScope(GLOBAL_SCOPE_TAG);

        this.isInitialized = false;
    }

    private IdentityHashMap<Object, String> dummyScopeTags = new IdentityHashMap<>();

    void buildScopes(List<Object> newKeys) {
        if(isFinalized) {
            this.isFinalized = false; // reset this for future travellers, I guess.
            this.isGlobalScopePendingActivation = true; // if we allow scopeRegistrations to be rebuilt once finalized, we need to enable activation of globals.
        }

        if(!isInitialized) { // this seems to be the best way to track that we can safely register and initialize the global scope.
            buildGlobalScope();
        }
        isInitialized = true;

        trackedKeys.addAll(newKeys);

        for(Object key : newKeys) {
            if(key instanceof ScopeKey.Child) {
                ScopeKey.Child child = (ScopeKey.Child) key;
                checkParentScopes(child);
                for(String parent : child.getParentScopes()) {
                    buildScope(key, parent, true, false);
                }
            }
            if(key instanceof ScopeKey) {
                ScopeKey scopeKey = (ScopeKey) key;
                String scopeTag = scopeKey.getScopeTag();
                buildScope(key, scopeTag, false, false);
            } else {
                String dummyScope;
                if(dummyScopeTags.containsKey(key)) {
                    dummyScope = dummyScopeTags.get(key);
                } else {
                    dummyScope = UUID.randomUUID().toString();
                }
                dummyScopeTags.put(key, dummyScope);
                buildScope(key, dummyScope, false, true);
            }
        }
    }

    void cleanupScopesBy(List<Object> newKeys) {
        Set<String> currentScopes = new LinkedHashSet<>();
        currentScopes.add(GLOBAL_SCOPE_TAG); // prevent global scope from being destroyed

        for(Object key : newKeys) {
            if(key instanceof ScopeKey.Child) {
                ScopeKey.Child child = (ScopeKey.Child) key;
                checkParentScopes(child);
                currentScopes.addAll(child.getParentScopes());
            }
            if(key instanceof ScopeKey) {
                ScopeKey scopeKey = (ScopeKey) key;
                currentScopes.add(scopeKey.getScopeTag());
            } else if(dummyScopeTags.containsKey(key)) {
                currentScopes.add(dummyScopeTags.get(key));
            }
        }

        List<String> activeScopes = new ArrayList<>(scopes.keySet());
        Collections.reverse(activeScopes);
        for(String activeScope : activeScopes) {
            if(!currentScopes.contains(activeScope)) {
                destroyScope(activeScope);
            }
        }

        CollectionHelper.retainAll(trackedKeys, newKeys); // see #256
        CollectionHelper.retainAll(dummyScopeTags.keySet(), newKeys); // see #256

        for(String currentScope : currentScopes) {
            if(activeScopes.contains(currentScope)) {
                scopes.reorderToEnd(currentScope);
            }
        }
    }

    void destroyScope(String scopeTag) {
        if(scopes.containsKey(scopeTag)) {
            ScopeRegistrations.ScopeInternals scopeInternals = scopes.remove(scopeTag);
            // noinspection ConstantConditions
            destroyServicesAndRemoveState(scopeTag, scopeInternals.scopeNode);

            scopeInternals.aheadOfTimeBackCallbackRegistry.removeEnabledChangedListener(innerEnabledChangedListener);
        }
    }

    private void destroyServicesAndRemoveState(String scopeTag, ScopeNode scopeNode) {
        Set<Map.Entry<String, Object>> services = scopeNode.services();
        List<Object> previousServices = new ArrayList<>(services.size());
        for(Map.Entry<String, Object> entry : services) {
            previousServices.add(entry.getValue());
        }
        Collections.reverse(previousServices);

        untrackEventInvocationTracker.clear();

        for(Object service : previousServices) {
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

        untrackEventInvocationTracker.clear(); // #213

        rootBundle.remove(scopeTag);
    }

    void dispatchActivation(@Nonnull Set<String> scopesToDeactivate, @Nonnull Set<String> scopesToActivate) {
        if(isGlobalScopePendingActivation) {
            isGlobalScopePendingActivation = false;
            activateGlobalScope();
        }

        for(String newScopeTag : scopesToActivate) {
            if(!scopes.containsKey(newScopeTag)) {
                throw new AssertionError(
                    "The new scope [" + newScopeTag + "] should exist, but it doesn't exist in [" + Arrays.toString(
                        scopes.keySet().toArray()) + "]" + "! This shouldn't happen. If you see this error, this functionality is broken.");
            }

            ScopeRegistrations.ScopeInternals newScope = scopes.get(newScopeTag);
            //noinspection ConstantConditions
            notifyScopeActivation(newScopeTag, newScope.scopeNode);
        }

        for(String previousScopeTag : scopesToDeactivate) {
            if(!scopes.containsKey(previousScopeTag)) {
                throw new AssertionError(
                    "The previous scope [" + previousScopeTag + "] should exist in [" + Arrays.toString(scopes.keySet().toArray()) + "]" + "! This shouldn't happen. If you see this error, this functionality is broken.");
            }

            ScopeRegistrations.ScopeInternals previousScopeNode = scopes.get(previousScopeTag);
            //noinspection ConstantConditions
            notifyScopeDeactivation(previousScopeTag, previousScopeNode.scopeNode);
        }
    }

    private void notifyScopeActivation(@Nonnull String newScopeTag, @Nonnull ScopeNode newScope) {
        for(Map.Entry<String, Object> entry : newScope.services()) {
            Object service = entry.getValue();

            if(isServiceNotActivated(service) && service instanceof ScopedServices.Activated) {
                ((ScopedServices.Activated) service).onServiceActive();
            }

            if(isServiceNotTrackedInScope(scopeActivatedServices, service, newScopeTag)) {
                trackServiceInScope(scopeActivatedServices, service, newScopeTag);
            }
        }
    }

    private void notifyScopeDeactivation(String previousScopeTag, ScopeNode previousScope) {
        Set<Map.Entry<String, Object>> services = previousScope.services();
        List<Object> previousServices = new ArrayList<>(services.size());
        for(Map.Entry<String, Object> entry : services) {
            previousServices.add(entry.getValue());
        }
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

        untrackEventInvocationTracker.clear(); // #213
    }

    StateBundle saveStates() {
        StateBundle rootBundle = new StateBundle();
        for(Map.Entry<String, ScopeRegistrations.ScopeInternals> scopeSet : scopes.entrySet()) {
            String scopeKey = scopeSet.getKey();
            ScopeRegistrations.ScopeInternals scopeInternals = scopeSet.getValue();
            ScopeNode services = scopeInternals.scopeNode;

            StateBundle scopeBundle = new StateBundle();
            for(Map.Entry<String, Object> serviceEntry : services.services()) {
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

    boolean hasService(@Nonnull String scopeTag, @Nonnull String serviceTag) {
        checkScopeTag(scopeTag);
        checkServiceTag(serviceTag);

        if(!scopes.containsKey(scopeTag)) {
            return false;
        }

        ScopeRegistrations.ScopeInternals scopeInternals = scopes.get(scopeTag);
        return scopeInternals != null && scopeInternals.scopeNode.hasService(serviceTag);
    }

    @Nonnull
    <T> T getService(@Nonnull String scopeTag, @Nonnull String serviceTag) {
        checkScopeTag(scopeTag);
        checkServiceTag(serviceTag);

        if(!scopes.containsKey(scopeTag)) {
            throw new IllegalArgumentException("The specified scope with tag [" + scopeTag + "] does not exist!");
        }

        ScopeRegistrations.ScopeInternals scopeInternals = scopes.get(scopeTag);
        //noinspection ConstantConditions
        if(!scopeInternals.scopeNode.hasService(serviceTag)) {
            throw new IllegalArgumentException("The specified service with tag [" + serviceTag + "] does not exist in scope [" + scopeTag + "]! Did you accidentally try to use the same scope tag with different services?");
        }
        return scopeInternals.scopeNode.getService(serviceTag);
    }

    boolean hasScope(@Nonnull String scopeTag) {
        checkScopeTag(scopeTag);

        return scopes.containsKey(scopeTag);
    }

    @Nonnull
    Set<String> findScopesForKey(@Nonnull Object key, @Nonnull ScopeLookupMode lookupMode) {
        checkKey(key);
        checkScopeLookupMode(lookupMode);

        return lookupMode.executeFindScopesForKey(this, key);
    }

    @Nonnull
    Set<String> findScopesForKeyAll(Object targetKey) {
        if(!isInitialized) {
            return Collections.emptySet();
        }

        LinkedHashSet<String> activeScopes = scopes.findScopesForKey(targetKey, false);

        if(!isFinalized && !globalServices.isEmpty()) {
            activeScopes.add(GLOBAL_SCOPE_TAG);
        }

        return Collections.unmodifiableSet(activeScopes);
    }

    @Nonnull
    Set<String> findScopesForKeyExplicit(Object targetKey) {
        if(!isInitialized) {
            return Collections.emptySet();
        }

        LinkedHashSet<String> activeScopes = scopes.findScopesForKey(targetKey, true);

        if(!isFinalized && !globalServices.isEmpty()) {
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

    boolean canFindScope(Object targetKey, String scopeTag, ScopeLookupMode lookupMode) {
        checkScopeTag(scopeTag);
        checkScopeLookupMode(lookupMode);

        if(!isInitialized) {
            return false;
        }

        Set<String> activeScopes = scopes.findScopesForKey(targetKey, lookupMode == ScopeLookupMode.EXPLICIT);

        return activeScopes.contains(scopeTag);
    }

    boolean canFindFromScopeExplicit(String scopeTag, String identifier) {
        if(!isInitialized) {
            return false;
        }

        Set<String> activeScopes = scopes.findScopesForScopeTag(scopeTag, true);

        for(String scope : activeScopes) {
            ScopeRegistrations.ScopeInternals scopeInternals = scopes.get(scope);
            if(scopeInternals != null && scopeInternals.scopeNode.hasService(identifier)) {
                return true;
            }
        }

        //noinspection RedundantIfStatement
        if(!isFinalized && globalServices.hasService(identifier)) {
            return true;
        }

        return false;
    }

    boolean canFindFromScopeAll(String scopeTag, String identifier) {
        if(!isInitialized) {
            return false;
        }

        LinkedHashSet<String> activeScopes = scopes.findScopesForScopeTag(scopeTag, false);

        for(String scope : activeScopes) {
            ScopeRegistrations.ScopeInternals scopeInternals = scopes.get(scope);
            if(scopeInternals != null && scopeInternals.scopeNode.hasService(identifier)) {
                return true;
            }
        }

        //noinspection RedundantIfStatement
        if(!isFinalized && globalServices.hasService(identifier)) {
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

    <T> T lookupFromScopeExplicit(String scopeTag, String identifier) {
        verifyStackIsInitialized();

        LinkedHashSet<String> activeScopes = scopes.findScopesForScopeTag(scopeTag, true);

        for(String scope : activeScopes) {
            ScopeRegistrations.ScopeInternals scopeInternals = scopes.get(scope);
            if(scopeInternals != null && scopeInternals.scopeNode.hasService(identifier)) {
                return scopeInternals.scopeNode.getService(identifier);
            }
        }

        if(!isFinalized && globalServices.hasService(identifier)) {
            return globalServices.getService(identifier);
        }

        throw new IllegalStateException("The service [" + identifier + "] does not exist in any scope that is accessible from [" + scopeTag + "], scopes are [" + Arrays.toString(
            activeScopes.toArray()) + "]!");
    }

    <T> T lookupFromScopeAll(String scopeTag, String identifier) {
        verifyStackIsInitialized();

        LinkedHashSet<String> activeScopes = scopes.findScopesForScopeTag(scopeTag, false);

        for(String scope : activeScopes) {
            ScopeRegistrations.ScopeInternals scopeInternals = scopes.get(scope);
            if(scopeInternals != null && scopeInternals.scopeNode.hasService(identifier)) {
                return scopeInternals.scopeNode.getService(identifier);
            }
        }

        if(!isFinalized && globalServices.hasService(identifier)) {
            return globalServices.getService(identifier);
        }

        throw new IllegalStateException("The service [" + identifier + "] does not exist in any scope that is accessible from [" + scopeTag + "], scopes are [" + Arrays.toString(
            activeScopes.toArray()) + "]!");
    }

    boolean canFindService(@Nonnull String identifier) {
        checkServiceTag(identifier);
        List<String> activeScopes = scopes.getScopeTagsInTraversalOrder();
        for(String scope : activeScopes) {
            ScopeRegistrations.ScopeInternals scopeInternals = scopes.get(scope);
            if(scopeInternals != null && scopeInternals.scopeNode.hasService(identifier)) {
                return true;
            }
        }

        return false;
    }

    @Nonnull
    <T> T lookupService(@Nonnull String identifier) {
        checkServiceTag(identifier);

        verifyStackIsInitialized();

        List<String> activeScopes = scopes.getScopeTagsInTraversalOrder();

        for(String scope : activeScopes) {
            ScopeRegistrations.ScopeInternals scopeInternals = scopes.get(scope);
            if(scopeInternals != null && scopeInternals.scopeNode.hasService(identifier)) {
                return scopeInternals.scopeNode.getService(identifier);
            }
        }

        if(!isFinalized && globalServices.hasService(identifier)) {
            return globalServices.getService(identifier);
        }

        throw new IllegalStateException("The service [" + identifier + "] does not exist in any scopes, which are " + Arrays.toString(
            activeScopes.toArray()) + "! " +
                                            "Is the scope tag registered via a ScopeKey? " +
                                            "If yes, make sure the StateChanger has been set by this time, " +
                                            "and that you've bound and are trying to lookup the service with the correct service tag. " +
                                            "Otherwise, it is likely that the scope you intend to inherit the service from does not exist.");
    }

    private void verifyStackIsInitialized() {
        if(!isInitialized) {
            throw new IllegalStateException("Cannot lookup from an empty stack.");
        }
    }


    static private void checkKey(@Nonnull Object key) {
        //noinspection ConstantConditions
        if(key == null) {
            throw new IllegalArgumentException("Key cannot be null!");
        }
    }

    static private void checkScopeTag(@Nonnull String scopeTag) {
        //noinspection ConstantConditions
        if(scopeTag == null) {
            throw new IllegalArgumentException("Scope tag cannot be null!");
        }
    }

    static private void checkServiceTag(@Nonnull String serviceTag) {
        //noinspection ConstantConditions
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