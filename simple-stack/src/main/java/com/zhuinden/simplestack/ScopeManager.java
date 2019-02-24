package com.zhuinden.simplestack;

import android.support.annotation.NonNull;

import com.zhuinden.statebundle.StateBundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class ScopeManager {
    static class AssertingScopedServices
            implements ScopedServices {

        @Override
        public void bindServices(@NonNull ServiceBinder serviceBinder) {
            throw new IllegalStateException(
                    "No scoped services are defined. To create scoped services, an instance of ScopedServices must be provided to configure the services that are available in a given scope.");
        }
    }

    private ScopedServices scopedServices = new AssertingScopedServices();

    ScopeManager() {
    }

    private BackstackManager backstackManager;

    void setBackstackManager(BackstackManager backstackManager) {
        this.backstackManager = backstackManager;
    }

    BackstackManager getManager() {
        return backstackManager;
    }

    Backstack getBackstack() {
        return backstackManager.getBackstack();
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

    private void buildScope(Object key, String scopeTag) {
        //noinspection ConstantConditions
        if(scopeTag == null) {
            throw new IllegalArgumentException("Scope tag provided by scope key cannot be null!");
        }
        if(!scopes.containsKey(scopeTag)) {
            lifecycleInvocationTracker.clear();

            Map<String, Object> scope = new LinkedHashMap<>();
            scopes.put(scopeTag, scope);

            scopedServices.bindServices(new ScopedServices.ServiceBinder(this, key, scopeTag, scope));

            for(Map.Entry<String, Object> serviceEntry : scope.entrySet()) {
                String serviceTag = serviceEntry.getKey();
                Object service = serviceEntry.getValue();
                if(rootBundle.containsKey(scopeTag)) {
                    if(service instanceof Bundleable) {
                        StateBundle scopeBundle = rootBundle.getBundle(scopeTag);
                        if(scopeBundle != null && scopeBundle.containsKey(serviceTag)) {
                            ((Bundleable) service).fromBundle(scopeBundle.getBundle(serviceTag));
                        }
                    }
                }
                if(service instanceof ScopedServices.Scoped) {
                    if(!lifecycleInvocationTracker.containsKey(service)) {
                        lifecycleInvocationTracker.add(service);
                        ((ScopedServices.Scoped) service).onEnterScope(scopeTag);
                    }
                }
            }
        }
    }

    private List<Object> latestState = null;

    private final IdentitySet<Object> lifecycleInvocationTracker = new IdentitySet<>();

    void finalizeScopes() {
        // this logic is actually inside BackstackManager for some reason
        this.latestState = Collections.emptyList();
    }

    void buildScopes(List<Object> newState) {
        this.latestState = newState;
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
            lifecycleInvocationTracker.clear();

            Map<String, Object> serviceMap = scopes.remove(scopeTag);
            List<Object> services = new ArrayList<>(serviceMap.values());
            Collections.reverse(services);
            for(Object service : services) {
                if(service instanceof ScopedServices.Scoped) {
                    if(!lifecycleInvocationTracker.containsKey(service)) {
                        lifecycleInvocationTracker.add(service);
                        ((ScopedServices.Scoped) service).onExitScope(scopeTag);
                    }
                }
            }
            rootBundle.remove(scopeTag);
        }
    }

    void dispatchActivation(@NonNull Set<String> scopesToDeactivate, @NonNull Set<String> scopesToActivate) {
        for(String newScopeTag : scopesToActivate) {
            if(!scopes.containsKey(newScopeTag)) {
                throw new AssertionError(
                        "The new scope should exist, but it doesn't! This shouldn't happen. If you see this error, this functionality is broken.");
            }

            lifecycleInvocationTracker.clear();

            Map<String, Object> newServiceMap = scopes.get(newScopeTag);
            for(Object service : newServiceMap.values()) {
                if(service instanceof ScopedServices.Activated) {
                    if(!lifecycleInvocationTracker.containsKey(service)) {
                        lifecycleInvocationTracker.add(service);
                        ((ScopedServices.Activated) service).onScopeActive(newScopeTag);
                    }
                }
            }
        }

        for(String previousScopeTag : scopesToDeactivate) {
            if(!scopes.containsKey(previousScopeTag)) {
                throw new AssertionError(
                        "The previous scope should exist, but it doesn't! This shouldn't happen. If you see this error, this functionality is broken.");
            }

            lifecycleInvocationTracker.clear();

            Map<String, Object> previousServiceMap = scopes.get(previousScopeTag);
            List<Object> previousServices = new ArrayList<>(previousServiceMap.values());
            Collections.reverse(previousServices);
            for(Object service : previousServices) {
                if(service instanceof ScopedServices.Activated) {
                    if(!lifecycleInvocationTracker.containsKey(service)) {
                        lifecycleInvocationTracker.add(service);
                        ((ScopedServices.Activated) service).onScopeInactive(previousScopeTag);
                    }
                }
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

    boolean canFindFromScope(String scopeTag, String serviceTag, ScopeLookupMode lookupMode) {
        checkServiceTag(serviceTag);
        checkScopeTag(scopeTag);
        checkScopeLookupMode(lookupMode);

        if(lookupMode == ScopeLookupMode.ALL) {
            return canFindFromScopeAll(scopeTag, serviceTag);
        } else if(lookupMode == ScopeLookupMode.EXPLICIT) {
            return canFindFromScopeExplicit(scopeTag, serviceTag);
        } else {
            throw new AssertionError("Mode was not handled.");
        }
    }

    private boolean canFindFromScopeExplicit(String scopeTag, String serviceTag) {
        if(this.latestState == null) {
            return false;
        }

        Set<String> activeScopes = new LinkedHashSet<>();
        List<Object> latestState = this.latestState;

        boolean isScopeFound = false;
        for(int i = latestState.size() - 1; i >= 0; i--) {
            Object key = latestState.get(i);
            if(key instanceof ScopeKey) {
                ScopeKey scopeKey = (ScopeKey) key;
                String currentScope = scopeKey.getScopeTag();
                if(currentScope.equals(scopeTag)) {
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
                    if(currentScope.equals(scopeTag)) {
                        isScopeFound = true;
                    }
                    if(isScopeFound) {
                        activeScopes.add(currentScope);
                    }
                }
            }

            if(isScopeFound) { // force explicit only in this mode.
                break;
            }
        }

        for(String scope : activeScopes) {
            if(hasService(scope, serviceTag)) {
                return true;
            }
        }

        return false;
    }

    private boolean canFindFromScopeAll(String scopeTag, String serviceTag) {
        if(this.latestState == null) {
            return false;
        }

        Set<String> activeScopes = new LinkedHashSet<>();
        List<Object> latestState = this.latestState;

        boolean isScopeFound = false;
        for(int i = latestState.size() - 1; i >= 0; i--) {
            Object key = latestState.get(i);
            if(key instanceof ScopeKey) {
                ScopeKey scopeKey = (ScopeKey) key;
                String currentScope = scopeKey.getScopeTag();
                if(currentScope.equals(scopeTag)) {
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
                    if(currentScope.equals(scopeTag)) {
                        isScopeFound = true;
                    }
                    if(isScopeFound) {
                        activeScopes.add(currentScope);
                    }
                }
            }
        }

        for(String scope : activeScopes) {
            if(hasService(scope, serviceTag)) {
                return true;
            }
        }

        return false;
    }

    <T> T lookupFromScope(String scopeTag, String serviceTag) {
        return lookupFromScope(scopeTag, serviceTag, ScopeLookupMode.ALL);
    }

    <T> T lookupFromScope(String scopeTag, String serviceTag, ScopeLookupMode lookupMode) {
        checkScopeTag(scopeTag);
        checkServiceTag(serviceTag);
        checkScopeLookupMode(lookupMode);

        if(lookupMode == ScopeLookupMode.ALL) {
            return lookupFromScopeAll(scopeTag, serviceTag);
        } else if(lookupMode == ScopeLookupMode.EXPLICIT) {
            return lookupFromScopeExplicit(scopeTag, serviceTag);
        } else {
            throw new AssertionError("Mode was not handled.");
        }
    }

    private <T> T lookupFromScopeExplicit(String scopeTag, String serviceTag) {
        verifyStackIsInitialized();

        Set<String> activeScopes = new LinkedHashSet<>();
        List<Object> latestState = this.latestState;

        boolean isScopeFound = false;
        for(int i = latestState.size() - 1; i >= 0; i--) {
            Object key = latestState.get(i);
            if(key instanceof ScopeKey) {
                ScopeKey scopeKey = (ScopeKey) key;
                String currentScope = scopeKey.getScopeTag();
                if(currentScope.equals(scopeTag)) {
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
                    if(currentScope.equals(scopeTag)) {
                        isScopeFound = true;
                    }
                    if(isScopeFound) {
                        activeScopes.add(currentScope);
                    }
                }
            }

            if(isScopeFound) { // force explicit only in this mode.
                break;
            }
        }

        for(String scope : activeScopes) {
            if(hasService(scope, serviceTag)) {
                return getService(scope, serviceTag);
            }
        }

        throw new IllegalStateException("The service [" + serviceTag + "] does not exist in any scope that is accessible from [" + scopeTag + "], scopes are [" + Arrays.toString(
                activeScopes.toArray()) + "]!");
    }

    private <T> T lookupFromScopeAll(String scopeTag, String serviceTag) {
        verifyStackIsInitialized();

        Set<String> activeScopes = new LinkedHashSet<>();
        List<Object> latestState = this.latestState;

        boolean isScopeFound = false;
        for(int i = latestState.size() - 1; i >= 0; i--) {
            Object key = latestState.get(i);
            if(key instanceof ScopeKey) {
                ScopeKey scopeKey = (ScopeKey) key;
                String currentScope = scopeKey.getScopeTag();
                if(currentScope.equals(scopeTag)) {
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
                    if(currentScope.equals(scopeTag)) {
                        isScopeFound = true;
                    }
                    if(isScopeFound) {
                        activeScopes.add(currentScope);
                    }
                }
            }
        }

        for(String scope : activeScopes) {
            if(hasService(scope, serviceTag)) {
                return getService(scope, serviceTag);
            }
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
        return false;
    }

    @NonNull
    <T> T lookupService(@NonNull String serviceTag) {
        checkServiceTag(serviceTag);

        verifyStackIsInitialized();

        Set<String> activeScopes = new LinkedHashSet<>();
        List<Object> latestState = this.latestState;
        for(int i = latestState.size() - 1; i >= 0; i--) {
            Object key = latestState.get(i);
            if(key instanceof ScopeKey) {
                ScopeKey scopeKey = (ScopeKey) key;
                activeScopes.add(scopeKey.getScopeTag());
            }
            if(key instanceof ScopeKey.Child) {
                ScopeKey.Child child = (ScopeKey.Child) key;
                checkParentScopes(child);
                List<String> parentScopes = child.getParentScopes();
                for(int j = parentScopes.size() - 1; j >= 0; j--) {
                    activeScopes.add(parentScopes.get(j));
                }
            }
        }

        for(String scopeTag : activeScopes) {
            if(hasService(scopeTag, serviceTag)) {
                return getService(scopeTag, serviceTag);
            }
        }
        throw new IllegalStateException("The service [" + serviceTag + "] does not exist in any scopes, which are " + Arrays.toString(
                activeScopes.toArray()) + "! " +
                "Is the scope tag registered via a ScopeKey? " +
                "If yes, make sure the StateChanger has been set by this time, " +
                "and that you've bound and are trying to lookup the service with the correct service tag. " +
                "Otherwise, it is likely that the scope you intend to inherit the service from does not exist.");
    }

    private void verifyStackIsInitialized() {
        if(this.latestState == null) {
            throw new IllegalStateException("Cannot lookup from an empty stack.");
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
