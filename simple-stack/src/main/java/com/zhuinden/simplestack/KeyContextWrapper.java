package com.zhuinden.simplestack;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Parcelable;
import android.view.LayoutInflater;

/**
 * Created by Zhuinden on 2017.01.14..
 */

class KeyContextWrapper
        extends ContextWrapper {
    static final String KEY = "Backstack.KEY";

    LayoutInflater layoutInflater;

    final Parcelable key;

    public KeyContextWrapper(Context base, Parcelable key) {
        super(base);
        this.key = key;
    }

    @Override
    public Object getSystemService(String name) {
        if(Context.LAYOUT_INFLATER_SERVICE.equals(name)) {
            if(layoutInflater == null) {
                layoutInflater = LayoutInflater.from(getBaseContext()).cloneInContext(this);
            }
            return layoutInflater;
        } else if(KEY.equals(name)) {
            return key;
        }
        return super.getSystemService(name);
    }


    public static <T extends Parcelable> T getKey(Context context) {
        // noinspection ResourceType
        Object key = context.getSystemService(KEY);
        // noinspection unchecked
        return (T) key;
    }
}
