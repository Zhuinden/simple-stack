package com.zhuinden.simplestack.helpers;

import java.util.List;

import javax.annotation.Nonnull;

public abstract class TestKeyWithOnlyParentServices extends TestKey implements HasParentServices {
    private final List<String> parentScopes;

    public TestKeyWithOnlyParentServices(String name, List<String> parentScopes) {
        super(name);
        this.parentScopes = parentScopes;
    }

    @Nonnull
    @Override
    public List<String> getParentScopes() {
        return parentScopes;
    }
}
