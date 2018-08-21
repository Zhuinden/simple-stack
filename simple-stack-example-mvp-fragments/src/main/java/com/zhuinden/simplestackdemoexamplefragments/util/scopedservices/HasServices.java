package com.zhuinden.simplestackdemoexamplefragments.util.scopedservices;

import com.zhuinden.simplestack.ScopedServices;

public interface HasServices {
    void bindServices(ScopedServices.ServiceBinder serviceBinder);
}
