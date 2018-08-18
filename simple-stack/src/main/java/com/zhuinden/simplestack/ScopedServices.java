package com.zhuinden.simplestack;

import android.support.annotation.NonNull;

import java.util.Map;

/**
 * Scoped Services allow binding services to a given scope. Scopes are identified by a "scope tag" defined by a {@link ScopeKey}.
 *
 * For each scope, the services are bound to that scope only when that scope is first created. Otherwise, they survive configuration change, and are shared between keys that belong to the same scope.
 *
 * This allows the creation of services that can contain observable data (think BehaviorRelay), which can be shared between multiple views/fragments.
 *
 * Additionally, services that implement {@link Bundleable} have their state persisted/restored across process death automatically.
 *
 * Services that implement {@link Scoped} will receive callbacks for when their scope is created, and their scope is destroyed.
 *
 * NOTE: Think of it as configuration: it is kept across configuration change, so it should not reference the Activity directly.
 */
public interface ScopedServices {
    /**
     * When a service implements Scoped, then it will receive a callback when the service is registered/unregistered from the scope.
     */
    public static interface Scoped {
        /**
         * Called when the object is added to the scope.
         *
         * @param scopeNode the scope node
         */
        void onEnterScope(@NonNull ScopeNode scopeNode);

        /**
         * Called when the scope is destroyed, and therefore the object is no longer in the scope.
         */
        void onExitScope();
    }

    /**
     * The {@link ServiceBinder} allows binding services to a given scope, when that scope is created for the first time.
     *
     * Please note that the service binder is only called when the scope is created, but not called if the scope already exists.
     */
    public static class ServiceBinder extends ScopeNode {
        ServiceBinder(ScopeManager scopeManager, Object key, String scopeTag, Map<String, Object> scope) {
            super(scopeManager, key, scopeTag, scope);
        }

        /**
         * Adds the service to the scope.
         *
         * @param serviceTag the tag of the service
         * @param service    the service
         */
        public void addService(@NonNull String serviceTag, @NonNull Object service) {
            //noinspection ConstantConditions
            if(service == null) {
                throw new IllegalArgumentException("Service tag cannot be null!");
            }
            //noinspection ConstantConditions
            if(service == null) {
                throw new IllegalArgumentException("The provided service should not be null!");
            }
            scope.put(serviceTag, service);
        }
    }

    void bindServices(@NonNull ServiceBinder serviceBinder);
}
