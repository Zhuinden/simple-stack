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
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.view.View;

import javax.annotation.Nonnull;

/**
 * A default fade animation.
 */
@TargetApi(11)
public final class FadeViewChangeHandler
        extends AnimatorViewChangeHandler {
    @Override
    protected Animator createAnimator(@Nonnull View previousView, @Nonnull View newView, int direction) {
        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator.ofFloat(previousView, "alpha", 1, 0));
        set.play(ObjectAnimator.ofFloat(newView, "alpha", 0, 1));
        return set;
    }

    @Override
    protected void resetPreviousViewValues(View previousView) {
        previousView.setAlpha(1f);
    }
}
