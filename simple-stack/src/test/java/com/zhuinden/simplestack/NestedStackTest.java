package com.zhuinden.simplestack;

import org.junit.Assert;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Owner on 2017. 02. 23..
 */

public class NestedStackTest {
    KeyParceler keyParceler = BackstackDelegate.DEFAULT_KEYPARCELER;

    StateChanger.Callback completionCallback;

    StateChanger stateChanger = new StateChanger() {
        @Override
        public void handleStateChange(StateChange stateChange, Callback callback) {
            completionCallback = callback;
        }
    };

    @Test
    public void nestedStackRootWorks() {
        TestKey rootKey = new TestKey("root");
        TestKey rootKey2 = new TestKey("root2");
        TestKey rootKey3 = new TestKey("root3");
        BackstackDelegate backstackDelegate = BackstackDelegate.create();
        backstackDelegate.onCreate(null, null, HistoryBuilder.single(rootKey));
        backstackDelegate.setStateChanger(stateChanger);
        completionCallback.stateChangeComplete();

        NestedStack nestedStack = backstackDelegate.getChildStack(rootKey);
        assertThat(nestedStack.getHistory()).isEmpty(); // it is a NestedStack that belongs to TestKey
        assertThat(nestedStack.getParent()).isNotNull(); // its parent is the root
        assertThat(nestedStack.getParent().getParent()).isNull();
        assertThat(nestedStack.getParent().getHistory()).containsExactly(rootKey);

        nestedStack.getParent().setHistory(HistoryBuilder.from(rootKey, rootKey2, rootKey3).build(), StateChange.FORWARD);
        completionCallback.stateChangeComplete();
        assertThat(nestedStack.getParent().getHistory()).containsExactly(rootKey, rootKey2, rootKey3);

        nestedStack.getParent().goTo(rootKey2);
        completionCallback.stateChangeComplete();
        assertThat(nestedStack.getParent().goBack()).isTrue();
        completionCallback.stateChangeComplete();
        assertThat(nestedStack.getParent().getHistory()).containsExactly(rootKey);
        assertThat(nestedStack.getParent().goBack()).isFalse();
    }

    @Test
    public void nestedStackChildWorks() {
        TestKey rootKey = new TestKey("root");
        TestKey rootKey2 = new TestKey("root2");
        TestKey rootKey3 = new TestKey("root3");

        BackstackDelegate backstackDelegate = BackstackDelegate.create();
        backstackDelegate.onCreate(null, null, HistoryBuilder.single(rootKey));
        backstackDelegate.setStateChanger(stateChanger);
        completionCallback.stateChangeComplete();

        NestedStack nestedStack = backstackDelegate.getChildStack(rootKey);
        nestedStack.initialize(rootKey2);
        nestedStack.setStateChanger(stateChanger);
        completionCallback.stateChangeComplete();

        NestedStack nestedStack2 = nestedStack.getChildStack(rootKey2);
        nestedStack2.initialize(rootKey3);
        nestedStack2.setStateChanger(stateChanger);
        completionCallback.stateChangeComplete();

        assertThat(nestedStack2.getHistory()).containsExactly(rootKey3);
        assertThat(nestedStack.getHistory()).containsExactly(rootKey2);
        assertThat(backstackDelegate.getBackstack().getHistory()).containsExactly(rootKey);
    }

    @Test
    public void nestedStackDelegatesBackToParent() {
        TestKey rootKey = new TestKey("root");
        TestKey rootKeyA1 = new TestKey("rootA1");
        TestKey rootKeyA2 = new TestKey("rootA2");
        TestKey rootKeyB1 = new TestKey("rootB1");

        BackstackDelegate backstackDelegate = BackstackDelegate.create();
        backstackDelegate.onCreate(null, null, HistoryBuilder.single(rootKey));
        backstackDelegate.setStateChanger(stateChanger);
        completionCallback.stateChangeComplete();

        NestedStack nestedStack = backstackDelegate.getChildStack(rootKey);
        nestedStack.initialize(rootKeyA1, rootKeyA2);
        nestedStack.setStateChanger(stateChanger);
        completionCallback.stateChangeComplete();

        NestedStack nestedStack2 = nestedStack.getChildStack(rootKeyA2);
        nestedStack2.initialize(rootKeyB1);
        nestedStack2.setStateChanger(stateChanger);
        completionCallback.stateChangeComplete();

        nestedStack2.goBack();
        completionCallback.stateChangeComplete();

        assertThat(nestedStack2.getHistory()).isEmpty();
        assertThat(nestedStack.getHistory()).containsExactly(rootKeyA1);
        assertThat(backstackDelegate.getBackstack().getHistory()).containsExactly(rootKey);
    }

