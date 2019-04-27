package com.example.fragmenttransitions;

import android.os.Build;
import android.support.v4.app.BackstackRecordAccessor;
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
        List<BaseKey> previousState = stateChange.getPreviousKeys();
        List<BaseKey> newState = stateChange.getNewKeys();
        BaseKey topPreviousKey = stateChange.topPreviousKey();
        BaseKey topNewKey = stateChange.topNewKey();
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

            if(topNewKey instanceof HasSharedElement || topPreviousKey instanceof HasSharedElement) {
                topNewFragment.setSharedElementEnterTransition(new DetailsTransition());
                topNewFragment.setSharedElementReturnTransition(new DetailsTransition());
                if(topPreviousFragment != null) {
                    topPreviousFragment.setSharedElementEnterTransition(new DetailsTransition());
                    topPreviousFragment.setSharedElementReturnTransition(new DetailsTransition());
                }
            }
        }

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction() //
                .disallowAddToBackStack() //
                .setReorderingAllowed(true); // WITHOUT THIS LINE, SHARED ELEMENT TRANSITIONS WON'T WORK.

        if(topNewKey instanceof HasSharedElement) {
            HasSharedElement forwardKey = (HasSharedElement) topNewKey;
            if(forwardKey.sharedElement() != null) {
                // to eliminate use of WeakReference<View>, we add the view's transition name to the FragmentTransaction directly.
                BackstackRecordAccessor.addSharedElement(fragmentTransaction, forwardKey.sharedElement().sourceTransitionName(), forwardKey.sharedElement().targetTransitionName());
            }
        }
        if(topPreviousKey instanceof HasSharedElement) {
            HasSharedElement backwardKey = (HasSharedElement) topPreviousKey;
            if(backwardKey.sharedElement() != null) {
                // during back navigation, we must invert the received "source" and "target".
                BackstackRecordAccessor.addSharedElement(fragmentTransaction, backwardKey.sharedElement().targetTransitionName(), backwardKey.sharedElement().sourceTransitionName());
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
            if(newKey.equals(stateChange.topNewKey())) {
                if(fragment != null) {
                    if(fragment.isRemoving()) { // Fragments are quirky, they die asynchronously. Ignore if they're still there.
                        fragmentTransaction.replace(containerId, newKey.newFragment(), newKey.getFragmentTag());
                    } else if(fragment.isHidden()) {
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
