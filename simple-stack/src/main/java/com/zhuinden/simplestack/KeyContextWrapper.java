package com.zhuinden.simplestack;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Parcelable;
import android.view.LayoutInflater;

/**
 * Context Wrapper for inflating views, containing the key inside it, making it accessible via `Backstack.getKey(Context)`.
 *
 * Created by Zhuinden on 2017.01.14..
 */
public class KeyContextWrapper
        extends ContextWrapper {
    public static final String TAG = "Backstack.KEY";

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
        } else if(TAG.equals(name)) {
            return key;
        }
        return super.getSystemService(name);
    }


    public static <T extends Parcelable> T getKey(Context context) {
        // noinspection ResourceType
        Object key = context.getSystemService(TAG);
        // noinspection unchecked
        return (T) key;
    }
}
