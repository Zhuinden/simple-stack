package com.zhuinden.simplestack;

import android.support.annotation.NonNull;

import com.zhuinden.simplestack.ServiceFactory;
import com.zhuinden.simplestack.Services;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

public class ServicesTest {
    @Test
    public void serviceFactoryBindsServices() {
        Object key = new Object();
        final Object service = new Object();
        List<ServiceFactory> servicesFactories = new ArrayList<>();
        servicesFactories.add(new ServiceFactory() {
            @Override
            public void bindServices(@NonNull Services.Builder builder) {
                builder.withService("HELLO", service);
            }
        });
        ServiceManager serviceManager = new ServiceManager(servicesFactories);
        serviceManager.setUp(key);

        assertThat(serviceManager.findServices(key).<Object>getService("HELLO")).isSameAs(service);
    }

    @Test
    public void unbindingServiceMakesItInaccessible() {
        Object key = new Object();
        final Object service = new Object();
        List<ServiceFactory> servicesFactories = new ArrayList<>();
        servicesFactories.add(new ServiceFactory() {
            @Override
            public void bindServices(@NonNull Services.Builder builder) {
                builder.withService("HELLO", service);
            }
        });
        ServiceManager serviceManager = new ServiceManager(servicesFactories);
        serviceManager.setUp(key);
        serviceManager.tearDown(key);

        try {
            serviceManager.findServices(key).getService("HELLO");
            fail();
        } catch(IllegalStateException e) {
            // OK!
        }
    }

    @Test
    public void bindingChildCreatesServiceOfParent() {
        final Object parentKey = new Object();
        Services.Child childKey = new Services.Child() {
            @Override
            public Object parent() {
                return parentKey;
            }
        };
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
        serviceManager.setUp(childKey);

        assertThat(serviceManager.findServices(parentKey).<Object>getService("HELLO")).isSameAs(parentService);
    }

    @Test
    public void unbindingChildMakesParentServiceInaccessible() {
        final Object parentKey = new Object();
        Services.Child childKey = new Services.Child() {
            @Override
            public Object parent() {
                return parentKey;
            }
        };
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
        serviceManager.setUp(childKey);
        serviceManager.tearDown(childKey);

        try {
            serviceManager.findServices(parentKey).getService("HELLO");
            fail();
        } catch(IllegalStateException e) {
            // OK!
        }
    }

    @Test
    public void childCanInheritParentServices() {
        final Object parentKey = new Object();
        Services.Child childKey = new Services.Child() {
            @Override
            public Object parent() {
                return parentKey;
            }
        };
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
        serviceManager.setUp(childKey);

        assertThat(serviceManager.findServices(parentKey).<Object>getService("HELLO")).isEqualTo(parentService);
        assertThat(serviceManager.findServices(childKey).<Object>getService("HELLO")).isEqualTo(parentService);
        assertThat(serviceManager.findServices(childKey).<Object>getService("HELLO")).isEqualTo(serviceManager.findServices(parentKey)
                .getService("HELLO"));
    }

    @Test
    public void serviceNotFoundReturnsNull() {
        final Object parentKey = new Object();
        Services.Child childKey = new Services.Child() {
            @Override
            public Object parent() {
                return parentKey;
            }
        };
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
        serviceManager.setUp(childKey);

        assertThat(serviceManager.findServices(childKey).<Object>getService("WORLD")).isNull();
    }


    @Test
    public void tearingDownKeyMultipleTimesThrowsException() {
        final Object key = new Object();
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
        serviceManager.setUp(key);
        assertThat(serviceManager.findServices(key).<Object>getService("HELLO")).isEqualTo(parentService);
        serviceManager.setUp(key);
        assertThat(serviceManager.findServices(key).<Object>getService("HELLO")).isEqualTo(parentService);
        serviceManager.tearDown(key);
        assertThat(serviceManager.findServices(key).<Object>getService("HELLO")).isEqualTo(parentService);
        serviceManager.tearDown(key);
        try {
            serviceManager.tearDown(key);
            fail();
        } catch(IllegalStateException e) {
            // OK!
        }
    }

