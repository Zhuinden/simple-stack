package com.zhuinden.simplestack;

/*
 * Copyright 2016 Square Inc.
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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link Services} contain the managed services that are bound to a given key.
 * It also provides the ability to access the parents' services by name as well.
 */
public class Services {
    /**
     * Used to portray a relationship towards the previous state that it is this current state's parent.
     *
     * For example, in chain A-B-C, if B is C's parent, then if C is the top state, B's services are not yet destroyed.
     *
     * At A-B-C-D, if C is not parent of D, then both B and C are persisted and torn down.
     */
    public interface Child {
        Object parent();
    }

    /**
     * Used to portray a relationship that the current state is composed of multiple states that should all exist simultaneously.
     *
     * For example, in a bottom navigation view, 3-4 children should exist at the same time.
     *
     * Their children are represented by a {@link NestedStack} that belongs to each view.
     */
    public interface Composite {
        List<?> keys();
    }

    /**
     * A builder that is able to access all services inherited from their parent services using {@link Services#getService(String)}.
     *
     * It is used to specify what managed services exist for the current key.
     */
    public static final class Builder
            extends Services {
        private final Map<String, Object> boundServices = new LinkedHashMap<>();
        private final Services parentServices;

        private Builder(ServiceManager serviceManager, Services parentServices, Object key) {
            super(serviceManager, key, parentServices, Collections.<String, Object>emptyMap());
            if(parentServices == null) {
                throw new NullPointerException("only root Services should have a null parentServices");
            }
            this.parentServices = parentServices;
        }

        /**
         * Used to bind a managed service to the given key's services by a given name.
         *
         * @param serviceName the name of the service
         * @param service     the service
         * @return the builder
         */
        @NonNull
        public Builder withService(@NonNull String serviceName, @NonNull Object service) {
            if(serviceName == null) {
                throw new IllegalArgumentException("The provided service [" + service + "] must have a non-null name!");
            }
            if(service == null) {
                throw new IllegalArgumentException("The provided service [" + serviceName + "] cannot be null!");
            }
            boundServices.put(serviceName, service);
            return this;
        }

        @NonNull
        Services build() {
            return new Services(serviceManager, getKey(), parentServices, boundServices);
        }
    }

    protected final ServiceManager serviceManager;
    private final Object key;
    @Nullable
    private final Services parentServices;
    final Map<String, Object> ownedServices = new LinkedHashMap<>();

    Services(ServiceManager serviceManager, Object key, @Nullable Services parentServices, Map<String, Object> boundServices) {
        this.serviceManager = serviceManager;
        this.key = key;
        this.parentServices = parentServices;
        this.ownedServices.putAll(boundServices);
    }

    /**
     * Returns the managed service bound with the given name.
     * It checks both locally, and if not found, then in its parent, and so on.
     * If not found, it returns null.
     *
     * @param name the name of the service.
     * @param <T>  the type of the service.
     * @return the service, or null if not found.
     */
    @Nullable
    public <T> T getService(@NonNull String name) {
        if(ownedServices.containsKey(name)) {
            @SuppressWarnings("unchecked") //
            final T service = (T) ownedServices.get(name);
            return service;
        }
        if(parentServices != null) {
            return parentServices.getService(name);
        }
        return null;
    }

    /**
     * Returns the key this services belongs to.

     * @param <T> the type of the key.
     * @return the key.
     */
    @NonNull
    public <T> T getKey() {
        //noinspection unchecked
        return (T) this.key;
    }

    @NonNull
    Builder extend(@NonNull Object key) {
        return new Builder(serviceManager, this, key);
    }
}