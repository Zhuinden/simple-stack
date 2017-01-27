package com.zhuinden.simplestackdemoexamplemvp.util;

import android.support.annotation.Nullable;

/**
 * Created by Owner on 2017. 01. 27..
 */

public class Strings {
    public static boolean isNullOrEmpty(@Nullable String string) {
        return string == null || string.length() == 0;
    }
}
