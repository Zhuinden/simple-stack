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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.zhuinden.statebundle.StateBundle;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class ScopingGlobalScopeTest {
    private abstract class TestKeyWithScope
            extends TestKey
            implements HasServices {
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
    }


    private interface HasServices
            extends ScopeKey {
        void bindServices(ServiceBinder serviceBinder);
    }

    private interface HasParentServices
            extends ScopeKey.Child {
        void bindServices(ServiceBinder serviceBinder);
    }


    private static class ServiceProvider
            implements ScopedServices {
        @Override
        public void bindServices(@NonNull ServiceBinder serviceBinder) {
            Object key = serviceBinder.getKey();
            if(key instanceof HasServices) {
                ((HasServices) key).bindServices(serviceBinder);
                return;
            }
            if(key instanceof HasParentServices) {
                ((HasParentServices) key).bindServices(serviceBinder);
                //noinspection UnnecessaryReturnStatement
                return;
            }
        }
    }

    @Test
    public void globalScopeExists() {
        Object service = new Object();

        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setGlobalServices(GlobalServices.builder()
                .addService("service", service)
                .build());
        backstackManager.setup(History.of(new TestKey("hello!")));
        backstackManager.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });

        assertThat(backstackManager.canFindService("service")).isTrue();
        assertThat(backstackManager.lookupService("service")).isSameAs(service);
    }

    @Test
    public void globalScopeLookupWorks() {
        final BackstackManager backstackManager = new BackstackManager();
        backstackManager.setScopedServices(new ServiceProvider());

        final Object globalService = new Object();

        backstackManager.setGlobalServices(GlobalServices.builder()
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

            @NonNull
            @Override
            public String getScopeTag() {
                return name;
            }

            @NonNull
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

            @NonNull
            @Override
            public String getScopeTag() {
                return name;
            }

            @NonNull
            @Override
            public List<String> getParentScopes() {
                return History.of("parent2");
            }
        }

        backstackManager.setup(History.of(new Key1("beep"), new Key2("boop")));
        backstackManager.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });

        assertThat(backstackManager.canFindFromScope("boop", "parentService2", ScopeLookupMode.EXPLICIT)).isTrue();
        assertThat(backstackManager.canFindFromScope("beep", "parentService2", ScopeLookupMode.EXPLICIT)).isTrue();

        assertThat(backstackManager.lookupFromScope("boop", "parentService2", ScopeLookupMode.EXPLICIT)).isSameAs(parentService2);
        assertThat(backstackManager.lookupFromScope("beep", "parentService2", ScopeLookupMode.EXPLICIT)).isSameAs(globalService);

        assertThat(backstackManager.canFindFromScope("boop", "parentService2", ScopeLookupMode.ALL)).isTrue();
        assertThat(backstackManager.canFindFromScope("beep", "parentService2", ScopeLookupMode.ALL)).isTrue();

        assertThat(backstackManager.lookupFromScope("boop", "parentService2", ScopeLookupMode.ALL)).isSameAs(parentService2);
        assertThat(backstackManager.lookupFromScope("beep", "parentService2", ScopeLookupMode.ALL)).isSameAs(globalService);

        assertThat(backstackManager.lookupService("parentService2")).isSameAs(parentService2);

        backstackManager.getBackstack().goBack();

        assertThat(backstackManager.lookupService("parentService2")).isSameAs(globalService);
    }

    @Test
    public void globalScopeLookupPrefersImplicitsToGlobal() {
        final BackstackManager backstackManager = new BackstackManager();
        backstackManager.setScopedServices(new ServiceProvider());

        final Object globalService = new Object();

        backstackManager.setGlobalServices(GlobalServices.builder()
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

            @NonNull
            @Override
            public String getScopeTag() {
                return name;
            }

            @NonNull
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

            @NonNull
            @Override
            public String getScopeTag() {
                return name;
            }

            @NonNull
            @Override
            public List<String> getParentScopes() {
                return History.of("parent2");
            }
        }

        backstackManager.setup(History.of(new Key1("beep"), new Key2("boop")));
        backstackManager.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });

        assertThat(backstackManager.canFindFromScope("boop", "parentService1", ScopeLookupMode.EXPLICIT)).isTrue();
        assertThat(backstackManager.canFindFromScope("beep", "parentService1", ScopeLookupMode.EXPLICIT)).isTrue();

        assertThat(backstackManager.lookupFromScope("boop", "parentService1", ScopeLookupMode.EXPLICIT)).isSameAs(globalService);
        assertThat(backstackManager.lookupFromScope("beep", "parentService1", ScopeLookupMode.EXPLICIT)).isSameAs(parentService1);

        assertThat(backstackManager.canFindFromScope("boop", "parentService1", ScopeLookupMode.ALL)).isTrue();
        assertThat(backstackManager.canFindFromScope("beep", "parentService1", ScopeLookupMode.ALL)).isTrue();

        assertThat(backstackManager.lookupFromScope("boop", "parentService1", ScopeLookupMode.ALL)).isSameAs(parentService1);
        assertThat(backstackManager.lookupFromScope("beep", "parentService1", ScopeLookupMode.ALL)).isSameAs(parentService1);

        assertThat(backstackManager.lookupService("parentService1")).isSameAs(parentService1);

        backstackManager.getBackstack().setHistory(History.of(new Key2("boop")), StateChange.REPLACE);

        assertThat(backstackManager.lookupService("parentService1")).isSameAs(globalService);
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

        backstackManager.setGlobalServices(GlobalServices.builder()
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

        backstackManager.setup(History.of(beep, boop));

        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        };
        backstackManager.setStateChanger(stateChanger);

        backstackManager.getBackstack().goTo(braap);

        backstackManager.getBackstack().removeStateChanger(); // just to make sure
        backstackManager.setStateChanger(stateChanger); // just to make sure

        backstackManager.getBackstack().goBack();

        backstackManager.finalizeScopes();

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
        }

        TestKey testKey2 = new TestKey("world");

        final Service service = new Service();
        final ScopeManager scopeManager = new ScopeManager();

        scopeManager.setGlobalServices(GlobalServices.builder().addService("service", service).build());

        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                scopeManager.buildScopes(stateChange.getNewKeys());
                completionCallback.stateChangeComplete();
            }
        };

        Backstack backstack = new Backstack(History.of(testKey2));
        backstack.setStateChanger(stateChanger);

        assertThat(scopeManager.canFindService("service")).isTrue();

        StateBundle stateBundle = scopeManager.saveStates();

        final ScopeManager scopeManager2 = new ScopeManager();
        scopeManager2.setGlobalServices(GlobalServices.builder()
                .addService("service", new Service())
                .build());

        StateChanger stateChanger2 = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                scopeManager2.buildScopes(stateChange.getNewKeys());
                completionCallback.stateChangeComplete();
            }
        };

        scopeManager2.setRestoredStates(stateBundle);

        Backstack backstack2 = new Backstack(History.of(testKey2));

        backstack2.setStateChanger(stateChanger2);

        assertThat(scopeManager2.lookupService("service")).isNotSameAs(service);
        assertThat(scopeManager.<Service>lookupService("service").blah).isEqualTo(2);
        assertThat(scopeManager2.<Service>lookupService("service").blah).isEqualTo(5);
    }

    @Test
    public void newHistoryShouldReinitializeScopes() {
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

            @NonNull
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

        backstackManager.setGlobalServices(GlobalServices.builder()
                .addService("globalService", globalService)
                .build()
        );
        backstackManager.setup(History.of(beep, boop));
        backstackManager.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });

        assertThat(backstackManager.canFindService("globalService")).isTrue();
        assertThat(backstackManager.canFindService("currentScopeService")).isTrue();
        assertThat(backstackManager.canFindService("implicitParentService")).isTrue();
        assertThat(backstackManager.canFindService("explicitParentService")).isTrue();

        backstackManager.finalizeScopes();

        assertThat(backstackManager.canFindService("currentScopeService")).isFalse();
        assertThat(backstackManager.canFindService("implicitParentService")).isFalse();
        assertThat(backstackManager.canFindService("explicitParentService")).isFalse();
        assertThat(backstackManager.canFindService("globalService")).isFalse();

        backstackManager.getBackstack().setHistory(History.of(beep, boop), StateChange.REPLACE);

        assertThat(backstackManager.canFindService("globalService")).isTrue();
        assertThat(backstackManager.canFindService("currentScopeService")).isTrue();
        assertThat(backstackManager.canFindService("implicitParentService")).isTrue();
        assertThat(backstackManager.canFindService("explicitParentService")).isTrue();

        assertThat(backstackManager.hasService("explicitParentScope", "explicitParentService")).isTrue();

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

        // this is just to check things, the test's important part is the one above
        backstackManager.getBackstack().goBack();

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
}
