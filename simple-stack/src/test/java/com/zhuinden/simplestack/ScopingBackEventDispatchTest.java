package com.zhuinden.simplestack;

import android.support.annotation.NonNull;

import com.zhuinden.simplestack.helpers.ServiceProvider;
import com.zhuinden.simplestack.helpers.TestKeyWithScope;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

public class ScopingBackEventDispatchTest {
    private class HandlesBackOnce implements ScopedServices.HandlesBack {
        private boolean handledBackOnce = false;

        @Override
        public boolean onBackEvent() {
            if(!handledBackOnce) {
                this.handledBackOnce = true;
                return true;
            }
            return false;
        }
    }

    @Test
    public void onBackStackGoBackDispatchesBackToActiveScope() {
        final HandlesBackOnce service1 = new HandlesBackOnce();
        final HandlesBackOnce service2 = new HandlesBackOnce();

        Object key1 = new TestKeyWithScope("key1") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
                serviceBinder.addService("service1", service1);
            }
        };

        Object key2 = new TestKeyWithScope("key2") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
                serviceBinder.addService("service2", service2);
            }
        };

        Backstack backstack = new Backstack();
        backstack.setScopedServices(new ServiceProvider());
        backstack.setup(History.of(key1, key2));
        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });

        assertThat(service2.handledBackOnce).isEqualTo(false);
        boolean handledBack = backstack.goBack();
        assertThat(handledBack).isEqualTo(true);
        assertThat(service2.handledBackOnce).isEqualTo(true);
        assertThat(backstack.getHistory()).containsExactly(key1, key2);

        assertThat(backstack.getHistory()).containsExactly(key1, key2);
        assertThat(service2.handledBackOnce).isEqualTo(true);
        handledBack = backstack.goBack();
        assertThat(handledBack).isEqualTo(true);
        assertThat(service2.handledBackOnce).isEqualTo(true);
        assertThat(backstack.getHistory()).containsExactly(key1);

        assertThat(service1.handledBackOnce).isEqualTo(false);
        handledBack = backstack.goBack();
        assertThat(handledBack).isEqualTo(true);
        assertThat(service1.handledBackOnce).isEqualTo(true);

        handledBack = backstack.goBack();
        assertThat(handledBack).isEqualTo(false);
    }

    @Test
    public void onBackDispatchDoesNotDispatchDuringStateChange() {
        final HandlesBackOnce service1 = new HandlesBackOnce();
        final HandlesBackOnce service2 = new HandlesBackOnce();

        Object key1 = new TestKeyWithScope("key1") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
                serviceBinder.addService("service1", service1);
            }
        };

        Object key2 = new TestKeyWithScope("key2") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
                serviceBinder.addService("service2", service2);
            }
        };

        Backstack backstack = new Backstack();
        backstack.setScopedServices(new ServiceProvider());
        backstack.setup(History.of(key1));

        final AtomicReference<StateChanger.Callback> callbackRef = new AtomicReference<>();
        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                callbackRef.set(completionCallback);
            }
        });

        callbackRef.get().stateChangeComplete();

        assertThat(backstack.getHistory()).containsExactly(key1);

        backstack.goTo(key2);

        assertThat(service1.handledBackOnce).isEqualTo(false);
        assertThat(service2.handledBackOnce).isEqualTo(false);

        boolean handled = backstack.goBack(); // ignored!

        assertThat(handled).isEqualTo(true);
        assertThat(service1.handledBackOnce).isEqualTo(false);
        assertThat(service2.handledBackOnce).isEqualTo(false);

        callbackRef.get().stateChangeComplete(); // goTo(key2)

        assertThat(backstack.getHistory()).containsExactly(key1, key2);

        handled = backstack.goBack();

        assertThat(handled).isEqualTo(true);
        assertThat(service1.handledBackOnce).isEqualTo(false);
        assertThat(service2.handledBackOnce).isEqualTo(true);

        handled = backstack.goBack();
        callbackRef.get().stateChangeComplete();

        assertThat(handled).isEqualTo(true);
        assertThat(service1.handledBackOnce).isEqualTo(false);
        assertThat(service2.handledBackOnce).isEqualTo(true);
        assertThat(backstack.getHistory()).containsExactly(key1);

        handled = backstack.goBack();

        assertThat(handled).isEqualTo(true);
        assertThat(service1.handledBackOnce).isEqualTo(true);
        assertThat(service2.handledBackOnce).isEqualTo(true);
        assertThat(backstack.getHistory()).containsExactly(key1);

        handled = backstack.goBack();
        assertThat(handled).isEqualTo(false);
    }

    @Test
    public void onBackDispatchToHandlesBackNotCalledOnAlias() {
        final HandlesBackOnce service = new HandlesBackOnce();

        Object key = new TestKeyWithScope("key") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
                serviceBinder.addAlias("service", service);
            }
        };

        Backstack backstack = new Backstack();
        backstack.setScopedServices(new ServiceProvider());
        backstack.setup(History.of(key));
        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });

        assertThat(backstack.getHistory()).containsExactly(key);
        assertThat(backstack.lookupService("service")).isSameAs(service);

        boolean handled = backstack.goBack();

        assertThat(handled).isEqualTo(false);
    }

    @Test
    public void onBackDispatchToHandlesBackCalledOnceForMultipleRegistrations() {
        throw new RuntimeException("blah");
    }

    @Test
    public void onBackDispatchHandlesBackHandlesInActiveParentScope() {
        throw new RuntimeException("blah");
    }

    @Test
    public void onBackDispatchHandlesBackHandlesInActiveParentScopeEvenIfTopIsNotScopeKey() {
        throw new RuntimeException("blah");
    }

    @Test
    public void onBackDispatchHandlesBackDispatchesToCorrectActiveChainAfterMoveToTop() {
        throw new RuntimeException("blah");
    }
}
