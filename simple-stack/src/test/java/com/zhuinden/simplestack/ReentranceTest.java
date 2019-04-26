package com.zhuinden.simplestack;

/*
 * Copyright 2014 Square Inc.
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


import android.os.Parcelable;
import android.support.annotation.NonNull;

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
    List<Object> lastStack;
    KeyChanger.Callback lastCallback;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void reentrantGo() {
        KeyChanger dispatcher = new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange navigation, @NonNull KeyChanger.Callback callback) {
                lastStack = navigation.getNewKeys();
                Parcelable next = navigation.topNewKey();
                if(next instanceof Detail) {
                    flow.goTo(new Loading());
                } else if(next instanceof Loading) {
                    flow.goTo(new Error());
                }
                callback.keyChangeComplete();
            }
        };
        flow = new Backstack(History.single(new Catalog()));
        flow.setKeyChanger(dispatcher);
        flow.goTo(new Detail());
        verifyHistory(lastStack, new Error(), new Loading(), new Detail(), new Catalog());
    }

    @Test
    public void reentrantGoThenBack() {
        KeyChanger dispatcher = new KeyChanger() {
            boolean loading = true;

            @Override
            public void handleKeyChange(@NonNull KeyChange navigation, @NonNull KeyChanger.Callback onComplete) {
                lastStack = navigation.getNewKeys();
                Object next = navigation.topNewKey();
                if(loading) {
                    if(next instanceof Detail) {
                        flow.goTo(new Loading());
                    } else if(next instanceof Loading) {
                        flow.goTo(new Error());
                    } else if(next instanceof Error) {
                        loading = false;
                        flow.setHistory(History.builderFrom(flow).removeLast().build(), KeyChange.BACKWARD);
                    }
                } else {
                    if(next instanceof Loading) {
                        flow.setHistory(History.builderFrom(flow).removeLast().build(), KeyChange.BACKWARD);
                    }
                }
                onComplete.keyChangeComplete();
            }
        };
        flow = new Backstack(History.single(new Catalog()));
        flow.setKeyChanger(dispatcher);
        verifyHistory(lastStack, new Catalog());
        flow.goTo(new Detail());
        verifyHistory(lastStack, new Detail(), new Catalog());
    }

    @Test
    public void reentrantForwardThenGo() {
        Backstack flow = new Backstack(History.single(new Catalog()));
        flow.setKeyChanger(new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange traversal, @NonNull KeyChanger.Callback callback) {
                lastStack = traversal.getNewKeys();
                Object next = traversal.topNewKey();
                if(next instanceof Detail) {
                    ReentranceTest.this.flow.setHistory(History.newBuilder() //
                                    .add(new Detail()) //
                                    .add(new Loading())  //
                                    .build(), //
                            KeyChange.FORWARD);
                } else if(next instanceof Loading) {
                    ReentranceTest.this.flow.goTo(new Error());
                }
                callback.keyChangeComplete();
            }
        });
        this.flow = flow;
        flow.goTo(new Detail());
        verifyHistory(lastStack, new Error(), new Loading(), new Detail());
    }

    @Test
    public void reentranceWaitsForCallback() {
        KeyChanger dispatcher = new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange traversal, @NonNull KeyChanger.Callback callback) {
                lastStack = traversal.getNewKeys();
                lastCallback = callback;
                Object next = traversal.topNewKey();
                if(next instanceof Detail) {
                    flow.goTo(new Loading());
                } else if(next instanceof Loading) {
                    flow.goTo(new Error());
                }
            }
        };
        flow = new Backstack(History.single(new Catalog()));
        flow.setKeyChanger(dispatcher);
        lastCallback.keyChangeComplete();

        flow.goTo(new Detail());
        verifyHistory(flow.getHistory(), new Catalog());
        lastCallback.keyChangeComplete();
        verifyHistory(flow.getHistory(), new Detail(), new Catalog());
        lastCallback.keyChangeComplete();
        verifyHistory(flow.getHistory(), new Loading(), new Detail(), new Catalog());
        lastCallback.keyChangeComplete();
        verifyHistory(flow.getHistory(), new Error(), new Loading(), new Detail(), new Catalog());
    }

    @Test
    public void onCompleteThrowsIfCalledTwice() {
        flow = new Backstack(History.single(new Catalog()));
        flow.setKeyChanger(new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange traversal, @NonNull KeyChanger.Callback callback) {
                lastStack = traversal.getNewKeys();
                lastCallback = callback;
            }
        });

        lastCallback.keyChangeComplete();
        try {
            lastCallback.keyChangeComplete();
        } catch(IllegalStateException e) {
            return;
        }
        fail("Second call to onComplete() should have thrown.");
    }

    @Test
    public void bootstrapTraversal() {
        flow = new Backstack(History.single(new Catalog()));

        flow.setKeyChanger(new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange traversal, @NonNull KeyChanger.Callback callback) {
                lastStack = traversal.getNewKeys();
                callback.keyChangeComplete();
            }
        });

        verifyHistory(lastStack, new Catalog());
    }

    @Test
    public void pendingTraversalReplacesBootstrap() {
        final AtomicInteger dispatchCount = new AtomicInteger(0);
        flow = new Backstack(History.single(new Catalog()));
        flow.goTo(new Detail());

        flow.setKeyChanger(new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange traversal, @NonNull KeyChanger.Callback callback) {
                dispatchCount.incrementAndGet();
                lastStack = traversal.getNewKeys();
                callback.keyChangeComplete();
            }
        });

        verifyHistory(lastStack, new Detail(), new Catalog());
        assertThat(dispatchCount.intValue()).isEqualTo(1);
    }

    @Test
    public void allPendingTraversalsFire() {
        flow = new Backstack(History.single(new Catalog()));
        flow.goTo(new Loading());
        flow.goTo(new Detail());
        flow.goTo(new Error());

        flow.setKeyChanger(new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange traversal, @NonNull KeyChanger.Callback callback) {
                lastCallback = callback;
            }
        });

        lastCallback.keyChangeComplete();
        verifyHistory(flow.getHistory(), new Loading(), new Catalog());

        lastCallback.keyChangeComplete();
        verifyHistory(flow.getHistory(), new Detail(), new Loading(), new Catalog());
    }

    @Test
    public void clearingDispatcherMidTraversalPauses() {
        flow = new Backstack(History.single(new Catalog()));

        flow.setKeyChanger(new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange traversal, @NonNull KeyChanger.Callback callback) {
                flow.goTo(new Loading());
                flow.removeKeyChanger();
                callback.keyChangeComplete();
            }
        });

        verifyHistory(flow.getHistory(), new Catalog());

        flow.setKeyChanger(new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange traversal, @NonNull KeyChanger.Callback callback) {
                callback.keyChangeComplete();
            }
        });

        verifyHistory(flow.getHistory(), new Loading(), new Catalog());
    }

    @Test
    public void handleKeyChangerSetInMidFlightWaitsForBootstrap() {
        flow = new Backstack(History.single(new Catalog()));
        flow.setKeyChanger(new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange traversal, @NonNull KeyChanger.Callback callback) {
                lastCallback = callback;
            }
        });
        flow.setKeyChanger(new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange traversal, @NonNull KeyChanger.Callback callback) {
                lastStack = traversal.getNewKeys();
                callback.keyChangeComplete();
            }
        });

        assertThat(lastStack).isNull();
        lastCallback.keyChangeComplete();
        verifyHistory(lastStack, new Catalog());
    }

    @Test
    public void handleKeyChangeerSetInMidFlightWithBigQueueNeedsNoBootstrap() {
        final AtomicInteger secondDispatcherCount = new AtomicInteger(0);
        flow = new Backstack(History.single(new Catalog()));
        flow.setKeyChanger(new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange traversal, @NonNull KeyChanger.Callback callback) {
                flow.goTo(new Detail());
                lastCallback = callback;
            }
        });
        flow.setKeyChanger(new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange traversal, @NonNull KeyChanger.Callback callback) {
                secondDispatcherCount.incrementAndGet();
                lastStack = traversal.getNewKeys();
                callback.keyChangeComplete();
            }
        });

        assertThat(lastStack).isNull();
        lastCallback.keyChangeComplete();
        verifyHistory(lastStack, new Detail(), new Catalog());
        assertThat(secondDispatcherCount.get()).isEqualTo(1);
    }

    @Test
    public void traversalsQueuedAfterDispatcherRemovedBootstrapTheNextOne() {
        final AtomicInteger secondDispatcherCount = new AtomicInteger(0);
        flow = new Backstack(History.single(new Catalog()));

        flow.setKeyChanger(new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange traversal, @NonNull KeyChanger.Callback callback) {
                lastCallback = callback;
                flow.removeKeyChanger();
                flow.goTo(new Loading());
            }
        });

        verifyHistory(flow.getHistory(), new Catalog());

        flow.setKeyChanger(new KeyChanger() {
            @Override
            public void handleKeyChange(@NonNull KeyChange traversal, @NonNull KeyChanger.Callback callback) {
                secondDispatcherCount.incrementAndGet();
                callback.keyChangeComplete();
            }
        });

        assertThat(secondDispatcherCount.get()).isZero();
        lastCallback.keyChangeComplete();

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

    private void verifyHistory(List<Object> history, Object... keys) {
        List<Object> copy = new ArrayList<>(history);
        Collections.reverse(copy);
        assertThat(copy).containsExactly(keys);
    }
}