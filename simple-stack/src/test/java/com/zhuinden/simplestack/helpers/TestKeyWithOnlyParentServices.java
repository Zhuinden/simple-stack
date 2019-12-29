package com.zhuinden.simplestack.helpers;

import android.support.annotation.NonNull;

import java.util.List;

public abstract class TestKeyWithOnlyParentServices extends TestKey implements HasParentServices {
    private final List<String> parentScopes;

    public TestKeyWithOnlyParentServices(String name, List<String> parentScopes) {
        super(name);
        this.parentScopes = parentScopes;
    }

    @NonNull
    @Override
    public List<String> getParentScopes() {
        return parentScopes;
    }
}
