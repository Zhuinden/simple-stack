package com.zhuinden.simplestack;

import android.annotation.TargetApi;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * An immutable wrapper over backstack history with some additional helper methods.
 */
public class History<T> extends AbstractList<T> implements List<T> {
    private final List<T> elements;
    
    History() {
        this(Collections.<T>emptyList());
    }
    
    History(List<T> elements) {
        this.elements = Collections.unmodifiableList(new ArrayList<>(elements));
    }

    // operations

    /**
     * Returns the last element in the list, or null if the history is empty.
     *
     * @param <K> the type of the key
     * @return the top key
     */
    @Nullable
    public <K> K top() {
        if(this.isEmpty()) {
            return null;
        }
        // noinspection unchecked
        return (K) this.get(this.size() - 1);
    }

    /**
     * Returns the element indexed from the top.
     *
     * Offset value `0` behaves the same as {@link History#top()}, while `1` returns the one before it.
     * Negative indices are wrapped around, for example `-1` is the first element of the stack, `-2` the second, and so on.
     *
     * Accepted values are in range of [-size, size).
     *
     * @throws IllegalStateException if the history doesn't contain any elements yet.
     * @throws IllegalArgumentException if the provided offset is outside the range of [-size, size).
     *
     * @param offset the offset from the top
     * @param <K> the type of the key
     * @return the key from the top with offset
     */
    @NonNull
    public <K> K fromTop(int offset) {
        int size = this.size();
        if(size <= 0) {
            throw new IllegalStateException("Cannot obtain elements from an uninitialized history.");
        }
        if(offset < -size || offset >= size) {
            throw new IllegalArgumentException("The provided offset value [" + offset + "] was out of range: [" + -size + "; " + size + ")");
        }
        while(offset < 0) {
            offset += size;
        }
        offset %= size;
        int target = (size - 1 - offset) % size;
        // noinspection unchecked
        return (K) this.get(target);
    }

    /**
     * Returns the root (bottom / first) element of this history, or null if it's empty.
     *
     * @param <K> the type of the key
     * @return the root (bottom) key
     */
    @Nullable
    public <K> K root() {
        if(isEmpty()) {
            return null;
        }
        // noinspection unchecked
        return (K) get(0);
    }

    // factories

    /**
     * Creates a {@link HistoryBuilder} from this {@link History} to create a modified version of it.
     *
     * @return the history builder
     */
    @NonNull
    public HistoryBuilder buildUpon() {
        return History.builderFrom(this);
    }
    
    /**
     * Creates a new history from the provided keys.
     *
     * @param keys the provided keys
     * @param <T> possible base type of the keys
     * @return the history
     */
    @SuppressWarnings("unchecked") // @SafeVarargs is API 19+
    @NonNull
    public static <T> History<T> of(T... keys) {
        if(keys == null) {
            throw new IllegalArgumentException("Cannot provide `null` as a key!");
        }
        for(Object key : keys) {
            if(key == null) {
                throw new IllegalArgumentException("Cannot provide `null` as a key!");
            }
        }
        return builderFrom(Arrays.asList(keys)).build();
    }

    /**
     * Creates a new history from the provided keys.
     *
     * @param keys the provided keys
     * @param <T> possible base type of the keys
     * @return the history
     */
    @NonNull
    public static <T> History<T> from(@NonNull List<? extends T> keys) {
        return builderFrom(keys).build();
    }

    /**
     * Creates a new history builder based on the {@link Backstack}'s history.
     *
     * @param backstack the {@link Backstack}.
     * @return the newly created {@link HistoryBuilder}.
     */
    @NonNull
    public static HistoryBuilder builderFrom(@NonNull Backstack backstack) {
        if(backstack == null) {
            throw new IllegalArgumentException("Backstack cannot be null!");
        }
        return newBuilder().addAll(backstack.getHistory());
    }

