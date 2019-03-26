package com.zhuinden.navigationexamplefrag;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.zhuinden.simplestack.StateChange;

import java.util.List;

/**
 * Created by Owner on 2017. 06. 29..
 */

public class FragmentStateChanger {
    private FragmentManager fragmentManager;
    private int containerId;

    public FragmentStateChanger(FragmentManager fragmentManager, int containerId) {
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;
    }

    public void handleStateChange(StateChange stateChange) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().disallowAddToBackStack();
        if(stateChange.getDirection() == StateChange.FORWARD) {
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left, R.anim.slide_in_from_right, R.anim.slide_out_to_left);
        } else if(stateChange.getDirection() == StateChange.BACKWARD) {
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right, R.anim.slide_in_from_left, R.anim.slide_out_to_right);
        }

        List<BaseKey> previousState = stateChange.getPreviousState();
        List<BaseKey> newState = stateChange.getNewState();
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
                    if(fragment.isRemoving()) { // Fragments are quirky, they die asynchronously. Ignore if they're still there.
                        fragmentTransaction.replace(containerId, newKey.newFragment(), newKey.getFragmentTag());
                    } else if(fragment.isHidden()) {
                        fragmentTransaction.show(fragment);
                    }
                } else {
                    fragment = newKey.newFragment();
                    fragmentTransaction.add(containerId, fragment, newKey.getFragmentTag());
                }
            } else {
                if(fragment != null && !fragment.isHidden()) {
                    fragmentTransaction.hide(fragment);
                }
            }
        }
        fragmentTransaction.commitAllowingStateLoss();
    }
}