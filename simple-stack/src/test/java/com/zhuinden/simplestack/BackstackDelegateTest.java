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

import android.app.Activity;
import android.app.Application;
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
import java.util.LinkedList;
import java.util.List;

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
        BackstackDelegate backstackDelegate = new BackstackDelegate();
        try {
            backstackDelegate.setPersistenceTag(null);
            fail();
        } catch(IllegalArgumentException e) {
            // OK!
        }
    }

    @Test
    public void setSamePersistenceTagTwiceShouldBeOk() {
        BackstackDelegate backstackDelegate = new BackstackDelegate();
        backstackDelegate.setPersistenceTag(new String("hello"));
        backstackDelegate.setPersistenceTag(new String("hello"));
        // no exceptions thrown
    }

    @Test
    public void setTwoDifferentPersistenceTagsShouldThrow() {
        BackstackDelegate backstackDelegate = new BackstackDelegate();
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
        BackstackDelegate backstackDelegate = new BackstackDelegate();
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
        BackstackDelegate backstackDelegate = new BackstackDelegate();
        TestKey testKey = new TestKey("hello");
        final TestKey restoredKey = new TestKey("world");
        ArrayList<Parcelable> restoredKeys = new ArrayList<Parcelable>() {{
            add(restoredKey);
        }};
        StateBundle stateBundle = new StateBundle();
        stateBundle.putParcelableArrayList(BackstackManager.getHistoryTag(), restoredKeys);
        Mockito.when(savedInstanceState.getParcelable(backstackDelegate.getHistoryTag())).thenReturn(stateBundle);
        backstackDelegate.onCreate(savedInstanceState, null, History.single(testKey));
        assertThat(backstackDelegate.getBackstack()).isNotNull();
        backstackDelegate.setStateChanger(stateChanger);
        assertThat(backstackDelegate.getBackstack().getHistory()).containsExactly(restoredKey);
    }

    @Test
    public void onCreateChoosesInitialKeysIfRestoredHistoryIsEmpty() {
        BackstackDelegate backstackDelegate = new BackstackDelegate();
        TestKey testKey = new TestKey("hello");
        ArrayList<Parcelable> restoredKeys = new ArrayList<>();
        Mockito.when(savedInstanceState.getParcelableArrayList(backstackDelegate.getHistoryTag())).thenReturn(restoredKeys);
        backstackDelegate.onCreate(savedInstanceState, null, History.single(testKey));
        assertThat(backstackDelegate.getBackstack()).isNotNull();
        backstackDelegate.setStateChanger(stateChanger);
        assertThat(backstackDelegate.getBackstack().getHistory()).containsExactly(testKey);
    }

    @Test
    public void getSavedStateThrowsBeforeOnCreate() {
        BackstackDelegate backstackDelegate = new BackstackDelegate();
        try {
            backstackDelegate.getSavedState(null);
            Assert.fail();
        } catch(IllegalStateException e) {
            // OK
        }
    }

    @Test
    public void getSavedStateForNullThrowsException() {
        BackstackDelegate backstackDelegate = new BackstackDelegate();
        TestKey testKey = new TestKey("hello");
        backstackDelegate.onCreate(null, null, History.single(testKey));
        try {
            backstackDelegate.getSavedState(null);
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // OK
        }
    }

    @Test
    public void onCreateInvalidNonConfigurationThrowsException() {
        BackstackDelegate backstackDelegate = new BackstackDelegate();
        try {
            backstackDelegate.onCreate(null, new TestKey("crashpls"), History.single(new TestKey("hello")));
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
        BackstackDelegate backstackDelegate = new BackstackDelegate();
        TestKey testKey = new TestKey("hello");
        backstackDelegate.onCreate(null, nonConfigurationInstance, History.single(testKey));
        assertThat(backstackDelegate.getBackstack()).isSameAs(backstack);
    }


    @Test
    public void testRestoreViewFromState() {
        BackstackDelegate backstackDelegate = new BackstackDelegate();
        TestKey key = new TestKey("hello");
        backstackDelegate.onCreate(null, null, History.single(key));
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
        BackstackDelegate backstackDelegate = new BackstackDelegate();
        TestKey a = new TestKey("hello");
        TestKey b = new TestKey("hello");
        backstackDelegate.onCreate(null, null, History.of(a, b));
        backstackDelegate.setStateChanger(stateChanger);
        assertThat(backstackDelegate.getBackstack().getHistory()).containsExactly(a, b);
        backstackDelegate.onBackPressed();
        assertThat(backstackDelegate.getBackstack().getHistory()).containsExactly(a);
    }

    @Test
    public void onPostResumeThrowsExceptionIfStateChangerNotSet() {
        BackstackDelegate backstackDelegate = new BackstackDelegate();
        TestKey key = new TestKey("hello");
        backstackDelegate.onCreate(null, null, History.single(key));
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
        BackstackDelegate backstackDelegate = new BackstackDelegate();
        TestKey key = new TestKey("hello");
        backstackDelegate.onCreate(null, null, History.single(key));
        backstackDelegate.setStateChanger(stateChanger);
        backstackDelegate.onPause();
        assertThat(backstackDelegate.getBackstack().hasStateChanger()).isFalse();
    }

    @Test
    public void onPostResumeReattachesStateChanger() {
        BackstackDelegate backstackDelegate = new BackstackDelegate();
        TestKey key = new TestKey("hello");
        backstackDelegate.onCreate(null, null, History.single(key));
        backstackDelegate.setStateChanger(stateChanger);
        backstackDelegate.onPause();
        assertThat(backstackDelegate.getBackstack().hasStateChanger()).isFalse();
        backstackDelegate.onPostResume();
        assertThat(backstackDelegate.getBackstack().hasStateChanger()).isTrue();
    }

    @Test
    public void getBackstackShouldThrowIfOnCreateNotCalled() {
        BackstackDelegate backstackDelegate = new BackstackDelegate();
        try {
            backstackDelegate.getBackstack();
            Assert.fail();
        } catch(IllegalStateException e) {
            // OK
        }
    }

    @Test
    public void addStateChangeListenerAddsCompletionListener() {
        TestKey testKey = new TestKey("hello");
        final List<StateChange> called = new LinkedList<>();
        Backstack.CompletionListener completionListener = new Backstack.CompletionListener() {
            @Override
            public void stateChangeCompleted(@NonNull StateChange stateChange) {
                called.add(stateChange);
            }
        };
        BackstackDelegate backstackDelegate = new BackstackDelegate();
        backstackDelegate.addStateChangeCompletionListener(completionListener);
        backstackDelegate.onCreate(null, null, History.single(testKey));
        backstackDelegate.setStateChanger(stateChanger);

        assertThat(called.get(0).topNewState()).isSameAs(testKey);
    }

    @Test
    public void addStateChangeListenerAfterOnCreateThrows() {
        TestKey testKey = new TestKey("hello");
        Backstack.CompletionListener completionListener = new Backstack.CompletionListener() {
            @Override
            public void stateChangeCompleted(@NonNull StateChange stateChange) {
                // do nothing
            }
        };
        BackstackDelegate backstackDelegate = new BackstackDelegate();
        backstackDelegate.onCreate(null, null, History.single(testKey));
        try {
            backstackDelegate.addStateChangeCompletionListener(completionListener);
            Assert.fail();
        } catch(IllegalStateException e) {
            // OK
        }
    }

    @Test
    public void addNullStateChangeListenerThrows() {
        TestKey testKey = new TestKey("hello");
        BackstackDelegate backstackDelegate = new BackstackDelegate();
        try {
            backstackDelegate.addStateChangeCompletionListener(null);
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // OK
        }
    }

    @Test
    public void getManagerReturnsBackstackManager() {
        TestKey testKey = new TestKey("Hello");
        BackstackDelegate backstackDelegate = new BackstackDelegate();
        backstackDelegate.onCreate(null, null, History.single(testKey));
        assertThat(backstackDelegate.getManager()).isNotNull();
    }

    @Test
    public void getManagerBeforeOnCreateThrows() {
        BackstackDelegate backstackDelegate = new BackstackDelegate();
        try {
            backstackDelegate.getManager();
            Assert.fail();
        } catch(IllegalStateException e) {
            // OK
        }
    }

    @Test
    public void setKeyFilterWithNullShouldThrow() {
        BackstackDelegate backstackDelegate = new BackstackDelegate();
        try {
            backstackDelegate.setKeyFilter(null);
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // OK!
        }
    }

    @Test
    public void setKeyFilterMustBeCalledBeforeOnCreate() {
        BackstackDelegate backstackDelegate = new BackstackDelegate();
        Object key = new TestKey("hello");
        backstackDelegate.onCreate(null, null, History.single(key));
        try {
            backstackDelegate.setKeyFilter(new KeyFilter() {
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

    @Test
    public void setKeyParcelerWithNullShouldThrow() {
        BackstackDelegate backstackDelegate = new BackstackDelegate();
        try {
            backstackDelegate.setKeyParceler(null);
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // OK!
        }
    }

    @Test
    public void setKeyParcelerMustBeCalledBeforeOnCreate() {
        BackstackDelegate backstackDelegate = new BackstackDelegate();
        Object key = new TestKey("hello");
        backstackDelegate.onCreate(null, null, History.single(key));
        try {
            backstackDelegate.setKeyParceler(new KeyParceler() {
                @Override
                public Parcelable toParcelable(Object object) {
                    return (Parcelable)object;
                }

                @Override
                public Object fromParcelable(Parcelable parcelable) {
                    return parcelable;
                }
            });
            Assert.fail();
        } catch(IllegalStateException e) {
            // OK!
        }
    }

    @Test
    public void setStateClearStrategyWithNullShouldThrow() {
        BackstackDelegate backstackDelegate = new BackstackDelegate();
        try {
            backstackDelegate.setStateClearStrategy(null);
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // OK!
        }
    }

    @Test
    public void setStateClearStrategyMustBeCalledBeforeOnCreate() {
        BackstackDelegate backstackDelegate = new BackstackDelegate();
        Object key = new TestKey("hello");
        backstackDelegate.onCreate(null, null, History.single(key));
        try {
            backstackDelegate.setStateClearStrategy(new DefaultStateClearStrategy());
            Assert.fail();
        } catch(IllegalStateException e) {
            // OK!
        }
    }

    @Test
    public void registerLifecycleCallbacksShouldThrowForNull() {
        BackstackDelegate backstackDelegate = new BackstackDelegate();
        try {
            backstackDelegate.registerForLifecycleCallbacks(null);
            Assert.fail();
        } catch(NullPointerException e) {
            // OK!
        }
    }

    @Test
    public void registerLifecycleCallbacksShouldThrowIfNotCalledCreate() {
        Activity activity = Mockito.mock(Activity.class);
        BackstackDelegate backstackDelegate = new BackstackDelegate();
        try {
            backstackDelegate.registerForLifecycleCallbacks(activity);
            Assert.fail();
        } catch(IllegalStateException e) {
            // OK!
        }
    }

    @Test
    public void registerLifecycleCallbacksShouldBeCalledForActivity() {
        Activity activity = Mockito.mock(Activity.class);
        Application application = Mockito.mock(Application.class);
        Mockito.when(activity.getApplication()).thenReturn(application);
        Object key = new TestKey("hello");
        BackstackDelegate backstackDelegate = new BackstackDelegate();
        backstackDelegate.onCreate(null, null, History.single(key));
        // THEN
        backstackDelegate.registerForLifecycleCallbacks(activity);
        Mockito.verify(activity, Mockito.times(1)).getApplication();
    }

    @Test
    public void callingOnPostResumeBeforeOnCreateShouldThrow() {
        BackstackDelegate backstackDelegate = new BackstackDelegate();
        backstackDelegate.setStateChanger(stateChanger);
        try {
            backstackDelegate.onPostResume();
            Assert.fail();
        } catch(IllegalStateException e) {
            assertThat(e.getMessage()).contains("This method can only be called after calling `onCreate()`");
            // OK!
        }
    }

    @Test
    public void callingOnPauseBeforeOnCreateShouldThrow() {
        BackstackDelegate backstackDelegate = new BackstackDelegate();
        try {
            backstackDelegate.onPause();
            Assert.fail();
        } catch(IllegalStateException e) {
            // OK!
        }
    }
}
