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
 * A {@link StateChanger} can be either set to initialize, or to reattach.
 * Initialize begins an initializing {@link StateChange} to set up initial state, reattach does not.
 */
public class Backstack {
    public static <T extends Object> T getKey(Context context) {
        return ManagedContextWrapper.getKey(context);
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

    private final List<Object> initialParameters;
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
        initialParameters = Collections.unmodifiableList(new ArrayList<>(Arrays.asList(initialKeys)));
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
        initialParameters = Collections.unmodifiableList(new ArrayList<>(initialKeys));
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

        ArrayList<Object> newHistory = new ArrayList<>();
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
            stack.clear();
            return false;
        }
        ArrayList<Object> newHistory = new ArrayList<>();

        List<Object> activeHistory = selectActiveHistory();
        for(int i = 0; i < activeHistory.size() - 1; i++) {
            newHistory.add(activeHistory.get(i));
        }
        enqueueStateChange(newHistory, StateChange.BACKWARD, false);
        return true;
    }

    /**
     * Sets the provided state list as the new active history.
     *
     * @param newHistory the new active history.
     * @param direction  The direction of the state change: BACKWARD, FORWARD or REPLACE.
     */
    public void setHistory(@NonNull List<Object> newHistory, @StateChange.StateChangeDirection int direction) {
        checkNewHistory(newHistory);
        enqueueStateChange(newHistory, direction, false);
    }

    /**
     * Returns an unmodifiable copy of the current history.
     *
     * @return the unmodifiable copy of history.
     */
    public List<Object> getHistory() {
        List<Object> copy = new ArrayList<>();
        copy.addAll(stack);
        return Collections.unmodifiableList(copy);
    }

    /**
     * Returns whether there is at least one queued {@link StateChange}.
     *
     * @return true if there is at least one enqueued {@link StateChange}.
     */
    public boolean isStateChangePending() {
        return !queuedStateChanges.isEmpty();
    }

    private void enqueueStateChange(List<Object> newHistory, int direction, boolean initialization) {
        PendingStateChange pendingStateChange = new PendingStateChange(newHistory, direction, initialization);
        queuedStateChanges.add(pendingStateChange);
        beginStateChangeIfPossible();
    }

    private List<Object> selectActiveHistory() {
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
        List<Object> newHistory = pendingStateChange.newHistory;
        @StateChange.StateChangeDirection int direction = pendingStateChange.direction;

        List<Object> previousState;
        if(initialization) {
            previousState = Collections.emptyList();
        } else {
            previousState = new ArrayList<>();
            previousState.addAll(stack);
        }
        final StateChange stateChange = new StateChange(Collections.unmodifiableList(previousState),
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
    private void checkNewHistory(List<Object> newHistory) {
        if(newHistory == null || newHistory.isEmpty()) {
            throw new IllegalArgumentException("New history cannot be null or empty");
        }
    }

    private void checkNewKey(Object newKey) {
        if(newKey == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
    }
}
