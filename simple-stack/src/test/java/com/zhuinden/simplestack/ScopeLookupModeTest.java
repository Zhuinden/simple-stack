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

import com.zhuinden.simplestack.helpers.Action;
import com.zhuinden.simplestack.helpers.HasParentServices;
import com.zhuinden.simplestack.helpers.HasServices;
import com.zhuinden.simplestack.helpers.ServiceProvider;
import com.zhuinden.simplestack.helpers.TestKey;

import org.junit.Test;

import java.util.List;

import static com.zhuinden.simplestack.helpers.AssertionHelper.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;

public class ScopeLookupModeTest {
    @Test
    public void lookupModesWork() {
        final Backstack backstack = new Backstack();
        backstack.setScopedServices(new ServiceProvider());

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

        backstack.setup(History.of(new Key1("beep"), new Key2("boop")));

        assertThat(backstack.canFindFromScope("boop", "service")).isFalse();
        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });

        // default (ALL)
        assertThat(backstack.canFindFromScope("beep", "service1")).isTrue();
        assertThat(backstack.canFindFromScope("beep", "service2")).isFalse();
        assertThat(backstack.canFindFromScope("beep", "parentService1")).isTrue();
        assertThat(backstack.canFindFromScope("beep", "parentService2")).isFalse();

        assertThat(backstack.canFindFromScope("parent1", "service1")).isFalse();
        assertThat(backstack.canFindFromScope("parent1", "service2")).isFalse();
        assertThat(backstack.canFindFromScope("parent1", "parentService1")).isTrue();
        assertThat(backstack.canFindFromScope("parent1", "parentService2")).isFalse();

        assertThat(backstack.canFindFromScope("boop", "service1")).isTrue();
        assertThat(backstack.canFindFromScope("boop", "service2")).isTrue();
        assertThat(backstack.canFindFromScope("boop", "parentService1")).isTrue();
        assertThat(backstack.canFindFromScope("boop", "parentService2")).isTrue();

        assertThat(backstack.canFindFromScope("parent2", "service1")).isTrue();
        assertThat(backstack.canFindFromScope("parent2", "service2")).isFalse();
        assertThat(backstack.canFindFromScope("parent2", "parentService1")).isTrue();
        assertThat(backstack.canFindFromScope("parent2", "parentService2")).isTrue();

        // ALL specified
        assertThat(backstack.canFindFromScope("beep", "service1", ScopeLookupMode.ALL)).isTrue();
        assertThat(backstack.canFindFromScope("beep", "service2", ScopeLookupMode.ALL)).isFalse();
        assertThat(backstack.canFindFromScope("beep", "parentService1", ScopeLookupMode.ALL)).isTrue();
        assertThat(backstack.canFindFromScope("beep", "parentService2", ScopeLookupMode.ALL)).isFalse();

        assertThat(backstack.canFindFromScope("parent1", "service1", ScopeLookupMode.ALL)).isFalse();
        assertThat(backstack.canFindFromScope("parent1", "service2", ScopeLookupMode.ALL)).isFalse();
        assertThat(backstack.canFindFromScope("parent1", "parentService1", ScopeLookupMode.ALL)).isTrue();
        assertThat(backstack.canFindFromScope("parent1", "parentService2", ScopeLookupMode.ALL)).isFalse();

        assertThat(backstack.canFindFromScope("boop", "service1", ScopeLookupMode.ALL)).isTrue();
        assertThat(backstack.canFindFromScope("boop", "service2", ScopeLookupMode.ALL)).isTrue();
        assertThat(backstack.canFindFromScope("boop", "parentService1", ScopeLookupMode.ALL)).isTrue();
        assertThat(backstack.canFindFromScope("boop", "parentService2", ScopeLookupMode.ALL)).isTrue();

        assertThat(backstack.canFindFromScope("parent2", "service1", ScopeLookupMode.ALL)).isTrue();
        assertThat(backstack.canFindFromScope("parent2", "service2", ScopeLookupMode.ALL)).isFalse();
        assertThat(backstack.canFindFromScope("parent2", "parentService1", ScopeLookupMode.ALL)).isTrue();
        assertThat(backstack.canFindFromScope("parent2", "parentService2", ScopeLookupMode.ALL)).isTrue();

        // EXPLICIT specified
        assertThat(backstack.canFindFromScope("beep", "service1", ScopeLookupMode.EXPLICIT)).isTrue();
        assertThat(backstack.canFindFromScope("beep", "service2", ScopeLookupMode.EXPLICIT)).isFalse();
        assertThat(backstack.canFindFromScope("beep", "parentService1", ScopeLookupMode.EXPLICIT)).isTrue();
        assertThat(backstack.canFindFromScope("beep", "parentService2", ScopeLookupMode.EXPLICIT)).isFalse();

        assertThat(backstack.canFindFromScope("parent1", "service1", ScopeLookupMode.EXPLICIT)).isFalse();
        assertThat(backstack.canFindFromScope("parent1", "service2", ScopeLookupMode.EXPLICIT)).isFalse();
        assertThat(backstack.canFindFromScope("parent1", "parentService1", ScopeLookupMode.EXPLICIT)).isTrue();
        assertThat(backstack.canFindFromScope("parent1", "parentService2", ScopeLookupMode.EXPLICIT)).isFalse();

        assertThat(backstack.canFindFromScope("boop", "service1", ScopeLookupMode.EXPLICIT)).isFalse();
        assertThat(backstack.canFindFromScope("boop", "service2", ScopeLookupMode.EXPLICIT)).isTrue();
        assertThat(backstack.canFindFromScope("boop", "parentService1", ScopeLookupMode.EXPLICIT)).isFalse();
        assertThat(backstack.canFindFromScope("boop", "parentService2", ScopeLookupMode.EXPLICIT)).isTrue();

        assertThat(backstack.canFindFromScope("parent2", "service1", ScopeLookupMode.EXPLICIT)).isFalse();
        assertThat(backstack.canFindFromScope("parent2", "service2", ScopeLookupMode.EXPLICIT)).isFalse();
        assertThat(backstack.canFindFromScope("parent2", "parentService1", ScopeLookupMode.EXPLICIT)).isFalse();
        assertThat(backstack.canFindFromScope("parent2", "parentService2", ScopeLookupMode.EXPLICIT)).isTrue();

        // default (ALL)
        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstack.lookupFromScope("beep", "service2");
            }
        });
        assertThat(backstack.lookupFromScope("beep", "service1")).isSameAs(service1);
        assertThat(backstack.lookupFromScope("beep", "parentService1")).isSameAs(parentService1);
        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstack.lookupFromScope("beep", "parentService2");
            }
        });

        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstack.lookupFromScope("parent1", "service1");
            }
        });
        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstack.lookupFromScope("parent1", "service2");
            }
        });
        assertThat(backstack.lookupFromScope("parent1", "parentService1")).isSameAs(parentService1);
        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstack.lookupFromScope("parent1", "parentService2");
            }
        });

        assertThat(backstack.lookupFromScope("boop", "service1")).isSameAs(service1);
        assertThat(backstack.lookupFromScope("boop", "service2")).isSameAs(service2);
        assertThat(backstack.lookupFromScope("boop", "parentService1")).isSameAs(parentService1);
        assertThat(backstack.lookupFromScope("boop", "parentService2")).isSameAs(parentService2);

        assertThat(backstack.lookupFromScope("parent2", "service1")).isSameAs(service1);
        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstack.lookupFromScope("parent2", "service2");
            }
        });
        assertThat(backstack.lookupFromScope("parent2", "parentService1")).isSameAs(parentService1);
        assertThat(backstack.lookupFromScope("parent2", "parentService2")).isSameAs(parentService2);

        // ALL specified
        assertThat(backstack.lookupFromScope("beep", "service1", ScopeLookupMode.ALL)).isSameAs(service1);
        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstack.lookupFromScope("beep", "service2", ScopeLookupMode.ALL);
            }
        });
        assertThat(backstack.lookupFromScope("beep", "parentService1", ScopeLookupMode.ALL)).isSameAs(parentService1);
        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstack.lookupFromScope("beep", "parentService2", ScopeLookupMode.ALL);
            }
        });

        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstack.lookupFromScope("parent1", "service1", ScopeLookupMode.ALL);
            }
        });
        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstack.lookupFromScope("parent1", "service2", ScopeLookupMode.ALL);
            }
        });
        assertThat(backstack.lookupFromScope("parent1", "parentService1", ScopeLookupMode.ALL)).isSameAs(parentService1);
        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstack.lookupFromScope("parent1", "parentService2", ScopeLookupMode.ALL);
            }
        });

        assertThat(backstack.lookupFromScope("boop", "service1", ScopeLookupMode.ALL)).isSameAs(service1);
        assertThat(backstack.lookupFromScope("boop", "service2", ScopeLookupMode.ALL)).isSameAs(service2);
        assertThat(backstack.lookupFromScope("boop", "parentService1", ScopeLookupMode.ALL)).isSameAs(parentService1);
        assertThat(backstack.lookupFromScope("boop", "parentService2", ScopeLookupMode.ALL)).isSameAs(parentService2);

        assertThat(backstack.lookupFromScope("parent2", "service1", ScopeLookupMode.ALL)).isSameAs(service1);
        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstack.lookupFromScope("parent2", "service2", ScopeLookupMode.ALL);
            }
        });
        assertThat(backstack.lookupFromScope("parent2", "parentService1", ScopeLookupMode.ALL)).isSameAs(parentService1);
        assertThat(backstack.lookupFromScope("parent2", "parentService2", ScopeLookupMode.ALL)).isSameAs(parentService2);

        // EXPLICIT specified
        assertThat(backstack.lookupFromScope("beep", "service1", ScopeLookupMode.EXPLICIT)).isSameAs(service1);
        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstack.lookupFromScope("beep", "service2", ScopeLookupMode.EXPLICIT);
            }
        });
        assertThat(backstack.lookupFromScope("beep", "parentService1", ScopeLookupMode.EXPLICIT)).isSameAs(parentService1);
        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstack.lookupFromScope("beep", "parentService2", ScopeLookupMode.EXPLICIT);
            }
        });

        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstack.lookupFromScope("parent1", "service1", ScopeLookupMode.EXPLICIT);
            }
        });
        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstack.lookupFromScope("parent1", "service2", ScopeLookupMode.EXPLICIT);
            }
        });
        assertThat(backstack.lookupFromScope("parent1", "parentService1", ScopeLookupMode.EXPLICIT)).isSameAs(parentService1);
        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstack.lookupFromScope("parent1", "parentService2", ScopeLookupMode.EXPLICIT);
            }
        });

        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstack.lookupFromScope("boop", "service1", ScopeLookupMode.EXPLICIT);
            }
        });
        assertThat(backstack.lookupFromScope("boop", "service2", ScopeLookupMode.EXPLICIT)).isSameAs(service2);
        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstack.lookupFromScope("boop", "parentService1", ScopeLookupMode.EXPLICIT);
            }
        });
        assertThat(backstack.lookupFromScope("boop", "parentService2", ScopeLookupMode.EXPLICIT)).isSameAs(parentService2);

        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstack.lookupFromScope("parent2", "service1", ScopeLookupMode.EXPLICIT);
            }
        });
        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstack.lookupFromScope("parent2", "service2", ScopeLookupMode.EXPLICIT);
            }
        });
        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstack.lookupFromScope("parent2", "parentService1", ScopeLookupMode.EXPLICIT);
            }
        });
        assertThat(backstack.lookupFromScope("parent2", "parentService2", ScopeLookupMode.EXPLICIT)).isSameAs(parentService2);
    }

    @Test
    public void findScopesForKeyWorks() {
        final Backstack backstack = new Backstack();
        backstack.setScopedServices(new ServiceProvider());

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

        backstack.setup(History.of(new Key1("beep"), new Key2("boop")));

        assertThat(backstack.findScopesForKey(new Key1("beep"), ScopeLookupMode.EXPLICIT)).isEmpty();
        assertThat(backstack.findScopesForKey(new Key2("boop"), ScopeLookupMode.EXPLICIT)).isEmpty();
        assertThat(backstack.findScopesForKey(new Key1("beep"), ScopeLookupMode.ALL)).isEmpty();
        assertThat(backstack.findScopesForKey(new Key2("boop"), ScopeLookupMode.ALL)).isEmpty();

        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });

        assertThat(backstack.findScopesForKey(new Key1("beep"), ScopeLookupMode.EXPLICIT)).containsExactly("beep", "parent1");
        assertThat(backstack.findScopesForKey(new Key2("boop"), ScopeLookupMode.EXPLICIT)).containsExactly("boop", "parent2");
        assertThat(backstack.findScopesForKey(new Key1("beep"), ScopeLookupMode.ALL)).containsExactly("beep", "parent1");
        assertThat(backstack.findScopesForKey(new Key2("boop"), ScopeLookupMode.ALL)).containsExactly("boop", "parent2", "beep", "parent1");
    }

    @Test
    public void findScopesForKeyIncludesGlobalScopeIfAvailable() {
        final Backstack backstack = new Backstack();

        backstack.setScopedServices(new ServiceProvider());

        Object globalService = new Object();
        backstack.setGlobalServices(
                GlobalServices.builder()
                        .addService("globalService", globalService)
                        .build()
        );

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

        backstack.setup(History.of(new Key1("beep"), new Key2("boop")));

        assertThat(backstack.findScopesForKey(new Key1("beep"), ScopeLookupMode.EXPLICIT)).isEmpty();
        assertThat(backstack.findScopesForKey(new Key2("boop"), ScopeLookupMode.EXPLICIT)).isEmpty();
        assertThat(backstack.findScopesForKey(new Key1("beep"), ScopeLookupMode.ALL)).isEmpty();
        assertThat(backstack.findScopesForKey(new Key2("boop"), ScopeLookupMode.ALL)).isEmpty();

        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });

        assertThat(backstack.findScopesForKey(new Key1("beep"), ScopeLookupMode.EXPLICIT)).containsExactly("beep", "parent1", ScopeManager.GLOBAL_SCOPE_TAG);
        assertThat(backstack.findScopesForKey(new Key2("boop"), ScopeLookupMode.EXPLICIT)).containsExactly("boop", "parent2", ScopeManager.GLOBAL_SCOPE_TAG);
        assertThat(backstack.findScopesForKey(new Key1("beep"), ScopeLookupMode.ALL)).containsExactly("beep", "parent1", ScopeManager.GLOBAL_SCOPE_TAG);
        assertThat(backstack.findScopesForKey(new Key2("boop"), ScopeLookupMode.ALL)).containsExactly("boop", "parent2", "beep", "parent1", ScopeManager.GLOBAL_SCOPE_TAG);
    }

    @Test
    public void findScopesForKeyOtherSetup() {
        abstract class TestKeyWithScope
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

        Backstack backstack = new Backstack();
        backstack.setScopedServices(new ServiceProvider());

        class MyService {
            private final String id;

            MyService(String id) {
                this.id = id;
            }

            @Override
            public String toString() {
                return "MyService{" +
                        "id=" + id +
                        '}';
            }
        }

        final Object service0 = new MyService("service0");

        backstack.setGlobalServices(GlobalServices.builder()
                .addService("service0", service0)
                .build());

        TestKeyWithScope beep = new TestKeyWithScope("scope1") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
            }
        };

        abstract class TestKeyWithExplicitParent extends TestKeyWithScope implements HasParentServices {
            TestKeyWithExplicitParent(String name) {
                super(name);
            }

            protected TestKeyWithExplicitParent(Parcel in) {
                super(in);
            }

            @Override
            public final void bindServices(ServiceBinder serviceBinder) {
                if(name.equals(serviceBinder.getScopeTag())) {
                    bindOwnServices(serviceBinder);
                } else {
                    bindParentServices(serviceBinder);
                }
            }

            abstract void bindParentServices(ServiceBinder serviceBinder);

            abstract void bindOwnServices(ServiceBinder serviceBinder);
        }

        TestKeyWithExplicitParent boop = new TestKeyWithExplicitParent("scope2") {
            @NonNull
            @Override
            public List<String> getParentScopes() {
                return History.of("parent1", "parent2");
            }

            @Override
            void bindParentServices(ServiceBinder serviceBinder) {
            }

            @Override
            void bindOwnServices(ServiceBinder serviceBinder) {
            }
        };

        TestKeyWithExplicitParent braap = new TestKeyWithExplicitParent("scope3") {
            @NonNull
            @Override
            public List<String> getParentScopes() {
                return History.of("parent1", "parent3");
            }

            @Override
            void bindParentServices(ServiceBinder serviceBinder) {
            }

            @Override
            void bindOwnServices(ServiceBinder serviceBinder) {
            }
        };

        /*                      GLOBAL
         *                                PARENT1
         *                        PARENT2        PARENT3
         *   BEEP               BOOP                 BRAAP
         */
        backstack.setup(History.of(beep, boop, braap));

        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        };
        backstack.setStateChanger(stateChanger);

        assertThat(backstack.findScopesForKey(beep, ScopeLookupMode.ALL)).containsExactly("scope1", ScopeManager.GLOBAL_SCOPE_TAG);
        assertThat(backstack.findScopesForKey(beep, ScopeLookupMode.EXPLICIT)).containsExactly("scope1", ScopeManager.GLOBAL_SCOPE_TAG);
        assertThat(backstack.findScopesForKey(boop, ScopeLookupMode.ALL)).containsExactly("scope2", "parent2", "parent1", "scope1", ScopeManager.GLOBAL_SCOPE_TAG);
        assertThat(backstack.findScopesForKey(boop, ScopeLookupMode.EXPLICIT)).containsExactly("scope2", "parent2", "parent1", ScopeManager.GLOBAL_SCOPE_TAG);
        assertThat(backstack.findScopesForKey(braap, ScopeLookupMode.ALL)).containsExactly("scope3", "parent3", "parent1", "scope2", "parent2", "scope1", ScopeManager.GLOBAL_SCOPE_TAG);
        assertThat(backstack.findScopesForKey(braap, ScopeLookupMode.EXPLICIT)).containsExactly("scope3", "parent3", "parent1", ScopeManager.GLOBAL_SCOPE_TAG);

        backstack.finalizeScopes();

        assertThat(backstack.findScopesForKey(beep, ScopeLookupMode.ALL)).isEmpty();
        assertThat(backstack.findScopesForKey(beep, ScopeLookupMode.EXPLICIT)).isEmpty();
        assertThat(backstack.findScopesForKey(boop, ScopeLookupMode.ALL)).isEmpty();
        assertThat(backstack.findScopesForKey(boop, ScopeLookupMode.EXPLICIT)).isEmpty();
        assertThat(backstack.findScopesForKey(braap, ScopeLookupMode.ALL)).isEmpty();
        assertThat(backstack.findScopesForKey(braap, ScopeLookupMode.EXPLICIT)).isEmpty();
    }
}
