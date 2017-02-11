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

    public static HistoryBuilder from(@NonNull List<? extends Parcelable> collection) {
        return newBuilder()
                .addAll(collection);
    }

    public static HistoryBuilder newBuilder() {
        return new HistoryBuilder();
    }

    public static ArrayList<Parcelable> single(@NonNull Parcelable key) {
        return newBuilder()
                .add(key)
                .build();
    }

    public HistoryBuilder addAll(@NonNull List<? extends Parcelable> collection) {
        if(collection == null) {
            throw new IllegalArgumentException("Provided collection cannot be null");
        }
        this.list.addAll(collection);
        return this;
    }

    public HistoryBuilder addAllAt(@NonNull List<? extends Parcelable> collection, int index) {
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

    public boolean contains(@NonNull Parcelable key) {
        checkKey(key);
        return list.contains(key);
    }

    public boolean containsAll(@NonNull Collection<Parcelable> keys) {
        if(keys == null) {
            throw new IllegalArgumentException("Keys cannot be null!");
        }
        return list.containsAll(keys);
    }

    public int size() {
        return list.size();
    }

    public HistoryBuilder remove(@NonNull Parcelable key) {
        checkKey(key);
        list.remove(key);
        return this;
    }

    public HistoryBuilder removeAt(int index) {
        list.remove(index);
        return this;
    }

    public HistoryBuilder retainAll(@NonNull Collection<Parcelable> keys) {
        checkKeys(keys);
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

    public HistoryBuilder removeUntil(@NonNull Parcelable key) {
        checkKey(key);
        while(!list.isEmpty() && !getLast().equals(key)) {
            removeLast();
        }
        if(list.isEmpty()) {
            throw new IllegalArgumentException("[" + key + "] was not found in history!");
        }
        return this;
    }

    public int indexOf(@NonNull Parcelable key) {
        checkKey(key);
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

    public HistoryBuilder add(@NonNull Parcelable key) {
        checkKey(key);
        list.add(key);
        return this;
    }

    public HistoryBuilder add(@NonNull Parcelable key, int index) {
        checkKey(key);
        list.add(index, key);
        return this;
    }

    public ArrayList<Parcelable> build() {
        return new ArrayList<>(this.list);
    }

    // validations
    private void checkKey(Parcelable key) {
        if(key == null) {
            throw new IllegalArgumentException("History key cannot be null!");
        }
    }

    private void checkKeys(Collection<Parcelable> keys) {
        if(keys == null) {
            throw new IllegalArgumentException("Keys cannot be null!");
        }
    }
}