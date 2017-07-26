package com.zhuinden.simplestackexamplemvvm.application;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestackexamplemvvm.R;
import com.zhuinden.simplestackexamplemvvm.core.viewmodels.ViewModelLifecycleHelper;

/**
 * Created by Owner on 2017. 06. 29..
 */

public class FragmentStateChanger {
    private final AppCompatActivity activity;
    private final FragmentManager fragmentManager;
    private final int containerId;

    public FragmentStateChanger(AppCompatActivity activity, FragmentManager fragmentManager, int containerId) {
        this.activity = activity;
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;
    }

    public void handleStateChange(StateChange stateChange) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().disallowAddToBackStack();
        if(stateChange.getDirection() == StateChange.FORWARD) {
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_right,
                    R.anim.slide_out_to_left,
                    R.anim.slide_in_from_right,
                    R.anim.slide_out_to_left);
        } else if(stateChange.getDirection() == StateChange.BACKWARD) {
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_left,
                    R.anim.slide_out_to_right,
                    R.anim.slide_in_from_left,
                    R.anim.slide_out_to_right);
        }

        for(BaseKey oldKey : stateChange.<BaseKey>getPreviousState()) {
            BaseFragment fragment = (BaseFragment) fragmentManager.findFragmentByTag(oldKey.getFragmentTag());
            if(fragment != null) {
                if(!stateChange.getNewState().contains(oldKey)) {
                    fragmentTransaction.remove(fragment);
                } else if(!fragment.isDetached()) {
                    fragmentTransaction.detach(fragment);
                }
            }
        }
        for(BaseKey newKey : stateChange.<BaseKey>getNewState()) {
            BaseFragment fragment = (BaseFragment) fragmentManager.findFragmentByTag(newKey.getFragmentTag());
            if(newKey.equals(stateChange.topNewState())) {
                if(fragment != null) {
                    if(fragment.isDetached()) {
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
                fragment.bindViewModel(ViewModelLifecycleHelper.getViewModel(activity, newKey.getViewModelTag()));
            }
        }
        fragmentTransaction.commitNow();
    }
}