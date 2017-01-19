package com.zhuinden.simplestackdemo;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by Owner on 2017. 01. 17..
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ExampleUnitTest.class, FlowTest.class, ReentranceTest.class})
public class TestSuite {
}
