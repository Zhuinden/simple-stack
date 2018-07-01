package com.zhuinden.simplestack;

import android.support.annotation.NonNull;

import java.util.Map;

public interface ScopedServices {
    /**
     * When a service implements Scoped, then it will receive a callback when the service is registered/unregistered from the scope.
     */
    public static interface Scoped {
        /**
         * Called when the object is added to the scope.
         *
         * @param scope the tag of the scope
         */
        void onEnterScope(@NonNull String scope);

        /**
         * Called when the scope is destroyed, and therefore the object is no longer in the scope.
         *
         * @param scope the tag of the scope
         */
        void onExitScope(@NonNull String scope);
    }

    /**
     * The {@link ServiceBinder} allows binding services to a given scope, when that scope is created for the first time.
     *
     * Please note that the service binder is only called when the scope is created, but not called if the scope already exists.
     */
    public static class ServiceBinder {
        private final Object key;
        private final String scopeTag;
        private final Map<String, Object> scope;

        ServiceBinder(Object key, String scopeTag, Map<String, Object> scope) {
            this.key = key;
            this.scopeTag = scopeTag;
            this.scope = scope;
        }

        /**
         * Returns the key that this service binder was created for.
         *
         * @param <T> the type of the key
         * @return the key
         */
        @NonNull
        public <T> T getKey() {
            //noinspection unchecked
            return (T) key;
        }

        /**
         * Returns the scope tag this service binder belongs to.
         *
         * @return the scope tag
         */
        @NonNull
        public final String getScopeTag() {
            return scopeTag;
        }

        /**
         * Adds the service to the scope.
         *
         * @param serviceTag the tag of the service
         * @param service    the service
         */
        public void add(@NonNull String serviceTag, @NonNull Object service) {
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

        /**
         * Returns whether the service with given service tag is in the scope.
         *
         * @param serviceTag the service tag
         * @return if the service is in the scope
         */
        public boolean has(@NonNull String serviceTag) {
            if(serviceTag == null) {
                throw new IllegalArgumentException("Service tag cannot be null!");
            }
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
        public <T> T get(@NonNull String serviceTag) {
            if(serviceTag == null) {
                throw new IllegalArgumentException("Service tag cannot be null!");
            }
            if(!has(serviceTag)) {
                throw new IllegalArgumentException("The service with tag [" + serviceTag + "] was not found!");
            }
            //noinspection unchecked
            return (T) scope.get(serviceTag);
        }
    }

    void bindServices(ServiceBinder serviceBinder);
}
