package com.zhuinden.simplestack;

import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.zhuinden.simplestack.helpers.Action;
import com.zhuinden.simplestack.helpers.HasParentServices;
import com.zhuinden.simplestack.helpers.ServiceProvider;
import com.zhuinden.simplestack.helpers.TestKeyWithScope;
import com.zhuinden.statebundle.StateBundle;

import org.junit.Test;

import java.util.List;

import static com.zhuinden.simplestack.helpers.AssertionHelper.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;

public class ScopingAliasTest {
    @Test
    public void aliasesWork() {
        Backstack backstack = new Backstack();
        backstack.setScopedServices(new ServiceProvider());

        final Object service = new Object();

        TestKeyWithScope boop = new TestKeyWithScope("boop") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
                serviceBinder.addService("service", service);

                assertThat(serviceBinder.hasService("alias")).isFalse();
                serviceBinder.addAlias("alias", service);
                assertThat(serviceBinder.hasService("alias")).isTrue();
            }
        };

        backstack.setup(History.of(boop));

        assertThat(backstack.hasService("boop", "service")).isFalse();
        assertThat(backstack.hasService("boop", "alias")).isFalse();

        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });

        // then
        assertThat(backstack.hasService("boop", "service")).isTrue();
        assertThat(backstack.hasService("boop", "alias")).isTrue();

        assertThat(backstack.getService("boop", "service")).isSameAs(service);
        assertThat(backstack.getService("boop", "alias")).isSameAs(service);

        assertThat(backstack.canFindFromScope("boop", "alias")).isTrue();
        assertThat(backstack.canFindFromScope("boop", "alias", ScopeLookupMode.ALL)).isTrue();
        assertThat(backstack.canFindFromScope("boop", "alias", ScopeLookupMode.EXPLICIT)).isTrue();
        assertThat(backstack.canFindService("alias")).isTrue();

        assertThat(backstack.lookupFromScope("boop", "alias")).isSameAs(service);
        assertThat(backstack.lookupFromScope("boop", "alias", ScopeLookupMode.ALL)).isSameAs(service);
        assertThat(backstack.lookupFromScope("boop", "alias", ScopeLookupMode.EXPLICIT)).isSameAs(service);
        assertThat(backstack.lookupService("alias")).isSameAs(service);
    }

    @Test
    public void aliasesWorkInMultipleScopes() {
        final Backstack backstack = new Backstack();
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

        final Object serviceShared0123P1P2P3 = new MyService("serviceShared0123P1P2P3");

        backstack.setGlobalServices(GlobalServices.builder()
                .addService("service0", service0)
                .addService("serviceShared0123P1P2P3", serviceShared0123P1P2P3)
                .addAlias("alias0", service0)
                .addAlias("aliasShared0123P1P2P3", serviceShared0123P1P2P3)
                .build());

        final Object service1 = new MyService("service1");
        final Object service2 = new MyService("service2");
        final Object service3 = new MyService("service3");

        final Object serviceShared12 = new MyService("serviceShared12");
        final Object serviceShared13 = new MyService("serviceShared13");
        final Object serviceShared23 = new MyService("serviceShared23");
        final Object serviceShared123 = new MyService("serviceShared123");
        final Object serviceShared1P1 = new MyService("serviceShared1P1");
        final Object serviceShared1P2 = new MyService("serviceShared1P2");
        final Object serviceShared1P3 = new MyService("serviceShared1P3");
        final Object serviceShared2P1 = new MyService("serviceShared2P1");
        final Object serviceShared2P2 = new MyService("serviceShared2P2");
        final Object serviceShared2P3 = new MyService("serviceShared2P3");
        final Object serviceShared3P1 = new MyService("serviceShared3P1");
        final Object serviceShared3P2 = new MyService("serviceShared3P2");
        final Object serviceShared3P3 = new MyService("serviceShared3P3");

        final Object serviceP1 = new MyService("serviceP1");
        final Object serviceP2 = new MyService("serviceP2");
        final Object serviceP3 = new MyService("serviceP3");

        TestKeyWithScope beep = new TestKeyWithScope("scope1") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
                assertThat(serviceBinder.getScopeTag()).isEqualTo(getScopeTag());

                serviceBinder.addService("service1", service1);

                serviceBinder.addService("serviceShared12", serviceShared12);
                serviceBinder.addService("serviceShared13", serviceShared13);
                serviceBinder.addService("serviceShared123", serviceShared123);
                serviceBinder.addService("serviceShared1P1", serviceShared1P1);
                serviceBinder.addService("serviceShared1P2", serviceShared1P2);
                serviceBinder.addService("serviceShared1P3", serviceShared1P3);
                serviceBinder.addService("serviceShared0123P1P2P3", serviceShared0123P1P2P3);
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
                if("parent1".equals(serviceBinder.getScopeTag())) {
                    serviceBinder.addService("serviceP1", serviceP1);
                    serviceBinder.addService("serviceShared1P1", serviceShared1P1);
                    serviceBinder.addService("serviceShared2P1", serviceShared2P1);
                    serviceBinder.addService("serviceShared3P1", serviceShared3P1);
                    serviceBinder.addService("serviceShared0123P1P2P3", serviceShared0123P1P2P3);

                    serviceBinder.addAlias("aliasP1", serviceP1);
                    serviceBinder.addAlias("aliasShared1P1", serviceShared1P1);
                    serviceBinder.addAlias("aliasShared2P1", serviceShared2P1);
                    serviceBinder.addAlias("aliasShared3P1", serviceShared3P1);
                    serviceBinder.addAlias("aliasShared0123P1P2P3", serviceShared0123P1P2P3);
                }
                if("parent2".equals(serviceBinder.getScopeTag())) {
                    serviceBinder.addService("serviceP2", serviceP2);
                    serviceBinder.addService("serviceShared1P2", serviceShared1P2);
                    serviceBinder.addService("serviceShared2P2", serviceShared2P2);
                    serviceBinder.addService("serviceShared3P2", serviceShared3P2);
                    serviceBinder.addService("serviceShared0123P1P2P3", serviceShared0123P1P2P3);

                    serviceBinder.addService("serviceP2", serviceP2);
                    serviceBinder.addService("serviceShared1P2", serviceShared1P2);
                    serviceBinder.addService("serviceShared2P2", serviceShared2P2);
                    serviceBinder.addService("serviceShared3P2", serviceShared3P2);
                    serviceBinder.addService("serviceShared0123P1P2P3", serviceShared0123P1P2P3);
                }
            }

            @Override
            void bindOwnServices(ServiceBinder serviceBinder) {
                serviceBinder.addService("service2", service2);

                serviceBinder.addService("serviceShared12", serviceShared12);
                serviceBinder.addService("serviceShared23", serviceShared23);
                serviceBinder.addService("serviceShared123", serviceShared123);
                serviceBinder.addService("serviceShared2P1", serviceShared2P1);
                serviceBinder.addService("serviceShared2P2", serviceShared2P2);
                serviceBinder.addService("serviceShared2P3", serviceShared2P3);
                serviceBinder.addService("serviceShared0123P1P2P3", serviceShared0123P1P2P3);
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
                if("parent1".equals(serviceBinder.getScopeTag())) {
                    serviceBinder.addService("serviceP1", serviceP1);
                    serviceBinder.addService("serviceShared1P1", serviceShared1P1);
                    serviceBinder.addService("serviceShared2P1", serviceShared2P1);
                    serviceBinder.addService("serviceShared3P1", serviceShared3P1);
                    serviceBinder.addService("serviceShared0123P1P2P3", serviceShared0123P1P2P3);
                }
                if("parent3".equals(serviceBinder.getScopeTag())) {
                    serviceBinder.addService("serviceP3", serviceP3);
                    serviceBinder.addService("serviceShared1P3", serviceShared1P3);
                    serviceBinder.addService("serviceShared2P3", serviceShared2P3);
                    serviceBinder.addService("serviceShared3P3", serviceShared3P3);
                    serviceBinder.addService("serviceShared0123P1P2P3", serviceShared0123P1P2P3);
                }
            }

            @Override
            void bindOwnServices(ServiceBinder serviceBinder) {
                serviceBinder.addService("service3", service3);

                serviceBinder.addService("serviceShared13", serviceShared13);
                serviceBinder.addService("serviceShared23", serviceShared23);
                serviceBinder.addService("serviceShared123", serviceShared123);
                serviceBinder.addService("serviceShared3P1", serviceShared3P1);
                serviceBinder.addService("serviceShared3P2", serviceShared3P2);
                serviceBinder.addService("serviceShared3P3", serviceShared3P3);
                serviceBinder.addService("serviceShared0123P1P2P3", serviceShared0123P1P2P3);
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

        // then
        assertThat(backstack.hasService("parent1", "serviceP1")).isTrue();
        assertThat(backstack.hasService("parent1", "aliasP1")).isTrue();

        assertThat(backstack.canFindFromScope("parent1", "aliasP1")).isTrue();
        assertThat(backstack.canFindFromScope("parent1", "aliasP1", ScopeLookupMode.ALL)).isTrue();
        assertThat(backstack.canFindFromScope("parent1", "aliasP1", ScopeLookupMode.EXPLICIT)).isTrue();
        assertThat(backstack.canFindService("aliasP1")).isTrue();

        assertThat(backstack.lookupFromScope("parent1", "aliasP1")).isSameAs(serviceP1);
        assertThat(backstack.lookupFromScope("parent1", "aliasP1", ScopeLookupMode.ALL)).isSameAs(serviceP1);
        assertThat(backstack.lookupFromScope("parent1", "aliasP1", ScopeLookupMode.EXPLICIT)).isSameAs(serviceP1);
        assertThat(backstack.lookupService("aliasP1")).isSameAs(serviceP1);

        // then
        assertThat(backstack.hasService(ScopeManager.GLOBAL_SCOPE_TAG, "service0")).isTrue();
        assertThat(backstack.hasService(ScopeManager.GLOBAL_SCOPE_TAG, "alias0")).isTrue();

        assertThat(backstack.canFindFromScope(ScopeManager.GLOBAL_SCOPE_TAG, "alias0")).isTrue();
        assertThat(backstack.canFindFromScope(ScopeManager.GLOBAL_SCOPE_TAG, "alias0", ScopeLookupMode.ALL)).isTrue();
        assertThat(backstack.canFindFromScope(ScopeManager.GLOBAL_SCOPE_TAG, "alias0", ScopeLookupMode.EXPLICIT)).isTrue();
        assertThat(backstack.canFindService("alias0")).isTrue();

        assertThat(backstack.lookupFromScope(ScopeManager.GLOBAL_SCOPE_TAG, "alias0")).isSameAs(service0);
        assertThat(backstack.lookupFromScope(ScopeManager.GLOBAL_SCOPE_TAG, "alias0", ScopeLookupMode.ALL)).isSameAs(service0);
        assertThat(backstack.lookupFromScope(ScopeManager.GLOBAL_SCOPE_TAG, "alias0", ScopeLookupMode.EXPLICIT)).isSameAs(service0);
        assertThat(backstack.lookupService("alias0")).isSameAs(service0);

        // ALSO

        backstack.goBack();

        backstack.goBack();

        // then
        assertThat(backstack.hasService("parent1", "serviceP1")).isFalse();
        assertThat(backstack.hasService("parent1", "aliasP1")).isFalse();

        assertThat(backstack.canFindFromScope("parent1", "aliasP1")).isFalse();
        assertThat(backstack.canFindFromScope("parent1", "aliasP1", ScopeLookupMode.ALL)).isFalse();
        assertThat(backstack.canFindFromScope("parent1", "aliasP1", ScopeLookupMode.EXPLICIT)).isFalse();
        assertThat(backstack.canFindService("aliasP1")).isFalse();

        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstack.lookupFromScope("parent1", "aliasP1");
            }
        });

        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstack.lookupFromScope("parent1", "aliasP1", ScopeLookupMode.ALL);
            }
        });

        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstack.lookupFromScope("parent1", "aliasP1", ScopeLookupMode.EXPLICIT);
            }
        });

        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstack.lookupFromScope("parent1", "aliasP1", ScopeLookupMode.ALL);
            }
        });

        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstack.lookupFromScope("parent1", "aliasP1", ScopeLookupMode.EXPLICIT);
            }
        });

        assertThrows(new Action() {
            @Override
            public void doSomething() {
                backstack.lookupService("aliasP1");
            }
        });
    }

    @Test
    public void bundleableNotCalledForAlias() {
        Backstack backstack = new Backstack();
        backstack.setScopedServices(new ServiceProvider());

        class Service implements Bundleable {
            private int saved = 0;
            private int restored = 0;

            @NonNull
            @Override
            public StateBundle toBundle() {
                saved++;
                return new StateBundle();
            }

            @Override
            public void fromBundle(@Nullable StateBundle bundle) {
                if(bundle != null) {
                    restored++;
                }
            }
        }
        final Service service = new Service();

        TestKeyWithScope boop = new TestKeyWithScope("boop") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
                serviceBinder.addService("service", service);
                serviceBinder.addAlias("alias", service);
            }
        };

        backstack.setup(History.of(boop));
        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });

        assertThat(backstack.hasService("boop", "service")).isTrue();
        assertThat(backstack.hasService("boop", "alias")).isTrue();

        StateBundle bundle = backstack.toBundle();

        Backstack backstack2 = new Backstack();
        backstack2.setScopedServices(new ServiceProvider());
        backstack2.setup(History.of(boop));
        backstack2.fromBundle(bundle);

        backstack2.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });

        assertThat(service.saved).isEqualTo(1);
        assertThat(service.restored).isEqualTo(1);
    }

    // @Test // TODO (ALIAS)
    public void aliasCannotBeAddedForServicesNotYetAdded() {
        Backstack backstack = new Backstack();
        backstack.setScopedServices(new ServiceProvider());

        final Object service = new Object();

        TestKeyWithScope boop = new TestKeyWithScope("boop") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
                serviceBinder.addAlias("alias", service);
            }
        };

        backstack.setup(History.of(boop));

        try {
            backstack.setStateChanger(new StateChanger() {
                @Override
                public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                    completionCallback.stateChangeComplete();
                }
            });
            // Assert.fail("Alias should be disallowed for services that are not yet added to any scopes"); // TODO (ALIAS): this restriction is not yet supported
        } catch(IllegalStateException e) {
            // assertThat(e.getMessage()).contains("A service should be added to a scope before it is bound to aliases");
        }
    }
}