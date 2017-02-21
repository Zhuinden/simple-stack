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

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Owner on 2017. 01. 20..
 */

public class HistoryBuilderTest {
    @Test
    public void containsWorks()
            throws Exception {
        TestKey hello = new TestKey("hello");
        TestKey world = new TestKey("world");
        TestKey nyeh = new TestKey("nyeh");
        HistoryBuilder historyBuilder = HistoryBuilder.from(hello, world);
        assertThat(historyBuilder.contains(hello)).isTrue();
        assertThat(historyBuilder.contains(world)).isTrue();
        assertThat(historyBuilder.contains(nyeh)).isFalse();
    }

    @Test
    public void containsAllWorks()
            throws Exception {
        TestKey hello = new TestKey("hello");
        TestKey world = new TestKey("world");
        TestKey nyeh = new TestKey("nyeh");
        HistoryBuilder historyBuilder = HistoryBuilder.from(hello, world);
        assertThat(historyBuilder.containsAll(HistoryBuilder.from(hello, world).build())).isTrue();
        assertThat(historyBuilder.containsAll(HistoryBuilder.from(hello, nyeh).build())).isFalse();
    }

    @Test
    public void sizeWorks()
            throws Exception {
        TestKey hello = new TestKey("hello");
        TestKey world = new TestKey("world");
        TestKey nyeh = new TestKey("nyeh");
        HistoryBuilder historyBuilder = HistoryBuilder.from(hello, world);
        assertThat(historyBuilder.size()).isEqualTo(2);
    }

    @Test
    public void removeAtWorks()
            throws Exception {
        TestKey hello = new TestKey("hello");
        TestKey world = new TestKey("world");
        TestKey nyeh = new TestKey("nyeh");
        HistoryBuilder historyBuilder = HistoryBuilder.from(hello, world, nyeh);
        assertThat(historyBuilder.size()).isEqualTo(3);
        historyBuilder.removeAt(1);
        assertThat(historyBuilder.build()).containsExactly(hello, nyeh);
        assertThat(historyBuilder.size()).isEqualTo(2);
    }

    @Test
    public void retainAllWorks()
            throws Exception {
        final TestKey hello = new TestKey("hello");
        final TestKey world = new TestKey("world");
        final TestKey nyeh = new TestKey("nyeh");
        HistoryBuilder historyBuilder = HistoryBuilder.from(hello, world, nyeh);
        historyBuilder.retainAll(new ArrayList<Object>() {{
            add(hello);
            add(nyeh);
        }});
        assertThat(historyBuilder.build()).containsExactly(hello, nyeh);
        assertThat(historyBuilder.build()).doesNotContain(world);
    }

    @Test
    public void isEmptyWorks()
            throws Exception {
        TestKey test = new TestKey("hello");
        HistoryBuilder historyBuilder = HistoryBuilder.newBuilder();
        assertThat(historyBuilder.isEmpty()).isTrue();
        historyBuilder.add(test);
        assertThat(historyBuilder.isEmpty()).isFalse();
    }

    @Test
    public void indexOfWorks()
            throws Exception {
        final TestKey hello = new TestKey("hello");
        final TestKey world = new TestKey("world");
        final TestKey nyeh = new TestKey("nyeh");
        HistoryBuilder historyBuilder = HistoryBuilder.from(hello, world, nyeh);
        assertThat(historyBuilder.indexOf(hello)).isEqualTo(0);
        assertThat(historyBuilder.indexOf(world)).isEqualTo(1);
        assertThat(historyBuilder.indexOf(nyeh)).isEqualTo(2);
    }

    @Test
    public void getWorks() {
        final TestKey hello = new TestKey("hello");
        final TestKey world = new TestKey("world");
        final TestKey nyeh = new TestKey("nyeh");
        HistoryBuilder historyBuilder = HistoryBuilder.from(hello, world, nyeh);
        assertThat(historyBuilder.get(0)).isEqualTo(hello);
        assertThat(historyBuilder.get(1)).isEqualTo(world);
        assertThat(historyBuilder.get(2)).isEqualTo(nyeh);
    }

    @Test
    public void clearWorks() {
        final TestKey hello = new TestKey("hello");
        final TestKey world = new TestKey("world");
        final TestKey nyeh = new TestKey("nyeh");
        HistoryBuilder historyBuilder = HistoryBuilder.from(hello, world, nyeh);
        assertThat(historyBuilder.size()).isEqualTo(3);
        historyBuilder.clear();
        assertThat(historyBuilder).isEmpty();
    }

