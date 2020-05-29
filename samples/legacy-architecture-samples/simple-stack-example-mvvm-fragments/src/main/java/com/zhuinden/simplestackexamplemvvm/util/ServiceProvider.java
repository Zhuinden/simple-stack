package com.zhuinden.simplestackexamplemvvm.util;

import androidx.annotation.NonNull;

import com.zhuinden.simplestack.ScopeKey;
import com.zhuinden.simplestack.ScopedServices;
import com.zhuinden.simplestack.ServiceBinder;

public class ServiceProvider
        implements ScopedServices {
    @Override
    public void bindServices(@NonNull ServiceBinder serviceBinder) {
        Object key = serviceBinder.getKey();
        if(key instanceof HasServices) {
            ((HasServices) key).bindServices(serviceBinder);
        }
    }

    public interface HasServices
            extends ScopeKey {
        void bindServices(@NonNull ServiceBinder serviceBinder);
    }
}
