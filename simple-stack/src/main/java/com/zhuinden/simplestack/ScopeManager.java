package com.zhuinden.simplestack;

import android.support.annotation.NonNull;

import com.zhuinden.statebundle.StateBundle;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

class ScopeManager {
    static class AssertingScopedServices
            implements ScopedServices {
        @Override
        public void bindServices(ServiceBinder serviceBinder) {
            throw new IllegalStateException(
                    "No scoped services are defined. To create scoped services, an instance of ScopedServices must be provided to configure the services that are available in a given scope.");
        }
    }

    private ScopedServices scopedServices = new AssertingScopedServices();

    ScopeManager() {
    }

    private final Map<String, Map<String, Object>> scopes = new LinkedHashMap<>();

    private final StateBundle rootBundle = new StateBundle();

    void setScopedServices(ScopedServices scopedServices) {
        this.scopedServices = scopedServices;
    }

    void buildScopes(StateChange stateChange) {
        for(Object key : stateChange.getNewState()) {
            if(key instanceof ScopeKey) {
                ScopeKey scopeKey = (ScopeKey) key;
                String scopeTag = scopeKey.getScopeTag();
                //noinspection ConstantConditions
                if(scopeKey == null) {
                    throw new IllegalArgumentException("Scope tag provided by scope key cannot be null!");
                }
                if(!scopes.containsKey(scopeTag)) {
                    Map<String, Object> scope = new LinkedHashMap<>();
                    scopes.put(scopeTag, scope);

                    scopedServices.bindServices(new ScopedServices.ServiceBinder(key, scopeTag, scope));

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
        }
    }

    void clearScopesNotIn(StateChange stateChange) {
        Set<String> currentScopes = new LinkedHashSet<>();
        for(Object key : stateChange.getNewState()) {
            if(key instanceof ScopeKey) {
                ScopeKey scopeKey = (ScopeKey) key;
                currentScopes.add(scopeKey.getScopeTag());
            }
        }
        Iterator<Map.Entry<String, Map<String, Object>>> scopeSet = scopes.entrySet().iterator();
        while(scopeSet.hasNext()) {
            Map.Entry<String, Map<String, Object>> entry = scopeSet.next();
            String scope = entry.getKey();
            Map<String, Object> services = entry.getValue();
            if(!currentScopes.contains(scope)) {
                for(Object service : services.values()) {
                    if(service instanceof ScopedServices.Scoped) {
                        ((ScopedServices.Scoped) service).onExitScope(scope);
                    }
                }
                scopeSet.remove();
                rootBundle.remove(scope);
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
        checkScopeExists(scopeTag);

        Map<String, Object> services = scopes.get(scopeTag);
        if(!services.containsKey(serviceTag)) {
            throw new IllegalArgumentException("The specified service with tag [" + serviceTag + "] does not exist in scope [" + scopeTag + "]!");
        }
        //noinspection unchecked
        return (T) services.get(serviceTag);
    }

    private void checkScopeExists(String scopeTag) {
        if(!scopes.containsKey(scopeTag)) {
            throw new IllegalArgumentException("The specified scope with tag [" + scopeTag + "] does not exist!");
        }
    }
}
