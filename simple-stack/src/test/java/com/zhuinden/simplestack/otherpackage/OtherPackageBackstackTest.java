package com.zhuinden.simplestack.otherpackage;

import com.zhuinden.simplestack.Backstack;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Owner on 2017. 02. 20..
 */

public class OtherPackageBackstackTest {
    @Test
    public void initialKeysShouldNotBeEmpty() {
        try {
            Backstack backstack = new Backstack();
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // good!
        }
    }
}
