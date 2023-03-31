package com.zhuinden.simplestack;

import java.util.ArrayList;
import java.util.List;

/**
 * Enableable callback interface that can be registered to receive back events when using {@link BackHandlingModel#AHEAD_OF_TIME}.
 */
public abstract class AheadOfTimeBackCallback {
    private final long threadId = Thread.currentThread().getId();

    private final List<EnabledChangedListener> enabledChangedListeners = new ArrayList<>();

    /**
     * Allows registering for when a {@link AheadOfTimeBackCallback}'s enabled status has changed.
     */
    public interface EnabledChangedListener {
        /**
         * Called when enabled has changed.
         *
         * @param isEnabled enabled
         */
        void onEnabledChanged(boolean isEnabled);
    }

    /**
     * Unless specified, a back callback is enabled by default.
     */
    public AheadOfTimeBackCallback(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    private boolean isEnabled;

    /**
     * Returns if the callback is currently enabled. Only enabled callbacks can receive {@link #onBackReceived()}.
     *
     * @return if the callback is enabled
     */
    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * Sets whether the callback is enabled.
     *
     * @param isEnabled if the callback should be enabled
     */
    public void setEnabled(boolean isEnabled) {
        checkCorrectThread();

        final boolean previousDisabled = this.isEnabled;

        this.isEnabled = isEnabled;

        if(previousDisabled != isEnabled) {
            notifyEnabledChangedListeners(isEnabled);
        }
    }

    private void checkCorrectThread() {
        if(threadId != Thread.currentThread().getId()) {
            throw new IllegalStateException(
                "Object should only be mutated on the same thread where the object was created.");
        }
    }

    /**
     * Add enabled changed listener. Must be called on the same thread as where the object is created.
     *
     * @param enabledChangedListener enabled changed listener
     */
    public void addEnabledChangedListener(EnabledChangedListener enabledChangedListener) {
        checkCorrectThread();

        this.enabledChangedListeners.add(enabledChangedListener);
    }

    /**
     * Remove an enabled changed listener. Must be called on the same thread as where the object is created.
     *
     * @param enabledChangedListener enabled changed listener
     */
    public void removeEnabledChangedListener(EnabledChangedListener enabledChangedListener) {
        checkCorrectThread();

        this.enabledChangedListeners.remove(enabledChangedListener);
    }

    private void notifyEnabledChangedListeners(boolean isEnabled) {
        List<EnabledChangedListener> copy = new ArrayList<>(enabledChangedListeners);
        for(EnabledChangedListener listener : copy) {
            listener.onEnabledChanged(isEnabled);
        }
    }

    /**
     * Called if this callback is registered and enabled to intercept back events.
     */
    public abstract void onBackReceived();
}
