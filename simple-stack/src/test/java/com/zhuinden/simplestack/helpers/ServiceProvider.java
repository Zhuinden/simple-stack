package com.zhuinden.simplestack.helpers;

import com.zhuinden.simplestack.ScopedServices;
import com.zhuinden.simplestack.ServiceBinder;

import javax.annotation.Nonnull;

public class ServiceProvider
        implements ScopedServices {
    @Override
    public void bindServices(@Nonnull ServiceBinder serviceBinder) {
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
