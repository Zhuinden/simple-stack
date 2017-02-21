/*
 * Copyright 2017 Gabor Varadi
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

import android.content.Context;
import android.content.ContextWrapper;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;

/**
 * ContextWrapper for inflating views, containing the key inside it.
 * The key is accessible via {@link Backstack#getKey(Context)} or {@link ManagedContextWrapper#getKey(Context)}.
 */
class ManagedContextWrapper
        extends ContextWrapper {
    static final String TAG = "Backstack.KEY";

    LayoutInflater layoutInflater;

    final Object key;
    final Services services;

    public ManagedContextWrapper(Context base, @NonNull Object key, @NonNull Services services) {
        super(base);
        if(key == null) {
            throw new IllegalArgumentException("Key cannot be null!");
        }
        this.key = key;
        if(services == null) {
            throw new IllegalArgumentException("Services cannot be null!");
        }
        this.services = services;
    }

    @Override
    public Object getSystemService(String name) {
        if(Context.LAYOUT_INFLATER_SERVICE.equals(name)) {
            if(layoutInflater == null) {
                layoutInflater = LayoutInflater.from(getBaseContext()).cloneInContext(this);
            }
            return layoutInflater;
        } else if(TAG.equals(name)) {
            return key;
        } else {
            Object service = services.getService(name);
            if(service != null) {
                return service;
            }
        }
        return super.getSystemService(name);
    }

    /**
     * Returns the key found inside the provided context.
     *
     * @param context the key context wrapper in which the key can be found.
     * @return the key.
     */
    public static <T extends Object> T getKey(Context context) {
        // noinspection ResourceType
        Object key = context.getSystemService(TAG);
        // noinspection unchecked
        return (T) key;
    }
}
