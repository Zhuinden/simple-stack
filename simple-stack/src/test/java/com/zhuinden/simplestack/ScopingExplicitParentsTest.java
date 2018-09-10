package com.zhuinden.simplestack;

import android.app.Activity;
import android.support.annotation.NonNull;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ScopingExplicitParentsTest {
    private static class DefaultScopedServices
            implements ScopedServices {
        @Override
        public void bindServices(@NonNull ServiceBinder serviceBinder) {
            // boop
        }
    }

    @Test
    public void explicitParentScopesListThrowsIfNull() {
        class ChildKey
                extends TestKey
                implements ScopeKey.Child {
            ChildKey(String name) {
                super(name);
            }

            @NonNull
            @Override
            public List<String> getParentScopes() {
                return null;
            }
        }
        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setScopedServices(new DefaultScopedServices());
        backstackManager.setup(History.of(new ChildKey("hello")));
        boolean success = false;
        try {
            backstackManager.setStateChanger(new StateChanger() {
                @Override
                public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                    completionCallback.stateChangeComplete();
                }
            });
        } catch(IllegalArgumentException e) {
            success = true;
        }
        if(!success) {
            Assert.fail("The parent scope list should not be null!");
        }
    }

    @Test
    public void explicitParentsWithoutScopedServicesThrows() {
        class ChildKey
                extends TestKey
                implements ScopeKey.Child {
            ChildKey(String name) {
                super(name);
            }

            @NonNull
            @Override
            public List<String> getParentScopes() {
                return History.of("parentScope");
            }
        }
        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setup(History.of(new ChildKey("hello")));
        boolean success = false;
        try {
            backstackManager.setStateChanger(new StateChanger() {
                @Override
                public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                    completionCallback.stateChangeComplete();
                }
            });
        } catch(IllegalStateException e) {
            success = true;
        }
        if(!success) {
            Assert.fail("Asserting scoped services should have thrown here!");
        }
    }

    @Test
    public void explicitParentsDefinedByScopeAreCreated() {
        class ChildKey
                extends TestKey
                implements ScopeKey.Child {
            ChildKey(String name) {
                super(name);
            }

            @NonNull
            @Override
            public List<String> getParentScopes() {
                return History.of("parentScope");
            }
        }
        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setScopedServices(new DefaultScopedServices());
        backstackManager.setup(History.of(new ChildKey("hello")));
        backstackManager.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });

        assertThat(backstackManager.hasScope("parentScope")).isTrue();
    }

    @Test
    public void explicitParentsDefinedByMultipleKeysAreCreatedInTheRightOrder() {
        class ChildKey1
                extends TestKey
                implements ScopeKey, ScopeKey.Child {
            ChildKey1(String name) {
                super(name);
            }

            @NonNull
            @Override
            public List<String> getParentScopes() {
                return History.of("parentScope1", "parentScope2");
            }

            @NonNull
            @Override
            public String getScopeTag() {
                return name;
            }
        }

        class ChildKey2
                extends TestKey
                implements ScopeKey, ScopeKey.Child {
            ChildKey2(String name) {
                super(name);
            }

            @NonNull
            @Override
            public List<String> getParentScopes() {
                return History.of("parentScope2", "parentScope3");
            }

            @NonNull
            @Override
            public String getScopeTag() {
                return name;
            }
        }

        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setScopedServices(new DefaultScopedServices());
        backstackManager.setup(History.of(new ChildKey1("hello"), new ChildKey2("world")));
        backstackManager.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });

        assertThat(backstackManager.hasScope("parentScope1")).isTrue();
        assertThat(backstackManager.hasScope("parentScope2")).isTrue();
        assertThat(backstackManager.hasScope("parentScope3")).isTrue();
        assertThat(backstackManager.hasScope("hello")).isTrue();
        assertThat(backstackManager.hasScope("world")).isTrue();

        backstackManager.getBackstack().goBack();

        assertThat(backstackManager.hasScope("parentScope1")).isTrue();
        assertThat(backstackManager.hasScope("parentScope2")).isTrue();
        assertThat(backstackManager.hasScope("parentScope3")).isFalse();
        assertThat(backstackManager.hasScope("hello")).isTrue();
        assertThat(backstackManager.hasScope("world")).isFalse();
    }

    @Test
    public void explicitParentsArePreferredDuringScopeLookup() {
        final Object service1 = new Object();
        final Object service2 = new Object();
        final Object service3 = new Object();
        final Object service4 = new Object();

        class ChildKey
                extends TestKey
                implements ScopeKey, ScopeKey.Child {
            private final List<String> parentScopes;

            ChildKey(String name, List<String> parentScopes) {
                super(name);
                this.parentScopes = parentScopes;
            }

            @NonNull
            @Override
            public String getScopeTag() {
                return name;
            }

            @NonNull
            @Override
            public List<String> getParentScopes() {
                return parentScopes;
            }
        }

        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setScopedServices(new ScopedServices() {
            @Override
            public void bindServices(@NonNull ServiceBinder serviceBinder) {
                String tag = serviceBinder.getScopeTag();
                if("hello".equals(tag)) {
                    serviceBinder.add("SERVICE", service1);
                }
                if("parentScope1".equals(tag)) {
                    serviceBinder.add("SERVICE", service2);
                }
                if("parentScope2".equals(tag)) {
                    serviceBinder.add("SERVICE", service3);
                }
                if("parentScope3".equals(tag)) {
                    serviceBinder.add("SERVICE", service4);
                }
            }
        });

        backstackManager.setup(History.of(
                new ChildKey("hello", History.of("parentScope1")),
                new ChildKey("world", History.of("parentScope2", "parentScope3"))
        ));
        backstackManager.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });

        assertThat(backstackManager.hasScope("hello")).isTrue();
        assertThat(backstackManager.hasScope("world")).isTrue();
        assertThat(backstackManager.hasScope("parentScope1")).isTrue();
        assertThat(backstackManager.hasScope("parentScope2")).isTrue();
        assertThat(backstackManager.hasScope("parentScope3")).isTrue();

        assertThat(backstackManager.getService("hello", "SERVICE")).isSameAs(service1);
        assertThat(backstackManager.getService("parentScope1", "SERVICE")).isSameAs(service2);
        assertThat(backstackManager.getService("parentScope2", "SERVICE")).isSameAs(service3);
        assertThat(backstackManager.getService("parentScope3", "SERVICE")).isSameAs(service4);

        assertThat(backstackManager.lookupService("SERVICE")).isSameAs(service4);

        backstackManager.getBackstack().goBack();

        assertThat(backstackManager.lookupService("SERVICE")).isSameAs(service1);
    }

    @Test
    public void explicitParentsAreLookedUpEvenInPreviousKeysDuringLookup() {
        final Object service1 = new Object();

        class ChildKey
                extends TestKey
                implements ScopeKey, ScopeKey.Child {
            private final List<String> parentScopes;

            ChildKey(String name, List<String> parentScopes) {
                super(name);
                this.parentScopes = parentScopes;
            }

            @NonNull
            @Override
            public String getScopeTag() {
                return name;
            }

            @NonNull
            @Override
            public List<String> getParentScopes() {
                return parentScopes;
            }
        }

        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setScopedServices(new ScopedServices() {
            @Override
            public void bindServices(@NonNull ServiceBinder serviceBinder) {
                if("parentScope1".equals(serviceBinder.getScopeTag())) {
                    serviceBinder.add("service", service1);
                }
            }
        });

        backstackManager.setup(History.of(
                new ChildKey("hello", History.of("parentScope1")),
                new ChildKey("world", History.of("parentScope2", "parentScope3"))
        ));
        backstackManager.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });

        assertThat(backstackManager.hasScope("parentScope1")).isTrue();
        assertThat(backstackManager.hasScope("parentScope2")).isTrue();
        assertThat(backstackManager.hasScope("parentScope3")).isTrue();
        assertThat(backstackManager.hasScope("hello")).isTrue();
        assertThat(backstackManager.hasScope("world")).isTrue();

        assertThat(backstackManager.lookupService("service")).isSameAs(service1);
    }

    @Test
    public void explicitParentsAreDestroyedWhileScopesAreFinalized() {
        Activity activity = Mockito.mock(Activity.class);
        Mockito.when(activity.isFinishing()).thenReturn(true);

        final List<Object> enteredScope = new ArrayList<>();
        final List<Object> exitedScope = new ArrayList<>();

        final List<Object> activated = new ArrayList<>();
        final List<Object> inactivated = new ArrayList<>();
        class Service
                implements ScopedServices.Scoped, ScopedServices.Activated {
            private boolean didEnterScope;
            private boolean didExitScope;
            private boolean didScopeActivate;
            private boolean didScopeInactivate;

            @Override
            public void onEnterScope(@NonNull String scope) {
                this.didEnterScope = true;
                enteredScope.add(this);
            }

            @Override
            public void onExitScope(@NonNull String scope) {
                this.didExitScope = true;
                exitedScope.add(this);
            }

            @Override
            public void onScopeActive(@NonNull String scope) {
                this.didScopeActivate = true;
                activated.add(this);
            }

            @Override
            public void onScopeInactive(@NonNull String scope) {
                this.didScopeInactivate = true;
                inactivated.add(this);
            }
        }
        final Service service = new Service();

        class ChildKey
                extends TestKey
                implements ScopeKey, ScopeKey.Child {
            private final List<String> parentScopes;

            ChildKey(String name, List<String> parentScopes) {
                super(name);
                this.parentScopes = parentScopes;
            }

            @NonNull
            @Override
            public String getScopeTag() {
                return name;
            }

            @NonNull
            @Override
            public List<String> getParentScopes() {
                return parentScopes;
            }
        }

        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        };
        BackstackDelegate backstackDelegate = new BackstackDelegate();
        backstackDelegate.setScopedServices(activity, new ScopedServices() {
            @Override
            public void bindServices(@NonNull ServiceBinder serviceBinder) {
                if("parentScope2".equals(serviceBinder.getScopeTag())) {
                    serviceBinder.add("service", service);
                }
            }
        });
        backstackDelegate.onCreate(null, null, History.of(
                new ChildKey("hello", History.of("parentScope1")),
                new ChildKey("world", History.of("parentScope2", "parentScope3"))
        ));
        backstackDelegate.setStateChanger(stateChanger);

        backstackDelegate.onPostResume();
        backstackDelegate.onPause();

        assertThat(backstackDelegate.hasScope("hello")).isTrue();
        assertThat(backstackDelegate.hasScope("world")).isTrue();
        assertThat(backstackDelegate.hasScope("parentScope1")).isTrue();
        assertThat(backstackDelegate.hasScope("parentScope2")).isTrue();
        assertThat(backstackDelegate.hasScope("parentScope3")).isTrue();
        assertThat(backstackDelegate.hasService("parentScope2", "service")).isTrue();
        assertThat(service.didEnterScope).isTrue();
        assertThat(service.didScopeActivate).isTrue();
        assertThat(service.didScopeInactivate).isFalse();
        assertThat(service.didExitScope).isFalse();
        backstackDelegate.onDestroy();
        assertThat(backstackDelegate.hasScope("hello")).isFalse();
        assertThat(backstackDelegate.hasScope("world")).isFalse();
        assertThat(backstackDelegate.hasScope("parentScope1")).isFalse();
        assertThat(backstackDelegate.hasScope("parentScope2")).isFalse();
        assertThat(backstackDelegate.hasScope("parentScope3")).isFalse();
        assertThat(backstackDelegate.hasService("parentScope2", "service")).isFalse();
        assertThat(service.didEnterScope).isTrue();
        assertThat(service.didScopeActivate).isTrue();
        assertThat(service.didScopeInactivate).isTrue();
        assertThat(service.didExitScope).isTrue();

        assertThat(enteredScope).containsExactly(service);
        assertThat(activated).containsExactly(service);
        assertThat(inactivated).containsExactly(service);
        assertThat(exitedScope).containsExactly(service);
    }

    @Test
    public void explicitParentsAreDestroyedIfNoScopeKeyKeepsThemAlive() {

        final List<Object> enteredScope = new ArrayList<>();
        final List<Object> exitedScope = new ArrayList<>();

        final List<Object> activated = new ArrayList<>();
        final List<Object> inactivated = new ArrayList<>();
        class Service
                implements ScopedServices.Scoped, ScopedServices.Activated {
            private boolean didEnterScope;
            private boolean didExitScope;
            private boolean didScopeActivate;
            private boolean didScopeInactivate;

            @Override
            public void onEnterScope(@NonNull String scope) {
                this.didEnterScope = true;
                enteredScope.add(this);
            }

            @Override
            public void onExitScope(@NonNull String scope) {
                this.didExitScope = true;
                exitedScope.add(this);
            }

            @Override
            public void onScopeActive(@NonNull String scope) {
                this.didScopeActivate = true;
                activated.add(this);
            }

            @Override
            public void onScopeInactive(@NonNull String scope) {
                this.didScopeInactivate = true;
                inactivated.add(this);
            }
        }
        final Service service = new Service();

        class ChildKey
                extends TestKey
                implements ScopeKey, ScopeKey.Child {
            private final List<String> parentScopes;

            ChildKey(String name, List<String> parentScopes) {
                super(name);
                this.parentScopes = parentScopes;
            }

            @NonNull
            @Override
            public String getScopeTag() {
                return name;
            }

            @NonNull
            @Override
            public List<String> getParentScopes() {
                return parentScopes;
            }
        }

        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        };
        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setScopedServices(new ScopedServices() {
            @Override
            public void bindServices(@NonNull ServiceBinder serviceBinder) {
                if("parentScope".equals(serviceBinder.getScopeTag())) {
                    serviceBinder.add("service", service);
                }
            }
        });
        backstackManager.setup(History.of(
                new ChildKey("hello", History.of("parentScope1")),
                new ChildKey("world", History.of("parentScope")),
                new ChildKey("bye", History.of("parentScope"))
        ));
        backstackManager.setStateChanger(stateChanger);

        assertThat(backstackManager.hasScope("parentScope")).isTrue();
        assertThat(backstackManager.lookupService("service")).isSameAs(service);
        assertThat(backstackManager.getService("parentScope", "service")).isSameAs(service);

        backstackManager.getBackstack().goBack();

        assertThat(backstackManager.hasScope("parentScope")).isTrue();
        assertThat(backstackManager.lookupService("service")).isSameAs(service);
        assertThat(backstackManager.getService("parentScope", "service")).isSameAs(service);

        backstackManager.getBackstack().goBack();

        assertThat(backstackManager.hasScope("parentScope")).isFalse();
        assertThat(backstackManager.canFindService("service")).isFalse();
    }

    @Test
    public void explicitParentServicesAreInitializedBeforeActualScopeKeyServices() {
        final List<Object> enteredScope = new ArrayList<>();
        final List<Object> exitedScope = new ArrayList<>();

        final List<Object> activated = new ArrayList<>();
        final List<Object> inactivated = new ArrayList<>();
        class Service
                implements ScopedServices.Scoped, ScopedServices.Activated {
            private boolean didEnterScope;
            private boolean didExitScope;
            private boolean didScopeActivate;
            private boolean didScopeInactivate;

            @Override
            public void onEnterScope(@NonNull String scope) {
                this.didEnterScope = true;
                enteredScope.add(this);
            }

            @Override
            public void onExitScope(@NonNull String scope) {
                this.didExitScope = true;
                exitedScope.add(this);
            }

            @Override
            public void onScopeActive(@NonNull String scope) {
                this.didScopeActivate = true;
                activated.add(this);
            }

            @Override
            public void onScopeInactive(@NonNull String scope) {
                this.didScopeInactivate = true;
                inactivated.add(this);
            }
        }
        final Service service1 = new Service();
        final Service service2 = new Service();

        class ChildKey
                extends TestKey
                implements ScopeKey, ScopeKey.Child {
            private final List<String> parentScopes;

            ChildKey(String name, List<String> parentScopes) {
                super(name);
                this.parentScopes = parentScopes;
            }

            @NonNull
            @Override
            public String getScopeTag() {
                return name;
            }

            @NonNull
            @Override
            public List<String> getParentScopes() {
                return parentScopes;
            }
        }

        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        };
        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setScopedServices(new ScopedServices() {
            @Override
            public void bindServices(@NonNull ServiceBinder serviceBinder) {
                if(serviceBinder.getScopeTag().equals("parentScope")) {
                    serviceBinder.add("service", service1);
                }
                if(serviceBinder.getScopeTag().equals("hello")) {
                    serviceBinder.add("service", service2);
                }
            }
        });
        backstackManager.setup(History.of(
                new ChildKey("hello", History.of("parentScope"))
        ));
        backstackManager.setStateChanger(stateChanger);

        assertThat(backstackManager.hasScope("hello")).isTrue();
        assertThat(backstackManager.hasScope("parentScope")).isTrue();
        assertThat(backstackManager.hasService("hello", "service")).isTrue();
        assertThat(backstackManager.hasService("parentScope", "service")).isTrue();
        assertThat(backstackManager.getService("hello", "service")).isSameAs(service2);
        assertThat(backstackManager.getService("parentScope", "service")).isSameAs(service1);

        assertThat(enteredScope).containsExactly(service1, service2);
        assertThat(activated).containsExactly(service1, service2);

        backstackManager.getBackstack().setHistory(
                History.of(new ChildKey("bye", History.of("boop"))),
                StateChange.BACKWARD);

        assertThat(exitedScope).containsExactly(service2, service1);
        assertThat(inactivated).containsExactly(service2, service1);
    }

    @Test
    public void servicesInExplicitParentsAreDestroyedOnlyAfterAllChildServicesAreDestroyed() {
        final List<Object> enteredScope = new ArrayList<>();
        final List<Object> exitedScope = new ArrayList<>();

        final List<Object> activated = new ArrayList<>();
        final List<Object> inactivated = new ArrayList<>();
        class Service
                implements ScopedServices.Scoped, ScopedServices.Activated {
            private boolean didEnterScope;
            private boolean didExitScope;
            private boolean didScopeActivate;
            private boolean didScopeInactivate;

            @Override
            public void onEnterScope(@NonNull String scope) {
                this.didEnterScope = true;
                enteredScope.add(this);
            }

            @Override
            public void onExitScope(@NonNull String scope) {
                this.didExitScope = true;
                exitedScope.add(this);
            }

            @Override
            public void onScopeActive(@NonNull String scope) {
                this.didScopeActivate = true;
                activated.add(this);
            }

            @Override
            public void onScopeInactive(@NonNull String scope) {
                this.didScopeInactivate = true;
                inactivated.add(this);
            }
        }
        final Service service1 = new Service();
        final Service service2 = new Service();

        final Service service3 = new Service();
        final Service service4 = new Service();

        class ChildKey
                extends TestKey
                implements ScopeKey, ScopeKey.Child {
            private final List<String> parentScopes;

            ChildKey(String name, List<String> parentScopes) {
                super(name);
                this.parentScopes = parentScopes;
            }

            @NonNull
            @Override
            public String getScopeTag() {
                return name;
            }

            @NonNull
            @Override
            public List<String> getParentScopes() {
                return parentScopes;
            }
        }

        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        };

        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setScopedServices(new ScopedServices() {
            @Override
            public void bindServices(@NonNull ServiceBinder serviceBinder) {
                if(serviceBinder.getScopeTag().equals("parentScope1")) {
                    serviceBinder.add("service1", service1);
                }
                if(serviceBinder.getScopeTag().equals("parentScope2")) {
                    serviceBinder.add("service2", service2);
                }

                if(serviceBinder.getScopeTag().equals("hello")) {
                    serviceBinder.add("service3", service3);
                }
                if(serviceBinder.getScopeTag().equals("world")) {
                    serviceBinder.add("service4", service4);
                }
            }
        });
        backstackManager.setup(
                History.of(
                        new ChildKey("hello", History.of("parentScope1", "parentScope2")),
                        new ChildKey("world", History.of("parentScope2"))
                )
        );
        backstackManager.setStateChanger(stateChanger);

        assertThat(backstackManager.hasScope("parentScope1")).isTrue();
        assertThat(backstackManager.hasScope("parentScope2")).isTrue();
        assertThat(backstackManager.hasScope("hello")).isTrue();
        assertThat(backstackManager.hasScope("world")).isTrue();
        assertThat(backstackManager.hasService("parentScope2", "service2")).isTrue();
        assertThat(backstackManager.hasService("parentScope1", "service1")).isTrue();
        assertThat(backstackManager.hasService("world", "service4")).isTrue();
        assertThat(backstackManager.hasService("hello", "service3")).isTrue();

        backstackManager.getBackstack().setHistory(
                History.of(new ChildKey("bye", History.of("boop"))),
                StateChange.BACKWARD);

        assertThat(backstackManager.hasScope("parentScope1")).isFalse();
        assertThat(backstackManager.hasScope("parentScope2")).isFalse();
        assertThat(backstackManager.hasScope("hello")).isFalse();
        assertThat(backstackManager.hasScope("world")).isFalse();
        assertThat(backstackManager.hasService("parentScope2", "service2")).isFalse();
        assertThat(backstackManager.hasService("parentScope1", "service1")).isFalse();
        assertThat(backstackManager.hasService("world", "service4")).isFalse();
        assertThat(backstackManager.hasService("hello", "service3")).isFalse();

        assertThat(enteredScope).containsExactly(service1, service2, service3, service4);
        assertThat(activated).containsExactly(service2, service4);
        assertThat(inactivated).containsExactly(service4, service2);
        assertThat(exitedScope).containsExactly(service4, service3, service2, service1);
    }

    @Test
    public void explicitParentServicesReceiveCallbacksBeforeChildInAscendingOrder() {
        final List<Object> enteredScope = new ArrayList<>();
        final List<Object> exitedScope = new ArrayList<>();

        final List<Object> activated = new ArrayList<>();
        final List<Object> inactivated = new ArrayList<>();
        class Service
                implements ScopedServices.Scoped, ScopedServices.Activated {
            private boolean didEnterScope;
            private boolean didExitScope;
            private boolean didScopeActivate;
            private boolean didScopeInactivate;

            @Override
            public void onEnterScope(@NonNull String scope) {
                this.didEnterScope = true;
                enteredScope.add(this);
            }

            @Override
            public void onExitScope(@NonNull String scope) {
                this.didExitScope = true;
                exitedScope.add(this);
            }

            @Override
            public void onScopeActive(@NonNull String scope) {
                this.didScopeActivate = true;
                activated.add(this);
            }

            @Override
            public void onScopeInactive(@NonNull String scope) {
                this.didScopeInactivate = true;
                inactivated.add(this);
            }
        }
        final Service serviceP0 = new Service();
        final Service serviceP1 = new Service();
        final Service serviceP2 = new Service();
        final Service serviceP3 = new Service();
        final Service serviceP4 = new Service();
        final Service serviceC1 = new Service();
        final Service serviceC2 = new Service();
        final Service serviceC3 = new Service();
        final Service serviceC4 = new Service();
        final Service serviceC5 = new Service();

        class ChildKey
                extends TestKey
                implements ScopeKey, ScopeKey.Child {
            private final List<String> parentScopes;

            ChildKey(String name, List<String> parentScopes) {
                super(name);
                this.parentScopes = parentScopes;
            }

            @NonNull
            @Override
            public String getScopeTag() {
                return name;
            }

            @NonNull
            @Override
            public List<String> getParentScopes() {
                return parentScopes;
            }
        }

        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        };

        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setScopedServices(new ScopedServices() {
            @Override
            public void bindServices(@NonNull ServiceBinder serviceBinder) {
                if(serviceBinder.getScopeTag().equals("P0")) {
                    serviceBinder.add("service", serviceP0);
                }
                if(serviceBinder.getScopeTag().equals("P1")) {
                    serviceBinder.add("service", serviceP1);
                }
                if(serviceBinder.getScopeTag().equals("P2")) {
                    serviceBinder.add("service", serviceP2);
                }
                if(serviceBinder.getScopeTag().equals("P3")) {
                    serviceBinder.add("service", serviceP3);
                }
                if(serviceBinder.getScopeTag().equals("P4")) {
                    serviceBinder.add("service", serviceP4);
                }
                if(serviceBinder.getScopeTag().equals("C1")) {
                    serviceBinder.add("service", serviceC1);
                }
                if(serviceBinder.getScopeTag().equals("C2")) {
                    serviceBinder.add("service", serviceC2);
                }
                if(serviceBinder.getScopeTag().equals("C3")) {
                    serviceBinder.add("service", serviceC3);
                }
                if(serviceBinder.getScopeTag().equals("C4")) {
                    serviceBinder.add("service", serviceC4);
                }
                if(serviceBinder.getScopeTag().equals("C5")) {
                    serviceBinder.add("service", serviceC5);
                }
            }
        });

        /*
         *                    PARENT0
         *            PARENT1
         *     PARENT2       PARENT3         PARENT4
         *   CHILD1 CHILD2    CHILD3     CHILD4   CHILD5
         */
        backstackManager.setup(
                History.of(
                        new ChildKey("C1", History.of("P0", "P1", "P2")),
                        new ChildKey("C2", History.of("P0", "P1", "P2")),
                        new ChildKey("C3", History.of("P0", "P1", "P3")),
                        new ChildKey("C4", History.of("P0", "P4")),
                        new ChildKey("C5", History.of("P0", "P4"))
                )
        );
        backstackManager.setStateChanger(stateChanger);

        assertThat(backstackManager.hasScope("C1")).isTrue();
        assertThat(backstackManager.hasScope("C2")).isTrue();
        assertThat(backstackManager.hasScope("C3")).isTrue();
        assertThat(backstackManager.hasScope("C4")).isTrue();
        assertThat(backstackManager.hasScope("C5")).isTrue();
        assertThat(backstackManager.hasScope("P0")).isTrue();
        assertThat(backstackManager.hasScope("P1")).isTrue();
        assertThat(backstackManager.hasScope("P2")).isTrue();
        assertThat(backstackManager.hasScope("P3")).isTrue();
        assertThat(backstackManager.hasScope("P4")).isTrue();

        assertThat(backstackManager.getService("C1", "service")).isSameAs(serviceC1);
        assertThat(backstackManager.getService("C2", "service")).isSameAs(serviceC2);
        assertThat(backstackManager.getService("C3", "service")).isSameAs(serviceC3);
        assertThat(backstackManager.getService("C4", "service")).isSameAs(serviceC4);
        assertThat(backstackManager.getService("C5", "service")).isSameAs(serviceC5);
        assertThat(backstackManager.getService("P0", "service")).isSameAs(serviceP0);
        assertThat(backstackManager.getService("P1", "service")).isSameAs(serviceP1);
        assertThat(backstackManager.getService("P2", "service")).isSameAs(serviceP2);
        assertThat(backstackManager.getService("P3", "service")).isSameAs(serviceP3);
        assertThat(backstackManager.getService("P4", "service")).isSameAs(serviceP4);

        /// verified set up

        assertThat(enteredScope).containsExactly(serviceP0,
                                                 serviceP1,
                                                 serviceP2,
                                                 serviceC1,
                                                 serviceC2,
                                                 serviceP3,
                                                 serviceC3,
                                                 serviceP4,
                                                 serviceC4,
                                                 serviceC5);
        assertThat(activated).containsExactly(serviceP0, serviceP4, serviceC5);
        assertThat(inactivated).isEmpty();
        assertThat(exitedScope).isEmpty();

        backstackManager.getBackstack().goBack(); // [C1, C2, C3, C4]

        assertThat(activated).containsExactly(serviceP0, serviceP4, serviceC5, serviceC4);
        assertThat(enteredScope).containsExactly(serviceP0,
                                                 serviceP1,
                                                 serviceP2,
                                                 serviceC1,
                                                 serviceC2,
                                                 serviceP3,
                                                 serviceC3,
                                                 serviceP4,
                                                 serviceC4,
                                                 serviceC5);
        assertThat(inactivated).containsExactly(serviceC5);
        assertThat(exitedScope).containsExactly(serviceC5);

        backstackManager.getBackstack().goBack(); // [C1, C2, C3]

        assertThat(activated).containsExactly(serviceP0,
                                              serviceP4,
                                              serviceC5,
                                              serviceC4,
                                              serviceP1,
                                              serviceP3,
                                              serviceC3);
        assertThat(enteredScope).containsExactly(serviceP0,
                                                 serviceP1,
                                                 serviceP2,
                                                 serviceC1,
                                                 serviceC2,
                                                 serviceP3,
                                                 serviceC3,
                                                 serviceP4,
                                                 serviceC4,
                                                 serviceC5);
        assertThat(inactivated).containsExactly(serviceC5, serviceC4, serviceP4);
        assertThat(exitedScope).containsExactly(serviceC5, serviceC4, serviceP4);

        backstackManager.getBackstack().jumpToRoot(); // [C1]

        assertThat(activated).containsExactly(serviceP0,
                                              serviceP4,
                                              serviceC5,
                                              serviceC4,
                                              serviceP1,
                                              serviceP3,
                                              serviceC3,
                                              serviceP2,
                                              serviceC1);
        assertThat(enteredScope).containsExactly(serviceP0,
                                                 serviceP1,
                                                 serviceP2,
                                                 serviceC1,
                                                 serviceC2,
                                                 serviceP3,
                                                 serviceC3,
                                                 serviceP4,
                                                 serviceC4,
                                                 serviceC5);
        assertThat(inactivated).containsExactly(serviceC5, serviceC4, serviceP4, serviceC3, serviceP3);
        assertThat(exitedScope).containsExactly(serviceC5, serviceC4, serviceP4, serviceC3, serviceP3, serviceC2);

        backstackManager.getBackstack().setHistory(History.of(new TestKey("bye")), StateChange.REPLACE); // ["bye"]

        assertThat(activated).containsExactly(serviceP0,
                                              serviceP4,
                                              serviceC5,
                                              serviceC4,
                                              serviceP1,
                                              serviceP3,
                                              serviceC3,
                                              serviceP2,
                                              serviceC1);
        assertThat(enteredScope).containsExactly(serviceP0,
                                                 serviceP1,
                                                 serviceP2,
                                                 serviceC1,
                                                 serviceC2,
                                                 serviceP3,
                                                 serviceC3,
                                                 serviceP4,
                                                 serviceC4,
                                                 serviceC5);
        assertThat(inactivated).containsExactly(serviceC5,
                                                serviceC4,
                                                serviceP4,
                                                serviceC3,
                                                serviceP3,
                                                serviceC1,
                                                serviceP2,
                                                serviceP1,
                                                serviceP0);
        assertThat(exitedScope).containsExactly(serviceC5,
                                                serviceC4,
                                                serviceP4,
                                                serviceC3,
                                                serviceP3,
                                                serviceC2,
                                                serviceC1,
                                                serviceP2,
                                                serviceP1,
                                                serviceP0);
    }

    @Test
    public void explicitParentServicesReceiveCallbacksBeforeChildInAscendingOrderOtherSetup() {
        final List<Object> enteredScope = new ArrayList<>();
        final List<Object> exitedScope = new ArrayList<>();

        final List<Object> activated = new ArrayList<>();
        final List<Object> inactivated = new ArrayList<>();
        class Service
                implements ScopedServices.Scoped, ScopedServices.Activated {
            private boolean didEnterScope;
            private boolean didExitScope;
            private boolean didScopeActivate;
            private boolean didScopeInactivate;

            @Override
            public void onEnterScope(@NonNull String scope) {
                this.didEnterScope = true;
                enteredScope.add(this);
            }

            @Override
            public void onExitScope(@NonNull String scope) {
                this.didExitScope = true;
                exitedScope.add(this);
            }

            @Override
            public void onScopeActive(@NonNull String scope) {
                this.didScopeActivate = true;
                activated.add(this);
            }

            @Override
            public void onScopeInactive(@NonNull String scope) {
                this.didScopeInactivate = true;
                inactivated.add(this);
            }
        }
        final Service serviceP0 = new Service();
        final Service serviceP1 = new Service();
        final Service serviceP2 = new Service();
        final Service serviceP3 = new Service();
        final Service serviceP4 = new Service();
        final Service serviceC1 = new Service();
        final Service serviceC2 = new Service();
        final Service serviceC3 = new Service();
        final Service serviceC4 = new Service();
        final Service serviceC5 = new Service();

        class ChildKey
                extends TestKey
                implements ScopeKey, ScopeKey.Child {
            private final List<String> parentScopes;

            ChildKey(String name, List<String> parentScopes) {
                super(name);
                this.parentScopes = parentScopes;
            }

            @NonNull
            @Override
            public String getScopeTag() {
                return name;
            }

            @NonNull
            @Override
            public List<String> getParentScopes() {
                return parentScopes;
            }
        }

        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        };

        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setScopedServices(new ScopedServices() {
            @Override
            public void bindServices(@NonNull ServiceBinder serviceBinder) {
                if(serviceBinder.getScopeTag().equals("P0")) {
                    serviceBinder.add("service", serviceP0);
                }
                if(serviceBinder.getScopeTag().equals("P1")) {
                    serviceBinder.add("service", serviceP1);
                }
                if(serviceBinder.getScopeTag().equals("P2")) {
                    serviceBinder.add("service", serviceP2);
                }
                if(serviceBinder.getScopeTag().equals("P3")) {
                    serviceBinder.add("service", serviceP3);
                }
                if(serviceBinder.getScopeTag().equals("P4")) {
                    serviceBinder.add("service", serviceP4);
                }
                if(serviceBinder.getScopeTag().equals("C1")) {
                    serviceBinder.add("service", serviceC1);
                }
                if(serviceBinder.getScopeTag().equals("C2")) {
                    serviceBinder.add("service", serviceC2);
                }
                if(serviceBinder.getScopeTag().equals("C3")) {
                    serviceBinder.add("service", serviceC3);
                }
                if(serviceBinder.getScopeTag().equals("C4")) {
                    serviceBinder.add("service", serviceC4);
                }
                if(serviceBinder.getScopeTag().equals("C5")) {
                    serviceBinder.add("service", serviceC5);
                }
            }
        });

        /*
         *                    PARENT0       PARENT4
         *            PARENT1           CHILD4   CHILD5
         *     PARENT2       PARENT3
         *   CHILD1 CHILD2    CHILD3
         */
        backstackManager.setup(
                History.of(
                        new ChildKey("C1", History.of("P0", "P1", "P2")),
                        new ChildKey("C2", History.of("P0", "P1", "P2")),
                        new ChildKey("C3", History.of("P0", "P1", "P3")),
                        new ChildKey("C4", History.of("P4")),
                        new ChildKey("C5", History.of("P4"))
                )
        );
        backstackManager.setStateChanger(stateChanger);

        assertThat(backstackManager.hasScope("C1")).isTrue();
        assertThat(backstackManager.hasScope("C2")).isTrue();
        assertThat(backstackManager.hasScope("C3")).isTrue();
        assertThat(backstackManager.hasScope("C4")).isTrue();
        assertThat(backstackManager.hasScope("C5")).isTrue();
        assertThat(backstackManager.hasScope("P0")).isTrue();
        assertThat(backstackManager.hasScope("P1")).isTrue();
        assertThat(backstackManager.hasScope("P2")).isTrue();
        assertThat(backstackManager.hasScope("P3")).isTrue();
        assertThat(backstackManager.hasScope("P4")).isTrue();

        assertThat(backstackManager.getService("C1", "service")).isSameAs(serviceC1);
        assertThat(backstackManager.getService("C2", "service")).isSameAs(serviceC2);
        assertThat(backstackManager.getService("C3", "service")).isSameAs(serviceC3);
        assertThat(backstackManager.getService("C4", "service")).isSameAs(serviceC4);
        assertThat(backstackManager.getService("C5", "service")).isSameAs(serviceC5);
        assertThat(backstackManager.getService("P0", "service")).isSameAs(serviceP0);
        assertThat(backstackManager.getService("P1", "service")).isSameAs(serviceP1);
        assertThat(backstackManager.getService("P2", "service")).isSameAs(serviceP2);
        assertThat(backstackManager.getService("P3", "service")).isSameAs(serviceP3);
        assertThat(backstackManager.getService("P4", "service")).isSameAs(serviceP4);

        /// verified set up

        assertThat(enteredScope).containsExactly(serviceP0,
                                                 serviceP1,
                                                 serviceP2,
                                                 serviceC1,
                                                 serviceC2,
                                                 serviceP3,
                                                 serviceC3,
                                                 serviceP4,
                                                 serviceC4,
                                                 serviceC5);
        assertThat(activated).containsExactly(serviceP4, serviceC5);
        assertThat(inactivated).isEmpty();
        assertThat(exitedScope).isEmpty();

        backstackManager.getBackstack().goBack(); // [C1, C2, C3, C4]

        assertThat(activated).containsExactly(serviceP4, serviceC5, serviceC4);
        assertThat(enteredScope).containsExactly(serviceP0,
                                                 serviceP1,
                                                 serviceP2,
                                                 serviceC1,
                                                 serviceC2,
                                                 serviceP3,
                                                 serviceC3,
                                                 serviceP4,
                                                 serviceC4,
                                                 serviceC5);
        assertThat(inactivated).containsExactly(serviceC5);
        assertThat(exitedScope).containsExactly(serviceC5);

        backstackManager.getBackstack().goBack(); // [C1, C2, C3]

        assertThat(activated).containsExactly(serviceP4,
                                              serviceC5,
                                              serviceC4,
                                              serviceP0,
                                              serviceP1,
                                              serviceP3,
                                              serviceC3);
        assertThat(enteredScope).containsExactly(serviceP0,
                                                 serviceP1,
                                                 serviceP2,
                                                 serviceC1,
                                                 serviceC2,
                                                 serviceP3,
                                                 serviceC3,
                                                 serviceP4,
                                                 serviceC4,
                                                 serviceC5);
        assertThat(inactivated).containsExactly(serviceC5, serviceC4, serviceP4);
        assertThat(exitedScope).containsExactly(serviceC5, serviceC4, serviceP4);

        backstackManager.getBackstack().jumpToRoot(); // [C1]

        assertThat(activated).containsExactly(serviceP4,
                                              serviceC5,
                                              serviceC4,
                                              serviceP0,
                                              serviceP1,
                                              serviceP3,
                                              serviceC3,
                                              serviceP2,
                                              serviceC1);
        assertThat(enteredScope).containsExactly(serviceP0,
                                                 serviceP1,
                                                 serviceP2,
                                                 serviceC1,
                                                 serviceC2,
                                                 serviceP3,
                                                 serviceC3,
                                                 serviceP4,
                                                 serviceC4,
                                                 serviceC5);
        assertThat(inactivated).containsExactly(serviceC5, serviceC4, serviceP4, serviceC3, serviceP3);
        assertThat(exitedScope).containsExactly(serviceC5, serviceC4, serviceP4, serviceC3, serviceP3, serviceC2);

        backstackManager.getBackstack().setHistory(History.of(new TestKey("bye")), StateChange.REPLACE); // ["bye"]

        assertThat(activated).containsExactly(serviceP4,
                                              serviceC5,
                                              serviceC4,
                                              serviceP0,
                                              serviceP1,
                                              serviceP3,
                                              serviceC3,
                                              serviceP2,
                                              serviceC1);
        assertThat(enteredScope).containsExactly(serviceP0,
                                                 serviceP1,
                                                 serviceP2,
                                                 serviceC1,
                                                 serviceC2,
                                                 serviceP3,
                                                 serviceC3,
                                                 serviceP4,
                                                 serviceC4,
                                                 serviceC5);
        assertThat(inactivated).containsExactly(serviceC5,
                                                serviceC4,
                                                serviceP4,
                                                serviceC3,
                                                serviceP3,
                                                serviceC1,
                                                serviceP2,
                                                serviceP1,
                                                serviceP0);
        assertThat(exitedScope).containsExactly(serviceC5,
                                                serviceC4,
                                                serviceP4,
                                                serviceC3,
                                                serviceP3,
                                                serviceC2,
                                                serviceC1,
                                                serviceP2,
                                                serviceP1,
                                                serviceP0);

        backstackManager.getBackstack().setHistory(History.of(new ChildKey("C5", History.of("P4"))),
                                                   StateChange.REPLACE);

        assertThat(activated).containsExactly(serviceP4,
                                              serviceC5,
                                              serviceC4,
                                              serviceP0,
                                              serviceP1,
                                              serviceP3,
                                              serviceC3,
                                              serviceP2,
                                              serviceC1,
                                              serviceP4,
                                              serviceC5);
        assertThat(enteredScope).containsExactly(serviceP0,
                                                 serviceP1,
                                                 serviceP2,
                                                 serviceC1,
                                                 serviceC2,
                                                 serviceP3,
                                                 serviceC3,
                                                 serviceP4,
                                                 serviceC4,
                                                 serviceC5,
                                                 serviceP4,
                                                 serviceC5);
        assertThat(inactivated).containsExactly(serviceC5,
                                                serviceC4,
                                                serviceP4,
                                                serviceC3,
                                                serviceP3,
                                                serviceC1,
                                                serviceP2,
                                                serviceP1,
                                                serviceP0);
        assertThat(exitedScope).containsExactly(serviceC5,
                                                serviceC4,
                                                serviceP4,
                                                serviceC3,
                                                serviceP3,
                                                serviceC2,
                                                serviceC1,
                                                serviceP2,
                                                serviceP1,
                                                serviceP0);

    }

    @Test
    public void explicitParentsAreCreatedEvenIfThereAreNoScopeKeys() {
        final List<Object> enteredScope = new ArrayList<>();
        final List<Object> exitedScope = new ArrayList<>();

        final List<Object> activated = new ArrayList<>();
        final List<Object> inactivated = new ArrayList<>();

        class Service
                implements ScopedServices.Scoped, ScopedServices.Activated {
            private boolean didEnterScope;
            private boolean didExitScope;
            private boolean didScopeActivate;
            private boolean didScopeInactivate;

            @Override
            public void onEnterScope(@NonNull String scope) {
                this.didEnterScope = true;
                enteredScope.add(this);
            }

            @Override
            public void onExitScope(@NonNull String scope) {
                this.didExitScope = true;
                exitedScope.add(this);
            }

            @Override
            public void onScopeActive(@NonNull String scope) {
                this.didScopeActivate = true;
                activated.add(this);
            }

            @Override
            public void onScopeInactive(@NonNull String scope) {
                this.didScopeInactivate = true;
                inactivated.add(this);
            }
        }

        class ChildKey
                extends TestKey
                implements ScopeKey.Child {
            private final List<String> parentScopes;

            ChildKey(String name, List<String> parentScopes) {
                super(name);
                this.parentScopes = parentScopes;
            }

            @NonNull
            @Override
            public List<String> getParentScopes() {
                return parentScopes;
            }
        }

        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        };

        final Service service1 = new Service();
        final Service service2 = new Service();

        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setScopedServices(new ScopedServices() {
            @Override
            public void bindServices(@NonNull ServiceBinder serviceBinder) {
                if(serviceBinder.getScopeTag().equals("boop")) {
                    serviceBinder.add("service", service1);
                }
                if(serviceBinder.getScopeTag().equals("beep")) {
                    serviceBinder.add("service", service2);
                }
            }
        });

        backstackManager.setup(History.of(
                new ChildKey("hello", History.of("boop")),
                new ChildKey("world", History.of("beep"))
        ));
        backstackManager.setStateChanger(stateChanger);

        assertThat(backstackManager.hasScope("boop")).isTrue();
        assertThat(backstackManager.hasScope("beep")).isTrue();
        assertThat(backstackManager.getService("boop", "service")).isSameAs(service1);
        assertThat(backstackManager.getService("beep", "service")).isSameAs(service2);

        assertThat(backstackManager.lookupService("service")).isSameAs(service2);

        backstackManager.getBackstack().goBack();

        assertThat(backstackManager.hasScope("boop")).isTrue();
        assertThat(backstackManager.hasScope("beep")).isFalse();
        assertThat(backstackManager.getService("boop", "service")).isSameAs(service1);
        assertThat(backstackManager.lookupService("service")).isSameAs(service1);
    }
}
