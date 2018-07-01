package com.zhuinden.simplestack;

import android.support.annotation.NonNull;

import java.util.Map;

/**
 * Inheriting from {@link ScopeKey} allows defining that our key belongs to a given scope, that a scope is associated with it.
 */
public interface ScopeKey {
    /**
     * When a service implements Scoped, then it will receive a callback when the service is registered/unregistered from the scope.
     */
    interface Scoped {
        /**
         * Called when the object is added to the scope.
         *
         * @param scope the tag of the scope
         */
        void onEnterScope(String scope);

        /**
         * Called when the scope is destroyed, and therefore the object is no longer in the scope.
         *
         * @param scope the tag of the scope
         */
        void onExitScope(String scope);
    }

    /**
     * The {@link ServiceBinder} allows binding services to a given scope, when that scope is created for the first time.
     *
     * Please note that the service binder is only called when the scope is created, but not called if the scope already exists.
     */
    class ServiceBinder {
        private final String scopeTag;
        private final Map<String, Object> scope;

        ServiceBinder(String scopeTag, Map<String, Object> scope) {
            this.scopeTag = scopeTag;
            this.scope = scope;
        }

        /**
         * Adds the service to the scope.
         *
         * @param serviceTag the tag of the service
         * @param service    the service
         */
        public void add(String serviceTag, @NonNull Object service) {
            //noinspection ConstantConditions
            if(service == null) {
                throw new IllegalArgumentException("The provided service should not be null!");
            }
            scope.put(serviceTag, service);
            if(service instanceof Scoped) {
                ((Scoped) service).onEnterScope(scopeTag);
            }
        }

        /**
         * Returns whether the service with given service tag is in the scope.
         *
         * @param serviceTag the service tag
         * @return if the service is in the scope
         */
        public boolean has(String serviceTag) {
            return scope.containsKey(serviceTag);
        }

        /**
         * Retrieves the service from the scope if it exists.
         *
         * @param serviceTag the service tag
         * @param <T>        the type of the service
         * @return the service
         * @throws IllegalArgumentException if the service is not in the scope
         */
        @NonNull
        public <T> T get(String serviceTag) {
            if(!has(serviceTag)) {
                throw new IllegalArgumentException("The service with tag [" + serviceTag + "] was not found!");
            }
            //noinspection unchecked
            return (T) scope.get(serviceTag);
        }
    }

    String getScopeTag();

    void bindServices(ServiceBinder serviceBinder);
}
