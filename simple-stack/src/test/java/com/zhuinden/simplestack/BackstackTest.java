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

/**
 * Created by Zhuinden on 2017.01.20..
 */

public class BackstackTest {
    KeyChanger.Callback callback = null;
    KeyChange keyChange = null;

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
    public void keyChangerShouldNotBeNull() {
        try {
            Backstack backstack = new Backstack(new TestKey("Hi"));
            backstack.setKeyChanger(null);
            Assert.fail();
        } catch(NullPointerException e) {
            // good!
        }
    }

    @Test
    public void newHistoryShouldNotBeNull() {
        try {
            Backstack backstack = new Backstack(new TestKey("Hi"));
            backstack.setHistory(null, KeyChange.FORWARD);
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
    public void goBackShouldReturnTrueDuringActiveKeyChange() {
        TestKey hi = new TestKey("hi");
        Backstack backstack = new Backstack(hi, new TestKey("bye"));

        backstack.setKeyChanger(new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange keyChange, @NonNull Callback completionCallback) {
                callback = completionCallback;
            }
        });

        callback.keyChangeComplete();

        backstack.goTo(hi);

        assertThat(backstack.goBack()).isTrue();
    }

    @Test
    public void goBackShouldReturnFalseWithOneElement() {
        TestKey hi = new TestKey("hi");
        Backstack backstack = new Backstack(hi);

        backstack.setKeyChanger(new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange keyChange, @NonNull Callback completionCallback) {
                callback = completionCallback;
            }
        });

        callback.keyChangeComplete();
        assertThat(backstack.goBack()).isFalse();
    }


    @Test
    public void topPreviousStateReturnsNullDuringInitializeKeyChange() {
        TestKey hi = new TestKey("hi");
        Backstack backstack = new Backstack(hi);

        backstack.setKeyChanger(new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange keyChange, @NonNull Callback completionCallback) {
                assertThat(keyChange.topPreviousKey()).isNull();
                callback = completionCallback;
            }
        });

        callback.keyChangeComplete();
    }

    @Test
    public void topPreviousStateReturnsTop() {
        final TestKey hi = new TestKey("hi");
        final TestKey bye = new TestKey("bye");
        Backstack backstack = new Backstack(hi, bye);

        backstack.setKeyChanger(new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange keyChange, @NonNull Callback completionCallback) {
                if(!keyChange.getPreviousKeys().isEmpty()) {
                    assertThat(keyChange.topPreviousKey()).isEqualTo(bye);
                }
                callback = completionCallback;
            }
        });

        callback.keyChangeComplete();

        backstack.goBack();
        callback.keyChangeComplete();
    }

    @Test
    public void uninitializedBackstackReturnsEmptyListAsHistory() {
        final TestKey hi = new TestKey("hi");
        final TestKey bye = new TestKey("bye");

        Backstack backstack = new Backstack(hi, bye);

        assertThat(backstack.getHistory()).isEmpty();

        backstack.setKeyChanger(new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange keyChange, @NonNull Callback completionCallback) {
                completionCallback.keyChangeComplete();
            }
        });

        assertThat(backstack.getHistory()).containsExactly(hi, bye);
    }

    @Test
    public void pendingKeyChangeCannotGoBackwards() {
        PendingKeyChange pendingKeyChange = new PendingKeyChange(null, KeyChange.REPLACE, false);
        pendingKeyChange.setStatus(PendingKeyChange.Status.COMPLETED);
        try {
            pendingKeyChange.setStatus(PendingKeyChange.Status.IN_PROGRESS);
            Assert.fail();
        } catch(IllegalStateException e) {
            // Good!
        }
    }

    @Test
    public void pendingKeyChangeStatusShouldNotBeNull() {
        PendingKeyChange pendingKeyChange = new PendingKeyChange(null, KeyChange.REPLACE, false);
        try {
            pendingKeyChange.setStatus(null);
            Assert.fail();
        } catch(NullPointerException e) {
            // Good!
        }
    }

    @Test
    public void forceExecuteShouldExecuteAndSecondCallbackIsSwallowed() {
        TestKey initial = new TestKey("hello");
        Backstack backstack = new Backstack(initial);

        KeyChanger keyChanger = new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange keyChange, @NonNull Callback completionCallback) {
                callback = completionCallback;
            }
        };
        backstack.setKeyChanger(keyChanger);
        backstack.executePendingKeyChange();

        assertThat(backstack.isKeyChangePending()).isFalse();
        callback.keyChangeComplete();
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
        KeyChanger keyChanger = new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange _keyChange, @NonNull Callback completionCallback) {
                keyChange = _keyChange;
                callback = completionCallback;
            }
        };
        backstack.addCompletionListener(completionListener);
        backstack.setKeyChanger(keyChanger);

        callback.keyChangeComplete();

        assertThat(backstack.isKeyChangePending()).isFalse();
        Mockito.verify(completionListener, Mockito.only()).keyChangeCompleted(keyChange);
    }

    @Test
    public void removedCompletionListenerShouldNotBeCalled() {
        TestKey initial = new TestKey("hello");
        Backstack backstack = new Backstack(initial);

        Backstack.CompletionListener completionListener = Mockito.mock(Backstack.CompletionListener.class);
        KeyChanger keyChanger = new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange _keyChange, @NonNull Callback completionCallback) {
                keyChange = _keyChange;
                callback = completionCallback;
            }
        };
        backstack.addCompletionListener(completionListener);
        backstack.removeCompletionListener(completionListener);
        backstack.setKeyChanger(keyChanger);

        callback.keyChangeComplete();

        assertThat(backstack.isKeyChangePending()).isFalse();
        Mockito.verify(completionListener, Mockito.never()).keyChangeCompleted(keyChange);
    }

    @Test
    public void resetShouldThrowIfKeyChangeIsEnqueued() {
        TestKey initial = new TestKey("hello");
        Backstack backstack = new Backstack(initial);
        KeyChanger keyChanger = new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange _keyChange, @NonNull Callback completionCallback) {
                keyChange = _keyChange;
                callback = completionCallback;
            }
        };
        backstack.setKeyChanger(keyChanger); // initialize key change
        try {
            backstack.reset();
            Assert.fail();
        } catch(IllegalStateException e) {
            // OK!
        }
    }

    @Test
    public void resetShouldClearStackIfKeyChangeIsComplete() {
        TestKey initial = new TestKey("hello");
        TestKey other = new TestKey("other");
        Backstack backstack = new Backstack(initial);
        KeyChanger keyChanger = new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange _keyChange, @NonNull Callback completionCallback) {
                keyChange = _keyChange;
                callback = completionCallback;
            }
        };
        backstack.setKeyChanger(keyChanger);
        callback.keyChangeComplete();
        backstack.goTo(other);
        callback.keyChangeComplete();
        backstack.reset();
        assertThat(backstack.getHistory()).isEmpty();
        backstack.setKeyChanger(keyChanger);
        callback.keyChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial);
    }

    @Test
    public void replaceTopShouldThrowIfNullIsGiven() {
        TestKey initial = new TestKey("hello");
        Backstack backstack = new Backstack(initial);
        try {
            backstack.replaceTop(null, KeyChange.REPLACE);
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
        KeyChanger keyChanger = new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange _keyChange, @NonNull Callback completionCallback) {
                keyChange = _keyChange;
                callback = completionCallback;
            }
        };
        backstack.setKeyChanger(keyChanger);
        callback.keyChangeComplete();
        backstack.goTo(other);
        callback.keyChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial, other);
        backstack.replaceTop(another, KeyChange.FORWARD);
        assertThat(keyChange.getDirection()).isEqualTo(KeyChange.FORWARD);
        callback.keyChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial, another);
    }

    @Test
    public void replaceTopReentrantShouldWorkAsIntended() {
        TestKey initial = new TestKey("hello");
        TestKey other = new TestKey("other");
        TestKey another = new TestKey("another");
        TestKey yetAnother = new TestKey("yetAnother");
        Backstack backstack = new Backstack(initial);
        KeyChanger keyChanger = new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange _keyChange, @NonNull Callback completionCallback) {
                keyChange = _keyChange;
                callback = completionCallback;
            }
        };
        backstack.setKeyChanger(keyChanger);
        callback.keyChangeComplete();
        backstack.goTo(other);
        callback.keyChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial, other);

        backstack.replaceTop(another, KeyChange.BACKWARD);
        backstack.replaceTop(yetAnother, KeyChange.REPLACE);
        assertThat(keyChange.getDirection()).isEqualTo(KeyChange.BACKWARD);
        callback.keyChangeComplete();
        assertThat(keyChange.getDirection()).isEqualTo(KeyChange.REPLACE);
        callback.keyChangeComplete();
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
        KeyChanger keyChanger = new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange _keyChange, @NonNull Callback completionCallback) {
                keyChange = _keyChange;
                callback = completionCallback;
            }
        };
        backstack.setKeyChanger(keyChanger);
        callback.keyChangeComplete();
        backstack.goUp(other);
        callback.keyChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(other);
    }

    @Test
    public void goUpWithMoreElementsNotFoundParentReplacesCurrentTop() {
        TestKey initial = new TestKey("hello");
        TestKey other = new TestKey("other");
        TestKey another = new TestKey("another");

        Backstack backstack = new Backstack(initial);
        KeyChanger keyChanger = new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange _keyChange, @NonNull Callback completionCallback) {
                keyChange = _keyChange;
                callback = completionCallback;
            }
        };
        backstack.setKeyChanger(keyChanger);
        callback.keyChangeComplete();
        backstack.goTo(other);
        callback.keyChangeComplete();

        backstack.goUp(another);
        callback.keyChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial, another);
    }

    @Test
    public void goUpWithMoreElementsFoundParentGoesToParent() {
        TestKey initial = new TestKey("hello");
        TestKey other = new TestKey("other");
        Backstack backstack = new Backstack(initial);
        KeyChanger keyChanger = new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange _keyChange, @NonNull Callback completionCallback) {
                keyChange = _keyChange;
                callback = completionCallback;
            }
        };
        backstack.setKeyChanger(keyChanger);
        callback.keyChangeComplete();
        backstack.goTo(other);
        callback.keyChangeComplete();
        backstack.goUp(initial);
        callback.keyChangeComplete();
        ;
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
        KeyChanger keyChanger = new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange _keyChange, @NonNull Callback completionCallback) {
                keyChange = _keyChange;
                callback = completionCallback;
            }
        };
        backstack.setKeyChanger(keyChanger);
        callback.keyChangeComplete();
        backstack.goTo(other);
        callback.keyChangeComplete();

        backstack.goUpChain(History.single(initial));
        callback.keyChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial);
    }

    @Test
    public void goUpChainWithSingleElementWhenMorePreviousExists() {
        TestKey initial1 = new TestKey("hello1");
        TestKey initial2 = new TestKey("hello2");
        TestKey initial3 = new TestKey("hello3");
        Backstack backstack = new Backstack(initial1, initial2, initial3);
        KeyChanger keyChanger = new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange _keyChange, @NonNull Callback completionCallback) {
                keyChange = _keyChange;
                callback = completionCallback;
            }
        };
        backstack.setKeyChanger(keyChanger);
        callback.keyChangeComplete();


        backstack.goUpChain(History.of(initial1));
        callback.keyChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial1);
    }

    @Test
    public void goUpChainWithSingleElementWhenPreviousDoesNotExist() {
        TestKey initial = new TestKey("hello");
        TestKey other = new TestKey("other");
        TestKey another = new TestKey("another");
        Backstack backstack = new Backstack(initial);
        KeyChanger keyChanger = new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange _keyChange, @NonNull Callback completionCallback) {
                keyChange = _keyChange;
                callback = completionCallback;
            }
        };
        backstack.setKeyChanger(keyChanger);
        callback.keyChangeComplete();
        backstack.goTo(other);
        callback.keyChangeComplete();

        backstack.goUpChain(History.single(another));
        callback.keyChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial, another);
    }

    @Test
    public void goUpChainWithMultipleElementWhenNoneExists() {
        TestKey initial1 = new TestKey("hello1");
        TestKey initial2 = new TestKey("hello2");
        TestKey initial3 = new TestKey("hello3");
        Backstack backstack = new Backstack(initial3);
        KeyChanger keyChanger = new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange _keyChange, @NonNull Callback completionCallback) {
                keyChange = _keyChange;
                callback = completionCallback;
            }
        };
        backstack.setKeyChanger(keyChanger);
        callback.keyChangeComplete();

        backstack.goUpChain(History.of(initial1, initial2));
        callback.keyChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial1, initial2);
    }

    @Test
    public void goUpChainWithMultipleElementWhenMorePreviousExists() {
        TestKey initial1 = new TestKey("hello1");
        TestKey initial2 = new TestKey("hello2");
        TestKey initial3 = new TestKey("hello3");
        TestKey initial4 = new TestKey("hello4");
        Backstack backstack = new Backstack(initial1, initial2, initial3, initial4);
        KeyChanger keyChanger = new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange _keyChange, @NonNull Callback completionCallback) {
                keyChange = _keyChange;
                callback = completionCallback;
            }
        };
        backstack.setKeyChanger(keyChanger);
        callback.keyChangeComplete();

        backstack.goUpChain(History.of(initial1, initial2));
        callback.keyChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial1, initial2);
    }

    @Test
    public void goUpChainWithSingleElementWhenPreviousDoesNotExistWithBefore() {
        TestKey initial1 = new TestKey("hello1");
        TestKey initial2 = new TestKey("hello2");
        TestKey initial3 = new TestKey("hello3");
        TestKey initial4 = new TestKey("hello4");
        Backstack backstack = new Backstack(initial1, initial2, initial3);
        KeyChanger keyChanger = new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange _keyChange, @NonNull Callback completionCallback) {
                keyChange = _keyChange;
                callback = completionCallback;
            }
        };
        backstack.setKeyChanger(keyChanger);
        callback.keyChangeComplete();

        backstack.goUpChain(History.of(initial4));
        callback.keyChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial1, initial2, initial4);
    }


    @Test
    public void goUpChainWithMultipleElementWhenAllExistsWithBefore() {
        TestKey initial1 = new TestKey("hello1");
        TestKey initial2 = new TestKey("hello2");
        TestKey initial3 = new TestKey("hello3");
        TestKey initial4 = new TestKey("hello4");
        Backstack backstack = new Backstack(initial1, initial2, initial3, initial4);
        KeyChanger keyChanger = new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange _keyChange, @NonNull Callback completionCallback) {
                keyChange = _keyChange;
                callback = completionCallback;
            }
        };
        backstack.setKeyChanger(keyChanger);
        callback.keyChangeComplete();

        backstack.goUpChain(History.of(initial2, initial3));
        callback.keyChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial1, initial2, initial3);
    }

    @Test
    public void goUpChainWithMultipleElementWhenMorePreviousExistsWithBefore2() {
        TestKey initial1 = new TestKey("hello1");
        TestKey initial2 = new TestKey("hello2");
        TestKey initial3 = new TestKey("hello3");
        TestKey initial4 = new TestKey("hello4");
        Backstack backstack = new Backstack(initial1, initial2, initial3, initial4);
        KeyChanger keyChanger = new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange _keyChange, @NonNull Callback completionCallback) {
                keyChange = _keyChange;
                callback = completionCallback;
            }
        };
        backstack.setKeyChanger(keyChanger);
        callback.keyChangeComplete();

        backstack.goUpChain(History.of(initial1, initial2));
        callback.keyChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial1, initial2);
    }

    @Test
    public void goUpChainWithMultipleElementWhenPreviousSomeExistsWithBefore() {
        TestKey initial1 = new TestKey("hello1");
        TestKey initial2 = new TestKey("hello2");
        TestKey initial3 = new TestKey("hello3");
        TestKey initial4 = new TestKey("hello4");
        Backstack backstack = new Backstack(initial1, initial2, initial4);
        KeyChanger keyChanger = new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange _keyChange, @NonNull Callback completionCallback) {
                keyChange = _keyChange;
                callback = completionCallback;
            }
        };
        backstack.setKeyChanger(keyChanger);
        callback.keyChangeComplete();

        backstack.goUpChain(History.of(initial2, initial3));
        callback.keyChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial1, initial2, initial3);
    }

    @Test
    public void goUpChainWithMultipleElementWhenPreviousSomeExistsWithBeforeAndReordering() {
        TestKey initial1 = new TestKey("hello1");
        TestKey initial2 = new TestKey("hello2");
        TestKey initial3 = new TestKey("hello3");
        TestKey initial4 = new TestKey("hello4");
        Backstack backstack = new Backstack(initial1, initial2, initial3, initial4);
        KeyChanger keyChanger = new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange _keyChange, @NonNull Callback completionCallback) {
                keyChange = _keyChange;
                callback = completionCallback;
            }
        };
        backstack.setKeyChanger(keyChanger);
        callback.keyChangeComplete();

        backstack.goUpChain(History.of(initial2, initial1));
        callback.keyChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial2, initial1); //initial1 reordered to end
    }


    @Test
    public void goUpChainWithMultipleElementWhenPreviousMoreExistsWithBeforeAndReordering() {
        TestKey initial1 = new TestKey("hello1");
        TestKey initial2 = new TestKey("hello2");
        TestKey initial3 = new TestKey("hello3");
        TestKey initial4 = new TestKey("hello4");
        Backstack backstack = new Backstack(initial1, initial2, initial3, initial4);
        KeyChanger keyChanger = new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange _keyChange, @NonNull Callback completionCallback) {
                keyChange = _keyChange;
                callback = completionCallback;
            }
        };
        backstack.setKeyChanger(keyChanger);
        callback.keyChangeComplete();

        backstack.goUpChain(History.of(initial3, initial2, initial1));
        callback.keyChangeComplete();
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
        KeyChanger keyChanger = new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange _keyChange, @NonNull Callback completionCallback) {
                keyChange = _keyChange;
                callback = completionCallback;
            }
        };
        backstack.setKeyChanger(keyChanger);
        callback.keyChangeComplete();

        backstack.goUpChain(History.of(other1, other2));
        callback.keyChangeComplete();
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
        KeyChanger keyChanger = new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange _keyChange, @NonNull Callback completionCallback) {
                keyChange = _keyChange;
                callback = completionCallback;
            }
        };
        backstack.setKeyChanger(keyChanger);
        callback.keyChangeComplete();

        backstack.goUpChain(History.of(other3, initial2, initial4, other1, other2));
        callback.keyChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial1, other3, initial2, initial4, other1, other2);
    }

    @Test
    public void goUpChainWithMultipleElementWhenPreviousMiddleExistsWithBefore() {
        TestKey initial1 = new TestKey("hello1");
        TestKey initial2 = new TestKey("hello2");
        TestKey initial3 = new TestKey("hello3");
        TestKey initial4 = new TestKey("hello4");
        Backstack backstack = new Backstack(initial1, initial2, initial3, initial4);
        KeyChanger keyChanger = new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange _keyChange, @NonNull Callback completionCallback) {
                keyChange = _keyChange;
                callback = completionCallback;
            }
        };
        backstack.setKeyChanger(keyChanger);
        callback.keyChangeComplete();

        backstack.goUpChain(History.of(initial2, initial3));
        callback.keyChangeComplete();
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
        KeyChanger keyChanger = new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange _keyChange, @NonNull Callback completionCallback) {
                keyChange = _keyChange;
                callback = completionCallback;
            }
        };
        backstack.setKeyChanger(keyChanger);
        callback.keyChangeComplete();

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

    @Test
    public void topWorks() {
        TestKey initial1 = new TestKey("hello1");
        TestKey initial2 = new TestKey("hello2");
        TestKey initial3 = new TestKey("hello3");
        Backstack backstack = new Backstack(initial1, initial2, initial3);

        try {
            backstack.top();
            Assert.fail();
        } catch(IllegalStateException e) {
            // OK!
        }

        KeyChanger keyChanger = new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange keyChange, @NonNull Callback completionCallback) {
                if(keyChange.getPreviousKeys().isEmpty()) {
                    assertThat(keyChange.backstack().top()).isSameAs(keyChange.topNewKey());
                } else {
                    assertThat(keyChange.backstack().top()).isSameAs(keyChange.topPreviousKey());
                }
                completionCallback.keyChangeComplete();
            }
        };

        backstack.setKeyChanger(keyChanger);

        assertThat(backstack.top()).isSameAs(initial3);

        backstack.setHistory(History.of(initial1, initial2), KeyChange.REPLACE);

        assertThat(backstack.top()).isSameAs(initial2);

        backstack.removeKeyChanger();
        backstack.setKeyChanger(keyChanger);
    }

    @Test
    public void rootWorks() {
        TestKey initial1 = new TestKey("hello1");
        TestKey initial2 = new TestKey("hello2");
        TestKey initial3 = new TestKey("hello3");
        Backstack backstack = new Backstack(initial1);

        try {
            backstack.root();
            Assert.fail();
        } catch(IllegalStateException e) {
            // OK!
        }

        KeyChanger keyChanger = new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange keyChange, @NonNull Callback completionCallback) {
                if(keyChange.getPreviousKeys().isEmpty()) {
                    assertThat(keyChange.backstack().root()).isSameAs(keyChange.getNewKeys().get(0));
                } else {
                    assertThat(keyChange.backstack().root()).isSameAs(keyChange.getPreviousKeys().get(0));
                }
                completionCallback.keyChangeComplete();
            }
        };

        backstack.setKeyChanger(keyChanger);

        assertThat(backstack.root()).isSameAs(initial1);

        backstack.setHistory(History.of(initial2, initial3), KeyChange.REPLACE);

        assertThat(backstack.root()).isSameAs(initial2);

        backstack.removeKeyChanger();
        backstack.setKeyChanger(keyChanger);
    }

    @Test
    public void moveToTopWorks() {
        TestKey initial1 = new TestKey("hello1");
        TestKey initial2 = new TestKey("hello2");
        TestKey initial3 = new TestKey("hello3");
        Backstack backstack = new Backstack(initial1);

        KeyChanger keyChanger = new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange _keyChange, @NonNull Callback completionCallback) {
                keyChange = _keyChange;
                callback = completionCallback;
            }
        };
        backstack.setKeyChanger(keyChanger);
        callback.keyChangeComplete(); // initial key change

        backstack.moveToTop(initial2);
        assertThat(keyChange.getDirection()).isSameAs(KeyChange.FORWARD);
        callback.keyChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial1, initial2);

        backstack.moveToTop(initial3);
        assertThat(keyChange.getDirection()).isSameAs(KeyChange.FORWARD);
        callback.keyChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial1, initial2, initial3);

        backstack.moveToTop(initial2, true);
        assertThat(keyChange.getDirection()).isSameAs(KeyChange.REPLACE);
        callback.keyChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial1, initial3, initial2);

        backstack.moveToTop(initial1, false);
        assertThat(keyChange.getDirection()).isSameAs(KeyChange.FORWARD);
        callback.keyChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial3, initial2, initial1);
    }

    @Test
    public void jumpToRootWorks() {
        TestKey initial1 = new TestKey("hello1");
        TestKey initial2 = new TestKey("hello2");
        TestKey initial3 = new TestKey("hello3");
        Backstack backstack = new Backstack(initial1);

        KeyChanger keyChanger = new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange _keyChange, @NonNull Callback completionCallback) {
                keyChange = _keyChange;
                callback = completionCallback;
            }
        };
        backstack.setHistory(History.of(initial1, initial2, initial3), KeyChange.REPLACE);
        backstack.setKeyChanger(keyChanger);
        callback.keyChangeComplete(); // initial key change
        assertThat(backstack.getHistory()).containsExactly(initial1, initial2, initial3);

        backstack.jumpToRoot();
        assertThat(keyChange.getDirection()).isSameAs(KeyChange.BACKWARD);
        callback.keyChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial1);

        backstack.jumpToRoot(KeyChange.REPLACE);
        assertThat(keyChange.getDirection()).isSameAs(KeyChange.REPLACE);
        callback.keyChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial1);

        backstack.jumpToRoot(KeyChange.FORWARD);
        assertThat(keyChange.getDirection()).isSameAs(KeyChange.FORWARD);
        callback.keyChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial1);
    }

    @Test
    public void goUpWithMoreElementsFoundParentNoFallbackRemovesTopKeys() {
        TestKey initial = new TestKey("hello");
        TestKey other = new TestKey("other");
        TestKey another = new TestKey("another");
        TestKey boop = new TestKey("boop");

        Backstack backstack = new Backstack(initial, other, another, boop);
        KeyChanger keyChanger = new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange _keyChange, @NonNull Callback completionCallback) {
                keyChange = _keyChange;
                callback = completionCallback;
            }
        };
        backstack.setKeyChanger(keyChanger);
        callback.keyChangeComplete();
        backstack.goUp(initial, false);
        callback.keyChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial);
    }

    @Test
    public void goUpWithMoreElementsFoundParentWithFallbackRemovesLast() {
        TestKey initial = new TestKey("hello");
        TestKey other = new TestKey("other");
        TestKey another = new TestKey("another");
        TestKey boop = new TestKey("boop");

        Backstack backstack = new Backstack(initial, other, another, boop);
        KeyChanger keyChanger = new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange _keyChange, @NonNull Callback completionCallback) {
                keyChange = _keyChange;
                callback = completionCallback;
            }
        };
        backstack.setKeyChanger(keyChanger);
        callback.keyChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial, other, another, boop);

        backstack.goUp(initial, true);
        callback.keyChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial, other, another);

        backstack.goUp(initial, true);
        callback.keyChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial, other);

        backstack.goUp(initial, true);
        callback.keyChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial);

        backstack.goUp(initial, true);
        callback.keyChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial);
    }

    @Test
    public void goUpChainWithFallbackOnExactMatchWorksAsDefaultBack() {
        TestKey initial = new TestKey("hello");
        TestKey other = new TestKey("other");
        TestKey another = new TestKey("another");
        TestKey boop = new TestKey("boop");

        Backstack backstack = new Backstack(initial, other, another, boop);
        KeyChanger keyChanger = new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange _keyChange, @NonNull Callback completionCallback) {
                keyChange = _keyChange;
                callback = completionCallback;
            }
        };
        backstack.setKeyChanger(keyChanger);
        callback.keyChangeComplete();
        backstack.goUpChain(History.of(initial, other), true);
        callback.keyChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial, other, another);

        backstack.goUpChain(History.of(initial, other), true);
        callback.keyChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial, other);

        backstack.goUpChain(History.of(initial, other), true);
        callback.keyChangeComplete();
        assertThat(backstack.getHistory()).containsExactly(initial, other);
    }

    @Test
    public void getHistoryInsideKeyChangeWorks() {
        TestKey initial = new TestKey("initial");

        TestKey other1 = new TestKey("other1");
        TestKey other2 = new TestKey("other2");
        Backstack backstack = new Backstack(initial);

        KeyChanger keyChanger = new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange keyChange, @NonNull Callback completionCallback) {
                List<?> history = keyChange.backstack().getHistory();
                if(keyChange.getPreviousKeys().isEmpty()) {
                    if(!history.isEmpty()) {
                        assertThat(history).containsExactlyElementsOf(keyChange.getNewKeys());
                    }
                } else {
                    assertThat(history).containsExactlyElementsOf(keyChange.getPreviousKeys());
                }
                completionCallback.keyChangeComplete();
            }
        };

        backstack.setKeyChanger(keyChanger);

        backstack.setHistory(History.of(other1, other2), KeyChange.REPLACE);

        backstack.removeKeyChanger();

        backstack.setKeyChanger(keyChanger);
    }

    @Test
    public void illegalThreadAccessThrowsException()
            throws InterruptedException {
        TestKey testKey = new TestKey("a");

        final Backstack backstack = new Backstack(testKey);
        backstack.setKeyChanger(new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange keyChange, @NonNull Callback completionCallback) {
                completionCallback.keyChangeComplete();
            }
        });
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Throwable> ref = new AtomicReference<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    backstack.setHistory(History.of(new TestKey("b")), KeyChange.REPLACE);
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
    public void keyChangeShouldKnowIfTopNewAndPreviousAreEqual() {
        TestKey testKey = new TestKey("a");
        TestKey testKey2 = new TestKey("b");

        final Backstack backstack = new Backstack(testKey);
        backstack.setKeyChanger(new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange keyChange, @NonNull Callback completionCallback) {
                assertThat(keyChange.topNewKey().equals(keyChange.topPreviousKey())).isEqualTo(keyChange.isTopNewKeyEqualToPrevious());

                completionCallback.keyChangeComplete();
            }
        });

        backstack.goTo(testKey2);
        backstack.goBack();
        backstack.setHistory(History.of(testKey2), KeyChange.REPLACE);
    }
}
