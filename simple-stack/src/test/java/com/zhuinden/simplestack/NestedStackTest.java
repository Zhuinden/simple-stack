package com.zhuinden.simplestack;

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

        NestedStack nestedStack = backstackDelegate.findService(rootKey, BackstackManager.LOCAL_STACK);
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

        NestedStack nestedStack = backstackDelegate.findService(rootKey, BackstackManager.LOCAL_STACK);
        nestedStack.initialize(rootKey2);
        nestedStack.setStateChanger(stateChanger);
        completionCallback.stateChangeComplete();

        NestedStack nestedStack2 = nestedStack.findService(rootKey2, BackstackManager.LOCAL_STACK);
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

        NestedStack nestedStack = backstackDelegate.findService(rootKey, BackstackManager.LOCAL_STACK);
        nestedStack.initialize(rootKeyA1, rootKeyA2);
        nestedStack.setStateChanger(stateChanger);
        completionCallback.stateChangeComplete();

        NestedStack nestedStack2 = nestedStack.findService(rootKeyA2, BackstackManager.LOCAL_STACK);
        nestedStack2.initialize(rootKeyB1);
        nestedStack2.setStateChanger(stateChanger);
        completionCallback.stateChangeComplete();

        nestedStack2.goBack();
        completionCallback.stateChangeComplete();

        assertThat(nestedStack2.getHistory()).isEmpty();
        assertThat(nestedStack.getHistory()).containsExactly(rootKeyA1);
        assertThat(backstackDelegate.getBackstack().getHistory()).containsExactly(rootKey);
    }
}
