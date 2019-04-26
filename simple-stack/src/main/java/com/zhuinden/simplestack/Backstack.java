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

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

import java.lang.annotation.Retention;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * The {@link Backstack} holds the current state, in the form of a list of Objects.
 * It queues up {@link KeyChange}s while a {@link KeyChanger} is not available.
 * When a {@link KeyChanger} is available, it attempts to execute the queued {@link KeyChange}s.
 * A {@link KeyChanger} can be either set to {@link Backstack#INITIALIZE}, or to {@link Backstack#REATTACH}.
 * {@link Backstack#INITIALIZE} begins an initializing {@link KeyChange} to set up initial state, {@link Backstack#REATTACH} does not.
 */
public class Backstack {
    public static <K> K getKey(@NonNull Context context) {
        return KeyContextWrapper.getKey(context);
    }

    //
    @Retention(SOURCE)
    @IntDef({INITIALIZE, REATTACH})
    private @interface KeyChangerRegisterMode {
    }

    public static final int INITIALIZE = 0;
    public static final int REATTACH = 1;
    //

    private final List<Object> originalStack = new ArrayList<>();

    private final List<Object> initialKeys;
    private List<Object> initialParameters;
    private List<Object> stack = originalStack;

    private LinkedList<PendingKeyChange> queuedKeyChanges = new LinkedList<>();

    private KeyChanger keyChanger;

    private final long threadId = Thread.currentThread().getId();

    /**
     * Creates the Backstack with the provided initial keys.
     *
     * @param initialKeys
     */
    public Backstack(@NonNull Object... initialKeys) {
        if(initialKeys == null || initialKeys.length <= 0) {
            throw new IllegalArgumentException("At least one initial key must be defined");
        }
        this.initialKeys = Collections.unmodifiableList(new ArrayList<>(Arrays.asList(initialKeys)));
        setInitialParameters(new ArrayList<>(this.initialKeys));
    }

    /**
     * Creates the Backstack with the provided initial keys.
     *
     * @param initialKeys
     */
    public Backstack(@NonNull List<?> initialKeys) {
        if(initialKeys == null) {
            throw new NullPointerException("Initial key list should not be null");
        }
        if(initialKeys.size() <= 0) {
            throw new IllegalArgumentException("Initial key list should contain at least one element");
        }
        this.initialKeys = Collections.unmodifiableList(new ArrayList<>(initialKeys));
        setInitialParameters(new ArrayList<>(this.initialKeys));
    }

    void setInitialParameters(List<?> initialKeys) {
        if(initialKeys == null || initialKeys.size() <= 0) {
            throw new IllegalArgumentException("At least one initial key must be defined");
        }
        this.initialParameters = new ArrayList<>(initialKeys);
    }

    /**
     * Indicates whether a {@link KeyChanger} is set.
     *
     * @return true if a {@link KeyChanger} is set, false otherwise.
     */
    @MainThread
    public boolean hasKeyChanger() {
        assertCorrectThread();

        return keyChanger != null;
    }

    /**
     * Sets a {@link KeyChanger} with {@link Backstack#INITIALIZE} register mode.
     *
     * @param keyChanger the new {@link KeyChanger}, which cannot be null.
     */
    @MainThread
    public void setKeyChanger(@NonNull KeyChanger keyChanger) {
        setKeyChanger(keyChanger, INITIALIZE);
    }

    /**
     * Sets a {@link KeyChanger}.
     *
     * @param keyChanger the new {@link KeyChanger}, which cannot be null.
     * @param registerMode indicates whether the {@link KeyChanger} is to be initialized, or is just reattached.
     */
    @MainThread
    public void setKeyChanger(@NonNull KeyChanger keyChanger, @KeyChangerRegisterMode int registerMode) {
        if(keyChanger == null) {
            throw new NullPointerException("New key changer cannot be null");
        }

        assertCorrectThread();

        this.keyChanger = keyChanger;
        if(registerMode == INITIALIZE && (queuedKeyChanges.size() <= 1 || stack.isEmpty())) {
            if(!beginKeyChangeIfPossible()) {
                ArrayList<Object> newHistory = new ArrayList<>(selectActiveHistory());
                if(stack.isEmpty()) {
                    stack = initialParameters;
                }
                enqueueKeyChange(newHistory, KeyChange.REPLACE, true);
            }
            return;
        }
        beginKeyChangeIfPossible();
    }

    /**
     * Removes the {@link KeyChanger}.
     */
    @MainThread
    public void removeKeyChanger() {
        assertCorrectThread();

        this.keyChanger = null;
    }

    /**
     * Goes to the new key.
     * If the key is found, then it goes backward to the existing key.
     * If the key is not found, then it goes forward to the newly added key.
     *
     * @param newKey the target state.
     */
    @MainThread
    public void goTo(@NonNull Object newKey) {
        checkNewKey(newKey);

        assertCorrectThread();

        List<?> activeHistory = selectActiveHistory();
        History.Builder historyBuilder = History.builderFrom(activeHistory);

        int direction;
        if(historyBuilder.contains(newKey)) {
            historyBuilder.removeUntil(newKey);
            direction = KeyChange.BACKWARD;
        } else {
            historyBuilder.add(newKey);
            direction = KeyChange.FORWARD;
        }
        setHistory(historyBuilder.build(), direction);
    }

    /**
     * Replaces the current top with the provided key.
     * This means removing the current last element, and then adding the new element.
     *
     * @param newTop the new top key
     * @param direction The direction of the {@link KeyChange}: {@link KeyChange#BACKWARD}, {@link KeyChange#FORWARD} or {@link KeyChange#REPLACE}.
     */
    @MainThread
    public void replaceTop(@NonNull Object newTop, @KeyChange.KeyChangeDirection int direction) {
        checkNewKey(newTop);

        assertCorrectThread();

        History.Builder historyBuilder = History.builderFrom(selectActiveHistory());
        if(!historyBuilder.isEmpty()) {
            historyBuilder.removeLast();
        }
        historyBuilder.add(newTop);
        setHistory(historyBuilder.build(), direction);
    }

    /**
     * Goes "up" to the provided element.
     * This means that if the provided element is found anywhere in the history, then the history goes to it.
     * If not found, then the current top is replaced with the provided element.
     *
     * Going up occurs in {@link KeyChange#BACKWARD} direction.
     *
     * @param newKey the new key to go up to
     */
    @MainThread
    public void goUp(@NonNull Object newKey) {
        goUp(newKey, false);
    }

    /**
     * Goes "up" to the provided element.
     * This means that if the provided element is found anywhere in the history, then the history goes to it (unless specified otherwise).
     * If not found, then the current top is replaced with the provided element.
     *
     * Going up occurs in {@link KeyChange#BACKWARD} direction.
     *
     * @param newKey the new key to go up to
     * @param fallbackToBack specifies that if the key is found in the backstack, then the navigation defaults to going back to previous, instead of clearing all keys on top of it to the target.
     */
    @MainThread
    public void goUp(@NonNull Object newKey, boolean fallbackToBack) {
        checkNewKey(newKey);

        assertCorrectThread();

        List<?> activeHistory = selectActiveHistory();
        int size = activeHistory.size();

        if(size <= 1) { // single-element history cannot contain the previous element. Short circuit to replaceTop.
            replaceTop(newKey, KeyChange.BACKWARD);
            return;
        }
        if(activeHistory.contains(newKey)) {
            if(fallbackToBack) {
                setHistory(History.builderFrom(activeHistory).removeLast().build(), KeyChange.BACKWARD);
            } else {
                goTo(newKey);
            }
        } else {
            replaceTop(newKey, KeyChange.BACKWARD);
        }
    }

    /**
     * Moves the provided new key to the top of the backstack.
     * If the key already exists, then it is first removed, and added as the last element.
     * If it doesn't exist, then it is just added as the last element.
     *
     * @param newKey the new key
     * @param asReplace specifies if the direction is {@link KeyChange#REPLACE} or {@link KeyChange#FORWARD}.
     */
    @MainThread
    public void moveToTop(@NonNull Object newKey, boolean asReplace) {
        checkNewKey(newKey);

        assertCorrectThread();

        List<?> activeHistory = selectActiveHistory();
        int direction = asReplace ? KeyChange.REPLACE : KeyChange.FORWARD;

        History.Builder historyBuilder = History.builderFrom(activeHistory);
        if(historyBuilder.contains(newKey)) {
            historyBuilder.remove(newKey);
        }
        historyBuilder.add(newKey);
        setHistory(historyBuilder.build(), direction);
    }

    /**
     * Moves the provided new key to the top of the backstack.
     * If the key already exists, then it is first removed, and added as the last element.
     * If it doesn't exist, then it is just added as the last element.
     *
     * The used direction is {@link KeyChange#FORWARD}.
     *
     * @param newKey the new key
     */
    @MainThread
    public void moveToTop(@NonNull Object newKey) {
        moveToTop(newKey, false);
    }

    /**
     * Jumps to the root of the backstack.
     *
     * This operation counts as a {@link KeyChange#BACKWARD} navigation.
     */
    @MainThread
    public void jumpToRoot() {
        jumpToRoot(KeyChange.BACKWARD);
    }

    /**
     * Jumps to the root of the backstack.
     *
     * @param direction The direction of the {@link KeyChange}: {@link KeyChange#BACKWARD}, {@link KeyChange#FORWARD} or {@link KeyChange#REPLACE}.
     */
    @MainThread
    public void jumpToRoot(@KeyChange.KeyChangeDirection int direction) {
        assertCorrectThread();

        List<?> activeHistory = selectActiveHistory();
        History<?> currentHistory = History.from(activeHistory);
        setHistory(History.of(currentHistory.root()), direction);
    }

    /**
     * Goes "up" once to the provided chain of parents.
     * If the chain of parents is found as previous elements, then it works as back navigation to that chain,, removing all other elements on top of it.
     * If the whole chain is not found, but at least one element of it is found, then the history is kept up to that point, then the chain is added, any duplicate element in the chain is added to the end as part of the chain.
     * If no element of the chain is found in the history, then the current top is removed, and the provided parent chain is added in its place.
     *
     * Going up the chain occurs in {@link KeyChange#BACKWARD} direction.
     *
     * @param parentChain the chain of parents, from oldest to newest.
     */
    @MainThread
    public void goUpChain(@NonNull List<?> parentChain) {
        goUpChain(parentChain, false);
    }

    /**
     * Goes "up" once to the provided chain of parents.
     * If the chain of parents is found as previous elements, then it works as back navigation to that chain, removing all other elements on top of it (unless specified otherwise).
     * If the whole chain is not found, but at least one element of it is found, then the history is kept up to that point, then the chain is added, any duplicate element in the chain is added to the end as part of the chain.
     * If no element of the chain is found in the history, then the current top is removed, and the provided parent chain is added in its place.
     *
     * Going up the chain occurs in {@link KeyChange#BACKWARD} direction.
     *
     * @param parentChain the chain of parents, from oldest to newest.
     * @param fallbackToBack determines that if the chain is fully found in the backstack, then the navigation will default to regular "back" to the previous element, instead of clearing the top elements.
     */
    @MainThread
    public void goUpChain(@NonNull List<?> parentChain, boolean fallbackToBack) {
        checkNewHistory(parentChain);

        assertCorrectThread();

        int parentChainSize = parentChain.size();
        if(parentChainSize == 1) {
            goUp(parentChain.get(0), fallbackToBack);
            return;
        }

        History.Builder historyBuilder = History.builderFrom(selectActiveHistory());
        historyBuilder.removeLast(); // we will never keep the current key on "up" navigation.

        int indexOfSubList = Collections.indexOfSubList(historyBuilder.build(), parentChain);

        if(indexOfSubList != -1) {
            // if the parent chain is found as is, then decide based on fallback what should happen
            if(fallbackToBack) {
                // last item is already removed, and we're defaulting to back.
                setHistory(historyBuilder.build(), KeyChange.BACKWARD);
            } else {
                // we clear all on top of it and go back to the chain
                goTo(parentChain.get(parentChainSize-1));
            }
            //noinspection UnnecessaryReturnStatement
            return;
        } else {
            // now we must check if any element is found in the new history.
            // if it exists, we go to that, and add the remaining chain to it.
            for(int i = 0; i < parentChainSize; i++) {
                Object key = parentChain.get(i);
                if(historyBuilder.contains(key)) {
                    // if the key is found, we'll keep all keys up to it.
                    // the remaining chain will be appended.
                    // if any elements in the chain are duplicates,
                    // they are ordered according to the provided chain.
                    int indexOfKey = historyBuilder.indexOf(key);
                    History.Builder newHistory = History.newBuilder();
                    for(int j = 0; j < indexOfKey; j++) {
                        newHistory.add(historyBuilder.get(j)); // preserve equivalent prefix
                    }
                    for(int j = 0; j < parentChainSize; j++) {
                        Object nextKey = parentChain.get(j);
                        if(newHistory.contains(nextKey)) {
                            // if the new chain reorders previous elements,
                            // then those are reordered according to the parent chain.
                            newHistory.remove(nextKey);
                        }
                        newHistory.add(nextKey);
                    }
                    setHistory(newHistory.build(), KeyChange.BACKWARD);
                    return;
                }
            }

            // no elements in the current history were found in the parent chain
            // default behavior is to add the newly received list in place of the original key
            History.Builder newHistory = historyBuilder.addAll(parentChain);
            setHistory(newHistory.build(), KeyChange.BACKWARD);
        }
    }

    /**
     * Goes back in the history.
     * If the key is found, then it goes backward to the existing key.
     * If the key is not found, then it goes forward to the newly added key.
     *
     * @return true if a key change is pending or is handled with a key change, false if there is only one state left.
     */
    @MainThread
    public boolean goBack() {
        assertCorrectThread();

        if(isKeyChangePending()) {
            return true;
        }
        if(stack.size() <= 1) {
            return false;
        }

        List<?> activeHistory = selectActiveHistory();
        History.Builder historyBuilder = History.builderFrom(activeHistory);
        historyBuilder.removeLast();
        setHistory(historyBuilder.build(), KeyChange.BACKWARD);
        return true;
    }

    private void resetBackstack() {
        stack.clear();
        initialParameters = new ArrayList<>(initialKeys);
    }

    /**
     * Immediately clears the backstack, it is NOT enqueued as a key change.
     *
     * If there are pending key changes, then it throws an exception.
     *
     * You generally don't need to use this method.
     */
    @MainThread
    public void forceClear() {
        assertCorrectThread();
        assertNoKeyChange();
        resetBackstack();
    }

    /**
     * Same as {@link Backstack#forceClear()}.
     *
     * You generally don't need to use this method.
     *
     * @deprecated The name `reset()` doesn't signal enough that it should not be used. If you need it, use {@link Backstack#forceClear()} instead, but you probably don't need it.
     */
    @Deprecated
    public void reset() {
        forceClear();
    }

    /**
     * Sets the provided state list as the new active history.
     *
     * @param newHistory the new active history.
     * @param direction  The direction of the {@link KeyChange}: {@link KeyChange#BACKWARD}, {@link KeyChange#FORWARD} or {@link KeyChange#REPLACE}.
     */
    @MainThread
    public void setHistory(@NonNull List<?> newHistory, @KeyChange.KeyChangeDirection int direction) {
        checkNewHistory(newHistory);

        assertCorrectThread();

        enqueueKeyChange(newHistory, direction, false); // must use enqueue!
    }

    /**
     * Returns the root (first) element of this history, or null if the history is empty.
     *
     * @throws IllegalStateException if the backstack history doesn't contain any elements yet.
     *
     * @param <K> the type of the key
     * @return the root (first) key
     */
    @NonNull
    public <K> K root() {
        if(stack.isEmpty()) {
            throw new IllegalStateException("Cannot obtain elements from an uninitialized backstack.");
        }
        // noinspection unchecked
        return (K) stack.get(0);
    }

    /**
     * Returns the last element in the list, or null if the history is empty.
     *
     * @throws IllegalStateException if the backstack history doesn't contain any elements yet.
     *
     * @param <K> the type of the key
     * @return the top key
     */
    @NonNull
    public <K> K top() {
        if(stack.isEmpty()) {
            throw new IllegalStateException("Cannot obtain elements from an uninitialized backstack.");
        }
        // noinspection unchecked
        return (K) stack.get(stack.size() - 1);
    }

    /**
     * Returns the element indexed from the top.
     *
     * Offset value `0` behaves the same as {@link Backstack#top()}, while `1` returns the one before it.
     * Negative indices are wrapped around, for example `-1` is the first element of the stack, `-2` the second, and so on.
     *
     * Accepted values are in range of [-size, size).
     *
     * @throws IllegalStateException if the backstack history doesn't contain any elements yet.
     * @throws IllegalArgumentException if the provided offset is outside the range of [-size, size).
     *
     * @param offset the offset from the top
     * @param <K> the type of the key
     * @return the key from the top with offset
     */
    @NonNull
    public <K> K fromTop(int offset) {
        int size = stack.size();
        if(size <= 0) {
            throw new IllegalStateException("Cannot obtain elements from an uninitialized backstack.");
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
        return (K) stack.get(target);
    }

    /**
     * Returns an unmodifiable copy of the current history.
     *
     * @return the unmodifiable copy of history.
     */
    @NonNull
    public <K> History<K> getHistory() {
        List<K> copy = new ArrayList<>(stack.size());
        for(Object key : stack) {
            // noinspection unchecked
            copy.add((K) key);
        }
        return History.from(copy);
    }

    /**
     * Returns an unmodifiable list that contains the keys this backstack is initialized with.
     *
     * @return the list of keys used at first initialization
     */
    @NonNull
    public <K> History<K> getInitialKeys() {
        List<K> copy = new ArrayList<>(initialKeys.size());
        for(Object key : initialKeys) {
            // noinspection unchecked
            copy.add((K) key);
        }
        return History.from(copy);
    }

    /**
     * Returns whether there is at least one queued {@link KeyChange}.
     *
     * @return true if there is at least one enqueued {@link KeyChange}.
     */
    public boolean isKeyChangePending() {
        assertCorrectThread();

        return !queuedKeyChanges.isEmpty();
    }

    private void enqueueKeyChange(List<?> newHistory, int direction, boolean initialization) {
        PendingKeyChange pendingKeyChange = new PendingKeyChange(newHistory, direction, initialization);
        queuedKeyChanges.add(pendingKeyChange);
        beginKeyChangeIfPossible();
    }

    private List<?> selectActiveHistory() {
        if(stack.isEmpty() && queuedKeyChanges.size() <= 0) {
            return initialParameters;
        } else if(queuedKeyChanges.size() <= 0) {
            return stack;
        } else {
            return queuedKeyChanges.getLast().newHistory;
        }
    }

    private boolean beginKeyChangeIfPossible() {
        if(hasKeyChanger() && isKeyChangePending()) {
            PendingKeyChange pendingKeyChange = queuedKeyChanges.getFirst();
            if(pendingKeyChange.getStatus() == PendingKeyChange.Status.ENQUEUED) {
                pendingKeyChange.setStatus(PendingKeyChange.Status.IN_PROGRESS);
                changeState(pendingKeyChange);
                return true;
            }
        }
        return false;
    }

    private void changeState(final PendingKeyChange pendingKeyChange) {
        boolean initialization = pendingKeyChange.initialization;
        List<?> newHistory = pendingKeyChange.newHistory;
        @KeyChange.KeyChangeDirection int direction = pendingKeyChange.direction;

        List<Object> previousState;
        if(initialization) {
            previousState = Collections.emptyList();
        } else {
            previousState = new ArrayList<>(stack);
        }
        final KeyChange keyChange = new KeyChange(this,
                Collections.unmodifiableList(previousState),
                Collections.unmodifiableList(newHistory),
                direction);
        KeyChanger.Callback completionCallback = new KeyChanger.Callback() {
            @Override
            public void keyChangeComplete() {
                assertCorrectThread();

                if(!pendingKeyChange.didForceExecute) {
                    if(pendingKeyChange.getStatus() == PendingKeyChange.Status.COMPLETED) {
                        throw new IllegalStateException("key change completion cannot be called multiple times!");
                    }
                    completeKeyChange(keyChange);
                }
            }
        };
        pendingKeyChange.completionCallback = completionCallback;
        keyChanger.handleKeyChange(keyChange, completionCallback);
    }

    private void completeKeyChange(KeyChange keyChange) {
        if(initialParameters == stack) {
            stack = originalStack;
        }
        stack.clear();
        stack.addAll(keyChange.newKeys);

        PendingKeyChange pendingKeyChange = queuedKeyChanges.removeFirst();
        pendingKeyChange.setStatus(PendingKeyChange.Status.COMPLETED);
        notifyCompletionListeners(keyChange);
        beginKeyChangeIfPossible();
    }

    // completion listeners

    /**
     * CompletionListener allows you to listen to when a KeyChange has been completed.
     * They are registered to the backstack with {@link Backstack#addCompletionListener(CompletionListener)}.
     * They are unregistered from the backstack with {@link Backstack#removeCompletionListener(CompletionListener)} methods.
     */
    public interface CompletionListener {
        /**
         * Callback method that is called when a {@link KeyChange} is complete.
         *
         * @param keyChange the key change that has been completed.
         */
        void keyChangeCompleted(@NonNull KeyChange keyChange);
    }

    private LinkedList<CompletionListener> completionListeners = new LinkedList<>();

    /**
     * Registers the {@link Backstack.CompletionListener}.
     *
     * @param completionListener The non-null completion listener to be registered.
     */
    public void addCompletionListener(@NonNull CompletionListener completionListener) {
        if(completionListener == null) {
            throw new IllegalArgumentException("Null completion listener cannot be added!");
        }

        assertCorrectThread();

        completionListeners.add(completionListener);
    }

    /**
     * Unregisters the {@link Backstack.CompletionListener}.
     *
     * @param completionListener The non-null completion listener to be unregistered.
     */
    public void removeCompletionListener(@NonNull CompletionListener completionListener) {
        if(completionListener == null) {
            throw new IllegalArgumentException("Null completion listener cannot be removed!");
        }

        assertCorrectThread();

        completionListeners.remove(completionListener);
    }

    /**
     * Unregisters all {@link Backstack.CompletionListener}s.
     */
    public void removeCompletionListeners() {
        completionListeners.clear();
    }

    private void notifyCompletionListeners(KeyChange keyChange) {
        for(CompletionListener completionListener : completionListeners) {
            completionListener.keyChangeCompleted(keyChange);
        }
    }

    // force execute

    /**
     * If there is a key change in progress, then calling this method will force it to be completed immediately.
     * Any future calls to {@link KeyChanger.Callback#keyChangeComplete()} for that given key change are ignored.
     */
    @MainThread
    public void executePendingKeyChange() {
        assertCorrectThread();

        if(isKeyChangePending()) {
            PendingKeyChange pendingKeyChange = queuedKeyChanges.getFirst();
            if(pendingKeyChange.getStatus() == PendingKeyChange.Status.IN_PROGRESS) {
                pendingKeyChange.completionCallback.keyChangeComplete();
                pendingKeyChange.didForceExecute = true;
            }
        }
    }

    // argument checks
    private void checkNewHistory(List<?> newHistory) {
        if(newHistory == null || newHistory.isEmpty()) {
            throw new IllegalArgumentException("New history cannot be null or empty");
        }
    }

    private void checkNewKey(Object newKey) {
        if(newKey == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
    }

    private void assertNoKeyChange() {
        if(isKeyChangePending()) {
            throw new IllegalStateException(
                    "This operation is not allowed while there are enqueued key changes.");
        }
    }

    private void assertCorrectThread() {
        if(Thread.currentThread().getId() != threadId) {
            throw new IllegalStateException(
                    "The backstack is not thread-safe, and must be manipulated only from the thread where it was originally created.");
        }
    }
}
