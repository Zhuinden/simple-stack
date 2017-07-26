package com.zhuinden.simplestackexamplemvvm.application.injection;

import com.zhuinden.simplestackexamplemvvm.application.CustomApplication;

/**
 * Created by Zhuinden on 2017.07.25..
 */

public class Injection {
    public static ApplicationComponent get() {
        return CustomApplication.get().appComponent();
    }
}
