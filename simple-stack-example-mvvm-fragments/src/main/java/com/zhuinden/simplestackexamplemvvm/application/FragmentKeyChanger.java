package com.zhuinden.simplestackexamplemvvm.application;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.zhuinden.simplestack.KeyChange;
import com.zhuinden.simplestackexamplemvvm.R;

import java.util.List;

/**
 * Created by Owner on 2017. 06. 29..
 */

public class FragmentKeyChanger {
    private final MainActivity activity;
    private final FragmentManager fragmentManager;
    private final int containerId;

    public FragmentKeyChanger(MainActivity activity, FragmentManager fragmentManager, int containerId) {
        this.activity = activity;
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;
    }

    public void handleKeyChange(KeyChange keyChange) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().disallowAddToBackStack();
        if(keyChange.getDirection() == KeyChange.FORWARD) {
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_right,
                    R.anim.slide_out_to_left,
                    R.anim.slide_in_from_right,
                    R.anim.slide_out_to_left);
        } else if(keyChange.getDirection() == KeyChange.BACKWARD) {
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_left,
                    R.anim.slide_out_to_right,
                    R.anim.slide_in_from_left,
                    R.anim.slide_out_to_right);
        }

        List<BaseKey<?>> previousState = keyChange.getPreviousKeys();
        List<BaseKey<?>> newState = keyChange.getNewKeys();
        for(BaseKey oldKey : previousState) {
            BaseFragment fragment = (BaseFragment) fragmentManager.findFragmentByTag(oldKey.getFragmentTag());
            if(fragment != null) {
                if(!newState.contains(oldKey)) {
                    fragmentTransaction.remove(fragment);
                } else if(!fragment.isDetached()) {
                    fragmentTransaction.detach(fragment);
                }
            }
        }
        for(BaseKey newKey : newState) {
            BaseFragment fragment = (BaseFragment) fragmentManager.findFragmentByTag(newKey.getFragmentTag());
            if(newKey.equals(keyChange.topNewKey())) {
                if(fragment != null) {
                    if(fragment.isRemoving()) { // Fragments are quirky, they die asynchronously. Ignore if they're still there.
                        fragment = newKey.newFragment();
                        fragmentTransaction.replace(containerId, fragment, newKey.getFragmentTag());
                    } else if(fragment.isDetached()) {
                        fragmentTransaction.attach(fragment);
                    }
                } else {
                    fragment = newKey.newFragment();
                    fragmentTransaction.add(containerId, fragment, newKey.getFragmentTag());
                }
            } else {
                if(fragment != null && !fragment.isDetached()) {
                    fragmentTransaction.detach(fragment);
                }
            }
            if(fragment != null) {
                //noinspection unchecked
                fragment.bindViewModel(activity.backstackDelegate.getService(newKey, newKey.getViewModelTag()));
            }
        }
        fragmentTransaction.commitNow();
    }
}
