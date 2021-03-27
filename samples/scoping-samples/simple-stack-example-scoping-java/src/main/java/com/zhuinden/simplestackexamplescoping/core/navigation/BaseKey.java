package com.zhuinden.simplestackexamplescoping.core.navigation;

import com.zhuinden.simplestack.ServiceBinder;
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey;
import com.zhuinden.simplestackextensions.services.DefaultServiceProvider;

import javax.annotation.Nonnull;

public abstract class BaseKey
        extends DefaultFragmentKey
        implements DefaultServiceProvider.HasServices {
    @Nonnull
    @Override
    public String getFragmentTag() {
        return toString();
    }

    @Nonnull
    @Override
    public String getScopeTag() {
        return toString();
    }

    @Override
    public void bindServices(@Nonnull ServiceBinder serviceBinder) {
    }
}