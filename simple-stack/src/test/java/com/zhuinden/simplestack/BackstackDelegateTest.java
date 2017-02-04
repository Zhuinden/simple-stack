package com.zhuinden.simplestack;

import android.os.Parcelable;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.fail;

/**
 * Created by Zhuinden on 2017.02.04..
 */

public class BackstackDelegateTest {


    @Test
    public void setNullPersistenceTagShouldThrow() {
        BackstackDelegate backstackDelegate = new BackstackDelegate(null);
        try {
            backstackDelegate.setPersistenceTag(null);
            fail();
        } catch(IllegalArgumentException e) {
            // OK!
        }
    }

    @Test
    public void setSamePersistenceTagTwiceShouldBeOk() {
        BackstackDelegate backstackDelegate = new BackstackDelegate(null);
        backstackDelegate.setPersistenceTag(new String("hello"));
        backstackDelegate.setPersistenceTag(new String("hello"));
        // no exceptions thrown
    }

    @Test
    public void setTwoDifferentPersistenceTagsShouldThrow() {
        BackstackDelegate backstackDelegate = new BackstackDelegate(null);
        backstackDelegate.setPersistenceTag(new String("hello"));
        try {
            backstackDelegate.setPersistenceTag(new String("world"));
            fail();
        } catch(IllegalStateException e) {
            // OK!
        }
    }

    @Test
    public void setPersistenceTagAfterOnCreateShouldThrow() {
        BackstackDelegate backstackDelegate = new BackstackDelegate(null);
        backstackDelegate.onCreate(null, null, new ArrayList<Parcelable>() {{
            add(new TestKey("hello"));
        }});
        try {
            backstackDelegate.setPersistenceTag(new String("world"));
            fail();
        } catch(IllegalStateException e) {
            // OK!
        }
    }
}
