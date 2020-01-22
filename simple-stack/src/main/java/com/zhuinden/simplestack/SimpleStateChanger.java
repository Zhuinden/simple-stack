package com.zhuinden.simplestack;

import android.support.annotation.NonNull;

/**
 * The {@link SimpleStateChanger} is a {@link StateChanger} that consumes {@link StateChange#isTopNewKeyEqualToPrevious()} as a no-op,
 * and doesn't expose {@link StateChanger.Callback}, expecting the handling of the state change to be synchronous.
 *
 * Whenever a {@link StateChange} happens with a new top key, the {@link NavigationHandler} is called.
 */
public class SimpleStateChanger
        implements StateChanger {
    private final NavigationHandler navigationHandler;

    public SimpleStateChanger(@NonNull NavigationHandler navigationHandler) {
        //noinspection ConstantConditions
        if(navigationHandler == null) {
            throw new NullPointerException("navigationHandler should not be null!");
        }
        this.navigationHandler = navigationHandler;
    }

    @Override
    public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
        if(stateChange.isTopNewKeyEqualToPrevious()) {
            completionCallback.stateChangeComplete();
            return;
        }
        navigationHandler.onNavigationEvent(stateChange);
        completionCallback.stateChangeComplete();
    }

    /**
     * Invoked whenever a navigation action happens.
     */
    public interface NavigationHandler {
        /**
         * Triggered when a {@link StateChange} happens.
         *
         * @param stateChange the state change
         */
        void onNavigationEvent(@NonNull StateChange stateChange);
    }
}
