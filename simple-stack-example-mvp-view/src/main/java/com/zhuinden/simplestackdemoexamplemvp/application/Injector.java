package com.zhuinden.simplestackdemoexamplemvp.application;

import com.zhuinden.simplestackdemoexamplemvp.application.injection.SingletonComponent;

/**
 * Created by Zhuinden on 2017.03.27..
 */

public class Injector {
    private Injector() {
    }

    public static SingletonComponent get() {
        return CustomApplication.get().getComponent();
    }
}
