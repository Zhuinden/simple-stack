package com.zhuinden.navigationexamplecond;

import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.zhuinden.simplestack.StateChange;

import java.util.Collections;
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
            BaseKey key = stateChange.topNewState();
            router.setRoot(RouterTransaction.with(key.newController()));
            return;
        }

        List<RouterTransaction> routerTransactions = new LinkedList<>();
        Iterator<RouterTransaction> currentTransactions = router.getBackstack().iterator();
        Iterator<Object> _newKeys = stateChange.getNewState().iterator();
        while(currentTransactions.hasNext() && _newKeys.hasNext()) {
            RouterTransaction currentTransaction = currentTransactions.next();
            BaseKey previousKey = ((BaseController) currentTransaction.controller()).getKey();
            BaseKey newKey = (BaseKey) _newKeys.next();
            if(!newKey.equals(previousKey)) {
                routerTransactions.add(RouterTransaction.with(newKey.newController()));
                break;
            }
            routerTransactions.add(currentTransaction);
        }
        while(_newKeys.hasNext()) {
            BaseKey newKey = (BaseKey) _newKeys.next();
            routerTransactions.add(RouterTransaction.with(newKey.newController()));
        }
        router.setBackstack(routerTransactions,
                stateChange.getDirection() == StateChange.REPLACE ? new FadeChangeHandler() //
                        : new HorizontalChangeHandler());
    }
}