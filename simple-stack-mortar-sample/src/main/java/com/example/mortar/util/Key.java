package com.example.mortar.util;

import android.os.Parcelable;

import com.zhuinden.servicetree.ServiceTree;
import com.zhuinden.simplestack.navigator.StateKey;

/**
 * Created by Zhuinden on 2017.05.13..
 */

public interface Key
        extends StateKey, Parcelable {
    void bindServices(ServiceTree.Node node);
}
