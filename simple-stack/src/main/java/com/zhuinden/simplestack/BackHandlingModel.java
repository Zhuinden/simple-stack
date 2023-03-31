package com.zhuinden.simplestack;

/**
 * Defines the back handling model.
 * <p>
 * The active back handling model affects whether certain functions are available. For example, {@link Backstack#willHandleAheadOfTimeBack()} is only available in {@link BackHandlingModel#AHEAD_OF_TIME} mode.
 * Only enable
 */
public enum BackHandlingModel {
    /**
     * Event bubbling back model is the original, "return true if event is handled, false otherwise" approach.
     * <p>
     * Due to Android 14 changes and the introduction of the OnBackInvokedCallback, using event bubbling cannot support the new predictive back concept.
     * <p>
     * This mode is required to support {@link ScopedServices.HandlesBack}.
     */
    EVENT_BUBBLING,
    /**
     * Ahead of time back model is the new concept introduced in Android 14.
     * <p>
     * The system requires knowing ahead of time (hence the name) that the app intends to handle back events. If the app doesn't signal that it needs back, then the system does not dispatch it.
     * <p>
     * When OnBackInvokedCallback is enabled, onBackPressed stops being called. All such back processing is replaced with the propagation of whether back will be handled, see {@link AheadOfTimeWillHandleBackChangedListener} and {@link AheadOfTimeBackCallback}.
     */
    AHEAD_OF_TIME,
}
