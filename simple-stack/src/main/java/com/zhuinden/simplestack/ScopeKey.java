package com.zhuinden.simplestack;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Inheriting from {@link ScopeKey} allows defining that our key belongs to a given scope, that a scope is associated with it.
 */
public interface ScopeKey {
    /**
     * Defines the tag of the scope this key defines the existence of.
     *
     * @return the tag of the scope
     */
    @NonNull
    String getScopeTag();

    /**
     * Inheriting from {@link Child} enables defining an explicit parent hierarchy, thus ensuring that
     * even if the scopes are not defined by the tags of any existing keys in the {@link Backstack}'s current {@link History},
     * the scopes will still be created, bound and shall exist.
     *
     * During {@link BackstackManager#lookupService(String)}, the explicit parents are traversed first, and implicit parents second. Implicit scope inheritance means that the previous keys' scopes will be traversed as well.
     *
     * If a {@link Child} is the top-most scope, then its explicit scopes are activated as well, so their services have {@link com.zhuinden.simplestack.ScopedServices.Activated#onScopeActive(String)} called if they implement {@link com.zhuinden.simplestack.ScopedServices.Activated}.
     */
    public interface Child {
        /**
         * Defines the hierarchy of the parent scope that ought to exist as explicit parents of this key.
         *
         * The order of the items matters: the top-most scope is the first, the bottom-most parent is the last.
         *
         * @return the list of scope tags that ought to serve as the key's hierarchy of explicit parents
         */
        @NonNull
        List<String> getParentScopes();
    }
}
