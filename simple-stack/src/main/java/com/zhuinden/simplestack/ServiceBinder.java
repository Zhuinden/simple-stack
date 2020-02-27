package com.zhuinden.simplestack;

import javax.annotation.Nonnull;

/**
 * The {@link ServiceBinder} allows binding services to a given scope, when that scope is created for the first time.
 *
 * Please note that the service binder is only called when the scope is created, but not called if the scope already exists.
 */
public class ServiceBinder {
    private final ScopeManager scopeManager;

    private final Object key;
    private final String scopeTag;
    private final ScopeNode scope;

    ServiceBinder(ScopeManager scopeManager, Object key, String scopeTag, ScopeNode scope) {
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
    @Nonnull
    public <T> T getKey() {
        //noinspection unchecked
        return (T) key;
    }

    /**
     * Returns the scope tag this service binder belongs to.
     *
     * @return the scope tag
     */
    @Nonnull
    public final String getScopeTag() {
        return scopeTag;
    }

    /**
     * Adds the service to the scope.
     *
     * @param serviceTag the tag of the service
     * @param service    the service
     */
    public void addService(@Nonnull String serviceTag, @Nonnull Object service) {
        scope.addService(serviceTag, service);
    }

    /**
     * Returns whether the service with given service tag is in the local scope.
     *
     * @param serviceTag the service tag
     * @return if the service is in the scope
     */
    public boolean hasService(@Nonnull String serviceTag) {
        return scope.hasService(serviceTag);
    }

    /**
     * Retrieves the service from the local scope if it exists.
     *
     * @param serviceTag the service tag
     * @param <T>        the type of the service
     * @return the service
     * @throws IllegalArgumentException if the service is not in the scope
     */
    @Nonnull
    public <T> T getService(@Nonnull String serviceTag) {
        return scope.getService(serviceTag);
    }

    /**
     * Adds an alias to the service within the local scope. Callbacks are not called for aliases, but they can be found as services.
     *
     * @param alias   the alias
     * @param service the service
     */
    public void addAlias(@Nonnull String alias, @Nonnull Object service) {
        scope.addAlias(alias, service);
    }

    /**
     * Returns whether the service can be found within the currently existing active scopes.
     *
     * @param serviceTag the service tag
     * @return if the service exists in active scopes
     */
    public boolean canFindService(@Nonnull String serviceTag) {
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
    @Nonnull
    public <T> T lookupService(@Nonnull String serviceTag) {
        return scopeManager.lookupService(serviceTag);
    }

    /**
     * Returns whether the service can be found if looked up from the provided scope.
     *
     * @param scopeTag   the scope tag
     * @param serviceTag the service tag
     * @return whether the service can be looked up from the provided scope
     */
    public boolean canFindFromScope(String scopeTag, String serviceTag) {
        return scopeManager.canFindFromScope(scopeTag, serviceTag, ScopeLookupMode.ALL);
    }

    /**
     * Retrieves the service from the current scope or any of its parents, if the service exists.
     *
     * @param serviceTag the service tag
     * @param <T>        the type of the service
     * @return the service
     * @throws IllegalArgumentException if the service is not found in the scope or any of its parents
     */
    @Nonnull
    public <T> T lookupFromScope(String scopeTag, String serviceTag) {
        return scopeManager.lookupFromScope(scopeTag, serviceTag, ScopeLookupMode.ALL);
    }

    /**
     * Returns the {@link Backstack} that manages the scopes.
     *
     * @return the backstack
     */
    @Nonnull
    public Backstack getBackstack() {
        return scopeManager.getBackstack();
    }
}
