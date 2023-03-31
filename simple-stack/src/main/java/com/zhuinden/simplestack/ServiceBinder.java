/*
 * Copyright 2019 Gabor Varadi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhuinden.simplestack;

import javax.annotation.Nonnull;

/**
 * The {@link ServiceBinder} allows binding services to a given scope, when that scope is created for the first time.
 * <p>
 * Please note that the service binder is only called when the scope is created, but not called if the scope already exists.
 */
public class ServiceBinder {
    private final ScopeManager scopeManager;

    private final Backstack backstack;

    private final Object key;
    private final String scopeTag;
    private final ScopeNode scope;
    private final AheadOfTimeBackCallbackRegistry aheadOfTimeBackCallbackRegistry;

    ServiceBinder(ScopeManager scopeManager, Object key, String scopeTag, ScopeNode scope, AheadOfTimeBackCallbackRegistry aheadOfTimeBackCallbackRegistry) {
        this.scopeManager = scopeManager;

        this.backstack = scopeManager.getBackstack();

        this.key = key;
        this.scopeTag = scopeTag;
        this.scope = scope;
        this.aheadOfTimeBackCallbackRegistry = aheadOfTimeBackCallbackRegistry;
    }

    /**
     * The ahead-of-time back callback registry can be used to register ahead-of-time back events within a given scope.
     *
     * The callback can be enabled to receive back events.
     */
    @Nonnull
    public AheadOfTimeBackCallbackRegistry getAheadOfTimeBackCallbackRegistry() {
        if(backstack.getBackHandlingModel() != BackHandlingModel.AHEAD_OF_TIME) {
            throw new IllegalStateException(
                "Using the ahead-of-time back callback registry is only allowed in AHEAD_OF_TIME back handling mode.");
        }
        return aheadOfTimeBackCallbackRegistry;
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
        return backstack;
    }
}
