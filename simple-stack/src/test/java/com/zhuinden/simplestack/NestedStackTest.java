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
}