    @Test
    public void nestedStackBackDestroysUnneededServicesAndCreatesPrevious() {
        TestKey rootKey = new TestKey("root");
        TestKey rootKeyA1 = new TestKey("rootA1");
        TestKey rootKeyA2 = new TestKey("rootA2");
        TestKey rootKeyB1 = new TestKey("rootB1");

        BackstackDelegate backstackDelegate = BackstackDelegate.create();
        backstackDelegate.onCreate(null, null, HistoryBuilder.single(rootKey));
        backstackDelegate.setStateChanger(stateChanger);
        completionCallback.stateChangeComplete();

        NestedStack nestedStack = backstackDelegate.getChildStack(rootKey);
        nestedStack.initialize(rootKeyA1, rootKeyA2);
        nestedStack.setStateChanger(stateChanger);
        completionCallback.stateChangeComplete();

        try {
            nestedStack.getChildStack(rootKeyA1);
            Assert.fail();
        } catch(IllegalStateException e) {
            // OK!
        }
        NestedStack nestedStack2 = nestedStack.getChildStack(rootKeyA2);
        nestedStack2.initialize(rootKeyB1);
        nestedStack2.setStateChanger(stateChanger);
        completionCallback.stateChangeComplete();

        nestedStack2.goBack();
        completionCallback.stateChangeComplete();

        try {
            nestedStack.getChildStack(rootKeyA2);
            Assert.fail();
        } catch(IllegalStateException e) {
            // OK!
        }
        assertThat(nestedStack.getChildStack(rootKeyA1)).isNotNull();
    }

    @Test
    public void nestedStackBackDestroysUnneededServiceAndRestoresPrevious() {
        TestKey rootKey = new TestKey("root");
        TestKey rootKeyA1 = new TestKey("rootA1");
        TestKey rootKeyA2 = new TestKey("rootA2");
        TestKey rootKeyB1 = new TestKey("rootB1");
        TestKey rootKeyB2 = new TestKey("rootB2");
        TestKey rootKeyC1 = new TestKey("rootC1");

        BackstackDelegate backstackDelegate = BackstackDelegate.create();
        backstackDelegate.onCreate(null, null, HistoryBuilder.single(rootKey));
        backstackDelegate.setStateChanger(stateChanger);
        completionCallback.stateChangeComplete();

        NestedStack nestedStack = backstackDelegate.getChildStack(rootKey);
        nestedStack.initialize(rootKeyA1);
        nestedStack.setStateChanger(stateChanger);
        completionCallback.stateChangeComplete();

        NestedStack nestedStackA1 = nestedStack.getChildStack(rootKeyA1);
        nestedStackA1.initialize(rootKeyB1, rootKeyB2);
        nestedStackA1.setStateChanger(stateChanger);
        completionCallback.stateChangeComplete();

        nestedStack.goTo(rootKeyA2);
        completionCallback.stateChangeComplete();

        try {
            nestedStackA1.getChildStack(rootKeyA1);
        } catch(IllegalStateException e) {
            // OK!
        }

        NestedStack nestedStack2 = nestedStack.getChildStack(rootKeyA2);
        nestedStack2.initialize(rootKeyC1);
        nestedStack2.setStateChanger(stateChanger);
        completionCallback.stateChangeComplete();

        nestedStack2.goBack();
        completionCallback.stateChangeComplete();

        try {
            nestedStack.getChildStack(rootKeyA2);
            Assert.fail();
        } catch(IllegalStateException e) {
            // OK!
        }
        assertThat(nestedStack.getChildStack(rootKeyA1)).isNotNull();

        nestedStackA1 = nestedStack.getChildStack(rootKeyA1);
        nestedStackA1.initialize(rootKeyB1, rootKeyC1); // initialization should be ignored once initial params are restored
        nestedStackA1.setStateChanger(stateChanger);
        completionCallback.stateChangeComplete();
        assertThat(nestedStackA1.getHistory()).containsExactly(rootKeyB1, rootKeyB2);
    }

