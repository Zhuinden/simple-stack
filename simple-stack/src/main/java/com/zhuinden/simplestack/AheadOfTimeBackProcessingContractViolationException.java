package com.zhuinden.simplestack;

/**
 * An exception thrown when using {@link BackHandlingModel#AHEAD_OF_TIME}, a back event was triggered when there was no one to answer it.
 * <p>
 * In the ahead-of-time model, an event can only be dispatched when there is someone known to receive the event.
 * <p>
 * An event dispatched without someone to receive it means the system should have kept it to itself, therefore it is an invalid state.
 */
public class AheadOfTimeBackProcessingContractViolationException
    extends IllegalStateException {
    AheadOfTimeBackProcessingContractViolationException(String message) {
        super(message);
    }

    static final long serialVersionUID = -237846328746782L;
}
