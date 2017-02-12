package com.example.stackmasterdetailfrag.util;

import android.content.Context;
import android.support.v4.app.FragmentManager;

/**
 * Created by Zhuinden on 2017.02.12..
 */

public class FragmentManagerService {
    private FragmentManagerService() {
    }

    public static final String FRAGMENT_MANAGER_TAG = "FragmentManagerService";

    public static FragmentManager get(Context context) {
        //noinspection ResourceType
        return (FragmentManager) context.getSystemService(FRAGMENT_MANAGER_TAG);
    }
}