    @Test
    public void compositeChildrenHaveServicesCreated() {
        class ChildWithParentField
                implements Services.Child {
            Services.Composite parent;

            @Override
            public Object parent() {
                return parent;
            }
        }
        final ChildWithParentField childA = new ChildWithParentField();
        final ChildWithParentField childB = new ChildWithParentField();
        final Services.Composite composite = new Services.Composite() {
            @Override
            public List<ChildWithParentField> keys() {
                return Arrays.asList(childA, childB);
            }
        };
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
        serviceManager.setUp(composite);

        assertThat(serviceManager.findServices(composite).<Object>getService("HELLO")).isSameAs(parentService);
        assertThat(serviceManager.findServices(childA).<Object>getService("WORLD")).isSameAs(childAService);
        assertThat(serviceManager.findServices(childB).<Object>getService("CROCODILES")).isSameAs(childBService);
        assertThat(serviceManager.findServices(childA).<Object>getService("HELLO")).isSameAs(parentService);
        assertThat(serviceManager.findServices(childB).<Object>getService("HELLO")).isSameAs(parentService);
    }

    @Test
    public void compositeChildrenHaveServicesTornDownWithParent() {
        class ChildWithParentField
                implements Services.Child {
            Services.Composite parent;

            @Override
            public Object parent() {
                return parent;
            }
        }
        final ChildWithParentField childA = new ChildWithParentField();
        final ChildWithParentField childB = new ChildWithParentField();
        final Services.Composite composite = new Services.Composite() {
            @Override
            public List<ChildWithParentField> keys() {
                return Arrays.asList(childA, childB);
            }
        };
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
        serviceManager.setUp(composite);
        assertThat(serviceManager.findServices(composite).<Object>getService("HELLO")).isSameAs(parentService);
        assertThat(serviceManager.findServices(childA).<Object>getService("WORLD")).isSameAs(childAService);
        assertThat(serviceManager.findServices(childB).<Object>getService("CROCODILES")).isSameAs(childBService);
        assertThat(serviceManager.findServices(childA).<Object>getService("HELLO")).isSameAs(parentService);
        assertThat(serviceManager.findServices(childB).<Object>getService("HELLO")).isSameAs(parentService);

        serviceManager.tearDown(composite);
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
        class CompositeChild
                implements Services.Child, Services.Composite {
            private Object parent;
            private List<?> children;

            private CompositeChild(List<?> children) {
                this(null, children);
            }

            public CompositeChild(Object parent, List<?> children) {
                this.parent = parent;
                this.children = children;
            }

            @Override
            public Object parent() {
                return parent;
            }

            @Override
            public List<?> keys() {
                return children;
            }
        }

        class Child
                implements Services.Child {
            private Object parent;

            private Child() {
            }

            public Child(Object parent) {
                this.parent = parent;
            }

            @Override
            public Object parent() {
                return parent;
            }
        }

        final Object _A = new Object();

        final Child _C = new Child();
        final Child _E = new Child();
        final Child _F = new Child();
        final Child _G = new Child();

        final CompositeChild _D = new CompositeChild(Arrays.asList(_F, _G));

        final CompositeChild _B = new CompositeChild(Arrays.asList(_C, _D, _E));

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
        serviceManager.setUp(_B);

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

        serviceManager.tearDown(_B);

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
        serviceManager.setUp(_D);

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

        serviceManager.setUp(_A);
        serviceManager.tearDown(_D);

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
        class CompositeChild
                implements Services.Composite, Services.Child {
            private Object parent;
            private List<?> children;

            public CompositeChild(Object parent, List<?> children) {
                this.parent = parent;
                this.children = children;
            }

            @Override
            public List<?> keys() {
                return children;
            }

            @Override
            public Object parent() {
                return parent;
            }
        }

        class Composite
                implements Services.Composite {
            private List<?> children;

            public Composite(List<?> children) {
                this.children = children;
            }

            @Override
            public List<?> keys() {
                return children;
            }
        }

        class Child
                implements Services.Child {
            private Object parent;

            private Child() {
            }

            public Child(Object parent) {
                this.parent = parent;
            }

            @Override
            public Object parent() {
                return parent;
            }
        }

        final Object _A = new Object();

        final Child _C = new Child();
        final Child _E = new Child();
        final Child _F = new Child();
        final Child _G = new Child();

        final Composite _D = new Composite(Arrays.asList(_F, _G));

        final CompositeChild _B = new CompositeChild(_A, Arrays.asList(_C, _D, _E));

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
        serviceManager.setUp(_B);

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

        serviceManager.tearDown(_B);

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
        serviceManager.setUp(_D);

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

        serviceManager.tearDown(_D);

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