package com.zhuinden.simplestack;

/*
 * Copyright 2014 Square Inc.
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

/**
 * A factory class that allows creating scoped services bound to a given key, allowing both creation, and destroy hooks.
 * Bound services that implement {@link com.zhuinden.simplestack.Bundleable} are able to persist and restore their state.
 */
public abstract class ServiceFactory {
    /**
     * Sets up any services associated with the key, and make them accessible via the context.
     * The {@link Services.Builder} extends {@link Services}, so all parent services are available.
     */
    public abstract void bindServices(@NonNull Services.Builder builder);

    /**
     * Tears down any services previously bound by {@link #bindServices}.
     */
    public void tearDownServices(@NonNull Services services) {
    }
}