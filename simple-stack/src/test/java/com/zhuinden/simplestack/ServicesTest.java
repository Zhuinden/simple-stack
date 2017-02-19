package com.zhuinden.simplestack;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

public class ServicesTest {
    @Mock
    StateChange stateChange;

    @Mock
    BackstackDelegate backstackDelegate;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Before
    public void before() {
        Mockito.when(stateChange.getNewState()).thenReturn(Collections.<Object>emptyList());
        Mockito.when(stateChange.getPreviousState()).thenReturn(Collections.<Object>emptyList());
        Mockito.when(stateChange.getDirection()).thenReturn(StateChange.REPLACE);
    }

    static class ChildKey extends TestKey implements Parcelable, Services.Child {
        Parcelable parent;

        ChildKey(String name, Parcelable parent) {
            super(name);
            this.parent = parent;
        }

        protected ChildKey(Parcel in) {
            super(in);
        }

        @Override
        public Parcelable parent() {
            return parent;
        }
    }

    @Test
    public void serviceFactoryBindsServices() {
        Parcelable key = new TestKey("test");
        final Object service = new Object();
        List<ServiceFactory> servicesFactories = new ArrayList<>();
        servicesFactories.add(new ServiceFactory() {
            @Override
            public void bindServices(@NonNull Services.Builder builder) {
                builder.withService("HELLO", service);
            }
        });
        ServiceManager serviceManager = new ServiceManager(servicesFactories);
        serviceManager.setUp(backstackDelegate, key);

        assertThat(serviceManager.findServices(key).<Object>getService("HELLO")).isSameAs(service);
    }

    @Test
    public void unbindingServiceMakesItInaccessible() {
        Parcelable key = new TestKey("test");
        final Object service = new Object();
        List<ServiceFactory> servicesFactories = new ArrayList<>();
        servicesFactories.add(new ServiceFactory() {
            @Override
            public void bindServices(@NonNull Services.Builder builder) {
                builder.withService("HELLO", service);
            }
        });
        ServiceManager serviceManager = new ServiceManager(servicesFactories);
        serviceManager.setUp(backstackDelegate, key);
        serviceManager.tearDown(backstackDelegate, true, key);

        try {
            serviceManager.findServices(key).getService("HELLO");
            fail();
        } catch(IllegalStateException e) {
            // OK!
        }
    }

    @Test
    public void bindingChildCreatesServiceOfParent() {
        final Parcelable parentKey = new TestKey("parent");
        ChildKey childKey = new ChildKey("child", parentKey);
        final Object parentService = new Object();
        List<ServiceFactory> servicesFactories = new ArrayList<>();
        servicesFactories.add(new ServiceFactory() {
            @Override
            public void bindServices(@NonNull Services.Builder builder) {
                if(builder.getKey() == parentKey) {
                    builder.withService("HELLO", parentService);
                }
            }
        });
        ServiceManager serviceManager = new ServiceManager(servicesFactories);
        serviceManager.setUp(backstackDelegate, childKey);

        assertThat(serviceManager.findServices(parentKey).<Object>getService("HELLO")).isSameAs(parentService);
    }

    @Test
    public void unbindingChildMakesParentServiceInaccessible() {
        final Parcelable parentKey = new TestKey("parent");
        ChildKey childKey = new ChildKey("child", parentKey);
        final Object parentService = new Object();
        List<ServiceFactory> servicesFactories = new ArrayList<>();
        servicesFactories.add(new ServiceFactory() {
            @Override
            public void bindServices(@NonNull Services.Builder builder) {
                if(builder.getKey() == parentKey) {
                    builder.withService("HELLO", parentService);
                }
            }
        });
        ServiceManager serviceManager = new ServiceManager(servicesFactories);
        serviceManager.setUp(backstackDelegate, childKey);
        serviceManager.tearDown(backstackDelegate, true, childKey);

        try {
            serviceManager.findServices(parentKey).getService("HELLO");
            fail();
        } catch(IllegalStateException e) {
            // OK!
        }
    }

