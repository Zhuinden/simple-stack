/*
 * Copyright 2018 Gabor Varadi
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
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.zhuinden.statebundle.StateBundle;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

public class ScopingTest {
    private static final Map<String, Object> services = new LinkedHashMap<>();

    private static final String SERVICE_TAG = "service";

    static {
        services.put(SERVICE_TAG, new Service());
    }

    private static class Service
            implements Bundleable, ScopedServices.Registered {
        int blah = 2;

        boolean didServiceRegister = false;
        boolean didServiceUnregister = false;

        @NonNull
        @Override
        public StateBundle toBundle() {
            StateBundle stateBundle = new StateBundle();
            stateBundle.putInt("blah", 5);
            return stateBundle;
        }

        @Override
        public void fromBundle(@Nullable StateBundle bundle) {
            if(bundle != null) {
                blah = bundle.getInt("blah");
            }
        }

        @Override
        public void onServiceRegistered() {
            didServiceRegister = true;
        }

        @Override
        public void onServiceUnregistered() {
            didServiceUnregister = true;
        }
    }

    StateChanger stateChanger = new StateChanger() {
        @Override
        public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
            completionCallback.stateChangeComplete();
        }
    };

    private interface HasServices
            extends ScopeKey {
        void bindServices(ScopedServices.ServiceBinder serviceBinder);
    }

    private static class ServiceProvider
            implements ScopedServices {
        @Override
        public void bindServices(@NonNull ServiceBinder serviceBinder) {
            Object key = serviceBinder.getKey();
            if(key instanceof HasServices) {
                ((HasServices) key).bindServices(serviceBinder);
            }
        }
    }

    private static class TestKeyWithScope
            extends TestKey
            implements ScopeKey, HasServices {
        TestKeyWithScope(String name) {
            super(name);
        }

        protected TestKeyWithScope(Parcel in) {
            super(in);
        }

        @NonNull
        @Override
        public String getScopeTag() {
            return name;
        }

        @Override
        public void bindServices(ScopedServices.ServiceBinder serviceBinder) {
            serviceBinder.add(SERVICE_TAG, services.get(SERVICE_TAG));
        }

        public static final Creator<TestKeyWithScope> CREATOR = new Creator<TestKeyWithScope>() {
            @Override
            public TestKeyWithScope createFromParcel(Parcel in) {
                return new TestKeyWithScope(in);
            }

            @Override
            public TestKeyWithScope[] newArray(int size) {
                return new TestKeyWithScope[size];
            }
        };
    }

    private TestKey testKey1 = new TestKey("hello");
    private TestKeyWithScope testKey2 = new TestKeyWithScope("world");
    private TestKey testKey3 = new TestKey("!");

    @Test
    public void scopedServicesShouldNotBeNull() {
        BackstackManager backstackManager = new BackstackManager();
        try {
            backstackManager.setScopedServices(null);
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // OK!
        }
    }

    @Test
    public void scopedServicesShouldBeSetBeforeSetup() {
        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setup(History.of(testKey1));
        try {
            backstackManager.setScopedServices(new ServiceProvider());
            Assert.fail();
        } catch(IllegalStateException e) {
            // OK!
        }
    }

    @Test
    public void scopedServicesThrowIfNoScopedServicesAreDefinedAndServicesAreToBeBound() {
        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setup(History.of(testKey2));

        try {
            backstackManager.setStateChanger(stateChanger);
            Assert.fail();
        } catch(IllegalStateException e) {
            assertThat(e.getMessage()).contains("scoped services");
        }
    }

    @Test
    public void scopeIsCreatedForScopeKeys() {
        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setScopedServices(new ServiceProvider());
        backstackManager.setup(History.single(testKey2));
        assertThat(backstackManager.hasService(testKey2, SERVICE_TAG)).isFalse();
        backstackManager.setStateChanger(stateChanger);
        assertThat(backstackManager.hasService(testKey2, SERVICE_TAG)).isTrue();

        Service service = backstackManager.getService(testKey2, SERVICE_TAG);
        assertThat(service).isSameAs(services.get(SERVICE_TAG));
    }

    @Test
    public void gettingNonExistentServiceThrows() {
        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setScopedServices(new ServiceProvider());
        backstackManager.setup(History.single(testKey2));
        assertThat(backstackManager.hasService(testKey2, SERVICE_TAG)).isFalse();
        backstackManager.setStateChanger(stateChanger);
        assertThat(backstackManager.hasService(testKey2, SERVICE_TAG)).isTrue();

        try {
            backstackManager.getService(testKey2, "d'oh");
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // OK!
        }
    }

    @Test
    public void serviceBinderAddThrowsForNullServiceTag() {
        final String nullTag = null;
        final Object service = new Service();
        TestKeyWithScope testKeyWithScope = new TestKeyWithScope("blah") {
            @Override
            public void bindServices(ScopedServices.ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());
                serviceBinder.add(nullTag, service);
            }

            @NonNull
            @Override
            public String getScopeTag() {
                return "beep";
            }
        };

        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setScopedServices(new ServiceProvider());
        backstackManager.setup(History.of(testKeyWithScope));
        try {
            backstackManager.setStateChanger(stateChanger);
            Assert.fail();
        } catch(Exception e) {
            assertThat(e.getMessage()).isEqualTo("Service tag cannot be null!");
        }
    }

    @Test
    public void serviceBinderAddThrowsForNullService() {
        final String serviceTag = "serviceTag";
        final Object nullService = null;
        TestKeyWithScope testKeyWithScope = new TestKeyWithScope("blah") {
            @Override
            public void bindServices(ScopedServices.ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());
                serviceBinder.add(serviceTag, null);
            }

            @NonNull
            @Override
            public String getScopeTag() {
                return "beep";
            }
        };

        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setScopedServices(new ServiceProvider());
        backstackManager.setup(History.of(testKeyWithScope));
        try {
            backstackManager.setStateChanger(stateChanger);
            Assert.fail();
        } catch(Exception e) {
            assertThat(e.getMessage()).isEqualTo("The provided service should not be null!");
        }
    }

    @Test
    public void scopeIsDestroyedForClearedScopeKeys() {
        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setScopedServices(new ServiceProvider());
        backstackManager.setup(History.single(testKey2));

        assertThat(backstackManager.hasService(testKey2, SERVICE_TAG)).isFalse();
        backstackManager.setStateChanger(stateChanger);
        assertThat(backstackManager.hasService(testKey2, SERVICE_TAG)).isTrue();

        backstackManager.getBackstack().setHistory(History.of(testKey1), StateChange.REPLACE);

        assertThat(backstackManager.hasService(testKey2, SERVICE_TAG)).isFalse();
    }

    @Test
    public void scopeServicesArePersistedToStateBundle() {
        final ScopeManager scopeManager = new ScopeManager();

        scopeManager.setScopedServices(new ServiceProvider());
        final Service service = new Service();
        TestKeyWithScope testKeyWithScope = new TestKeyWithScope("blah") {
            @Override
            public void bindServices(ScopedServices.ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());
                serviceBinder.add(SERVICE_TAG, service);
            }

            @NonNull
            @Override
            public String getScopeTag() {
                return "beep";
            }
        };

        Backstack backstack = new Backstack(History.of(testKeyWithScope));
        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                scopeManager.buildScopes(stateChange.getNewState());
                completionCallback.stateChangeComplete();
            }
        });

        assertThat(scopeManager.hasService(testKeyWithScope.getScopeTag(), SERVICE_TAG)).isTrue();

        StateBundle stateBundle = scopeManager.saveStates();

        //noinspection ConstantConditions
        assertThat(stateBundle.getBundle(testKeyWithScope.getScopeTag()).getBundle(SERVICE_TAG).getInt("blah"))
                .isEqualTo(5);
    }

    @Test
    public void persistedStateOfScopedServicesIsRestored() {
        final ScopeManager scopeManager = new ScopeManager();
        scopeManager.setScopedServices(new ServiceProvider());

        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                scopeManager.buildScopes(stateChange.getNewState());
                completionCallback.stateChangeComplete();
            }
        };

        Backstack backstack = new Backstack(History.of(testKey2));
        backstack.setStateChanger(stateChanger);

        assertThat(scopeManager.hasService(testKey2.getScopeTag(), SERVICE_TAG));

        StateBundle stateBundle = scopeManager.saveStates();

        final ScopeManager scopeManager2 = new ScopeManager();
        scopeManager2.setScopedServices(new ServiceProvider());

        StateChanger stateChanger2 = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                scopeManager2.buildScopes(stateChange.getNewState());
                completionCallback.stateChangeComplete();
            }
        };

        scopeManager2.setRestoredStates(stateBundle);

        Backstack backstack2 = new Backstack(History.of(testKey2));

        backstack2.setStateChanger(stateChanger2);
        assertThat(scopeManager2.<Service>getService(testKey2.getScopeTag(), SERVICE_TAG).blah).isEqualTo(5);
    }

    @Test
    public void nonExistentServiceShouldReturnFalseAndThrow() {
        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setScopedServices(new ServiceProvider());
        backstackManager.setup(History.of(testKey2));

        assertThat(backstackManager.hasService(testKey2, SERVICE_TAG)).isFalse();
        try {
            backstackManager.getService(testKey2, SERVICE_TAG);
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // OK!
        }
    }

    @Test
    public void scopedServiceCallbackIsCalledCorrectly() {
        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setScopedServices(new ServiceProvider());
        final Service service = new Service();
        TestKeyWithScope testKeyWithScope = new TestKeyWithScope("blah") {
            @Override
            public void bindServices(ScopedServices.ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());
                serviceBinder.add(SERVICE_TAG, service);
            }

            @NonNull
            @Override
            public String getScopeTag() {
                return "beep";
            }
        };

        backstackManager.setup(History.of(testKeyWithScope));

        assertThat(service.didServiceRegister).isFalse();
        assertThat(service.didServiceUnregister).isFalse();

        backstackManager.setStateChanger(stateChanger);

        assertThat(service.didServiceRegister).isTrue();
        assertThat(service.didServiceUnregister).isFalse();

        backstackManager.getBackstack().setHistory(History.single(testKey1), StateChange.REPLACE);

        assertThat(service.didServiceRegister).isTrue();
        assertThat(service.didServiceUnregister).isTrue();
    }

    @Test
    public void scopesAreFinalizedWhenActivityIsFinishing() {
        Activity activity = Mockito.mock(Activity.class);
        Mockito.when(activity.isFinishing()).thenReturn(true);

        final Service service = new Service();
        TestKeyWithScope testKeyWithScope = new TestKeyWithScope("blah") {
            @Override
            public void bindServices(ScopedServices.ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());
                serviceBinder.add(SERVICE_TAG, service);
            }

            @NonNull
            @Override
            public String getScopeTag() {
                return "beep";
            }
        };

        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        };
        BackstackDelegate backstackDelegate = new BackstackDelegate();
        backstackDelegate.setScopedServices(activity, new ServiceProvider());
        backstackDelegate.onCreate(null, null, History.of(testKeyWithScope));
        backstackDelegate.setStateChanger(stateChanger);

        backstackDelegate.onPostResume();
        backstackDelegate.onPause();

        assertThat(backstackDelegate.hasScope("beep")).isTrue();
        assertThat(backstackDelegate.hasService(testKeyWithScope, SERVICE_TAG)).isTrue();
        assertThat(service.didServiceRegister).isTrue();
        assertThat(service.didServiceUnregister).isFalse();
        backstackDelegate.onDestroy();
        assertThat(backstackDelegate.hasScope("beep")).isFalse();
        assertThat(backstackDelegate.hasService(testKeyWithScope, SERVICE_TAG)).isFalse();
        assertThat(service.didServiceRegister).isTrue();
        assertThat(service.didServiceUnregister).isTrue();
    }

    @Test
    public void lookupServiceNoOverlapsWorks() {
        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setScopedServices(new ServiceProvider());
        final Service service = new Service();
        TestKeyWithScope beep = new TestKeyWithScope("beep") {
            @Override
            public void bindServices(ScopedServices.ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());
                serviceBinder.add(SERVICE_TAG, service);
            }

            @NonNull
            @Override
            public String getScopeTag() {
                return "beep";
            }
        };

        TestKeyWithScope boop = new TestKeyWithScope("boop") {
            @Override
            public void bindServices(ScopedServices.ServiceBinder serviceBinder) {
            }

            @NonNull
            @Override
            public String getScopeTag() {
                return "boop";
            }
        };

        backstackManager.setup(History.of(beep, boop));

        assertThat(backstackManager.hasService("beep", SERVICE_TAG)).isFalse();
        backstackManager.setStateChanger(stateChanger);
        assertThat(backstackManager.hasService("beep", SERVICE_TAG)).isTrue();
        assertThat(backstackManager.hasService("boop", SERVICE_TAG)).isFalse();
        assertThat(backstackManager.<Object>lookupService(SERVICE_TAG)).isSameAs(service);
        backstackManager.getBackstack().goBack();
        assertThat(backstackManager.hasService("beep", SERVICE_TAG)).isTrue();
        assertThat(backstackManager.<Object>lookupService(SERVICE_TAG)).isSameAs(service);
        backstackManager.getBackstack().setHistory(History.single(testKey1), StateChange.REPLACE);
        assertThat(backstackManager.hasService("beep", SERVICE_TAG)).isFalse();
        try {
            backstackManager.lookupService(SERVICE_TAG);
            Assert.fail();
        } catch(IllegalStateException e) {
            assertThat(e.getMessage()).contains("does not exist in any scope");
            // OK!
        }
    }

    @Test
    public void lookupServiceWithOverlapsWorks() {
        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setScopedServices(new ServiceProvider());
        final Service service1 = new Service();
        final Service service2 = new Service();
        TestKeyWithScope beep = new TestKeyWithScope("beep") {
            @Override
            public void bindServices(ScopedServices.ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());
                serviceBinder.add(SERVICE_TAG, service1);
            }

            @NonNull
            @Override
            public String getScopeTag() {
                return "beep";
            }
        };

        TestKeyWithScope boop = new TestKeyWithScope("boop") {
            @Override
            public void bindServices(ScopedServices.ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());
                serviceBinder.add(SERVICE_TAG, service2);
            }

            @NonNull
            @Override
            public String getScopeTag() {
                return "boop";
            }
        };

        backstackManager.setup(History.of(beep, boop));

        assertThat(backstackManager.hasScope("beep")).isFalse();
        assertThat(backstackManager.hasScope("boop")).isFalse();
        assertThat(backstackManager.hasService("beep", SERVICE_TAG)).isFalse();
        assertThat(backstackManager.hasService("boop", SERVICE_TAG)).isFalse();
        backstackManager.setStateChanger(stateChanger);
        assertThat(backstackManager.hasScope("beep")).isTrue();
        assertThat(backstackManager.hasScope("boop")).isTrue();
        assertThat(backstackManager.hasService("beep", SERVICE_TAG)).isTrue();
        assertThat(backstackManager.hasService("boop", SERVICE_TAG)).isTrue();
        assertThat(backstackManager.<Object>lookupService(SERVICE_TAG)).isSameAs(service2);
        backstackManager.getBackstack().goBack();
        assertThat(backstackManager.hasScope("beep")).isTrue();
        assertThat(backstackManager.hasScope("boop")).isFalse();
        assertThat(backstackManager.hasService("beep", SERVICE_TAG)).isTrue();
        assertThat(backstackManager.hasService("boop", SERVICE_TAG)).isFalse();
        assertThat(backstackManager.<Object>lookupService(SERVICE_TAG)).isSameAs(service1);
        backstackManager.getBackstack().setHistory(History.single(testKey1), StateChange.REPLACE);
        assertThat(backstackManager.hasScope("beep")).isFalse();
        assertThat(backstackManager.hasScope("boop")).isFalse();
        assertThat(backstackManager.hasService("beep", SERVICE_TAG)).isFalse();
        assertThat(backstackManager.hasService("boop", SERVICE_TAG)).isFalse();
        try {
            backstackManager.lookupService(SERVICE_TAG);
            Assert.fail();
        } catch(IllegalStateException e) {
            assertThat(e.getMessage()).contains("does not exist in any scope");
            // OK!
        }
    }

    @Test
    public void canFindServiceNoOverlapsWorks() {
        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setScopedServices(new ServiceProvider());
        final Service service = new Service();
        TestKeyWithScope beep = new TestKeyWithScope("beep") {
            @Override
            public void bindServices(ScopedServices.ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());
                serviceBinder.add(SERVICE_TAG, service);
            }

            @NonNull
            @Override
            public String getScopeTag() {
                return "beep";
            }
        };

        TestKeyWithScope boop = new TestKeyWithScope("boop") {
            @Override
            public void bindServices(ScopedServices.ServiceBinder serviceBinder) {
            }

            @NonNull
            @Override
            public String getScopeTag() {
                return "boop";
            }
        };

        backstackManager.setup(History.of(beep, boop));

        assertThat(backstackManager.hasService("beep", SERVICE_TAG)).isFalse();
        backstackManager.setStateChanger(stateChanger);
        assertThat(backstackManager.hasService("beep", SERVICE_TAG)).isTrue();
        assertThat(backstackManager.hasService("boop", SERVICE_TAG)).isFalse();
        assertThat(backstackManager.<Object>canFindService(SERVICE_TAG)).isTrue();
        backstackManager.getBackstack().goBack();
        assertThat(backstackManager.hasService("beep", SERVICE_TAG)).isTrue();
        assertThat(backstackManager.<Object>canFindService(SERVICE_TAG)).isTrue();
        backstackManager.getBackstack().setHistory(History.single(testKey1), StateChange.REPLACE);
        assertThat(backstackManager.hasService("beep", SERVICE_TAG)).isFalse();
        assertThat(backstackManager.canFindService(SERVICE_TAG)).isFalse();
    }

    @Test
    public void canFindServiceWithOverlapsWorks() {
        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setScopedServices(new ServiceProvider());
        final Service service1 = new Service();
        final Service service2 = new Service();
        TestKeyWithScope beep = new TestKeyWithScope("beep") {
            @Override
            public void bindServices(ScopedServices.ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());
                serviceBinder.add(SERVICE_TAG, service1);
            }

            @NonNull
            @Override
            public String getScopeTag() {
                return "beep";
            }
        };

        TestKeyWithScope boop = new TestKeyWithScope("boop") {
            @Override
            public void bindServices(ScopedServices.ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());
                serviceBinder.add(SERVICE_TAG, service2);
            }

            @NonNull
            @Override
            public String getScopeTag() {
                return "boop";
            }
        };

        backstackManager.setup(History.of(beep, boop));

        assertThat(backstackManager.hasService("beep", SERVICE_TAG)).isFalse();
        assertThat(backstackManager.hasService("boop", SERVICE_TAG)).isFalse();
        backstackManager.setStateChanger(stateChanger);
        assertThat(backstackManager.hasService("beep", SERVICE_TAG)).isTrue();
        assertThat(backstackManager.hasService("boop", SERVICE_TAG)).isTrue();
        assertThat(backstackManager.<Object>canFindService(SERVICE_TAG)).isTrue();
        backstackManager.getBackstack().goBack();
        assertThat(backstackManager.hasService("beep", SERVICE_TAG)).isTrue();
        assertThat(backstackManager.hasService("boop", SERVICE_TAG)).isFalse();
        assertThat(backstackManager.<Object>canFindService(SERVICE_TAG)).isTrue();
        backstackManager.getBackstack().setHistory(History.single(testKey1), StateChange.REPLACE);
        assertThat(backstackManager.hasService("beep", SERVICE_TAG)).isFalse();
        assertThat(backstackManager.hasService("boop", SERVICE_TAG)).isFalse();
        assertThat(backstackManager.canFindService(SERVICE_TAG)).isFalse();
    }

    @Test
    public void serviceBinderMethodsWork() {
        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setScopedServices(new ServiceProvider());
        final Service service1 = new Service();
        final Service service2 = new Service();
        TestKeyWithScope beep = new TestKeyWithScope("beep") {
            @Override
            public void bindServices(ScopedServices.ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());

                assertThat(serviceBinder.has("SERVICE1")).isFalse();
                assertThat(serviceBinder.canFind("SERVICE1")).isFalse();
                serviceBinder.add("SERVICE1", service1);
                assertThat(serviceBinder.has("SERVICE1")).isTrue();
                assertThat(serviceBinder.canFind("SERVICE1")).isTrue();

                assertThat(serviceBinder.lookup("SERVICE1")).isSameAs(service1);
            }

            @NonNull
            @Override
            public String getScopeTag() {
                return "beep";
            }
        };

        TestKeyWithScope boop = new TestKeyWithScope("boop") {
            @Override
            public void bindServices(ScopedServices.ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());

                assertThat(serviceBinder.has("SERVICE1")).isFalse();
                assertThat(serviceBinder.canFind("SERVICE1")).isTrue();
                assertThat(serviceBinder.has("SERVICE2")).isFalse();
                serviceBinder.add("SERVICE2", service2);
                assertThat(serviceBinder.has("SERVICE1")).isFalse();
                assertThat(serviceBinder.canFind("SERVICE1")).isTrue();
                assertThat(serviceBinder.has("SERVICE2")).isTrue();

                assertThat(serviceBinder.lookup("SERVICE1")).isSameAs(service1);
                assertThat(serviceBinder.lookup("SERVICE2")).isSameAs(service2);
            }

            @NonNull
            @Override
            public String getScopeTag() {
                return "boop";
            }
        };

        backstackManager.setup(History.of(beep, boop));

        backstackManager.setStateChanger(stateChanger);
    }

    @Test
    public void scopeCreationAndDestructionHappensInForwardAndReverseOrder() {
        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setScopedServices(new ServiceProvider());

        final List<Object> serviceRegistered = new ArrayList<>();
        final List<Object> serviceUnregistered = new ArrayList<>();

        class MyService implements ScopedServices.Registered {
            @Override
            public void onServiceRegistered() {
                serviceRegistered.add(this);
            }

            @Override
            public void onServiceUnregistered() {
                serviceUnregistered.add(this);
            }
        }

        final MyService service1 = new MyService();
        final MyService service2 = new MyService();

        TestKeyWithScope beep = new TestKeyWithScope("beep") {
            @Override
            public void bindServices(ScopedServices.ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());

                serviceBinder.add("SERVICE1", service1);
            }

            @NonNull
            @Override
            public String getScopeTag() {
                return "beep";
            }
        };

        TestKeyWithScope boop = new TestKeyWithScope("boop") {
            @Override
            public void bindServices(ScopedServices.ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());

                serviceBinder.add("SERVICE2", service2);
            }

            @NonNull
            @Override
            public String getScopeTag() {
                return "boop";
            }
        };

        TestKey bye = new TestKey("bye");

        backstackManager.setup(History.of(beep, boop));

        assertThat(serviceRegistered).isEmpty();
        assertThat(serviceUnregistered).isEmpty();
        backstackManager.setStateChanger(stateChanger);

        assertThat(serviceRegistered).containsExactly(service1, service2);
        assertThat(serviceUnregistered).isEmpty();

        backstackManager.getBackstack().setHistory(History.of(bye), StateChange.REPLACE);

        assertThat(serviceRegistered).containsExactly(service1, service2);
        assertThat(serviceUnregistered).containsExactly(service2, service1);
    }

    @Test
    public void serviceCreationAndDestructionHappensInForwardAndReverseOrder() {
        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setScopedServices(new ServiceProvider());

        final List<Object> serviceRegistered = new ArrayList<>();
        final List<Object> serviceUnregistered = new ArrayList<>();

        class MyService implements ScopedServices.Registered {
            @Override
            public void onServiceRegistered() {
                serviceRegistered.add(this);
            }

            @Override
            public void onServiceUnregistered() {
                serviceUnregistered.add(this);
            }
        }

        final MyService service1 = new MyService();
        final MyService service2 = new MyService();


        TestKeyWithScope beep = new TestKeyWithScope("beep") {
            @Override
            public void bindServices(ScopedServices.ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());

                serviceBinder.add("SERVICE1", service1);
                serviceBinder.add("SERVICE2", service2);
            }

            @NonNull
            @Override
            public String getScopeTag() {
                return "beep";
            }
        };

        TestKey bye = new TestKey("bye");

        backstackManager.setup(History.of(beep));

        assertThat(serviceRegistered).isEmpty();
        assertThat(serviceUnregistered).isEmpty();
        backstackManager.setStateChanger(stateChanger);

        assertThat(serviceRegistered).containsExactly(service1, service2);
        assertThat(serviceUnregistered).isEmpty();

        backstackManager.getBackstack().setHistory(History.of(bye), StateChange.REPLACE);

        assertThat(serviceRegistered).containsExactly(service1, service2);
        assertThat(serviceUnregistered).containsExactly(service2, service1);
    }

    @Test
    public void scopedServicesCanRetrieveBackstackFromServiceBinder() {
        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setScopedServices(new ServiceProvider());

        final AtomicReference<Backstack> backstack = new AtomicReference<>();

        class MyService {
        }

        final MyService service1 = new MyService();

        TestKeyWithScope beep = new TestKeyWithScope("beep") {
            @Override
            public void bindServices(ScopedServices.ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());

                serviceBinder.add("SERVICE1", service1);
                backstack.set(serviceBinder.getBackstack());
            }

            @NonNull
            @Override
            public String getScopeTag() {
                return "beep";
            }
        };

        backstackManager.setup(History.of(beep));
        backstackManager.setStateChanger(stateChanger);
        assertThat(backstackManager.getBackstack()).isSameAs(backstack.get());
    }

    @Test
    public void activatedWorks() {
        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setScopedServices(new ServiceProvider());

        final List<Object> activatedServices = new ArrayList<>();
        final List<Object> deactivatedServices = new ArrayList<>();

        class MyService
                implements ScopedServices.Activated {
            @Override
            public void onServiceActive() {
                activatedServices.add(this);
            }

            @Override
            public void onServiceInactive() {
                deactivatedServices.add(this);
            }
        }

        final MyService service1 = new MyService();
        final MyService service2 = new MyService();

        TestKeyWithScope beep = new TestKeyWithScope("beep") {
            @Override
            public void bindServices(ScopedServices.ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());

                serviceBinder.add("SERVICE1", service1);
            }

            @NonNull
            @Override
            public String getScopeTag() {
                return "beep";
            }
        };

        TestKeyWithScope boop = new TestKeyWithScope("boop") {
            @Override
            public void bindServices(ScopedServices.ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());

                serviceBinder.add("SERVICE2", service2);
            }

            @NonNull
            @Override
            public String getScopeTag() {
                return "boop";
            }
        };

        TestKey bye = new TestKey("bye");

        backstackManager.setup(History.of(beep, boop));

        assertThat(activatedServices).isEmpty();
        assertThat(deactivatedServices).isEmpty();
        backstackManager.setStateChanger(stateChanger);

        assertThat(activatedServices).containsExactly(service2);
        assertThat(deactivatedServices).isEmpty();

        backstackManager.getBackstack().goBack();

        assertThat(activatedServices).containsExactly(service2, service1);
        assertThat(deactivatedServices).containsExactly(service2);

        backstackManager.getBackstack().setHistory(History.of(bye), StateChange.REPLACE);

        assertThat(activatedServices).containsExactly(service2, service1);
        assertThat(deactivatedServices).containsExactly(service2, service1);
    }

    @Test
    public void activatedIsCalledInRightOrder() {
        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setScopedServices(new ServiceProvider());

        final List<Object> activatedServices = new ArrayList<>();
        final List<Object> deactivatedServices = new ArrayList<>();

        class MyService
                implements ScopedServices.Activated {
            @Override
            public void onServiceActive() {
                activatedServices.add(this);
            }

            @Override
            public void onServiceInactive() {
                deactivatedServices.add(this);
            }
        }

        final MyService service1 = new MyService();
        final MyService service2 = new MyService();
        final MyService service3 = new MyService();

        final MyService service4 = new MyService();
        final MyService service5 = new MyService();
        final MyService service6 = new MyService();

        TestKeyWithScope beep = new TestKeyWithScope("beep") {
            @Override
            public void bindServices(ScopedServices.ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());

                serviceBinder.add("SERVICE1", service1);
                serviceBinder.add("SERVICE2", service2);
                serviceBinder.add("SERVICE3", service3);
            }

            @NonNull
            @Override
            public String getScopeTag() {
                return "beep";
            }
        };


        TestKeyWithScope boop = new TestKeyWithScope("beep") {
            @Override
            public void bindServices(ScopedServices.ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());

                serviceBinder.add("SERVICE4", service4);
                serviceBinder.add("SERVICE5", service5);
                serviceBinder.add("SERVICE6", service6);
            }

            @NonNull
            @Override
            public String getScopeTag() {
                return "boop";
            }
        };

        backstackManager.setup(History.of(beep));

        assertThat(activatedServices).isEmpty();
        backstackManager.setStateChanger(stateChanger);

        assertThat(activatedServices).containsExactly(service1, service2, service3);

        backstackManager.getBackstack().goTo(boop);
        assertThat(activatedServices).containsExactly(service1, service2, service3, service4, service5, service6);
        assertThat(deactivatedServices).containsExactly(service3, service2, service1);
    }

    @Test
    public void deactivatedIsCalledInReverseOrder() {
        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setScopedServices(new ServiceProvider());

        final List<Object> deactivatedServices = new ArrayList<>();

        class MyService
                implements ScopedServices.Activated {
            @Override
            public void onServiceActive() {
            }

            @Override
            public void onServiceInactive() {
                deactivatedServices.add(this);
            }
        }

        final MyService service1 = new MyService();
        final MyService service2 = new MyService();
        final MyService service3 = new MyService();

        final MyService service4 = new MyService();
        final MyService service5 = new MyService();
        final MyService service6 = new MyService();

        TestKeyWithScope beep = new TestKeyWithScope("beep") {
            @Override
            public void bindServices(ScopedServices.ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());

                serviceBinder.add("SERVICE1", service1);
                serviceBinder.add("SERVICE2", service2);
                serviceBinder.add("SERVICE3", service3);
            }

            @NonNull
            @Override
            public String getScopeTag() {
                return "beep";
            }
        };


        TestKeyWithScope boop = new TestKeyWithScope("beep") {
            @Override
            public void bindServices(ScopedServices.ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());

                serviceBinder.add("SERVICE4", service4);
                serviceBinder.add("SERVICE5", service5);
                serviceBinder.add("SERVICE6", service6);
            }

            @NonNull
            @Override
            public String getScopeTag() {
                return "boop";
            }
        };

        backstackManager.setup(History.of(beep));

        assertThat(deactivatedServices).isEmpty();
        backstackManager.setStateChanger(stateChanger);

        backstackManager.getBackstack().goTo(boop);

        assertThat(deactivatedServices).containsExactly(service3, service2, service1);

        TestKey bye = new TestKey("bye");
        backstackManager.getBackstack().setHistory(History.of(bye), StateChange.REPLACE);

        assertThat(deactivatedServices).containsExactly(service3, service2, service1, service6, service5, service4);
    }

    @Test
    public void activationIsCalledOnlyOnce() {
        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setScopedServices(new ServiceProvider());

        final List<Object> activatedServices = new ArrayList<>();
        final List<Object> deactivatedServices = new ArrayList<>();

        class MyService
                implements ScopedServices.Activated {
            @Override
            public void onServiceActive() {
                activatedServices.add(this);
            }

            @Override
            public void onServiceInactive() {
                deactivatedServices.add(this);
            }
        }

        final MyService service1 = new MyService();

        TestKeyWithScope beep = new TestKeyWithScope("beep") {
            @Override
            public void bindServices(ScopedServices.ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());

                serviceBinder.add("SERVICE1", service1);
            }

            @NonNull
            @Override
            public String getScopeTag() {
                return "beep";
            }
        };

        backstackManager.setup(History.of(beep));

        assertThat(activatedServices).isEmpty();
        assertThat(deactivatedServices).isEmpty();
        backstackManager.setStateChanger(stateChanger);

        assertThat(activatedServices).containsExactly(service1);

        backstackManager.getBackstack().removeStateChanger();
        backstackManager.setStateChanger(stateChanger);

        assertThat(activatedServices).containsExactly(service1);
    }

    @Test
    public void deactivationIsCalledOnlyOnce() {
        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setScopedServices(new ServiceProvider());

        final List<Object> activatedServices = new ArrayList<>();
        final List<Object> deactivatedServices = new ArrayList<>();

        class MyService
                implements ScopedServices.Activated {
            @Override
            public void onServiceActive() {
                activatedServices.add(this);
            }

            @Override
            public void onServiceInactive() {
                deactivatedServices.add(this);
            }
        }

        final MyService service1 = new MyService();

        TestKeyWithScope beep = new TestKeyWithScope("beep") {
            @Override
            public void bindServices(ScopedServices.ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());

                serviceBinder.add("SERVICE1", service1);
            }

            @NonNull
            @Override
            public String getScopeTag() {
                return "beep";
            }
        };

        TestKey bye = new TestKey("bye");
        backstackManager.setup(History.of(beep));

        assertThat(activatedServices).isEmpty();
        assertThat(deactivatedServices).isEmpty();
        backstackManager.setStateChanger(stateChanger);

        assertThat(activatedServices).containsExactly(service1);
        assertThat(deactivatedServices).isEmpty();

        backstackManager.getBackstack().removeStateChanger();
        assertThat(deactivatedServices).isEmpty();

        backstackManager.getBackstack().setHistory(History.of(bye), StateChange.REPLACE);
        assertThat(deactivatedServices).isEmpty();

        backstackManager.setStateChanger(stateChanger);
        assertThat(deactivatedServices).containsExactly(service1);

        backstackManager.getBackstack().removeStateChanger();
        backstackManager.setStateChanger(stateChanger);
        assertThat(deactivatedServices).containsExactly(service1);
    }

    @Test
    public void activationHappensEvenWithForceExecutedStateChangeAndInitializeStateChange() {
        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setScopedServices(new ServiceProvider());

        final List<Object> activatedServices = new ArrayList<>();
        final List<Object> deactivatedServices = new ArrayList<>();

        final AtomicReference<StateChanger.Callback> callback = new AtomicReference<>();
        StateChanger pendingStateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                callback.set(completionCallback);
            }
        };

        class MyService
                implements ScopedServices.Activated {
            @Override
            public void onServiceActive() {
                activatedServices.add(this);
            }

            @Override
            public void onServiceInactive() {
                deactivatedServices.add(this);
            }
        }

        final MyService service1 = new MyService();
        final MyService service2 = new MyService();
        final MyService service3 = new MyService();

        final MyService service4 = new MyService();
        final MyService service5 = new MyService();
        final MyService service6 = new MyService();

        TestKeyWithScope beep = new TestKeyWithScope("beep") {
            @Override
            public void bindServices(ScopedServices.ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());

                serviceBinder.add("SERVICE1", service1);
                serviceBinder.add("SERVICE2", service2);
                serviceBinder.add("SERVICE3", service3);
            }

            @NonNull
            @Override
            public String getScopeTag() {
                return "beep";
            }
        };


        TestKeyWithScope boop = new TestKeyWithScope("beep") {
            @Override
            public void bindServices(ScopedServices.ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());

                serviceBinder.add("SERVICE4", service4);
                serviceBinder.add("SERVICE5", service5);
                serviceBinder.add("SERVICE6", service6);
            }

            @NonNull
            @Override
            public String getScopeTag() {
                return "boop";
            }
        };

        backstackManager.setup(History.of(beep));

        assertThat(activatedServices).isEmpty();
        backstackManager.setStateChanger(pendingStateChanger);
        callback.get().stateChangeComplete();

        assertThat(activatedServices).containsExactly(service1, service2, service3);

        backstackManager.getBackstack().setHistory(History.of(boop), StateChange.BACKWARD);
        backstackManager.getBackstack().removeStateChanger();
        backstackManager.getBackstack().executePendingStateChange();

        backstackManager.setStateChanger(pendingStateChanger);
        callback.get().stateChangeComplete();

        assertThat(activatedServices).containsExactly(service1, service2, service3, service4, service5, service6);
        assertThat(deactivatedServices).containsExactly(service3, service2, service1);
    }

    @Test
    public void activeScopeIsDeactivatedWhenScopesAreFinalized() {
        Activity activity = Mockito.mock(Activity.class);
        Mockito.when(activity.isFinishing()).thenReturn(true);

        class MyService
                implements ScopedServices.Activated {
            boolean didServiceActivate = false;
            boolean didScopeDeactivate = false;

            @Override
            public void onServiceActive() {
                didServiceActivate = true;
            }

            @Override
            public void onServiceInactive() {
                didScopeDeactivate = true;
            }
        }

        final MyService service = new MyService();
        TestKeyWithScope testKeyWithScope = new TestKeyWithScope("blah") {
            @Override
            public void bindServices(ScopedServices.ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());
                serviceBinder.add(SERVICE_TAG, service);
            }

            @NonNull
            @Override
            public String getScopeTag() {
                return "beep";
            }
        };

        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        };
        BackstackDelegate backstackDelegate = new BackstackDelegate();
        backstackDelegate.setScopedServices(activity, new ServiceProvider());
        backstackDelegate.onCreate(null, null, History.of(testKeyWithScope));
        backstackDelegate.setStateChanger(stateChanger);

        assertThat(backstackDelegate.canFindService(SERVICE_TAG)).isTrue();

        backstackDelegate.onPostResume();
        backstackDelegate.onPause();

        assertThat(service.didServiceActivate).isTrue();
        assertThat(service.didScopeDeactivate).isFalse();

        assertThat(backstackDelegate.hasScope("beep")).isTrue();
        assertThat(backstackDelegate.hasService(testKeyWithScope, SERVICE_TAG)).isTrue();
        backstackDelegate.onDestroy();
        assertThat(backstackDelegate.hasScope("beep")).isFalse();
        assertThat(backstackDelegate.hasService(testKeyWithScope, SERVICE_TAG)).isFalse();
        assertThat(service.didServiceActivate).isTrue();
        assertThat(service.didScopeDeactivate).isTrue();
    }

    private enum ServiceEvent {
        CREATE,
        ACTIVE,
        INACTIVE,
        DESTROY
    }

    private static class Pair<S, T> {
        private S first;
        private T second;

        private Pair(S first, T second) {
            this.first = first;
            this.second = second;
        }

        public static <S, T> Pair<S, T> of(S first, T second) {
            return new Pair<>(first, second);
        }

        @Override
        public boolean equals(Object o) {
            if(this == o) {
                return true;
            }
            if(o == null || getClass() != o.getClass()) {
                return false;
            }
            Pair<?, ?> pair = (Pair<?, ?>) o;
            return Objects.equals(first, pair.first) &&
                    Objects.equals(second, pair.second);
        }

        @Override
        public int hashCode() {
            return Objects.hash(first, second);
        }

        @Override
        public String toString() {
            return "Pair{" +
                    "first=" + first +
                    ", second=" + second +
                    '}';
        }
    }

    @Test
    public void registeredAndActivatedAreCalledInRightOrder() {
        final List<Pair<Object, ServiceEvent>> events = new ArrayList<>();

        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setScopedServices(new ServiceProvider());

        class MyService
                implements ScopedServices.Activated, ScopedServices.Registered {
            private int id = 0;

            MyService(int id) {
                this.id = id;
            }

            @Override
            public void onServiceActive() {
                events.add(Pair.of((Object) this, ServiceEvent.ACTIVE));
            }

            @Override
            public void onServiceInactive() {
                events.add(Pair.of((Object) this, ServiceEvent.INACTIVE));
            }

            @Override
            public void onServiceRegistered() {
                events.add(Pair.of((Object) this, ServiceEvent.CREATE));
            }

            @Override
            public void onServiceUnregistered() {
                events.add(Pair.of((Object) this, ServiceEvent.DESTROY));
            }

            @Override
            public String toString() {
                return "MyService{" +
                        "id=" + id +
                        '}';
            }
        }

        final Object service1 = new MyService(1);
        final Object service2 = new MyService(2);
        final Object service3 = new MyService(3);

        final Object service4 = new MyService(4);
        final Object service5 = new MyService(5);
        final Object service6 = new MyService(6);

        final Object service7 = new MyService(7);
        final Object service8 = new MyService(8);
        final Object service9 = new MyService(9);

        TestKeyWithScope beep = new TestKeyWithScope("beep") {
            @Override
            public void bindServices(ScopedServices.ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());

                serviceBinder.add("SERVICE1", service1);
                serviceBinder.add("SERVICE2", service2);
                serviceBinder.add("SERVICE3", service3);
            }

            @NonNull
            @Override
            public String getScopeTag() {
                return "beep";
            }
        };

        TestKeyWithScope boop = new TestKeyWithScope("boop") {
            @Override
            public void bindServices(ScopedServices.ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());

                serviceBinder.add("SERVICE4", service4);
                serviceBinder.add("SERVICE5", service5);
                serviceBinder.add("SERVICE6", service6);
            }

            @NonNull
            @Override
            public String getScopeTag() {
                return "boop";
            }
        };

        TestKeyWithScope braap = new TestKeyWithScope("braap") {
            @Override
            public void bindServices(ScopedServices.ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());

                serviceBinder.add("SERVICE7", service7);
                serviceBinder.add("SERVICE8", service8);
                serviceBinder.add("SERVICE9", service9);
            }

            @NonNull
            @Override
            public String getScopeTag() {
                return "braap";
            }
        };

        backstackManager.setup(History.of(beep, boop));

        backstackManager.setStateChanger(stateChanger);

        backstackManager.getBackstack().goTo(braap);

        backstackManager.getBackstack().removeStateChanger(); // just to make sure
        backstackManager.setStateChanger(stateChanger); // just to make sure

        backstackManager.getBackstack().goBack();

        TestKey bye = new TestKey("bye");
        backstackManager.getBackstack().setHistory(History.of(bye), StateChange.REPLACE);

        assertThat(events).containsExactly(
                Pair.of(service1, ServiceEvent.CREATE),
                Pair.of(service2, ServiceEvent.CREATE),
                Pair.of(service3, ServiceEvent.CREATE),
                Pair.of(service4, ServiceEvent.CREATE),
                Pair.of(service5, ServiceEvent.CREATE),
                Pair.of(service6, ServiceEvent.CREATE),
                Pair.of(service4, ServiceEvent.ACTIVE),
                Pair.of(service5, ServiceEvent.ACTIVE),
                Pair.of(service6, ServiceEvent.ACTIVE),
                Pair.of(service7, ServiceEvent.CREATE),
                Pair.of(service8, ServiceEvent.CREATE),
                Pair.of(service9, ServiceEvent.CREATE),
                Pair.of(service7, ServiceEvent.ACTIVE),
                Pair.of(service8, ServiceEvent.ACTIVE),
                Pair.of(service9, ServiceEvent.ACTIVE),
                Pair.of(service6, ServiceEvent.INACTIVE),
                Pair.of(service5, ServiceEvent.INACTIVE),
                Pair.of(service4, ServiceEvent.INACTIVE),
                Pair.of(service4, ServiceEvent.ACTIVE),
                Pair.of(service5, ServiceEvent.ACTIVE),
                Pair.of(service6, ServiceEvent.ACTIVE),
                Pair.of(service9, ServiceEvent.INACTIVE),
                Pair.of(service8, ServiceEvent.INACTIVE),
                Pair.of(service7, ServiceEvent.INACTIVE),
                Pair.of(service9, ServiceEvent.DESTROY),
                Pair.of(service8, ServiceEvent.DESTROY),
                Pair.of(service7, ServiceEvent.DESTROY),
                Pair.of(service6, ServiceEvent.INACTIVE),
                Pair.of(service5, ServiceEvent.INACTIVE),
                Pair.of(service4, ServiceEvent.INACTIVE),
                Pair.of(service6, ServiceEvent.DESTROY),
                Pair.of(service5, ServiceEvent.DESTROY),
                Pair.of(service4, ServiceEvent.DESTROY),
                Pair.of(service3, ServiceEvent.DESTROY),
                Pair.of(service2, ServiceEvent.DESTROY),
                Pair.of(service1, ServiceEvent.DESTROY)
        );
    }

    @Test
    public void lookupServiceFromScopeWorks() {
        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setScopedServices(new ServiceProvider());

        final Object service1 = new Object();
        final Object service2 = new Object();
        final Object service0 = new Object();
        final Object service3 = new Object();

        class Key1
                extends TestKey
                implements HasServices {
            Key1(String name) {
                super(name);
            }

            protected Key1(Parcel in) {
                super(in);
            }

            @Override
            public void bindServices(ScopedServices.ServiceBinder serviceBinder) {
                assertThat(serviceBinder.has("service0")).isFalse();
                assertThat(serviceBinder.canFindFrom(serviceBinder.getScopeTag(), "service0")).isFalse();
                serviceBinder.add("service0", service0);
                assertThat(serviceBinder.has("service0")).isTrue();
                assertThat(serviceBinder.canFindFrom(serviceBinder.getScopeTag(), "service0")).isTrue();
                assertThat(serviceBinder.get("service0")).isSameAs(service0);
                assertThat(serviceBinder.lookup("service0")).isSameAs(service0);
                assertThat(serviceBinder.lookupFrom(serviceBinder.getScopeTag(), "service0")).isSameAs(service0);
                assertThat(serviceBinder.canFindFrom(serviceBinder.getScopeTag(), "service")).isFalse();
                serviceBinder.add("service", service1);
                assertThat(serviceBinder.canFindFrom(serviceBinder.getScopeTag(), "service")).isTrue();
                assertThat(serviceBinder.lookupFrom(serviceBinder.getScopeTag(), "service")).isSameAs(service1);
            }

            @NonNull
            @Override
            public String getScopeTag() {
                return "beep";
            }
        }

        class Key2
                extends TestKey
                implements HasServices {
            Key2(String name) {
                super(name);
            }

            protected Key2(Parcel in) {
                super(in);
            }

            @Override
            public void bindServices(ScopedServices.ServiceBinder serviceBinder) {
                assertThat(serviceBinder.lookup("service0")).isSameAs(service0);
                assertThat(serviceBinder.canFindFrom(serviceBinder.getScopeTag(), "service")).isTrue();
                assertThat(serviceBinder.lookup("service")).isSameAs(service1);
                serviceBinder.add("service", service2);
                // the most important assertion here
                assertThat(serviceBinder.lookup("service")).isSameAs(service2);
                assertThat(serviceBinder.lookupFrom("beep", "service")).isSameAs(service1);

                serviceBinder.add("service3", service3);
            }

            @NonNull
            @Override
            public String getScopeTag() {
                return "boop";
            }
        }

        backstackManager.setup(History.of(new Key1("beep"), new Key2("boop")));
        backstackManager.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });

        assertThat(backstackManager.lookupService("service")).isSameAs(service2);
        assertThat(backstackManager.canFindService("service3")).isTrue();
        assertThat(backstackManager.canFindFromScope("boop", "service3")).isTrue();
        assertThat(backstackManager.lookupFromScope("boop", "service3")).isSameAs(service3);
        assertThat(backstackManager.lookupFromScope("beep", "service")).isSameAs(service1);
        assertThat(backstackManager.lookupFromScope("boop", "service")).isSameAs(service2);

        backstackManager.getBackstack().goBack();

        assertThat(backstackManager.canFindFromScope("boop", "service3")).isFalse();
        assertThat(backstackManager.lookupService("service")).isSameAs(service1);
    }

    @Test
    public void sameServiceRegisteredInScopeMultipleTimesReceivesCallbackOnlyOnce() {
        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setScopedServices(new ServiceProvider());

        final List<Object> activated = new ArrayList<>();
        final List<Object> inactivated = new ArrayList<>();
        final List<Object> registered = new ArrayList<>();
        final List<Object> unregistered = new ArrayList<>();

        class MyService
                implements ScopedServices.Activated, ScopedServices.Registered {

            @Override
            public void onServiceActive() {
                activated.add(this);
            }

            @Override
            public void onServiceInactive() {
                inactivated.add(this);
            }

            @Override
            public void onServiceRegistered() {
                registered.add(this);
            }

            @Override
            public void onServiceUnregistered() {
                unregistered.add(this);
            }
        }

        final MyService service = new MyService();

        final String serviceTag1 = "service1";
        final String serviceTag2 = "service2";

        TestKeyWithScope beep = new TestKeyWithScope("beep") {
            @Override
            public void bindServices(ScopedServices.ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());
                serviceBinder.add(serviceTag1, service);
                serviceBinder.add(serviceTag2, service);
            }

            @NonNull
            @Override
            public String getScopeTag() {
                return "beep";
            }
        };

        TestKey clear = new TestKey("clear");

        backstackManager.setup(History.of(beep));

        assertThat(activated).isEmpty();
        assertThat(inactivated).isEmpty();
        assertThat(registered).isEmpty();
        assertThat(unregistered).isEmpty();
        backstackManager.setStateChanger(stateChanger);

        assertThat(activated).isNotEmpty();
        assertThat(inactivated).isEmpty();
        assertThat(registered).isNotEmpty();
        assertThat(unregistered).isEmpty();

        assertThat(activated).containsOnlyOnce(service);
        assertThat(registered).containsOnlyOnce(service);

        backstackManager.getBackstack().setHistory(History.of(clear), StateChange.REPLACE);

        assertThat(activated).isNotEmpty();
        assertThat(inactivated).isNotEmpty();
        assertThat(registered).isNotEmpty();
        assertThat(unregistered).isNotEmpty();

        assertThat(inactivated).containsOnlyOnce(service);
        assertThat(unregistered).containsOnlyOnce(service);
    }

    @Test
    public void finalizingScopeTwiceShouldBeNoOp() {
        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setScopedServices(new ServiceProvider());
        backstackManager.setup(History.of(testKey2));
        backstackManager.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });
        assertThat(backstackManager.hasService(testKey2, SERVICE_TAG)).isTrue();

        backstackManager.finalizeScopes();

        try {
            backstackManager.finalizeScopes();
        } catch(Throwable e) {
            Assert.fail("Should be no-op.");
        }
    }
}
