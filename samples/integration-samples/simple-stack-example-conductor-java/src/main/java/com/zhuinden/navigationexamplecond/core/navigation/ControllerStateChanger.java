package com.zhuinden.navigationexamplecond.core.navigation;

import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.zhuinden.simplestack.StateChange;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Owner on 2017. 06. 29..
 */

public class ControllerStateChanger {
    private final Router router;

    public ControllerStateChanger(Router router) {
        this.router = router;
    }

    public void handleStateChange(StateChange stateChange) {
        if(!router.hasRootController()) {
            BaseKey key = stateChange.topNewKey();
            router.setRoot(RouterTransaction.with(key.newController()));
            return;
        }

        List<RouterTransaction> routerTransactions = new LinkedList<>();
        Iterator<RouterTransaction> currentTransactions = router.getBackstack().iterator();
        Iterator<BaseKey> newKeys = stateChange.<BaseKey>getNewKeys().iterator();
        while(currentTransactions.hasNext() && newKeys.hasNext()) {
            RouterTransaction currentTransaction = currentTransactions.next();
            BaseKey previousKey = ((BaseController) currentTransaction.controller()).getKey();
            BaseKey newKey = newKeys.next();
            if(!newKey.equals(previousKey)) {
                routerTransactions.add(RouterTransaction.with(newKey.newController()));
                break;
            }
            routerTransactions.add(currentTransaction);
        }
        while(newKeys.hasNext()) {
            BaseKey newKey = newKeys.next();
            routerTransactions.add(RouterTransaction.with(newKey.newController()));
        }
        router.setBackstack(routerTransactions,
                stateChange.getDirection() == StateChange.REPLACE ? new FadeChangeHandler() //
                        : new HorizontalChangeHandler());
    }
}