    @Test
    public void childCanInheritParentServices() {
        final Parcelable parentKey = new TestKey("parent");
        ChildKey childKey = new ChildKey("child", parentKey);
        final Object parentService = new Object();
        List<ServiceFactory> servicesFactories = new ArrayList<>();
        servicesFactories.add(new ServiceFactory() {
            @Override
            public void bindServices(@NonNull Services.Builder builder) {
                if(builder.getKey() == parentKey) {
                    builder.withService("HELLO", parentService);
                }
            }
        });
        ServiceManager serviceManager = new ServiceManager(servicesFactories);
        serviceManager.setUp(backstackDelegate, childKey);

        assertThat(serviceManager.findServices(parentKey).<Object>getService("HELLO")).isEqualTo(parentService);
        assertThat(serviceManager.findServices(childKey).<Object>getService("HELLO")).isEqualTo(parentService);
        assertThat(serviceManager.findServices(childKey).<Object>getService("HELLO")).isEqualTo(serviceManager.findServices(parentKey)
                .getService("HELLO"));
    }

    @Test
    public void serviceNotFoundReturnsNull() {
        final Parcelable parentKey = new TestKey("parent");
        ChildKey childKey = new ChildKey("child", parentKey);
        final Object parentService = new Object();
        List<ServiceFactory> servicesFactories = new ArrayList<>();
        servicesFactories.add(new ServiceFactory() {
            @Override
            public void bindServices(@NonNull Services.Builder builder) {
                if(builder.getKey() == parentKey) {
                    builder.withService("HELLO", parentService);
                }
            }
        });
        ServiceManager serviceManager = new ServiceManager(servicesFactories);
        serviceManager.setUp(backstackDelegate, childKey);

        assertThat(serviceManager.findServices(childKey).<Object>getService("WORLD")).isNull();
    }


    @Test
    public void tearingDownKeyMultipleTimesThrowsException() {
        final Parcelable key = new TestKey("test");
        final Object parentService = new Object();
        List<ServiceFactory> servicesFactories = new ArrayList<>();
        servicesFactories.add(new ServiceFactory() {
            @Override
            public void bindServices(@NonNull Services.Builder builder) {
                if(builder.getKey() == key) {
                    builder.withService("HELLO", parentService);
                }
            }
        });
        ServiceManager serviceManager = new ServiceManager(servicesFactories);
        serviceManager.setUp(backstackDelegate, key);
        assertThat(serviceManager.findServices(key).<Object>getService("HELLO")).isEqualTo(parentService);
        serviceManager.setUp(backstackDelegate, key);
        assertThat(serviceManager.findServices(key).<Object>getService("HELLO")).isEqualTo(parentService);
        serviceManager.tearDown(backstackDelegate, true, key);
        assertThat(serviceManager.findServices(key).<Object>getService("HELLO")).isEqualTo(parentService);
        serviceManager.tearDown(backstackDelegate, true, key);
        try {
            serviceManager.tearDown(backstackDelegate, true, key);
            fail();
        } catch(IllegalStateException e) {
            // OK!
        }
    }

