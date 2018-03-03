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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Zhuinden on 2017.01.20..
 */

public class BackstackTest {
    StateChanger.Callback callback = null;
    StateChange stateChange = null;

    @Test
    public void initialKeysShouldNotBeEmpty() {
        try {
            Backstack backstack = new Backstack();
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // good!
        }
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
        Backstack backstack = new Backstack(hi);

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
        Backstack backstack = new Backstack(hi);

        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
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
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
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
    public void uninitializedBackstackReturnsEmptyListAsHistory() {
        final TestKey hi = new TestKey("hi");
        final TestKey bye = new TestKey("bye");

        Backstack backstack = new Backstack(hi, bye);

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
        Backstack backstack = new Backstack(initial);

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
        Backstack backstack = new Backstack(initial);

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
    public void resetShouldThrowIfStateChangeIsEnqueued() {
        TestKey initial = new TestKey("hello");
        Backstack backstack = new Backstack(initial);
        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange _stateChange, @NonNull Callback completionCallback) {
                stateChange = _stateChange;
                callback = completionCallback;
            }
        };
        backstack.setStateChanger(stateChanger); // initialize state change
        try {
            backstack.reset();
            Assert.fail();
        } catch(IllegalStateException e) {
            // OK!
        }
    }

    @Test
    public void resetShouldClearStackIfStateChangeIsComplete() {
        TestKey initial = new TestKey("hello");
        TestKey other = new TestKey("other");
        Backstack backstack = new Backstack(initial);
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
        backstack.reset();
        assertThat(backstack.getHistory()).isEmpty();
        backstack.setStateChanger(stateChanger);
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial);
    }

    @Test
    public void replaceTopShouldThrowIfNullIsGiven() {
        TestKey initial = new TestKey("hello");
        Backstack backstack = new Backstack(initial);
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
        Backstack backstack = new Backstack(initial);
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
        Backstack backstack = new Backstack(initial);
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
        Backstack backstack = new Backstack(initial);
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
        Backstack backstack = new Backstack(initial);
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

        Backstack backstack = new Backstack(initial);
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
        Backstack backstack = new Backstack(initial);
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
        callback.stateChangeComplete();;
        assertThat(backstack.getHistory()).containsExactly(initial);
    }

    @Test
    public void goUpChainThrowsForNull() {
        TestKey initial = new TestKey("hello");
        Backstack backstack = new Backstack(initial);
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
        Backstack backstack = new Backstack(initial);
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
        Backstack backstack = new Backstack(initial);
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

        backstack.goUpChain(HistoryBuilder.single(initial));
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial);
    }

    @Test
    public void goUpChainWithSingleElementWhenMorePreviousExists() {
        TestKey initial1 = new TestKey("hello1");
        TestKey initial2 = new TestKey("hello2");
        TestKey initial3 = new TestKey("hello3");
        Backstack backstack = new Backstack(initial1, initial2, initial3);
        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange _stateChange, @NonNull Callback completionCallback) {
                stateChange = _stateChange;
                callback = completionCallback;
            }
        };
        backstack.setStateChanger(stateChanger);
        callback.stateChangeComplete();


        backstack.goUpChain(HistoryBuilder.from(initial1).build());
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial1);
    }

    @Test
    public void goUpChainWithSingleElementWhenPreviousDoesNotExist() {
        TestKey initial = new TestKey("hello");
        TestKey other = new TestKey("other");
        TestKey another = new TestKey("another");
        Backstack backstack = new Backstack(initial);
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

        backstack.goUpChain(HistoryBuilder.single(another));
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial, another);
    }

    @Test
    public void goUpChainWithMultipleElementWhenNoneExists() {
        TestKey initial1 = new TestKey("hello1");
        TestKey initial2 = new TestKey("hello2");
        TestKey initial3 = new TestKey("hello3");
        Backstack backstack = new Backstack(initial3);
        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange _stateChange, @NonNull Callback completionCallback) {
                stateChange = _stateChange;
                callback = completionCallback;
            }
        };
        backstack.setStateChanger(stateChanger);
        callback.stateChangeComplete();

        backstack.goUpChain(HistoryBuilder.from(initial1, initial2).build());
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial1, initial2);
    }

    @Test
    public void goUpChainWithMultipleElementWhenMorePreviousExists() {
        TestKey initial1 = new TestKey("hello1");
        TestKey initial2 = new TestKey("hello2");
        TestKey initial3 = new TestKey("hello3");
        TestKey initial4 = new TestKey("hello4");
        Backstack backstack = new Backstack(initial1, initial2, initial3, initial4);
        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange _stateChange, @NonNull Callback completionCallback) {
                stateChange = _stateChange;
                callback = completionCallback;
            }
        };
        backstack.setStateChanger(stateChanger);
        callback.stateChangeComplete();

        backstack.goUpChain(HistoryBuilder.from(initial1, initial2).build());
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial1, initial2);
    }

    @Test
    public void goUpChainWithSingleElementWhenPreviousDoesNotExistWithBefore() {
        TestKey initial1 = new TestKey("hello1");
        TestKey initial2 = new TestKey("hello2");
        TestKey initial3 = new TestKey("hello3");
        TestKey initial4 = new TestKey("hello4");
        Backstack backstack = new Backstack(initial1, initial2, initial3);
        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange _stateChange, @NonNull Callback completionCallback) {
                stateChange = _stateChange;
                callback = completionCallback;
            }
        };
        backstack.setStateChanger(stateChanger);
        callback.stateChangeComplete();

        backstack.goUpChain(HistoryBuilder.from(initial4).build());
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial1, initial2, initial4);
    }


    @Test
    public void goUpChainWithMultipleElementWhenAllExistsWithBefore() {
        TestKey initial1 = new TestKey("hello1");
        TestKey initial2 = new TestKey("hello2");
        TestKey initial3 = new TestKey("hello3");
        TestKey initial4 = new TestKey("hello4");
        Backstack backstack = new Backstack(initial1, initial2, initial3, initial4);
        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange _stateChange, @NonNull Callback completionCallback) {
                stateChange = _stateChange;
                callback = completionCallback;
            }
        };
        backstack.setStateChanger(stateChanger);
        callback.stateChangeComplete();

        backstack.goUpChain(HistoryBuilder.from(initial2, initial3).build());
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial1, initial2, initial3);
    }

    @Test
    public void goUpChainWithMultipleElementWhenMorePreviousExistsWithBefore2() {
        TestKey initial1 = new TestKey("hello1");
        TestKey initial2 = new TestKey("hello2");
        TestKey initial3 = new TestKey("hello3");
        TestKey initial4 = new TestKey("hello4");
        Backstack backstack = new Backstack(initial1, initial2, initial3, initial4);
        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange _stateChange, @NonNull Callback completionCallback) {
                stateChange = _stateChange;
                callback = completionCallback;
            }
        };
        backstack.setStateChanger(stateChanger);
        callback.stateChangeComplete();

        backstack.goUpChain(HistoryBuilder.from(initial1, initial2).build());
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial1, initial2);
    }

    @Test
    public void goUpChainWithMultipleElementWhenPreviousSomeExistsWithBefore() {
        TestKey initial1 = new TestKey("hello1");
        TestKey initial2 = new TestKey("hello2");
        TestKey initial3 = new TestKey("hello3");
        TestKey initial4 = new TestKey("hello4");
        Backstack backstack = new Backstack(initial1, initial2, initial4);
        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange _stateChange, @NonNull Callback completionCallback) {
                stateChange = _stateChange;
                callback = completionCallback;
            }
        };
        backstack.setStateChanger(stateChanger);
        callback.stateChangeComplete();

        backstack.goUpChain(HistoryBuilder.from(initial2, initial3).build());
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial1, initial2, initial3);
    }

    @Test
    public void goUpChainWithMultipleElementWhenPreviousSomeExistsWithBeforeAndReordering() {
        TestKey initial1 = new TestKey("hello1");
        TestKey initial2 = new TestKey("hello2");
        TestKey initial3 = new TestKey("hello3");
        TestKey initial4 = new TestKey("hello4");
        Backstack backstack = new Backstack(initial1, initial2, initial3, initial4);
        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange _stateChange, @NonNull Callback completionCallback) {
                stateChange = _stateChange;
                callback = completionCallback;
            }
        };
        backstack.setStateChanger(stateChanger);
        callback.stateChangeComplete();

        backstack.goUpChain(HistoryBuilder.from(initial2, initial1).build());
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial2, initial1); //initial1 reordered to end
    }


    @Test
    public void goUpChainWithMultipleElementWhenPreviousMoreExistsWithBeforeAndReordering() {
        TestKey initial1 = new TestKey("hello1");
        TestKey initial2 = new TestKey("hello2");
        TestKey initial3 = new TestKey("hello3");
        TestKey initial4 = new TestKey("hello4");
        Backstack backstack = new Backstack(initial1, initial2, initial3, initial4);
        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange _stateChange, @NonNull Callback completionCallback) {
                stateChange = _stateChange;
                callback = completionCallback;
            }
        };
        backstack.setStateChanger(stateChanger);
        callback.stateChangeComplete();

        backstack.goUpChain(HistoryBuilder.from(initial3, initial2, initial1).build());
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

        Backstack backstack = new Backstack(initial1, initial2, initial3, initial4);
        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange _stateChange, @NonNull Callback completionCallback) {
                stateChange = _stateChange;
                callback = completionCallback;
            }
        };
        backstack.setStateChanger(stateChanger);
        callback.stateChangeComplete();

        backstack.goUpChain(HistoryBuilder.from(other1, other2).build());
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

        Backstack backstack = new Backstack(initial1, initial2, initial3, initial4);
        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange _stateChange, @NonNull Callback completionCallback) {
                stateChange = _stateChange;
                callback = completionCallback;
            }
        };
        backstack.setStateChanger(stateChanger);
        callback.stateChangeComplete();

        backstack.goUpChain(HistoryBuilder.from(other3, initial2, initial4, other1, other2).build());
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial1, other3, initial2, initial4, other1, other2);
    }

    @Test
    public void goUpChainWithMultipleElementWhenPreviousMiddleExistsWithBefore() {
        TestKey initial1 = new TestKey("hello1");
        TestKey initial2 = new TestKey("hello2");
        TestKey initial3 = new TestKey("hello3");
        TestKey initial4 = new TestKey("hello4");
        Backstack backstack = new Backstack(initial1, initial2, initial3, initial4);
        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange _stateChange, @NonNull Callback completionCallback) {
                stateChange = _stateChange;
                callback = completionCallback;
            }
        };
        backstack.setStateChanger(stateChanger);
        callback.stateChangeComplete();

        backstack.goUpChain(HistoryBuilder.from(initial2, initial3).build());
        callback.stateChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial1, initial2, initial3);
    }

    @Test
    public void fromTopOnEmptyArrayThrows() {
        TestKey initial1 = new TestKey("hello1");
        Backstack backstack = new Backstack(initial1);
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
        Backstack backstack = new Backstack(initial1, initial2, initial3, initial4);
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
        assertThat(backstack.fromTop(-1)).isSameAs(backstack.fromTop(backstack.getHistory().size()-1));
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
}
