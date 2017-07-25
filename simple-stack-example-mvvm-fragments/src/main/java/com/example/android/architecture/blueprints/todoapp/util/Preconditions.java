package com.example.android.architecture.blueprints.todoapp.util;

/**
 * Created by Owner on 2017. 07. 25..
 */

public class Preconditions {
    public static <T> T checkNotNull(T object) {
        if(object == null) {
            throw new NullPointerException("object == null");
        }
        return object;
    }

    public static void checkArgument(boolean isTrue, String errorMessage) {
        if(!isTrue) {
            throw new RuntimeException(errorMessage);
        }
    }
}
