package com.zhuinden.navigationexamplecond.core.navigation;


import com.bluelinelabs.conductor.Controller;

/**
 * Created by Owner on 2017. 06. 29..
 */

public abstract class BaseController extends Controller {
    public final <T extends BaseKey> T getKey() {
        return getArgs().getParcelable("KEY");
    }
}
