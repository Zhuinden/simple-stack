package com.zhuinden.simplestack;

import android.support.annotation.NonNull;

import com.zhuinden.statebundle.StateBundle;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

class ScopeManager {
    ScopeManager() {
    }

    private final Map<String, Map<String, Object>> scopes = new LinkedHashMap<>();

    private final StateBundle rootBundle = new StateBundle();

    void buildScopes(StateChange stateChange) {
        for(Object key : stateChange.getNewState()) {
            if(key instanceof ScopeKey) {
                ScopeKey scopeKey = (ScopeKey) key;
                String scopeTag = scopeKey.getScopeTag();
                if(!scopes.containsKey(scopeTag)) {
                    Map<String, Object> scope = new LinkedHashMap<>();
                    scopes.put(scopeTag, scope);
                    scopeKey.bindServices(new ScopeKey.ServiceBinder(scopeTag, scope));

                    // restore states
                    if(rootBundle.containsKey(scopeTag)) {
                        for(Map.Entry<String, Object> serviceEntry : scope.entrySet()) {
                            String serviceTag = serviceEntry.getKey();
                            Object service = serviceEntry.getValue();
                            if(service instanceof Bundleable) {
                                StateBundle scopeBundle = rootBundle.getBundle(scopeTag);
                                if(scopeBundle != null && scopeBundle.containsKey(serviceTag)) {
                                    ((Bundleable) service).fromBundle(scopeBundle.getBundle(serviceTag));
                                }
                            }
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
                    if(service instanceof ScopeKey.Scoped) {
                        ((ScopeKey.Scoped) service).onExitScope(scope);
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

    boolean hasService(String scopeTag, String serviceTag) {
        if(!scopes.containsKey(scopeTag)) {
            return false;
        }

        Map<String, Object> services = scopes.get(scopeTag);
        return services.containsKey(serviceTag);
    }

    @NonNull
    <T> T getService(String scopeTag, String serviceTag) {
        checkScopeExists(scopeTag);

        Map<String, Object> services = scopes.get(scopeTag);
        if(!services.containsKey(serviceTag)) {
            throw new IllegalArgumentException("The specified service with tag [" + serviceTag + "] does not exist!");
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
