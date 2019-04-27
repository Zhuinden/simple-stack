package com.zhuinden.navigationexamplecond;

import android.os.Bundle;
import android.os.Parcelable;

/**
 * Created by Owner on 2017. 06. 29..
 */

public abstract class BaseKey implements Parcelable {
    public final BaseController newController() {
        BaseController controller = createController();
        Bundle bundle = controller.getArgs();
        bundle.putParcelable("KEY", this);
        return controller;
    }

    protected abstract BaseController createController();
}
