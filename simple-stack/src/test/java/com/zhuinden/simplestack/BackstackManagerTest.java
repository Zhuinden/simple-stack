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

import com.zhuinden.statebundle.StateBundle;

import junit.framework.Assert;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Owner on 2017. 03. 25..
 */

public class BackstackManagerTest {
    KeyChanger keyChanger = new KeyChanger() {
        @Override
        public void handleKeyChange(@NonNull KeyChange keyChange, @NonNull Callback completionCallback) {
            completionCallback.keyChangeComplete();
        }
    };

    @Test
    public void afterClearAndRestorationTheInitialKeysShouldBeRestoredAndNotOverwrittenByRestoredState() {
        TestKey initial = new TestKey("initial");
        TestKey restored = new TestKey("restored");

        ArrayList<Parcelable> history = new ArrayList<>();
        history.add(restored);
        StateBundle stateBundle = new StateBundle();
        stateBundle.putParcelableArrayList(BackstackManager.getHistoryTag(), history);

        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setup(History.single(initial));
        backstackManager.fromBundle(stateBundle);
        backstackManager.setKeyChanger(keyChanger);

        Backstack backstack = backstackManager.getBackstack();
        if(!backstack.goBack()) {
            backstack.reset();
        }
        assertThat(backstack.getHistory()).isEmpty();
        backstack.setKeyChanger(keyChanger);
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
        stateBundle.putParcelableArrayList(BackstackManager.getHistoryTag(),
                history);

        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setup(History.single(initial));
        backstackManager.fromBundle(stateBundle);
        backstackManager.setKeyChanger(keyChanger);

        Backstack backstack = backstackManager.getBackstack();
        backstack.goBack();
        assertThat(backstack.getHistory()).isNotEmpty();
        assertThat(backstack.getHistory()).containsExactly(restored);
        backstack.setKeyChanger(keyChanger);
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
        stateBundle.putParcelableArrayList(BackstackManager.getHistoryTag(), history);

        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setKeyFilter(new KeyFilter() {
            @NonNull
            @Override
            public List<Object> filterHistory(@NonNull List<Object> restoredKeys) {
                restoredKeys.remove(filtered);
                return restoredKeys;
            }
        });
        backstackManager.setup(History.single(initial));
        backstackManager.fromBundle(stateBundle);
        backstackManager.setKeyChanger(keyChanger);

        Backstack backstack = backstackManager.getBackstack();
        assertThat(backstack.getHistory()).contains(restored);
        assertThat(backstack.getHistory()).doesNotContain(filtered);

        //// would restore properly
        backstackManager = new BackstackManager();
        backstackManager.setup(History.single(initial));
        backstackManager.fromBundle(stateBundle);
        backstackManager.setKeyChanger(keyChanger);

        backstack = backstackManager.getBackstack();
        assertThat(backstack.getHistory()).contains(restored, filtered);

        //// if both are filtered, restore initial
        backstackManager = new BackstackManager();
        backstackManager.setKeyFilter(new KeyFilter() {
            @NonNull
            @Override
            public List<Object> filterHistory(@NonNull List<Object> restoredKeys) {
                restoredKeys.remove(restored);
                restoredKeys.remove(filtered);
                return restoredKeys;
            }
        });

        backstackManager.setup(History.single(initial));
        backstackManager.fromBundle(stateBundle);
        backstackManager.setKeyChanger(keyChanger);

        backstack = backstackManager.getBackstack();
        assertThat(backstack.getHistory()).doesNotContain(restored, filtered);
        assertThat(backstack.getHistory()).contains(initial);
    }

    @Test
    public void keyFilterSetAfterSetupShouldThrow() {
        final TestKey initial = new TestKey("initial");
        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setup(History.single(initial));
        try {
            backstackManager.setKeyFilter(new KeyFilter() {
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
    public void keyChangeCompletionListenerIsCalledCorrectly() {
        final TestKey initial = new TestKey("initial");
        final TestKey other = new TestKey("other");
        final AtomicInteger atomicInteger = new AtomicInteger(0);
        final List<Integer> integers = new ArrayList<>();
        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setup(History.single(initial));
        Backstack.CompletionListener keyChangeCompletionListener = new Backstack.CompletionListener() {
            @Override
            public void keyChangeCompleted(@NonNull KeyChange keyChange) {
                integers.add(atomicInteger.getAndIncrement());
            }
        };
        backstackManager.addKeyChangeCompletionListener(keyChangeCompletionListener);
        backstackManager.setKeyChanger(keyChanger);
        backstackManager.getBackstack().goTo(other);
        backstackManager.getBackstack().goBack();
        assertThat(integers).containsExactly(0, 1, 2);
        backstackManager.removeKeyChangeCompletionListener(keyChangeCompletionListener);
        backstackManager.getBackstack().setHistory(History.single(other), KeyChange.REPLACE);
        assertThat(integers).containsExactly(0, 1, 2);
        backstackManager.addKeyChangeCompletionListener(keyChangeCompletionListener);
        backstackManager.getBackstack().setHistory(History.single(initial), KeyChange.REPLACE);
        assertThat(integers).containsExactly(0, 1, 2, 3);
        backstackManager.removeAllKeyChangeCompletionListeners();
        backstackManager.getBackstack().goTo(other);
        assertThat(integers).containsExactly(0, 1, 2, 3);
    }

    @Test
    public void keyChangeCompletionListenerIsCalledInCorrectOrder() {
        final TestKey initial = new TestKey("initial");
        final TestKey other = new TestKey("other");
        final List<TestKey> integers = new ArrayList<>();
        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setup(History.single(initial));
        Backstack backstack = backstackManager.getBackstack();
        Backstack.CompletionListener keyChangeCompletionListener = new Backstack.CompletionListener() {
            @Override
            public void keyChangeCompleted(@NonNull KeyChange keyChange) {
                integers.add(keyChange.<TestKey>topNewKey());
            }
        };
        backstackManager.addKeyChangeCompletionListener(keyChangeCompletionListener);
        backstackManager.setKeyChanger(keyChanger);
        backstackManager.detachKeyChanger();
        backstack.goTo(other);
        backstack.setHistory(History.single(initial), KeyChange.REPLACE);
        backstack.setHistory(History.single(other), KeyChange.REPLACE);
        backstack.setHistory(History.single(initial), KeyChange.REPLACE);
        backstack.setHistory(History.single(initial), KeyChange.REPLACE);
        backstack.goTo(other);
        backstackManager.reattachKeyChanger();
        assertThat(integers).containsExactly(initial, other, initial, other, initial, initial, other);
    }

    @Test
    public void getInitialKeysReturnsExpectedValues() {
        TestKey initial = new TestKey("initial");
        TestKey restored = new TestKey("restored");

        ArrayList<Parcelable> history = new ArrayList<>();
        history.add(restored);
        StateBundle stateBundle = new StateBundle();
        stateBundle.putParcelableArrayList(BackstackManager.getHistoryTag(), history);

        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setup(History.single(initial));
        backstackManager.fromBundle(stateBundle);
        backstackManager.setKeyChanger(keyChanger);

        Backstack backstack = backstackManager.getBackstack();
        backstack.goBack();
        backstack.setKeyChanger(keyChanger);

        assertThat(backstack.getInitialKeys()).containsExactly(initial);
    }
}
