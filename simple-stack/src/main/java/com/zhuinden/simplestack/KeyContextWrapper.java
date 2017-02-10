/*
 * Copyright 2017 Gabor Varadi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
