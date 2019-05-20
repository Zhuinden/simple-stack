/*
 * Copyright 2018 Gabor Varadi
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

import android.support.annotation.NonNull;

import com.zhuinden.simplestack.helpers.TestKey;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

/**
 * Created by zhuinden on 2018. 03. 03..
 */
public class HistoryTest {
    @Test
    public void from() throws Exception {
        TestKey testKey1 = new TestKey("Hello");
        TestKey testKey2 = new TestKey("World");
        assertThat(History.of(testKey1, testKey2)).containsExactly(testKey1, testKey2);
    }

    @Test
    public void fromList() throws Exception {
        TestKey testKey1 = new TestKey("Hello");
        TestKey testKey2 = new TestKey("World");
        List<TestKey> list = new ArrayList<>(2);
        list.add(testKey1);
        list.add(testKey2);
        assertThat(History.from(list)).containsExactly(testKey1, testKey2);
    }

    @Test
    public void top() throws Exception {
        TestKey testKey1 = new TestKey("Hello");
        TestKey testKey2 = new TestKey("World");
        History history = History.of(testKey1, testKey2);
        assertThat(history.top()).isSameAs(testKey2);
    }

    @Test
    public void fromTop() throws Exception {
        TestKey testKey1 = new TestKey("Hello");
        TestKey testKey2 = new TestKey("World");
        TestKey testKey3 = new TestKey("Kappa");
        History history = History.of(testKey1, testKey2, testKey3);
        assertThat(history.fromTop(0)).isSameAs(testKey3);
        assertThat(history.fromTop(1)).isSameAs(testKey2);
        assertThat(history.fromTop(2)).isSameAs(testKey1);
        assertThat(history.fromTop(-1)).isSameAs(testKey1);
        assertThat(history.fromTop(-2)).isSameAs(testKey2);
        assertThat(history.fromTop(-1*history.size())).isSameAs(testKey3);
        assertThat(history.top()).isSameAs(history.fromTop(0));

        try {
            history.fromTop(history.size());
            fail();
        } catch (IllegalArgumentException e) {
            // OK!
        }

        try {
            history.fromTop((history.size()+1)*-1);
            fail();
        } catch (IllegalArgumentException e) {
            // OK!
        }
    }

    @Test
    public void root() throws Exception {
        TestKey testKey1 = new TestKey("Hello");
        TestKey testKey2 = new TestKey("World");
        TestKey testKey3 = new TestKey("Kappa");
        History history = History.of(testKey1, testKey2, testKey3);

        assertThat(history.root()).isSameAs(testKey1);

        History emptyHistory = History.of();
        assertThat(emptyHistory.root()).isNull();
    }

    @Test
    public void buildUpon() throws Exception {
        TestKey testKey1 = new TestKey("Hello");
        TestKey testKey2 = new TestKey("World");
        TestKey testKey3 = new TestKey("Kappa");
        History history = History.of(testKey1, testKey2);
        assertThat(history.buildUpon().add(testKey3).build()).containsExactly(testKey1, testKey2, testKey3);
    }

    @Test
    public void builderFromObjects() throws Exception {
        TestKey testKey1 = new TestKey("Hello");
        TestKey testKey2 = new TestKey("World");
        assertThat(History.builderOf(testKey1, testKey2).build()).containsExactly(testKey1, testKey2);
    }

    @Test
    public void builderFromList() throws Exception {
        TestKey testKey1 = new TestKey("Hello");
        TestKey testKey2 = new TestKey("World");
        TestKey testKey3 = new TestKey("Kappa");

        List<TestKey> list = new ArrayList<>(3);
        list.add(testKey1);
        list.add(testKey2);
        list.add(testKey3);

        assertThat(History.builderFrom(list)).containsExactly(testKey1, testKey2, testKey3);
    }

