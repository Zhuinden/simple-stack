package com.example.fragmenttransitions;

/**
 * Created by Owner on 2017. 08. 08..
 */

public interface HasSharedElement {
    SharedElement sharedElement();

    interface Target {
        SharedElement sharedElement();
    }
}
