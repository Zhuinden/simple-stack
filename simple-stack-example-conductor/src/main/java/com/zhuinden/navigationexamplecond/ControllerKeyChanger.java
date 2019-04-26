package com.zhuinden.navigationexamplecond;

import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.zhuinden.simplestack.KeyChange;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Owner on 2017. 06. 29..
 */

public class ControllerKeyChanger {
    private final Router router;

    public ControllerKeyChanger(Router router) {
        this.router = router;
    }

    public void handleKeyChange(KeyChange keyChange) {
        if(!router.hasRootController()) {
            BaseKey key = keyChange.topNewKey();
            router.setRoot(RouterTransaction.with(key.newController()));
            return;
        }

        List<RouterTransaction> routerTransactions = new LinkedList<>();
        Iterator<RouterTransaction> currentTransactions = router.getBackstack().iterator();
        Iterator<BaseKey> newKeys = keyChange.<BaseKey>getNewKeys().iterator();
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
                keyChange.getDirection() == KeyChange.REPLACE ? new FadeChangeHandler() //
                        : new HorizontalChangeHandler());
    }
}