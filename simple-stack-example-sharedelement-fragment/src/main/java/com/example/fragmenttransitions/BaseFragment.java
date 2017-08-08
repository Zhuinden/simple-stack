package com.example.fragmenttransitions;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

/**
 * Created by Owner on 2017. 08. 08..
 */

public class BaseFragment
        extends Fragment {
    @NonNull
    public final <T extends BaseKey> T getKey() {
        T key = getArguments() != null ? getArguments().getParcelable("KEY") : null;
        if(key == null) {
            throw new NullPointerException("Key should not be null");
        }
        return key;
    }
}
