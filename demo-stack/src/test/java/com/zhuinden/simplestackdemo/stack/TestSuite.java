package com.zhuinden.simplestackdemo.stack;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by Owner on 2017. 01. 17..
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ExampleUnitTest.class, FlowTest.class, ReentranceTest.class, BackstackTest.class})
public class TestSuite {
}
