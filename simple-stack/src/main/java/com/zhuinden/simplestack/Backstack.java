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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.annotation.Retention;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * The {@link Backstack} holds the current state, in the form of a list of Objects.
 * It queues up {@link StateChange}s while a {@link StateChanger} is not available.
 * When a {@link StateChanger} is available, it attempts to execute the queued {@link StateChange}s.
 * A {@link StateChanger} can be either set to {@link Backstack#INITIALIZE}, or to {@link Backstack#REATTACH}.
 * {@link Backstack#INITIALIZE} begins an initializing {@link StateChange} to set up initial state, {@link Backstack#REATTACH} does not.
 */
public class Backstack {
    public static <K> K getKey(@NonNull Context context) {
        return KeyContextWrapper.getKey(context);
    }

    //
    @Retention(SOURCE)
    @IntDef({INITIALIZE, REATTACH})
    private @interface StateChangerRegisterMode {
    }

    public static final int INITIALIZE = 0;
    public static final int REATTACH = 1;
    //

    private final List<Object> originalStack = new ArrayList<>();

    private final List<Object> initialKeys;
    private List<Object> initialParameters;
    private List<Object> stack = originalStack;

    private LinkedList<PendingStateChange> queuedStateChanges = new LinkedList<>();

    private StateChanger stateChanger;

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
     * Indicates whether a {@link StateChanger} is set.
     *
     * @return true if a {@link StateChanger} is set, false otherwise.
     */
    public boolean hasStateChanger() {
        return stateChanger != null;
    }

    /**
     * Sets a {@link StateChanger} with {@link Backstack#INITIALIZE} register mode.
     *
     * @param stateChanger the new {@link StateChanger}, which cannot be null.
     */
    public void setStateChanger(@NonNull StateChanger stateChanger) {
        setStateChanger(stateChanger, INITIALIZE);
    }

    /**
     * Sets a {@link StateChanger}.
     *
     * @param stateChanger the new {@link StateChanger}, which cannot be null.
     * @param registerMode indicates whether the {@link StateChanger} is to be initialized, or is just reattached.
     */
    public void setStateChanger(@NonNull StateChanger stateChanger, @StateChangerRegisterMode int registerMode) {
        if(stateChanger == null) {
            throw new NullPointerException("New state changer cannot be null");
        }
        this.stateChanger = stateChanger;
        if(registerMode == INITIALIZE && (queuedStateChanges.size() <= 1 || stack.isEmpty())) {
            if(!beginStateChangeIfPossible()) {
                ArrayList<Object> newHistory = new ArrayList<>();
                newHistory.addAll(selectActiveHistory());
                stack = initialParameters;
                enqueueStateChange(newHistory, StateChange.REPLACE, true);
            }
            return;
        }
        beginStateChangeIfPossible();
    }

    /**
     * Removes the {@link StateChanger}.
     */
    public void removeStateChanger() {
        this.stateChanger = null;
    }

    /**
     * Goes to the new key.
     * If the key is found, then it goes backward to the existing key.
     * If the key is not found, then it goes forward to the newly added key.
     *
     * @param newKey the target state.
     */
    public void goTo(@NonNull Object newKey) {
        checkNewKey(newKey);

        List<Object> newHistory = new ArrayList<>();
        boolean isNewKey = true;
        for(Object key : selectActiveHistory()) {
            newHistory.add(key);
            if(key.equals(newKey)) {
                isNewKey = false;
                break;
            }
        }
        int direction;
        if(isNewKey) {
            newHistory.add(newKey);
            direction = StateChange.FORWARD;
        } else {
            direction = StateChange.BACKWARD;
        }
        enqueueStateChange(newHistory, direction, false);
    }

    /**
     * Replaces the current top with the provided key.
     * This means removing the current last element, and then adding the new element.
     *
     * @param newTop the new top key
     * @param direction The direction of the {@link StateChange}: {@link StateChange#BACKWARD}, {@link StateChange#FORWARD} or {@link StateChange#REPLACE}.
     */
    public void replaceTop(@NonNull Object newTop, @StateChange.StateChangeDirection int direction) {
        checkNewKey(newTop);

        HistoryBuilder historyBuilder = History.builderFrom(selectActiveHistory());
        if(!historyBuilder.isEmpty()) {
            historyBuilder.removeLast();
        }
        List<Object> newHistory = historyBuilder.add(newTop) //
                .build();
        setHistory(newHistory, direction);
    }

    /**
     * Goes "up" to the provided element.
     * This means that if the provided element is found anywhere in the history, then the history goes to it.
     * If not found, then the current top is replaced with the provided element.
     *
     * Going up occurs in {@link StateChange#BACKWARD} direction.
     *
     * @param newKey the new key to go up to
     */
    public void goUp(@NonNull Object newKey) {
        checkNewKey(newKey);

        List<?> currentHistory = selectActiveHistory();
        int size = currentHistory.size();

        if(size <= 1) { // single-element history cannot contain the previous element. Short circuit to replaceTop.
            replaceTop(newKey, StateChange.BACKWARD);
            return;
        }
        if(currentHistory.contains(newKey)) {
            goTo(newKey);
        } else {
            replaceTop(newKey, StateChange.BACKWARD);
        }
    }

    /**
     * Goes "up" once to the provided chain of parents.
     * If the chain of parents is found as previous elements, then it works as back navigation to that chain.
     * If the whole chain is not found, but at least one element of it is found, then the history is kept up to that point, then the chain is added, any duplicate element in the chain is added to the end as part of the chain.
     * If no element of the chain is found in the history, then the current top is removed, and the provided parent chain is added in its place.
     *
     * Going up the chain occurs in {@link StateChange#BACKWARD} direction.
     *
     * @param parentChain the chain of parents, from oldest to newest.
     */
    public void goUpChain(@NonNull List<?> parentChain) {
        checkNewHistory(parentChain);

        int parentChainSize = parentChain.size();
        if(parentChainSize == 1) {
            goUp(parentChain.get(0));
            return;
        }

        HistoryBuilder historyBuilder = History.builderFrom(selectActiveHistory());
        historyBuilder.removeLast(); // we will never keep the current key on "up" navigation.

        int indexOfSubList = Collections.indexOfSubList(historyBuilder.build(), parentChain);

        if(indexOfSubList != -1) {
            // if the parent chain is found as is,
            // we clear all on top of it and go back to the chain
            goTo(parentChain.get(parentChainSize-1));
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
                    HistoryBuilder newHistory = History.newBuilder();
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
                    setHistory(newHistory.build(), StateChange.BACKWARD);
                    return;
                }
            }

            // no elements in the current history were found in the parent chain
            // default behavior is to add the newly received list in place of the original key
            HistoryBuilder newHistory = historyBuilder.addAll(parentChain);
            setHistory(newHistory.build(), StateChange.BACKWARD);
        }
    }

    /**
     * Goes back in the history.
     * If the key is found, then it goes backward to the existing key.
     * If the key is not found, then it goes forward to the newly added key.
     *
     * @return true if a state change is pending or is handled with a state change, false if there is only one state left.
     */
    public boolean goBack() {
        if(isStateChangePending()) {
            return true;
        }
        if(stack.size() <= 1) {
            return false;
        }
        List<Object> newHistory = new ArrayList<>();

        List<?> activeHistory = selectActiveHistory();
        for(int i = 0; i < activeHistory.size() - 1; i++) {
            newHistory.add(activeHistory.get(i));
        }
        enqueueStateChange(newHistory, StateChange.BACKWARD, false);
        return true;
    }

    private void resetBackstack() {
        stack.clear();
        initialParameters = new ArrayList<>(initialKeys);
    }

    /**
     * Immediately clears the backstack, it is NOT enqueued as a state change.
     *
     * If there are pending state changes, then it throws an exception.
     *
     * You generally don't need to use this method.
     */
    public void reset() {
        assertNoStateChange();
        resetBackstack();
    }

    /**
     * Sets the provided state list as the new active history.
     *
     * @param newHistory the new active history.
     * @param direction  The direction of the {@link StateChange}: {@link StateChange#BACKWARD}, {@link StateChange#FORWARD} or {@link StateChange#REPLACE}.
     */
    public void setHistory(@NonNull List<?> newHistory, @StateChange.StateChangeDirection int direction) {
        checkNewHistory(newHistory);
        enqueueStateChange(newHistory, direction, false);
    }

    /**
     * Returns the root (first) element of this history, or null if the history is empty.
     *
     * @param <K> the type of the key
     * @return the root (first) key
     */
    @Nullable
    public <K> K root() {
        if(stack.isEmpty()) {
            return null;
        }
        // noinspection unchecked
        return (K) stack.get(0);
    }

    /**
     * Returns the last element in the list, or null if the history is empty.
     *
     * @param <K> the type of the key
     * @return the top key
     */
    @Nullable
    public <K> K top() {
        if(stack.isEmpty()) {
            return null;
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
     * Returns whether there is at least one queued {@link StateChange}.
     *
     * @return true if there is at least one enqueued {@link StateChange}.
     */
    public boolean isStateChangePending() {
        return !queuedStateChanges.isEmpty();
    }

    private void enqueueStateChange(List<?> newHistory, int direction, boolean initialization) {
        PendingStateChange pendingStateChange = new PendingStateChange(newHistory, direction, initialization);
        queuedStateChanges.add(pendingStateChange);
        beginStateChangeIfPossible();
    }

    private List<?> selectActiveHistory() {
        if(stack.isEmpty() && queuedStateChanges.size() <= 0) {
            return initialParameters;
        } else if(queuedStateChanges.size() <= 0) {
            return stack;
        } else {
            return queuedStateChanges.getLast().newHistory;
        }
    }

    private boolean beginStateChangeIfPossible() {
        if(hasStateChanger() && isStateChangePending()) {
            PendingStateChange pendingStateChange = queuedStateChanges.getFirst();
            if(pendingStateChange.getStatus() == PendingStateChange.Status.ENQUEUED) {
                pendingStateChange.setStatus(PendingStateChange.Status.IN_PROGRESS);
                changeState(pendingStateChange);
                return true;
            }
        }
        return false;
    }

    private void changeState(final PendingStateChange pendingStateChange) {
        boolean initialization = pendingStateChange.initialization;
        List<?> newHistory = pendingStateChange.newHistory;
        @StateChange.StateChangeDirection int direction = pendingStateChange.direction;

        List<Object> previousState;
        if(initialization) {
            previousState = Collections.emptyList();
        } else {
            previousState = new ArrayList<>();
            previousState.addAll(stack);
        }
        final StateChange stateChange = new StateChange(this,
                Collections.unmodifiableList(previousState),
                Collections.unmodifiableList(newHistory),
                direction);
        StateChanger.Callback completionCallback = new StateChanger.Callback() {
            @Override
            public void stateChangeComplete() {
                if(!pendingStateChange.didForceExecute) {
                    if(pendingStateChange.getStatus() == PendingStateChange.Status.COMPLETED) {
                        throw new IllegalStateException("State change completion cannot be called multiple times!");
                    }
                    completeStateChange(stateChange);
                }
            }
        };
        pendingStateChange.completionCallback = completionCallback;
        stateChanger.handleStateChange(stateChange, completionCallback);
    }

    private void completeStateChange(StateChange stateChange) {
        if(initialParameters == stack) {
            stack = originalStack;
        }
        stack.clear();
        stack.addAll(stateChange.newState);

        PendingStateChange pendingStateChange = queuedStateChanges.removeFirst();
        pendingStateChange.setStatus(PendingStateChange.Status.COMPLETED);
        notifyCompletionListeners(stateChange);
        beginStateChangeIfPossible();
    }

    // completion listeners

    /**
     * CompletionListener allows you to listen to when a StateChange has been completed.
     * They are registered to the backstack with {@link Backstack#addCompletionListener(CompletionListener)}.
     * They are unregistered from the backstack with {@link Backstack#removeCompletionListener(CompletionListener)} methods.
     */
    public interface CompletionListener {
        /**
         * Callback method that is called when a {@link StateChange} is complete.
         *
         * @param stateChange the state change that has been completed.
         */
        void stateChangeCompleted(@NonNull StateChange stateChange);
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
        completionListeners.remove(completionListener);
    }

    /**
     * Unregisters all {@link Backstack.CompletionListener}s.
     */
    public void removeCompletionListeners() {
        completionListeners.clear();
    }

    private void notifyCompletionListeners(StateChange stateChange) {
        for(CompletionListener completionListener : completionListeners) {
            completionListener.stateChangeCompleted(stateChange);
        }
    }

    // force execute

    /**
     * If there is a state change in progress, then calling this method will force it to be completed immediately.
     * Any future calls to {@link StateChanger.Callback#stateChangeComplete()} for that given state change are ignored.
     */
    public void executePendingStateChange() {
        if(isStateChangePending()) {
            PendingStateChange pendingStateChange = queuedStateChanges.getFirst();
            if(pendingStateChange.getStatus() == PendingStateChange.Status.IN_PROGRESS) {
                pendingStateChange.completionCallback.stateChangeComplete();
                pendingStateChange.didForceExecute = true;
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

    private void assertNoStateChange() {
        if(isStateChangePending()) {
            throw new IllegalStateException(
                    "This operation is not allowed while there are enqueued state changes.");
        }
    }
}
