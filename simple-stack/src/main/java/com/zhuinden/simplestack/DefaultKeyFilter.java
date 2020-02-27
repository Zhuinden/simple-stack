package com.zhuinden.simplestack;

/**
 * Created by Owner on 2017. 05. 03..
 */

import java.util.List;

import javax.annotation.Nonnull;

/**
 * The default {@link KeyFilter} which does not remove any keys, just restores all provided keys.
 */
public class DefaultKeyFilter
        implements KeyFilter {
    @Override
    @Nonnull
    public List<Object> filterHistory(@Nonnull List<Object> restoredKeys) {
        return restoredKeys;
    }
}
