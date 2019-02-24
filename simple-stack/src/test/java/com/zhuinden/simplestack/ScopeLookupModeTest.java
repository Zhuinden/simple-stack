package com.zhuinden.simplestack;

import android.os.Parcel;
import android.support.annotation.NonNull;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ScopeLookupModeTest {
    private interface HasServices
            extends ScopeKey {
        void bindServices(ScopedServices.ServiceBinder serviceBinder);
    }

    private interface HasParentServices
            extends ScopeKey.Child {
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

    @Test
    public void lookupModesWork() {
        final BackstackManager backstackManager = new BackstackManager();
        backstackManager.setScopedServices(new ServiceProvider());

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
            public void bindServices(ScopedServices.ServiceBinder serviceBinder) {
                if("parent1".equals(serviceBinder.getScopeTag())) {
                    serviceBinder.add("parentService1", parentService1);
                } else if(name.equals(serviceBinder.getScopeTag())) {
                    serviceBinder.add("service1", service1);
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
            public void bindServices(ScopedServices.ServiceBinder serviceBinder) {
                if("parent2".equals(serviceBinder.getScopeTag())) {
                    serviceBinder.add("parentService2", parentService2);
                } else if(name.equals(serviceBinder.getScopeTag())) {
                    serviceBinder.add("service2", service2);
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

        assertThat(backstackManager.canFindFromScope("boop", "service")).isFalse();
        backstackManager.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });

        // default (ALL)
        assertThat(backstackManager.canFindFromScope("beep", "service1")).isTrue();
        assertThat(backstackManager.canFindFromScope("beep", "service2")).isFalse();
        assertThat(backstackManager.canFindFromScope("beep", "parentService1")).isTrue();
        assertThat(backstackManager.canFindFromScope("beep", "parentService2")).isFalse();

        assertThat(backstackManager.canFindFromScope("parent1", "service1")).isFalse();
        assertThat(backstackManager.canFindFromScope("parent1", "service2")).isFalse();
        assertThat(backstackManager.canFindFromScope("parent1", "parentService1")).isTrue();
        assertThat(backstackManager.canFindFromScope("parent1", "parentService2")).isFalse();

        assertThat(backstackManager.canFindFromScope("boop", "service1")).isTrue();
        assertThat(backstackManager.canFindFromScope("boop", "service2")).isTrue();
        assertThat(backstackManager.canFindFromScope("boop", "parentService1")).isTrue();
        assertThat(backstackManager.canFindFromScope("boop", "parentService2")).isTrue();

        assertThat(backstackManager.canFindFromScope("parent2", "service1")).isTrue();
        assertThat(backstackManager.canFindFromScope("parent2", "service2")).isFalse();
        assertThat(backstackManager.canFindFromScope("parent2", "parentService1")).isTrue();
        assertThat(backstackManager.canFindFromScope("parent2", "parentService2")).isTrue();

        // ALL specified
        assertThat(backstackManager.canFindFromScope("beep", "service1", ScopeLookupMode.ALL)).isTrue();
        assertThat(backstackManager.canFindFromScope("beep", "service2", ScopeLookupMode.ALL)).isFalse();
        assertThat(backstackManager.canFindFromScope("beep", "parentService1", ScopeLookupMode.ALL)).isTrue();
        assertThat(backstackManager.canFindFromScope("beep", "parentService2", ScopeLookupMode.ALL)).isFalse();

        assertThat(backstackManager.canFindFromScope("parent1", "service1", ScopeLookupMode.ALL)).isFalse();
        assertThat(backstackManager.canFindFromScope("parent1", "service2", ScopeLookupMode.ALL)).isFalse();
        assertThat(backstackManager.canFindFromScope("parent1", "parentService1", ScopeLookupMode.ALL)).isTrue();
        assertThat(backstackManager.canFindFromScope("parent1", "parentService2", ScopeLookupMode.ALL)).isFalse();

        assertThat(backstackManager.canFindFromScope("boop", "service1", ScopeLookupMode.ALL)).isTrue();
        assertThat(backstackManager.canFindFromScope("boop", "service2", ScopeLookupMode.ALL)).isTrue();
        assertThat(backstackManager.canFindFromScope("boop", "parentService1", ScopeLookupMode.ALL)).isTrue();
        assertThat(backstackManager.canFindFromScope("boop", "parentService2", ScopeLookupMode.ALL)).isTrue();

        assertThat(backstackManager.canFindFromScope("parent2", "service1", ScopeLookupMode.ALL)).isTrue();
        assertThat(backstackManager.canFindFromScope("parent2", "service2", ScopeLookupMode.ALL)).isFalse();
        assertThat(backstackManager.canFindFromScope("parent2", "parentService1", ScopeLookupMode.ALL)).isTrue();
        assertThat(backstackManager.canFindFromScope("parent2", "parentService2", ScopeLookupMode.ALL)).isTrue();

        // EXPLICIT specified
        assertThat(backstackManager.canFindFromScope("beep", "service1", ScopeLookupMode.EXPLICIT)).isTrue();
        assertThat(backstackManager.canFindFromScope("beep", "service2", ScopeLookupMode.EXPLICIT)).isFalse();
        assertThat(backstackManager.canFindFromScope("beep", "parentService1", ScopeLookupMode.EXPLICIT)).isTrue();
        assertThat(backstackManager.canFindFromScope("beep", "parentService2", ScopeLookupMode.EXPLICIT)).isFalse();

        assertThat(backstackManager.canFindFromScope("parent1", "service1", ScopeLookupMode.EXPLICIT)).isFalse();
        assertThat(backstackManager.canFindFromScope("parent1", "service2", ScopeLookupMode.EXPLICIT)).isFalse();
        assertThat(backstackManager.canFindFromScope("parent1", "parentService1", ScopeLookupMode.EXPLICIT)).isTrue();
        assertThat(backstackManager.canFindFromScope("parent1", "parentService2", ScopeLookupMode.EXPLICIT)).isFalse();

        assertThat(backstackManager.canFindFromScope("boop", "service1", ScopeLookupMode.EXPLICIT)).isFalse();
        assertThat(backstackManager.canFindFromScope("boop", "service2", ScopeLookupMode.EXPLICIT)).isTrue();
        assertThat(backstackManager.canFindFromScope("boop", "parentService1", ScopeLookupMode.EXPLICIT)).isFalse();
        assertThat(backstackManager.canFindFromScope("boop", "parentService2", ScopeLookupMode.EXPLICIT)).isTrue();

        assertThat(backstackManager.canFindFromScope("parent2", "service1", ScopeLookupMode.EXPLICIT)).isFalse();
        assertThat(backstackManager.canFindFromScope("parent2", "service2", ScopeLookupMode.EXPLICIT)).isFalse();
        assertThat(backstackManager.canFindFromScope("parent2", "parentService1", ScopeLookupMode.EXPLICIT)).isFalse();
        assertThat(backstackManager.canFindFromScope("parent2", "parentService2", ScopeLookupMode.EXPLICIT)).isTrue();

        // default (ALL)
        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstackManager.lookupFromScope("beep", "service2");
            }
        });
        assertThat(backstackManager.lookupFromScope("beep", "service1")).isSameAs(service1);
        assertThat(backstackManager.lookupFromScope("beep", "parentService1")).isSameAs(parentService1);
        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstackManager.lookupFromScope("beep", "parentService2");
            }
        });

        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstackManager.lookupFromScope("parent1", "service1");
            }
        });
        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstackManager.lookupFromScope("parent1", "service2");
            }
        });
        assertThat(backstackManager.lookupFromScope("parent1", "parentService1")).isSameAs(parentService1);
        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstackManager.lookupFromScope("parent1", "parentService2");
            }
        });

        assertThat(backstackManager.lookupFromScope("boop", "service1")).isSameAs(service1);
        assertThat(backstackManager.lookupFromScope("boop", "service2")).isSameAs(service2);
        assertThat(backstackManager.lookupFromScope("boop", "parentService1")).isSameAs(parentService1);
        assertThat(backstackManager.lookupFromScope("boop", "parentService2")).isSameAs(parentService2);

        assertThat(backstackManager.lookupFromScope("parent2", "service1")).isSameAs(service1);
        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstackManager.lookupFromScope("parent2", "service2");
            }
        });
        assertThat(backstackManager.lookupFromScope("parent2", "parentService1")).isSameAs(parentService1);
        assertThat(backstackManager.lookupFromScope("parent2", "parentService2")).isSameAs(parentService2);

        // ALL specified
        assertThat(backstackManager.lookupFromScope("beep", "service1", ScopeLookupMode.ALL)).isSameAs(service1);
        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstackManager.lookupFromScope("beep", "service2", ScopeLookupMode.ALL);
            }
        });
        assertThat(backstackManager.lookupFromScope("beep", "parentService1", ScopeLookupMode.ALL)).isSameAs(parentService1);
        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstackManager.lookupFromScope("beep", "parentService2", ScopeLookupMode.ALL);
            }
        });

        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstackManager.lookupFromScope("parent1", "service1", ScopeLookupMode.ALL);
            }
        });
        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstackManager.lookupFromScope("parent1", "service2", ScopeLookupMode.ALL);
            }
        });
        assertThat(backstackManager.lookupFromScope("parent1", "parentService1", ScopeLookupMode.ALL)).isSameAs(parentService1);
        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstackManager.lookupFromScope("parent1", "parentService2", ScopeLookupMode.ALL);
            }
        });

        assertThat(backstackManager.lookupFromScope("boop", "service1", ScopeLookupMode.ALL)).isSameAs(service1);
        assertThat(backstackManager.lookupFromScope("boop", "service2", ScopeLookupMode.ALL)).isSameAs(service2);
        assertThat(backstackManager.lookupFromScope("boop", "parentService1", ScopeLookupMode.ALL)).isSameAs(parentService1);
        assertThat(backstackManager.lookupFromScope("boop", "parentService2", ScopeLookupMode.ALL)).isSameAs(parentService2);

        assertThat(backstackManager.lookupFromScope("parent2", "service1", ScopeLookupMode.ALL)).isSameAs(service1);
        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstackManager.lookupFromScope("parent2", "service2", ScopeLookupMode.ALL);
            }
        });
        assertThat(backstackManager.lookupFromScope("parent2", "parentService1", ScopeLookupMode.ALL)).isSameAs(parentService1);
        assertThat(backstackManager.lookupFromScope("parent2", "parentService2", ScopeLookupMode.ALL)).isSameAs(parentService2);

        // EXPLICIT specified
        assertThat(backstackManager.lookupFromScope("beep", "service1", ScopeLookupMode.EXPLICIT)).isSameAs(service1);
        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstackManager.lookupFromScope("beep", "service2", ScopeLookupMode.EXPLICIT);
            }
        });
        assertThat(backstackManager.lookupFromScope("beep", "parentService1", ScopeLookupMode.EXPLICIT)).isSameAs(parentService1);
        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstackManager.lookupFromScope("beep", "parentService2", ScopeLookupMode.EXPLICIT);
            }
        });

        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstackManager.lookupFromScope("parent1", "service1", ScopeLookupMode.EXPLICIT);
            }
        });
        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstackManager.lookupFromScope("parent1", "service2", ScopeLookupMode.EXPLICIT);
            }
        });
        assertThat(backstackManager.lookupFromScope("parent1", "parentService1", ScopeLookupMode.EXPLICIT)).isSameAs(parentService1);
        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstackManager.lookupFromScope("parent1", "parentService2", ScopeLookupMode.EXPLICIT);
            }
        });

        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstackManager.lookupFromScope("boop", "service1", ScopeLookupMode.EXPLICIT);
            }
        });
        assertThat(backstackManager.lookupFromScope("boop", "service2", ScopeLookupMode.EXPLICIT)).isSameAs(service2);
        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstackManager.lookupFromScope("boop", "parentService1", ScopeLookupMode.EXPLICIT);
            }
        });
        assertThat(backstackManager.lookupFromScope("boop", "parentService2", ScopeLookupMode.EXPLICIT)).isSameAs(parentService2);

        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstackManager.lookupFromScope("parent2", "service1", ScopeLookupMode.EXPLICIT);
            }
        });
        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstackManager.lookupFromScope("parent2", "service2", ScopeLookupMode.EXPLICIT);
            }
        });
        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstackManager.lookupFromScope("parent2", "parentService1", ScopeLookupMode.EXPLICIT);
            }
        });
        assertThat(backstackManager.lookupFromScope("parent2", "parentService2", ScopeLookupMode.EXPLICIT)).isSameAs(parentService2);
    }

    private interface Action {
        void doSomething();
    }

    private void assertThrows(Action action) {
        try {
            action.doSomething();
            Assert.fail("Did not throw exception.");
        } catch(Exception e) {
            // OK!
        }
    }
}
