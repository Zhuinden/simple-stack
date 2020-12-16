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

import android.content.Context;
import android.os.Parcelable;
import android.view.View;

import com.zhuinden.simplestack.helpers.ServiceProvider;
import com.zhuinden.simplestack.helpers.TestKey;
import com.zhuinden.simplestack.helpers.TestKeyWithExplicitParent;
import com.zhuinden.simplestack.helpers.TestKeyWithOnlyParentServices;
import com.zhuinden.simplestack.helpers.TestKeyWithScope;
import com.zhuinden.statebundle.StateBundle;

import org.junit.Assert;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Owner on 2017. 03. 25..
 */

public class BackstackTest {
    StateChanger stateChanger = new StateChanger() {
        @Override
        public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
            completionCallback.stateChangeComplete();
        }
    };

    @Test
    public void afterClearAndRestorationTheInitialKeysShouldBeRestoredAndNotOverwrittenByRestoredState() {
        TestKey initial = new TestKey("initial");
        TestKey restored = new TestKey("restored");

        ArrayList<Parcelable> history = new ArrayList<>();
        history.add(restored);
        StateBundle stateBundle = new StateBundle();
        stateBundle.putParcelableArrayList(Backstack.getHistoryTag(), history);

        Backstack backstack = new Backstack();
        backstack.setup(History.single(initial));
        backstack.fromBundle(stateBundle);
        backstack.setStateChanger(stateChanger);

        if(!backstack.goBack()) {
            backstack.forceClear();
        }
        assertThat(backstack.getHistory()).isEmpty();
        backstack.setStateChanger(stateChanger);
        assertThat(backstack.getHistory()).doesNotContain(restored);
        assertThat(backstack.getHistory()).containsExactly(initial);
    }

    @Test
    public void onGoBackKeysShouldNotBeClearedAndShouldRestoreRestoredKey() {
        TestKey initial = new TestKey("initial");
        TestKey restored = new TestKey("restored");

        ArrayList<Parcelable> history = new ArrayList<>();
        history.add(restored);
        StateBundle stateBundle = new StateBundle();
        stateBundle.putParcelableArrayList(Backstack.getHistoryTag(),
                                           history);

        Backstack backstack = new Backstack();
        backstack.setup(History.single(initial));
        backstack.fromBundle(stateBundle);
        backstack.setStateChanger(stateChanger);


        backstack.goBack();
        assertThat(backstack.getHistory()).isNotEmpty();
        assertThat(backstack.getHistory()).containsExactly(restored);
        backstack.setStateChanger(stateChanger);
        assertThat(backstack.getHistory()).containsExactly(restored);
    }

    @Test
    public void afterClearAndRestorationTheFilteredAreNotRestored() {
        final TestKey initial = new TestKey("initial");
        final TestKey restored = new TestKey("restored");
        final TestKey filtered = new TestKey("filtered");

        ArrayList<Parcelable> history = new ArrayList<>();
        history.add(restored);
        history.add(filtered);
        StateBundle stateBundle = new StateBundle();
        stateBundle.putParcelableArrayList(Backstack.getHistoryTag(), history);

        Backstack backstack = new Backstack();
        backstack.setKeyFilter(new KeyFilter() {
            @Nonnull
            @Override
            public List<Object> filterHistory(@Nonnull List<Object> restoredKeys) {
                restoredKeys.remove(filtered);
                return restoredKeys;
            }
        });
        backstack.setup(History.single(initial));
        backstack.fromBundle(stateBundle);
        backstack.setStateChanger(stateChanger);


        assertThat(backstack.getHistory()).contains(restored);
        assertThat(backstack.getHistory()).doesNotContain(filtered);

        //// would restore properly
        backstack = new Backstack();
        backstack.setup(History.single(initial));
        backstack.fromBundle(stateBundle);
        backstack.setStateChanger(stateChanger);

        assertThat(backstack.getHistory()).contains(restored, filtered);

        //// if both are filtered, restore initial
        backstack = new Backstack();
        backstack.setKeyFilter(new KeyFilter() {
            @Nonnull
            @Override
            public List<Object> filterHistory(@Nonnull List<Object> restoredKeys) {
                restoredKeys.remove(restored);
                restoredKeys.remove(filtered);
                return restoredKeys;
            }
        });

        backstack.setup(History.single(initial));
        backstack.fromBundle(stateBundle);
        backstack.setStateChanger(stateChanger);


        assertThat(backstack.getHistory()).doesNotContain(restored, filtered);
        assertThat(backstack.getHistory()).contains(initial);
    }

    @Test
    public void keyFilterSetAfterSetupShouldThrow() {
        final TestKey initial = new TestKey("initial");
        Backstack backstack = new Backstack();
        backstack.setup(History.single(initial));
        try {
            backstack.setKeyFilter(new KeyFilter() {
                @Nonnull
                @Override
                public List<Object> filterHistory(@Nonnull List<Object> restoredKeys) {
                    return restoredKeys;
                }

            });
            Assert.fail();
        } catch(IllegalStateException e) {
            // OK!
        }
    }

    @Test
    public void stateChangeCompletionListenerIsCalledCorrectly() {
        final TestKey initial = new TestKey("initial");
        final TestKey other = new TestKey("other");
        final AtomicInteger atomicInteger = new AtomicInteger(0);
        final List<Integer> integers = new ArrayList<>();
        Backstack backstack = new Backstack();
        backstack.setup(History.single(initial));
        Backstack.CompletionListener stateChangeCompletionListener = new Backstack.CompletionListener() {
            @Override
            public void stateChangeCompleted(@Nonnull StateChange stateChange) {
                integers.add(atomicInteger.getAndIncrement());
            }
        };
        backstack.addStateChangeCompletionListener(stateChangeCompletionListener);
        backstack.setStateChanger(stateChanger);
        backstack.goTo(other);
        backstack.goBack();
        assertThat(integers).containsExactly(0, 1, 2);
        backstack.removeStateChangeCompletionListener(stateChangeCompletionListener);
        backstack.setHistory(History.single(other), StateChange.REPLACE);
        assertThat(integers).containsExactly(0, 1, 2);
        backstack.addStateChangeCompletionListener(stateChangeCompletionListener);
        backstack.setHistory(History.single(initial), StateChange.REPLACE);
        assertThat(integers).containsExactly(0, 1, 2, 3);
        backstack.removeAllStateChangeCompletionListeners();
        backstack.goTo(other);
        assertThat(integers).containsExactly(0, 1, 2, 3);
    }

    @Test
    public void stateChangeCompletionListenerIsCalledInCorrectOrder() {
        final TestKey initial = new TestKey("initial");
        final TestKey other = new TestKey("other");
        final List<TestKey> integers = new ArrayList<>();
        Backstack backstack = new Backstack();
        backstack.setup(History.single(initial));

        Backstack.CompletionListener stateChangeCompletionListener = new Backstack.CompletionListener() {
            @Override
            public void stateChangeCompleted(@Nonnull StateChange stateChange) {
                integers.add(stateChange.<TestKey>topNewKey());
            }
        };
        backstack.addStateChangeCompletionListener(stateChangeCompletionListener);
        backstack.setStateChanger(stateChanger);
        backstack.detachStateChanger();
        backstack.goTo(other);
        backstack.setHistory(History.single(initial), StateChange.REPLACE);
        backstack.setHistory(History.single(other), StateChange.REPLACE);
        backstack.setHistory(History.single(initial), StateChange.REPLACE);
        backstack.setHistory(History.single(initial), StateChange.REPLACE);
        backstack.goTo(other);
        backstack.reattachStateChanger();
        assertThat(integers).containsExactly(
                initial, other, initial, other, initial, initial, other);
    }

    @Test
    public void getInitialKeysReturnsExpectedValues() {
        TestKey initial = new TestKey("initial");
        TestKey restored = new TestKey("restored");

        ArrayList<Parcelable> history = new ArrayList<>();
        history.add(restored);
        StateBundle stateBundle = new StateBundle();
        stateBundle.putParcelableArrayList(Backstack.getHistoryTag(), history);

        Backstack backstack = new Backstack();
        backstack.setup(History.single(initial));
        backstack.fromBundle(stateBundle);
        backstack.setStateChanger(stateChanger);

        backstack.goBack();
        backstack.setStateChanger(stateChanger);

        assertThat(backstack.getInitialKeys()).containsExactly(initial);
    }

    @Test
    public void savedStateWorksForResultPassing() {
        TestKey first = new TestKey("first");

        View view = Mockito.mock(View.class);
        Context context = Mockito.mock(Context.class);
        Mockito.when(view.getContext()).thenReturn(context);
        Mockito.when(context.getSystemService(KeyContextWrapper.TAG)).thenReturn(first);

        TestKey second = new TestKey("second");

        Backstack backstack = new Backstack();
        backstack.setup(History.of(first));
        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });

        backstack.goTo(second);

        StateBundle firstBundle = backstack.getSavedState(first).getBundle();
        firstBundle.putString("result", "Success!");

        backstack.persistViewToState(view);

        StateBundle persistedBundle = backstack.toBundle();

        Backstack backstack2 = new Backstack();
        backstack2.setup(History.of(second));
        backstack2.fromBundle(persistedBundle);
        backstack2.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });

        assertThat(backstack2.getSavedState(first).getBundle().getString("result")).isEqualTo(
                "Success!");
    }

    @Test
    public void uninitializedStackGoBackWorks() {
        TestKey first = new TestKey("first");

        Backstack backstack = new Backstack();
        backstack.setup(History.of(first));

        assertThat(backstack.goBack()).isFalse();
    }

    @Test
    public void removeStateChangerListenersDoesNotBreakScoping() {
        Backstack backstack = new Backstack();

        ScopeKey key1 = new ScopeKey() {
            @Nonnull
            @Override
            public String getScopeTag() {
                return "key1";
            }

            @Override
            public String toString() {
                return "KEY1";
            }
        };

        final ScopeKey key2 = new ScopeKey() {
            @Nonnull
            @Override
            public String getScopeTag() {
                return "key2";
            }

            @Override
            public String toString() {
                return "KEY2";
            }
        };

        final Object key3 = new Object();

        class Service implements ScopedServices.Registered, ScopedServices.Activated {
            private boolean isActivatedCalled;
            private boolean isDeactivatedCalled;
            private boolean isRegisteredCalled;
            private boolean isUnregisteredCalled;

            @Override
            public void onServiceActive() {
                isActivatedCalled = true;
            }

            @Override
            public void onServiceInactive() {
                isDeactivatedCalled = true;
            }

            @Override
            public void onServiceRegistered() {
                isRegisteredCalled = true;
            }

            @Override
            public void onServiceUnregistered() {
                isUnregisteredCalled = true;
            }
        }

        final Service service1 = new Service();
        final Service service2 = new Service();

        backstack.setScopedServices(new ScopedServices() {
            @Override
            public void bindServices(@Nonnull ServiceBinder serviceBinder) {
                if("key1".equals(serviceBinder.getScopeTag())) {
                    serviceBinder.addService("service1", service1);
                }
                if("key2".equals(serviceBinder.getScopeTag())) {
                    serviceBinder.addService("service2", service2);
                }
            }
        });

        backstack.setup(History.of(key1));

        final StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        };

        backstack.setStateChanger(stateChanger);

        backstack.removeStateChanger();

        backstack.setHistory(History.of(key2), StateChange.REPLACE);

        backstack.removeAllStateChangeCompletionListeners();

        backstack.setStateChanger(stateChanger);

        backstack.setHistory(History.of(key3), StateChange.REPLACE);

        assertThat(service1.isActivatedCalled).isTrue();
        assertThat(service1.isDeactivatedCalled).isTrue();
        assertThat(service1.isRegisteredCalled).isTrue();
        assertThat(service1.isUnregisteredCalled).isTrue();

        assertThat(service2.isRegisteredCalled).isTrue();
        assertThat(service2.isUnregisteredCalled).isTrue();
        assertThat(service2.isActivatedCalled).isTrue();
        assertThat(service2.isDeactivatedCalled).isTrue();
    }
    
    @Test
    public void exitScopeThrowsWhenBackstackIsEmpty() {
        Backstack backstack = new Backstack();

        backstack.setScopedServices(new ServiceProvider());

        Object key = new TestKeyWithScope("blah") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
            }
        };

        backstack.setup(History.of(key));

        try {
            backstack.exitScope("blah");
            Assert.fail();
        } catch(IllegalStateException e) {
            // OK!
        }
    }

    @Test
    public void exitScopeThrowsWhenScopeIsNotFound() {
        Backstack backstack = new Backstack();

        backstack.setScopedServices(new ServiceProvider());

        Object key = new TestKeyWithScope("blah") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
            }
        };

        backstack.setup(History.of(key));

        try {
            backstack.exitScope("blahhhh");
            Assert.fail();
        } catch(IllegalStateException e) {
            // OK!
        }
    }
    
    @Test
    public void exitScopeDefaultsToJumpToRootIfRootHasScope() {
        Backstack backstack = new Backstack();

        backstack.setScopedServices(new ServiceProvider());

        Object key = new TestKeyWithScope("blah") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
            }
        };

        backstack.setup(History.of(key));
        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });

        backstack.exitScope("blah");

        assertThat(backstack.getHistory()).containsExactly(key);
    }

    @Test
    public void exitScopeExitsImplicitScopeCorrectly() {
        Backstack backstack = new Backstack();

        backstack.setScopedServices(new ServiceProvider());

        Object firstKey = new TestKey("firstKey");
        Object lastKey = new TestKey("lastKey");

        Object key = new TestKeyWithScope("blah") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
            }
        };

        backstack.setup(History.of(firstKey, key, lastKey));
        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });

        backstack.exitScope("blah");

        assertThat(backstack.getHistory()).containsExactly(firstKey);
    }

    @Test
    public void exitScopeExitsExplicitScopeCorrectly() {
        Backstack backstack = new Backstack();

        backstack.setScopedServices(new ServiceProvider());

        Object firstKey = new TestKey("firstKey");
        Object lastKey = new TestKey("lastKey");

        Object key = new TestKeyWithOnlyParentServices("key", History.of("blah")) {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
            }
        };

        backstack.setup(History.of(firstKey, key, lastKey));
        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });

        backstack.exitScope("blah");

        assertThat(backstack.getHistory()).containsExactly(firstKey);
    }

    @Test
    public void exitScopeExitsExplicitScopesCorrectly() {
        Backstack backstack = new Backstack();

        backstack.setScopedServices(new ServiceProvider());

        Object firstKey = new TestKey("firstKey");
        Object secondKey = new TestKey("secondKey");
        Object lastKey = new TestKey("lastKey");

        Object key1 = new TestKeyWithExplicitParent("key1") {
            @Nonnull
            @Override
            public List<String> getParentScopes() {
                return History.of("blah", "parentKey1");
            }

            @Override
            protected void bindParentServices(ServiceBinder serviceBinder) {
            }

            @Override
            protected void bindOwnServices(ServiceBinder serviceBinder) {
            }
        };

        Object key2 = new TestKeyWithExplicitParent("key2") {
            @Nonnull
            @Override
            public List<String> getParentScopes() {
                return History.of("blah", "parentKey2");
            }

            @Override
            protected void bindParentServices(ServiceBinder serviceBinder) {
            }

            @Override
            protected void bindOwnServices(ServiceBinder serviceBinder) {
            }
        };

        backstack.setup(History.of(firstKey, secondKey, key1, key2, lastKey));
        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });

        backstack.exitScope("blah");

        assertThat(backstack.getHistory()).containsExactly(firstKey, secondKey);
    }

    @Test
    public void exitScopeToThrowsWhenBackstackIsEmpty() {
        Backstack backstack = new Backstack();

        backstack.setScopedServices(new ServiceProvider());

        Object key = new TestKeyWithScope("blah") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
            }
        };

        backstack.setup(History.of(key));

        Object targetKey = new TestKey("target");

        try {
            backstack.exitScopeTo("blah", targetKey, StateChange.FORWARD);
            Assert.fail();
        } catch(IllegalStateException e) {
            // OK!
        }
    }

    @Test
    public void exitScopeToThrowsWhenScopeIsNotFound() {
        Backstack backstack = new Backstack();

        backstack.setScopedServices(new ServiceProvider());

        Object key = new TestKeyWithScope("blah") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
            }
        };

        backstack.setup(History.of(key));

        Object targetKey = new TestKey("target");

        try {
            backstack.exitScopeTo("blah", targetKey, StateChange.FORWARD);
            Assert.fail();
        } catch(IllegalStateException e) {
            // OK!
        }
    }

    @Test
    public void exitScopeToExitsImplicitScopeCorrectlyAndGoesBackIfFound() {
        Backstack backstack = new Backstack();

        backstack.setScopedServices(new ServiceProvider());

        Object firstKey = new TestKey("firstKey");
        Object secondKey = new TestKey("secondKey");
        Object thirdKey = new TestKey("thirdKey");
        Object lastKey = new TestKey("lastKey");

        Object key = new TestKeyWithScope("blah") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
            }
        };

        backstack.setup(History.of(firstKey, secondKey, thirdKey, key, lastKey));
        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });

        backstack.exitScopeTo("blah", secondKey, StateChange.BACKWARD);

        assertThat(backstack.getHistory()).containsExactly(firstKey, secondKey);
    }

    @Test
    public void exitScopeToExitsImplicitScopeCorrectlyAndAppendsIfNotFound() {
        Backstack backstack = new Backstack();

        backstack.setScopedServices(new ServiceProvider());

        Object firstKey = new TestKey("firstKey");
        Object secondKey = new TestKey("secondKey");
        Object thirdKey = new TestKey("thirdKey");
        Object lastKey = new TestKey("lastKey");

        Object key = new TestKeyWithScope("blah") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
            }
        };

        Object targetKey = new TestKey("targetKey");

        backstack.setup(History.of(firstKey, secondKey, thirdKey, key, lastKey));
        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });

        backstack.exitScopeTo("blah", targetKey, StateChange.FORWARD);

        assertThat(backstack.getHistory()).containsExactly(firstKey, secondKey, thirdKey, targetKey);
    }

    @Test
    public void exitScopeToExitsExplicitScopeCorrectlyAndGoesBackIfFound() {
        Backstack backstack = new Backstack();

        backstack.setScopedServices(new ServiceProvider());

        Object firstKey = new TestKey("firstKey");
        Object secondKey = new TestKey("secondKey");
        Object thirdKey = new TestKey("thirdKey");
        Object lastKey = new TestKey("lastKey");

        Object key = new TestKeyWithOnlyParentServices("key", History.of("blah")) {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
            }
        };

        backstack.setup(History.of(firstKey, secondKey, thirdKey, key, lastKey));
        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });

        backstack.exitScopeTo("blah", secondKey, StateChange.BACKWARD);

        assertThat(backstack.getHistory()).containsExactly(firstKey, secondKey);
    }

    @Test
    public void exitScopeToExitsExplicitScopeCorrectlyAndAppendsIfNotFound() {
        Backstack backstack = new Backstack();

        backstack.setScopedServices(new ServiceProvider());

        Object firstKey = new TestKey("firstKey");
        Object secondKey = new TestKey("secondKey");
        Object thirdKey = new TestKey("thirdKey");
        Object lastKey = new TestKey("lastKey");

        Object key = new TestKeyWithOnlyParentServices("key", History.of("blah")) {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
            }
        };

        Object targetKey = new TestKey("targetKey");

        backstack.setup(History.of(firstKey, secondKey, thirdKey, key, lastKey));
        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });

        backstack.exitScopeTo("blah", targetKey, StateChange.FORWARD);

        assertThat(backstack.getHistory()).containsExactly(firstKey, secondKey, thirdKey, targetKey);
    }

    @Test
    public void exitScopeToExitsExplicitScopesCorrectly() {
        Backstack backstack = new Backstack();

        backstack.setScopedServices(new ServiceProvider());

        Object firstKey = new TestKey("firstKey");
        Object secondKey = new TestKey("secondKey");
        Object lastKey = new TestKey("lastKey");

        Object key1 = new TestKeyWithExplicitParent("key1") {
            @Nonnull
            @Override
            public List<String> getParentScopes() {
                return History.of("blah", "parentKey1");
            }

            @Override
            protected void bindParentServices(ServiceBinder serviceBinder) {
            }

            @Override
            protected void bindOwnServices(ServiceBinder serviceBinder) {
            }
        };

        Object key2 = new TestKeyWithExplicitParent("key2") {
            @Nonnull
            @Override
            public List<String> getParentScopes() {
                return History.of("blah", "parentKey2");
            }

            @Override
            protected void bindParentServices(ServiceBinder serviceBinder) {
            }

            @Override
            protected void bindOwnServices(ServiceBinder serviceBinder) {
            }
        };

        backstack.setup(History.of(firstKey, secondKey, key1, key2, lastKey));
        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });

        Object targetKey = new TestKeyWithScope("blah") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
            }
        };

        backstack.exitScopeTo("blah", targetKey, StateChange.FORWARD);

        assertThat(backstack.getHistory()).containsExactly(firstKey, secondKey, targetKey);
    }

    @Test
    public void exitScopeToDefaultsToJumpToRootIfRootHasScope() {
        Backstack backstack = new Backstack();

        backstack.setScopedServices(new ServiceProvider());

        Object key = new TestKeyWithScope("blah") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
            }
        };

        backstack.setup(History.of(key));
        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });

        backstack.exitScopeTo("blah", key, StateChange.BACKWARD);

        assertThat(backstack.getHistory()).containsExactly(key);
    }

    @Test
    public void exitScopeToWorks() {
        Backstack backstack = new Backstack();

        backstack.setScopedServices(new ServiceProvider());

        Object key = new TestKeyWithScope("blah") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
            }
        };

        Object targetKey = new TestKey("targetKey");

        backstack.setup(History.of(key));
        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });

        backstack.exitScopeTo("blah", targetKey, StateChange.FORWARD);

        assertThat(backstack.getHistory()).containsExactly(targetKey);
    }
}