    @Test
    public void addAtIndexWorks() {
        final TestKey hello = new TestKey("hello");
        final TestKey world = new TestKey("world");
        final TestKey nyeh = new TestKey("nyeh");
        HistoryBuilder historyBuilder = HistoryBuilder.from(hello, world);
        assertThat(historyBuilder).containsExactly(hello, world);
        historyBuilder.add(nyeh, 1);
        assertThat(historyBuilder).containsExactly(hello, nyeh, world);
    }

    @Test
    public void removeWorks() {
        final TestKey hello = new TestKey("hello");
        final TestKey world = new TestKey("world");
        HistoryBuilder historyBuilder = HistoryBuilder.from(hello, world);
        assertThat(historyBuilder).containsExactly(hello, world);
        historyBuilder.remove(hello);
        assertThat(historyBuilder).containsExactly(world);
    }

    @Test
    public void addAllAtWorks() {
        final TestKey hello = new TestKey("hello");
        final TestKey world = new TestKey("world");
        final TestKey nyeh = new TestKey("nyeh");
        final TestKey bleh = new TestKey("bleh");
        HistoryBuilder historyBuilder = HistoryBuilder.from(hello, bleh);
        assertThat(historyBuilder).containsExactly(hello, bleh);
        historyBuilder.addAllAt(new ArrayList<Object>() {{
            add(world);
            add(nyeh);
        }}, 1);
        assertThat(historyBuilder).containsExactly(hello, world, nyeh, bleh);
    }

    @Test
    public void removeUntilThrowsIfKeyNotFound() {
        try {
            HistoryBuilder builder = HistoryBuilder.newBuilder().add(new TestKey("hello"));
            builder.removeUntil(new TestKey("bye"));
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // Good!
        }
    }

    @Test
    public void removeUntilShouldThrowIfKeyIsNull() {
        try {
            HistoryBuilder.newBuilder().removeUntil(null);
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // Good!
        }
    }

    @Test
    public void removeUntilRemovesUntil() {
        TestKey hi = new TestKey("hi");
        TestKey hello = new TestKey("hello");
        TestKey bye = new TestKey("bye");
        ArrayList<Object> history = HistoryBuilder.newBuilder().add(hi).add(hello).add(bye).removeUntil(hi).build();
        assertThat(history).containsExactly(hi);
    }

    @Test
    public void getLastReturnsNullIfEmpty() {
        assertThat(HistoryBuilder.newBuilder().getLast()).isNull();
    }

    @Test
    public void removeLastThrowsIfBuilderIsEmpty() {
        try {
            HistoryBuilder.newBuilder().removeLast();
        } catch(IllegalStateException e) {
            // Good!
        }
    }

    @Test
    public void removeLastRemovesLast() {
        TestKey hi = new TestKey("hi");
        TestKey hello = new TestKey("hello");
        TestKey bye = new TestKey("bye");
        HistoryBuilder builder = HistoryBuilder.newBuilder().add(hi).add(hello).add(bye);
        List<Object> history = builder.removeLast().build();
        assertThat(history).containsExactly(hi, hello);
    }

    @Test
    public void getLastReturnsLastIfNotEmpty() {
        TestKey hi = new TestKey("hi");
        TestKey hello = new TestKey("hello");
        TestKey bye = new TestKey("bye");
        HistoryBuilder historyBuilder = HistoryBuilder.newBuilder().add(hi).add(hello).add(bye);
        assertThat(historyBuilder.getLast()).isEqualTo(bye);
        assertThat(historyBuilder.build().get(historyBuilder.build().size() - 1)).isEqualTo(bye);
    }

    @Test
    public void keyCannotBeNull() {
        try {
            HistoryBuilder.single(null);
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // Good!
        }
    }

    @Test
    public void historyBuilderWorksAsIterable() {
        HistoryBuilder historyBuilder = HistoryBuilder.newBuilder().add(new TestKey("hello")).add(new TestKey("bye"));
        int i = 0;
        for(Object _key : historyBuilder) {
            TestKey key = (TestKey) _key;
            if(i == 0) {
                assertThat(key.name).isEqualTo("hello");
            } else if(i == 1) {
                assertThat(key.name).isEqualTo("bye");
            }
            i++;
        }
    }

    @Test
    public void fromObjectsWorks() {
        TestKey a = new TestKey("a");
        TestKey b = new TestKey("b");
        HistoryBuilder historyBuilder = HistoryBuilder.from(a, b);
        assertThat(historyBuilder.build()).containsExactly(a, b);
    }
}
