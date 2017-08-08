package com.example.fragmenttransitions;

import android.support.v4.util.Pair;
import android.view.View;

/**
 * Created by Owner on 2017. 08. 08..
 */

public interface HasSharedElement {
    Pair<View, String> sharedElement();

    String transitionName();

    interface Target {
        Pair<View, String> sharedElement();
    }
}
