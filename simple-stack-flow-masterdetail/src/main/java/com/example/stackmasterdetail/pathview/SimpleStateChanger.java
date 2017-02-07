/*
 * Copyright 2014 Square Inc.
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

package com.example.stackmasterdetail.pathview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.stackmasterdetail.util.BackstackService;
import com.example.stackmasterdetail.util.Utils;
import com.zhuinden.simplestack.BackstackDelegate;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Provides basic right-to-left transitions. Saves and restores view state.
 */
public class SimpleStateChanger
        implements StateChanger {
    private static final Map<Class, Integer> PATH_LAYOUT_CACHE = new LinkedHashMap<>();

    private ViewGroup root;
    private BackstackDelegate backstackDelegate;
    private Context baseContext;

    public SimpleStateChanger(ViewGroup root) {
        this.root = root;
        this.backstackDelegate = BackstackService.getDelegate(root.getContext());
        this.baseContext = root.getContext();
    }

    @Override
    public void handleStateChange(final StateChange stateChange, final StateChanger.Callback callback) {
        Parcelable newKey = stateChange.topNewState();
        View newView;
        Context context = stateChange.createContext(baseContext, newKey);
        int layout = getLayout(newKey);
        newView = LayoutInflater.from(context).inflate(layout, root, false);

        View previousView = null;
        if(stateChange.topPreviousState() != null) {
            previousView = root.getChildAt(0);
            backstackDelegate.persistViewToState(previousView);
        }
        backstackDelegate.restoreViewFromState(newView);

        if(previousView == null || stateChange.getDirection() == StateChange.REPLACE) {
            root.removeAllViews();
            root.addView(newView);
            backstackDelegate.clearStatesNotIn(stateChange.getNewState());
            callback.stateChangeComplete();
        } else {
            root.addView(newView);
            final View finalPreviousView = previousView;
            Utils.waitForMeasure(newView, new Utils.OnMeasuredCallback() {
                @Override
                public void onMeasured(View view, int width, int height) {
                    runAnimation(root, finalPreviousView, view, stateChange.getDirection(), new StateChanger.Callback() {
                        @Override
                        public void stateChangeComplete() {
                            root.removeView(finalPreviousView);
                            backstackDelegate.clearStatesNotIn(stateChange.getNewState());
                            callback.stateChangeComplete();
                        }
                    });
                }
            });
        }
    }

    protected int getLayout(Parcelable path) {
        Class pathType = path.getClass();
        Integer layoutResId = PATH_LAYOUT_CACHE.get(pathType);
        if(layoutResId == null) {
            Layout layout = (Layout) pathType.getAnnotation(Layout.class);
            if(layout == null) {
                throw new IllegalArgumentException(String.format("@%s annotation not found on class %s",
                        Layout.class.getSimpleName(),
                        pathType.getName()));
            }
            layoutResId = layout.value();
            PATH_LAYOUT_CACHE.put(pathType, layoutResId);
        }
        return layoutResId;
    }

    private void runAnimation(final ViewGroup container, final View from, final View to, int direction, final StateChanger.Callback callback) {
        Animator animator = createSegue(from, to, direction);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                container.removeView(from);
                callback.stateChangeComplete();
            }
        });
        animator.start();
    }

    private Animator createSegue(View from, View to, int direction) {
        boolean backward = direction == StateChange.BACKWARD;
        int fromTranslation = backward ? from.getWidth() : -from.getWidth();
        int toTranslation = backward ? -to.getWidth() : to.getWidth();

        AnimatorSet set = new AnimatorSet();

        set.play(ObjectAnimator.ofFloat(from, View.TRANSLATION_X, fromTranslation));
        set.play(ObjectAnimator.ofFloat(to, View.TRANSLATION_X, toTranslation, 0));

        return set;
    }
}
