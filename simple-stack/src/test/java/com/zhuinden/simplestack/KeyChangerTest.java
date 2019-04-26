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
import android.support.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class KeyChangerTest {
    private static class TestKeyChanger
            implements KeyChanger {
        private KeyChange keyChange;
        private List<Object> originalState;
        private List<Object> newState;

        @Override
        public void handleKeyChange(@NonNull KeyChange keyChange, @NonNull Callback completionCallback) {
            this.keyChange = keyChange;
            originalState = keyChange.getPreviousKeys();
            newState = keyChange.getNewKeys();
            completionCallback.keyChangeComplete();
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

    TestKeyChanger testKeyChanger;

    @Before
    public void before() {
        backstack = new Backstack(new A(), new B(), new C(), new D());
        testKeyChanger = new TestKeyChanger();
        backstack.setKeyChanger(testKeyChanger);
    }

    @Test
    public void keyChangeExposesBackstack() {
        assertThat(testKeyChanger.keyChange.backstack()).isSameAs(backstack);
    }

    @Test
    public void keyChangeCreatesContextThatExposesKey() {
        Context context = Mockito.mock(Context.class);
        Context newContext = testKeyChanger.keyChange.createContext(context, testKeyChanger.keyChange.topNewKey());
        assertThat(Backstack.getKey(newContext)).isSameAs(testKeyChanger.keyChange.topNewKey());
    }

    @Test
    public void initialStateIsABCD()
            throws Exception {
        assertThat(testKeyChanger.originalState).isEmpty();
        assertThat(testKeyChanger.newState).containsExactly(new A(), new B(), new C(), new D());
        assertThat(backstack.getHistory()).containsExactlyElementsOf(testKeyChanger.newState);
    }

    @Test
    public void goToBgoestoAB() {
        backstack.goTo(new B());
        assertThat(testKeyChanger.originalState).containsExactly(new A(), new B(), new C(), new D());
        assertThat(testKeyChanger.newState).containsExactly(new A(), new B());
        assertThat(backstack.getHistory()).containsExactlyElementsOf(testKeyChanger.newState);
    }

    @Test
    public void goToEgoestoABCDE() {
        backstack.goTo(new E());
        assertThat(testKeyChanger.originalState).containsExactly(new A(), new B(), new C(), new D());
        assertThat(testKeyChanger.newState).containsExactly(new A(), new B(), new C(), new D(), new E());
        assertThat(backstack.getHistory()).containsExactlyElementsOf(testKeyChanger.newState);
    }

    @Test
    public void goBackGoesToC() {
        boolean didGoBack = backstack.goBack();
        assertThat(didGoBack).isTrue();
        assertThat(testKeyChanger.originalState).containsExactly(new A(), new B(), new C(), new D());
        assertThat(testKeyChanger.newState).containsExactly(new A(), new B(), new C());
        assertThat(backstack.getHistory()).containsExactlyElementsOf(testKeyChanger.newState);
    }

    @Test
    public void goBackOneElementReturnsFalse() {
        backstack = new Backstack(new A());
        backstack.setKeyChanger(testKeyChanger);
        boolean didGoBack = backstack.goBack();
        assertThat(didGoBack).isFalse();
    }

    @Test
    public void setHistoryGoesToSetHistory() {
        ArrayList<Object> newHistory = new ArrayList<>();
        newHistory.add(new C());
        newHistory.add(new B());
        newHistory.add(new D());
        backstack.setHistory(newHistory, KeyChange.FORWARD);

        assertThat(testKeyChanger.originalState).containsExactly(new A(), new B(), new C(), new D());
        assertThat(testKeyChanger.newState).containsExactly(new C(), new B(), new D());
        assertThat(backstack.getHistory()).containsExactlyElementsOf(testKeyChanger.newState);
    }
}