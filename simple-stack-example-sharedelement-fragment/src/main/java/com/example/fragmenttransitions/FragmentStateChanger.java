package com.example.fragmenttransitions;

import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.transition.Fade;

import com.zhuinden.simplestack.StateChange;

import java.util.List;

/**
 * Created by Owner on 2017. 08. 08..
 */

public class FragmentStateChanger {
    private FragmentManager fragmentManager;
    private int containerId;

    public FragmentStateChanger(FragmentManager fragmentManager, int containerId) {
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;
    }

    public void handleStateChange(StateChange stateChange) {
        List<BaseKey> previousState = stateChange.getPreviousState();
        List<BaseKey> newState = stateChange.getNewState();
        BaseKey topPreviousKey = stateChange.topPreviousState();
        BaseKey topNewKey = stateChange.topNewState();
        Fragment topPreviousFragment = null;
        if(topPreviousKey != null) {
            topPreviousFragment = fragmentManager.findFragmentByTag(topPreviousKey.getFragmentTag());
        }
        Fragment topNewFragment = fragmentManager.findFragmentByTag(topNewKey.getFragmentTag());
        if(topNewFragment == null) {
            topNewFragment = topNewKey.newFragment();
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(topPreviousFragment != null) {
                topPreviousFragment.setExitTransition(new Fade());
            }
            topNewFragment.setEnterTransition(new Fade());

            if(topNewKey instanceof DetailsKey) {
                topNewFragment.setSharedElementEnterTransition(new DetailsTransition());
                topNewFragment.setSharedElementReturnTransition(new DetailsTransition());
            }
        }

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().disallowAddToBackStack();
        if(topNewKey instanceof HasSharedElement) {
            HasSharedElement elementKey = (HasSharedElement) topNewKey;
            if(elementKey.sharedElement() != null) {
                fragmentTransaction.addSharedElement(elementKey.sharedElement().first, elementKey.sharedElement().second);
            }
        }
        for(BaseKey previousKey : previousState) {
            if(!newState.contains(previousKey) && !previousKey.equals(topPreviousKey)) {
                Fragment fragment = fragmentManager.findFragmentByTag(previousKey.getFragmentTag());
                if(fragment != null) {
                    fragmentTransaction.remove(fragment);
                }
            }
        }
        for(BaseKey newKey : newState) {
            Fragment fragment = fragmentManager.findFragmentByTag(newKey.getFragmentTag());
            if(!newKey.equals(topNewKey) && !newKey.equals(topPreviousKey)) {
                if(fragment != null) {
                    fragmentTransaction.hide(fragment);
                }
            } else {
                if(fragment != null) {
                    if(fragment.isHidden()) {
                        fragmentTransaction.show(fragment);
                    }
                } else {
                    fragmentTransaction.replace(containerId, topNewFragment, topNewKey.getFragmentTag());
                }
            }
        }

        fragmentTransaction.commit();
    }
}
