package com.zhuinden.simplestack;

import android.support.annotation.NonNull;

/**
 * Inheriting from {@link ScopeKey} allows defining that our key belongs to a given scope, that a scope is associated with it.
 */
public interface ScopeKey {
    @NonNull
    String getScopeTag();
}
