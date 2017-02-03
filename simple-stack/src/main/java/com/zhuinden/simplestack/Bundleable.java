package com.zhuinden.simplestack;

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * An interface that specifies that the custom viewgroup that implements this also places its persisted state into a Bundle.
 *
 * Created by Zhuinden on 2017. 01. 23..
 */

public interface Bundleable {
    Bundle toBundle();

    void fromBundle(@Nullable Bundle bundle);
}
