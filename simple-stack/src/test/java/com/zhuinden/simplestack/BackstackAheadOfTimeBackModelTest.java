package com.zhuinden.simplestack;

import com.zhuinden.simplestack.helpers.ServiceProvider;
import com.zhuinden.simplestack.helpers.TestKey;
import com.zhuinden.simplestack.helpers.TestKeyWithExplicitParent;
import com.zhuinden.simplestack.helpers.TestKeyWithScope;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import javax.annotation.Nonnull;

import static org.assertj.core.api.Assertions.assertThat;

public class BackstackAheadOfTimeBackModelTest {
    @Test
    public void settingBackModelAfterSetupFails() {
        Backstack backstack = new Backstack();

        TestKey testKey = new TestKey("test");

        backstack.setup(History.of(testKey));


        try {
            backstack.setBackHandlingModel(BackHandlingModel.AHEAD_OF_TIME);
            Assert.fail();
        } catch(IllegalStateException e) {
            // OK!
        }
    }

    @Test
    public void settingBackModelBeforeSetupWorks() {
        Backstack backstack = new Backstack();

        TestKey testKey1 = new TestKey("test1");
        TestKey testKey2 = new TestKey("test2");

        backstack.setBackHandlingModel(BackHandlingModel.AHEAD_OF_TIME);

        backstack.setup(History.of(testKey1, testKey2));

        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });

        assertThat(backstack.willHandleAheadOfTimeBack()).isTrue();

        assertThat(backstack.goBack()).isTrue();

        assertThat(backstack.willHandleAheadOfTimeBack()).isFalse();

        try {
            backstack.goBack();
            Assert.fail();
        } catch(AheadOfTimeBackProcessingContractViolationException e) {
            // OK!
        }
    }

    @Test
    public void disallowBackstackOpsThatRequireAheadOfTimeMode() {
        Backstack backstack = new Backstack();

        backstack.setBackHandlingModel(BackHandlingModel.EVENT_BUBBLING);

        try {
            backstack.willHandleAheadOfTimeBack();
            Assert.fail();
        } catch(IllegalStateException e) {
            // OK!
        }

        try {
            backstack.addAheadOfTimeWillHandleBackChangedListener(new AheadOfTimeWillHandleBackChangedListener() {
                @Override
                public void willHandleBackChanged(boolean willHandleBack) {

                }
            });
            Assert.fail();
        } catch(IllegalStateException e) {
            // OK!
        }

        try {
            backstack.removeAheadOfTimeWillHandleBackChangedListener(new AheadOfTimeWillHandleBackChangedListener() {
                @Override
                public void willHandleBackChanged(boolean willHandleBack) {

                }
            });
            Assert.fail();
        } catch(IllegalStateException e) {
            // OK!
        }
    }

    @Test
    public void initializeAheadOfTimeWithOneChangesCorrectly() {

        Backstack backstack = new Backstack();
        backstack.setBackHandlingModel(BackHandlingModel.AHEAD_OF_TIME);

        TestKey testKey1 = new TestKey("testKey1");
        TestKey testKey2 = new TestKey("testKey2");
        TestKey testKey3 = new TestKey("testKey3");
        TestKey testKey4 = new TestKey("testKey4");

        backstack.setup(History.of(testKey1));

        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
                assertThat(stateChange.backstack.willHandleAheadOfTimeBack()).isTrue();

                completionCallback.stateChangeComplete();
            }
        });

        assertThat(backstack.willHandleAheadOfTimeBack()).isFalse();

        backstack.goTo(testKey2);

        assertThat(backstack.willHandleAheadOfTimeBack()).isTrue();

        backstack.goTo(testKey3);

        assertThat(backstack.willHandleAheadOfTimeBack()).isTrue();

        backstack.goTo(testKey4);

        assertThat(backstack.willHandleAheadOfTimeBack()).isTrue();

        assertThat(backstack.goBack()).isTrue();

        assertThat(backstack.willHandleAheadOfTimeBack()).isTrue();

        assertThat(backstack.goBack()).isTrue();

        assertThat(backstack.willHandleAheadOfTimeBack()).isTrue();

        assertThat(backstack.goBack()).isTrue();

        assertThat(backstack.willHandleAheadOfTimeBack()).isFalse();

        try {
            backstack.goBack();
            Assert.fail();
        } catch(AheadOfTimeBackProcessingContractViolationException e) {
            // OK!
        }

        backstack.setHistory(History.of(testKey2, testKey3), StateChange.FORWARD);

        assertThat(backstack.willHandleAheadOfTimeBack()).isTrue();

        assertThat(backstack.goBack()).isTrue();

        assertThat(backstack.willHandleAheadOfTimeBack()).isFalse();
    }

    @Test
    public void interceptingWithBackCallbackRegistryInScopedServicesWorks() {
        Backstack backstack = new Backstack();

        backstack.setScopedServices(new ServiceProvider());

        class ScopedService
            implements ScopedServices.Registered {
            private final AheadOfTimeBackCallbackRegistry aheadOfTimeBackCallbackRegistry;

            private boolean shouldInterceptBack = true;

            public boolean isShouldInterceptBack() {
                return shouldInterceptBack;
            }

            private void updateShouldInterceptBack(boolean shouldInterceptBack) {
                this.shouldInterceptBack = shouldInterceptBack;
                aheadOfTimeBackCallback.setEnabled(shouldInterceptBack);
            }

            private final AheadOfTimeBackCallback aheadOfTimeBackCallback = new AheadOfTimeBackCallback(
                shouldInterceptBack) {
                @Override
                public void onBackReceived() {
                    updateShouldInterceptBack(false);
                }
            };

            public ScopedService(AheadOfTimeBackCallbackRegistry aheadOfTimeBackCallbackRegistry) {
                this.aheadOfTimeBackCallbackRegistry = aheadOfTimeBackCallbackRegistry;
            }

            @Override
            public void onServiceRegistered() {
                aheadOfTimeBackCallbackRegistry.registerAheadOfTimeBackCallback(aheadOfTimeBackCallback);
            }

            @Override
            public void onServiceUnregistered() {
                aheadOfTimeBackCallbackRegistry.unregisterAheadOfTimeCallback(aheadOfTimeBackCallback);
            }
        }

        TestKeyWithScope key1 = new TestKeyWithScope("key1") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
                serviceBinder.addService(ScopedService.class.getName(),
                                         new ScopedService(serviceBinder.getAheadOfTimeBackCallbackRegistry()));
            }
        };

        backstack.setBackHandlingModel(BackHandlingModel.AHEAD_OF_TIME);

        backstack.setup(History.of(key1));

        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });

        assertThat(backstack.willHandleAheadOfTimeBack()).isTrue();

        ScopedService scopedService = backstack.getService(key1, ScopedService.class.getName());
        assertThat(scopedService.shouldInterceptBack).isTrue();

        backstack.goBack();

        assertThat(scopedService.shouldInterceptBack).isFalse();
        assertThat(backstack.willHandleAheadOfTimeBack()).isFalse();
    }

    @Test
    public void interceptingWithBackCallbackRegistryInScopedServicesInitiallyFalseWorks() {
        Backstack backstack = new Backstack();

        backstack.setScopedServices(new ServiceProvider());

        class ScopedService
            implements ScopedServices.Registered {
            private final AheadOfTimeBackCallbackRegistry aheadOfTimeBackCallbackRegistry;

            private boolean shouldInterceptBack = false;

            private void updateShouldInterceptBack(boolean shouldInterceptBack) {
                this.shouldInterceptBack = shouldInterceptBack;
                aheadOfTimeBackCallback.setEnabled(shouldInterceptBack);
            }

            public void startInterceptingBack() {
                updateShouldInterceptBack(true);
            }

            private final AheadOfTimeBackCallback aheadOfTimeBackCallback = new AheadOfTimeBackCallback(
                shouldInterceptBack) {
                @Override
                public void onBackReceived() {
                    updateShouldInterceptBack(false);
                }
            };

            public ScopedService(AheadOfTimeBackCallbackRegistry aheadOfTimeBackCallbackRegistry) {
                this.aheadOfTimeBackCallbackRegistry = aheadOfTimeBackCallbackRegistry;
            }

            @Override
            public void onServiceRegistered() {
                aheadOfTimeBackCallbackRegistry.registerAheadOfTimeBackCallback(aheadOfTimeBackCallback);
            }

            @Override
            public void onServiceUnregistered() {
                aheadOfTimeBackCallbackRegistry.unregisterAheadOfTimeCallback(aheadOfTimeBackCallback);
            }
        }

        TestKeyWithScope key1 = new TestKeyWithScope("key1") {
            @Override
            public void bindServices(ServiceBinder serviceBinder) {
                serviceBinder.addService(ScopedService.class.getName(),
                                         new ScopedService(serviceBinder.getAheadOfTimeBackCallbackRegistry()));
            }
        };

        backstack.setBackHandlingModel(BackHandlingModel.AHEAD_OF_TIME);

        backstack.setup(History.of(key1));

        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });

        ScopedService scopedService = backstack.getService(key1, ScopedService.class.getName());

        assertThat(scopedService.shouldInterceptBack).isFalse();
        assertThat(backstack.willHandleAheadOfTimeBack()).isFalse();

        scopedService.startInterceptingBack();

        assertThat(scopedService.shouldInterceptBack).isTrue();
        assertThat(backstack.willHandleAheadOfTimeBack()).isTrue();

        backstack.goBack();

        assertThat(scopedService.shouldInterceptBack).isFalse();
        assertThat(backstack.willHandleAheadOfTimeBack()).isFalse();
    }

    @Test
    public void interceptingWithBackCallbackRegistryInScopedServicesWorksAcrossParentScopeHierarchy() {
        Backstack backstack = new Backstack();

        backstack.setScopedServices(new ServiceProvider());

        class ScopedService
            implements ScopedServices.Registered {
            private final AheadOfTimeBackCallbackRegistry aheadOfTimeBackCallbackRegistry;

            private boolean shouldInterceptBack = true;

            private void updateShouldInterceptBack(boolean shouldInterceptBack) {
                this.shouldInterceptBack = shouldInterceptBack;
                aheadOfTimeBackCallback.setEnabled(shouldInterceptBack);
            }

            public void startInterceptingBack() {
                updateShouldInterceptBack(true);
            }

            public void stopInterceptingBack() {
                updateShouldInterceptBack(false);
            }

            private final AheadOfTimeBackCallback aheadOfTimeBackCallback = new AheadOfTimeBackCallback(
                shouldInterceptBack) {
                @Override
                public void onBackReceived() {
                    updateShouldInterceptBack(false);
                }
            };

            public ScopedService(AheadOfTimeBackCallbackRegistry aheadOfTimeBackCallbackRegistry) {
                this.aheadOfTimeBackCallbackRegistry = aheadOfTimeBackCallbackRegistry;
            }

            @Override
            public void onServiceRegistered() {
                aheadOfTimeBackCallbackRegistry.registerAheadOfTimeBackCallback(aheadOfTimeBackCallback);
            }

            @Override
            public void onServiceUnregistered() {
                aheadOfTimeBackCallbackRegistry.unregisterAheadOfTimeCallback(aheadOfTimeBackCallback);
            }
        }

        final String sharedParentScope = "SHARED_PARENT_SCOPE";

        TestKeyWithExplicitParent key1 = new TestKeyWithExplicitParent("key1") {
            @Nonnull
            @Override
            public List<String> getParentScopes() {
                return History.of(sharedParentScope, "key1p1", "key1p2");
            }

            @Override
            protected void bindParentServices(ServiceBinder serviceBinder) {
                serviceBinder.addService(serviceBinder.getScopeTag() + "_" + ScopedService.class.getName(),
                                         new ScopedService(serviceBinder.getAheadOfTimeBackCallbackRegistry()));
            }

            @Override
            protected void bindOwnServices(ServiceBinder serviceBinder) {
                serviceBinder.addService(ScopedService.class.getName(),
                                         new ScopedService(serviceBinder.getAheadOfTimeBackCallbackRegistry()));
            }
        };

        TestKeyWithExplicitParent key2 = new TestKeyWithExplicitParent("key2") {
            @Nonnull
            @Override
            public List<String> getParentScopes() {
                return History.of(sharedParentScope, "key2p1", "key2p2");
            }

            @Override
            protected void bindParentServices(ServiceBinder serviceBinder) {
                serviceBinder.addService(serviceBinder.getScopeTag() + "_" + ScopedService.class.getName(),
                                         new ScopedService(serviceBinder.getAheadOfTimeBackCallbackRegistry()));
            }

            @Override
            protected void bindOwnServices(ServiceBinder serviceBinder) {
                serviceBinder.addService(ScopedService.class.getName(),
                                         new ScopedService(serviceBinder.getAheadOfTimeBackCallbackRegistry()));
            }
        };


        backstack.setBackHandlingModel(BackHandlingModel.AHEAD_OF_TIME);

        backstack.setup(History.of(key1));

        backstack.setStateChanger(new StateChanger() {
            @Override
            public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
                completionCallback.stateChangeComplete();
            }
        });


        assertThat(backstack.<ScopedService>getService(key1,
                                                       ScopedService.class.getName()).shouldInterceptBack).isTrue();
        assertThat(backstack.<ScopedService>getService("key1p1",
                                                       "key1p1_" + ScopedService.class.getName()).shouldInterceptBack).isTrue();
        assertThat(backstack.<ScopedService>getService("key1p2",
                                                       "key1p2_" + ScopedService.class.getName()).shouldInterceptBack).isTrue();
        assertThat(backstack.hasScope(key2.getScopeTag())).isFalse();
        assertThat(backstack.hasScope("key2p1")).isFalse();
        assertThat(backstack.hasScope("key2p2")).isFalse();
        assertThat(backstack.<ScopedService>getService(sharedParentScope,
                                                       sharedParentScope + "_" + ScopedService.class.getName()).shouldInterceptBack).isTrue();
        assertThat(backstack.willHandleAheadOfTimeBack()).isTrue();

        backstack.goBack();

        assertThat(backstack.<ScopedService>getService(key1,
                                                       ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.<ScopedService>getService("key1p1",
                                                       "key1p1_" + ScopedService.class.getName()).shouldInterceptBack).isTrue();
        assertThat(backstack.<ScopedService>getService("key1p2",
                                                       "key1p2_" + ScopedService.class.getName()).shouldInterceptBack).isTrue();
        assertThat(backstack.<ScopedService>getService(sharedParentScope,
                                                       sharedParentScope + "_" + ScopedService.class.getName()).shouldInterceptBack).isTrue();
        assertThat(backstack.willHandleAheadOfTimeBack()).isTrue();

        backstack.goBack();

        assertThat(backstack.<ScopedService>getService(key1,
                                                       ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.<ScopedService>getService("key1p1",
                                                       "key1p1_" + ScopedService.class.getName()).shouldInterceptBack).isTrue();
        assertThat(backstack.<ScopedService>getService("key1p2",
                                                       "key1p2_" + ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.<ScopedService>getService(sharedParentScope,
                                                       sharedParentScope + "_" + ScopedService.class.getName()).shouldInterceptBack).isTrue();
        assertThat(backstack.willHandleAheadOfTimeBack()).isTrue();

        backstack.goBack();

        assertThat(backstack.<ScopedService>getService(key1,
                                                       ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.<ScopedService>getService("key1p1",
                                                       "key1p1_" + ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.<ScopedService>getService("key1p2",
                                                       "key1p2_" + ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.<ScopedService>getService(sharedParentScope,
                                                       sharedParentScope + "_" + ScopedService.class.getName()).shouldInterceptBack).isTrue();
        assertThat(backstack.willHandleAheadOfTimeBack()).isTrue();

        backstack.goBack();

        assertThat(backstack.<ScopedService>getService(key1,
                                                       ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.<ScopedService>getService("key1p1",
                                                       "key1p1_" + ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.<ScopedService>getService("key1p2",
                                                       "key1p2_" + ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.<ScopedService>getService(sharedParentScope,
                                                       sharedParentScope + "_" + ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.willHandleAheadOfTimeBack()).isFalse();

        try {
            backstack.goBack();
            Assert.fail();
        } catch(AheadOfTimeBackProcessingContractViolationException e) {
            // OK!
        }

        backstack.goTo(key2);

        assertThat(backstack.<ScopedService>getService(key1,
                                                       ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.<ScopedService>getService(key2,
                                                       ScopedService.class.getName()).shouldInterceptBack).isTrue();
        assertThat(backstack.<ScopedService>getService("key1p1",
                                                       "key1p1_" + ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.<ScopedService>getService("key1p2",
                                                       "key1p2_" + ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.<ScopedService>getService("key2p1",
                                                       "key2p1_" + ScopedService.class.getName()).shouldInterceptBack).isTrue();
        assertThat(backstack.<ScopedService>getService("key2p2",
                                                       "key2p2_" + ScopedService.class.getName()).shouldInterceptBack).isTrue();
        assertThat(backstack.<ScopedService>getService(sharedParentScope,
                                                       sharedParentScope + "_" + ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.willHandleAheadOfTimeBack()).isTrue();

        backstack.goBack();

        assertThat(backstack.<ScopedService>getService(key1,
                                                       ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.<ScopedService>getService(key2,
                                                       ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.<ScopedService>getService("key1p1",
                                                       "key1p1_" + ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.<ScopedService>getService("key1p2",
                                                       "key1p2_" + ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.<ScopedService>getService("key2p1",
                                                       "key2p1_" + ScopedService.class.getName()).shouldInterceptBack).isTrue();
        assertThat(backstack.<ScopedService>getService("key2p2",
                                                       "key2p2_" + ScopedService.class.getName()).shouldInterceptBack).isTrue();
        assertThat(backstack.<ScopedService>getService(sharedParentScope,
                                                       sharedParentScope + "_" + ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.willHandleAheadOfTimeBack()).isTrue();

        backstack.goBack();

        assertThat(backstack.<ScopedService>getService(key1,
                                                       ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.<ScopedService>getService(key2,
                                                       ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.<ScopedService>getService("key1p1",
                                                       "key1p1_" + ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.<ScopedService>getService("key1p2",
                                                       "key1p2_" + ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.<ScopedService>getService("key2p1",
                                                       "key2p1_" + ScopedService.class.getName()).shouldInterceptBack).isTrue();
        assertThat(backstack.<ScopedService>getService("key2p2",
                                                       "key2p2_" + ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.<ScopedService>getService(sharedParentScope,
                                                       sharedParentScope + "_" + ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.willHandleAheadOfTimeBack()).isTrue();

        backstack.goBack();

        assertThat(backstack.<ScopedService>getService(key1,
                                                       ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.<ScopedService>getService(key2,
                                                       ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.<ScopedService>getService("key1p1",
                                                       "key1p1_" + ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.<ScopedService>getService("key1p2",
                                                       "key1p2_" + ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.<ScopedService>getService("key2p1",
                                                       "key2p1_" + ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.<ScopedService>getService("key2p2",
                                                       "key2p2_" + ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.<ScopedService>getService(sharedParentScope,
                                                       sharedParentScope + "_" + ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.willHandleAheadOfTimeBack()).isTrue();

        backstack.goBack();

        assertThat(backstack.<ScopedService>getService(key1,
                                                       ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.<ScopedService>getService("key1p1",
                                                       "key1p1_" + ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.<ScopedService>getService("key1p2",
                                                       "key1p2_" + ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.hasScope(key2.getScopeTag())).isFalse();
        assertThat(backstack.hasScope("key2p1")).isFalse();
        assertThat(backstack.hasScope("key2p2")).isFalse();
        assertThat(backstack.<ScopedService>getService(sharedParentScope,
                                                       sharedParentScope + "_" + ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.willHandleAheadOfTimeBack()).isFalse();

        try {
            backstack.goBack();
            Assert.fail();
        } catch(AheadOfTimeBackProcessingContractViolationException e) {
            // OK!
        }

        backstack.<ScopedService>getService(sharedParentScope,
                                            sharedParentScope + "_" + ScopedService.class.getName()).startInterceptingBack();

        assertThat(backstack.<ScopedService>getService(key1,
                                                       ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.<ScopedService>getService("key1p1",
                                                       "key1p1_" + ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.<ScopedService>getService("key1p2",
                                                       "key1p2_" + ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.hasScope(key2.getScopeTag())).isFalse();
        assertThat(backstack.hasScope("key2p1")).isFalse();
        assertThat(backstack.hasScope("key2p2")).isFalse();
        assertThat(backstack.<ScopedService>getService(sharedParentScope,
                                                       sharedParentScope + "_" + ScopedService.class.getName()).shouldInterceptBack).isTrue();
        assertThat(backstack.willHandleAheadOfTimeBack()).isTrue();

        backstack.goBack();

        assertThat(backstack.<ScopedService>getService(key1,
                                                       ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.<ScopedService>getService("key1p1",
                                                       "key1p1_" + ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.<ScopedService>getService("key1p2",
                                                       "key1p2_" + ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.hasScope(key2.getScopeTag())).isFalse();
        assertThat(backstack.hasScope("key2p1")).isFalse();
        assertThat(backstack.hasScope("key2p2")).isFalse();
        assertThat(backstack.<ScopedService>getService(sharedParentScope,
                                                       sharedParentScope + "_" + ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.willHandleAheadOfTimeBack()).isFalse();

        backstack.<ScopedService>getService("key1p1",
                                            "key1p1_" + ScopedService.class.getName()).startInterceptingBack();

        assertThat(backstack.<ScopedService>getService(key1,
                                                       ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.<ScopedService>getService("key1p1",
                                                       "key1p1_" + ScopedService.class.getName()).shouldInterceptBack).isTrue();
        assertThat(backstack.<ScopedService>getService("key1p2",
                                                       "key1p2_" + ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.hasScope(key2.getScopeTag())).isFalse();
        assertThat(backstack.hasScope("key2p1")).isFalse();
        assertThat(backstack.hasScope("key2p2")).isFalse();
        assertThat(backstack.<ScopedService>getService(sharedParentScope,
                                                       sharedParentScope + "_" + ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.willHandleAheadOfTimeBack()).isTrue();

        backstack.goBack();

        assertThat(backstack.<ScopedService>getService(key1,
                                                       ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.<ScopedService>getService("key1p1",
                                                       "key1p1_" + ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.<ScopedService>getService("key1p2",
                                                       "key1p2_" + ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.hasScope(key2.getScopeTag())).isFalse();
        assertThat(backstack.hasScope("key2p1")).isFalse();
        assertThat(backstack.hasScope("key2p2")).isFalse();
        assertThat(backstack.<ScopedService>getService(sharedParentScope,
                                                       sharedParentScope + "_" + ScopedService.class.getName()).shouldInterceptBack).isFalse();
        assertThat(backstack.willHandleAheadOfTimeBack()).isFalse();
    }
}