    @Test
    public void setStateChangerThrowsIfUninitialized() {
        TestKey rootKey = new TestKey("root");
        TestKey rootKeyA1 = new TestKey("rootA1");

        BackstackDelegate backstackDelegate = BackstackDelegate.create();
        backstackDelegate.onCreate(null, null, HistoryBuilder.single(rootKey));
        backstackDelegate.setStateChanger(stateChanger);
        completionCallback.stateChangeComplete();

        NestedStack nestedStack = backstackDelegate.getChildStack(rootKey);
        nestedStack.goTo(rootKeyA1);
        try {
            nestedStack.setStateChanger(stateChanger); // uninitialized!
            Assert.fail();
        } catch(IllegalStateException e) {
            // OK!
        }
    }

    @Test
    public void initializeAndGoToUsesTypicalReentranceAndEnqueuesGoTo() {
        TestKey rootKey = new TestKey("root");
        TestKey rootKeyA1 = new TestKey("rootA1");
        TestKey rootKeyA2 = new TestKey("rootA2");

        BackstackDelegate backstackDelegate = BackstackDelegate.create();
        backstackDelegate.onCreate(null, null, HistoryBuilder.single(rootKey));
        backstackDelegate.setStateChanger(stateChanger);
        completionCallback.stateChangeComplete();

        NestedStack nestedStack = backstackDelegate.getChildStack(rootKey);
        nestedStack.initialize(rootKeyA1);
        nestedStack.goTo(rootKeyA2);
        nestedStack.setStateChanger(stateChanger);
        completionCallback.stateChangeComplete();
        assertThat(nestedStack.getHistory()).containsExactly(rootKeyA1, rootKeyA2);
    }

    @Test
    public void goToAndInitializeResultsInIgnoredBootstrap() {
        TestKey rootKey = new TestKey("root");
        TestKey rootKeyA1 = new TestKey("rootA1");
        TestKey rootKeyA2 = new TestKey("rootA2");

        BackstackDelegate backstackDelegate = BackstackDelegate.create();
        backstackDelegate.onCreate(null, null, HistoryBuilder.single(rootKey));
        backstackDelegate.setStateChanger(stateChanger);
        completionCallback.stateChangeComplete();

        NestedStack nestedStack = backstackDelegate.getChildStack(rootKey);
        nestedStack.goTo(rootKeyA2);
        nestedStack.initialize(rootKeyA1);
        nestedStack.setStateChanger(stateChanger);
        completionCallback.stateChangeComplete();
        assertThat(nestedStack.getHistory()).containsExactly(rootKeyA2);
    }

    @Test
    public void initializeAndSetHistoryEndsWithSetHistory() {
        TestKey rootKey = new TestKey("root");
        TestKey rootKeyA1 = new TestKey("rootA1");
        TestKey rootKeyA2 = new TestKey("rootA2");
        TestKey rootKeyA3 = new TestKey("rootA3");

        BackstackDelegate backstackDelegate = BackstackDelegate.create();
        backstackDelegate.onCreate(null, null, HistoryBuilder.single(rootKey));
        backstackDelegate.setStateChanger(stateChanger);
        completionCallback.stateChangeComplete();

        NestedStack nestedStack = backstackDelegate.getChildStack(rootKey);
        nestedStack.initialize(rootKeyA1);
        nestedStack.setHistory(HistoryBuilder.from(rootKeyA2, rootKeyA3).build(), StateChange.REPLACE);
        nestedStack.setStateChanger(stateChanger);
        completionCallback.stateChangeComplete();
        assertThat(nestedStack.getHistory()).containsExactly(rootKeyA2, rootKeyA3);
    }

    @Test
    public void initializeWithEmptyListThrowsException() {
        TestKey rootKey = new TestKey("root");

        BackstackDelegate backstackDelegate = BackstackDelegate.create();
        backstackDelegate.onCreate(null, null, HistoryBuilder.single(rootKey));
        backstackDelegate.setStateChanger(stateChanger);
        completionCallback.stateChangeComplete();

        NestedStack nestedStack = backstackDelegate.getChildStack(rootKey);
        try {
            nestedStack.initialize();
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // OK!
        }
    }
}
