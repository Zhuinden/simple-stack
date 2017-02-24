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
    Bundle savedInstanceState;

    @Mock
    Backstack backstack;

    @Mock
    ServiceManager serviceManager;

    @Mock(extraInterfaces = Bundleable.class)
    View view;

    @Mock
    Context context;

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
        BackstackDelegate backstackDelegate = BackstackDelegate.create();
        try {
            backstackDelegate.setPersistenceTag(null);
            fail();
        } catch(IllegalArgumentException e) {
            // OK!
        }
    }

    @Test
    public void setSamePersistenceTagTwiceShouldBeOk() {
        BackstackDelegate backstackDelegate = BackstackDelegate.create();
        backstackDelegate.setPersistenceTag(new String("hello"));
        backstackDelegate.setPersistenceTag(new String("hello"));
        // no exceptions thrown
    }

    @Test
    public void setTwoDifferentPersistenceTagsShouldThrow() {
        BackstackDelegate backstackDelegate = BackstackDelegate.create();
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
        BackstackDelegate backstackDelegate = BackstackDelegate.create();
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
        BackstackDelegate backstackDelegate = BackstackDelegate.create();
        TestKey testKey = new TestKey("hello");
        final TestKey restoredKey = new TestKey("world");
        ArrayList<Parcelable> restoredKeys = new ArrayList<Parcelable>() {{
            add(restoredKey);
        }};
        StateBundle stateBundle = new StateBundle();
        stateBundle.putParcelableArrayList("HISTORY", restoredKeys);
        Mockito.when(savedInstanceState.getParcelable(backstackDelegate.getHistoryTag())).thenReturn(stateBundle);
        backstackDelegate.onCreate(savedInstanceState, null, HistoryBuilder.single(testKey));
        assertThat(backstackDelegate.getBackstack()).isNotNull();
        backstackDelegate.setStateChanger(stateChanger);
        assertThat(backstackDelegate.getBackstack().getHistory()).containsExactly(restoredKey);
    }

    @Test
    public void onCreateChoosesInitialKeysIfRestoredHistoryIsEmpty() {
        BackstackDelegate backstackDelegate = BackstackDelegate.create();
        TestKey testKey = new TestKey("hello");
        ArrayList<Parcelable> restoredKeys = new ArrayList<>();
        Mockito.when(savedInstanceState.getParcelableArrayList(backstackDelegate.getHistoryTag())).thenReturn(restoredKeys);
        backstackDelegate.onCreate(savedInstanceState, null, HistoryBuilder.single(testKey));
        assertThat(backstackDelegate.getBackstack()).isNotNull();
        backstackDelegate.setStateChanger(stateChanger);
        assertThat(backstackDelegate.getBackstack().getHistory()).containsExactly(testKey);
    }

    @Test
    public void getSavedStateForNullThrowsException() {
        BackstackDelegate backstackDelegate = BackstackDelegate.create();
        try {
            backstackDelegate.getSavedState(null);
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // OK
        }
    }

    @Test
    public void onCreateInvalidNonConfigurationThrowsException() {
        BackstackDelegate backstackDelegate = BackstackDelegate.create();
        try {
            backstackDelegate.onCreate(null, new TestKey("crashpls"), HistoryBuilder.single(new TestKey("hello")));
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // OK
        }
    }

    @Test
    public void onCreateRestoresFromNonConfigInstance() {
        BackstackDelegate.NonConfigurationInstance nonConfigurationInstance = new BackstackDelegate.NonConfigurationInstance(backstack,
                serviceManager);
        BackstackDelegate backstackDelegate = BackstackDelegate.create();
        TestKey testKey = new TestKey("hello");
        backstackDelegate.onCreate(null, nonConfigurationInstance, HistoryBuilder.single(testKey));
        assertThat(backstackDelegate.getBackstack()).isSameAs(backstack);
    }

    @Test
    public void nullServiceFactoryThrowsException() {
        try {
            BackstackDelegate.configure().addServiceFactory(null);
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // OK!
        }
    }

    @Test
    public void goingBackTearsDownUnneededKeys() {
        final TestKey first = new TestKey("hello");
        final Services.Child second = new Services.Child() {
            @Override
            public Object parent() {
                return first;
            }
        };
        BackstackDelegate backstackDelegate = BackstackDelegate.configure().addServiceFactory(new ServiceFactory() {
            @Override
            public void bindServices(@NonNull Services.Builder builder) {
                if(builder.getKey() == first) {
                    builder.withService("FIRST", "FIRST");
                } else if(builder.getKey() == second) {
                    builder.withService("SECOND", "SECOND");
                }
            }
        }).build();
        backstackDelegate.onCreate(null, null, HistoryBuilder.from().add(first).add(second).build());
        backstackDelegate.setStateChanger(stateChanger);
        assertThat(backstackDelegate.getBackstack().getHistory()).containsExactly(first, second);
        assertThat(backstackDelegate.findService(first, "FIRST")).isEqualTo("FIRST");
        assertThat(backstackDelegate.findService(second, "SECOND")).isEqualTo("SECOND");
        backstackDelegate.getBackstack().goBack();
        try {
            backstackDelegate.findService(second, "SECOND");
            Assert.fail();
        } catch(IllegalStateException e) {
            // OK
        }
    }

    @Test
    public void childWithoutRelationOnlyCreatesServicesWhenNavigatingToThem() {
        final TestKey first = new TestKey("hello");
        final TestKey second = new TestKey("world");
        BackstackDelegate backstackDelegate = BackstackDelegate.configure().addServiceFactory(new ServiceFactory() {
            @Override
            public void bindServices(@NonNull Services.Builder builder) {
                if(builder.getKey() == first) {
                    builder.withService("FIRST", "FIRST");
                } else if(builder.getKey() == second) {
                    builder.withService("SECOND", "SECOND");
                }
            }
        }).build();
        backstackDelegate.onCreate(null, null, HistoryBuilder.from(first, second).build());
        backstackDelegate.setStateChanger(stateChanger);
        assertThat(backstackDelegate.getBackstack().getHistory()).containsExactly(first, second);
        try {
            backstackDelegate.findService(first, "FIRST");
        } catch(IllegalStateException e) {
            // OK!
        }
        assertThat(backstackDelegate.findService(second, "SECOND")).isEqualTo("SECOND");

        backstackDelegate.getBackstack().goBack();
        assertThat(backstackDelegate.findService(first, "FIRST")).isEqualTo("FIRST");
        try {
            backstackDelegate.findService(second, "SECOND");
            Assert.fail();
        } catch(IllegalStateException e) {
            // OK
        }
    }

