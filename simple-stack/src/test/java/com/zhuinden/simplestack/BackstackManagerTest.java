package com.zhuinden.simplestack;

import android.os.Parcelable;

import com.zhuinden.statebundle.StateBundle;

import org.junit.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Owner on 2017. 03. 25..
 */

public class BackstackManagerTest {
    StateChanger stateChanger = new StateChanger() {
        @Override
        public void handleStateChange(StateChange stateChange, Callback completionCallback) {
            completionCallback.stateChangeComplete();
        }
    };

    @Test
    public void afterClearAndRestorationTheInitialKeysShouldBeRestoredAndNotOverwrittenByRestoredState() {
        TestKey initial = new TestKey("initial");
        TestKey restored = new TestKey("restored");

        ArrayList<Parcelable> history = new ArrayList<>();
        history.add(restored);
        StateBundle stateBundle = new StateBundle();
        stateBundle.putParcelableArrayList(BackstackManager.getHistoryTag(), history);

        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setup(HistoryBuilder.single(initial));
        backstackManager.fromBundle(stateBundle);
        backstackManager.setStateChanger(stateChanger);

        Backstack backstack = backstackManager.getBackstack();
        backstack.goBack();
        assertThat(backstack.getHistory()).isEmpty();
        backstack.setStateChanger(stateChanger, Backstack.INITIALIZE);
        assertThat(backstack.getHistory()).doesNotContain(restored);
        assertThat(backstack.getHistory()).containsExactly(initial);
    }
}
