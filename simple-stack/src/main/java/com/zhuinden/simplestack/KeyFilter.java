package com.zhuinden.simplestack;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * This class allows you to clear keys from your history to be restored, that the app does not need to restore.
 */
public interface KeyFilter {
    /**
     * The method used to filter the history before setting it back into the backstack.
     *
     * @param restoredKeys the keys that were originally restored
     * @return the filtered history
     */
    @NonNull
    List<Object> filterHistory(@NonNull List<Object> restoredKeys);
}