//    @Test
//    @Ignore // unfortunately, `serviceBundle.toBundle()` just isn't very unit-test-friendly
//    public void onCreateRestoresStatesOfService() {
//        class Service implements Bundleable {
//            String name;
//
//            @NonNull
//            @Override
//            public StateBundle toBundle() {
//                return new StateBundle();
//            }
//
//            @Override
//            public void fromBundle(@Nullable StateBundle bundle) {
//                if(bundle != null) {
//                    name = bundle.getString("SERVICE");
//                }
//            }
//        }
//
//        final Service service = new Service();
//
//        final TestKey testKey = new TestKey("hello");
//        ArrayList<Parcelable> parcelledStates = new ArrayList<>();
//        BackstackDelegate.ParcelledState parcelledState = new BackstackDelegate.ParcelledState();
//        parcelledStates.add(parcelledState);
//        parcelledState.parcelableKey = testKey;
//        Bundle bundle = Mockito.mock(Bundle.class);
//        StateBundle viewStateBundle = new StateBundle();
//        viewStateBundle.putString("VIEW", "VIEW");
//        StateBundle serviceStateBundle = new StateBundle();
//        serviceStateBundle.putString("SERVICE", "SERVICE");
//
//        Mockito.when(bundle.getBundle("VIEW_BUNDLE")).thenReturn(viewStateBundle.toBundle()); // won't work
//        Mockito.when(bundle.getBundle("SERVICE_BUNDLE")).thenReturn(serviceStateBundle.toBundle()); // won't work
//        parcelledState.bundle = bundle;
//
//        BackstackDelegate backstackDelegate = BackstackDelegate.configure().addServiceFactory(new ServiceFactory() {
//            @Override
//            public void bindServices(@NonNull Services.Builder builder) {
//                if(builder.getKey() == testKey) {
//                    builder.withService("S", service);
//                }
//            }
//        }).build();
//        Mockito.when(savedInstanceState.getParcelableArrayList(backstackDelegate.getStateTag())).thenReturn(parcelledStates);
//        backstackDelegate.setStateChanger(stateChanger);
//
//        assertThat(service.name).isEqualTo("SERVICE");
//    }

    @Test
    public void testRestoreViewFromState() {
        BackstackDelegate backstackDelegate = BackstackDelegate.create();
        TestKey key = new TestKey("hello");
        backstackDelegate.onCreate(null, null, HistoryBuilder.single(key));
        backstackDelegate.setStateChanger(stateChanger);

        Mockito.when(view.getContext()).thenReturn(context);
        StateBundle stateBundle = new StateBundle();
        Mockito.when(((Bundleable) view).toBundle()).thenReturn(stateBundle);
        // noinspection ResourceType
        Mockito.when(context.getSystemService(ManagedContextWrapper.TAG)).thenReturn(key);
        backstackDelegate.persistViewToState(view);

        backstackDelegate.restoreViewFromState(view);
        ((Bundleable) Mockito.verify(view, Mockito.times(1))).fromBundle(stateBundle);
    }

    @Test
    public void onBackPressedGoesBack() {
        BackstackDelegate backstackDelegate = BackstackDelegate.create();
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
        BackstackDelegate backstackDelegate = BackstackDelegate.create();
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
        BackstackDelegate backstackDelegate = BackstackDelegate.create();
        TestKey key = new TestKey("hello");
        backstackDelegate.onCreate(null, null, HistoryBuilder.single(key));
        backstackDelegate.setStateChanger(stateChanger);
        backstackDelegate.onPause();
        assertThat(backstackDelegate.getBackstack().hasStateChanger()).isFalse();
    }

    @Test
    public void onPostResumeReattachesStateChanger() {
        BackstackDelegate backstackDelegate = BackstackDelegate.create();
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
        BackstackDelegate backstackDelegate = BackstackDelegate.create();
        try {
            backstackDelegate.getBackstack();
            Assert.fail();
        } catch(IllegalStateException e) {
            // OK
        }
    }
}
