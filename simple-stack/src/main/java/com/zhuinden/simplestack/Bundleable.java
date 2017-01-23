package com.zhuinden.simplestack;

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by Owner on 2017. 01. 23..
 */

public interface Bundleable {
    Bundle toBundle();

    void fromBundle(@Nullable Bundle bundle);
}
