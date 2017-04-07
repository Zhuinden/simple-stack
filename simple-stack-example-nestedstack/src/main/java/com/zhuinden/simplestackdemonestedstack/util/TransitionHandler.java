package com.zhuinden.simplestackdemonestedstack.util;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.transitionseverywhere.TransitionManager;
import com.zhuinden.simplestack.navigator.ViewChangeHandler;

/**
 * Created by Zhuinden on 2017.03.27..
 */

public class TransitionHandler
        implements ViewChangeHandler {
    @Override
    public void performViewChange(@NonNull ViewGroup container, @NonNull View previousView, @NonNull View newView, int direction, @NonNull CompletionCallback completionCallback) {
        TransitionManager.beginDelayedTransition(container);
        container.removeView(previousView);
        container.addView(newView);
        completionCallback.onCompleted();
    }
}
