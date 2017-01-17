package com.zhuinden.simplestackdemo.stack;

import android.os.Parcelable;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Created by Owner on 2017. 01. 12..
 */

public class Backstack {
    //
    @Retention(SOURCE)
    @IntDef({INITIALIZE, REATTACH})
    public @interface StateChangerRegisterMode {
    }

    public static final int INITIALIZE = 0;
    public static final int REATTACH = 1;
    //

    private final List<Parcelable> originalStack = new ArrayList<>();

    private final List<Parcelable> initialParameters;
    private List<Parcelable> stack = originalStack;

    private LinkedList<PendingStateChange> queuedStateChanges = new LinkedList<>();

    private StateChanger stateChanger;

    public Backstack(Parcelable... initialKeys) {
        if(initialKeys == null || initialKeys.length <= 0) {
            throw new IllegalArgumentException("At least one initial key must be defined");
        }
        initialParameters = Collections.unmodifiableList(new ArrayList<>(Arrays.asList(initialKeys)));
    }

    public Backstack(List<Parcelable> initialKeys) {
        if(initialKeys == null) {
            throw new NullPointerException("Initial key list should not be null");
        }
        if(initialKeys.size() <= 0) {
            throw new IllegalArgumentException("Initial key list should contain at least one element");
        }
        initialParameters = Collections.unmodifiableList(new ArrayList<>(initialKeys));
    }

    public boolean hasStateChanger() {
        return stateChanger != null;
    }

    public void setStateChanger(StateChanger stateChanger, @StateChangerRegisterMode int registerMode) {
        if(stateChanger == null) {
            throw new NullPointerException("New state changer cannot be null");
        }
        this.stateChanger = stateChanger;
        if(registerMode == INITIALIZE && (queuedStateChanges.size() <= 1 || stack.isEmpty())) {
            if(!beginStateChangeIfPossible()) {
                ArrayList<Parcelable> newHistory = new ArrayList<>();
                newHistory.addAll(stack.isEmpty() ? initialParameters : stack);
                stack = initialParameters;
                enqueueStateChange(newHistory, StateChange.Direction.REPLACE, true);
            }
            return;
        }
        beginStateChangeIfPossible();
    }

    public void removeStateChanger() {
        this.stateChanger = null;
    }

    public void goTo(Parcelable newKey) {
        checkNewKey(newKey);

        ArrayList<Parcelable> newHistory = new ArrayList<>();
        boolean isNewKey = true;
        for(Parcelable key : selectActiveHistory()) {
            newHistory.add(key);
            if(key.equals(newKey)) {
                isNewKey = false;
                break;
            }
        }
        StateChange.Direction direction;
        if(isNewKey) {
            newHistory.add(newKey);
            direction = StateChange.Direction.FORWARD;
        } else {
            direction = StateChange.Direction.BACKWARD;
        }
        enqueueStateChange(newHistory, direction, false);
    }

    public boolean goBack() {
        if(!queuedStateChanges.isEmpty() && queuedStateChanges.get(0)
                .getStatus()
                .ordinal() >= PendingStateChange.Status.IN_PROGRESS.ordinal()) {
            return true;
        }
        if(stack.size() <= 1) {
            stack.clear();
            return false;
        }
        ArrayList<Parcelable> newHistory = new ArrayList<>();

        List<Parcelable> activeHistory = selectActiveHistory();
        for(int i = 0; i < activeHistory.size() - 1; i++) {
            newHistory.add(activeHistory.get(i));
        }
        enqueueStateChange(newHistory, StateChange.Direction.BACKWARD, false);
        return true;
    }

    public void setHistory(List<Parcelable> newHistory, StateChange.Direction direction) {
        checkNewHistory(newHistory);
        enqueueStateChange(newHistory, direction, false);
    }

    public List<Parcelable> getHistory() {
        List<Parcelable> copy = new ArrayList<>();
        copy.addAll(stack);
        return Collections.unmodifiableList(copy);
    }

    private void enqueueStateChange(List<Parcelable> newHistory, StateChange.Direction direction, boolean initialization) {
        PendingStateChange pendingStateChange = new PendingStateChange(newHistory, direction, initialization);
        queuedStateChanges.add(pendingStateChange);
        beginStateChangeIfPossible();
    }

    private List<Parcelable> selectActiveHistory() {
        if(stack.isEmpty() && queuedStateChanges.size() <= 0) {
            return initialParameters;
        } else if(queuedStateChanges.size() <= 0) {
            return stack;
        } else {
            return queuedStateChanges.getLast().newHistory;
        }
    }

    private boolean beginStateChangeIfPossible() {
        if(hasStateChanger() && !queuedStateChanges.isEmpty()) {
            PendingStateChange pendingStateChange = queuedStateChanges.get(0);
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
        List<Parcelable> newHistory = pendingStateChange.newHistory;
        StateChange.Direction direction = pendingStateChange.direction;

        List<Parcelable> previousState;
        if(initialization) {
            previousState = Collections.emptyList();
        } else {
            previousState = new ArrayList<>();
            previousState.addAll(stack);
        }
        final StateChange stateChange = new StateChange(Collections.unmodifiableList(previousState),
                Collections.unmodifiableList(newHistory),
                direction);
        stateChanger.handleStateChange(stateChange, new StateChanger.Callback() {
            @Override
            public void stateChangeComplete() {
                if(pendingStateChange.getStatus() == PendingStateChange.Status.COMPLETED) {
                    throw new IllegalStateException("State change completion cannot be called multiple times!");
                }
                completeStateChange(stateChange);
            }
        });
    }

    private void completeStateChange(StateChange stateChange) {
        if(initialParameters == stack) {
            stack = originalStack;
        }
        stack.clear();
        stack.addAll(stateChange.newState);

        PendingStateChange pendingStateChange = queuedStateChanges.remove(0);
        if(pendingStateChange.getStatus() != PendingStateChange.Status.IN_PROGRESS) {
            throw new IllegalStateException("An error occurred in state management: " + //
                    "expected [" + PendingStateChange.Status.IN_PROGRESS + "] but was [" + pendingStateChange.getStatus() + "]");
        }
        pendingStateChange.setStatus(PendingStateChange.Status.COMPLETED);
        beginStateChangeIfPossible();
    }

    // argument checks
    private void checkNewHistory(List<Parcelable> newHistory) {
        if(newHistory == null || newHistory.isEmpty()) {
            throw new IllegalArgumentException("New history cannot be null or empty");
        }
    }

    private void checkNewKey(Parcelable newKey) {
        if(newKey == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
    }
}
