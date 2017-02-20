package com.zhuinden.simplestackdemomultistack.util;

import android.content.Context;

/**
 * Created by Zhuinden on 2017.02.19..
 */

public class ServiceLocator {
    public static <T> T getService(Context context, String name) {
        // noinspection unchecked
        return (T) context.getSystemService(name);
    }
}
