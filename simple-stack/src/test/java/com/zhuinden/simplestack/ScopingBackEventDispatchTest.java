package com.zhuinden.simplestack;

import android.support.annotation.NonNull;

import com.zhuinden.simplestack.helpers.ServiceProvider;
import com.zhuinden.simplestack.helpers.TestKeyWithExplicitParent;
import com.zhuinden.simplestack.helpers.TestKeyWithOnlyParentServices;
import com.zhuinden.simplestack.helpers.TestKeyWithScope;

import org.junit.Test;

import java.util.List;
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
    public void onBackDispatchToHandlesBackCalledInReverseOrder() {
        final HandlesBackOnce service1 = new HandlesBackOnce();
        final HandlesBackOnce service2 = new HandlesBackOnce();

        Object key = new TestKeyWithScope("key") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
                serviceBinder.addService("service1", service1);
                serviceBinder.addService("service2", service2);
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
        assertThat(backstack.lookupService("service1")).isSameAs(service1);
        assertThat(backstack.lookupService("service2")).isSameAs(service2);

        assertThat(service1.handledBackOnce).isEqualTo(false);
        assertThat(service2.handledBackOnce).isEqualTo(false);
        boolean handled = backstack.goBack();

        assertThat(handled).isEqualTo(true);
        assertThat(service1.handledBackOnce).isEqualTo(false);
        assertThat(service2.handledBackOnce).isEqualTo(true);

        handled = backstack.goBack();
        assertThat(handled).isEqualTo(true);
        assertThat(service1.handledBackOnce).isEqualTo(true);
        assertThat(service2.handledBackOnce).isEqualTo(true);

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
        class BackCounter implements ScopedServices.HandlesBack {
            private int count = 0;

            @Override
            public boolean onBackEvent() {
                count++;

                return false;
            }
        }

        final BackCounter service = new BackCounter();

        Object key = new TestKeyWithScope("key") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
                serviceBinder.addService("service1", service);
                serviceBinder.addService("service2", service);
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

        boolean handled = backstack.goBack();
        assertThat(handled).isFalse();

        assertThat(service.count).isEqualTo(1);
    }

    @Test
    public void onBackDispatchExecutesEvenIfNoStateChanger() {
        final HandlesBackOnce service = new HandlesBackOnce();

        Object key = new TestKeyWithScope("key") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
                serviceBinder.addService("service", service);
            }
        };

        Backstack backstack = new Backstack();
        backstack.setScopedServices(new ServiceProvider());
        backstack.setup(History.of(key));

        StateChanger stateChanger = new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        };

        backstack.setStateChanger(stateChanger);

        assertThat(backstack.getHistory()).containsExactly(key);

        backstack.detachStateChanger();

        boolean handled = backstack.goBack();

        assertThat(handled).isTrue();
        assertThat(service.handledBackOnce).isTrue();

        handled = backstack.goBack(); // returns `false` even if no state changer
        assertThat(handled).isFalse();
    }

    @Test
    public void onBackDispatchHandlesBackHandlesInActiveParentScope() {
        final HandlesBackOnce previousService = new HandlesBackOnce();
        Object previousKey = new TestKeyWithScope("previousKey") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
                serviceBinder.addService("service1", previousService);
            }
        };

        final HandlesBackOnce parentService1 = new HandlesBackOnce();
        final HandlesBackOnce parentService2 = new HandlesBackOnce();
        final HandlesBackOnce service1 = new HandlesBackOnce();
        final HandlesBackOnce service2 = new HandlesBackOnce();

        Object key = new TestKeyWithExplicitParent("key") {
            @Override
            protected void bindParentServices(ServiceBinder serviceBinder) {
                if("parentScope".equals(serviceBinder.getScopeTag())) {
                    serviceBinder.addService("parentService1", parentService1);
                    serviceBinder.addService("parentService2", parentService2);
                }
            }

            @Override
            protected void bindOwnServices(ServiceBinder serviceBinder) {
                serviceBinder.addService("service1", service1);
                serviceBinder.addService("service2", service2);
            }

            @NonNull
            @Override
            public List<String> getParentScopes() {
                return History.of("parentScope");
            }
        };

        Backstack backstack = new Backstack();
        backstack.setScopedServices(new ServiceProvider());
        backstack.setup(History.of(previousKey, key));
        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });

        assertThat(backstack.lookupService("parentService1")).isSameAs(parentService1);
        assertThat(backstack.lookupService("parentService2")).isSameAs(parentService2);
        assertThat(backstack.lookupService("service1")).isSameAs(service1);
        assertThat(backstack.lookupService("service2")).isSameAs(service2);

        boolean handled;

        assertThat(parentService1.handledBackOnce).isFalse();
        assertThat(parentService2.handledBackOnce).isFalse();
        assertThat(service1.handledBackOnce).isFalse();
        assertThat(service2.handledBackOnce).isFalse();

        handled = backstack.goBack();

        assertThat(handled).isTrue();
        assertThat(parentService1.handledBackOnce).isFalse();
        assertThat(parentService2.handledBackOnce).isFalse();
        assertThat(service1.handledBackOnce).isFalse();
        assertThat(service2.handledBackOnce).isTrue();

        handled = backstack.goBack();

        assertThat(handled).isTrue();
        assertThat(parentService1.handledBackOnce).isFalse();
        assertThat(parentService2.handledBackOnce).isFalse();
        assertThat(service1.handledBackOnce).isTrue();
        assertThat(service2.handledBackOnce).isTrue();

        handled = backstack.goBack();

        assertThat(handled).isTrue();
        assertThat(parentService1.handledBackOnce).isFalse();
        assertThat(parentService2.handledBackOnce).isTrue();
        assertThat(service1.handledBackOnce).isTrue();
        assertThat(service2.handledBackOnce).isTrue();

        handled = backstack.goBack();

        assertThat(handled).isTrue();
        assertThat(parentService1.handledBackOnce).isTrue();
        assertThat(parentService2.handledBackOnce).isTrue();
        assertThat(service1.handledBackOnce).isTrue();
        assertThat(service2.handledBackOnce).isTrue();

        assertThat(previousService.handledBackOnce).isFalse();

        assertThat(backstack.getHistory()).containsExactly(previousKey, key);
        handled = backstack.goBack();
        assertThat(handled).isTrue();
        assertThat(backstack.getHistory()).containsExactly(previousKey);

        handled = backstack.goBack();
        assertThat(handled).isTrue();
        assertThat(previousService.handledBackOnce).isTrue();

        handled = backstack.goBack();
        assertThat(handled).isFalse();
    }

    @Test
    public void onBackDispatchHandlesBackHandlesInActiveParentScopeEvenIfTopIsNotScopeKey() {
        final HandlesBackOnce parentService = new HandlesBackOnce();

        Object key = new TestKeyWithOnlyParentServices("key", History.of("parentScope")) {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
                if("parentScope".equals(serviceBinder.getScopeTag())) {
                    serviceBinder.addService("parentService", parentService);
                }
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

        assertThat(backstack.lookupService("parentService")).isSameAs(parentService);

        assertThat(parentService.handledBackOnce).isFalse();
        boolean handled = backstack.goBack();
        assertThat(handled).isTrue();
        assertThat(parentService.handledBackOnce).isTrue();

        handled = backstack.goBack();
        assertThat(handled).isFalse();
    }

    @Test
    public void onBackDispatchHandlesBackDispatchesToCorrectActiveChainAfterMoveToTop() {
        final HandlesBackOnce previousService = new HandlesBackOnce();
        final HandlesBackOnce nextService = new HandlesBackOnce();

        Object previousKey = new TestKeyWithScope("previous") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
                serviceBinder.addService("service", previousService);
            }
        };

        Object nextKey = new TestKeyWithScope("next") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
                serviceBinder.addService("service", nextService);
            }
        };

        Backstack backstack = new Backstack();
        backstack.setScopedServices(new ServiceProvider());
        backstack.setup(History.of(previousKey, nextKey));
        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });

        assertThat(backstack.lookupService("service")).isSameAs(nextService);

        backstack.moveToTop(previousKey);

        assertThat(backstack.getHistory()).containsExactly(nextKey, previousKey);

        assertThat(backstack.lookupService("service")).isSameAs(previousService);

        boolean handled;

        assertThat(previousService.handledBackOnce).isFalse();
        handled = backstack.goBack();

        assertThat(handled).isTrue();
        assertThat(previousService.handledBackOnce).isTrue();

        handled = backstack.goBack();

        assertThat(handled).isTrue();
        assertThat(backstack.getHistory()).containsExactly(nextKey);

        assertThat(nextService.handledBackOnce).isFalse();
        handled = backstack.goBack();

        assertThat(handled).isTrue();
        assertThat(nextService.handledBackOnce).isTrue();

        handled = backstack.goBack();
        assertThat(handled).isFalse();
    }
}
