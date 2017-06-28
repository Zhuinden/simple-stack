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
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.view.View;

import com.zhuinden.statebundle.StateBundle;

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
    @Mock(extraInterfaces = Bundleable.class)
    View view;

    @Mock
    Context context;

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
        public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
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
        stateBundle.putParcelableArrayList(BackstackManager.getHistoryTag(), restoredKeys);
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


    @Test
    public void testRestoreViewFromState() {
        BackstackDelegate backstackDelegate = new BackstackDelegate(null);
        TestKey key = new TestKey("hello");
        backstackDelegate.onCreate(null, null, HistoryBuilder.single(key));
        backstackDelegate.setStateChanger(stateChanger);

        Mockito.when(view.getContext()).thenReturn(context);
        StateBundle stateBundle = new StateBundle();
        Mockito.when(((Bundleable) view).toBundle()).thenReturn(stateBundle);
        // noinspection ResourceType
        Mockito.when(context.getSystemService(KeyContextWrapper.TAG)).thenReturn(key);
        backstackDelegate.persistViewToState(view);

        backstackDelegate.restoreViewFromState(view);
        ((Bundleable) Mockito.verify(view, Mockito.times(1))).fromBundle(stateBundle);
    }

    @Test
    public void onBackPressedGoesBack() {
        BackstackDelegate backstackDelegate = new BackstackDelegate(null);
        TestKey a = new TestKey("hello");
        TestKey b = new TestKey("hello");
        backstackDelegate.onCreate(null, null, HistoryBuilder.from(a, b).build());
        backstackDelegate.setStateChanger(stateChanger);
        assertThat(backstackDelegate.getBackstack().getHistory()).containsExactly(a, b);
        backstackDelegate.onBackPressed();
        assertThat(backstackDelegate.getBackstack().getHistory()).containsExactly(a);
    }

    @Test
    public void onPostResumeThrowsExceptionIfStateChangerNotSet() {
        BackstackDelegate backstackDelegate = new BackstackDelegate(null);
        TestKey key = new TestKey("hello");
        backstackDelegate.onCreate(null, null, HistoryBuilder.single(key));
        // no state changer set
        try {
            backstackDelegate.onPostResume();
            Assert.fail();
        } catch(IllegalStateException e) {
            // OK
        }
    }

    @Test
    public void onPauseRemovesStateChanger() {
        BackstackDelegate backstackDelegate = new BackstackDelegate(null);
        TestKey key = new TestKey("hello");
        backstackDelegate.onCreate(null, null, HistoryBuilder.single(key));
        backstackDelegate.setStateChanger(stateChanger);
        backstackDelegate.onPause();
        assertThat(backstackDelegate.getBackstack().hasStateChanger()).isFalse();
    }

    @Test
    public void onPostResumeReattachesStateChanger() {
        BackstackDelegate backstackDelegate = new BackstackDelegate(null);
        TestKey key = new TestKey("hello");
        backstackDelegate.onCreate(null, null, HistoryBuilder.single(key));
        backstackDelegate.setStateChanger(stateChanger);
        backstackDelegate.onPause();
        assertThat(backstackDelegate.getBackstack().hasStateChanger()).isFalse();
        backstackDelegate.onPostResume();
        assertThat(backstackDelegate.getBackstack().hasStateChanger()).isTrue();
    }

    @Test
    public void getBackstackShouldThrowIfOnCreateNotCalled() {
        BackstackDelegate backstackDelegate = new BackstackDelegate(null);
        try {
            backstackDelegate.getBackstack();
            Assert.fail();
        } catch(IllegalStateException e) {
            // OK
        }
    }

}
