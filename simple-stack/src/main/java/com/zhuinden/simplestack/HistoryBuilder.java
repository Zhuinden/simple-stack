package com.zhuinden.simplestack;

import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Convenience class for creating `ArrayList of Parcelable` for backstack history.
 *
 * Created by Zhuinden on 2017. 01. 16..
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

    public HistoryBuilder addAllAt(List<? extends Parcelable> collection, int index) {
        if(collection == null) {
            throw new IllegalArgumentException("Provided collection cannot be null");
        }
        this.list.addAll(index, collection);
        return this;
    }

    public HistoryBuilder clear() {
        list.clear();
        return this;
    }

    public boolean contains(Parcelable key) {
        return list.contains(key);
    }

    public boolean containsAll(Collection<Parcelable> keys) {
        return list.containsAll(keys);
    }

    public int size() {
        return list.size();
    }

    public HistoryBuilder remove(Parcelable key) {
        list.remove(key);
        return this;
    }

    public HistoryBuilder removeAt(int index) {
        list.remove(index);
        return this;
    }

    public HistoryBuilder retainAll(Collection<Parcelable> keys) {
        list.retainAll(keys);
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
        while(!list.isEmpty() && !getLast().equals(key)) {
            removeLast();
        }
        if(list.isEmpty()) {
            throw new IllegalArgumentException("[" + key + "] was not found in history!");
        }
        return this;
    }

    public int indexOf(Parcelable key) {
        return list.indexOf(key);
    }

    public <T extends Parcelable> T get(int index) {
        // noinspection unchecked
        return (T) list.get(index);
    }

    public <T extends Parcelable> T getLast() {
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

    public HistoryBuilder add(Parcelable key, int index) {
        if(key == null) {
            throw new IllegalArgumentException("History key cannot be null");
        }
        list.add(index, key);
        return this;
    }

    public ArrayList<Parcelable> build() {
        return new ArrayList<>(this.list);
    }
}