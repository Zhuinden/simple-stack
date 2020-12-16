package com.zhuinden.simplestack;

import javax.annotation.Nonnull;

/**
 * The {@link AsyncStateChanger} is a {@link StateChanger} that consumes {@link StateChange#isTopNewKeyEqualToPrevious()} as a no-op,
 * but still exposes {@link Callback}, expecting the handling of the state change to be asynchronous.
 *
 * Whenever a {@link StateChange} happens with a new top key, the {@link NavigationHandler} is called.
 */
public class AsyncStateChanger
        implements StateChanger {
    private final NavigationHandler navigationHandler;

    public AsyncStateChanger(@Nonnull NavigationHandler navigationHandler) {
        //noinspection ConstantConditions
        if(navigationHandler == null) {
            throw new NullPointerException("navigationHandler should not be null!");
        }
        this.navigationHandler = navigationHandler;
    }

    @Override
    public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
        if(stateChange.isTopNewKeyEqualToPrevious()) {
            completionCallback.stateChangeComplete();
            return;
        }
        navigationHandler.onNavigationEvent(stateChange, completionCallback);
    }

    /**
     * Invoked whenever a navigation action happens.
     */
    public interface NavigationHandler {
        /**
         * Triggered when a {@link StateChange} happens.
         *
         * @param stateChange the state change
         * @param completionCallback the completion callback
         */
        void onNavigationEvent(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback);
    }
}
