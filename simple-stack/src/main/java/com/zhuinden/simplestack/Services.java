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

public class Services {
    public interface Child {
        Object parent();
    }

    public interface Composite {
        List<? extends Object> keys();
    }

    public static final class Builder
            extends Services {
        private final Map<String, Object> boundServices = new LinkedHashMap<>();
        private final Services parentServices;

        private Builder(Services parentServices, Object key) {
            super(key, parentServices, Collections.<String, Object>emptyMap());
            if(parentServices == null) {
                throw new NullPointerException("only root Services should have a null parentServices");
            }
            this.parentServices = parentServices;
        }

        @NonNull
        public Builder withService(@NonNull String serviceName, @NonNull Object service) {
            if(service == null) {
                throw new IllegalArgumentException("The provided service [" + serviceName + "] cannot be null!");
            }
            boundServices.put(serviceName, service);
            return this;
        }

        @NonNull
        Services build() {
            return new Services(getKey(), parentServices, boundServices);
        }
    }

    private final Object key;
    @Nullable
    private final Services parentServices;
    final Map<String, Object> ownedServices = new LinkedHashMap<>();

    Services(Object key, @Nullable Services parentServices, Map<String, Object> boundServices) {
        this.key = key;
        this.parentServices = parentServices;
        this.ownedServices.putAll(boundServices);
    }

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

    @NonNull
    public <T> T getKey() {
        //noinspection unchecked
        return (T) this.key;
    }

    @NonNull
    Builder extend(@NonNull Object key) {
        return new Builder(this, key);
    }
}