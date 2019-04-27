package com.zhuinden.simplestack;

/*
 * Copyright 2013 Square Inc.
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

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

public class FlowTest {
    static class Uno {
        public Uno() {
        }
    }

    static class Dos {
        public Dos() {
        }
    }

    static class Tres {
        public Tres() {
        }
    }

    final TestKey able = new TestKey("Able");
    final TestKey baker = new TestKey("Baker");
    final TestKey charlie = new TestKey("Charlie");
    final TestKey delta = new TestKey("Delta");

    History<?> lastStack;
    int lastDirection;

    class FlowDispatcher
            implements StateChanger {
        @Override
        public void handleStateChange(@NonNull StateChange stateChange, @NonNull StateChanger.Callback callback) {
            lastStack = stateChange.getNewKeys();
            lastDirection = stateChange.getDirection();
            callback.stateChangeComplete();
        }
    }

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void oneTwoThree() {
        History<?> history = History.single(new Uno());
        Backstack flow = new Backstack(history);
        flow.setStateChanger(new FlowDispatcher());

        flow.goTo(new Dos());
        assertThat(lastStack.top()).isInstanceOf(Dos.class);
        assertThat(lastDirection).isSameAs(StateChange.FORWARD);

        flow.goTo(new Tres());
        assertThat(lastStack.top()).isInstanceOf(Tres.class);
        assertThat(lastDirection).isSameAs(StateChange.FORWARD);

        assertThat(flow.goBack()).isTrue();
        assertThat(lastStack.top()).isInstanceOf(Dos.class);
        assertThat(lastDirection).isSameAs(StateChange.BACKWARD);

        assertThat(flow.goBack()).isTrue();
        assertThat(lastStack.top()).isInstanceOf(Uno.class);
        assertThat(lastDirection).isSameAs(StateChange.BACKWARD);

        assertThat(flow.goBack()).isFalse();
    }

    @Test
    public void historyChangesAfterListenerCall() {
        final History<?> firstHistory = History.single(new Uno());

        class Ourrobouros
                implements StateChanger {
            Backstack flow = new Backstack(firstHistory);

            {
                flow.setStateChanger(this);
            }

            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull StateChanger.Callback onComplete) {
                assertThat(firstHistory).hasSameSizeAs(flow.getHistory());
                Iterator<?> original = firstHistory.iterator();
                for(Object o : flow.getHistory()) {
                    assertThat(o).isEqualTo(original.next());
                }
                onComplete.stateChangeComplete();
            }
        }

        Ourrobouros listener = new Ourrobouros();
        listener.flow.goTo(new Dos());
    }

    @Test
    public void historyPushAllIsPushy() {
        History<?> history = History.from(Arrays.asList(able, baker, charlie));
        assertThat(history.size()).isEqualTo(3);

        Backstack flow = new Backstack(history);
        flow.setStateChanger(new FlowDispatcher());

        assertThat(flow.goBack()).isTrue();
        assertThat(lastStack.top()).isEqualTo(baker);

        assertThat(flow.goBack()).isTrue();
        assertThat(lastStack.top()).isEqualTo(able);

        assertThat(flow.goBack()).isFalse();
    }

    @Test
    public void setHistoryWorks() {
        History<?> history = History.from(Arrays.asList(able, baker));
        Backstack flow = new Backstack(history);
        FlowDispatcher handleStateChangeer = new FlowDispatcher();
        flow.setStateChanger(handleStateChangeer);

        History<?> newHistory = History.from(Arrays.asList(charlie, delta));
        flow.setHistory(newHistory, StateChange.FORWARD);
        assertThat(lastDirection).isSameAs(StateChange.FORWARD);
        assertThat(lastStack.top()).isSameAs(delta);
        assertThat(flow.goBack()).isTrue();
        assertThat(lastStack.top()).isSameAs(charlie);
        assertThat(flow.goBack()).isFalse();
    }

    @Test
    public void setObjectGoesBack() {
        History<?> history = History.from(Arrays.asList(able, baker, charlie, delta));
        Backstack flow = new Backstack(history);
        flow.setStateChanger(new FlowDispatcher());

        assertThat(history.size()).isEqualTo(4);

        flow.goTo(charlie);
        assertThat(lastStack.top()).isEqualTo(charlie);
        assertThat(lastStack.size()).isEqualTo(3);
        assertThat(lastDirection).isEqualTo(StateChange.BACKWARD);

        assertThat(flow.goBack()).isTrue();
        assertThat(lastStack.top()).isEqualTo(baker);
        assertThat(lastDirection).isEqualTo(StateChange.BACKWARD);

        assertThat(flow.goBack()).isTrue();
        assertThat(lastStack.top()).isEqualTo(able);
        assertThat(lastDirection).isEqualTo(StateChange.BACKWARD);

        assertThat(flow.goBack()).isFalse();
    }

    @Test
    public void setObjectToMissingObjectPushes() {
        History<?> history = History.from(Arrays.asList(able, baker));
        Backstack flow = new Backstack(history);
        flow.setStateChanger(new FlowDispatcher());
        assertThat(history.size()).isEqualTo(2);

        flow.goTo(charlie);
        assertThat(lastStack.top()).isEqualTo(charlie);
        assertThat(lastStack.size()).isEqualTo(3);
        assertThat(lastDirection).isEqualTo(StateChange.FORWARD);

        assertThat(flow.goBack()).isTrue();
        assertThat(lastStack.top()).isEqualTo(baker);
        assertThat(lastDirection).isEqualTo(StateChange.BACKWARD);

        assertThat(flow.goBack()).isTrue();
        assertThat(lastStack.top()).isEqualTo(able);
        assertThat(lastDirection).isEqualTo(StateChange.BACKWARD);
        assertThat(flow.goBack()).isFalse();
    }

    @Test
    public void setObjectKeepsOriginal() {
        History<?> history = History.from(Arrays.asList(able, baker));
        Backstack flow = new Backstack(history);
        flow.setStateChanger(new FlowDispatcher());
        assertThat(history.size()).isEqualTo(2);

        flow.goTo(new TestKey("Able"));
        assertThat(lastStack.top()).isEqualTo(new TestKey("Able"));
        assertThat(lastStack.top() == able).isTrue();
        assertThat(lastStack.top()).isSameAs(able);
        assertThat(lastStack.size()).isEqualTo(1);
        assertThat(lastDirection).isEqualTo(StateChange.BACKWARD);
    }

    @Test
    public void replaceHistoryResultsInLengthOneHistory() {
        History<?> history = History.from(Arrays.asList(able, baker, charlie));
        Backstack flow = new Backstack(history);
        flow.setStateChanger(new FlowDispatcher());
        assertThat(history.size()).isEqualTo(3);

        flow.setHistory(History.single(delta), StateChange.REPLACE);
        assertThat(lastStack.top()).isEqualTo(new TestKey("Delta"));
        assertThat(lastStack.top() == delta).isTrue();
        assertThat(lastStack.top()).isSameAs(delta);
        assertThat(lastStack.size()).isEqualTo(1);
        assertThat(lastDirection).isEqualTo(StateChange.REPLACE);
    }

    @Test
    public void replaceTopDoesNotAlterHistoryLength() {
        History<?> history = History.from(Arrays.asList(able, baker, charlie));
        Backstack flow = new Backstack(history);
        flow.setStateChanger(new FlowDispatcher());
        assertThat(history.size()).isEqualTo(3);

        flow.setHistory(History.builderFrom(flow).removeLast().add(delta).build(), StateChange.REPLACE);
        assertThat(lastStack.top()).isEqualTo(new TestKey("Delta"));
        assertThat(lastStack.top() == delta).isTrue();
        assertThat(lastStack.top()).isSameAs(delta);
        assertThat(lastStack.size()).isEqualTo(3);
        assertThat(lastDirection).isEqualTo(StateChange.REPLACE);
    }

    @SuppressWarnings({"CheckResult"})
    @Test
    public void setHistoryKeepsOriginals() {
        TestKey able = new TestKey("Able");
        TestKey baker = new TestKey("Baker");
        TestKey charlie = new TestKey("Charlie");
        TestKey delta = new TestKey("Delta");
        History<?> history = History.from(Arrays.asList(able, baker, charlie, delta));
        Backstack flow = new Backstack(history);
        flow.setStateChanger(new FlowDispatcher());
        assertThat(history.size()).isEqualTo(4);

        TestKey echo = new TestKey("Echo");
        TestKey foxtrot = new TestKey("Foxtrot");
        History<?> newHistory = History.from(Arrays.asList(able, baker, echo, foxtrot));
        flow.setHistory(newHistory, StateChange.REPLACE);
        assertThat(lastStack.size()).isEqualTo(4);
        assertThat(lastStack.top()).isEqualTo(foxtrot);
        flow.goBack();
        assertThat(lastStack.size()).isEqualTo(3);
        assertThat(lastStack.top()).isEqualTo(echo);
        flow.goBack();
        assertThat(lastStack.size()).isEqualTo(2);
        assertThat(lastStack.top()).isSameAs(baker);
        flow.goBack();
        assertThat(lastStack.size()).isEqualTo(1);
        assertThat(lastStack.top()).isSameAs(able);
    }

    static class Picky
            implements Parcelable {
        final String value;

        Picky(String value) {
            this.value = value;
        }

        protected Picky(Parcel in) {
            value = in.readString();
        }

        public static final Creator<Picky> CREATOR = new Creator<Picky>() {
            @Override
            public Picky createFromParcel(Parcel in) {
                return new Picky(in);
            }

            @Override
            public Picky[] newArray(int size) {
                return new Picky[size];
            }
        };

        @Override
        public boolean equals(Object o) {
            if(this == o) {
                return true;
            }
            if(o == null || getClass() != o.getClass()) {
                return false;
            }

            Picky picky = (Picky) o;
            return value.equals(picky.value);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(value);
        }
    }

    @Test
    public void setCallsEquals() {
        History<?> history = History.newBuilder()
                .addAll(Arrays.asList(new Picky("Able"), new Picky("Baker"), new Picky("Charlie"), new Picky("Delta")))
                .build();
        Backstack flow = new Backstack(history);
        flow.setStateChanger(new FlowDispatcher());

        assertThat(history.size()).isEqualTo(4);

        flow.goTo(new Picky("Charlie"));
        assertThat(lastStack.top()).isEqualTo(new Picky("Charlie"));
        assertThat(lastStack.size()).isEqualTo(3);
        assertThat(lastDirection).isEqualTo(StateChange.BACKWARD);

        assertThat(flow.goBack()).isTrue();
        assertThat(lastStack.top()).isEqualTo(new Picky("Baker"));
        assertThat(lastDirection).isEqualTo(StateChange.BACKWARD);

        assertThat(flow.goBack()).isTrue();
        assertThat(lastStack.top()).isEqualTo(new Picky("Able"));
        assertThat(lastDirection).isEqualTo(StateChange.BACKWARD);

        assertThat(flow.goBack()).isFalse();
    }

}