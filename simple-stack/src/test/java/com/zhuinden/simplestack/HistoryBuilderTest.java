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
    public void historyBuilderThrowsForListThatContainsNull() {
        List<Object> nullList = new ArrayList<>();
        nullList.add(null);
        try {
            HistoryBuilder.from(nullList);
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // OK!
        }
    }

    @Test
    public void historyBuilderThrowsForNullBackstack() {
        try {
            HistoryBuilder historyBuilder = History.builderFrom((Backstack)null);
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // OK!
        }
    }

    @Test
    public void historyBuilderThrowsForAddingNull() {
        HistoryBuilder historyBuilder = History.newBuilder();
        try {
            historyBuilder.add(null);
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // OK!
        }
    }

    @Test
    public void historyBuilderThrowsFromNull() {
        try {
            HistoryBuilder.from((Object)null).build();
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // OK!
        }
    }

    @Test
    public void removeUntilThrowsIfKeyNotFound() {
        try {
            HistoryBuilder builder = History.newBuilder().add(new TestKey("hello"));
            builder.removeUntil(new TestKey("bye"));
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // Good!
        }
    }

    @Test
    public void removeUntilShouldThrowIfKeyIsNull() {
        try {
            History.newBuilder().removeUntil(null);
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
        List<Object> history = History.newBuilder().add(hi).add(hello).add(bye).removeUntil(hi).build();
        assertThat(history).containsExactly(hi);
    }

    @Test
    public void getLastReturnsNullIfEmpty() {
        assertThat(History.newBuilder().getLast()).isNull();
    }

    @Test
    public void removeLastThrowsIfBuilderIsEmpty() {
        try {
            History.newBuilder().removeLast();
        } catch(IllegalStateException e) {
            // Good!
        }
    }

    @Test
    public void removeLastRemovesLast() {
        TestKey hi = new TestKey("hi");
        TestKey hello = new TestKey("hello");
        TestKey bye = new TestKey("bye");
        HistoryBuilder builder = History.newBuilder().add(hi).add(hello).add(bye);
        List<Object> history = builder.removeLast().build();
        assertThat(history).containsExactly(hi, hello);
    }

    @Test
    public void getLastReturnsLastIfNotEmpty() {
        TestKey hi = new TestKey("hi");
        TestKey hello = new TestKey("hello");
        TestKey bye = new TestKey("bye");
        HistoryBuilder historyBuilder = History.newBuilder().add(hi).add(hello).add(bye);
        assertThat(historyBuilder.getLast()).isEqualTo(bye);
        assertThat(historyBuilder.build().get(historyBuilder.build().size()-1)).isEqualTo(bye);
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
        HistoryBuilder historyBuilder = History.newBuilder().add(new TestKey("hello")).add(new TestKey("bye"));
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
}
