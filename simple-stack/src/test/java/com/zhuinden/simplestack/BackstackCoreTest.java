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
import android.support.annotation.NonNull;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

public class BackstackCoreTest {
    StateChanger.Callback callback = null;
    StateChange stateChange = null;

    @Test
    public void initialKeysShouldNotBeEmpty() {
        try {
            // TODO this test is testing internal behavior
            NavigationCore navigationCore = new NavigationCore();
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // good!
        }
    }

    @Test
    public void initialKeysShouldNotBeEmptyList() {
        try {
            Backstack backstack = new Backstack();
            backstack.setup(new ArrayList<Parcelable>());
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // good!
        }
    }

    @Test
    public void initialKeysShouldNotBeNullList() {
        try {
            List<Parcelable> list = null;
            Backstack backstack = new Backstack();
            backstack.setup(list);
            Assert.fail();
        } catch(NullPointerException e) {
            // good!
        }
    }

    @Test
    public void stateChangerShouldNotBeNull() {
        try {
            // TODO this test is testing internal behavior
            NavigationCore navigationCore = new NavigationCore(new TestKey("Hi"));
            navigationCore.setStateChanger(null);
            Assert.fail();
        } catch(NullPointerException e) {
            // good!
        }
    }

    @Test
    public void newHistoryShouldNotBeNull() {
        try {
            Backstack backstack = new Backstack();
            backstack.setup(History.of(new TestKey("Hi")));
            backstack.setHistory(null, StateChange.FORWARD);
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // good!
        }
    }

    @Test
    public void newHistoryKeyShouldNotBeNull() {
        try {
            Backstack backstack = new Backstack();
            backstack.setup(History.of(new TestKey("Hi")));
            backstack.goTo(null);
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // good!
        }
    }

