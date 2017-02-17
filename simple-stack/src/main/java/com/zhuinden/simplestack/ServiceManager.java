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

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class ServiceManager {
    static class RootKey implements Parcelable {
        private RootKey() {
        }

        protected RootKey(Parcel in) {
        }

        public static final Creator<RootKey> CREATOR = new Creator<RootKey>() {
            @Override
            public RootKey createFromParcel(Parcel in) {
                return new RootKey(in);
            }

            @Override
            public RootKey[] newArray(int size) {
                return new RootKey[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
        }
    }

    static final Parcelable ROOT_KEY = new RootKey() {
        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {

        }

        @Override
        public String toString() {
            return "Services.ROOT_KEY";
        }
    };

    private final Services rootServices;
    private final Map<Parcelable, ReferenceCountedServices> keyToManagedServicesMap = new LinkedHashMap<>();
    private final List<ServiceFactory> servicesFactories = new ArrayList<>();

    ServiceManager(List<ServiceFactory> servicesFactories) {
        this(servicesFactories, Collections.<String, Object>emptyMap());
    }

    ServiceManager(List<ServiceFactory> servicesFactories, Map<String, Object> rootServices) {
        this.rootServices = new Services(ROOT_KEY, null, rootServices);
        this.servicesFactories.addAll(servicesFactories);
        keyToManagedServicesMap.put(ROOT_KEY, new ReferenceCountedServices(this.rootServices));
    }

    public boolean hasServices(Parcelable key) {
        return keyToManagedServicesMap.containsKey(key);
    }

    public Services findServices(Parcelable key) {
        final ReferenceCountedServices managed = keyToManagedServicesMap.get(key);
        if(managed == null) {
            throw new IllegalStateException("No services currently exists for key " + key);
        }
        return managed.services;
    }

    void setUp(BackstackDelegate backstackDelegate, @NonNull StateChange stateChange, Parcelable key) {
        Services parent = keyToManagedServicesMap.get(ROOT_KEY).services;
        if(key instanceof Services.Child) {
            final Parcelable parentKey = ((Services.Child)key).parent();
            setUp(backstackDelegate, stateChange, parentKey);
            parent = keyToManagedServicesMap.get(parentKey).services;
        }
        ReferenceCountedServices managedService = createNonExistentManagedServicesAndIncrementUsageCount(backstackDelegate,
                stateChange, parent, key);
        parent = managedService.services;
        if(key instanceof Services.Composite) {
            buildComposite(backstackDelegate, stateChange, key, parent);
        }
    }

    private void buildComposite(BackstackDelegate backstackDelegate, StateChange stateChange, Parcelable key, Services parent) {
        Services.Composite composite = (Services.Composite) key;
        List<? extends Parcelable> children = composite.keys();
        for(int i = 0; i < children.size(); i++) {
            Parcelable child = children.get(i);
            ReferenceCountedServices managedServices = createNonExistentManagedServicesAndIncrementUsageCount(backstackDelegate,
                    stateChange, parent, child);
            if(child instanceof Services.Composite) {
                buildComposite(backstackDelegate, stateChange, child, managedServices.services);
            }
        }
    }

    void tearDown(BackstackDelegate backstackDelegate, StateChange stateChange, Parcelable key) {
        tearDown(backstackDelegate, stateChange, key, false);
    }

    private void tearDown(BackstackDelegate backstackDelegate, StateChange stateChange, Parcelable key, boolean isFromComposite) {
        if(key instanceof Services.Composite) {
            Services.Composite composite = (Services.Composite) key;
            List<? extends Parcelable> children = composite.keys();
            for(int i = children.size() - 1; i >= 0; i--) {
                tearDown(backstackDelegate, stateChange, children.get(i), true);
            }
        }
        decrementAndMaybeRemoveKey(backstackDelegate, stateChange, key);
        if(!isFromComposite && key instanceof Services.Child) {
            tearDown(backstackDelegate, stateChange, ((Services.Child) key).parent(), false);
        }
    }

    @NonNull
    private ReferenceCountedServices createNonExistentManagedServicesAndIncrementUsageCount(BackstackDelegate backstackDelegate, StateChange stateChange, @Nullable Services parentServices, Parcelable key) {
        ReferenceCountedServices node = keyToManagedServicesMap.get(key);
        if(node == null) {
            // @formatter:off
            // Bind the local key as a service.
            @SuppressWarnings("ConstantConditions")
            Services.Builder builder = parentServices.extend(key);
            // @formatter:on

            // Add any services from the factories
            for(int i = 0, size = servicesFactories.size(); i < size; i++) {
                servicesFactories.get(i).bindServices(builder);
            }
            node = new ReferenceCountedServices(builder.build());
            SavedState savedState = backstackDelegate.getSavedState(key);
            if(savedState != null && savedState.getBundle() != null) {
                Bundle bundle = savedState.getBundle().getBundle("___SERVICE_STATES");
                if(bundle != null) {
                    for(Map.Entry<String, Object> serviceEntry : node.services.ownedServices.entrySet()) {
                        if(serviceEntry.getValue() instanceof Bundleable) {
                            Bundle serviceBundle = bundle.getBundle(serviceEntry.getKey());
                            ((Bundleable) serviceEntry.getValue()).fromBundle(serviceBundle);
                        }
                    }
                }
            }
            keyToManagedServicesMap.put(key, node);
        }
        node.usageCount++;
        return node;
    }

    private boolean decrementAndMaybeRemoveKey(BackstackDelegate backstackDelegate, StateChange stateChange, Parcelable key) {
        ReferenceCountedServices node = keyToManagedServicesMap.get(key);
        if(node == null) {
            throw new IllegalStateException("Cannot remove a node that doesn't exist or has already been removed!");
        }
        node.usageCount--;
        if(key != ROOT_KEY && node.usageCount == 0) {
            if(stateChange.getNewState().contains(key)) { // TODO: THIS SHOULD CONSIDER COMPOSITE + CHILD AS WELL!!!!!!!!!!!!
                SavedState savedState = backstackDelegate.getSavedState(key);
                Bundle bundle = new Bundle();
                savedState.getBundle().putBundle("___SERVICE_STATES", bundle);
                for(Map.Entry<String, Object> serviceEntry : node.services.ownedServices.entrySet()) {
                    if(serviceEntry.getValue() instanceof Bundleable) {
                        bundle.putBundle(serviceEntry.getKey(), ((Bundleable) serviceEntry.getValue()).toBundle());
                    }
                }
            }
            int count = servicesFactories.size();
            for(int i = count - 1; i >= 0; i--) {
                servicesFactories.get(i).tearDownServices(node.services);
            }
            keyToManagedServicesMap.remove(key);
            return true;
        }
        return false;
    }

    private static final class ReferenceCountedServices {
        final Services services;

        int usageCount = 0;

        private ReferenceCountedServices(Services services) {
            this.services = services;
        }
    }

    public void dumpLogData() {
        Log.i("ServiceManager", "Services: ");
        for(Map.Entry<Parcelable, ReferenceCountedServices> entry : keyToManagedServicesMap.entrySet()) {
            Log.i("ServiceManager", "  [" + entry.getKey() + "] :: " + entry.getValue().usageCount);
        }
    }
}