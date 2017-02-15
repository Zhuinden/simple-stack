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
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Convenience class for creating ArrayList of Parcelables, for backstack history.
 */
public class HistoryBuilder
        implements Iterable<Parcelable> {
    private ArrayList<Parcelable> list = new ArrayList<>();

    private HistoryBuilder() { // use newBuilder()
    }

    /**
     * Creates a new history builder based on the {@link Backstack}'s history.
     *
     * @param backstack the {@link Backstack}.
     * @return the newly created {@link HistoryBuilder}.
     */
    public static HistoryBuilder from(@NonNull Backstack backstack) {
        if(backstack == null) {
            throw new IllegalArgumentException("Backstack cannot be null!");
        }
        return from(backstack.getHistory());
    }

    /**
     * Creates a new history builder based on the {@link BackstackDelegate)}'s managed backstack history.
     *
     * @param backstackDelegate the {@link BackstackDelegate}.
     * @return the newly created {@link HistoryBuilder}.
     */
    public static HistoryBuilder from(@NonNull BackstackDelegate backstackDelegate) {
        if(backstackDelegate == null) {
            throw new IllegalArgumentException("BackstackDelegate cannot be null!");
        }
        return from(backstackDelegate.getBackstack());
    }

    /**
     * Creates a new history builder from the provided ordered collection.
     *
     * @param keys
     * @return the newly created {@link HistoryBuilder}.
     */
    public static HistoryBuilder from(@NonNull List<? extends Parcelable> keys) {
        return newBuilder().addAll(keys);
    }

    /**
     * Creates a new empty history builder.
     *
     * @return the newly created {@link HistoryBuilder}.
     */
    public static HistoryBuilder newBuilder() {
        return new HistoryBuilder();
    }

    /**
     * Creates a new array list of parcelable that contains only the provided key.
     *
     * @param key
     * @return an array list of parcelable that contains the key.
     */
    public static ArrayList<Parcelable> single(@NonNull Parcelable key) {
        return newBuilder()
                .add(key)
                .build();
    }

    /**
     * Adds the keys to the builder.
     *
     * @param keys
     * @return the current builder.
     */
    public HistoryBuilder addAll(@NonNull List<? extends Parcelable> keys) {
        if(keys == null) {
            throw new IllegalArgumentException("Provided collection cannot be null");
        }
        this.list.addAll(keys);
        return this;
    }

    /**
     * Adds the keys to the builder at a given index.
     *
     * @param keys
     * @param index
     * @return the current builder.
     */
    public HistoryBuilder addAllAt(@NonNull List<? extends Parcelable> keys, int index) {
        if(keys == null) {
            throw new IllegalArgumentException("Provided collection cannot be null");
        }
        this.list.addAll(index, keys);
        return this;
    }

    /**
     * Clears the history builder.
     *
     * @return the current builder.
     */
    public HistoryBuilder clear() {
        list.clear();
        return this;
    }

    /**
     * Returns if the given key is contained within the builder.
     *
     * @param key
     * @return true if the builder contains the given key.
     */
    public boolean contains(@NonNull Parcelable key) {
        checkKey(key);
        return list.contains(key);
    }

    /**
     * Returns if the builder contains all provided keys.
     *
     * @param keys
     * @return true if the builder contains all keys.
     */
    public boolean containsAll(@NonNull Collection<Parcelable> keys) {
        if(keys == null) {
            throw new IllegalArgumentException("Keys cannot be null!");
        }
        return list.containsAll(keys);
    }

    /**
     * Returns the size of the builder.
     *
     * @return the number of keys in the builder.
     */
    public int size() {
        return list.size();
    }

    /**
     * Removes the given key from the builder.
     *
     * @param key
     * @return the current builder.
     */
    public HistoryBuilder remove(@NonNull Parcelable key) {
        checkKey(key);
        list.remove(key);
        return this;
    }

    /**
     * Remove the key at the given index.
     *
     * @param index
     * @return the current builder.
     */
    public HistoryBuilder removeAt(int index) {
        list.remove(index);
        return this;
    }

    /**
     * Removes all keys from the builder not contained inside the provided keys.
     *
     * @param keys
     * @return the current builder.
     */
    public HistoryBuilder retainAll(@NonNull Collection<Parcelable> keys) {
        checkKeys(keys);
        list.retainAll(keys);
        return this;
    }

    /**
     * Returns if the builder is empty.
     *
     * @return true if the builder does not contain any keys
     */
    public boolean isEmpty() {
        return list.isEmpty();
    }

    /**
     * Removes the last entry in the builder.
     * If the builder is empty, an exception is thrown.
     *
     * @return the current builder.
     */
    public HistoryBuilder removeLast() {
        if(list.isEmpty()) {
            throw new IllegalStateException("Cannot remove element from empty builder");
        }
        list.remove(list.size() - 1);
        return this;
    }

    /**
     * Removes all keys until the provided key is found.
     * If the key is not found, an exception is thrown.
     *
     * @param key
     * @return the current builder.
     */
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

    /**
     * Returns the index of the provided key.
     *
     * @param key
     * @return the index, -1 if not found.
     */
    public int indexOf(@NonNull Parcelable key) {
        checkKey(key);
        return list.indexOf(key);
    }

    /**
     * Returns the key at the given index.
     *
     * @param index
     * @return the key at the given index
     */
    public <T extends Parcelable> T get(int index) {
        // noinspection unchecked
        return (T) list.get(index);
    }

    /**
     * Returns the last element of the builder.
     * If the builder is empty, null is returned.
     *
     * @return the key at the last index
     */
    @Nullable
    public <T extends Parcelable> T getLast() {
        // noinspection unchecked
        return (T)(list.isEmpty() ? null : list.get(list.size() - 1));
    }

    /**
     * Adds the provided key as the last element of the builder.
     *
     * @param key
     * @return the current builder.
     */
    public HistoryBuilder add(@NonNull Parcelable key) {
        checkKey(key);
        list.add(key);
        return this;
    }

    /**
     * Adds the provided key at the provided index.
     *
     * @param key
     * @param index
     * @return the current builder.
     */
    public HistoryBuilder add(@NonNull Parcelable key, int index) {
        checkKey(key);
        list.add(index, key);
        return this;
    }

    /**
     * Provides an iterator for the builder.
     *
     * @return the iterator
     */
    @Override
    public Iterator<Parcelable> iterator() {
        return list.iterator();
    }

    /**
     * Creates a copied version of the builder as an ArrayList.
     *
     * @return the built history.
     */
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