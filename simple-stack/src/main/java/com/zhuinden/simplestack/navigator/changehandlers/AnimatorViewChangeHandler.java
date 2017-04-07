/*
 * Copyright 2017 Gabor Varadi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhuinden.simplestack.navigator.changehandlers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.zhuinden.simplestack.navigator.ViewChangeHandler;

/**
 * Convenience base class to support view animations using Animator.
 */
@TargetApi(11)
public abstract class AnimatorViewChangeHandler
        implements ViewChangeHandler {
    @Override
    public void performViewChange(@NonNull final ViewGroup container, @NonNull final View previousView, @NonNull final View newView, final int direction, @NonNull final CompletionCallback completionCallback) {
        container.addView(newView);
        ViewUtils.waitForMeasure(newView, new ViewUtils.OnMeasuredCallback() {
            @Override
            public void onMeasured(View view, int width, int height) {
                runAnimation(previousView, newView, direction, new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        container.removeView(previousView);
                        completionCallback.onCompleted();
                    }
                });
            }
        });
    }

    // animation
    private void runAnimation(final View previousView, final View newView, int direction, AnimatorListenerAdapter animatorListenerAdapter) {
        Animator animator = createAnimator(previousView, newView, direction);
        animator.addListener(animatorListenerAdapter);
        animator.start();
    }

    protected abstract Animator createAnimator(@NonNull View previousView, @NonNull View newView, int direction);
}
