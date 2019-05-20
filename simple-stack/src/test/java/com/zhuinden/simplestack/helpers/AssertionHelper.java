package com.zhuinden.simplestack.helpers;

import org.junit.Assert;

public class AssertionHelper {
    private AssertionHelper() {
    }

    public static void assertThrows(Action action) {
        try {
            action.doSomething();
            Assert.fail("Did not throw exception.");
        } catch(Exception e) {
            // OK!
        }
    }
}
