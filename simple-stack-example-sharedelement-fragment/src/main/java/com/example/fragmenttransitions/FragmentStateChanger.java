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

            if(topNewKey instanceof HasSharedElement || topPreviousFragment instanceof HasSharedElement.Target) {
                topNewFragment.setSharedElementEnterTransition(new DetailsTransition());
                topNewFragment.setSharedElementReturnTransition(new DetailsTransition());
                if(topPreviousFragment != null) {
                    topPreviousFragment.setSharedElementEnterTransition(new DetailsTransition());
                    topPreviousFragment.setSharedElementReturnTransition(new DetailsTransition());
                }
            }
        }

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().disallowAddToBackStack();

        fragmentTransaction.setReorderingAllowed(true); // WITHOUT THIS LINE, SHARED ELEMENT TRANSITIONS WON'T WORK.

        if(topNewKey instanceof HasSharedElement) {
            HasSharedElement elementKey = (HasSharedElement) topNewKey;
            if(elementKey.sharedElement() != null) {
                fragmentTransaction.addSharedElement(elementKey.sharedElement().first, elementKey.sharedElement().second);
            }
        }
        if(topPreviousFragment != null && topPreviousFragment instanceof HasSharedElement.Target) {
            HasSharedElement.Target target = (HasSharedElement.Target) topPreviousFragment;
            if(target.sharedElement() != null) {
                fragmentTransaction.addSharedElement(target.sharedElement().first, target.sharedElement().second);
            }
        }

        for(BaseKey oldKey : previousState) {
            Fragment fragment = fragmentManager.findFragmentByTag(oldKey.getFragmentTag());
            if(fragment != null) {
                if(!newState.contains(oldKey)) {
                    fragmentTransaction.remove(fragment);
                } else if(!fragment.isHidden()) {
                    fragmentTransaction.hide(fragment);
                }
            }
        }
        for(BaseKey newKey : newState) {
            Fragment fragment = fragmentManager.findFragmentByTag(newKey.getFragmentTag());
            if(newKey.equals(stateChange.topNewState())) {
                if(fragment != null) {
                    if(fragment.isHidden()) {
                        fragmentTransaction.show(fragment);
                    }
                } else {
                    fragmentTransaction.add(containerId, topNewFragment, newKey.getFragmentTag());
                }
            } else {
                if(fragment != null && !fragment.isHidden()) {
                    fragmentTransaction.hide(fragment);
                }
            }
        }

        fragmentTransaction.commitNow();
    }
}
