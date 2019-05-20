package com.zhuinden.simplestack.helpers;

import android.support.annotation.NonNull;

import com.zhuinden.simplestack.ScopedServices;
import com.zhuinden.simplestack.ServiceBinder;

public class ServiceProvider
        implements ScopedServices {
    @Override
    public void bindServices(@NonNull ServiceBinder serviceBinder) {
        Object key = serviceBinder.getKey();
        if(key instanceof HasServices) {
            ((HasServices) key).bindServices(serviceBinder);
            return;
        }
        if(key instanceof HasParentServices) {
            ((HasParentServices) key).bindServices(serviceBinder);
            //noinspection UnnecessaryReturnStatement
            return;
        }
    }
}
