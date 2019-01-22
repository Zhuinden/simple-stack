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
 * Services that implements {@link Activated} will receive callbacks for when their scope becomes the active (top-most) scope, or become inactive.
 *
 * NOTE: Think of it as configuration: it is kept across configuration change, so it should not reference the Activity directly.
 */
public interface ScopedServices {
    /**
     * When a service implements {@link Scoped}, then it will receive a callback when the service is registered/unregistered from the scope.
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
     * When a service implements {@link Activated}, then it will receive a callback when the scope the service belongs to becomes the active (top-most) scope.
     */
    public static interface Activated {
        /**
         * Called when the scope the service is bound to becomes the top-most scope.
         *
         * @param scope the tag of the scope
         */
        void onScopeActive(@NonNull String scope);

        /**
         * Called when the scope is no longer the top-most scope.
         *
         * @param scope the tag of the scope
         */
        void onScopeInactive(@NonNull String scope);
    }

    /**
     * The {@link ServiceBinder} allows binding services to a given scope, when that scope is created for the first time.
     *
     * Please note that the service binder is only called when the scope is created, but not called if the scope already exists.
     */
    public static class ServiceBinder {
        private final ScopeManager scopeManager;

        private final Object key;
        private final String scopeTag;
        private final Map<String, Object> scope;

        ServiceBinder(ScopeManager scopeManager, Object key, String scopeTag, Map<String, Object> scope) {
            this.scopeManager = scopeManager;

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
            if(serviceTag == null) {
                throw new IllegalArgumentException("Service tag cannot be null!");
            }
            //noinspection ConstantConditions
            if(service == null) {
                throw new IllegalArgumentException("The provided service should not be null!");
            }
            scope.put(serviceTag, service);
        }

        /**
         * Returns whether the service with given service tag is in the local scope.
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
         * Retrieves the service from the local scope if it exists.
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

        /**
         * Returns whether the service can be found within the currently existing active scopes.
         *
         * @param serviceTag the service tag
         * @return if the service exists in active scopes
         */
        public boolean canFind(@NonNull String serviceTag) {
            return scopeManager.canFindService(serviceTag);
        }

        /**
         * Retrieves the service from the active scopes if it exists.
         *
         * @param serviceTag the service tag
         * @param <T>        the type of the service
         * @return the service
         * @throws IllegalArgumentException if the service is not found in any active scopes
         */
        @NonNull
        public <T> T lookup(@NonNull String serviceTag) {
            return scopeManager.lookupService(serviceTag);
        }

        /**
         * Returns whether the service can be found if looked up from the provided scope.
         *
         * @param scopeTag   the scope tag
         * @param serviceTag the service tag
         * @return whether the service can be looked up from the provided scope
         */
        public boolean canFindFrom(String scopeTag, String serviceTag) {
            return scopeManager.canFindFromScope(scopeTag, serviceTag);
        }

        /**
         * Retrieves the service from the current scope or any of its parents, if the service exists.
         *
         * @param serviceTag the service tag
         * @param <T>        the type of the service
         * @return the service
         * @throws IllegalArgumentException if the service is not found in the scope or any of its parents
         */
        @NonNull
        public <T> T lookupFrom(String scopeTag, String serviceTag) {
            return scopeManager.lookupFromScope(scopeTag, serviceTag);
        }

        /**
         * Returns the {@link Backstack} that belongs to the {@link BackstackManager} that manages the scopes.
         *
         * @return the backstack
         */
        @NonNull
        public Backstack getBackstack() {
            return scopeManager.getBackstack();
        }

        /**
         * Returns the {@link BackstackManager} that manages the scopes.
         *
         * @return the backstack manager
         */
        @NonNull
        public BackstackManager getManager() {
            return scopeManager.getManager();
        }
    }

    void bindServices(@NonNull ServiceBinder serviceBinder);
}
