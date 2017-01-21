package com.zhuinden.simplestack;

import android.os.Parcelable;

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
        ArrayList<Parcelable> history = HistoryBuilder.newBuilder().add(hi).add(hello).add(bye).removeUntil(hi).build();
        assertThat(history).containsExactly(hi);
    }

    @Test
    public void peekReturnsNullIfEmpty() {
        assertThat(HistoryBuilder.newBuilder().peek()).isNull();
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
        List<Parcelable> history = builder.removeLast().build();
        assertThat(history).containsExactly(hi, hello);
    }

    @Test
    public void peekReturnsLastIfNotEmpty() {
        TestKey hi = new TestKey("hi");
        TestKey hello = new TestKey("hello");
        TestKey bye = new TestKey("bye");
        HistoryBuilder historyBuilder = HistoryBuilder.newBuilder().add(hi).add(hello).add(bye);
        assertThat(historyBuilder.peek()).isEqualTo(bye);
        assertThat(historyBuilder.build().get(historyBuilder.build().size()-1)).isEqualTo(bye);
    }

    @Test
    public void collectionCannotBeNull() {
        try {
            HistoryBuilder.from(null);
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // Good!
        }
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
}
