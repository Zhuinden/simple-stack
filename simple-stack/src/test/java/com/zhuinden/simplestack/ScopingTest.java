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
            implements Bundleable, ScopedServices.Scoped {
        int blah = 2;

        boolean didEnterScope = false;
        boolean didExitScope = false;

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
        public void onEnterScope(@NonNull String scope) {
            didEnterScope = true;
        }

        @Override
        public void onExitScope(@NonNull String scope) {
            didExitScope = true;
        }
    }

    StateChanger stateChanger = new StateChanger() {
        @Override
        public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
            completionCallback.stateChangeComplete();
        }
    };

    public interface HasServices {
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
            return "boop";
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

        assertThat(service.didEnterScope).isFalse();
        assertThat(service.didExitScope).isFalse();

        backstackManager.setStateChanger(stateChanger);

        assertThat(service.didEnterScope).isTrue();
        assertThat(service.didExitScope).isFalse();

        backstackManager.getBackstack().setHistory(History.single(testKey1), StateChange.REPLACE);

        assertThat(service.didEnterScope).isTrue();
        assertThat(service.didExitScope).isTrue();
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
        assertThat(service.didEnterScope).isTrue();
        assertThat(service.didExitScope).isFalse();
        backstackDelegate.onDestroy();
        assertThat(backstackDelegate.hasScope("beep")).isFalse();
        assertThat(backstackDelegate.hasService(testKeyWithScope, SERVICE_TAG)).isFalse();
        assertThat(service.didEnterScope).isTrue();
        assertThat(service.didExitScope).isTrue();
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

        final List<Object> enteredScope = new ArrayList<>();
        final List<Object> exitedScope = new ArrayList<>();

        class MyService implements ScopedServices.Scoped {
            @Override
            public void onEnterScope(@NonNull String scope) {
                enteredScope.add(this);
            }

            @Override
            public void onExitScope(@NonNull String scope) {
                exitedScope.add(this);
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

        assertThat(enteredScope).isEmpty();
        assertThat(exitedScope).isEmpty();
        backstackManager.setStateChanger(stateChanger);

        assertThat(enteredScope).containsExactly(service1, service2);
        assertThat(exitedScope).isEmpty();

        backstackManager.getBackstack().setHistory(History.of(bye), StateChange.REPLACE);

        assertThat(enteredScope).containsExactly(service1, service2);
        assertThat(exitedScope).containsExactly(service2, service1);
    }

    @Test
    public void serviceCreationAndDestructionHappensInForwardAndReverseOrder() {
        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setScopedServices(new ServiceProvider());

        final List<Object> enteredScope = new ArrayList<>();
        final List<Object> exitedScope = new ArrayList<>();

        class MyService implements ScopedServices.Scoped {
            @Override
            public void onEnterScope(@NonNull String scope) {
                enteredScope.add(this);
            }

            @Override
            public void onExitScope(@NonNull String scope) {
                exitedScope.add(this);
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

        assertThat(enteredScope).isEmpty();
        assertThat(exitedScope).isEmpty();
        backstackManager.setStateChanger(stateChanger);

        assertThat(enteredScope).containsExactly(service1, service2);
        assertThat(exitedScope).isEmpty();

        backstackManager.getBackstack().setHistory(History.of(bye), StateChange.REPLACE);

        assertThat(enteredScope).containsExactly(service1, service2);
        assertThat(exitedScope).containsExactly(service2, service1);
    }

    @Test
    public void scopedServicesCanRetrieveBackstackFromServiceBinder() {
        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setScopedServices(new ServiceProvider());

        final AtomicReference<Backstack> backstack = new AtomicReference<>();

        class MyService implements ScopedServices.Scoped {
            @Override
            public void onEnterScope(@NonNull String scope) {
            }

            @Override
            public void onExitScope(@NonNull String scope) {
            }
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

        final List<Object> activatedScope = new ArrayList<>();
        final List<Object> deactivatedScope = new ArrayList<>();

        class MyService
                implements ScopedServices.Activated {
            @Override
            public void onScopeActive(@NonNull String scope) {
                activatedScope.add(this);
            }

            @Override
            public void onScopeInactive(@NonNull String scope) {
                deactivatedScope.add(this);
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

        assertThat(activatedScope).isEmpty();
        assertThat(deactivatedScope).isEmpty();
        backstackManager.setStateChanger(stateChanger);

        assertThat(activatedScope).containsExactly(service2);
        assertThat(deactivatedScope).isEmpty();

        backstackManager.getBackstack().goBack();

        assertThat(activatedScope).containsExactly(service2, service1);
        assertThat(deactivatedScope).containsExactly(service2);

        backstackManager.getBackstack().setHistory(History.of(bye), StateChange.REPLACE);

        assertThat(activatedScope).containsExactly(service2, service1);
        assertThat(deactivatedScope).containsExactly(service2, service1);
    }

    @Test
    public void activatedIsCalledInRightOrder() {
        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setScopedServices(new ServiceProvider());

        final List<Object> activatedScope = new ArrayList<>();
        final List<Object> deactivatedScope = new ArrayList<>();

        class MyService
                implements ScopedServices.Activated {
            @Override
            public void onScopeActive(@NonNull String scope) {
                activatedScope.add(this);
            }

            @Override
            public void onScopeInactive(@NonNull String scope) {
                deactivatedScope.add(this);
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

        assertThat(activatedScope).isEmpty();
        backstackManager.setStateChanger(stateChanger);

        assertThat(activatedScope).containsExactly(service1, service2, service3);

        backstackManager.getBackstack().goTo(boop);
        assertThat(activatedScope).containsExactly(service1, service2, service3, service4, service5, service6);
        assertThat(deactivatedScope).containsExactly(service3, service2, service1);
    }

    @Test
    public void deactivatedIsCalledInReverseOrder() {
        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setScopedServices(new ServiceProvider());

        final List<Object> deactivatedScope = new ArrayList<>();

        class MyService
                implements ScopedServices.Activated {
            @Override
            public void onScopeActive(@NonNull String scope) {
            }

            @Override
            public void onScopeInactive(@NonNull String scope) {
                deactivatedScope.add(this);
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

        assertThat(deactivatedScope).isEmpty();
        backstackManager.setStateChanger(stateChanger);

        backstackManager.getBackstack().goTo(boop);

        assertThat(deactivatedScope).containsExactly(service3, service2, service1);

        TestKey bye = new TestKey("bye");
        backstackManager.getBackstack().setHistory(History.of(bye), StateChange.REPLACE);

        assertThat(deactivatedScope).containsExactly(service3, service2, service1, service6, service5, service4);
    }

    @Test
    public void activationIsCalledOnlyOnce() {
        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setScopedServices(new ServiceProvider());

        final List<Object> activatedScope = new ArrayList<>();
        final List<Object> deactivatedScope = new ArrayList<>();

        class MyService
                implements ScopedServices.Activated {
            @Override
            public void onScopeActive(@NonNull String scope) {
                activatedScope.add(this);
            }

            @Override
            public void onScopeInactive(@NonNull String scope) {
                deactivatedScope.add(this);
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

        assertThat(activatedScope).isEmpty();
        assertThat(deactivatedScope).isEmpty();
        backstackManager.setStateChanger(stateChanger);

        assertThat(activatedScope).containsExactly(service1);

        backstackManager.getBackstack().removeStateChanger();
        backstackManager.setStateChanger(stateChanger);

        assertThat(activatedScope).containsExactly(service1);
    }

    @Test
    public void deactivationIsCalledOnlyOnce() {
        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setScopedServices(new ServiceProvider());

        final List<Object> activatedScope = new ArrayList<>();
        final List<Object> deactivatedScope = new ArrayList<>();

        class MyService
                implements ScopedServices.Activated {
            @Override
            public void onScopeActive(@NonNull String scope) {
                activatedScope.add(this);
            }

            @Override
            public void onScopeInactive(@NonNull String scope) {
                deactivatedScope.add(this);
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

        assertThat(activatedScope).isEmpty();
        assertThat(deactivatedScope).isEmpty();
        backstackManager.setStateChanger(stateChanger);

        assertThat(activatedScope).containsExactly(service1);
        assertThat(deactivatedScope).isEmpty();

        backstackManager.getBackstack().removeStateChanger();
        assertThat(deactivatedScope).isEmpty();

        backstackManager.getBackstack().setHistory(History.of(bye), StateChange.REPLACE);
        assertThat(deactivatedScope).isEmpty();

        backstackManager.setStateChanger(stateChanger);
        assertThat(deactivatedScope).containsExactly(service1);

        backstackManager.getBackstack().removeStateChanger();
        backstackManager.setStateChanger(stateChanger);
        assertThat(deactivatedScope).containsExactly(service1);
    }

    @Test
    public void activationHappensEvenWithForceExecutedStateChangeAndInitializeStateChange() {
        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setScopedServices(new ServiceProvider());

        final List<Object> activatedScope = new ArrayList<>();
        final List<Object> deactivatedScope = new ArrayList<>();

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
            public void onScopeActive(@NonNull String scope) {
                activatedScope.add(this);
            }

            @Override
            public void onScopeInactive(@NonNull String scope) {
                deactivatedScope.add(this);
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

        assertThat(activatedScope).isEmpty();
        backstackManager.setStateChanger(pendingStateChanger);
        callback.get().stateChangeComplete();

        assertThat(activatedScope).containsExactly(service1, service2, service3);

        backstackManager.getBackstack().setHistory(History.of(boop), StateChange.BACKWARD);
        backstackManager.getBackstack().removeStateChanger();
        backstackManager.getBackstack().executePendingStateChange();

        backstackManager.setStateChanger(pendingStateChanger);
        callback.get().stateChangeComplete();

        assertThat(activatedScope).containsExactly(service1, service2, service3, service4, service5, service6);
        assertThat(deactivatedScope).containsExactly(service3, service2, service1);
    }

    @Test
    public void activeScopeIsDeactivatedWhenScopesAreFinalized() {
        Activity activity = Mockito.mock(Activity.class);
        Mockito.when(activity.isFinishing()).thenReturn(true);

        class MyService
                implements ScopedServices.Activated {
            boolean didScopeActivate = false;
            boolean didScopeDeactivate = false;

            @Override
            public void onScopeActive(@NonNull String scope) {
                didScopeActivate = true;
            }

            @Override
            public void onScopeInactive(@NonNull String scope) {
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

        assertThat(service.didScopeActivate).isTrue();
        assertThat(service.didScopeDeactivate).isFalse();

        assertThat(backstackDelegate.hasScope("beep")).isTrue();
        assertThat(backstackDelegate.hasService(testKeyWithScope, SERVICE_TAG)).isTrue();
        backstackDelegate.onDestroy();
        assertThat(backstackDelegate.hasScope("beep")).isFalse();
        assertThat(backstackDelegate.hasService(testKeyWithScope, SERVICE_TAG)).isFalse();
        assertThat(service.didScopeActivate).isTrue();
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
    public void scopedAndActivatedAreCalledInRightOrder() {
        final List<Pair<Object, ServiceEvent>> events = new ArrayList<>();

        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setScopedServices(new ServiceProvider());

        class MyService
                implements ScopedServices.Activated, ScopedServices.Scoped {
            private int id = 0;

            MyService(int id) {
                this.id = id;
            }

            @Override
            public void onScopeActive(@NonNull String scope) {
                events.add(Pair.of((Object) this, ServiceEvent.ACTIVE));
            }

            @Override
            public void onScopeInactive(@NonNull String scope) {
                events.add(Pair.of((Object) this, ServiceEvent.INACTIVE));
            }

            @Override
            public void onEnterScope(@NonNull String scope) {
                events.add(Pair.of((Object) this, ServiceEvent.CREATE));
            }

            @Override
            public void onExitScope(@NonNull String scope) {
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
}
