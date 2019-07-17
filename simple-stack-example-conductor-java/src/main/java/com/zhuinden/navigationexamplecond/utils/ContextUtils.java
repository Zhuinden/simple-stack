package com.zhuinden.navigationexamplecond.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;

public class ContextUtils {
    private ContextUtils() {
    }

    public static Activity findActivity(Context context) {
        if(context instanceof Activity) {
            return (Activity) context;
        }
        if(context instanceof ContextWrapper) {
            Context baseContext = ((ContextWrapper) context).getBaseContext();
            if(baseContext instanceof Activity) {
                return (Activity) baseContext;
            } else {
                return findActivity(baseContext);
            }
        }
        throw new IllegalArgumentException("Activity not found as parent of context");
    }
}
