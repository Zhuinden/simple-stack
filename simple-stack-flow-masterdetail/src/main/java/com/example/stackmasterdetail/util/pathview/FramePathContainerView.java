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

package com.example.stackmasterdetail.util.pathview;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.stackmasterdetail.util.Container;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;

public class FramePathContainerView
        extends FrameLayout
        implements HandlesBack, StateChanger, Container {
    private boolean disabled;

    public FramePathContainerView(Context context) {
        super(context);
        init();
    }

    public FramePathContainerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FramePathContainerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public FramePathContainerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        if(!isInEditMode()) {
            container = createContainer();
        }
    }

    StateChanger container;

    @Override
    public StateChanger createContainer() {
        return new SimpleStateChanger(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return !disabled && super.dispatchTouchEvent(ev);
    }

    @Override
    public ViewGroup getContainerView() {
        return this;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    public void handleStateChange(@NonNull StateChange traversal, @NonNull final StateChanger.Callback callback) {
        disabled = true;
        container.handleStateChange(traversal, new StateChanger.Callback() {
            @Override
            public void stateChangeComplete() {
                callback.stateChangeComplete();
                disabled = false;
            }
        });
    }

    @Override
    public boolean onBackPressed() {
        return BackSupport.onBackPressed(getCurrentChild());
    }

    @Override
    public ViewGroup getCurrentChild() {
        return (ViewGroup) getContainerView().getChildAt(0);
    }
}
