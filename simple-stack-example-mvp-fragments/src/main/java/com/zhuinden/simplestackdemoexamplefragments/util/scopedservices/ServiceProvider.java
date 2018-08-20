package com.zhuinden.simplestackdemoexamplefragments.util.scopedservices;

import android.support.annotation.NonNull;

import com.zhuinden.simplestack.ScopedServices;

public class ServiceProvider implements ScopedServices {
    @Override
    public void bindServices(@NonNull ServiceBinder serviceBinder) {
        if(serviceBinder.getKey() instanceof HasServices) {
            ((HasServices) serviceBinder.getKey()).bindServices(serviceBinder);
        }
    }
}