    @Test
    public void compositeChildrenHaveServicesCreated() {
        class CompositeKey extends TestKey implements Services.Composite {
            List<? extends Parcelable> keys;

            CompositeKey(String name, List<? extends Parcelable> keys) {
                super(name);
                this.keys = keys;
            }

            protected CompositeKey(Parcel in) {
                super(in);
            }

            @Override
            public List<? extends Parcelable> keys() {
                return keys;
            }
        }

        class ChildWithParentField
                extends TestKey implements Services.Child {
            CompositeKey parent;

            ChildWithParentField(String name) {
                super(name);
            }

            protected ChildWithParentField(Parcel in) {
                super(in);
            }

            @Override
            public Parcelable parent() {
                return parent;
            }
        }
        final ChildWithParentField childA = new ChildWithParentField("childA");
        final ChildWithParentField childB = new ChildWithParentField("childB");
        final CompositeKey composite = new CompositeKey("composite", Arrays.asList(childA, childB));
        childA.parent = composite;
        childB.parent = composite;

        final Object childAService = new Object();
        final Object childBService = new Object();
        final Object parentService = new Object();
        List<ServiceFactory> servicesFactories = new ArrayList<>();
        servicesFactories.add(new ServiceFactory() {
            @Override
            public void bindServices(@NonNull Services.Builder builder) {
                if(builder.getKey() == composite) {
                    builder.withService("HELLO", parentService);
                } else if(builder.getKey() == childA) {
                    builder.withService("WORLD", childAService);
                } else if(builder.getKey() == childB) {
                    builder.withService("CROCODILES", childBService);
                }
            }
        });
        ServiceManager serviceManager = new ServiceManager(servicesFactories);
        serviceManager.setUp(backstackDelegate, composite);

        assertThat(serviceManager.findServices(composite).<Object>getService("HELLO")).isSameAs(parentService);
        assertThat(serviceManager.findServices(childA).<Object>getService("WORLD")).isSameAs(childAService);
        assertThat(serviceManager.findServices(childB).<Object>getService("CROCODILES")).isSameAs(childBService);
        assertThat(serviceManager.findServices(childA).<Object>getService("HELLO")).isSameAs(parentService);
        assertThat(serviceManager.findServices(childB).<Object>getService("HELLO")).isSameAs(parentService);
    }

    @Test
    public void compositeChildrenHaveServicesTornDownWithParent() {
        class CompositeKey extends TestKey implements Services.Composite {
            List<? extends Parcelable> keys;

            CompositeKey(String name, List<? extends Parcelable> keys) {
                super(name);
                this.keys = keys;
            }

            protected CompositeKey(Parcel in) {
                super(in);
            }

            @Override
            public List<? extends Parcelable> keys() {
                return keys;
            }
        }

        class ChildWithParentField
                extends TestKey implements Services.Child {
            CompositeKey parent;

            ChildWithParentField(String name) {
                super(name);
            }

            protected ChildWithParentField(Parcel in) {
                super(in);
            }

            @Override
            public Parcelable parent() {
                return parent;
            }
        }

        final ChildWithParentField childA = new ChildWithParentField("childA");
        final ChildWithParentField childB = new ChildWithParentField("childB");
        final CompositeKey composite = new CompositeKey("composite", Arrays.asList(childA, childB));
        childA.parent = composite;
        childB.parent = composite;

        final Object childAService = new Object();
        final Object childBService = new Object();
        final Object parentService = new Object();
        List<ServiceFactory> servicesFactories = new ArrayList<>();
        servicesFactories.add(new ServiceFactory() {
            @Override
            public void bindServices(@NonNull Services.Builder builder) {
                if(builder.getKey() == composite) {
                    builder.withService("HELLO", parentService);
                } else if(builder.getKey() == childA) {
                    builder.withService("WORLD", childAService);
                } else if(builder.getKey() == childB) {
                    builder.withService("CROCODILES", childBService);
                }
            }
        });
        ServiceManager serviceManager = new ServiceManager(servicesFactories);
        serviceManager.setUp(backstackDelegate, composite);
        assertThat(serviceManager.findServices(composite).<Object>getService("HELLO")).isSameAs(parentService);
        assertThat(serviceManager.findServices(childA).<Object>getService("WORLD")).isSameAs(childAService);
        assertThat(serviceManager.findServices(childB).<Object>getService("CROCODILES")).isSameAs(childBService);
        assertThat(serviceManager.findServices(childA).<Object>getService("HELLO")).isSameAs(parentService);
        assertThat(serviceManager.findServices(childB).<Object>getService("HELLO")).isSameAs(parentService);

        serviceManager.tearDown(backstackDelegate, true, composite);
        try {
            assertThat(serviceManager.findServices(composite).<Object>getService("HELLO")).isSameAs(parentService);
            fail();
        } catch(IllegalStateException e) {
            // OK!
        }

        try {
            assertThat(serviceManager.findServices(childA).<Object>getService("WORLD")).isSameAs(childAService);
            fail();
        } catch(IllegalStateException e) {
            // OK!
        }

        try {
            assertThat(serviceManager.findServices(childB).<Object>getService("CROCODILES")).isSameAs(childBService);
            fail();
        } catch(IllegalStateException e) {
            // OK!
        }

        try {
            assertThat(serviceManager.findServices(childA).<Object>getService("HELLO")).isSameAs(parentService);
            fail();
        } catch(IllegalStateException e) {
            // OK!
        }

        try {
            assertThat(serviceManager.findServices(childB).<Object>getService("HELLO")).isSameAs(parentService);
            fail();
        } catch(IllegalStateException e) {
            // OK!
        }
    }

