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

import com.zhuinden.simplestack.helpers.HasServices;
import com.zhuinden.simplestack.helpers.ServiceProvider;
import com.zhuinden.simplestack.helpers.TestKey;
import com.zhuinden.simplestack.helpers.TestKeyWithOnlyParentServices;
import com.zhuinden.statebundle.StateBundle;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

        @Nonnull
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
        public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
            completionCallback.stateChangeComplete();
        }
    };

    private static class TestKeyWithScope
            extends TestKey
            implements ScopeKey, HasServices {
        TestKeyWithScope(String name) {
            super(name);
        }

        protected TestKeyWithScope(Parcel in) {
            super(in);
        }

        @Nonnull
        @Override
        public String getScopeTag() {
            return name;
        }

        @Override
        public void bindServices(ServiceBinder serviceBinder) {
            serviceBinder.addService(SERVICE_TAG, services.get(SERVICE_TAG));
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
        Backstack backstack = new Backstack();
        try {
            backstack.setScopedServices(null);
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // OK!
        }
    }

    @Test
    public void scopedServicesShouldBeSetBeforeSetup() {
        Backstack backstack = new Backstack();
        backstack.setup(History.of(testKey1));
        try {
            backstack.setScopedServices(new ServiceProvider());
            Assert.fail();
        } catch(IllegalStateException e) {
            // OK!
        }
    }

    @Test
    public void scopedServicesThrowIfNoScopedServicesAreDefinedAndServicesAreToBeBound() {
        Backstack backstack = new Backstack();
        backstack.setup(History.of(testKey2));

        try {
            backstack.setStateChanger(stateChanger);
            Assert.fail();
        } catch(IllegalStateException e) {
            assertThat(e.getMessage()).contains("scoped services");
        }
    }

    @Test
    public void scopeIsCreatedForScopeKeys() {
        Backstack backstack = new Backstack();
        backstack.setScopedServices(new ServiceProvider());
        backstack.setup(History.single(testKey2));
        assertThat(backstack.hasService(testKey2, SERVICE_TAG)).isFalse();
        backstack.setStateChanger(stateChanger);
        assertThat(backstack.hasService(testKey2, SERVICE_TAG)).isTrue();

        Service service = backstack.getService(testKey2, SERVICE_TAG);
        assertThat(service).isSameAs(services.get(SERVICE_TAG));
    }

    @Test
    public void gettingNonExistentServiceThrows() {
        Backstack backstack = new Backstack();
        backstack.setScopedServices(new ServiceProvider());
        backstack.setup(History.single(testKey2));
        assertThat(backstack.hasService(testKey2, SERVICE_TAG)).isFalse();
        backstack.setStateChanger(stateChanger);
        assertThat(backstack.hasService(testKey2, SERVICE_TAG)).isTrue();

        try {
            backstack.getService(testKey2, "d'oh");
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
            public void bindServices(ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());
                serviceBinder.addService(nullTag, service);
            }

            @Nonnull
            @Override
            public String getScopeTag() {
                return "beep";
            }
        };

        Backstack backstack = new Backstack();
        backstack.setScopedServices(new ServiceProvider());
        backstack.setup(History.of(testKeyWithScope));
        try {
            backstack.setStateChanger(stateChanger);
            Assert.fail();
        } catch(Exception e) {
            assertThat(e.getMessage()).isEqualTo("serviceTag cannot be null!");
        }
    }

    @Test
    public void serviceBinderThrowsIfRootBackstackIsAService() {
        final String serviceTag = "backstack";

        final Backstack backstack = new Backstack();

        TestKeyWithScope testKeyWithScope = new TestKeyWithScope("blah") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());
                serviceBinder.addService(serviceTag, backstack);
            }

            @Nonnull
            @Override
            public String getScopeTag() {
                return "beep";
            }
        };


        backstack.setScopedServices(new ServiceProvider());
        backstack.setup(History.of(testKeyWithScope));
        try {
            backstack.setStateChanger(stateChanger);
            Assert.fail("This would cause a save-state loop in toBundle()");
        } catch(IllegalArgumentException e) {
            // OK!
        }
    }

    @Test
    public void serviceBinderSucceedsIfRootBackstackIsAnAlias() {
        final String serviceTag = "backstack";

        final Backstack backstack = new Backstack();

        TestKeyWithScope testKeyWithScope = new TestKeyWithScope("blah") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());
                serviceBinder.addAlias(serviceTag, backstack);
            }

            @Nonnull
            @Override
            public String getScopeTag() {
                return "beep";
            }
        };


        backstack.setScopedServices(new ServiceProvider());
        backstack.setup(History.of(testKeyWithScope));
        backstack.setStateChanger(stateChanger);

        assertThat(backstack.lookupService(serviceTag)).isSameAs(backstack);
    }

    @Test
    public void serviceBinderAddThrowsForNullService() {
        final String serviceTag = "serviceTag";
        final Object nullService = null;
        TestKeyWithScope testKeyWithScope = new TestKeyWithScope("blah") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());
                serviceBinder.addService(serviceTag, null);
            }

            @Nonnull
            @Override
            public String getScopeTag() {
                return "beep";
            }
        };

        Backstack backstack = new Backstack();
        backstack.setScopedServices(new ServiceProvider());
        backstack.setup(History.of(testKeyWithScope));
        try {
            backstack.setStateChanger(stateChanger);
            Assert.fail();
        } catch(Exception e) {
            assertThat(e.getMessage()).isEqualTo("service cannot be null!");
        }
    }

    @Test
    public void scopeIsDestroyedForClearedScopeKeys() {
        Backstack backstack = new Backstack();
        backstack.setScopedServices(new ServiceProvider());
        backstack.setup(History.single(testKey2));

        assertThat(backstack.hasService(testKey2, SERVICE_TAG)).isFalse();
        backstack.setStateChanger(stateChanger);
        assertThat(backstack.hasService(testKey2, SERVICE_TAG)).isTrue();

        backstack.setHistory(History.of(testKey1), StateChange.REPLACE);

        assertThat(backstack.hasService(testKey2, SERVICE_TAG)).isFalse();
    }

    @Test
    public void scopeServicesArePersistedToStateBundle() {
        final Backstack backstack = new Backstack();

        backstack.setScopedServices(new ServiceProvider());
        final Service service = new Service();
        TestKeyWithScope testKeyWithScope = new TestKeyWithScope("blah") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());
                serviceBinder.addService(SERVICE_TAG, service);
            }

            @Nonnull
            @Override
            public String getScopeTag() {
                return "beep";
            }
        };

        backstack.setup(History.of(testKeyWithScope));
        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });

        assertThat(backstack.hasService(testKeyWithScope.getScopeTag(), SERVICE_TAG)).isTrue();

        StateBundle stateBundle = backstack.toBundle();

        //noinspection ConstantConditions
        assertThat(stateBundle.getBundle(Backstack.getScopesTag()).getBundle(testKeyWithScope.getScopeTag()).getBundle(SERVICE_TAG).getInt("blah"))
                .isEqualTo(5); // backstack.getScopesTag() is internal
    }

    @Test
    public void persistedStateOfScopedServicesIsRestored() {
        Backstack backstack = new Backstack();
        backstack.setScopedServices(new ServiceProvider());
        backstack.setup(History.of(testKey2));

        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        };

        backstack.setStateChanger(stateChanger);

        assertThat(backstack.hasService(testKey2.getScopeTag(), SERVICE_TAG)).isTrue();

        StateBundle stateBundle = backstack.toBundle();

        Backstack backstack2 = new Backstack();
        backstack2.setScopedServices(new ServiceProvider());

        StateChanger stateChanger2 = new StateChanger() {
            @Override
            public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        };

        backstack2.setup(History.of(testKey2));
        backstack2.fromBundle(stateBundle);
        backstack2.setStateChanger(stateChanger2);
        assertThat(backstack2.<Service>getService(testKey2.getScopeTag(), SERVICE_TAG).blah).isEqualTo(5);
    }

    @Test
    public void nonExistentServiceShouldReturnFalseAndThrow() {
        Backstack backstack = new Backstack();
        backstack.setScopedServices(new ServiceProvider());
        backstack.setup(History.of(testKey2));

        assertThat(backstack.hasService(testKey2, SERVICE_TAG)).isFalse();
        try {
            backstack.getService(testKey2, SERVICE_TAG);
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // OK!
        }
    }

    @Test
    public void scopedServiceCallbackIsCalledCorrectly() {
        Backstack backstack = new Backstack();
        backstack.setScopedServices(new ServiceProvider());
        final Service service = new Service();
        TestKeyWithScope testKeyWithScope = new TestKeyWithScope("blah") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());
                serviceBinder.addService(SERVICE_TAG, service);
            }

            @Nonnull
            @Override
            public String getScopeTag() {
                return "beep";
            }
        };

        backstack.setup(History.of(testKeyWithScope));

        assertThat(service.didServiceRegister).isFalse();
        assertThat(service.didServiceUnregister).isFalse();

        backstack.setStateChanger(stateChanger);

        assertThat(service.didServiceRegister).isTrue();
        assertThat(service.didServiceUnregister).isFalse();

        backstack.setHistory(History.single(testKey1), StateChange.REPLACE);

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
            public void bindServices(ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());
                serviceBinder.addService(SERVICE_TAG, service);
            }

            @Nonnull
            @Override
            public String getScopeTag() {
                return "beep";
            }
        };

        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
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
        Backstack backstack = new Backstack();
        backstack.setScopedServices(new ServiceProvider());
        final Service service = new Service();
        TestKeyWithScope beep = new TestKeyWithScope("beep") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());
                serviceBinder.addService(SERVICE_TAG, service);
            }

            @Nonnull
            @Override
            public String getScopeTag() {
                return "beep";
            }
        };

        TestKeyWithScope boop = new TestKeyWithScope("boop") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
            }

            @Nonnull
            @Override
            public String getScopeTag() {
                return "boop";
            }
        };

        backstack.setup(History.of(beep, boop));

        assertThat(backstack.hasService("beep", SERVICE_TAG)).isFalse();
        backstack.setStateChanger(stateChanger);
        assertThat(backstack.hasService("beep", SERVICE_TAG)).isTrue();
        assertThat(backstack.hasService("boop", SERVICE_TAG)).isFalse();
        assertThat(backstack.<Object>lookupService(SERVICE_TAG)).isSameAs(service);
        backstack.goBack();
        assertThat(backstack.hasService("beep", SERVICE_TAG)).isTrue();
        assertThat(backstack.<Object>lookupService(SERVICE_TAG)).isSameAs(service);
        backstack.setHistory(History.single(testKey1), StateChange.REPLACE);
        assertThat(backstack.hasService("beep", SERVICE_TAG)).isFalse();
        try {
            backstack.lookupService(SERVICE_TAG);
            Assert.fail();
        } catch(IllegalStateException e) {
            assertThat(e.getMessage()).contains("does not exist in any scope");
            // OK!
        }
    }

    @Test
    public void lookupServiceWithOverlapsWorks() {
        Backstack backstack = new Backstack();
        backstack.setScopedServices(new ServiceProvider());
        final Service service1 = new Service();
        final Service service2 = new Service();
        TestKeyWithScope beep = new TestKeyWithScope("beep") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());
                serviceBinder.addService(SERVICE_TAG, service1);
            }

            @Nonnull
            @Override
            public String getScopeTag() {
                return "beep";
            }
        };

        TestKeyWithScope boop = new TestKeyWithScope("boop") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());
                serviceBinder.addService(SERVICE_TAG, service2);
            }

            @Nonnull
            @Override
            public String getScopeTag() {
                return "boop";
            }
        };

        backstack.setup(History.of(beep, boop));

        assertThat(backstack.hasScope("beep")).isFalse();
        assertThat(backstack.hasScope("boop")).isFalse();
        assertThat(backstack.hasService("beep", SERVICE_TAG)).isFalse();
        assertThat(backstack.hasService("boop", SERVICE_TAG)).isFalse();
        backstack.setStateChanger(stateChanger);
        assertThat(backstack.hasScope("beep")).isTrue();
        assertThat(backstack.hasScope("boop")).isTrue();
        assertThat(backstack.hasService("beep", SERVICE_TAG)).isTrue();
        assertThat(backstack.hasService("boop", SERVICE_TAG)).isTrue();
        assertThat(backstack.<Object>lookupService(SERVICE_TAG)).isSameAs(service2);
        backstack.goBack();
        assertThat(backstack.hasScope("beep")).isTrue();
        assertThat(backstack.hasScope("boop")).isFalse();
        assertThat(backstack.hasService("beep", SERVICE_TAG)).isTrue();
        assertThat(backstack.hasService("boop", SERVICE_TAG)).isFalse();
        assertThat(backstack.<Object>lookupService(SERVICE_TAG)).isSameAs(service1);
        backstack.setHistory(History.single(testKey1), StateChange.REPLACE);
        assertThat(backstack.hasScope("beep")).isFalse();
        assertThat(backstack.hasScope("boop")).isFalse();
        assertThat(backstack.hasService("beep", SERVICE_TAG)).isFalse();
        assertThat(backstack.hasService("boop", SERVICE_TAG)).isFalse();
        try {
            backstack.lookupService(SERVICE_TAG);
            Assert.fail();
        } catch(IllegalStateException e) {
            assertThat(e.getMessage()).contains("does not exist in any scope");
            // OK!
        }
    }

    @Test
    public void canFindServiceNoOverlapsWorks() {
        Backstack backstack = new Backstack();
        backstack.setScopedServices(new ServiceProvider());
        final Service service = new Service();
        TestKeyWithScope beep = new TestKeyWithScope("beep") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());
                serviceBinder.addService(SERVICE_TAG, service);
            }

            @Nonnull
            @Override
            public String getScopeTag() {
                return "beep";
            }
        };

        TestKeyWithScope boop = new TestKeyWithScope("boop") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
            }

            @Nonnull
            @Override
            public String getScopeTag() {
                return "boop";
            }
        };

        backstack.setup(History.of(beep, boop));

        assertThat(backstack.hasService("beep", SERVICE_TAG)).isFalse();
        backstack.setStateChanger(stateChanger);
        assertThat(backstack.hasService("beep", SERVICE_TAG)).isTrue();
        assertThat(backstack.hasService("boop", SERVICE_TAG)).isFalse();
        assertThat(backstack.<Object>canFindService(SERVICE_TAG)).isTrue();
        backstack.goBack();
        assertThat(backstack.hasService("beep", SERVICE_TAG)).isTrue();
        assertThat(backstack.<Object>canFindService(SERVICE_TAG)).isTrue();
        backstack.setHistory(History.single(testKey1), StateChange.REPLACE);
        assertThat(backstack.hasService("beep", SERVICE_TAG)).isFalse();
        assertThat(backstack.canFindService(SERVICE_TAG)).isFalse();
    }

    @Test
    public void canFindServiceWithOverlapsWorks() {
        Backstack backstack = new Backstack();
        backstack.setScopedServices(new ServiceProvider());
        final Service service1 = new Service();
        final Service service2 = new Service();
        TestKeyWithScope beep = new TestKeyWithScope("beep") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());
                serviceBinder.addService(SERVICE_TAG, service1);
            }

            @Nonnull
            @Override
            public String getScopeTag() {
                return "beep";
            }
        };

        TestKeyWithScope boop = new TestKeyWithScope("boop") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());
                serviceBinder.addService(SERVICE_TAG, service2);
            }

            @Nonnull
            @Override
            public String getScopeTag() {
                return "boop";
            }
        };

        backstack.setup(History.of(beep, boop));

        assertThat(backstack.hasService("beep", SERVICE_TAG)).isFalse();
        assertThat(backstack.hasService("boop", SERVICE_TAG)).isFalse();
        backstack.setStateChanger(stateChanger);
        assertThat(backstack.hasService("beep", SERVICE_TAG)).isTrue();
        assertThat(backstack.hasService("boop", SERVICE_TAG)).isTrue();
        assertThat(backstack.<Object>canFindService(SERVICE_TAG)).isTrue();
        backstack.goBack();
        assertThat(backstack.hasService("beep", SERVICE_TAG)).isTrue();
        assertThat(backstack.hasService("boop", SERVICE_TAG)).isFalse();
        assertThat(backstack.<Object>canFindService(SERVICE_TAG)).isTrue();
        backstack.setHistory(History.single(testKey1), StateChange.REPLACE);
        assertThat(backstack.hasService("beep", SERVICE_TAG)).isFalse();
        assertThat(backstack.hasService("boop", SERVICE_TAG)).isFalse();
        assertThat(backstack.canFindService(SERVICE_TAG)).isFalse();
    }

    @Test
    public void serviceBinderMethodsWork() {
        Backstack backstack = new Backstack();
        backstack.setScopedServices(new ServiceProvider());
        final Service service1 = new Service();
        final Service service2 = new Service();
        TestKeyWithScope beep = new TestKeyWithScope("beep") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());

                assertThat(serviceBinder.hasService("SERVICE1")).isFalse();
                assertThat(serviceBinder.canFindService("SERVICE1")).isFalse();
                serviceBinder.addService("SERVICE1", service1);
                assertThat(serviceBinder.hasService("SERVICE1")).isTrue();
                assertThat(serviceBinder.canFindService("SERVICE1")).isTrue();

                assertThat(serviceBinder.lookupService("SERVICE1")).isSameAs(service1);
            }

            @Nonnull
            @Override
            public String getScopeTag() {
                return "beep";
            }
        };

        TestKeyWithScope boop = new TestKeyWithScope("boop") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());

                assertThat(serviceBinder.hasService("SERVICE1")).isFalse();
                assertThat(serviceBinder.canFindService("SERVICE1")).isTrue();
                assertThat(serviceBinder.hasService("SERVICE2")).isFalse();
                serviceBinder.addService("SERVICE2", service2);
                assertThat(serviceBinder.hasService("SERVICE2")).isTrue();
                assertThat(serviceBinder.canFindService("SERVICE2")).isTrue();
                assertThat(serviceBinder.hasService("SERVICE2")).isTrue();

                assertThat(serviceBinder.lookupService("SERVICE1")).isSameAs(service1);
                assertThat(serviceBinder.lookupService("SERVICE2")).isSameAs(service2);
            }

            @Nonnull
            @Override
            public String getScopeTag() {
                return "boop";
            }
        };

        backstack.setup(History.of(beep, boop));

        backstack.setStateChanger(stateChanger);
    }

    @Test
    public void scopeCreationAndDestructionHappensInForwardAndReverseOrder() {
        Backstack backstack = new Backstack();
        backstack.setScopedServices(new ServiceProvider());

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
            public void bindServices(ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());

                serviceBinder.addService("SERVICE1", service1);
            }

            @Nonnull
            @Override
            public String getScopeTag() {
                return "beep";
            }
        };

        TestKeyWithScope boop = new TestKeyWithScope("boop") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());

                serviceBinder.addService("SERVICE2", service2);
            }

            @Nonnull
            @Override
            public String getScopeTag() {
                return "boop";
            }
        };

        TestKey bye = new TestKey("bye");

        backstack.setup(History.of(beep, boop));

        assertThat(serviceRegistered).isEmpty();
        assertThat(serviceUnregistered).isEmpty();
        backstack.setStateChanger(stateChanger);

        assertThat(serviceRegistered).containsExactly(service1, service2);
        assertThat(serviceUnregistered).isEmpty();

        backstack.setHistory(History.of(bye), StateChange.REPLACE);

        assertThat(serviceRegistered).containsExactly(service1, service2);
        assertThat(serviceUnregistered).containsExactly(service2, service1);
    }

    @Test
    public void serviceCreationAndDestructionHappensInForwardAndReverseOrder() {
        Backstack backstack = new Backstack();
        backstack.setScopedServices(new ServiceProvider());

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
            public void bindServices(ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());

                serviceBinder.addService("SERVICE1", service1);
                serviceBinder.addService("SERVICE2", service2);
            }

            @Nonnull
            @Override
            public String getScopeTag() {
                return "beep";
            }
        };

        TestKey bye = new TestKey("bye");

        backstack.setup(History.of(beep));

        assertThat(serviceRegistered).isEmpty();
        assertThat(serviceUnregistered).isEmpty();
        backstack.setStateChanger(stateChanger);

        assertThat(serviceRegistered).containsExactly(service1, service2);
        assertThat(serviceUnregistered).isEmpty();

        backstack.setHistory(History.of(bye), StateChange.REPLACE);

        assertThat(serviceRegistered).containsExactly(service1, service2);
        assertThat(serviceUnregistered).containsExactly(service2, service1);
    }

    @Test
    public void scopedServicesCanRetrieveBackstackFromServiceBinder() {
        final Backstack backstack = new Backstack();
        backstack.setScopedServices(new ServiceProvider());

        final AtomicReference<Backstack> ref = new AtomicReference<>();

        class MyService {
        }

        final MyService service1 = new MyService();

        TestKeyWithScope beep = new TestKeyWithScope("beep") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());

                serviceBinder.addService("SERVICE1", service1);
                ref.set(serviceBinder.getBackstack());
            }

            @Nonnull
            @Override
            public String getScopeTag() {
                return "beep";
            }
        };

        backstack.setup(History.of(beep));
        backstack.setStateChanger(stateChanger);
        assertThat(backstack).isSameAs(ref.get());
    }

    @Test
    public void activatedWorks() {
        Backstack backstack = new Backstack();
        backstack.setScopedServices(new ServiceProvider());

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
            public void bindServices(ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());

                serviceBinder.addService("SERVICE1", service1);
            }

            @Nonnull
            @Override
            public String getScopeTag() {
                return "beep";
            }
        };

        TestKeyWithScope boop = new TestKeyWithScope("boop") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());

                serviceBinder.addService("SERVICE2", service2);
            }

            @Nonnull
            @Override
            public String getScopeTag() {
                return "boop";
            }
        };

        TestKey bye = new TestKey("bye");

        backstack.setup(History.of(beep, boop));

        assertThat(activatedServices).isEmpty();
        assertThat(deactivatedServices).isEmpty();
        backstack.setStateChanger(stateChanger);

        assertThat(activatedServices).containsExactly(service2);
        assertThat(deactivatedServices).isEmpty();

        backstack.goBack();

        assertThat(activatedServices).containsExactly(service2, service1);
        assertThat(deactivatedServices).containsExactly(service2);

        backstack.setHistory(History.of(bye), StateChange.REPLACE);

        assertThat(activatedServices).containsExactly(service2, service1);
        assertThat(deactivatedServices).containsExactly(service2, service1);
    }

    @Test
    public void activatedIsCalledInRightOrder() {
        Backstack backstack = new Backstack();
        backstack.setScopedServices(new ServiceProvider());

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
            public void bindServices(ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());

                serviceBinder.addService("SERVICE1", service1);
                serviceBinder.addService("SERVICE2", service2);
                serviceBinder.addService("SERVICE3", service3);
            }

            @Nonnull
            @Override
            public String getScopeTag() {
                return "beep";
            }
        };


        TestKeyWithScope boop = new TestKeyWithScope("beep") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());

                serviceBinder.addService("SERVICE4", service4);
                serviceBinder.addService("SERVICE5", service5);
                serviceBinder.addService("SERVICE6", service6);
            }

            @Nonnull
            @Override
            public String getScopeTag() {
                return "boop";
            }
        };

        backstack.setup(History.of(beep));

        assertThat(activatedServices).isEmpty();
        backstack.setStateChanger(stateChanger);

        assertThat(activatedServices).containsExactly(service1, service2, service3);

        backstack.goTo(boop);
        assertThat(activatedServices).containsExactly(service1, service2, service3, service4, service5, service6);
        assertThat(deactivatedServices).containsExactly(service3, service2, service1);
    }

    @Test
    public void deactivatedIsCalledInReverseOrder() {
        Backstack backstack = new Backstack();
        backstack.setScopedServices(new ServiceProvider());

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
            public void bindServices(ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());

                serviceBinder.addService("SERVICE1", service1);
                serviceBinder.addService("SERVICE2", service2);
                serviceBinder.addService("SERVICE3", service3);
            }

            @Nonnull
            @Override
            public String getScopeTag() {
                return "beep";
            }
        };


        TestKeyWithScope boop = new TestKeyWithScope("beep") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());

                serviceBinder.addService("SERVICE4", service4);
                serviceBinder.addService("SERVICE5", service5);
                serviceBinder.addService("SERVICE6", service6);
            }

            @Nonnull
            @Override
            public String getScopeTag() {
                return "boop";
            }
        };

        backstack.setup(History.of(beep));

        assertThat(deactivatedServices).isEmpty();
        backstack.setStateChanger(stateChanger);

        backstack.goTo(boop);

        assertThat(deactivatedServices).containsExactly(service3, service2, service1);

        TestKey bye = new TestKey("bye");
        backstack.setHistory(History.of(bye), StateChange.REPLACE);

        assertThat(deactivatedServices).containsExactly(service3, service2, service1, service6, service5, service4);
    }

    @Test
    public void activationIsCalledOnlyOnce() {
        Backstack backstack = new Backstack();
        backstack.setScopedServices(new ServiceProvider());

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
            public void bindServices(ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());

                serviceBinder.addService("SERVICE1", service1);
            }

            @Nonnull
            @Override
            public String getScopeTag() {
                return "beep";
            }
        };

        backstack.setup(History.of(beep));

        assertThat(activatedServices).isEmpty();
        assertThat(deactivatedServices).isEmpty();
        backstack.setStateChanger(stateChanger);

        assertThat(activatedServices).containsExactly(service1);

        backstack.removeStateChanger();
        backstack.setStateChanger(stateChanger);

        assertThat(activatedServices).containsExactly(service1);
    }

    @Test
    public void deactivationIsCalledOnlyOnce() {
        Backstack backstack = new Backstack();
        backstack.setScopedServices(new ServiceProvider());

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
            public void bindServices(ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());

                serviceBinder.addService("SERVICE1", service1);
            }

            @Nonnull
            @Override
            public String getScopeTag() {
                return "beep";
            }
        };

        TestKey bye = new TestKey("bye");
        backstack.setup(History.of(beep));

        assertThat(activatedServices).isEmpty();
        assertThat(deactivatedServices).isEmpty();
        backstack.setStateChanger(stateChanger);

        assertThat(activatedServices).containsExactly(service1);
        assertThat(deactivatedServices).isEmpty();

        backstack.removeStateChanger();
        assertThat(deactivatedServices).isEmpty();

        backstack.setHistory(History.of(bye), StateChange.REPLACE);
        assertThat(deactivatedServices).isEmpty();

        backstack.setStateChanger(stateChanger);
        assertThat(deactivatedServices).containsExactly(service1);

        backstack.removeStateChanger();
        backstack.setStateChanger(stateChanger);
        assertThat(deactivatedServices).containsExactly(service1);
    }

    @Test
    public void activationHappensEvenWithForceExecutedStateChangeAndInitializeStateChange() {
        Backstack backstack = new Backstack();
        backstack.setScopedServices(new ServiceProvider());

        final List<Object> activatedServices = new ArrayList<>();
        final List<Object> deactivatedServices = new ArrayList<>();

        final AtomicReference<StateChanger.Callback> callback = new AtomicReference<>();
        StateChanger pendingStateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
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
            public void bindServices(ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());

                serviceBinder.addService("SERVICE1", service1);
                serviceBinder.addService("SERVICE2", service2);
                serviceBinder.addService("SERVICE3", service3);
            }

            @Nonnull
            @Override
            public String getScopeTag() {
                return "beep";
            }
        };


        TestKeyWithScope boop = new TestKeyWithScope("beep") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());

                serviceBinder.addService("SERVICE4", service4);
                serviceBinder.addService("SERVICE5", service5);
                serviceBinder.addService("SERVICE6", service6);
            }

            @Nonnull
            @Override
            public String getScopeTag() {
                return "boop";
            }
        };

        backstack.setup(History.of(beep));

        assertThat(activatedServices).isEmpty();
        backstack.setStateChanger(pendingStateChanger);
        callback.get().stateChangeComplete();

        assertThat(activatedServices).containsExactly(service1, service2, service3);

        backstack.setHistory(History.of(boop), StateChange.BACKWARD);
        backstack.removeStateChanger();
        backstack.executePendingStateChange();

        backstack.setStateChanger(pendingStateChanger);
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
            public void bindServices(ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());
                serviceBinder.addService(SERVICE_TAG, service);
            }

            @Nonnull
            @Override
            public String getScopeTag() {
                return "beep";
            }
        };

        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        };
        Backstack backstack = new Backstack();
        backstack.setScopedServices(new ServiceProvider());
        backstack.setup(History.of(testKeyWithScope));
        backstack.setStateChanger(stateChanger);

        assertThat(backstack.canFindService(SERVICE_TAG)).isTrue();

        backstack.reattachStateChanger();
        backstack.detachStateChanger();

        assertThat(service.didServiceActivate).isTrue();
        assertThat(service.didScopeDeactivate).isFalse();

        assertThat(backstack.hasScope("beep")).isTrue();
        assertThat(backstack.hasService(testKeyWithScope, SERVICE_TAG)).isTrue();
        backstack.finalizeScopes();
        assertThat(backstack.hasScope("beep")).isFalse();
        assertThat(backstack.hasService(testKeyWithScope, SERVICE_TAG)).isFalse();
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

        Backstack backstack = new Backstack();
        backstack.setScopedServices(new ServiceProvider());

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
            public void bindServices(ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());

                serviceBinder.addService("SERVICE1", service1);
                serviceBinder.addService("SERVICE2", service2);
                serviceBinder.addService("SERVICE3", service3);
            }

            @Nonnull
            @Override
            public String getScopeTag() {
                return "beep";
            }
        };

        TestKeyWithScope boop = new TestKeyWithScope("boop") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());

                serviceBinder.addService("SERVICE4", service4);
                serviceBinder.addService("SERVICE5", service5);
                serviceBinder.addService("SERVICE6", service6);
            }

            @Nonnull
            @Override
            public String getScopeTag() {
                return "boop";
            }
        };

        TestKeyWithScope braap = new TestKeyWithScope("braap") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());

                serviceBinder.addService("SERVICE7", service7);
                serviceBinder.addService("SERVICE8", service8);
                serviceBinder.addService("SERVICE9", service9);
            }

            @Nonnull
            @Override
            public String getScopeTag() {
                return "braap";
            }
        };

        backstack.setup(History.of(beep, boop));

        backstack.setStateChanger(stateChanger);

        backstack.goTo(braap);

        backstack.removeStateChanger(); // just to make sure
        backstack.setStateChanger(stateChanger); // just to make sure

        backstack.goBack();

        TestKey bye = new TestKey("bye");
        backstack.setHistory(History.of(bye), StateChange.REPLACE);

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
    public void navigationIsPossibleAndEnqueuedDuringActivationDispatch() {
        final TestKey destination = new TestKey("destination");

        Backstack backstack = new Backstack();
        backstack.setScopedServices(new ServiceProvider());

        class MyService
                implements ScopedServices.Activated {
            private final Backstack backstack;

            public MyService(Backstack backstack) {
                this.backstack = backstack;
            }

            @Override
            public void onServiceActive() {
                backstack.setHistory(History.of(destination), StateChange.REPLACE);
            }

            @Override
            public void onServiceInactive() {

            }
        }

        final MyService service = new MyService(backstack);

        TestKeyWithOnlyParentServices beep = new TestKeyWithOnlyParentServices("beep",
                History.of(
                        "registration")) {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
                if(serviceBinder.getScopeTag().equals("registration")) {
                    serviceBinder.addService("SERVICE", service);
                }
            }
        };

        TestKeyWithScope boop = new TestKeyWithScope("boop") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
            }
        };


        backstack.setup(History.of(boop));
        backstack.setStateChanger(stateChanger);

        backstack.setHistory(History.of(beep), StateChange.REPLACE);

        assertThat(backstack.getHistory()).containsExactly(destination);
    }


    @Test
    public void lookupServiceFromScopeWorks() {
        Backstack backstack = new Backstack();
        backstack.setScopedServices(new ServiceProvider());

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
            public void bindServices(ServiceBinder serviceBinder) {
                assertThat(serviceBinder.hasService("service0")).isFalse();
                assertThat(serviceBinder.canFindFromScope(serviceBinder.getScopeTag(), "service0")).isFalse();
                serviceBinder.addService("service0", service0);
                assertThat(serviceBinder.hasService("service0")).isTrue();
                assertThat(serviceBinder.canFindFromScope(serviceBinder.getScopeTag(), "service0")).isTrue();
                assertThat(serviceBinder.getService("service0")).isSameAs(service0);
                assertThat(serviceBinder.lookupService("service0")).isSameAs(service0);
                assertThat(serviceBinder.lookupFromScope(serviceBinder.getScopeTag(), "service0")).isSameAs(service0);
                assertThat(serviceBinder.canFindFromScope(serviceBinder.getScopeTag(), "service")).isFalse();
                serviceBinder.addService("service", service1);
                assertThat(serviceBinder.canFindFromScope(serviceBinder.getScopeTag(), "service")).isTrue();
                assertThat(serviceBinder.lookupFromScope(serviceBinder.getScopeTag(), "service")).isSameAs(service1);
            }

            @Nonnull
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
            public void bindServices(ServiceBinder serviceBinder) {
                assertThat(serviceBinder.lookupService("service0")).isSameAs(service0);
                assertThat(serviceBinder.canFindFromScope(serviceBinder.getScopeTag(), "service")).isTrue();
                assertThat(serviceBinder.lookupService("service")).isSameAs(service1);
                serviceBinder.addService("service", service2);
                // the mostimportant assertion here
                assertThat(serviceBinder.lookupService("service")).isSameAs(service2);
                assertThat(serviceBinder.lookupFromScope("beep", "service")).isSameAs(service1);

                serviceBinder.addService("service3", service3);
            }

            @Nonnull
            @Override
            public String getScopeTag() {
                return "boop";
            }
        }

        backstack.setup(History.of(new Key1("beep"), new Key2("boop")));
        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });

        assertThat(backstack.lookupService("service")).isSameAs(service2);
        assertThat(backstack.canFindService("service3")).isTrue();
        assertThat(backstack.canFindFromScope("boop", "service3")).isTrue();
        assertThat(backstack.lookupFromScope("boop", "service3")).isSameAs(service3);
        assertThat(backstack.lookupFromScope("beep", "service")).isSameAs(service1);
        assertThat(backstack.lookupFromScope("boop", "service")).isSameAs(service2);

        backstack.goBack();

        assertThat(backstack.canFindFromScope("boop", "service3")).isFalse();
        assertThat(backstack.lookupService("service")).isSameAs(service1);
    }

    @Test
    public void sameServiceRegisteredInScopeMultipleTimesReceivesCallbackOnlyOnce() {
        Backstack backstack = new Backstack();
        backstack.setScopedServices(new ServiceProvider());

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
            public void bindServices(ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());
                serviceBinder.addService(serviceTag1, service);
                serviceBinder.addService(serviceTag2, service);
            }

            @Nonnull
            @Override
            public String getScopeTag() {
                return "beep";
            }
        };

        TestKey clear = new TestKey("clear");

        backstack.setup(History.of(beep));

        assertThat(activated).isEmpty();
        assertThat(inactivated).isEmpty();
        assertThat(registered).isEmpty();
        assertThat(unregistered).isEmpty();
        backstack.setStateChanger(stateChanger);

        assertThat(activated).isNotEmpty();
        assertThat(inactivated).isEmpty();
        assertThat(registered).isNotEmpty();
        assertThat(unregistered).isEmpty();

        assertThat(activated).containsOnlyOnce(service);
        assertThat(registered).containsOnlyOnce(service);

        backstack.setHistory(History.of(clear), StateChange.REPLACE);

        assertThat(activated).isNotEmpty();
        assertThat(inactivated).isNotEmpty();
        assertThat(registered).isNotEmpty();
        assertThat(unregistered).isNotEmpty();

        assertThat(inactivated).containsOnlyOnce(service);
        assertThat(unregistered).containsOnlyOnce(service);
    }

    @Test
    public void finalizingScopeTwiceShouldBeNoOp() {
        Backstack backstack = new Backstack();
        backstack.setScopedServices(new ServiceProvider());
        backstack.setup(History.of(testKey2));
        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });
        assertThat(backstack.hasService(testKey2, SERVICE_TAG)).isTrue();

        backstack.finalizeScopes();

        try {
            backstack.finalizeScopes();
        } catch(Throwable e) {
            Assert.fail("Should be no-op.");
        }
    }

    @Test
    public void scopeBuiltByNavigationButNotInLatestKeysShouldBeAccessibleByLookup() {
        Backstack backstack = new Backstack();

        final Object helloService = new Object();
        final Object worldService = new Object();
        final Object kappaService = new Object();

        backstack.setScopedServices(new ScopedServices() {
            @Override
            public void bindServices(@Nonnull ServiceBinder serviceBinder) {
                if("hello".equals(serviceBinder.getScopeTag())) {
                    serviceBinder.addService("hello", helloService);
                } else if("world".equals(serviceBinder.getScopeTag())) {
                    serviceBinder.addService("world", worldService);
                } else if("kappa".equals(serviceBinder.getScopeTag())) {
                    serviceBinder.addService("kappa", kappaService);
                }
            }
        });

        TestKeyWithScope scopeKey1 = new TestKeyWithScope("hello");
        TestKeyWithScope scopeKey2 = new TestKeyWithScope("world");
        TestKeyWithScope scopeKey3 = new TestKeyWithScope("kappa");

        backstack.setup(History.of(scopeKey1, scopeKey2));

        final AtomicReference<StateChanger.Callback> callbackRef = new AtomicReference<>();

        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
                callbackRef.set(completionCallback);
            }
        });

        callbackRef.get().stateChangeComplete();

        backstack.setHistory(History.of(scopeKey1, scopeKey3), StateChange.REPLACE);

        assertThat(backstack.lookupService("hello")).isSameAs(helloService);
        assertThat(backstack.lookupService("kappa")).isSameAs(kappaService);
        assertThat(backstack.lookupService("world")).isSameAs(worldService);

        assertThat(backstack.canFindService("hello")).isTrue();
        assertThat(backstack.canFindService("kappa")).isTrue();
        assertThat(backstack.canFindService("world")).isTrue();

        callbackRef.get().stateChangeComplete();


        assertThat(backstack.canFindService("hello")).isTrue();
        assertThat(backstack.canFindService("kappa")).isTrue();
        assertThat(backstack.canFindService("world")).isFalse();
    }

    @Test
    public void scopeBuiltByNavigationButNotInLatestKeysCanBeFoundByKey() {
        Backstack backstack = new Backstack();

        final Object helloService = new Object();
        final Object worldService = new Object();
        final Object kappaService = new Object();

        backstack.setScopedServices(new ScopedServices() {
            @Override
            public void bindServices(@Nonnull ServiceBinder serviceBinder) {
                if("hello".equals(serviceBinder.getScopeTag())) {
                    serviceBinder.addService("hello", helloService);
                } else if("world".equals(serviceBinder.getScopeTag())) {
                    serviceBinder.addService("world", worldService);
                } else if("kappa".equals(serviceBinder.getScopeTag())) {
                    serviceBinder.addService("kappa", kappaService);
                }
            }
        });

        class TestKeyWithExplicitParent extends TestKeyWithScope implements ScopeKey.Child {
            private String[] parentScopes;

            TestKeyWithExplicitParent(String name, String... parentScopes) {
                super(name);
                this.parentScopes = parentScopes;
            }

            protected TestKeyWithExplicitParent(Parcel in) {
                super(in);
            }

            @Nonnull
            @Override
            public List<String> getParentScopes() {
                return History.from(Arrays.asList(parentScopes));
            }
        }

        TestKeyWithScope scopeKey1 = new TestKeyWithScope("hello");
        TestKeyWithScope scopeKey2 = new TestKeyWithExplicitParent("world", "parent");
        TestKeyWithScope scopeKey3 = new TestKeyWithScope("kappa");
        TestKey scopeKey4 = new TestKey("aaaaaa");

        backstack.setup(History.of(scopeKey1, scopeKey2));

        final AtomicReference<StateChanger.Callback> callbackRef = new AtomicReference<>();

        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
                callbackRef.set(completionCallback);
            }
        });

        callbackRef.get().stateChangeComplete();

        backstack.setHistory(History.of(scopeKey1, scopeKey4, scopeKey3), StateChange.REPLACE);

        assertThat(backstack.findScopesForKey(scopeKey1, ScopeLookupMode.ALL)).containsExactly("hello");
        assertThat(backstack.findScopesForKey(scopeKey2, ScopeLookupMode.ALL)).containsExactly("world", "parent", "hello");
        assertThat(backstack.findScopesForKey(scopeKey3, ScopeLookupMode.ALL)).containsExactly("kappa", "world", "parent", "hello");

        assertThat(backstack.findScopesForKey(scopeKey1, ScopeLookupMode.EXPLICIT)).containsExactly("hello");
        assertThat(backstack.findScopesForKey(scopeKey2, ScopeLookupMode.EXPLICIT)).containsExactly("world", "parent");
        assertThat(backstack.findScopesForKey(scopeKey3, ScopeLookupMode.EXPLICIT)).containsExactly("kappa");

        callbackRef.get().stateChangeComplete();

        assertThat(backstack.findScopesForKey(scopeKey1, ScopeLookupMode.ALL)).containsExactly("hello");
        assertThat(backstack.findScopesForKey(scopeKey2, ScopeLookupMode.ALL)).isEmpty();
        assertThat(backstack.findScopesForKey(scopeKey3, ScopeLookupMode.ALL)).containsExactly("kappa", "hello");
        assertThat(backstack.findScopesForKey(scopeKey4, ScopeLookupMode.ALL)).containsExactly(
                "hello");

        assertThat(backstack.findScopesForKey(scopeKey1, ScopeLookupMode.EXPLICIT)).containsExactly("hello");
        assertThat(backstack.findScopesForKey(scopeKey2, ScopeLookupMode.EXPLICIT)).isEmpty();
        assertThat(backstack.findScopesForKey(scopeKey3, ScopeLookupMode.EXPLICIT)).containsExactly("kappa");
    }


    @Test
    public void scopeBuiltByNavigationButNotInLatestKeysCanBeFoundFromScope() {
        Backstack backstack = new Backstack();

        final Object helloService = new Object();
        final Object worldService = new Object();
        final Object kappaService = new Object();
        final Object parentService = new Object();

        backstack.setScopedServices(new ScopedServices() {
            @Override
            public void bindServices(@Nonnull ServiceBinder serviceBinder) {
                if("hello".equals(serviceBinder.getScopeTag())) {
                    serviceBinder.addService("hello", helloService);
                } else if("world".equals(serviceBinder.getScopeTag())) {
                    serviceBinder.addService("world", worldService);
                } else if("kappa".equals(serviceBinder.getScopeTag())) {
                    serviceBinder.addService("kappa", kappaService);
                } else if("parent".equals(serviceBinder.getScopeTag())) {
                    serviceBinder.addService("parent", parentService);
                }
            }
        });


        class TestKeyWithExplicitParent extends TestKeyWithScope implements ScopeKey.Child {
            private String[] parentScopes;

            TestKeyWithExplicitParent(String name, String... parentScopes) {
                super(name);
                this.parentScopes = parentScopes;
            }

            protected TestKeyWithExplicitParent(Parcel in) {
                super(in);
            }

            @Nonnull
            @Override
            public List<String> getParentScopes() {
                return History.from(Arrays.asList(parentScopes));
            }
        }

        TestKeyWithScope scopeKey1 = new TestKeyWithScope("hello");
        TestKeyWithScope scopeKey2 = new TestKeyWithExplicitParent("world", "parent");
        TestKeyWithScope scopeKey3 = new TestKeyWithScope("kappa");

        backstack.setup(History.of(scopeKey1, scopeKey2));

        final AtomicReference<StateChanger.Callback> callbackRef = new AtomicReference<>();

        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
                callbackRef.set(completionCallback);
            }
        });

        callbackRef.get().stateChangeComplete();

        backstack.setHistory(History.of(scopeKey1, scopeKey3), StateChange.REPLACE);

        assertThat(backstack.lookupFromScope("hello", "hello")).isSameAs(helloService);
        assertThat(backstack.lookupFromScope("world", "world")).isSameAs(worldService);
        assertThat(backstack.lookupFromScope("kappa", "kappa")).isSameAs(kappaService);

        assertThat(backstack.lookupFromScope("hello", "hello", ScopeLookupMode.ALL)).isSameAs(helloService);
        assertThat(backstack.lookupFromScope("world", "world", ScopeLookupMode.ALL)).isSameAs(worldService);
        assertThat(backstack.lookupFromScope("kappa", "kappa", ScopeLookupMode.ALL)).isSameAs(kappaService);

        assertThat(backstack.lookupFromScope("hello", "hello", ScopeLookupMode.EXPLICIT)).isSameAs(helloService);
        assertThat(backstack.lookupFromScope("world", "world", ScopeLookupMode.EXPLICIT)).isSameAs(worldService);
        assertThat(backstack.lookupFromScope("kappa", "kappa", ScopeLookupMode.EXPLICIT)).isSameAs(kappaService);

        assertThat(backstack.lookupFromScope("world", "hello")).isSameAs(helloService);

        assertThat(backstack.lookupFromScope("kappa", "hello")).isSameAs(helloService);
        assertThat(backstack.lookupFromScope("kappa", "world")).isSameAs(worldService);

        assertThat(backstack.lookupFromScope("world", "hello", ScopeLookupMode.ALL)).isSameAs(helloService);

        assertThat(backstack.lookupFromScope("kappa", "hello", ScopeLookupMode.ALL)).isSameAs(helloService);
        assertThat(backstack.lookupFromScope("kappa", "world", ScopeLookupMode.ALL)).isSameAs(worldService);

        assertThat(backstack.lookupFromScope("world", "parent", ScopeLookupMode.EXPLICIT)).isSameAs(parentService);

        //

        assertThat(backstack.canFindFromScope("hello", "hello")).isTrue();
        assertThat(backstack.canFindFromScope("world", "world")).isTrue();
        assertThat(backstack.canFindFromScope("kappa", "kappa")).isTrue();

        assertThat(backstack.canFindFromScope("hello", "hello", ScopeLookupMode.ALL)).isTrue();
        assertThat(backstack.canFindFromScope("world", "world", ScopeLookupMode.ALL)).isTrue();
        assertThat(backstack.canFindFromScope("kappa", "kappa", ScopeLookupMode.ALL)).isTrue();

        assertThat(backstack.canFindFromScope("hello", "hello", ScopeLookupMode.EXPLICIT)).isTrue();
        assertThat(backstack.canFindFromScope("world", "world", ScopeLookupMode.EXPLICIT)).isTrue();
        assertThat(backstack.canFindFromScope("kappa", "kappa", ScopeLookupMode.EXPLICIT)).isTrue();

        assertThat(backstack.canFindFromScope("world", "hello")).isTrue();

        assertThat(backstack.canFindFromScope("kappa", "hello")).isTrue();
        assertThat(backstack.canFindFromScope("kappa", "world")).isTrue();

        assertThat(backstack.canFindFromScope("world", "hello", ScopeLookupMode.ALL)).isTrue();

        assertThat(backstack.canFindFromScope("kappa", "hello", ScopeLookupMode.ALL)).isTrue();
        assertThat(backstack.canFindFromScope("kappa", "world", ScopeLookupMode.ALL)).isTrue();

        assertThat(backstack.canFindFromScope("world", "parent", ScopeLookupMode.EXPLICIT)).isTrue();

        assertThat(backstack.canFindFromScope("parent", "parent", ScopeLookupMode.EXPLICIT)).isTrue();

        callbackRef.get().stateChangeComplete();

        assertThat(backstack.canFindFromScope("world", "world", ScopeLookupMode.ALL)).isFalse();
        assertThat(backstack.canFindFromScope("world", "parent", ScopeLookupMode.ALL)).isFalse();
        assertThat(backstack.canFindFromScope("parent", "parent", ScopeLookupMode.ALL)).isFalse();

        assertThat(backstack.canFindFromScope("world", "world", ScopeLookupMode.EXPLICIT)).isFalse();
        assertThat(backstack.canFindFromScope("world", "parent", ScopeLookupMode.EXPLICIT)).isFalse();
        assertThat(backstack.canFindFromScope("parent", "parent", ScopeLookupMode.EXPLICIT)).isFalse();
    }

    @Test
    public void keyWithinNavigationButWithoutScopeStillAbleToFindScopes() {
        Backstack backstack = new Backstack();

        final Object helloService = new Object();
        final Object worldService = new Object();
        final Object kappaService = new Object();
        final Object parentService = new Object();

        backstack.setScopedServices(new ScopedServices() {
            @Override
            public void bindServices(@Nonnull ServiceBinder serviceBinder) {
                if("hello".equals(serviceBinder.getScopeTag())) {
                    serviceBinder.addService("hello", helloService);
                } else if("world".equals(serviceBinder.getScopeTag())) {
                    serviceBinder.addService("world", worldService);
                } else if("kappa".equals(serviceBinder.getScopeTag())) {
                    serviceBinder.addService("kappa", kappaService);
                } else if("parent".equals(serviceBinder.getScopeTag())) {
                    serviceBinder.addService("parent", parentService);
                }
            }
        });


        class TestKeyWithExplicitParent extends TestKeyWithScope implements ScopeKey.Child {
            private String[] parentScopes;

            TestKeyWithExplicitParent(String name, String... parentScopes) {
                super(name);
                this.parentScopes = parentScopes;
            }

            protected TestKeyWithExplicitParent(Parcel in) {
                super(in);
            }

            @Nonnull
            @Override
            public List<String> getParentScopes() {
                return History.from(Arrays.asList(parentScopes));
            }
        }

        TestKeyWithScope scopeKey1 = new TestKeyWithScope("hello");
        TestKeyWithScope scopeKey2 = new TestKeyWithExplicitParent("world", "parent");
        TestKeyWithScope scopeKey3 = new TestKeyWithScope("kappa");
        TestKey key4 = new TestKey("360noscope");
        TestKey key5 = new TestKey("180noscope");

        backstack.setup(History.of(scopeKey1, scopeKey2, key4));

        final AtomicReference<StateChanger.Callback> callbackRef = new AtomicReference<>();

        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
                callbackRef.set(completionCallback);
            }
        });

        callbackRef.get().stateChangeComplete();

        assertThat(backstack.findScopesForKey(key4, ScopeLookupMode.EXPLICIT)).isEmpty();
        assertThat(backstack.findScopesForKey(key4, ScopeLookupMode.ALL)).containsExactly("world", "parent", "hello");

        backstack.setHistory(History.of(scopeKey1, scopeKey3, key5), StateChange.REPLACE);

        assertThat(backstack.findScopesForKey(key4, ScopeLookupMode.EXPLICIT)).isEmpty();
        assertThat(backstack.findScopesForKey(key4, ScopeLookupMode.ALL)).containsExactly("world", "parent", "hello");

        assertThat(backstack.findScopesForKey(key5, ScopeLookupMode.EXPLICIT)).isEmpty();
        assertThat(backstack.findScopesForKey(key5, ScopeLookupMode.ALL)).containsExactly("kappa", "world", "parent", "hello");

        callbackRef.get().stateChangeComplete();

        assertThat(backstack.findScopesForKey(key4, ScopeLookupMode.EXPLICIT)).isEmpty();
        assertThat(backstack.findScopesForKey(key4, ScopeLookupMode.ALL)).isEmpty();

        assertThat(backstack.findScopesForKey(key5, ScopeLookupMode.EXPLICIT)).isEmpty();
        assertThat(backstack.findScopesForKey(key5, ScopeLookupMode.ALL)).containsExactly("kappa", "hello");
    }

    @Test
    public void keyWithinNavigationWithOnlyExplicitScopeStillAbleToFindScopes() {
        Backstack backstack = new Backstack();

        final Object helloService = new Object();
        final Object worldService = new Object();
        final Object kappaService = new Object();
        final Object parentService = new Object();
        final Object parent2Service = new Object();

        backstack.setScopedServices(new ScopedServices() {
            @Override
            public void bindServices(@Nonnull ServiceBinder serviceBinder) {
                if("hello".equals(serviceBinder.getScopeTag())) {
                    serviceBinder.addService("hello", helloService);
                } else if("world".equals(serviceBinder.getScopeTag())) {
                    serviceBinder.addService("world", worldService);
                } else if("kappa".equals(serviceBinder.getScopeTag())) {
                    serviceBinder.addService("kappa", kappaService);
                } else if("parent".equals(serviceBinder.getScopeTag())) {
                    serviceBinder.addService("parent", parentService);
                } else if("parent2".equals(serviceBinder.getScopeTag())) {
                    serviceBinder.addService("parent2", parent2Service);
                }
            }
        });


        class TestKeyWithExplicitParent extends TestKeyWithScope implements ScopeKey.Child {
            private String[] parentScopes;

            TestKeyWithExplicitParent(String name, String... parentScopes) {
                super(name);
                this.parentScopes = parentScopes;
            }

            protected TestKeyWithExplicitParent(Parcel in) {
                super(in);
            }

            @Nonnull
            @Override
            public List<String> getParentScopes() {
                return History.from(Arrays.asList(parentScopes));
            }
        }


        class TestKeyWithOnlyExplicitParent extends TestKey implements ScopeKey.Child {
            private String[] parentScopes;

            TestKeyWithOnlyExplicitParent(String name, String... parentScopes) {
                super(name);
                this.parentScopes = parentScopes;
            }

            protected TestKeyWithOnlyExplicitParent(Parcel in) {
                super(in);
            }

            @Nonnull
            @Override
            public List<String> getParentScopes() {
                return History.from(Arrays.asList(parentScopes));
            }
        }

        TestKeyWithScope scopeKey1 = new TestKeyWithScope("hello");
        TestKeyWithScope scopeKey2 = new TestKeyWithExplicitParent("world", "parent");
        TestKeyWithScope scopeKey3 = new TestKeyWithScope("kappa");
        TestKey key4 = new TestKeyWithOnlyExplicitParent("parentpls", "parent2");

        backstack.setup(History.of(scopeKey1, scopeKey2, key4));

        final AtomicReference<StateChanger.Callback> callbackRef = new AtomicReference<>();

        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
                callbackRef.set(completionCallback);
            }
        });

        callbackRef.get().stateChangeComplete();

        assertThat(backstack.findScopesForKey(key4, ScopeLookupMode.EXPLICIT)).containsExactly("parent2");
        assertThat(backstack.findScopesForKey(key4, ScopeLookupMode.ALL)).containsExactly("parent2", "world", "parent", "hello");

        backstack.setHistory(History.of(scopeKey1, scopeKey3), StateChange.REPLACE);

        assertThat(backstack.findScopesForKey(key4, ScopeLookupMode.EXPLICIT)).containsExactly("parent2");
        assertThat(backstack.findScopesForKey(key4, ScopeLookupMode.ALL)).containsExactly("parent2", "world", "parent", "hello");

        callbackRef.get().stateChangeComplete();

        assertThat(backstack.findScopesForKey(key4, ScopeLookupMode.EXPLICIT)).isEmpty();
        assertThat(backstack.findScopesForKey(key4, ScopeLookupMode.ALL)).isEmpty();
    }

    @Test
    public void reproduceCrashIssue220() {
        Backstack backstack = new Backstack();
        Object key1 = new Object();

        ScopeKey key2 = new ScopeKey() {
            @Nonnull
            @Override
            public String getScopeTag() {
                return "key2";
            }

            @Override
            public String toString() {
                return "KEY2";
            }
        };

        ScopeKey key3 = new ScopeKey() {
            @Nonnull
            @Override
            public String getScopeTag() {
                return "key3";
            }

            @Override
            public String toString() {
                return "KEY3";
            }
        };

        backstack.setScopedServices(new ScopedServices() {
            @Override
            public void bindServices(@Nonnull ServiceBinder serviceBinder) {
                // just be there
            }
        });

        backstack.setup(History.of(key1));

        final StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        };

        backstack.setStateChanger(stateChanger);

        backstack.removeStateChanger();

        backstack.setHistory(History.of(key2), StateChange.REPLACE);

        backstack.setHistory(History.of(key3), StateChange.REPLACE);

        backstack.setStateChanger(stateChanger); // <-- crash
    }
}
