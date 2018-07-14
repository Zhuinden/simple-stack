package com.zhuinden.simplestack;

import android.app.Activity;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.zhuinden.statebundle.StateBundle;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.LinkedHashMap;
import java.util.Map;

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
        assertThat(stateBundle.getBundle(testKeyWithScope.getScopeTag()).getBundle(SERVICE_TAG).getInt("blah")).isEqualTo(
                5);
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

        assertThat(backstackDelegate.hasService(testKeyWithScope, SERVICE_TAG)).isTrue();
        assertThat(service.didEnterScope).isTrue();
        assertThat(service.didExitScope).isFalse();
        backstackDelegate.onDestroy();
        assertThat(backstackDelegate.hasService(testKeyWithScope, SERVICE_TAG)).isFalse();
        assertThat(service.didEnterScope).isTrue();
        assertThat(service.didExitScope).isTrue();
    }
}
