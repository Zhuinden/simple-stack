package com.zhuinden.simplestack;

/**
 * A listener with which it is possible to register for changes in whether the backstack will want to handle back events.
 * <p>
 * Can only be used with {@link BackHandlingModel#AHEAD_OF_TIME}.
 * <p>
 * Attempting to register this listener while using {@link BackHandlingModel#EVENT_BUBBLING} throws an exception.
 */
public interface AheadOfTimeWillHandleBackChangedListener {
    /**
     * Called when whether back will be handled has changed.
     *
     * @param willHandleBack if back would be handled
     */
    void willHandleBackChanged(boolean willHandleBack);
}
