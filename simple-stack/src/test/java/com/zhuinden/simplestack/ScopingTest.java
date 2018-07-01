package com.zhuinden.simplestack;

import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.zhuinden.statebundle.StateBundle;

import junit.framework.Assert;

import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ScopingTest {
    private static final Map<String, Object> services = new LinkedHashMap<>();

    private static final String SERVICE_TAG = "service";

    static {
        services.put(SERVICE_TAG, new Service());
    }

    private static class Service implements Bundleable, ScopeKey.Scoped {
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
        public void onEnterScope(String scope) {
            didEnterScope = true;
        }

        @Override
        public void onExitScope(String scope) {
            didExitScope = true;
        }
    }

    StateChanger stateChanger = new StateChanger() {
        @Override
        public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
            completionCallback.stateChangeComplete();
        }
    };

    private static class TestKeyWithScope
            extends TestKey
            implements ScopeKey {
        TestKeyWithScope(String name) {
            super(name);
        }

        protected TestKeyWithScope(Parcel in) {
            super(in);
        }

        @Override
        public String getScopeTag() {
            return "boop";
        }

        @Override
        public void bindServices(ServiceBinder serviceBinder) {
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
    public void scopeIsCreatedForScopeKeys() {
        BackstackManager backstackManager = new BackstackManager();
        backstackManager.setup(History.single(testKey2));
        assertThat(backstackManager.hasService(testKey2, SERVICE_TAG)).isFalse();
        backstackManager.setStateChanger(stateChanger);
        assertThat(backstackManager.hasService(testKey2, SERVICE_TAG)).isTrue();

        Service service = backstackManager.getService(testKey2, SERVICE_TAG);
        assertThat(service).isSameAs(services.get(SERVICE_TAG));
    }

    @Test
    public void scopeIsDestroyedForClearedScopeKeys() {
        BackstackManager backstackManager = new BackstackManager();
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
        
        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                scopeManager.buildScopes(stateChange);
                completionCallback.stateChangeComplete();
            }
        };
        
        Backstack backstack = new Backstack(History.of(testKey2));
        backstack.setStateChanger(stateChanger);
        
        assertThat(scopeManager.hasService(testKey2.getScopeTag(), SERVICE_TAG));

        StateBundle stateBundle = scopeManager.saveStates();

        assertThat(stateBundle.getBundle(testKey2.getScopeTag()).getBundle(SERVICE_TAG).getInt("blah")).isEqualTo(5);
    }

    @Test
    public void persistedStateOfScopedServicesIsRestored() {
        final ScopeManager scopeManager = new ScopeManager();

        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                scopeManager.buildScopes(stateChange);
                completionCallback.stateChangeComplete();
            }
        };

        Backstack backstack = new Backstack(History.of(testKey2));
        backstack.setStateChanger(stateChanger);

        assertThat(scopeManager.hasService(testKey2.getScopeTag(), SERVICE_TAG));

        StateBundle stateBundle = scopeManager.saveStates();

        final ScopeManager scopeManager2 = new ScopeManager();
        StateChanger stateChanger2 = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                scopeManager2.buildScopes(stateChange);
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

        final Service service = new Service();
        TestKeyWithScope testKeyWithScope = new TestKeyWithScope("blah") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
                serviceBinder.add(SERVICE_TAG, service);
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
}
