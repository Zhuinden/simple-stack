package com.zhuinden.simplestack;

import android.content.Context;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Owner on 2017. 02. 21..
 */
public class ManagedContextWrapperTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    Context context;

    @Mock
    Services services;

    private static final Object KEY = new Object();

    ManagedContextWrapper managedContextWrapper;

    @Before
    public void setup() {
        managedContextWrapper = new ManagedContextWrapper(context, KEY, services);
    }

    @Test
    public void keyIsAcquiredByTag()
            throws Exception {
        // noinspection ResourceType
        Object key = managedContextWrapper.getSystemService(ManagedContextWrapper.TAG);
        assertThat(key).isSameAs(KEY);
    }

    @Test
    public void keyIsAcquiredThroughBackstack() {
        assertThat(Backstack.getKey(managedContextWrapper)).isSameAs(KEY);
    }

    @Test
    public void servicesGetDelegatedToServices() {
        Object nyeh = new Object();
        Mockito.when(services.getService("nyeh")).thenReturn(nyeh);
        // noinspection ResourceType
        Object blah = managedContextWrapper.getSystemService("nyeh");
        Mockito.verify(services, Mockito.only()).getService("nyeh");
        assertThat(blah).isSameAs(nyeh);
    }
}