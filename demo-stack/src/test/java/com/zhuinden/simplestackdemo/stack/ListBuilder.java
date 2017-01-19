package com.zhuinden.simplestackdemo.stack;

import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by Owner on 2017. 01. 16..
 */


class ListBuilder {
    private List<Parcelable> list = new ArrayList<>();

    public static ListBuilder emptyBuilder() {
        return new ListBuilder();
    }

    public static List<Parcelable> single(Parcelable parcelable) {
        ListBuilder listBuilder = new ListBuilder();
        listBuilder.list.add(parcelable);
        return listBuilder.list;
    }

    public ListBuilder pushAll(Parcelable[] collection) {
        this.list.addAll(Arrays.asList(collection));
        return this;
    }

    public ListBuilder pushAll(Collection<? extends Parcelable> collection) {
        this.list.addAll(collection);
        return this;
    }

    public ListBuilder removeLast() {
        if(list.size() > 0) {
            list.remove(list.size() - 1);
        }
        return this;
    }

    public ListBuilder push(Parcelable parcelable) {
        list.add(parcelable);
        return this;
    }

    public List<Parcelable> build() {
        return list;
    }
}