    /**
     * Creates a new history builder based on the {@link BackstackDelegate}'s managed backstack history.
     *
     * @param backstackDelegate the {@link BackstackDelegate}.
     * @return the newly created {@link HistoryBuilder}.
     */
    @NonNull
    public static HistoryBuilder builderFrom(@NonNull BackstackDelegate backstackDelegate) {
        if(backstackDelegate == null) {
            throw new IllegalArgumentException("BackstackDelegate cannot be null!");
        }
        return builderFrom(backstackDelegate.getBackstack());
    }

    /**
     * Creates a new history builder from the provided ordered elements.
     *
     * @param keys
     * @return the newly created {@link HistoryBuilder}.
     */
    @SuppressWarnings("unchecked") // @SafeVarargs is API 19+
    @NonNull
    public static HistoryBuilder builderOf(Object... keys) {
        return builderFrom(Arrays.asList(keys));
    }

    /**
     * Creates a new history builder from the provided ordered collection.
     *
     * @param keys
     * @return the newly created {@link HistoryBuilder}.
     */
    @NonNull
    public static HistoryBuilder builderFrom(@NonNull List<?> keys) {
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
     * @return the newly created {@link HistoryBuilder}.
     */
    @NonNull
    public static HistoryBuilder newBuilder() {
        return new HistoryBuilder();
    }

    /**
     * Creates a new history that contains only the provided key.
     *
     * @param key
     * @return a history that contains the key.
     */
    @NonNull
    public static <T> History<T> single(@NonNull T key) {
        return History.newBuilder()
                .add(key)
                .build();
    }

    // delegations
    @Override
    public boolean add(T t) {
        return elements.add(t);
    }

    @Override
    public T set(int index, T element) {
        return elements.set(index, element);
    }

    @Override
    public void add(int index, T element) {
        elements.add(index, element);
    }

    @Override
    public T remove(int index) {
        return elements.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return elements.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return elements.lastIndexOf(o);
    }

    @Override
    public void clear() {
        elements.clear();
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        return elements.addAll(index, c);
    }

    @NonNull
    @Override
    public Iterator<T> iterator() {
        return elements.iterator();
    }

    @NonNull
    @Override
    public ListIterator<T> listIterator() {
        return elements.listIterator();
    }

    @NonNull
    @Override
    public ListIterator<T> listIterator(int index) {
        return elements.listIterator(index);
    }

    @NonNull
    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return elements.subList(fromIndex, toIndex);
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) {
            return false;
        }
        if(!(o instanceof History)) {
            return false;
        }
        return elements.equals(((History)o).elements);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + elements.hashCode();
        return result;
    }

    @Override
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return elements.contains(o);
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return elements.toArray();
    }

    @NonNull
    @Override
    public <T1> T1[] toArray(@NonNull T1[] a) {
        return elements.toArray(a);
    }

    @Override
    public boolean remove(Object o) {
        return elements.remove(o);
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> c) {
        return elements.containsAll(c);
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends T> c) {
        return elements.addAll(c);
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        return elements.removeAll(c);
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> c) {
        return elements.retainAll(c);
    }

    @Override
    public String toString() {
        return Arrays.toString(elements.toArray());
    }

    @Override
    public T get(int index) {
        return elements.get(index);
    }

    @Override
    public int size() {
        return elements.size();
    }

    @Override
    @TargetApi(24)
    public boolean removeIf(Predicate<? super T> filter) {
        return elements.removeIf(filter);
    }

    @Override
    @TargetApi(24)
    public void replaceAll(UnaryOperator<T> operator) {
        elements.replaceAll(operator);
    }

    @Override
    @TargetApi(24)
    public void sort(Comparator<? super T> c) {
        elements.sort(c);
    }

    @Override
    @TargetApi(24)
    public Spliterator<T> spliterator() {
        return elements.spliterator();
    }

    @Override
    @TargetApi(24)
    public Stream<T> stream() {
        return elements.stream();
    }

    @Override
    @TargetApi(24)
    public Stream<T> parallelStream() {
        return elements.parallelStream();
    }

    @Override
    @TargetApi(24)
    public void forEach(Consumer<? super T> action) {
        elements.forEach(action);
    }
}
