package com.zhuinden.simplestack;

import android.support.annotation.NonNull;

/**
 * {@link ScopedServices} allow binding services to a given scope. Scopes are identified by a "scope tag" defined by a {@link ScopeKey}.
 *
 * For each scope, the services are bound to that scope only when that scope is first created. Otherwise, they survive configuration change, and are shared between keys that belong to the same scope.
 *
 * This allows the creation of services that can contain observable data (think BehaviorRelay), which can be shared between multiple views/fragments.
 *
 * Additionally, services that implement {@link Bundleable} have their state persisted/restored across process death automatically.
 *
 * Services that implements {@link Activated} will receive callbacks for when their scope becomes the active (top-most) scope, or become inactive.
 *
 * NOTE: Think of it as configuration: it is kept across configuration change, so it should not reference the Activity directly.
 */
public interface ScopedServices {
    /**
     * When a service implements {@link Activated}, then it will receive a callback when the scope the service belongs to becomes the active (top-most) scope.
     */
    public static interface Activated {
        /**
         * Called when the explicit parent chain the service is bound to becomes the top-most scope.
         */
        void onServiceActive();

        /**
         * Called when the explicit parent chain the service is bound to is no longer the top-most scope.
         */
        void onServiceInactive();
    }

    /**
     * When a service implements {@link Registered}, then it will receive a callback when the service has been registered to at least one scope.
     */
    public static interface Registered {
        /**
         * Called when the service has been registered to any scopes, and this is the first scope it was registered to.
         */
        void onServiceRegistered();

        /**
         * Called when the service is no longer registered in any scopes.
         */
        void onServiceUnregistered();
    }

    /**
     * When a service implements {@link HandlesBack}, then it will receive a callback when back is being dispatched across the active scope chain.
     */
    public static interface HandlesBack {
        /**
         * Called when back is being dispatched.
         *
         * @return whether the service handled back.
         */
        boolean onBackEvent();
    }

    /**
     * Used to configure the services that belong to given scopes.
     *
     * @param serviceBinder the binder that allows binding services to scopes.
     */
    void bindServices(@NonNull ServiceBinder serviceBinder);
}
