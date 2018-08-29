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
import android.support.annotation.NonNull;
import android.view.View;

/**
 * A default segue animation.
 */
@TargetApi(11)
public final class SegueViewChangeHandler
        extends AnimatorViewChangeHandler {
    @Override
    protected Animator createAnimator(@NonNull View from, @NonNull View to, int direction) {
        int fromTranslation = (-1) * direction * from.getWidth();
        int toTranslation = direction * to.getWidth();

        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator.ofFloat(from, "translationX", fromTranslation));
        set.play(ObjectAnimator.ofFloat(to, "translationX", toTranslation, 0));
        return set;
    }

    @Override
    protected void resetPreviousViewValues(View previousView) {
        previousView.setTranslationX(0f);
    }
}
