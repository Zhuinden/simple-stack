package com.zhuinden.simplestackdemo;

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


import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.zhuinden.simplestackdemo.stack.Backstack;
import com.zhuinden.simplestackdemo.stack.StateChange;
import com.zhuinden.simplestackdemo.stack.StateChanger;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.MockitoAnnotations.initMocks;

public class ReentranceTest {

    Backstack flow;
    List<Parcelable> lastStack;
    StateChanger.Callback lastCallback;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void reentrantGo() {
        StateChanger dispatcher = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange navigation, @NonNull StateChanger.Callback callback) {
                lastStack = navigation.getNewState();
                Parcelable next = navigation.topNewState();
                if(next instanceof Detail) {
                    flow.goTo(new Loading());
                } else if(next instanceof Loading) {
                    flow.goTo(new Error());
                }
                callback.stateChangeComplete();
            }
        };
        flow = new Backstack(ListBuilder.single(new Catalog()));
        flow.setStateChanger(dispatcher, Backstack.INITIALIZE);
        flow.goTo(new Detail());
        verifyHistory(lastStack, new Error(), new Loading(), new Detail(), new Catalog());
    }

    @Test
    public void reentrantGoThenBack() {
        StateChanger dispatcher = new StateChanger() {
            boolean loading = true;

            @Override
            public void handleStateChange(@NonNull StateChange navigation, @NonNull StateChanger.Callback onComplete) {
                lastStack = navigation.getNewState();
                Object next = navigation.topNewState();
                if(loading) {
                    if(next instanceof Detail) {
                        flow.goTo(new Loading());
                    } else if(next instanceof Loading) {
                        flow.goTo(new Error());
                    } else if(next instanceof Error) {
                        loading = false;
                        flow.setHistory(ListBuilder.emptyBuilder().pushAll(flow.getHistory()).removeLast().build(),
                                StateChange.Direction.BACKWARD);
                    }
                } else {
                    if(next instanceof Loading) {
                        flow.setHistory(ListBuilder.emptyBuilder().pushAll(flow.getHistory()).removeLast().build(),
                                StateChange.Direction.BACKWARD);
                    }
                }
                onComplete.stateChangeComplete();
            }
        };
        flow = new Backstack(ListBuilder.single(new Catalog()));
        flow.setStateChanger(dispatcher, Backstack.INITIALIZE);
        verifyHistory(lastStack, new Catalog());
        flow.goTo(new Detail());
        verifyHistory(lastStack, new Detail(), new Catalog());
    }

    @Test
    public void reentrantForwardThenGo() {
        Backstack flow = new Backstack(ListBuilder.single(new Catalog()));
        flow.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange traversal, @NonNull StateChanger.Callback callback) {
                lastStack = traversal.getNewState();
                Object next = traversal.topNewState();
                if(next instanceof Detail) {
                    ReentranceTest.this.flow.setHistory(ListBuilder.emptyBuilder().push(new Detail()).push(new Loading()).build(),
                            StateChange.Direction.FORWARD);
                } else if(next instanceof Loading) {
                    ReentranceTest.this.flow.goTo(new Error());
                }
                callback.stateChangeComplete();
            }
        }, Backstack.INITIALIZE);
        this.flow = flow;
        flow.goTo(new Detail());
        verifyHistory(lastStack, new Error(), new Loading(), new Detail());
    }

    @Test
    public void reentranceWaitsForCallback() {
        StateChanger dispatcher = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange traversal, @NonNull StateChanger.Callback callback) {
                lastStack = traversal.getNewState();
                lastCallback = callback;
                Object next = traversal.topNewState();
                if(next instanceof Detail) {
                    flow.goTo(new Loading());
                } else if(next instanceof Loading) {
                    flow.goTo(new Error());
                }
            }
        };
        flow = new Backstack(ListBuilder.single(new Catalog()));
        flow.setStateChanger(dispatcher, Backstack.INITIALIZE);
        lastCallback.stateChangeComplete();

        flow.goTo(new Detail());
        verifyHistory(flow.getHistory(), new Catalog());
        lastCallback.stateChangeComplete();
        verifyHistory(flow.getHistory(), new Detail(), new Catalog());
        lastCallback.stateChangeComplete();
        verifyHistory(flow.getHistory(), new Loading(), new Detail(), new Catalog());
        lastCallback.stateChangeComplete();
        verifyHistory(flow.getHistory(), new Error(), new Loading(), new Detail(), new Catalog());
    }

    @Test
    public void onCompleteThrowsIfCalledTwice() {
        flow = new Backstack(ListBuilder.single(new Catalog()));
        flow.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange traversal, @NonNull StateChanger.Callback callback) {
                lastStack = traversal.getNewState();
                lastCallback = callback;
            }
        }, Backstack.INITIALIZE);

        lastCallback.stateChangeComplete();
        try {
            lastCallback.stateChangeComplete();
        } catch(IllegalStateException e) {
            return;
        }
        fail("Second call to onComplete() should have thrown.");
    }

    @Test
    public void bootstrapTraversal() {
        flow = new Backstack(ListBuilder.single(new Catalog()));

        flow.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange traversal, @NonNull StateChanger.Callback callback) {
                lastStack = traversal.getNewState();
                callback.stateChangeComplete();
            }
        }, Backstack.INITIALIZE);

        verifyHistory(lastStack, new Catalog());
    }

    @Test
    public void pendingTraversalReplacesBootstrap() {
        final AtomicInteger dispatchCount = new AtomicInteger(0);
        flow = new Backstack(ListBuilder.single(new Catalog()));
        flow.goTo(new Detail());

        flow.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange traversal, @NonNull StateChanger.Callback callback) {
                dispatchCount.incrementAndGet();
                lastStack = traversal.getNewState();
                callback.stateChangeComplete();
            }
        }, Backstack.INITIALIZE);

        verifyHistory(lastStack, new Detail(), new Catalog());
        assertThat(dispatchCount.intValue()).isEqualTo(1);
    }

    @Test
    public void allPendingTraversalsFire() {
        flow = new Backstack(ListBuilder.single(new Catalog()));
        flow.goTo(new Loading());
        flow.goTo(new Detail());
        flow.goTo(new Error());

        flow.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange traversal, @NonNull StateChanger.Callback callback) {
                lastCallback = callback;
            }
        }, Backstack.INITIALIZE);

        lastCallback.stateChangeComplete();
        verifyHistory(flow.getHistory(), new Loading(), new Catalog());

        lastCallback.stateChangeComplete();
        verifyHistory(flow.getHistory(), new Detail(), new Loading(), new Catalog());
    }

    @Test
    public void clearingDispatcherMidTraversalPauses() {
        flow = new Backstack(ListBuilder.single(new Catalog()));

        flow.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange traversal, @NonNull StateChanger.Callback callback) {
                flow.goTo(new Loading());
                flow.removeStateChanger();
                callback.stateChangeComplete();
            }
        }, Backstack.INITIALIZE);

        verifyHistory(flow.getHistory(), new Catalog());

        flow.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange traversal, @NonNull StateChanger.Callback callback) {
                callback.stateChangeComplete();
            }
        }, Backstack.INITIALIZE);

        verifyHistory(flow.getHistory(), new Loading(), new Catalog());
    }

    @Test
    public void handleStateChangeerSetInMidFlightWaitsForBootstrap() {
        flow = new Backstack(ListBuilder.single(new Catalog()));
        flow.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange traversal, @NonNull StateChanger.Callback callback) {
                lastCallback = callback;
            }
        }, Backstack.INITIALIZE);
        flow.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange traversal, @NonNull StateChanger.Callback callback) {
                lastStack = traversal.getNewState();
                callback.stateChangeComplete();
            }
        }, Backstack.INITIALIZE);

        assertThat(lastStack).isNull();
        lastCallback.stateChangeComplete();
        verifyHistory(lastStack, new Catalog());
    }

    @Test
    public void handleStateChangeerSetInMidFlightWithBigQueueNeedsNoBootstrap() {
        final AtomicInteger secondDispatcherCount = new AtomicInteger(0);
        flow = new Backstack(ListBuilder.single(new Catalog()));
        flow.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange traversal, @NonNull StateChanger.Callback callback) {
                flow.goTo(new Detail());
                lastCallback = callback;
            }
        }, Backstack.INITIALIZE);
        flow.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange traversal, @NonNull StateChanger.Callback callback) {
                secondDispatcherCount.incrementAndGet();
                lastStack = traversal.getNewState();
                callback.stateChangeComplete();
            }
        }, Backstack.INITIALIZE);

        assertThat(lastStack).isNull();
        lastCallback.stateChangeComplete();
        verifyHistory(lastStack, new Detail(), new Catalog());
        assertThat(secondDispatcherCount.get()).isEqualTo(1);
    }

    @Test
    public void traversalsQueuedAfterDispatcherRemovedBootstrapTheNextOne() {
        final AtomicInteger secondDispatcherCount = new AtomicInteger(0);
        flow = new Backstack(ListBuilder.single(new Catalog()));

        flow.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange traversal, @NonNull StateChanger.Callback callback) {
                lastCallback = callback;
                flow.removeStateChanger();
                flow.goTo(new Loading());
            }
        }, Backstack.INITIALIZE);

        verifyHistory(flow.getHistory(), new Catalog());

        flow.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange traversal, @NonNull StateChanger.Callback callback) {
                secondDispatcherCount.incrementAndGet();
                callback.stateChangeComplete();
            }
        }, Backstack.INITIALIZE);

        assertThat(secondDispatcherCount.get()).isZero();
        lastCallback.stateChangeComplete();

        assertThat(secondDispatcherCount.get()).isEqualTo(1);
        verifyHistory(flow.getHistory(), new Loading(), new Catalog());
    }

    static class Catalog
            extends TestKey {
        Catalog() {
            super("catalog");
        }
    }

    static class Detail
            extends TestKey {
        Detail() {
            super("detail");
        }
    }

    static class Loading
            extends TestKey {
        Loading() {
            super("loading");
        }
    }

    static class Error
            extends TestKey {
        Error() {
            super("error");
        }
    }

    private void verifyHistory(List<Parcelable> history, Parcelable... keys) {
        List<Parcelable> copy = new ArrayList<>(history);
        Collections.reverse(copy);
        assertThat(copy).containsExactly(keys);
    }
}