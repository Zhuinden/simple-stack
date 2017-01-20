package com.zhuinden.simplestackdemo.stack;

import android.os.Parcelable;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Zhuinden on 2017.01.20..
 */

public class BackstackTest {
    @Test
    public void initialKeysShouldNotBeEmpty() {
        try {
            Backstack backstack = new Backstack();
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // good!
        }
    }

    @Test
    public void initialKeysShouldNotBeEmptyList() {
        try {
            Backstack backstack = new Backstack(new ArrayList<Parcelable>());
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // good!
        }
    }

    @Test
    public void initialKeysShouldNotBeNullList() {
        try {
            List<Parcelable> list = null;
            Backstack backstack = new Backstack(list);
            Assert.fail();
        } catch(NullPointerException e) {
            // good!
        }
    }

    @Test
    public void stateChangerShouldNotBeNull() {
        try {
            Backstack backstack = new Backstack(new TestKey("Hi"));
            backstack.setStateChanger(null, Backstack.INITIALIZE);
            Assert.fail();
        } catch(NullPointerException e) {
            // good!
        }
    }

    @Test
    public void newHistoryShouldNotBeNull() {
        try {
            Backstack backstack = new Backstack(new TestKey("Hi"));
            backstack.setHistory(null, StateChange.Direction.FORWARD);
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // good!
        }
    }

    @Test
    public void newHistoryKeyShouldNotBeNull() {
        try {
            Backstack backstack = new Backstack(new TestKey("Hi"));
            backstack.goTo(null);
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // good!
        }
    }

    StateChanger.Callback callback = null;

    @Test
    public void goBackShouldReturnTrueDuringActiveStateChange() {
        TestKey hi = new TestKey("hi");
        Backstack backstack = new Backstack(hi, new TestKey("bye"));

        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(StateChange stateChange, Callback completionCallback) {
                callback = completionCallback;
            }
        }, Backstack.INITIALIZE);

        callback.stateChangeComplete();

        backstack.goTo(hi);

        assertThat(backstack.goBack()).isTrue();
    }

    @Test
    public void goBackShouldReturnFalseWithOneElement() {
        TestKey hi = new TestKey("hi");
        Backstack backstack = new Backstack(hi);

        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(StateChange stateChange, Callback completionCallback) {
                callback = completionCallback;
            }
        }, Backstack.INITIALIZE);

        callback.stateChangeComplete();
        assertThat(backstack.goBack()).isFalse();
    }


    @Test
    public void topPreviousStateReturnsNullDuringInitializeStateChange() {
        TestKey hi = new TestKey("hi");
        Backstack backstack = new Backstack(hi);

        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(StateChange stateChange, Callback completionCallback) {
                assertThat(stateChange.topPreviousState()).isNull();
                callback = completionCallback;
            }
        }, Backstack.INITIALIZE);

        callback.stateChangeComplete();
    }

    @Test
    public void topPreviousStateReturnsTop() {
        final TestKey hi = new TestKey("hi");
        final TestKey bye = new TestKey("bye");
        Backstack backstack = new Backstack(hi, bye);

        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(StateChange stateChange, Callback completionCallback) {
                if(!stateChange.getPreviousState().isEmpty()) {
                    assertThat(stateChange.topPreviousState()).isEqualTo(bye);
                }
                callback = completionCallback;
            }
        }, Backstack.INITIALIZE);

        callback.stateChangeComplete();

        backstack.goBack();
        callback.stateChangeComplete();
    }

    @Test
    public void pendingStateChangeCannotGoBackwards() {
        PendingStateChange pendingStateChange = new PendingStateChange(null, null, false);
        pendingStateChange.setStatus(PendingStateChange.Status.COMPLETED);
        try {
            pendingStateChange.setStatus(PendingStateChange.Status.IN_PROGRESS);
            Assert.fail();
        } catch(IllegalStateException e) {
            // Good!
        }
    }

    @Test
    public void pendingStateChangeStatusShouldNotBeNull() {
        PendingStateChange pendingStateChange = new PendingStateChange(null, null, false);
        try {
            pendingStateChange.setStatus(null);
            Assert.fail();
        } catch(NullPointerException e) {
            // Good!
        }
    }
}
