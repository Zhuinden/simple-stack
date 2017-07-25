package com.example.android.architecture.blueprints.todoapp.util;

import android.support.annotation.Nullable;

import java.util.Arrays;

/**
 * Created by Owner on 2017. 07. 25..
 */

public class Objects {
    public static int hashCode(Object... objects) {
        return Arrays.hashCode(objects);
    }

    public static boolean equal(@Nullable Object a, @Nullable Object b) {
        return a == b || (a != null && a.equals(b));
    }
}
