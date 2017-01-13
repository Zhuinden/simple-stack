package com.zhuinden.simplestackdemo.stack;

import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Owner on 2017. 01. 12..
 */

public class Backstack {
    private ArrayList<Parcelable> stack = new ArrayList<>();

    private StateChanger stateChanger;

    private boolean isStateChangeInProgress;

    public Backstack(Parcelable... initialKeys) {
        if(initialKeys == null || initialKeys.length <= 0) {
            throw new IllegalArgumentException("At least one initial key must be defined");
        }
        Collections.addAll(stack, initialKeys);
    }

    public Backstack(List<Parcelable> initialKeys) {
        if(initialKeys == null) {
            throw new NullPointerException("Initial key list should not be null");
        }
        if(initialKeys.size() <= 0) {
            throw new IllegalArgumentException("Initial key list should contain at least one element");
        }
        stack.addAll(initialKeys);
    }

    public void setStateChanger(StateChanger stateChanger) {
        if(stateChanger == null) {
            throw new NullPointerException("New state changer cannot be null");
        }
        this.stateChanger = stateChanger;
        ArrayList<Parcelable> newHistory = new ArrayList<>();
        newHistory.addAll(stack);
        changeState(newHistory, StateChange.Direction.REPLACE, true);
    }

    public void removeStateChanger() {
        this.stateChanger = null;
    }

    public void goTo(Parcelable newKey) {
        checkNewKey(newKey);
        checkStateChanger();
        checkStateChangeInProgress();

        ArrayList<Parcelable> newHistory = new ArrayList<>();
        boolean isNewKey = true;
        for(Parcelable key : stack) {
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
        changeState(newHistory, direction, false);
    }

    public boolean goBack() {
        if(isStateChangeInProgress) {
            return true;
        }

        checkStateChanger();

        if(stack.size() <= 0) {
            throw new IllegalStateException("Cannot go back when stack has no items");
        }
        if(stack.size() == 1) {
            stack.clear();
            return false;
        }
        ArrayList<Parcelable> newHistory = new ArrayList<>();
        for(int i = 0; i < stack.size() - 1; i++) {
            newHistory.add(stack.get(i));
        }
        changeState(newHistory, StateChange.Direction.BACKWARD, false);
        return true;
    }

    public void setHistory(List<Parcelable> newHistory, StateChange.Direction direction) {
        checkStateChanger();
        checkStateChangeInProgress();
        if(newHistory == null || newHistory.isEmpty()) {
            throw new NullPointerException("New history cannot be null or empty");
        }
        changeState(newHistory, direction, false);
    }

    public List<Parcelable> getHistory() {
        return Collections.unmodifiableList(stack);
    }

    //
    private void checkStateChangeInProgress() {
        if(isStateChangeInProgress) {
            throw new IllegalStateException("Cannot execute state change while another state change is in progress"); // FIXME: Flow allows queueing traversals
        }
    }

    private void checkStateChanger() {
        if(stateChanger == null) {
            throw new IllegalStateException("State changes are not possible while state changer is not set"); // FIXME: Flow allows queueing traversals
        }
    }

    private void checkNewKey(Parcelable newKey) {
        if(newKey == null) {
            throw new NullPointerException("Key cannot be null");
        }
    }

    private void changeState(List<Parcelable> newHistory, StateChange.Direction direction, boolean initialization) {
        List<Parcelable> previousState;
        if(initialization) {
            previousState = Collections.emptyList();
        } else {
            previousState = new ArrayList<>();
            previousState.addAll(stack);
        }
        final StateChange stateChange = new StateChange(previousState, Collections.unmodifiableList(newHistory), direction);
        isStateChangeInProgress = true;
        stateChanger.handleStateChange(stateChange, new StateChanger.Callback() {
            @Override
            public void stateChangeComplete() {
                completeStateChange(stateChange);
            }
        });
    }

    private void completeStateChange(StateChange stateChange) {
        this.stack.clear();
        this.stack.addAll(stateChange.newState);
        isStateChangeInProgress = false;
    }
}
