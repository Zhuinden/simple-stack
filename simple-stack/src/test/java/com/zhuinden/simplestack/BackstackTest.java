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
package com.zhuinden.simplestack;

import android.os.Parcelable;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Zhuinden on 2017.01.20..
 */

public class BackstackTest {
    StateChanger.Callback callback = null;
    StateChange stateChange = null;

    @Test
    public void initialKeysCanBeEmptyInSamePackage() {
        Backstack backstack = new Backstack();
    }

    @Test
    public void initialKeysShouldNotBeEmptyList() {
        try {
            Backstack backstack = new Backstack(new ArrayList<Parcelable>());
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // good!
        }
    }

    @Test
    public void initialKeysShouldNotBeNullList() {
        try {
            List<Parcelable> list = null;
            Backstack backstack = new Backstack(list);
            Assert.fail();
        } catch(NullPointerException e) {
            // good!
        }
    }

    @Test
    public void stateChangerShouldNotBeNull() {
        try {
            Backstack backstack = new Backstack(new TestKey("Hi"));
            backstack.setStateChanger(null);
            Assert.fail();
        } catch(NullPointerException e) {
            // good!
        }
    }

    @Test
    public void newHistoryShouldNotBeNull() {
        try {
            Backstack backstack = new Backstack(new TestKey("Hi"));
            backstack.setHistory(null, StateChange.FORWARD);
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // good!
        }
    }

    @Test
    public void newHistoryKeyShouldNotBeNull() {
        try {
            Backstack backstack = new Backstack(new TestKey("Hi"));
            backstack.goTo(null);
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // good!
        }
    }

    @Test
    public void goBackShouldReturnTrueDuringActiveStateChange() {
        TestKey hi = new TestKey("hi");
        Backstack backstack = new Backstack(hi, new TestKey("bye"));

        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(StateChange stateChange, Callback completionCallback) {
                callback = completionCallback;
            }
        });

        callback.stateChangeComplete();

        backstack.goTo(hi);

        assertThat(backstack.goBack()).isTrue();
    }

    @Test
    public void goBackShouldReturnFalseWithOneElement() {
        TestKey hi = new TestKey("hi");
        Backstack backstack = new Backstack(hi);

        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(StateChange stateChange, Callback completionCallback) {
                callback = completionCallback;
            }
        });

        callback.stateChangeComplete();
        assertThat(backstack.goBack()).isFalse();
    }


    @Test
    public void topPreviousStateReturnsNullDuringInitializeStateChange() {
        TestKey hi = new TestKey("hi");
        Backstack backstack = new Backstack(hi);

        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(StateChange stateChange, Callback completionCallback) {
                assertThat(stateChange.topPreviousState()).isNull();
                callback = completionCallback;
            }
        });

        callback.stateChangeComplete();
    }

    @Test
    public void topPreviousStateReturnsTop() {
        final TestKey hi = new TestKey("hi");
        final TestKey bye = new TestKey("bye");
        Backstack backstack = new Backstack(hi, bye);

        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(StateChange stateChange, Callback completionCallback) {
                if(!stateChange.getPreviousState().isEmpty()) {
                    assertThat(stateChange.topPreviousState()).isEqualTo(bye);
                }
                callback = completionCallback;
            }
        });

        callback.stateChangeComplete();

        backstack.goBack();
        callback.stateChangeComplete();
    }

    @Test
    public void pendingStateChangeCannotGoBackwards() {
        PendingStateChange pendingStateChange = new PendingStateChange(null, StateChange.REPLACE, false);
        pendingStateChange.setStatus(PendingStateChange.Status.COMPLETED);
        try {
            pendingStateChange.setStatus(PendingStateChange.Status.IN_PROGRESS);
            Assert.fail();
        } catch(IllegalStateException e) {
            // Good!
        }
    }

    @Test
    public void pendingStateChangeStatusShouldNotBeNull() {
        PendingStateChange pendingStateChange = new PendingStateChange(null, StateChange.REPLACE, false);
        try {
            pendingStateChange.setStatus(null);
            Assert.fail();
        } catch(NullPointerException e) {
            // Good!
        }
    }

    @Test
    public void forceExecuteShouldExecuteAndSecondCallbackIsSwallowed() {
        TestKey initial = new TestKey("hello");
        Backstack backstack = new Backstack(initial);

        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(StateChange stateChange, Callback completionCallback) {
                callback = completionCallback;
            }
        };
        backstack.setStateChanger(stateChanger);
        backstack.executePendingStateChange();

        assertThat(backstack.isStateChangePending()).isFalse();
        callback.stateChangeComplete();
        // no exception thrown
    }

    @Test
    public void nullChangeListenerAddShouldThrow() {
        TestKey initial = new TestKey("hello");
        Backstack backstack = new Backstack(initial);
        try {
            backstack.addCompletionListener(null);
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // OK!
        }
    }

    @Test
    public void nullChangeListenerRemoveShouldThrow() {
        TestKey initial = new TestKey("hello");
        Backstack backstack = new Backstack(initial);
        try {
            backstack.removeCompletionListener(null);
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // OK!
        }
    }

    @Test
    public void completionListenerShouldBeCalled() {
        TestKey initial = new TestKey("hello");
        Backstack backstack = new Backstack(initial);

        Backstack.CompletionListener completionListener = Mockito.mock(Backstack.CompletionListener.class);
        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(StateChange _stateChange, Callback completionCallback) {
                stateChange = _stateChange;
                callback = completionCallback;
            }
        };
        backstack.addCompletionListener(completionListener);
        backstack.setStateChanger(stateChanger);

        callback.stateChangeComplete();

        assertThat(backstack.isStateChangePending()).isFalse();
        Mockito.verify(completionListener, Mockito.only()).stateChangeCompleted(stateChange);
    }

    @Test
    public void removedCompletionListenerShouldNotBeCalled() {
        TestKey initial = new TestKey("hello");
        Backstack backstack = new Backstack(initial);

        Backstack.CompletionListener completionListener = Mockito.mock(Backstack.CompletionListener.class);
        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(StateChange _stateChange, Callback completionCallback) {
                stateChange = _stateChange;
                callback = completionCallback;
            }
        };
        backstack.addCompletionListener(completionListener);
        backstack.removeCompletionListener(completionListener);
        backstack.setStateChanger(stateChanger);

        callback.stateChangeComplete();

        assertThat(backstack.isStateChangePending()).isFalse();
        Mockito.verify(completionListener, Mockito.never()).stateChangeCompleted(stateChange);
    }
}
