package com.zhuinden.simplestack;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.zhuinden.statebundle.StateBundle;

import java.util.ArrayList;
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
                    ((ScopedServices.Scoped) service).onEnterScope(scopeTag);
                }
            }
        }
    }

    void buildScopes(List<Object> newState) {
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
        for(String activeScope: scopeSet) {
            if(!currentScopes.contains(activeScope)) {
                Map<String, Object> scope = scopes.get(activeScope);
                List<Object> services = new ArrayList<>(scope.values());
                Collections.reverse(services);
                for(Object service : services) {
                    if(service instanceof ScopedServices.Scoped) {
                        ((ScopedServices.Scoped) service).onExitScope(activeScope);
                    }
                }
                scopes.remove(activeScope);
                rootBundle.remove(activeScope);
            }
        }
    }

    void destroyScope(String scopeTag) {
        if(scopes.containsKey(scopeTag)) {
            Map<String, Object> serviceMap = scopes.remove(scopeTag);
            List<Object> services = new ArrayList<>(serviceMap.values());
            Collections.reverse(services);
            for(Object service : services) {
                if(service instanceof ScopedServices.Scoped) {
                    ((ScopedServices.Scoped) service).onExitScope(scopeTag);
                }
            }
            rootBundle.remove(scopeTag);
        }
    }

    void dispatchActivation(@Nullable String previousScopeTag, @Nullable String newScopeTag) {
        if(newScopeTag != null) {
            if(!scopes.containsKey(newScopeTag)) {
                throw new AssertionError(
                        "The new scope should exist, but it doesn't! This shouldn't happen. If you see this error, this functionality is broken.");
            }
            Map<String, Object> newServiceMap = scopes.get(newScopeTag);
            for(Object service : newServiceMap.values()) {
                if(service instanceof ScopedServices.Activated) {
                    ((ScopedServices.Activated) service).onScopeActive(newScopeTag);
                }
            }
        }

        if(previousScopeTag != null) {
            if(!scopes.containsKey(previousScopeTag)) {
                throw new AssertionError(
                        "The previous scope should exist, but it doesn't! This shouldn't happen. If you see this error, this functionality is broken.");
            }
            Map<String, Object> previousServiceMap = scopes.get(previousScopeTag);
            List<Object> previousServices = new ArrayList<>(previousServiceMap.values());
            Collections.reverse(previousServices);
            for(Object service : previousServices) {
                if(service instanceof ScopedServices.Activated) {
                    ((ScopedServices.Activated) service).onScopeInactive(previousScopeTag);
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
        if(scopeTag == null) {
            throw new IllegalArgumentException("Scope tag cannot be null!");
        }
        if(serviceTag == null) {
            throw new IllegalArgumentException("Service tag cannot be null!");
        }
        if(!scopes.containsKey(scopeTag)) {
            return false;
        }

        Map<String, Object> services = scopes.get(scopeTag);
        return services.containsKey(serviceTag);
    }

    @NonNull
    <T> T getService(@NonNull String scopeTag, @NonNull String serviceTag) {
        if(scopeTag == null) {
            throw new IllegalArgumentException("Scope tag cannot be null!");
        }
        if(serviceTag == null) {
            throw new IllegalArgumentException("Service tag cannot be null!");
        }

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
        if(scopeTag == null) {
            throw new IllegalArgumentException("Scope tag cannot be null!");
        }
        return scopes.containsKey(scopeTag);
    }

    boolean canFindService(@NonNull String serviceTag) {
        if(serviceTag == null) {
            throw new IllegalArgumentException("Service tag cannot be null!");
        }
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
        if(serviceTag == null) {
            throw new IllegalArgumentException("Service tag cannot be null!");
        }
        List<String> activeScopes = getActiveScopesReverse();
        for(String scopeTag : activeScopes) {
            if(hasService(scopeTag, serviceTag)) {
                return getService(scopeTag, serviceTag);
            }
        }
        throw new IllegalStateException("The service [" + serviceTag + "] does not exist in any scopes! " +
                "Is the scope tag registered via a ScopeKey? " +
                "If yes, make sure the StateChanger has been set by this time, " +
                "and that you've bound and are trying to lookup the service with the correct service tag. " +
                "Otherwise, it is likely that the scope you intend to inherit the service from does not exist.");
    }

    static void checkParentScopes(ScopeKey.Child child) {
        //noinspection ConstantConditions
        if(child.getParentScopes() == null) {
            throw new IllegalArgumentException("Parent scopes cannot be null!");
        }
    }
}
