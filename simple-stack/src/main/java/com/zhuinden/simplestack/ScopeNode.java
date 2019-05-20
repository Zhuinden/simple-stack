package com.zhuinden.simplestack;

import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

class ScopeNode {
    private final Map<String, Object> services = new LinkedHashMap<>();
    private final Map<String, Object> aliases = new LinkedHashMap<>();

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
    }

    public boolean hasService(@NonNull String serviceTag) {
        checkServiceTag(serviceTag);
        if(this.services.containsKey(serviceTag)) {
            return true;
        }

        checkAlias(serviceTag);
        //noinspection RedundantIfStatement
        if(this.aliases.containsKey(serviceTag)) {
            return true;
        }

        return false;
    }

    public void addAlias(@NonNull String alias, @NonNull Object service) {
        checkAlias(alias);
        checkService(service);

        this.aliases.put(alias, service);
    }

    public Set<Map.Entry<String, Object>> services() {
        return Collections.unmodifiableSet(services.entrySet());
    }

    public <T> T getService(@NonNull String serviceTag) {
        checkServiceTag(serviceTag);
        if(services.containsKey(serviceTag)) {
            //noinspection unchecked
            return (T) services.get(serviceTag);
        }

        checkAlias(serviceTag);
        if(aliases.containsKey(serviceTag)) {
            //noinspection unchecked
            return (T) aliases.get(serviceTag);
        }
        throw new IllegalArgumentException("Scope does not contain [" + serviceTag + "]");
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
