package com.zhuinden.simplestack;

/*
 * Copyright 2013 Square Inc.
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

public class FlowTest {
    static class Uno
            implements Parcelable {
        public Uno() {
        }

        protected Uno(Parcel in) {
        }

        public static final Creator<Uno> CREATOR = new Creator<Uno>() {
            @Override
            public Uno createFromParcel(Parcel in) {
                return new Uno(in);
            }

            @Override
            public Uno[] newArray(int size) {
                return new Uno[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
        }
    }

    static class Dos
            implements Parcelable {
        public Dos() {
        }

        protected Dos(Parcel in) {
        }

        public static final Creator<Dos> CREATOR = new Creator<Dos>() {
            @Override
            public Dos createFromParcel(Parcel in) {
                return new Dos(in);
            }

            @Override
            public Dos[] newArray(int size) {
                return new Dos[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
        }
    }

    static class Tres
            implements Parcelable {
        public Tres() {
        }

        protected Tres(Parcel in) {
        }

        public static final Creator<Tres> CREATOR = new Creator<Tres>() {
            @Override
            public Tres createFromParcel(Parcel in) {
                return new Tres(in);
            }

            @Override
            public Tres[] newArray(int size) {
                return new Tres[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
        }
    }

    final TestKey able = new TestKey("Able");
    final TestKey baker = new TestKey("Baker");
    final TestKey charlie = new TestKey("Charlie");
    final TestKey delta = new TestKey("Delta");

    List<Object> lastStack;
    int lastDirection;

    class FlowDispatcher
            implements StateChanger {
        @Override
        public void handleStateChange(@NonNull StateChange stateChange, @NonNull StateChanger.Callback callback) {
            lastStack = stateChange.getNewState();
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
        List<Object> history = HistoryBuilder.single(new Uno());
        Backstack flow = new Backstack(history);
        flow.setStateChanger(new FlowDispatcher());

        flow.goTo(new Dos());
        assertThat(lastStack.get(lastStack.size() - 1)).isInstanceOf(Dos.class);
        assertThat(lastDirection).isSameAs(StateChange.FORWARD);

        flow.goTo(new Tres());
        assertThat(lastStack.get(lastStack.size() - 1)).isInstanceOf(Tres.class);
        assertThat(lastDirection).isSameAs(StateChange.FORWARD);

        assertThat(flow.goBack()).isTrue();
        assertThat(lastStack.get(lastStack.size() - 1)).isInstanceOf(Dos.class);
        assertThat(lastDirection).isSameAs(StateChange.BACKWARD);

        assertThat(flow.goBack()).isTrue();
        assertThat(lastStack.get(lastStack.size() - 1)).isInstanceOf(Uno.class);
        assertThat(lastDirection).isSameAs(StateChange.BACKWARD);

        assertThat(flow.goBack()).isFalse();
    }

    @Test
    public void historyChangesAfterListenerCall() {
        final List<Object> firstHistory = HistoryBuilder.single(new Uno());

        class Ourrobouros
                implements StateChanger {
            Backstack flow = new Backstack(firstHistory);

            {
                flow.setStateChanger(this);
            }

            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull StateChanger.Callback onComplete) {
                assertThat(firstHistory).hasSameSizeAs(flow.getHistory());
                Iterator<Object> original = firstHistory.iterator();
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
        List<Object> history = HistoryBuilder.from(Arrays.<Parcelable>asList(able, baker, charlie)).build();
        assertThat(history.size()).isEqualTo(3);

        Backstack flow = new Backstack(history);
        flow.setStateChanger(new FlowDispatcher());

        assertThat(flow.goBack()).isTrue();
        assertThat(lastStack.get(lastStack.size() - 1)).isEqualTo(baker);

        assertThat(flow.goBack()).isTrue();
        assertThat(lastStack.get(lastStack.size() - 1)).isEqualTo(able);

        assertThat(flow.goBack()).isFalse();
    }

    @Test
    public void setHistoryWorks() {
        List<Object> history = HistoryBuilder.from(Arrays.<Parcelable>asList(able, baker)).build();
        Backstack flow = new Backstack(history);
        FlowDispatcher handleStateChanger = new FlowDispatcher();
        flow.setStateChanger(handleStateChanger);

        List<Object> newHistory = HistoryBuilder.from(Arrays.<Parcelable>asList(charlie, delta)).build();
        flow.setHistory(newHistory, StateChange.FORWARD);
        assertThat(lastDirection).isSameAs(StateChange.FORWARD);
        assertThat(lastStack.get(lastStack.size() - 1)).isSameAs(delta);
        assertThat(flow.goBack()).isTrue();
        assertThat(lastStack.get(lastStack.size() - 1)).isSameAs(charlie);
        assertThat(flow.goBack()).isFalse();
    }

    @Test
    public void setObjectGoesBack() {
        List<Object> history = HistoryBuilder.from(Arrays.<Parcelable>asList(able, baker, charlie, delta)).build();
        Backstack flow = new Backstack(history);
        flow.setStateChanger(new FlowDispatcher());

        assertThat(history.size()).isEqualTo(4);

        flow.goTo(charlie);
        assertThat(lastStack.get(lastStack.size() - 1)).isEqualTo(charlie);
        assertThat(lastStack.size()).isEqualTo(3);
        assertThat(lastDirection).isEqualTo(StateChange.BACKWARD);

        assertThat(flow.goBack()).isTrue();
        assertThat(lastStack.get(lastStack.size() - 1)).isEqualTo(baker);
        assertThat(lastDirection).isEqualTo(StateChange.BACKWARD);

        assertThat(flow.goBack()).isTrue();
        assertThat(lastStack.get(lastStack.size() - 1)).isEqualTo(able);
        assertThat(lastDirection).isEqualTo(StateChange.BACKWARD);

        assertThat(flow.goBack()).isFalse();
    }

    @Test
    public void setObjectToMissingObjectPushes() {
        List<Object> history = HistoryBuilder.from(Arrays.<Parcelable>asList(able, baker)).build();
        Backstack flow = new Backstack(history);
        flow.setStateChanger(new FlowDispatcher());
        assertThat(history.size()).isEqualTo(2);

        flow.goTo(charlie);
        assertThat(lastStack.get(lastStack.size() - 1)).isEqualTo(charlie);
        assertThat(lastStack.size()).isEqualTo(3);
        assertThat(lastDirection).isEqualTo(StateChange.FORWARD);

        assertThat(flow.goBack()).isTrue();
        assertThat(lastStack.get(lastStack.size() - 1)).isEqualTo(baker);
        assertThat(lastDirection).isEqualTo(StateChange.BACKWARD);

        assertThat(flow.goBack()).isTrue();
        assertThat(lastStack.get(lastStack.size() - 1)).isEqualTo(able);
        assertThat(lastDirection).isEqualTo(StateChange.BACKWARD);
        assertThat(flow.goBack()).isFalse();
    }

    @Test
    public void setObjectKeepsOriginal() {
        List<Object> history = HistoryBuilder.from(Arrays.<Parcelable>asList(able, baker)).build();
        Backstack flow = new Backstack(history);
        flow.setStateChanger(new FlowDispatcher());
        assertThat(history.size()).isEqualTo(2);

        flow.goTo(new TestKey("Able"));
        assertThat(lastStack.get(lastStack.size() - 1)).isEqualTo(new TestKey("Able"));
        assertThat(lastStack.get(lastStack.size() - 1) == able).isTrue();
        assertThat(lastStack.get(lastStack.size() - 1)).isSameAs(able);
        assertThat(lastStack.size()).isEqualTo(1);
        assertThat(lastDirection).isEqualTo(StateChange.BACKWARD);
    }

    @Test
    public void replaceHistoryResultsInLengthOneHistory() {
        List<Object> history = HistoryBuilder.from(Arrays.<Parcelable>asList(able, baker, charlie)).build();
        Backstack flow = new Backstack(history);
        flow.setStateChanger(new FlowDispatcher());
        assertThat(history.size()).isEqualTo(3);

        flow.setHistory(HistoryBuilder.single(delta), StateChange.REPLACE);
        assertThat(lastStack.get(lastStack.size() - 1)).isEqualTo(new TestKey("Delta"));
        assertThat(lastStack.get(lastStack.size() - 1) == delta).isTrue();
        assertThat(lastStack.get(lastStack.size() - 1)).isSameAs(delta);
        assertThat(lastStack.size()).isEqualTo(1);
        assertThat(lastDirection).isEqualTo(StateChange.REPLACE);
    }

    @Test
    public void replaceTopDoesNotAlterHistoryLength() {
        List<Object> history = HistoryBuilder.from(Arrays.<Parcelable>asList(able, baker, charlie)).build();
        Backstack flow = new Backstack(history);
        flow.setStateChanger(new FlowDispatcher());
        assertThat(history.size()).isEqualTo(3);

        flow.setHistory(HistoryBuilder.from(flow).removeLast().add(delta).build(), StateChange.REPLACE);
        assertThat(lastStack.get(lastStack.size() - 1)).isEqualTo(new TestKey("Delta"));
        assertThat(lastStack.get(lastStack.size() - 1) == delta).isTrue();
        assertThat(lastStack.get(lastStack.size() - 1)).isSameAs(delta);
        assertThat(lastStack.size()).isEqualTo(3);
        assertThat(lastDirection).isEqualTo(StateChange.REPLACE);
    }

    @SuppressWarnings({"deprecation", "CheckResult"})
    @Test
    public void setHistoryKeepsOriginals() {
        TestKey able = new TestKey("Able");
        TestKey baker = new TestKey("Baker");
        TestKey charlie = new TestKey("Charlie");
        TestKey delta = new TestKey("Delta");
        List<Object> history = HistoryBuilder.from(Arrays.<Parcelable>asList(able, baker, charlie, delta)).build();
        Backstack flow = new Backstack(history);
        flow.setStateChanger(new FlowDispatcher());
        assertThat(history.size()).isEqualTo(4);

        TestKey echo = new TestKey("Echo");
        TestKey foxtrot = new TestKey("Foxtrot");
        List<Object> newHistory = HistoryBuilder.from(Arrays.<Parcelable>asList(able, baker, echo, foxtrot)).build();
        flow.setHistory(newHistory, StateChange.REPLACE);
        assertThat(lastStack.size()).isEqualTo(4);
        assertThat(lastStack.get(lastStack.size() - 1)).isEqualTo(foxtrot);
        flow.goBack();
        assertThat(lastStack.size()).isEqualTo(3);
        assertThat(lastStack.get(lastStack.size() - 1)).isEqualTo(echo);
        flow.goBack();
        assertThat(lastStack.size()).isEqualTo(2);
        assertThat(lastStack.get(lastStack.size() - 1)).isSameAs(baker);
        flow.goBack();
        assertThat(lastStack.size()).isEqualTo(1);
        assertThat(lastStack.get(lastStack.size() - 1)).isSameAs(able);
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
        List<Object> history = HistoryBuilder.newBuilder()
                .addAll(Arrays.<Parcelable>asList(new Picky("Able"), new Picky("Baker"), new Picky("Charlie"), new Picky("Delta")))
                .build();
        Backstack flow = new Backstack(history);
        flow.setStateChanger(new FlowDispatcher());

        assertThat(history.size()).isEqualTo(4);

        flow.goTo(new Picky("Charlie"));
        assertThat(lastStack.get(lastStack.size() - 1)).isEqualTo(new Picky("Charlie"));
        assertThat(lastStack.size()).isEqualTo(3);
        assertThat(lastDirection).isEqualTo(StateChange.BACKWARD);

        assertThat(flow.goBack()).isTrue();
        assertThat(lastStack.get(lastStack.size() - 1)).isEqualTo(new Picky("Baker"));
        assertThat(lastDirection).isEqualTo(StateChange.BACKWARD);

        assertThat(flow.goBack()).isTrue();
        assertThat(lastStack.get(lastStack.size() - 1)).isEqualTo(new Picky("Able"));
        assertThat(lastDirection).isEqualTo(StateChange.BACKWARD);

        assertThat(flow.goBack()).isFalse();
    }

}