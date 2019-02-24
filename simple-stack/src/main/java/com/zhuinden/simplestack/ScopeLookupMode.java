package com.zhuinden.simplestack;

/**
 * Specifies the mode used for looking up services within the scopes.
 *
 * Allows customizing whether both implicit and explicit parents are used, or only explicit parents.
 */
public enum ScopeLookupMode {
    ALL,
    EXPLICIT
}
