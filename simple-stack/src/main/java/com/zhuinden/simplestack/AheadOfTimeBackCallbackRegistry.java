package com.zhuinden.simplestack;


import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

/**
 * The ahead-of-time back callback registry is the direct replacement of {@link ScopedServices.HandlesBack} when using {@link BackHandlingModel#AHEAD_OF_TIME}.
 * <p>
 * When using {@link BackHandlingModel#AHEAD_OF_TIME}, it allows using {@link #registerAheadOfTimeBackCallback(AheadOfTimeBackCallback)} and {@link #unregisterAheadOfTimeBackCallback(AheadOfTimeBackCallback)}.
 */
public class AheadOfTimeBackCallbackRegistry
    extends AheadOfTimeBackCallback {
    private final AheadOfTimeBackCallback.EnabledChangedListener childEventListener = new EnabledChangedListener() {
        @Override
        public void onEnabledChanged(boolean isEnabled) {
            if(isEnabled) {
                setEnabled(true);
                return;
            }

            for(int i = callbacks.size() - 1; i >= 0; i--) {
                AheadOfTimeBackCallback callback = callbacks.get(i);
                if(callback.isEnabled()) {
                    setEnabled(true);
                    return;
                }
            }

            setEnabled(false);
        }
    };

    private final long threadId = Thread.currentThread().getId();

    public AheadOfTimeBackCallbackRegistry() {
        super(false);
    }

    private List<AheadOfTimeBackCallback> callbacks = new ArrayList<>();

    private void checkCorrectThread() {
        if(threadId != Thread.currentThread().getId()) {
            throw new IllegalStateException(
                "Object should only be mutated on the same thread where the object was created.");
        }
    }

    /**
     * Registers an {@link AheadOfTimeBackCallback} to the {@link AheadOfTimeBackCallbackRegistry}.
     * <p>
     * When the registry contains at least 1 enabled {@link AheadOfTimeBackCallback}, it is enabled, and will dispatch back to the last-most registered enabled {@link AheadOfTimeBackCallback}.
     *
     * @param aheadOfTimeBackCallback the back callback
     */
    public void registerAheadOfTimeBackCallback(@Nonnull AheadOfTimeBackCallback aheadOfTimeBackCallback) {
        checkCorrectThread();

        if(aheadOfTimeBackCallback == null) {
            throw new IllegalArgumentException("aheadOfTimeBackCallback cannot be null!");
        }

        callbacks.add(aheadOfTimeBackCallback);

        if(aheadOfTimeBackCallback.isEnabled()) {
            setEnabled(true);
        }

        aheadOfTimeBackCallback.addEnabledChangedListener(childEventListener);
    }

    /**
     * Unregisters an {@link AheadOfTimeBackCallback} from the {@link AheadOfTimeBackCallbackRegistry}.
     * <p>
     * When the registry contains at least 1 enabled {@link AheadOfTimeBackCallback}, it is enabled, and will dispatch back to the last-most registered enabled {@link AheadOfTimeBackCallback}.
     *
     * @param aheadOfTimeBackCallback the back callback
     */
    public void unregisterAheadOfTimeBackCallback(@Nonnull AheadOfTimeBackCallback aheadOfTimeBackCallback) {
        checkCorrectThread();

        if(aheadOfTimeBackCallback == null) {
            throw new IllegalArgumentException("aheadOfTimeBackCallback cannot be null!");
        }

        aheadOfTimeBackCallback.removeEnabledChangedListener(childEventListener);

        callbacks.remove(aheadOfTimeBackCallback);
    }

    /**
     * Receive back event. This is used as a {@link AheadOfTimeBackCallback}, do not call it manually.
     */
    @Override
    public final void onBackReceived() {
        // reverse iteration is intentional here, it's not even just for when
        for(int i = callbacks.size() - 1; i >= 0; i--) {
            AheadOfTimeBackCallback callback = callbacks.get(i);

            if(callback.isEnabled()) {
                callback.onBackReceived();
                return;
            }
        }

        throw new AheadOfTimeBackProcessingContractViolationException(
            "Registry was enabled, but it contained no enabled callback. This is most definitely an error. Please report it if you see it.");
    }
}
