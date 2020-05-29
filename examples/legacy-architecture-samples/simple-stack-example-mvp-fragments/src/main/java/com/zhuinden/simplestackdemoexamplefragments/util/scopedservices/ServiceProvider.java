package com.zhuinden.simplestackdemoexamplefragments.util.scopedservices;

import androidx.annotation.NonNull;

import com.zhuinden.simplestack.ScopedServices;
import com.zhuinden.simplestack.ServiceBinder;

public class ServiceProvider implements ScopedServices {
    @Override
    public void bindServices(@NonNull ServiceBinder serviceBinder) {
        if(serviceBinder.getKey() instanceof HasServices) {
            ((HasServices) serviceBinder.getKey()).bindServices(serviceBinder);
        }
    }
}
