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

import android.os.Bundle;
import android.os.Parcelable;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

/**
 * Created by Zhuinden on 2017.02.04..
 */

public class BackstackDelegateTest {
    @Mock
    Backstack backstack;

    @Mock
    BackstackManager backstackManager;

    @Mock
    Bundle savedInstanceState;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    StateChanger stateChanger = new StateChanger() {
        @Override
        public void handleStateChange(StateChange stateChange, Callback completionCallback) {
            completionCallback.stateChangeComplete();
        }
    };

    @Test
    public void setNullPersistenceTagShouldThrow() {
        BackstackDelegate backstackDelegate = new BackstackDelegate(null);
        try {
            backstackDelegate.setPersistenceTag(null);
            fail();
        } catch(IllegalArgumentException e) {
            // OK!
        }
    }

    @Test
    public void setSamePersistenceTagTwiceShouldBeOk() {
        BackstackDelegate backstackDelegate = new BackstackDelegate(null);
        backstackDelegate.setPersistenceTag(new String("hello"));
        backstackDelegate.setPersistenceTag(new String("hello"));
        // no exceptions thrown
    }

    @Test
    public void setTwoDifferentPersistenceTagsShouldThrow() {
        BackstackDelegate backstackDelegate = new BackstackDelegate(null);
        backstackDelegate.setPersistenceTag(new String("hello"));
        try {
            backstackDelegate.setPersistenceTag(new String("world"));
            fail();
        } catch(IllegalStateException e) {
            // OK!
        }
    }

    @Test
    public void setPersistenceTagAfterOnCreateShouldThrow() {
        BackstackDelegate backstackDelegate = new BackstackDelegate(null);
        backstackDelegate.onCreate(null, null, new ArrayList<Object>() {{
            add(new TestKey("hello"));
        }});
        try {
            backstackDelegate.setPersistenceTag(new String("world"));
            fail();
        } catch(IllegalStateException e) {
            // OK!
        }
    }


    @Test
    public void onCreateRestoresBackstackKeys() {
        BackstackDelegate backstackDelegate = new BackstackDelegate(null);
        TestKey testKey = new TestKey("hello");
        final TestKey restoredKey = new TestKey("world");
        ArrayList<Parcelable> restoredKeys = new ArrayList<Parcelable>() {{
            add(restoredKey);
        }};
        StateBundle stateBundle = new StateBundle();
        stateBundle.putParcelableArrayList(BackstackManager.HISTORY_TAG, restoredKeys);
        Mockito.when(savedInstanceState.getParcelable(backstackDelegate.getHistoryTag())).thenReturn(stateBundle);
        backstackDelegate.onCreate(savedInstanceState, null, HistoryBuilder.single(testKey));
        assertThat(backstackDelegate.getBackstack()).isNotNull();
        backstackDelegate.setStateChanger(stateChanger);
        assertThat(backstackDelegate.getBackstack().getHistory()).containsExactly(restoredKey);
    }

    @Test
    public void onCreateChoosesInitialKeysIfRestoredHistoryIsEmpty() {
        BackstackDelegate backstackDelegate = new BackstackDelegate(null);
        TestKey testKey = new TestKey("hello");
        ArrayList<Parcelable> restoredKeys = new ArrayList<>();
        Mockito.when(savedInstanceState.getParcelableArrayList(backstackDelegate.getHistoryTag())).thenReturn(restoredKeys);
        backstackDelegate.onCreate(savedInstanceState, null, HistoryBuilder.single(testKey));
        assertThat(backstackDelegate.getBackstack()).isNotNull();
        backstackDelegate.setStateChanger(stateChanger);
        assertThat(backstackDelegate.getBackstack().getHistory()).containsExactly(testKey);
    }

    @Test
    public void getSavedStateThrowsBeforeOnCreate() {
        BackstackDelegate backstackDelegate = new BackstackDelegate(null);
        try {
            backstackDelegate.getSavedState(null);
            Assert.fail();
        } catch(IllegalStateException e) {
            // OK
        }
    }

    @Test
    public void getSavedStateForNullThrowsException() {
        BackstackDelegate backstackDelegate = new BackstackDelegate(null);
        TestKey testKey = new TestKey("hello");
        backstackDelegate.onCreate(null, null, HistoryBuilder.single(testKey));
        try {
            backstackDelegate.getSavedState(null);
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // OK
        }
    }

    @Test
    public void onCreateInvalidNonConfigurationThrowsException() {
        BackstackDelegate backstackDelegate = new BackstackDelegate(null);
        try {
            backstackDelegate.onCreate(null, new TestKey("crashpls"), HistoryBuilder.single(new TestKey("hello")));
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // OK
        }
    }

    @Test
    public void onCreateRestoresFromNonConfigInstance() {
        Mockito.when(backstackManager.getBackstack()).thenReturn(backstack);
        BackstackDelegate.NonConfigurationInstance nonConfigurationInstance = new BackstackDelegate.NonConfigurationInstance(
                backstackManager);
        BackstackDelegate backstackDelegate = new BackstackDelegate(null);
        TestKey testKey = new TestKey("hello");
        backstackDelegate.onCreate(null, nonConfigurationInstance, HistoryBuilder.single(testKey));
        assertThat(backstackDelegate.getBackstack()).isSameAs(backstack);
    }

}
