package com.zhuinden.simplestack.helpers;

import com.zhuinden.simplestack.ScopeKey;
import com.zhuinden.simplestack.ServiceBinder;

public interface HasParentServices
        extends ScopeKey.Child {
    void bindServices(ServiceBinder serviceBinder);
}
