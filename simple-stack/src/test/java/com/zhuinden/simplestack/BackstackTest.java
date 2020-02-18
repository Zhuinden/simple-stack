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
import android.support.annotation.NonNull;
import android.view.View;

import com.zhuinden.simplestack.helpers.TestKey;
import com.zhuinden.statebundle.StateBundle;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Owner on 2017. 03. 25..
 */

public class BackstackTest {
    StateChanger stateChanger = new StateChanger() {
        @Override
        public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
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
            @NonNull
            @Override
            public List<Object> filterHistory(@NonNull List<Object> restoredKeys) {
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
            @NonNull
            @Override
            public List<Object> filterHistory(@NonNull List<Object> restoredKeys) {
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
                @NonNull
                @Override
                public List<Object> filterHistory(@NonNull List<Object> restoredKeys) {
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
            public void stateChangeCompleted(@NonNull StateChange stateChange) {
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
            public void stateChangeCompleted(@NonNull StateChange stateChange) {
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
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
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
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
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
}
