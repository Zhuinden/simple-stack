package com.zhuinden.simplestack;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

class ScopeNode {
    private final Map<String, Object> services = new LinkedHashMap<>();
    private final Map<String, Object> aliases = new LinkedHashMap<>();

    ScopeNode() {
    }

    ScopeNode(@Nonnull ScopeNode services) {
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

    public void addService(@Nonnull String serviceTag, @Nonnull Object service) {
        checkServiceTag(serviceTag);
        checkService(service);

        this.services.put(serviceTag, service);
    }

    public boolean hasService(@Nonnull String serviceTag) {
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

    public void addAlias(@Nonnull String alias, @Nonnull Object service) {
        checkAlias(alias);
        checkService(service);

        this.aliases.put(alias, service);
    }

    public Set<Map.Entry<String, Object>> services() {
        return Collections.unmodifiableSet(services.entrySet());
    }

    public <T> T getService(@Nonnull String serviceTag) {
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

    private static void checkServiceTag(@Nonnull String serviceTag) {
        //noinspection ConstantConditions
        if(serviceTag == null) {
            throw new IllegalArgumentException("serviceTag cannot be null!");
        }
    }

    private static void checkService(@Nonnull Object service) {
        //noinspection ConstantConditions
        if(service == null) {
            throw new IllegalArgumentException("service cannot be null!");
        }
    }

    private static void checkAlias(@Nonnull String alias) {
        // noinspection ConstantConditions
        if(alias == null) {
            throw new IllegalArgumentException("alias cannot be null!");
        }
    }
}
