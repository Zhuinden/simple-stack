package com.zhuinden.simplestackdemoexamplefragments.application;

import com.zhuinden.simplestackdemoexamplefragments.application.injection.SingletonComponent;

/**
 * Created by Owner on 2017. 02. 03..
 */

public class Injector {
    public static SingletonComponent get() {
        return CustomApplication.get().getComponent();
    }
}