    @Test
    public void compositeNestedServicesWork() {
        class CompositeChild extends TestKey
                implements Services.Child, Services.Composite {
            private Parcelable parent;
            private List<? extends Parcelable> children;

            private CompositeChild(String name, List<? extends Parcelable> children) {
                this(name, null, children);
            }

            public CompositeChild(String name, Parcelable parent, List<? extends Parcelable> children) {
                super(name);
                this.parent = parent;
                this.children = children;
            }

            @Override
            public Parcelable parent() {
                return parent;
            }

            @Override
            public List<? extends Parcelable> keys() {
                return children;
            }
        }

        class Child extends TestKey
                implements Services.Child {
            private Parcelable parent;

            private Child(String name) {
                super(name);
            }

            public Child(String name, Parcelable parent) {
                super(name);
                this.parent = parent;
            }

            @Override
            public Parcelable parent() {
                return parent;
            }
        }

        final TestKey _A = new TestKey("A");

        final Child _C = new Child("C");
        final Child _E = new Child("E");
        final Child _F = new Child("F");
        final Child _G = new Child("G");

        final CompositeChild _D = new CompositeChild("D", Arrays.asList(_F, _G));

        final CompositeChild _B = new CompositeChild("B", Arrays.asList(_C, _D, _E));

        _B.parent = _A;
        _C.parent = _B;
        _D.parent = _B;
        _E.parent = _B;
        _F.parent = _D;
        _G.parent = _D;

        List<ServiceFactory> servicesFactories = new ArrayList<>();
        servicesFactories.add(new ServiceFactory() {
            @Override
            public void bindServices(@NonNull Services.Builder builder) {
                if(builder.getKey() == _A) {
                    builder.withService("A", "A");
                } else if(builder.getKey() == _B) {
                    builder.withService("B", "B");
                } else if(builder.getKey() == _C) {
                    builder.withService("C", "C");
                } else if(builder.getKey() == _D) {
                    builder.withService("D", "D");
                } else if(builder.getKey() == _E) {
                    builder.withService("E", "E");
                } else if(builder.getKey() == _F) {
                    builder.withService("F", "F");
                } else if(builder.getKey() == _G) {
                    builder.withService("G", "G");
                }
            }
        });
        ServiceManager serviceManager = new ServiceManager(servicesFactories);
        serviceManager.setUp(backstackDelegate, _B);

        /**
         *
         *          A       ----        B
         *                            / | \
         *                           /  |  \
         *                          C   D   E
         *                             / \
         *                            /   \
         *                           F     G
         */
        assertThat(serviceManager.findServices(_A).<String>getService("A")).isEqualTo("A");
        assertThat(serviceManager.findServices(_B).<String>getService("A")).isEqualTo("A");
        assertThat(serviceManager.findServices(_B).<String>getService("B")).isEqualTo("B");
        assertThat(serviceManager.findServices(_C).<String>getService("A")).isEqualTo("A");
        assertThat(serviceManager.findServices(_C).<String>getService("B")).isEqualTo("B");
        assertThat(serviceManager.findServices(_C).<String>getService("C")).isEqualTo("C");
        assertThat(serviceManager.findServices(_D).<String>getService("A")).isEqualTo("A");
        assertThat(serviceManager.findServices(_D).<String>getService("B")).isEqualTo("B");
        assertThat(serviceManager.findServices(_D).<String>getService("D")).isEqualTo("D");
        assertThat(serviceManager.findServices(_E).<String>getService("A")).isEqualTo("A");
        assertThat(serviceManager.findServices(_E).<String>getService("B")).isEqualTo("B");
        assertThat(serviceManager.findServices(_E).<String>getService("E")).isEqualTo("E");
        assertThat(serviceManager.findServices(_F).<String>getService("A")).isEqualTo("A");
        assertThat(serviceManager.findServices(_F).<String>getService("B")).isEqualTo("B");
        assertThat(serviceManager.findServices(_F).<String>getService("D")).isEqualTo("D");
        assertThat(serviceManager.findServices(_F).<String>getService("F")).isEqualTo("F");
        assertThat(serviceManager.findServices(_G).<String>getService("A")).isEqualTo("A");
        assertThat(serviceManager.findServices(_G).<String>getService("B")).isEqualTo("B");
        assertThat(serviceManager.findServices(_G).<String>getService("D")).isEqualTo("D");
        assertThat(serviceManager.findServices(_G).<String>getService("G")).isEqualTo("G");

        serviceManager.tearDown(backstackDelegate, true, _B);

        try {
            assertThat(serviceManager.findServices(_A).<String>getService("A")).isEqualTo("A");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_B).<String>getService("A")).isEqualTo("A");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_B).<String>getService("B")).isEqualTo("B");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_C).<String>getService("A")).isEqualTo("A");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_C).<String>getService("B")).isEqualTo("B");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_C).<String>getService("C")).isEqualTo("C");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_D).<String>getService("A")).isEqualTo("A");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_D).<String>getService("B")).isEqualTo("B");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_D).<String>getService("D")).isEqualTo("D");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_E).<String>getService("A")).isEqualTo("A");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_E).<String>getService("B")).isEqualTo("B");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_E).<String>getService("E")).isEqualTo("E");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_F).<String>getService("A")).isEqualTo("A");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_F).<String>getService("B")).isEqualTo("B");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_F).<String>getService("D")).isEqualTo("D");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_F).<String>getService("F")).isEqualTo("F");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_G).<String>getService("A")).isEqualTo("A");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_G).<String>getService("B")).isEqualTo("B");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_G).<String>getService("D")).isEqualTo("D");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_G).<String>getService("G")).isEqualTo("G");
            fail();
        } catch(IllegalStateException e) { /* OK */ }


        /////
        serviceManager.setUp(backstackDelegate, _D);

        assertThat(serviceManager.findServices(_A).<String>getService("A")).isEqualTo("A");
        assertThat(serviceManager.findServices(_B).<String>getService("A")).isEqualTo("A");
        assertThat(serviceManager.findServices(_B).<String>getService("B")).isEqualTo("B");
        assertThat(serviceManager.findServices(_C).<String>getService("A")).isEqualTo("A");
        assertThat(serviceManager.findServices(_C).<String>getService("B")).isEqualTo("B");
        assertThat(serviceManager.findServices(_C).<String>getService("C")).isEqualTo("C");
        assertThat(serviceManager.findServices(_D).<String>getService("A")).isEqualTo("A");
        assertThat(serviceManager.findServices(_D).<String>getService("B")).isEqualTo("B");
        assertThat(serviceManager.findServices(_D).<String>getService("D")).isEqualTo("D");
        assertThat(serviceManager.findServices(_E).<String>getService("A")).isEqualTo("A");
        assertThat(serviceManager.findServices(_E).<String>getService("B")).isEqualTo("B");
        assertThat(serviceManager.findServices(_E).<String>getService("E")).isEqualTo("E");
        assertThat(serviceManager.findServices(_F).<String>getService("A")).isEqualTo("A");
        assertThat(serviceManager.findServices(_F).<String>getService("B")).isEqualTo("B");
        assertThat(serviceManager.findServices(_F).<String>getService("D")).isEqualTo("D");
        assertThat(serviceManager.findServices(_F).<String>getService("F")).isEqualTo("F");
        assertThat(serviceManager.findServices(_G).<String>getService("A")).isEqualTo("A");
        assertThat(serviceManager.findServices(_G).<String>getService("B")).isEqualTo("B");
        assertThat(serviceManager.findServices(_G).<String>getService("D")).isEqualTo("D");
        assertThat(serviceManager.findServices(_G).<String>getService("G")).isEqualTo("G");

        serviceManager.setUp(backstackDelegate, _A);
        serviceManager.tearDown(backstackDelegate, true, _D);

        assertThat(serviceManager.findServices(_A).<String>getService("A")).isEqualTo("A");
        try {
            assertThat(serviceManager.findServices(_B).<String>getService("A")).isEqualTo("A");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_B).<String>getService("B")).isEqualTo("B");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_C).<String>getService("A")).isEqualTo("A");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_C).<String>getService("B")).isEqualTo("B");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_C).<String>getService("C")).isEqualTo("C");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_D).<String>getService("A")).isEqualTo("A");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_D).<String>getService("B")).isEqualTo("B");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_D).<String>getService("D")).isEqualTo("D");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_E).<String>getService("A")).isEqualTo("A");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_E).<String>getService("B")).isEqualTo("B");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_E).<String>getService("E")).isEqualTo("E");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_F).<String>getService("A")).isEqualTo("A");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_F).<String>getService("B")).isEqualTo("B");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_F).<String>getService("D")).isEqualTo("D");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_F).<String>getService("F")).isEqualTo("F");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_G).<String>getService("A")).isEqualTo("A");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_G).<String>getService("B")).isEqualTo("B");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_G).<String>getService("D")).isEqualTo("D");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_G).<String>getService("G")).isEqualTo("G");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
    }

    @Test
    public void compositeUnidirectionalServicesWork() {
        class CompositeChild extends TestKey
                implements Services.Composite, Services.Child {
            private Parcelable parent;
            private List<? extends Parcelable> children;

            public CompositeChild(String name, Parcelable parent, List<? extends Parcelable> children) {
                super(name);
                this.parent = parent;
                this.children = children;
            }

            @Override
            public List<? extends Parcelable> keys() {
                return children;
            }

            @Override
            public Parcelable parent() {
                return parent;
            }
        }

        class Composite extends TestKey
                implements Services.Composite {
            private List<? extends Parcelable> children;

            public Composite(String name, List<? extends Parcelable> children) {
                super(name);
                this.children = children;
            }

            @Override
            public List<? extends Parcelable> keys() {
                return children;
            }
        }

        class Child extends TestKey
                implements Services.Child {
            private Parcelable parent;

            private Child(String name) {
                super(name);
            }

            public Child(String name, Parcelable parent) {
                super(name);
                this.parent = parent;
            }

            @Override
            public Parcelable parent() {
                return parent;
            }
        }

        final TestKey _A = new TestKey("A");

        final Child _C = new Child("C");
        final Child _E = new Child("E");
        final Child _F = new Child("F");
        final Child _G = new Child("G");

        final Composite _D = new Composite("D", Arrays.asList(_F, _G));

        final CompositeChild _B = new CompositeChild("B", _A, Arrays.asList(_C, _D, _E));

        _C.parent = _B;
        //_D.parent = _B;
        _E.parent = _B;
        _F.parent = _D;
        _G.parent = _D;

        List<ServiceFactory> servicesFactories = new ArrayList<>();
        servicesFactories.add(new ServiceFactory() {
            @Override
            public void bindServices(@NonNull Services.Builder builder) {
                if(builder.getKey() == _A) {
                    builder.withService("A", "A");
                } else if(builder.getKey() == _B) {
                    builder.withService("B", "B");
                } else if(builder.getKey() == _C) {
                    builder.withService("C", "C");
                } else if(builder.getKey() == _D) {
                    builder.withService("D", "D");
                } else if(builder.getKey() == _E) {
                    builder.withService("E", "E");
                } else if(builder.getKey() == _F) {
                    builder.withService("F", "F");
                } else if(builder.getKey() == _G) {
                    builder.withService("G", "G");
                }
            }
        });
        ServiceManager serviceManager = new ServiceManager(servicesFactories);
        serviceManager.setUp(backstackDelegate, _B);

        /**
         *
         *          A       ----        B
         *                            / | \
         *                           /  |  \
         *                          C   D   E
         *                             / \
         *                            /   \
         *                           F     G
         */
        assertThat(serviceManager.findServices(_A).<String>getService("A")).isEqualTo("A");
        assertThat(serviceManager.findServices(_B).<String>getService("A")).isEqualTo("A");
        assertThat(serviceManager.findServices(_B).<String>getService("B")).isEqualTo("B");
        assertThat(serviceManager.findServices(_C).<String>getService("A")).isEqualTo("A");
        assertThat(serviceManager.findServices(_C).<String>getService("B")).isEqualTo("B");
        assertThat(serviceManager.findServices(_C).<String>getService("C")).isEqualTo("C");
        assertThat(serviceManager.findServices(_D).<String>getService("A")).isEqualTo("A");
        assertThat(serviceManager.findServices(_D).<String>getService("B")).isEqualTo("B");
        assertThat(serviceManager.findServices(_D).<String>getService("D")).isEqualTo("D");
        assertThat(serviceManager.findServices(_E).<String>getService("A")).isEqualTo("A");
        assertThat(serviceManager.findServices(_E).<String>getService("B")).isEqualTo("B");
        assertThat(serviceManager.findServices(_E).<String>getService("E")).isEqualTo("E");
        assertThat(serviceManager.findServices(_F).<String>getService("A")).isEqualTo("A");
        assertThat(serviceManager.findServices(_F).<String>getService("B")).isEqualTo("B");
        assertThat(serviceManager.findServices(_F).<String>getService("D")).isEqualTo("D");
        assertThat(serviceManager.findServices(_F).<String>getService("F")).isEqualTo("F");
        assertThat(serviceManager.findServices(_G).<String>getService("A")).isEqualTo("A");
        assertThat(serviceManager.findServices(_G).<String>getService("B")).isEqualTo("B");
        assertThat(serviceManager.findServices(_G).<String>getService("D")).isEqualTo("D");
        assertThat(serviceManager.findServices(_G).<String>getService("G")).isEqualTo("G");

        serviceManager.tearDown(backstackDelegate, true, _B);

        try {
            assertThat(serviceManager.findServices(_A).<String>getService("A")).isEqualTo("A");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_B).<String>getService("A")).isEqualTo("A");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_B).<String>getService("B")).isEqualTo("B");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_C).<String>getService("A")).isEqualTo("A");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_C).<String>getService("B")).isEqualTo("B");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_C).<String>getService("C")).isEqualTo("C");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_D).<String>getService("A")).isEqualTo("A");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_D).<String>getService("B")).isEqualTo("B");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_D).<String>getService("D")).isEqualTo("D");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_E).<String>getService("A")).isEqualTo("A");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_E).<String>getService("B")).isEqualTo("B");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_E).<String>getService("E")).isEqualTo("E");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_F).<String>getService("A")).isEqualTo("A");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_F).<String>getService("B")).isEqualTo("B");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_F).<String>getService("D")).isEqualTo("D");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_F).<String>getService("F")).isEqualTo("F");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_G).<String>getService("A")).isEqualTo("A");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_G).<String>getService("B")).isEqualTo("B");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_G).<String>getService("D")).isEqualTo("D");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_G).<String>getService("G")).isEqualTo("G");
            fail();
        } catch(IllegalStateException e) { /* OK */ }


        /////
        serviceManager.setUp(backstackDelegate, _D);

        //assertThat(serviceManager.findServices(_A).<String>getService("A")).isEqualTo("A");
        //assertThat(serviceManager.findServices(_B).<String>getService("A")).isEqualTo("A");
        //assertThat(serviceManager.findServices(_B).<String>getService("B")).isEqualTo("B");
        //assertThat(serviceManager.findServices(_C).<String>getService("A")).isEqualTo("A");
        //assertThat(serviceManager.findServices(_C).<String>getService("B")).isEqualTo("B");
        //assertThat(serviceManager.findServices(_C).<String>getService("C")).isEqualTo("C");
        //assertThat(serviceManager.findServices(_D).<String>getService("A")).isEqualTo("A");
        //assertThat(serviceManager.findServices(_D).<String>getService("B")).isEqualTo("B");
        assertThat(serviceManager.findServices(_D).<String>getService("D")).isEqualTo("D");
        //assertThat(serviceManager.findServices(_E).<String>getService("A")).isEqualTo("A");
        //assertThat(serviceManager.findServices(_E).<String>getService("B")).isEqualTo("B");
        //assertThat(serviceManager.findServices(_E).<String>getService("E")).isEqualTo("E");
        //assertThat(serviceManager.findServices(_F).<String>getService("A")).isEqualTo("A");
        //assertThat(serviceManager.findServices(_F).<String>getService("B")).isEqualTo("B");
        assertThat(serviceManager.findServices(_F).<String>getService("D")).isEqualTo("D");
        assertThat(serviceManager.findServices(_F).<String>getService("F")).isEqualTo("F");
        //assertThat(serviceManager.findServices(_G).<String>getService("A")).isEqualTo("A");
        //assertThat(serviceManager.findServices(_G).<String>getService("B")).isEqualTo("B");
        assertThat(serviceManager.findServices(_G).<String>getService("D")).isEqualTo("D");
        assertThat(serviceManager.findServices(_G).<String>getService("G")).isEqualTo("G");

        serviceManager.tearDown(backstackDelegate, true, _D);

        try {
            assertThat(serviceManager.findServices(_A).<String>getService("A")).isEqualTo("A");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_B).<String>getService("A")).isEqualTo("A");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_B).<String>getService("B")).isEqualTo("B");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_C).<String>getService("A")).isEqualTo("A");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_C).<String>getService("B")).isEqualTo("B");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_C).<String>getService("C")).isEqualTo("C");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_D).<String>getService("A")).isEqualTo("A");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_D).<String>getService("B")).isEqualTo("B");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_D).<String>getService("D")).isEqualTo("D");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_E).<String>getService("A")).isEqualTo("A");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_E).<String>getService("B")).isEqualTo("B");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_E).<String>getService("E")).isEqualTo("E");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_F).<String>getService("A")).isEqualTo("A");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_F).<String>getService("B")).isEqualTo("B");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_F).<String>getService("D")).isEqualTo("D");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_F).<String>getService("F")).isEqualTo("F");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_G).<String>getService("A")).isEqualTo("A");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_G).<String>getService("B")).isEqualTo("B");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_G).<String>getService("D")).isEqualTo("D");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
        try {
            assertThat(serviceManager.findServices(_G).<String>getService("G")).isEqualTo("G");
            fail();
        } catch(IllegalStateException e) { /* OK */ }
    }
}