package com.example.android.architecture.blueprints.todoapp.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Owner on 2017. 07. 25..
 */

public class Lists {
    public static <T> List<T> newArrayList(Collection<T> objects) {
        List<T> list = new LinkedList<>();
        for(T t : objects) {
            list.add(t);
        }
        return new ArrayList<T>(list);
    }

    public static <T> List<T> newArrayList(T[] objects) {
        List<T> list = new ArrayList<T>(objects.length);
        for(T t : objects) {
            list.add(t);
        }
        return list;
    }
}
