package com.zhuinden.simplestack;

import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

class ScopeNode {
    private final Map<String, Object> services = new LinkedHashMap<>();
    private final Map<String, Object> aliases = new LinkedHashMap<>();

    private final IdentityHashMap<Object, Integer> serviceTracker = new IdentityHashMap<>();

    ScopeNode() {
    }

    ScopeNode(@NonNull ScopeNode services) {
        //noinspection ConstantConditions
        if(services == null) {
            throw new IllegalArgumentException("services cannot be null!");
        }
        this.services.putAll(services.services);
        this.aliases.putAll(services.aliases);
    }

    public boolean isEmpty() {
        return services.isEmpty();
    }

    public void addService(@NonNull String serviceTag, @NonNull Object service) {
        checkServiceTag(serviceTag);
        checkService(service);
        this.services.put(serviceTag, service);

        if(!serviceTracker.containsKey(service)) {
            this.serviceTracker.put(service, 1); // for alias restriction
        }
    }

    public boolean hasService(@NonNull String serviceTag) {
        checkServiceTag(serviceTag);
        return this.services.containsKey(serviceTag);
    }

    public void addAlias(@NonNull String alias, @NonNull Object service) {
        checkAlias(alias);
        checkService(service);

        if(!serviceTracker.containsKey(service)) {
            throw new IllegalStateException("A service should be added to the scope before it is bound to aliases.");
        }
        this.aliases.put(alias, service);
    }

    public boolean hasAlias(@NonNull String alias) {
        checkAlias(alias);
        return this.aliases.containsKey(alias);
    }

    public Set<Map.Entry<String, Object>> services() {
        return Collections.unmodifiableSet(services.entrySet());
    }

    public <T> T getService(@NonNull String serviceTag) {
        checkServiceTag(serviceTag);

        if(hasService(serviceTag)) {
            //noinspection unchecked
            return (T) services.get(serviceTag);
        }
        throw new IllegalArgumentException("Scope does not contain [" + serviceTag + "]");
    }

    boolean hasServiceOrAlias(@NonNull String identifier) {
        checkServiceTag(identifier);
        checkAlias(identifier);

        if(hasService(identifier)) {
            return true;
        }

        //noinspection RedundantIfStatement
        if(hasAlias(identifier)) {
            return true;
        }

        return false;
    }

    <T> T getServiceOrAlias(@NonNull String identifier) {
        checkServiceTag(identifier);
        checkAlias(identifier);

        if(hasService(identifier)) {
            return getService(identifier);
        }

        if(hasAlias(identifier)) {
            //noinspection unchecked
            return (T) aliases.get(identifier);
        }
        throw new IllegalArgumentException("Scope does not contain [" + identifier + "]");
    }

    private static void checkServiceTag(@NonNull String serviceTag) {
        //noinspection ConstantConditions
        if(serviceTag == null) {
            throw new IllegalArgumentException("serviceTag cannot be null!");
        }
    }

    private static void checkService(@NonNull Object service) {
        //noinspection ConstantConditions
        if(service == null) {
            throw new IllegalArgumentException("service cannot be null!");
        }
    }

    private static void checkAlias(@NonNull String alias) {
        // noinspection ConstantConditions
        if(alias == null) {
            throw new IllegalArgumentException("alias cannot be null!");
        }
    }
}
