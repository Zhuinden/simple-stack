package com.zhuinden.simplestack;

import java.util.Map;

import javax.annotation.Nonnull;

/**
 * A default strategy that clears the state for all keys that are not found in the new state.
 */
public class DefaultStateClearStrategy
        implements Backstack.StateClearStrategy {
    @Override
    public void clearStatesNotIn(@Nonnull Map<Object, SavedState> keyStateMap, @Nonnull StateChange stateChange) {
        keyStateMap.keySet().retainAll(stateChange.getNewKeys());
    }
}
