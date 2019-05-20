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

import android.support.annotation.NonNull;

import java.util.Map;
import java.util.Set;

/**
 * Used to describe the global services registered to the global scope.
 *
 * Should be created using {@link GlobalServices#builder()}.
 */
public class GlobalServices {
    private final ScopeNode services;

    ScopeNode getScope() {
        return services;
    }

    boolean hasServiceOrAlias(String identifier) {
        return services.hasServiceOrAlias(identifier);
    }

    <T> T getServiceOrAlias(@NonNull String identifier) {
        return services.getServiceOrAlias(identifier);
    }

    boolean isEmpty() {
        return services.isEmpty();
    }

    private GlobalServices(ScopeNode scope) {
        this.services = scope;
    }

    /**
     * Returns if the global scope contains the provided alias.
     *
     * @param alias the alias
     * @return if it contains the alias
     */
    public boolean hasAlias(@NonNull String alias) {
        return services.hasAlias(alias);
    }

    /**
     * Returns if the global scope contains a service with the provided service tag.
     *
     * @param serviceTag the service tag
     * @return if it contains the service
     */
    public boolean hasService(@NonNull String serviceTag) {
        return services.hasService(serviceTag);
    }

    /**
     * Returns the service. Throws if not found.
     *
     * @param serviceTag the service tag
     * @param <T>        the type of the service
     * @return the service
     */
    @NonNull
    public <T> T getService(@NonNull String serviceTag) {
        return services.getService(serviceTag);
    }

    /**
     * Returns a set of entries with the contained service tags and services.
     *
     * @return the entry set
     */
    @NonNull
    public Set<Map.Entry<String, Object>> services() {
        return services.services();
    }

    /**
     * Creates a builder.
     *
     * @return the builder
     */
    @NonNull
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
        @NonNull
        public Builder addService(@NonNull String serviceTag, @NonNull Object service) {
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
        @NonNull
        public Builder addAlias(@NonNull String alias, @NonNull Object service) {
            scope.addAlias(alias, service);
            return this;
        }

        /**
         * Constructs the global services.
         *
         * @return the global services
         */
        @NonNull
        public GlobalServices build() {
            return new GlobalServices(new ScopeNode(scope));
        }
    }
}
