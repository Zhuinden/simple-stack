package com.zhuinden.simplestack;

import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Convenience class for creating `ArrayList<Parcelable>` for backstack history.
 *
 * Created by Owner on 2017. 01. 16..
 */
public class HistoryBuilder {
    private ArrayList<Parcelable> list = new ArrayList<>();

    private HistoryBuilder() { // use newBuilder()
    }

    public static HistoryBuilder from(List<? extends Parcelable> collection) {
        return newBuilder()
                .addAll(collection);
    }

    public static HistoryBuilder newBuilder() {
        return new HistoryBuilder();
    }

    public static ArrayList<Parcelable> single(Parcelable key) {
        return newBuilder()
                .add(key)
                .build();
    }

    public HistoryBuilder addAll(List<? extends Parcelable> collection) {
        if(collection == null) {
            throw new IllegalArgumentException("Provided collection cannot be null");
        }
        this.list.addAll(collection);
        return this;
    }

    public HistoryBuilder removeLast() {
        if(list.isEmpty()) {
            throw new IllegalStateException("Cannot remove element from empty list");
        }
        list.remove(list.size() - 1);
        return this;
    }

    public HistoryBuilder removeUntil(Parcelable key) {
        if(key == null) {
            throw new IllegalArgumentException("History key cannot be null");
        }
        while(!list.isEmpty() && !peek().equals(key)) {
            removeLast();
        }
        if(list.isEmpty()) {
            throw new IllegalArgumentException("[" + key + "] was not found in history!");
        }
        return this;
    }

    public <T extends Parcelable> T peek() {
        // noinspection unchecked
        return (T)(list.isEmpty() ? null : list.get(list.size() - 1));
    }

    public HistoryBuilder add(Parcelable key) {
        if(key == null) {
            throw new IllegalArgumentException("History key cannot be null");
        }
        list.add(key);
        return this;
    }

    public ArrayList<Parcelable> build() {
        ArrayList<Parcelable> list = new ArrayList<>(this.list);
        return list;
    }
}