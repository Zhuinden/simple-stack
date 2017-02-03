package com.zhuinden.demo;

/**
 * Created by Owner on 2017. 02. 03..
 */

public abstract class BaseKey
        implements Key {
    @Override
    public String getFragmentTag() {
        return toString();
    }
}
