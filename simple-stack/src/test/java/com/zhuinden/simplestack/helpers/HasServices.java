package com.zhuinden.simplestack.helpers;

import com.zhuinden.simplestack.ScopeKey;
import com.zhuinden.simplestack.ServiceBinder;

public interface HasServices
        extends ScopeKey {
    void bindServices(ServiceBinder serviceBinder);
}
