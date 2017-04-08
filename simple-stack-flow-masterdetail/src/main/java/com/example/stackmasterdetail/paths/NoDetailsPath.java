package com.example.stackmasterdetail.paths;

import com.example.stackmasterdetail.R;
import com.example.stackmasterdetail.application.Path;
import com.google.auto.value.AutoValue;

/**
 * Created by Zhuinden on 2017.04.08..
 */

@AutoValue
public abstract class NoDetailsPath
        extends Path {
    public static NoDetailsPath create() {
        return new AutoValue_NoDetailsPath();
    }

    @Override
    public String getTitle() {
        return "No Details";
    }

    @Override
    public int layout() {
        return R.layout.no_details;
    }
}
