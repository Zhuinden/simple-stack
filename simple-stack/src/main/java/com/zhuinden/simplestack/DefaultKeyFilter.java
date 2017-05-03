package com.zhuinden.simplestack;

/**
 * Created by Owner on 2017. 05. 03..
 */

import android.support.annotation.NonNull;

import java.util.List;

/**
 * The default {@link KeyFilter} which does not remove any keys, just restores all provided keys.
 */
public class DefaultKeyFilter
        implements KeyFilter {
    @Override
    @NonNull
    public List<Object> filterHistory(@NonNull List<Object> restoredKeys) {
        return restoredKeys;
    }
}