    @Test
    public void builderFromBackstackDelegate() throws Exception {
        TestKey testKey1 = new TestKey("Hello");
        TestKey testKey2 = new TestKey("World");

        BackstackDelegate backstackDelegate = new BackstackDelegate();
        backstackDelegate.onCreate(null, null, History.of(testKey1, testKey2));
        backstackDelegate.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });
        assertThat(History.builderFrom(backstackDelegate)).containsExactly(testKey1, testKey2);
    }

    @Test
    public void builderFromBackstack() throws Exception {
        TestKey testKey1 = new TestKey("Hello");
        TestKey testKey2 = new TestKey("World");

        BackstackDelegate backstackDelegate = new BackstackDelegate();
        backstackDelegate.onCreate(null, null, History.of(testKey1, testKey2));
        backstackDelegate.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });
        assertThat(History.builderFrom(backstackDelegate.getBackstack())).containsExactly(testKey1, testKey2);
    }

    @Test
    public void newBuilder() throws Exception {
        TestKey testKey1 = new TestKey("Hello");
        TestKey testKey2 = new TestKey("World");

        assertThat(History.newBuilder().add(testKey1).add(testKey2).build()).containsExactly(testKey1, testKey2);
    }

    @Test
    public void single() throws Exception {
        TestKey testKey1 = new TestKey("Hello");

        assertThat(History.single(testKey1)).containsExactly(testKey1);
    }

    @Test
    public void add() throws Exception {
        TestKey testKey1 = new TestKey("Hello");
        TestKey testKey2 = new TestKey("World");
        History history = History.of(testKey1);

        try {
            history.add(testKey2);
            fail();
        } catch(UnsupportedOperationException e) {
            // OK!
        }
    }

    @Test
    public void set() throws Exception {
        TestKey testKey1 = new TestKey("Hello");
        TestKey testKey2 = new TestKey("World");
        History history = History.of(testKey1);

        try {
            history.set(0, testKey2);
            fail();
        } catch(UnsupportedOperationException e) {
            // OK!
        }
    }

    @Test
    public void addWithIndex() throws Exception {
        TestKey testKey1 = new TestKey("Hello");
        TestKey testKey2 = new TestKey("World");
        History history = History.of(testKey1);

        try {
            history.add(0, testKey2);
            fail();
        } catch(UnsupportedOperationException e) {
            // OK!
        }
    }

    @Test
    public void remove() throws Exception {
        TestKey testKey1 = new TestKey("Hello");
        TestKey testKey2 = new TestKey("World");
        History history = History.of(testKey1, testKey2);

        try {
            history.remove(testKey2);
            fail();
        } catch(UnsupportedOperationException e) {
            // OK!
        }
    }

    @Test
    public void indexOf() throws Exception {
        TestKey testKey1 = new TestKey("Hello");
        TestKey testKey2 = new TestKey("World");
        History history = History.of(testKey1, testKey2);

        assertThat(history.indexOf(testKey2)).isEqualTo(1);
        assertThat(history.indexOf(testKey1)).isEqualTo(0);
    }

    @Test
    public void lastIndexOf() throws Exception {
        TestKey testKey1 = new TestKey("Hello");
        TestKey testKey2 = new TestKey("World");
        History history = History.of(testKey1, testKey2);

        assertThat(history.lastIndexOf(testKey2)).isEqualTo(1);
        assertThat(history.lastIndexOf(testKey1)).isEqualTo(0);
    }

    @Test
    public void clear() throws Exception {
        TestKey testKey1 = new TestKey("Hello");
        TestKey testKey2 = new TestKey("World");
        History history = History.of(testKey1, testKey2);

        try {
            history.clear();
            fail();
        } catch(UnsupportedOperationException e) {
            // OK!
        }
    }

    @Test
    public void addAll() throws Exception {
        TestKey testKey1 = new TestKey("Hello");
        TestKey testKey2 = new TestKey("World");
        TestKey testKey3 = new TestKey("Kappa");
        History history = History.of(testKey1);

        List<Object> objects = new ArrayList<>(2);
        objects.add(testKey2);
        objects.add(testKey3);

        try {
            history.addAll(objects);
            fail();
        } catch(UnsupportedOperationException e) {
            // OK!
        }
    }

    @Test
    public void addAllAt() throws Exception {
        TestKey testKey1 = new TestKey("Hello");
        TestKey testKey2 = new TestKey("World");
        TestKey testKey3 = new TestKey("Kappa");
        History history = History.of(testKey1, testKey2);

        List<Object> objects = new ArrayList<>(1);
        objects.add(testKey3);

        try {
            history.addAll(1, objects);
            fail();
        } catch(UnsupportedOperationException e) {
            // OK!
        }
    }


    @Test
    public void iterator() throws Exception {
        TestKey testKey1 = new TestKey("Hello");
        TestKey testKey2 = new TestKey("World");
        TestKey testKey3 = new TestKey("Kappa");
        History history = History.of(testKey1, testKey2, testKey3);
        for(Object key : history) {
            assertThat(key).isIn(testKey1, testKey2, testKey3);
        }
    }

    @Test
    public void listIterator() throws Exception {
        TestKey testKey1 = new TestKey("Hello");
        TestKey testKey2 = new TestKey("World");
        TestKey testKey3 = new TestKey("Kappa");
        History history = History.of(testKey1, testKey2, testKey3);
        Iterator<Object> keys = history.listIterator();
        while(keys.hasNext()) {
            Object key = keys.next();
            assertThat(key).isIn(testKey1, testKey2, testKey3);
        }
    }

    @Test
    public void subList() throws Exception {
        TestKey testKey1 = new TestKey("Hello");
        TestKey testKey2 = new TestKey("World");
        TestKey testKey3 = new TestKey("Kappa");
        History history = History.of(testKey1, testKey2, testKey3);
        assertThat(history.subList(1, 3)).containsExactly(testKey2, testKey3);
    }

    @Test
    public void equals() throws Exception {
        TestKey testKey1 = new TestKey("Hello");
        TestKey testKey2 = new TestKey("World");
        TestKey testKey3 = new TestKey("Kappa");
        History history1 = History.of(testKey1, testKey2, testKey3);
        History history2 = History.of(testKey1, testKey2, testKey3);
        assertThat(history1.equals(history2)).isTrue();

        History history3 = History.of(testKey1, testKey2);
        assertThat(history1.equals(history3)).isFalse();
    }

    @Test
    public void isEmpty() throws Exception {
        TestKey testKey1 = new TestKey("Hello");
        TestKey testKey2 = new TestKey("World");
        TestKey testKey3 = new TestKey("Kappa");
        History history1 = History.of(testKey1, testKey2, testKey3);
        assertThat(history1.isEmpty()).isFalse();
        assertThat(History.of().isEmpty()).isTrue();
    }

    @Test
    public void contains() throws Exception {
        TestKey testKey1 = new TestKey("Hello");
        TestKey testKey2 = new TestKey("World");
        TestKey testKey3 = new TestKey("Kappa");
        History history = History.of(testKey1, testKey2);
        assertThat(history.contains(testKey2)).isTrue();
        assertThat(history.contains(testKey3)).isFalse();
    }

    @Test
    public void containsAll() throws Exception {
        TestKey testKey1 = new TestKey("Hello");
        TestKey testKey2 = new TestKey("World");
        TestKey testKey3 = new TestKey("Kappa");
        History history = History.of(testKey1, testKey2);
        assertThat(history.contains(testKey2)).isTrue();
        assertThat(history.contains(testKey3)).isFalse();
    }

    @Test
    public void removeAll() throws Exception {
        TestKey testKey1 = new TestKey("Hello");
        TestKey testKey2 = new TestKey("World");
        TestKey testKey3 = new TestKey("Kappa");
        List<Object> list = new ArrayList<>();
        list.add(testKey3);
        History history = History.of(testKey1, testKey2);
        try {
            history.removeAll(list);
            fail();
        } catch(UnsupportedOperationException e) {
            // OK!
        }
    }

    @Test
    public void retainAll() throws Exception {
        TestKey testKey1 = new TestKey("Hello");
        TestKey testKey2 = new TestKey("World");
        TestKey testKey3 = new TestKey("Kappa");
        List<Object> list = new ArrayList<>();
        list.add(testKey3);
        History history = History.of(testKey1, testKey2, testKey3);
        try {
            history.retainAll(list);
            fail();
        } catch(UnsupportedOperationException e) {
            // OK!
        }
    }

    @Test
    public void get() throws Exception {
        TestKey testKey1 = new TestKey("Hello");
        TestKey testKey2 = new TestKey("World");
        TestKey testKey3 = new TestKey("Kappa");
        History history = History.of(testKey1, testKey2, testKey3);
        assertThat(history.get(0)).isSameAs(testKey1);
        assertThat(history.get(1)).isSameAs(testKey2);
        assertThat(history.get(2)).isSameAs(testKey3);
    }

    @Test
    public void size() throws Exception {
        TestKey testKey1 = new TestKey("Hello");
        TestKey testKey2 = new TestKey("World");
        TestKey testKey3 = new TestKey("Kappa");
        History history = History.of(testKey1, testKey2, testKey3);
        assertThat(history.size()).isEqualTo(3);
    }
}