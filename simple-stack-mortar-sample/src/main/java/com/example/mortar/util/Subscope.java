package com.example.mortar.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by Zhuinden on 2017.05.12..
 */

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface Subscope {
}
