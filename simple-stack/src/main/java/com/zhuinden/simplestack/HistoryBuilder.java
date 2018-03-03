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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Builder for {@link History}.
 *
 * Will be moved to {@link History} as `History.Builder` in 2.0.
 */
public class HistoryBuilder
        implements Iterable<Object> {
    private ArrayList<Object> list = new ArrayList<>();

    HistoryBuilder() { // use History.newBuilder()
    }

    /**
     * Creates a new history builder based on the {@link Backstack}'s history.
     *
     * Deprecated in 1.9, in favor of {@link History#builderFrom(Backstack)}.
     *
     * @param backstack the {@link Backstack}.
     * @return the newly created {@link HistoryBuilder}.
     */
    @NonNull
    @Deprecated
    public static HistoryBuilder from(@NonNull Backstack backstack) {
        if(backstack == null) {
            throw new IllegalArgumentException("Backstack cannot be null!");
        }
        return History.builderFrom(backstack.getHistory());
    }

    /**
     * Creates a new history builder based on the {@link BackstackDelegate}'s managed backstack history.
     *
     * Deprecated in 1.9, in favor of {@link History#builderFrom(BackstackDelegate)}.
     *
     * @param backstackDelegate the {@link BackstackDelegate}.
     * @return the newly created {@link HistoryBuilder}.
     */
    @NonNull
    @Deprecated
    public static HistoryBuilder from(@NonNull BackstackDelegate backstackDelegate) {
        if(backstackDelegate == null) {
            throw new IllegalArgumentException("BackstackDelegate cannot be null!");
        }
        return History.builderFrom(backstackDelegate.getBackstack());
    }

    /**
     * Creates a new history builder from the provided ordered elements.
     *
     * Deprecated in 1.9, in favor of {@link History#builderOf(Object...)}.
     *
     * @param keys
     * @return the newly created {@link HistoryBuilder}.
     */
    @NonNull
    @Deprecated
    public static HistoryBuilder from(Object... keys) {
        return History.builderFrom(Arrays.asList(keys));
    }

    /**
     * Creates a new history builder from the provided ordered collection.
     *
     * Deprecated in 1.9, in favor of {@link History#builderFrom(List)}.
     *
     * @param keys
     * @return the newly created {@link HistoryBuilder}.
     */
    @NonNull
    @Deprecated
    public static HistoryBuilder from(@NonNull List<?> keys) {
        for(Object key : keys) {
            if(key == null) {
                throw new IllegalArgumentException("Cannot provide `null` as a key!");
            }
        }
        return History.newBuilder().addAll(keys);
    }

    /**
     * Creates a new empty history builder.
     *
     * Deprecated in 1.9, in favor of {@link History#newBuilder()}.
     *
     * @return the newly created {@link HistoryBuilder}.
     */
    @NonNull
    @Deprecated
    public static HistoryBuilder newBuilder() {
        return new HistoryBuilder();
    }

    /**
     * Creates a new array list of object that contains only the provided key.
     *
     * Deprecated in 1.9, in favor of {@link History#single(Object)}.
     *
     * @param key
     * @return an array list of object that contains the key.
     */
    @NonNull
    @Deprecated
    public static History<Object> single(@NonNull Object key) {
        return History.newBuilder()
                .add(key)
                .build();
    }

    /**
     * Adds the keys to the builder.
     *
     * @param keys
     * @return the current builder.
     */
    @NonNull
    public HistoryBuilder addAll(@NonNull List<?> keys) {
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
    @NonNull
    public HistoryBuilder addAllAt(@NonNull List<?> keys, int index) {
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
    @NonNull
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
    public boolean contains(@NonNull Object key) {
        checkKey(key);
        return list.contains(key);
    }

    /**
     * Returns if the builder contains all provided keys.
     *
     * @param keys
     * @return true if the builder contains all keys.
     */
    public boolean containsAll(@NonNull Collection<?> keys) {
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
    @NonNull
    public HistoryBuilder remove(@NonNull Object key) {
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
    @NonNull
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
    @NonNull
    public HistoryBuilder retainAll(@NonNull Collection<?> keys) {
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
    @NonNull
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
    @NonNull
    public HistoryBuilder removeUntil(@NonNull Object key) {
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
    public int indexOf(@NonNull Object key) {
        checkKey(key);
        return list.indexOf(key);
    }

    /**
     * Returns the key at the given index.
     *
     * @param index
     * @return the key at the given index
     */
    @NonNull
    public <T> T get(int index) {
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
    public <T> T getLast() {
        // noinspection unchecked
        return (T)(list.isEmpty() ? null : list.get(list.size() - 1));
    }

    /**
     * Adds the provided key as the last element of the builder.
     *
     * @param key
     * @return the current builder.
     */
    @NonNull
    public HistoryBuilder add(@NonNull Object key) {
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
    @NonNull
    public HistoryBuilder add(@NonNull Object key, int index) {
        checkKey(key);
        list.add(index, key);
        return this;
    }

    /**
     * Provides an iterator for the builder.
     *
     * @return the iterator
     */
    @NonNull
    @Override
    public Iterator<Object> iterator() {
        return list.iterator();
    }

    /**
     * Creates the history, which is immutable.
     *
     * @return the built history.
     */
    @NonNull
    public <T> History<T> build() {
        List<T> list = new LinkedList<>();
        for(Object obj: this.list) {
            // noinspection unchecked
            list.add((T)obj);
        }
        return new History<>(list);
    }

    // validations
    private void checkKey(Object key) {
        if(key == null) {
            throw new IllegalArgumentException("History key cannot be null!");
        }
    }

    private void checkKeys(Collection<?> keys) {
        if(keys == null) {
            throw new IllegalArgumentException("Keys cannot be null!");
        }
    }
}