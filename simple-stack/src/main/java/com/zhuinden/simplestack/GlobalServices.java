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

import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

/**
 * Used to describe the global services registered to the global scope.
 *
 * Should be created using {@link GlobalServices#builder()}.
 */
public class GlobalServices {
    public static final String SCOPE_TAG = "__SIMPLE_STACK_INTERNAL_GLOBAL_SCOPE__";

    private final ScopeNode scope;

    ScopeNode getScope() {
        return scope;
    }

    boolean isEmpty() {
        return scope.isEmpty();
    }

    private GlobalServices(ScopeNode scope) {
        this.scope = scope;
    }

    /**
     * Returns if the global scope contains a service with the provided service tag.
     *
     * @param serviceTag the service tag
     * @return if it contains the service
     */
    public boolean hasService(@Nonnull String serviceTag) {
        return scope.hasService(serviceTag);
    }

    /**
     * Returns the service. Throws if not found.
     *
     * @param serviceTag the service tag
     * @param <T>        the type of the service
     * @return the service
     */
    @Nonnull
    public <T> T getService(@Nonnull String serviceTag) {
        return scope.getService(serviceTag);
    }

    /**
     * Returns a set of entries with the contained service tags and services.
     *
     * @return the entry set
     */
    @Nonnull
    public Set<Map.Entry<String, Object>> services() {
        return scope.services();
    }

    /**
     * Creates a builder.
     *
     * @return the builder
     */
    @Nonnull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * The builder for the global scope.
     */
    public static class Builder {
        private Builder() {
        }

        private final ScopeNode scope = new ScopeNode();

        /**
         * Adds a service to the global scope.
         *
         * @param serviceTag the service tag
         * @param service    the service
         * @return the builder
         */
        @Nonnull
        public Builder addService(@Nonnull String serviceTag, @Nonnull Object service) {
            scope.addService(serviceTag, service);
            return this;
        }

        /**
         * Adds an alias to a service in the global scope.
         *
         * @param alias   the alias
         * @param service the service
         * @return the builder
         */
        @Nonnull
        public Builder addAlias(@Nonnull String alias, @Nonnull Object service) {
            scope.addAlias(alias, service);
            return this;
        }

        /**
         * Constructs the global services.
         *
         * @return the global services
         */
        @Nonnull
        public GlobalServices build() {
            return new GlobalServices(new ScopeNode(scope));
        }
    }

    /**
     * The {@link GlobalServices.Factory} enables deferring the creation of globally scoped services to execute only when they have to be created.
     */
    public interface Factory {
        /**
         * Invoked when the global scope is created.
         *
         * @return the global services
         */
        GlobalServices create();
    }
}
