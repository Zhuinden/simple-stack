package com.zhuinden.simplestack;


import java.util.ArrayList;
import java.util.List;

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

    public void registerAheadOfTimeBackCallback(AheadOfTimeBackCallback aheadOfTimeBackCallback) {
        checkCorrectThread();

        callbacks.add(aheadOfTimeBackCallback);

        if(aheadOfTimeBackCallback.isEnabled()) {
            setEnabled(true);
        }

        aheadOfTimeBackCallback.addEnabledChangedListener(childEventListener);
    }

    public void unregisterAheadOfTimeCallback(AheadOfTimeBackCallback aheadOfTimeBackCallback) {
        checkCorrectThread();

        aheadOfTimeBackCallback.removeEnabledChangedListener(childEventListener);

        callbacks.remove(aheadOfTimeBackCallback);
    }

    @Override
    public void onBackReceived() {
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