    @Test
    public void goBackShouldReturnTrueDuringActiveStateChange() {
        TestKey hi = new TestKey("hi");
        Backstack backstack = new Backstack();
        backstack.setup(History.of(hi, new TestKey("bye")));

        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
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
        Backstack backstack = new Backstack();
        backstack.setup(History.of(hi));

        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                callback = completionCallback;
            }
        });

        callback.stateChangeComplete();
        assertThat(backstack.goBack()).isFalse();
    }


    @Test
    public void topPreviousStateReturnsNullDuringInitializeStateChange() {
        TestKey hi = new TestKey("hi");
        Backstack backstack = new Backstack();
        backstack.setup(History.of(hi));

        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                assertThat(stateChange.topPreviousKey()).isNull();
                callback = completionCallback;
            }
        });

        callback.stateChangeComplete();
    }

    @Test
    public void topPreviousStateReturnsTop() {
        final TestKey hi = new TestKey("hi");
        final TestKey bye = new TestKey("bye");
        Backstack backstack = new Backstack();
        backstack.setup(History.of(hi, bye));

        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                if(!stateChange.getPreviousKeys().isEmpty()) {
                    assertThat(stateChange.topPreviousKey()).isEqualTo(bye);
                }
                callback = completionCallback;
            }
        });

        callback.stateChangeComplete();

        backstack.goBack();
        callback.stateChangeComplete();
    }

    @Test
    public void uninitializedBackstackReturnsEmptyListAsHistory() {
        final TestKey hi = new TestKey("hi");
        final TestKey bye = new TestKey("bye");

        Backstack backstack = new Backstack();
        backstack.setup(History.of(hi, bye));

        assertThat(backstack.getHistory()).isEmpty();

        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });

        assertThat(backstack.getHistory()).containsExactly(hi, bye);
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
        Backstack backstack = new Backstack();
        backstack.setup(History.of(initial));

        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
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
        Backstack backstack = new Backstack();
        backstack.setup(History.of(initial));
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
        Backstack backstack = new Backstack();
        backstack.setup(History.of(initial));
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
        Backstack backstack = new Backstack();
        backstack.setup(History.of(initial));

        Backstack.CompletionListener completionListener = Mockito.mock(Backstack.CompletionListener.class);
        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange _stateChange, @NonNull Callback completionCallback) {
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
        Backstack backstack = new Backstack();
        backstack.setup(History.of(initial));

        Backstack.CompletionListener completionListener = Mockito.mock(Backstack.CompletionListener.class);
        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange _stateChange, @NonNull Callback completionCallback) {
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

    @Test
    public void forceClearShouldThrowIfStateChangeIsEnqueued() {
        TestKey initial = new TestKey("hello");
        Backstack backstack = new Backstack();
        backstack.setup(History.of(initial));
        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange _stateChange, @NonNull Callback completionCallback) {
                stateChange = _stateChange;
                callback = completionCallback;
            }
        };
        backstack.setStateChanger(stateChanger); // initialize state change
        try {
            backstack.forceClear();
            Assert.fail();
        } catch(IllegalStateException e) {
            // OK!
        }
    }

    @Test
    public void forceClearShouldClearStackIfStateChangeIsComplete() {
        TestKey initial = new TestKey("hello");
        TestKey other = new TestKey("other");
        Backstack backstack = new Backstack();
        backstack.setup(History.of(initial));
        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange _stateChange, @NonNull Callback completionCallback) {
                stateChange = _stateChange;
                callback = completionCallback;
            }
        };
        backstack.setStateChanger(stateChanger);
        callback.stateChangeComplete();
        backstack.goTo(other);
        callback.stateChangeComplete();
        backstack.forceClear();
        assertThat(backstack.getHistory()).isEmpty();
        backstack.setStateChanger(stateChanger);
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial);
    }

    @Test
    public void replaceTopShouldThrowIfNullIsGiven() {
        TestKey initial = new TestKey("hello");
        Backstack backstack = new Backstack();
        backstack.setup(History.of(initial));
        try {
            backstack.replaceTop(null, StateChange.REPLACE);
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // OK!
        }
    }

    @Test
    public void replaceTopShouldWorkAsIntended() {
        TestKey initial = new TestKey("hello");
        TestKey other = new TestKey("other");
        TestKey another = new TestKey("another");
        Backstack backstack = new Backstack();
        backstack.setup(History.of(initial));
        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange _stateChange, @NonNull Callback completionCallback) {
                stateChange = _stateChange;
                callback = completionCallback;
            }
        };
        backstack.setStateChanger(stateChanger);
        callback.stateChangeComplete();
        backstack.goTo(other);
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial, other);
        backstack.replaceTop(another, StateChange.FORWARD);
        assertThat(stateChange.getDirection()).isEqualTo(StateChange.FORWARD);
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial, another);
    }

    @Test
    public void replaceTopReentrantShouldWorkAsIntended() {
        TestKey initial = new TestKey("hello");
        TestKey other = new TestKey("other");
        TestKey another = new TestKey("another");
        TestKey yetAnother = new TestKey("yetAnother");
        Backstack backstack = new Backstack();
        backstack.setup(History.of(initial));
        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange _stateChange, @NonNull Callback completionCallback) {
                stateChange = _stateChange;
                callback = completionCallback;
            }
        };
        backstack.setStateChanger(stateChanger);
        callback.stateChangeComplete();
        backstack.goTo(other);
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial, other);

        backstack.replaceTop(another, StateChange.BACKWARD);
        backstack.replaceTop(yetAnother, StateChange.REPLACE);
        assertThat(stateChange.getDirection()).isEqualTo(StateChange.BACKWARD);
        callback.stateChangeComplete();
        assertThat(stateChange.getDirection()).isEqualTo(StateChange.REPLACE);
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial, yetAnother);
    }

    @Test
    public void goUpThrowsForNull() {
        TestKey initial = new TestKey("hello");
        Backstack backstack = new Backstack();
        backstack.setup(History.of(initial));
        try {
            backstack.goUp(null);
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // OK!
        }
    }

    @Test
    public void goUpReplacesTopForSingleElement() {
        TestKey initial = new TestKey("hello");
        TestKey other = new TestKey("other");
        Backstack backstack = new Backstack();
        backstack.setup(History.of(initial));
        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange _stateChange, @NonNull Callback completionCallback) {
                stateChange = _stateChange;
                callback = completionCallback;
            }
        };
        backstack.setStateChanger(stateChanger);
        callback.stateChangeComplete();
        backstack.goUp(other);
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(other);
    }

    @Test
    public void goUpWithMoreElementsNotFoundParentReplacesCurrentTop() {
        TestKey initial = new TestKey("hello");
        TestKey other = new TestKey("other");
        TestKey another = new TestKey("another");

        Backstack backstack = new Backstack();
        backstack.setup(History.of(initial));
        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange _stateChange, @NonNull Callback completionCallback) {
                stateChange = _stateChange;
                callback = completionCallback;
            }
        };
        backstack.setStateChanger(stateChanger);
        callback.stateChangeComplete();
        backstack.goTo(other);
        callback.stateChangeComplete();

        backstack.goUp(another);
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial, another);
    }

    @Test
    public void goUpWithMoreElementsFoundParentGoesToParent() {
        TestKey initial = new TestKey("hello");
        TestKey other = new TestKey("other");
        Backstack backstack = new Backstack();
        backstack.setup(History.of(initial));
        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange _stateChange, @NonNull Callback completionCallback) {
                stateChange = _stateChange;
                callback = completionCallback;
            }
        };
        backstack.setStateChanger(stateChanger);
        callback.stateChangeComplete();
        backstack.goTo(other);
        callback.stateChangeComplete();
        backstack.goUp(initial);
        callback.stateChangeComplete();
        ;
        assertThat(backstack.getHistory()).containsExactly(initial);
    }

    @Test
    public void goUpChainThrowsForNull() {
        TestKey initial = new TestKey("hello");
        Backstack backstack = new Backstack();
        backstack.setup(History.of(initial));
        try {
            backstack.goUpChain(null);
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // OK!
        }
    }

    @Test
    public void goUpChainThrowsForEmptyList() {
        TestKey initial = new TestKey("hello");
        Backstack backstack = new Backstack();
        backstack.setup(History.of(initial));
        try {
            backstack.goUpChain(new ArrayList<>());
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // OK!
        }
    }

    @Test
    public void goUpChainWithSingleElementWhenPreviousExists() {
        TestKey initial = new TestKey("hello");
        TestKey other = new TestKey("other");
        Backstack backstack = new Backstack();
        backstack.setup(History.of(initial));
        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange _stateChange, @NonNull Callback completionCallback) {
                stateChange = _stateChange;
                callback = completionCallback;
            }
        };
        backstack.setStateChanger(stateChanger);
        callback.stateChangeComplete();
        backstack.goTo(other);
        callback.stateChangeComplete();

        backstack.goUpChain(History.single(initial));
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial);
    }

    @Test
    public void goUpChainWithSingleElementWhenMorePreviousExists() {
        TestKey initial1 = new TestKey("hello1");
        TestKey initial2 = new TestKey("hello2");
        TestKey initial3 = new TestKey("hello3");
        Backstack backstack = new Backstack();
        backstack.setup(History.of(initial1, initial2, initial3));
        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange _stateChange, @NonNull Callback completionCallback) {
                stateChange = _stateChange;
                callback = completionCallback;
            }
        };
        backstack.setStateChanger(stateChanger);
        callback.stateChangeComplete();


        backstack.goUpChain(History.of(initial1));
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial1);
    }

    @Test
    public void goUpChainWithSingleElementWhenPreviousDoesNotExist() {
        TestKey initial = new TestKey("hello");
        TestKey other = new TestKey("other");
        TestKey another = new TestKey("another");
        Backstack backstack = new Backstack();
        backstack.setup(History.of(initial));
        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange _stateChange, @NonNull Callback completionCallback) {
                stateChange = _stateChange;
                callback = completionCallback;
            }
        };
        backstack.setStateChanger(stateChanger);
        callback.stateChangeComplete();
        backstack.goTo(other);
        callback.stateChangeComplete();

        backstack.goUpChain(History.single(another));
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial, another);
    }

    @Test
    public void goUpChainWithMultipleElementWhenNoneExists() {
        TestKey initial1 = new TestKey("hello1");
        TestKey initial2 = new TestKey("hello2");
        TestKey initial3 = new TestKey("hello3");
        Backstack backstack = new Backstack();
        backstack.setup(History.of(initial3));
        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange _stateChange, @NonNull Callback completionCallback) {
                stateChange = _stateChange;
                callback = completionCallback;
            }
        };
        backstack.setStateChanger(stateChanger);
        callback.stateChangeComplete();

        backstack.goUpChain(History.of(initial1, initial2));
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial1, initial2);
    }

    @Test
    public void goUpChainWithMultipleElementWhenMorePreviousExists() {
        TestKey initial1 = new TestKey("hello1");
        TestKey initial2 = new TestKey("hello2");
        TestKey initial3 = new TestKey("hello3");
        TestKey initial4 = new TestKey("hello4");
        Backstack backstack = new Backstack();
        backstack.setup(History.of(initial1, initial2, initial3, initial4));
        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange _stateChange, @NonNull Callback completionCallback) {
                stateChange = _stateChange;
                callback = completionCallback;
            }
        };
        backstack.setStateChanger(stateChanger);
        callback.stateChangeComplete();

        backstack.goUpChain(History.of(initial1, initial2));
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial1, initial2);
    }

    @Test
    public void goUpChainWithSingleElementWhenPreviousDoesNotExistWithBefore() {
        TestKey initial1 = new TestKey("hello1");
        TestKey initial2 = new TestKey("hello2");
        TestKey initial3 = new TestKey("hello3");
        TestKey initial4 = new TestKey("hello4");
        Backstack backstack = new Backstack();
        backstack.setup(History.of(initial1, initial2, initial3));
        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange _stateChange, @NonNull Callback completionCallback) {
                stateChange = _stateChange;
                callback = completionCallback;
            }
        };
        backstack.setStateChanger(stateChanger);
        callback.stateChangeComplete();

        backstack.goUpChain(History.of(initial4));
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial1, initial2, initial4);
    }


    @Test
    public void goUpChainWithMultipleElementWhenAllExistsWithBefore() {
        TestKey initial1 = new TestKey("hello1");
        TestKey initial2 = new TestKey("hello2");
        TestKey initial3 = new TestKey("hello3");
        TestKey initial4 = new TestKey("hello4");
        Backstack backstack = new Backstack();
        backstack.setup(History.of(initial1, initial2, initial3, initial4));
        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange _stateChange, @NonNull Callback completionCallback) {
                stateChange = _stateChange;
                callback = completionCallback;
            }
        };
        backstack.setStateChanger(stateChanger);
        callback.stateChangeComplete();

        backstack.goUpChain(History.of(initial2, initial3));
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial1, initial2, initial3);
    }

    @Test
    public void goUpChainWithMultipleElementWhenMorePreviousExistsWithBefore2() {
        TestKey initial1 = new TestKey("hello1");
        TestKey initial2 = new TestKey("hello2");
        TestKey initial3 = new TestKey("hello3");
        TestKey initial4 = new TestKey("hello4");
        Backstack backstack = new Backstack();
        backstack.setup(History.of(initial1, initial2, initial3, initial4));
        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange _stateChange, @NonNull Callback completionCallback) {
                stateChange = _stateChange;
                callback = completionCallback;
            }
        };
        backstack.setStateChanger(stateChanger);
        callback.stateChangeComplete();

        backstack.goUpChain(History.of(initial1, initial2));
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial1, initial2);
    }

    @Test
    public void goUpChainWithMultipleElementWhenPreviousSomeExistsWithBefore() {
        TestKey initial1 = new TestKey("hello1");
        TestKey initial2 = new TestKey("hello2");
        TestKey initial3 = new TestKey("hello3");
        TestKey initial4 = new TestKey("hello4");
        Backstack backstack = new Backstack();
        backstack.setup(History.of(initial1, initial2, initial4));
        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange _stateChange, @NonNull Callback completionCallback) {
                stateChange = _stateChange;
                callback = completionCallback;
            }
        };
        backstack.setStateChanger(stateChanger);
        callback.stateChangeComplete();

        backstack.goUpChain(History.of(initial2, initial3));
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial1, initial2, initial3);
    }

    @Test
    public void goUpChainWithMultipleElementWhenPreviousSomeExistsWithBeforeAndReordering() {
        TestKey initial1 = new TestKey("hello1");
        TestKey initial2 = new TestKey("hello2");
        TestKey initial3 = new TestKey("hello3");
        TestKey initial4 = new TestKey("hello4");
        Backstack backstack = new Backstack();
        backstack.setup(History.of(initial1, initial2, initial3, initial4));
        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange _stateChange, @NonNull Callback completionCallback) {
                stateChange = _stateChange;
                callback = completionCallback;
            }
        };
        backstack.setStateChanger(stateChanger);
        callback.stateChangeComplete();

        backstack.goUpChain(History.of(initial2, initial1));
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial2, initial1); //initial1 reordered to end
    }


    @Test
    public void goUpChainWithMultipleElementWhenPreviousMoreExistsWithBeforeAndReordering() {
        TestKey initial1 = new TestKey("hello1");
        TestKey initial2 = new TestKey("hello2");
        TestKey initial3 = new TestKey("hello3");
        TestKey initial4 = new TestKey("hello4");
        Backstack backstack = new Backstack();
        backstack.setup(History.of(initial1, initial2, initial3, initial4));
        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange _stateChange, @NonNull Callback completionCallback) {
                stateChange = _stateChange;
                callback = completionCallback;
            }
        };
        backstack.setStateChanger(stateChanger);
        callback.stateChangeComplete();

        backstack.goUpChain(History.of(initial3, initial2, initial1));
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial3, initial2, initial1); //initial1 reordered to end
    }

    @Test
    public void goUpChainWithMultipleElementWhenPreviousNoneExistWithBefore() {
        TestKey initial1 = new TestKey("hello1");
        TestKey initial2 = new TestKey("hello2");
        TestKey initial3 = new TestKey("hello3");
        TestKey initial4 = new TestKey("hello4");

        TestKey other1 = new TestKey("other1");
        TestKey other2 = new TestKey("other2");

        Backstack backstack = new Backstack();
        backstack.setup(History.of(initial1, initial2, initial3, initial4));
        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange _stateChange, @NonNull Callback completionCallback) {
                stateChange = _stateChange;
                callback = completionCallback;
            }
        };
        backstack.setStateChanger(stateChanger);
        callback.stateChangeComplete();

        backstack.goUpChain(History.of(other1, other2));
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial1, initial2, initial3, other1, other2);
    }

    @Test
    public void goUpChainWithMultipleElementWhenPreviousSomeExistWithBeforeAlsoKeepsPreviousInChain() {
        TestKey initial1 = new TestKey("hello1");
        TestKey initial2 = new TestKey("hello2");
        TestKey initial3 = new TestKey("hello3");
        TestKey initial4 = new TestKey("hello4");

        TestKey other1 = new TestKey("other1");
        TestKey other2 = new TestKey("other2");
        TestKey other3 = new TestKey("other3");

        Backstack backstack = new Backstack();
        backstack.setup(History.of(initial1, initial2, initial3, initial4));
        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange _stateChange, @NonNull Callback completionCallback) {
                stateChange = _stateChange;
                callback = completionCallback;
            }
        };
        backstack.setStateChanger(stateChanger);
        callback.stateChangeComplete();

        backstack.goUpChain(History.of(other3, initial2, initial4, other1, other2));
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial1, other3, initial2, initial4, other1, other2);
    }

    @Test
    public void goUpChainWithMultipleElementWhenPreviousMiddleExistsWithBefore() {
        TestKey initial1 = new TestKey("hello1");
        TestKey initial2 = new TestKey("hello2");
        TestKey initial3 = new TestKey("hello3");
        TestKey initial4 = new TestKey("hello4");
        Backstack backstack = new Backstack();
        backstack.setup(History.of(initial1, initial2, initial3, initial4));
        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange _stateChange, @NonNull Callback completionCallback) {
                stateChange = _stateChange;
                callback = completionCallback;
            }
        };
        backstack.setStateChanger(stateChanger);
        callback.stateChangeComplete();

        backstack.goUpChain(History.of(initial2, initial3));
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial1, initial2, initial3);
    }

    @Test
    public void fromTopOnEmptyArrayThrows() {
        TestKey initial1 = new TestKey("hello1");
        Backstack backstack = new Backstack();
        backstack.setup(History.of(initial1));
        try {
            backstack.fromTop(0);
            Assert.fail();
        } catch(IllegalStateException e) {
            // OK!
        }
    }

    @Test
    public void fromTopWorksAsExpected() {
        TestKey initial1 = new TestKey("hello1");
        TestKey initial2 = new TestKey("hello2");
        TestKey initial3 = new TestKey("hello3");
        TestKey initial4 = new TestKey("hello4");
        Backstack backstack = new Backstack();
        backstack.setup(History.of(initial1, initial2, initial3, initial4));
        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange _stateChange, @NonNull Callback completionCallback) {
                stateChange = _stateChange;
                callback = completionCallback;
            }
        };
        backstack.setStateChanger(stateChanger);
        callback.stateChangeComplete();

        assertThat(backstack.fromTop(0)).isSameAs(backstack.top());
        assertThat(backstack.fromTop(0)).isSameAs(initial4);
        assertThat(backstack.fromTop(1)).isSameAs(initial3);
        assertThat(backstack.fromTop(2)).isSameAs(initial2);
        assertThat(backstack.fromTop(3)).isSameAs(initial1);
        assertThat(backstack.fromTop(-1)).isSameAs(backstack.fromTop(backstack.getHistory().size() - 1));
        assertThat(backstack.fromTop(-1)).isSameAs(backstack.fromTop(3));
        assertThat(backstack.fromTop(-1)).isSameAs(initial1);
        assertThat(backstack.fromTop(-2)).isSameAs(initial2);
        assertThat(backstack.fromTop(-3)).isSameAs(initial3);
        assertThat(backstack.fromTop(-4)).isSameAs(initial4);
        try {
            backstack.fromTop(-5);
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // OK!
        }
        try {
            backstack.fromTop(-6);
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // OK!
        }
        try {
            backstack.fromTop(4);
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // OK!
        }

        try {
            backstack.fromTop(5);
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // OK!
        }
    }

    @Test
    public void topWorks() {
        TestKey initial1 = new TestKey("hello1");
        TestKey initial2 = new TestKey("hello2");
        TestKey initial3 = new TestKey("hello3");
        Backstack backstack = new Backstack();
        backstack.setup(History.of(initial1, initial2, initial3));

        try {
            backstack.top();
            org.junit.Assert.fail();
        } catch(IllegalStateException e) {
            // OK!
        }

        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                if(stateChange.getPreviousKeys().isEmpty()) {
                    assertThat(stateChange.getBackstack().top()).isSameAs(stateChange.topNewKey());
                } else {
                    assertThat(stateChange.getBackstack().top()).isSameAs(stateChange.topPreviousKey());
                }
                completionCallback.stateChangeComplete();
            }
        };

        backstack.setStateChanger(stateChanger);

        assertThat(backstack.top()).isSameAs(initial3);

        backstack.setHistory(History.of(initial1, initial2), StateChange.REPLACE);

        assertThat(backstack.top()).isSameAs(initial2);

        backstack.removeStateChanger();
        backstack.setStateChanger(stateChanger);
    }

    @Test
    public void rootWorks() {
        TestKey initial1 = new TestKey("hello1");
        TestKey initial2 = new TestKey("hello2");
        TestKey initial3 = new TestKey("hello3");
        Backstack backstack = new Backstack();
        backstack.setup(History.of(initial1));

        try {
            backstack.root();
            org.junit.Assert.fail();
        } catch(IllegalStateException e) {
            // OK!
        }

        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                if(stateChange.getPreviousKeys().isEmpty()) {
                    assertThat(stateChange.getBackstack().root()).isSameAs(stateChange.getNewKeys().get(0));
                } else {
                    assertThat(stateChange.getBackstack().root()).isSameAs(stateChange.getPreviousKeys().get(0));
                }
                completionCallback.stateChangeComplete();
            }
        };

        backstack.setStateChanger(stateChanger);

        assertThat(backstack.root()).isSameAs(initial1);

        backstack.setHistory(History.of(initial2, initial3), StateChange.REPLACE);

        assertThat(backstack.root()).isSameAs(initial2);

        backstack.removeStateChanger();
        backstack.setStateChanger(stateChanger);
    }

    @Test
    public void moveToTopWorks() {
        TestKey initial1 = new TestKey("hello1");
        TestKey initial2 = new TestKey("hello2");
        TestKey initial3 = new TestKey("hello3");
        Backstack backstack = new Backstack();
        backstack.setup(History.of(initial1));

        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange _stateChange, @NonNull Callback completionCallback) {
                stateChange = _stateChange;
                callback = completionCallback;
            }
        };
        backstack.setStateChanger(stateChanger);
        callback.stateChangeComplete(); // initial state change

        backstack.moveToTop(initial2);
        assertThat(stateChange.getDirection()).isSameAs(StateChange.FORWARD);
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial1, initial2);

        backstack.moveToTop(initial3);
        assertThat(stateChange.getDirection()).isSameAs(StateChange.FORWARD);
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial1, initial2, initial3);

        backstack.moveToTop(initial2, true);
        assertThat(stateChange.getDirection()).isSameAs(StateChange.REPLACE);
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial1, initial3, initial2);

        backstack.moveToTop(initial1, false);
        assertThat(stateChange.getDirection()).isSameAs(StateChange.FORWARD);
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial3, initial2, initial1);
    }

    @Test
    public void jumpToRootWorks() {
        TestKey initial1 = new TestKey("hello1");
        TestKey initial2 = new TestKey("hello2");
        TestKey initial3 = new TestKey("hello3");
        Backstack backstack = new Backstack();
        backstack.setup(History.of(initial1));

        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange _stateChange, @NonNull Callback completionCallback) {
                stateChange = _stateChange;
                callback = completionCallback;
            }
        };
        backstack.setHistory(History.of(initial1, initial2, initial3), StateChange.REPLACE);
        backstack.setStateChanger(stateChanger);
        callback.stateChangeComplete(); // initial state change
        assertThat(backstack.getHistory()).containsExactly(initial1, initial2, initial3);

        backstack.jumpToRoot();
        assertThat(stateChange.getDirection()).isSameAs(StateChange.BACKWARD);
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial1);

        backstack.jumpToRoot(StateChange.REPLACE);
        assertThat(stateChange.getDirection()).isSameAs(StateChange.REPLACE);
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial1);

        backstack.jumpToRoot(StateChange.FORWARD);
        assertThat(stateChange.getDirection()).isSameAs(StateChange.FORWARD);
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial1);
    }

    @Test
    public void goUpWithMoreElementsFoundParentNoFallbackRemovesTopKeys() {
        TestKey initial = new TestKey("hello");
        TestKey other = new TestKey("other");
        TestKey another = new TestKey("another");
        TestKey boop = new TestKey("boop");

        Backstack backstack = new Backstack();
        backstack.setup(History.of(initial, other, another, boop));
        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange _stateChange, @NonNull Callback completionCallback) {
                stateChange = _stateChange;
                callback = completionCallback;
            }
        };
        backstack.setStateChanger(stateChanger);
        callback.stateChangeComplete();
        backstack.goUp(initial, false);
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial);
    }

    @Test
    public void goUpWithMoreElementsFoundParentWithFallbackRemovesLast() {
        TestKey initial = new TestKey("hello");
        TestKey other = new TestKey("other");
        TestKey another = new TestKey("another");
        TestKey boop = new TestKey("boop");

        Backstack backstack = new Backstack();
        backstack.setup(History.of(initial, other, another, boop));
        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange _stateChange, @NonNull Callback completionCallback) {
                stateChange = _stateChange;
                callback = completionCallback;
            }
        };
        backstack.setStateChanger(stateChanger);
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial, other, another, boop);

        backstack.goUp(initial, true);
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial, other, another);

        backstack.goUp(initial, true);
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial, other);

        backstack.goUp(initial, true);
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial);

        backstack.goUp(initial, true);
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial);
    }

    @Test
    public void goUpChainWithFallbackOnExactMatchWorksAsDefaultBack() {
        TestKey initial = new TestKey("hello");
        TestKey other = new TestKey("other");
        TestKey another = new TestKey("another");
        TestKey boop = new TestKey("boop");

        Backstack backstack = new Backstack();
        backstack.setup(History.of(initial, other, another, boop));
        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange _stateChange, @NonNull Callback completionCallback) {
                stateChange = _stateChange;
                callback = completionCallback;
            }
        };
        backstack.setStateChanger(stateChanger);
        callback.stateChangeComplete();
        backstack.goUpChain(History.of(initial, other), true);
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial, other, another);

        backstack.goUpChain(History.of(initial, other), true);
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial, other);

        backstack.goUpChain(History.of(initial, other), true);
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial, other);
    }

    @Test
    public void getHistoryInsideStateChangeWorks() {
        TestKey initial = new TestKey("initial");

        TestKey other1 = new TestKey("other1");
        TestKey other2 = new TestKey("other2");

        Backstack backstack = new Backstack();
        backstack.setup(History.of(initial));

        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                List<?> history = stateChange.getBackstack().getHistory();
                if(stateChange.getPreviousKeys().isEmpty()) {
                    if(!history.isEmpty()) {
                        assertThat(history).containsExactlyElementsOf(stateChange.getNewKeys());
                    }
                } else {
                    assertThat(history).containsExactlyElementsOf(stateChange.getPreviousKeys());
                }
                completionCallback.stateChangeComplete();
            }
        };

        backstack.setStateChanger(stateChanger);

        backstack.setHistory(History.of(other1, other2), StateChange.REPLACE);

        backstack.removeStateChanger();

        backstack.setStateChanger(stateChanger);
    }

    @Test
    public void illegalThreadAccessThrowsException()
            throws InterruptedException {
        TestKey testKey = new TestKey("a");

        final Backstack backstack = new Backstack();
        backstack.setup(History.of(testKey));
        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Throwable> ref = new AtomicReference<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    backstack.setHistory(History.of(new TestKey("b")), StateChange.REPLACE);
                } catch(Exception e) {
                    ref.set(e);
                } finally {
                    latch.countDown();
                }
            }
        }).start();

        latch.await();

        assertThat(ref.get()).isInstanceOf(IllegalStateException.class);
        assertThat(ref.get().getMessage()).contains("backstack is not thread-safe");
    }

    @Test
    public void stateChangeShouldKnowIfTopNewAndPreviousAreEqual() {
        TestKey testKey = new TestKey("a");
        TestKey testKey2 = new TestKey("b");

        final Backstack backstack = new Backstack();
        backstack.setup(History.of(testKey));
        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                assertThat(stateChange.topNewKey().equals(stateChange.topPreviousKey())).isEqualTo(stateChange.isTopNewKeyEqualToPrevious());

                completionCallback.stateChangeComplete();
            }
        });

        backstack.goTo(testKey2);
        backstack.goBack();
        backstack.setHistory(History.of(testKey2), StateChange.REPLACE);
    }
}
