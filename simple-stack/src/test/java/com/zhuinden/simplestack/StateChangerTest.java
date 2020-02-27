/*
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
package com.zhuinden.simplestack;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nonnull;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class StateChangerTest {
    private static class TestStateChanger
            implements StateChanger {
        private StateChange stateChange;
        private List<Object> originalState;
        private List<Object> newState;

        @Override
        public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
            this.stateChange = stateChange;
            originalState = stateChange.getPreviousKeys();
            newState = stateChange.getNewKeys();
            completionCallback.stateChangeComplete();
        }
    }

    private static class A
            implements Parcelable {
        public A() {
        }

        protected A(Parcel in) {
        }

        public static final Creator<A> CREATOR = new Creator<A>() {
            @Override
            public A createFromParcel(Parcel in) {
                return new A(in);
            }

            @Override
            public A[] newArray(int size) {
                return new A[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
        }

        @Override
        public int hashCode() {
            return A.class.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == null) {
                return false;
            }
            return obj instanceof A;
        }
    }

    private static class B
            implements Parcelable {
        public B() {
        }

        protected B(Parcel in) {
        }

        public static final Creator<B> CREATOR = new Creator<B>() {
            @Override
            public B createFromParcel(Parcel in) {
                return new B(in);
            }

            @Override
            public B[] newArray(int size) {
                return new B[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
        }

        @Override
        public int hashCode() {
            return B.class.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == null) {
                return false;
            }
            return obj instanceof B;
        }
    }

    private static class C
            implements Parcelable {
        public C() {
        }

        protected C(Parcel in) {
        }

        public static final Creator<C> CREATOR = new Creator<C>() {
            @Override
            public C createFromParcel(Parcel in) {
                return new C(in);
            }

            @Override
            public C[] newArray(int size) {
                return new C[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
        }

        @Override
        public int hashCode() {
            return C.class.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == null) {
                return false;
            }
            return obj instanceof C;
        }
    }

    private static class D
            implements Parcelable {
        public D() {
        }

        protected D(Parcel in) {
        }

        public static final Creator<D> CREATOR = new Creator<D>() {
            @Override
            public D createFromParcel(Parcel in) {
                return new D(in);
            }

            @Override
            public D[] newArray(int size) {
                return new D[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
        }

        @Override
        public int hashCode() {
            return D.class.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == null) {
                return false;
            }
            return obj instanceof D;
        }
    }

    private static class E
            implements Parcelable {
        public E() {
        }

        protected E(Parcel in) {
        }

        public static final Creator<E> CREATOR = new Creator<E>() {
            @Override
            public E createFromParcel(Parcel in) {
                return new E(in);
            }

            @Override
            public E[] newArray(int size) {
                return new E[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
        }

        @Override
        public int hashCode() {
            return E.class.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == null) {
                return false;
            }
            return obj instanceof E;
        }
    }

    Backstack backstack;

    TestStateChanger testStateChanger;

    @Before
    public void before() {
        backstack = new Backstack();
        backstack.setup(History.of(new A(), new B(), new C(), new D()));
        testStateChanger = new TestStateChanger();
        backstack.setStateChanger(testStateChanger);
    }

    @Test
    public void stateChangeExposesBackstack() {
        assertThat(testStateChanger.stateChange.getBackstack()).isSameAs(backstack);
    }

    @Test
    public void stateChangeCreatesContextThatExposesKey() {
        Context context = Mockito.mock(Context.class);
        Context newContext = testStateChanger.stateChange.createContext(context, testStateChanger.stateChange.topNewKey());
        assertThat(Backstack.getKey(newContext)).isSameAs(testStateChanger.stateChange.topNewKey());
    }

    @Test
    public void initialStateIsABCD()
            throws Exception {
        assertThat(testStateChanger.originalState).isEmpty();
        assertThat(testStateChanger.newState).containsExactly(new A(), new B(), new C(), new D());
        assertThat(backstack.getHistory()).containsExactlyElementsOf(testStateChanger.newState);
    }

    @Test
    public void goToBgoestoAB() {
        backstack.goTo(new B());
        assertThat(testStateChanger.originalState).containsExactly(new A(), new B(), new C(), new D());
        assertThat(testStateChanger.newState).containsExactly(new A(), new B());
        assertThat(backstack.getHistory()).containsExactlyElementsOf(testStateChanger.newState);
    }

    @Test
    public void goToEgoestoABCDE() {
        backstack.goTo(new E());
        assertThat(testStateChanger.originalState).containsExactly(new A(), new B(), new C(), new D());
        assertThat(testStateChanger.newState).containsExactly(new A(), new B(), new C(), new D(), new E());
        assertThat(backstack.getHistory()).containsExactlyElementsOf(testStateChanger.newState);
    }

    @Test
    public void goBackGoesToC() {
        boolean didGoBack = backstack.goBack();
        assertThat(didGoBack).isTrue();
        assertThat(testStateChanger.originalState).containsExactly(new A(), new B(), new C(), new D());
        assertThat(testStateChanger.newState).containsExactly(new A(), new B(), new C());
        assertThat(backstack.getHistory()).containsExactlyElementsOf(testStateChanger.newState);
    }

    @Test
    public void goBackOneElementReturnsFalse() {
        backstack = new Backstack();
        backstack.setup(History.of(new A()));
        backstack.setStateChanger(testStateChanger);
        boolean didGoBack = backstack.goBack();
        assertThat(didGoBack).isFalse();
    }

    @Test
    public void setHistoryGoesToSetHistory() {
        ArrayList<Object> newHistory = new ArrayList<>();
        newHistory.add(new C());
        newHistory.add(new B());
        newHistory.add(new D());
        backstack.setHistory(newHistory, StateChange.FORWARD);

        assertThat(testStateChanger.originalState).containsExactly(new A(), new B(), new C(), new D());
        assertThat(testStateChanger.newState).containsExactly(new C(), new B(), new D());
        assertThat(backstack.getHistory()).containsExactlyElementsOf(testStateChanger.newState);
    }

    @Test
    public void goBackIsTerminalAndIgnoresEnqueueCallsThatIsNotSetHistory() {
        final AtomicReference<StateChanger.Callback> callbackRef = new AtomicReference<>();

        final StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
                callbackRef.set(completionCallback);
            }
        };

        backstack = new Backstack();
        backstack.setup(History.of(new A(), new B()));
        backstack.setStateChanger(stateChanger);

        callbackRef.get().stateChangeComplete();

        backstack.goBack();
        backstack.goTo(new D());
        callbackRef.get().stateChangeComplete();

        try {
            callbackRef.get().stateChangeComplete();
            Assert.fail();
        } catch(IllegalStateException e) {
            // OK!
        }

        assertThat(backstack.getHistory()).containsExactly(new A());
    }

    @Test
    public void jumpToRootIsTerminalAndIgnoresEnqueueCallsThatIsNotSetHistory() {
        final AtomicReference<StateChanger.Callback> callbackRef = new AtomicReference<>();

        final StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
                callbackRef.set(completionCallback);
            }
        };

        backstack = new Backstack();
        backstack.setup(History.of(new A(), new B()));
        backstack.setStateChanger(stateChanger);

        callbackRef.get().stateChangeComplete();

        backstack.jumpToRoot();
        backstack.goTo(new D());
        callbackRef.get().stateChangeComplete();
        try {
            callbackRef.get().stateChangeComplete();
            Assert.fail();
        } catch(IllegalStateException e) {
            // OK!
        }

        assertThat(backstack.getHistory()).containsExactly(new A());
    }

    @Test
    public void goBackIsTerminalButEnqueuesSetHistory() {
        final AtomicReference<StateChanger.Callback> callbackRef = new AtomicReference<>();

        final StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
                callbackRef.set(completionCallback);
            }
        };

        backstack = new Backstack();
        backstack.setup(History.of(new A(), new B()));
        backstack.setStateChanger(stateChanger);

        callbackRef.get().stateChangeComplete();

        backstack.goBack();
        backstack.setHistory(History.of(new D()), StateChange.REPLACE);
        callbackRef.get().stateChangeComplete();
        callbackRef.get().stateChangeComplete();

        assertThat(backstack.getHistory()).containsExactly(new D());
    }

    @Test
    public void jumpToRootIsTerminalButEnqueuesSetHistory() {
        final AtomicReference<StateChanger.Callback> callbackRef = new AtomicReference<>();

        final StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
                callbackRef.set(completionCallback);
            }
        };

        backstack = new Backstack();
        backstack.setup(History.of(new A(), new B()));
        backstack.setStateChanger(stateChanger);

        callbackRef.get().stateChangeComplete();

        backstack.jumpToRoot();
        backstack.setHistory(History.of(new D()), StateChange.REPLACE);
        callbackRef.get().stateChangeComplete();
        callbackRef.get().stateChangeComplete();

        assertThat(backstack.getHistory()).containsExactly(new D());
    }

    @Test
    public void simpleStateChangerWorks() {
        Backstack backstack = new Backstack();
        A a = new A();
        B b = new B();
        C c = new C();

        final List<Object> history = new ArrayList<>();

        final SimpleStateChanger.NavigationHandler navigationHandler = new SimpleStateChanger.NavigationHandler() {
            @Override
            public void onNavigationEvent(@Nonnull StateChange stateChange) {
                history.add(stateChange.topNewKey());
            }
        };

        backstack.setup(History.of(a));
        backstack.setStateChanger(new SimpleStateChanger(navigationHandler));

        assertThat(history).containsExactly(a);

        backstack.setHistory(History.of(b), StateChange.REPLACE);

        assertThat(history).containsExactly(a, b);

        backstack.goTo(c);

        assertThat(history).containsExactly(a, b, c);

        backstack.goTo(c);

        assertThat(history).containsExactly(a, b, c);

        backstack.goBack();

        assertThat(history).containsExactly(a, b, c, b);

        backstack.setHistory(History.of(a), StateChange.REPLACE);

        assertThat(history).containsExactly(a, b, c, b, a);

        try {
            new SimpleStateChanger(null);
            Assert.fail();
        } catch(NullPointerException e) {
            // OK
        }
    }
}