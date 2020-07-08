/*
 * Copyright 2019 Gabor Varadi
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

import android.os.Parcel;

import com.zhuinden.simplestack.helpers.HasParentServices;
import com.zhuinden.simplestack.helpers.HasServices;
import com.zhuinden.simplestack.helpers.ServiceProvider;
import com.zhuinden.simplestack.helpers.TestKey;
import com.zhuinden.simplestack.helpers.TestKeyWithScope;
import com.zhuinden.statebundle.StateBundle;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.assertj.core.api.Assertions.assertThat;

public class ScopingGlobalScopeTest {
    @Test
    public void globalScopeExists() {
        Object service = new Object();

        Backstack backstack = new Backstack();
        backstack.setGlobalServices(GlobalServices.builder()
                .addService("service", service)
                .build());
        backstack.setup(History.of(new TestKey("hello!")));
        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });

        assertThat(backstack.canFindService("service")).isTrue();
        assertThat(backstack.lookupService("service")).isSameAs(service);
    }

    @Test
    public void globalScopeLookupWorks() {
        final Backstack backstack = new Backstack();
        backstack.setScopedServices(new ServiceProvider());

        final Object globalService = new Object();

        backstack.setGlobalServices(GlobalServices.builder()
                .addService("parentService2", globalService)
                .build());

        final Object parentService1 = new Object();
        final Object parentService2 = new Object();

        final Object service1 = new Object();
        final Object service2 = new Object();

        class Key1
                extends TestKey
                implements HasServices, HasParentServices {
            Key1(String name) {
                super(name);
            }

            protected Key1(Parcel in) {
                super(in);
            }

            @Override
            public void bindServices(ServiceBinder serviceBinder) {
                if("parent1".equals(serviceBinder.getScopeTag())) {
                    serviceBinder.addService("parentService1", parentService1);
                } else if(name.equals(serviceBinder.getScopeTag())) {
                    serviceBinder.addService("service1", service1);
                }
            }

            @Nonnull
            @Override
            public String getScopeTag() {
                return name;
            }

            @Nonnull
            @Override
            public List<String> getParentScopes() {
                return History.of("parent1");
            }
        }

        class Key2
                extends TestKey
                implements HasServices, HasParentServices {
            Key2(String name) {
                super(name);
            }

            protected Key2(Parcel in) {
                super(in);
            }

            @Override
            public void bindServices(ServiceBinder serviceBinder) {
                if("parent2".equals(serviceBinder.getScopeTag())) {
                    serviceBinder.addService("parentService2", parentService2);
                } else if(name.equals(serviceBinder.getScopeTag())) {
                    serviceBinder.addService("service2", service2);
                }
            }

            @Nonnull
            @Override
            public String getScopeTag() {
                return name;
            }

            @Nonnull
            @Override
            public List<String> getParentScopes() {
                return History.of("parent2");
            }
        }

        backstack.setup(History.of(new Key1("beep"), new Key2("boop")));
        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });

        assertThat(backstack.canFindFromScope("boop", "parentService2", ScopeLookupMode.EXPLICIT)).isTrue();
        assertThat(backstack.canFindFromScope("beep", "parentService2", ScopeLookupMode.EXPLICIT)).isTrue();

        assertThat(backstack.lookupFromScope("boop", "parentService2", ScopeLookupMode.EXPLICIT)).isSameAs(parentService2);
        assertThat(backstack.lookupFromScope("beep", "parentService2", ScopeLookupMode.EXPLICIT)).isSameAs(globalService);

        assertThat(backstack.canFindFromScope("boop", "parentService2", ScopeLookupMode.ALL)).isTrue();
        assertThat(backstack.canFindFromScope("beep", "parentService2", ScopeLookupMode.ALL)).isTrue();

        assertThat(backstack.lookupFromScope("boop", "parentService2", ScopeLookupMode.ALL)).isSameAs(parentService2);
        assertThat(backstack.lookupFromScope("beep", "parentService2", ScopeLookupMode.ALL)).isSameAs(globalService);

        assertThat(backstack.lookupService("parentService2")).isSameAs(parentService2);

        backstack.goBack();

        assertThat(backstack.lookupService("parentService2")).isSameAs(globalService);
    }

    @Test
    public void globalScopeLookupPrefersImplicitsToGlobal() {
        final Backstack backstack = new Backstack();
        backstack.setScopedServices(new ServiceProvider());

        final Object globalService = new Object();

        backstack.setGlobalServices(GlobalServices.builder()
                .addService("parentService1", globalService)
                .build());

        final Object parentService1 = new Object();
        final Object parentService2 = new Object();

        final Object service1 = new Object();
        final Object service2 = new Object();

        class Key1
                extends TestKey
                implements HasServices, HasParentServices {
            Key1(String name) {
                super(name);
            }

            protected Key1(Parcel in) {
                super(in);
            }

            @Override
            public void bindServices(ServiceBinder serviceBinder) {
                if("parent1".equals(serviceBinder.getScopeTag())) {
                    serviceBinder.addService("parentService1", parentService1);
                } else if(name.equals(serviceBinder.getScopeTag())) {
                    serviceBinder.addService("service1", service1);
                }
            }

            @Nonnull
            @Override
            public String getScopeTag() {
                return name;
            }

            @Nonnull
            @Override
            public List<String> getParentScopes() {
                return History.of("parent1");
            }
        }

        class Key2
                extends TestKey
                implements HasServices, HasParentServices {
            Key2(String name) {
                super(name);
            }

            protected Key2(Parcel in) {
                super(in);
            }

            @Override
            public void bindServices(ServiceBinder serviceBinder) {
                if("parent2".equals(serviceBinder.getScopeTag())) {
                    serviceBinder.addService("parentService2", parentService2);
                } else if(name.equals(serviceBinder.getScopeTag())) {
                    serviceBinder.addService("service2", service2);
                }
            }

            @Nonnull
            @Override
            public String getScopeTag() {
                return name;
            }

            @Nonnull
            @Override
            public List<String> getParentScopes() {
                return History.of("parent2");
            }
        }

        backstack.setup(History.of(new Key1("beep"), new Key2("boop")));
        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });

        assertThat(backstack.canFindFromScope("boop", "parentService1", ScopeLookupMode.EXPLICIT)).isTrue();
        assertThat(backstack.canFindFromScope("beep", "parentService1", ScopeLookupMode.EXPLICIT)).isTrue();

        assertThat(backstack.lookupFromScope("boop", "parentService1", ScopeLookupMode.EXPLICIT)).isSameAs(globalService);
        assertThat(backstack.lookupFromScope("beep", "parentService1", ScopeLookupMode.EXPLICIT)).isSameAs(parentService1);

        assertThat(backstack.canFindFromScope("boop", "parentService1", ScopeLookupMode.ALL)).isTrue();
        assertThat(backstack.canFindFromScope("beep", "parentService1", ScopeLookupMode.ALL)).isTrue();

        assertThat(backstack.lookupFromScope("boop", "parentService1", ScopeLookupMode.ALL)).isSameAs(parentService1);
        assertThat(backstack.lookupFromScope("beep", "parentService1", ScopeLookupMode.ALL)).isSameAs(parentService1);

        assertThat(backstack.lookupService("parentService1")).isSameAs(parentService1);

        backstack.setHistory(History.of(new Key2("boop")), StateChange.REPLACE);

        assertThat(backstack.lookupService("parentService1")).isSameAs(globalService);
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
    public void serviceLifecycleCallbacksWork() {
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
            public String toString() {
                return "MyService{" +
                        "id=" + id +
                        '}';
            }

            @Override
            public void onServiceRegistered() {
                events.add(Pair.of((Object) this, ServiceEvent.CREATE));
            }

            @Override
            public void onServiceUnregistered() {
                events.add(Pair.of((Object) this, ServiceEvent.DESTROY));
            }
        }

        final Object service0 = new MyService(0);

        backstack.setGlobalServices(GlobalServices.builder()
                .addService("SERVICE0", service0)
                .build());

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
        };

        TestKeyWithScope boop = new TestKeyWithScope("boop") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());

                serviceBinder.addService("SERVICE4", service4);
                serviceBinder.addService("SERVICE5", service5);
                serviceBinder.addService("SERVICE6", service6);
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
        };

        backstack.setup(History.of(beep, boop));

        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        };
        backstack.setStateChanger(stateChanger);

        backstack.goTo(braap);

        backstack.removeStateChanger(); // just to make sure
        backstack.setStateChanger(stateChanger); // just to make sure

        backstack.goBack();

        backstack.finalizeScopes();

        assertThat(events).containsExactly(
                Pair.of(service0, ServiceEvent.CREATE),
                Pair.of(service1, ServiceEvent.CREATE),
                Pair.of(service2, ServiceEvent.CREATE),
                Pair.of(service3, ServiceEvent.CREATE),
                Pair.of(service4, ServiceEvent.CREATE),
                Pair.of(service5, ServiceEvent.CREATE),
                Pair.of(service6, ServiceEvent.CREATE),
                Pair.of(service0, ServiceEvent.ACTIVE),
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
                Pair.of(service0, ServiceEvent.INACTIVE),
                Pair.of(service6, ServiceEvent.DESTROY),
                Pair.of(service5, ServiceEvent.DESTROY),
                Pair.of(service4, ServiceEvent.DESTROY),
                Pair.of(service3, ServiceEvent.DESTROY),
                Pair.of(service2, ServiceEvent.DESTROY),
                Pair.of(service1, ServiceEvent.DESTROY),
                Pair.of(service0, ServiceEvent.DESTROY)
        );
    }

    @Test
    public void globalServiceStateIsRestored() {
        class Service
                implements Bundleable {
            int blah = 2;

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
        }

        TestKey testKey = new TestKey("world");

        final Service service = new Service();
        final Backstack backstack = new Backstack();
        backstack.setGlobalServices(GlobalServices.builder().addService("service", service).build());

        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        };

        backstack.setup(History.of(testKey));
        backstack.setStateChanger(stateChanger);

        assertThat(backstack.canFindService("service")).isTrue();

        StateBundle stateBundle = backstack.toBundle();

        final Backstack backstack2 = new Backstack();
        backstack2.setGlobalServices(GlobalServices.builder()
                .addService("service", new Service())
                .build());

        StateChanger stateChanger2 = new StateChanger() {
            @Override
            public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        };

        backstack2.setup(History.of(testKey));
        backstack2.fromBundle(stateBundle);
        backstack2.setStateChanger(stateChanger2);

        assertThat(backstack2.lookupService("service")).isNotSameAs(service);
        assertThat(backstack.<Service>lookupService("service").blah).isEqualTo(2);
        assertThat(backstack2.<Service>lookupService("service").blah).isEqualTo(5);
    }

    @Test
    public void newHistoryShouldReinitializeScopes() {
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
            public String toString() {
                return "MyService{" +
                        "id=" + id +
                        '}';
            }

            @Override
            public void onServiceRegistered() {
                events.add(Pair.of((Object) this, ServiceEvent.CREATE));
            }

            @Override
            public void onServiceUnregistered() {
                events.add(Pair.of((Object) this, ServiceEvent.DESTROY));
            }
        }


        final MyService globalService = new MyService(0);
        final MyService explicitParentService = new MyService(1);
        final MyService implicitParentService = new MyService(2);
        final MyService currentScopeService = new MyService(3);

        abstract class TestKeyWithExplicitParent extends TestKeyWithScope implements HasParentServices {
            TestKeyWithExplicitParent(String name) {
                super(name);
            }

            protected TestKeyWithExplicitParent(Parcel in) {
                super(in);
            }

            @Nonnull
            @Override
            public List<String> getParentScopes() {
                return History.of("explicitParentScope");
            }

            @Override
            public final void bindServices(ServiceBinder serviceBinder) {
                if("explicitParentScope".equals(serviceBinder.getScopeTag())) {
                    serviceBinder.addService("explicitParentService", explicitParentService);
                }
                if(name.equals(serviceBinder.getScopeTag())) {
                    bindOwnServices(serviceBinder);
                }
            }

            abstract void bindOwnServices(ServiceBinder serviceBinder);
        }

        TestKeyWithScope beep = new TestKeyWithExplicitParent("beep") {
            @Override
            void bindOwnServices(ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());

                serviceBinder.addService("implicitParentService", implicitParentService);
            }
        };

        TestKeyWithScope boop = new TestKeyWithExplicitParent("boop") {
            @Override
            void bindOwnServices(ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());

                serviceBinder.addService("currentScopeService", currentScopeService);
            }
        };

        backstack.setGlobalServices(GlobalServices.builder()
                .addService("globalService", globalService)
                .build()
        );
        backstack.setup(History.of(beep, boop));
        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });

        assertThat(backstack.canFindService("globalService")).isTrue();
        assertThat(backstack.canFindService("currentScopeService")).isTrue();
        assertThat(backstack.canFindService("implicitParentService")).isTrue();
        assertThat(backstack.canFindService("explicitParentService")).isTrue();

        backstack.finalizeScopes();

        assertThat(backstack.canFindService("currentScopeService")).isFalse();
        assertThat(backstack.canFindService("implicitParentService")).isFalse();
        assertThat(backstack.canFindService("explicitParentService")).isFalse();
        assertThat(backstack.canFindService("globalService")).isFalse();

        backstack.setHistory(History.of(beep, boop), StateChange.REPLACE);

        assertThat(backstack.canFindService("globalService")).isTrue();
        assertThat(backstack.canFindService("currentScopeService")).isTrue();
        assertThat(backstack.canFindService("implicitParentService")).isTrue();
        assertThat(backstack.canFindService("explicitParentService")).isTrue();

        assertThat(backstack.hasService("explicitParentScope", "explicitParentService")).isTrue();

        assertThat(events).containsExactly(
                Pair.of((Object)globalService, ServiceEvent.CREATE),
                Pair.of((Object)explicitParentService, ServiceEvent.CREATE),
                Pair.of((Object)implicitParentService, ServiceEvent.CREATE),
                Pair.of((Object)currentScopeService, ServiceEvent.CREATE),
                Pair.of((Object)globalService, ServiceEvent.ACTIVE),
                Pair.of((Object)explicitParentService, ServiceEvent.ACTIVE),
                Pair.of((Object)currentScopeService, ServiceEvent.ACTIVE),
                Pair.of((Object)currentScopeService, ServiceEvent.INACTIVE),
                Pair.of((Object)explicitParentService, ServiceEvent.INACTIVE),
                Pair.of((Object)globalService, ServiceEvent.INACTIVE),
                Pair.of((Object)currentScopeService, ServiceEvent.DESTROY),
                Pair.of((Object)implicitParentService, ServiceEvent.DESTROY),
                Pair.of((Object)explicitParentService, ServiceEvent.DESTROY),
                Pair.of((Object)globalService, ServiceEvent.DESTROY),
                Pair.of((Object)globalService, ServiceEvent.CREATE),
                Pair.of((Object)explicitParentService, ServiceEvent.CREATE),
                Pair.of((Object)implicitParentService, ServiceEvent.CREATE),
                Pair.of((Object)currentScopeService, ServiceEvent.CREATE),
                Pair.of((Object)globalService, ServiceEvent.ACTIVE),
                Pair.of((Object)explicitParentService, ServiceEvent.ACTIVE),
                Pair.of((Object)currentScopeService, ServiceEvent.ACTIVE)
        );

        // this is just to check things, the test'simportant part is the one above
        backstack.goBack();

        assertThat(events).containsExactly(
                Pair.of((Object)globalService, ServiceEvent.CREATE),
                Pair.of((Object)explicitParentService, ServiceEvent.CREATE),
                Pair.of((Object)implicitParentService, ServiceEvent.CREATE),
                Pair.of((Object)currentScopeService, ServiceEvent.CREATE),
                Pair.of((Object)globalService, ServiceEvent.ACTIVE),
                Pair.of((Object)explicitParentService, ServiceEvent.ACTIVE),
                Pair.of((Object)currentScopeService, ServiceEvent.ACTIVE),
                Pair.of((Object)currentScopeService, ServiceEvent.INACTIVE),
                Pair.of((Object)explicitParentService, ServiceEvent.INACTIVE),
                Pair.of((Object)globalService, ServiceEvent.INACTIVE),
                Pair.of((Object)currentScopeService, ServiceEvent.DESTROY),
                Pair.of((Object)implicitParentService, ServiceEvent.DESTROY),
                Pair.of((Object)explicitParentService, ServiceEvent.DESTROY),
                Pair.of((Object)globalService, ServiceEvent.DESTROY),
                Pair.of((Object)globalService, ServiceEvent.CREATE),
                Pair.of((Object)explicitParentService, ServiceEvent.CREATE),
                Pair.of((Object)implicitParentService, ServiceEvent.CREATE),
                Pair.of((Object)currentScopeService, ServiceEvent.CREATE),
                Pair.of((Object)globalService, ServiceEvent.ACTIVE),
                Pair.of((Object)explicitParentService, ServiceEvent.ACTIVE),
                Pair.of((Object)currentScopeService, ServiceEvent.ACTIVE),
                Pair.of((Object)implicitParentService, ServiceEvent.ACTIVE),
                Pair.of((Object)currentScopeService, ServiceEvent.INACTIVE),
                Pair.of((Object)currentScopeService, ServiceEvent.DESTROY)
        );
    }

    @Test
    public void globalServicesFactoryWorks() {
        final Object service = new Object();

        Backstack backstack = new Backstack();
        backstack.setGlobalServices(new GlobalServices.Factory() {
            @Nonnull
            @Override
            public GlobalServices create(@Nonnull Backstack backstack) {
                return GlobalServices.builder()
                        .addService("service", service)
                        .build();
            }
        });
        backstack.setup(History.of(new TestKey("hello!")));
        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });

        assertThat(backstack.canFindService("service")).isTrue();
        assertThat(backstack.lookupService("service")).isSameAs(service);
    }

    @Test
    public void globalServicesFactoryOverridesGlobalServices() {
        final Object service1 = new Object();
        final Object service2 = new Object();

        Backstack backstack = new Backstack();

        backstack.setGlobalServices(GlobalServices.builder()
                .addService("service1", service1)
                .build());

        backstack.setGlobalServices(new GlobalServices.Factory() {
            @Nonnull
            @Override
            public GlobalServices create(@Nonnull Backstack backstack) {
                return GlobalServices.builder()
                        .addService("service2", service2)
                        .build();
            }
        });

        backstack.setup(History.of(new TestKey("hello!")));
        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });

        assertThat(backstack.canFindService("service1")).isFalse();
        assertThat(backstack.canFindService("service2")).isTrue();
        assertThat(backstack.lookupService("service2")).isSameAs(service2);
    }

    @Test
    public void globalServicesFactoryRunsAgainAfterFinalization() {
        Backstack backstack = new Backstack();

        final AtomicReference<Object> serviceRef = new AtomicReference<>();
        backstack.setGlobalServices(new GlobalServices.Factory() {
            @Nonnull
            @Override
            public GlobalServices create(@Nonnull Backstack backstack) {
                Object service = new Object();
                serviceRef.set(service);
                return GlobalServices.builder()
                        .addService("service", service)
                        .build();
            }
        });
        backstack.setup(History.of(new TestKey("hello!")));
        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });

        assertThat(backstack.canFindService("service")).isTrue();

        Object service1 = serviceRef.get();

        backstack.finalizeScopes();
        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });

        Object service2 = serviceRef.get();

        assertThat(service1).isNotSameAs(service2);
    }


    @Test
    public void globalServicesFactoryFailsIfBackstackIsAddedAsService() {
        Backstack backstack = new Backstack();

        backstack.setGlobalServices(new GlobalServices.Factory() {
            @Nonnull
            @Override
            public GlobalServices create(@Nonnull Backstack backstack) {
                return GlobalServices.builder().addService("backstack", backstack).build();
            }
        });

        backstack.setup(History.of(new TestKey("hello!")));
        try {
            backstack.setStateChanger(new StateChanger() {
                @Override
                public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
                    completionCallback.stateChangeComplete();
                }
            });
            StateBundle bundle = backstack.toBundle();
            Assert.fail("This would fail on `toBundle()`");
        } catch(IllegalArgumentException e) {
            // OK!
        }
    }

    @Test
    public void globalServicesFactorySucceedsIfBackstackIsAddedAsAlias() {
        Backstack backstack = new Backstack();

        backstack.setGlobalServices(new GlobalServices.Factory() {
            @Nonnull
            @Override
            public GlobalServices create(@Nonnull Backstack backstack) {
                return GlobalServices.builder().addAlias("backstack", backstack).build();
            }
        });

        backstack.setup(History.of(new TestKey("hello!")));
        backstack.setStateChanger(new StateChanger() {
                @Override
                public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
                    completionCallback.stateChangeComplete();
            }
            });

        assertThat(backstack.lookupService("backstack")).isSameAs(backstack);
    }
}
