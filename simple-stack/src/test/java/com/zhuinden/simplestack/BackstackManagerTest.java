package com.zhuinden.simplestack;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.zhuinden.statebundle.StateBundle;

import junit.framework.Assert;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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

    @Test
    public void afterClearAndRestorationTheFilteredAreNotRestored() {
        final TestKey initial = new TestKey("initial");
        final TestKey restored = new TestKey("restored");
        final TestKey filtered = new TestKey("filtered");

        ArrayList<Parcelable> history = new ArrayList<>();
        history.add(restored);
        history.add(filtered);
        StateBundle stateBundle = new StateBundle();
        stateBundle.putParcelableArrayList(BackstackManager.getHistoryTag(), history);

        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setKeyFilter(new KeyFilter() {
            @NonNull
            @Override
            public List<Object> filterHistory(@NonNull List<Object> restoredKeys) {
                restoredKeys.remove(filtered);
                return restoredKeys;
            }
        });
        backstackManager.setup(HistoryBuilder.single(initial));
        backstackManager.fromBundle(stateBundle);
        backstackManager.setStateChanger(stateChanger);

        Backstack backstack = backstackManager.getBackstack();
        assertThat(backstack.getHistory()).contains(restored);
        assertThat(backstack.getHistory()).doesNotContain(filtered);

        //// would restore properly
        backstackManager = new BackstackManager();
        backstackManager.setup(HistoryBuilder.single(initial));
        backstackManager.fromBundle(stateBundle);
        backstackManager.setStateChanger(stateChanger);

        backstack = backstackManager.getBackstack();
        assertThat(backstack.getHistory()).contains(restored, filtered);

        //// if both are filtered, restore initial
        backstackManager = new BackstackManager();
        backstackManager.setKeyFilter(new KeyFilter() {
            @NonNull
            @Override
            public List<Object> filterHistory(@NonNull List<Object> restoredKeys) {
                restoredKeys.remove(restored);
                restoredKeys.remove(filtered);
                return restoredKeys;
            }
        });

        backstackManager.setup(HistoryBuilder.single(initial));
        backstackManager.fromBundle(stateBundle);
        backstackManager.setStateChanger(stateChanger);

        backstack = backstackManager.getBackstack();
        assertThat(backstack.getHistory()).doesNotContain(restored, filtered);
        assertThat(backstack.getHistory()).contains(initial);
    }

    @Test
    public void keyFilterSetAfterSetupShouldThrow() {
        final TestKey initial = new TestKey("initial");
        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setup(HistoryBuilder.single(initial));
        try {
            backstackManager.setKeyFilter(new KeyFilter() {
                @NonNull
                @Override
                public List<Object> filterHistory(@NonNull List<Object> restoredKeys) {
                    return restoredKeys;
                }

            });
            Assert.fail();
        } catch(IllegalStateException e) {
            // OK!
        }
    }
}
