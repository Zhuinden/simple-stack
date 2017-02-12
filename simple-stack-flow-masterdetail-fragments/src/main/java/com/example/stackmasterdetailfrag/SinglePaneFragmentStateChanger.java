package com.example.stackmasterdetailfrag;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.zhuinden.simplestack.StateChange;

/**
 * Created by Zhuinden on 2017.02.12..
 */

public class SinglePaneFragmentStateChanger {
    private FragmentManager fragmentManager;
    private int containerId;

    public SinglePaneFragmentStateChanger(FragmentManager fragmentManager, int containerId) {
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;
    }

    public void handleStateChange(StateChange stateChange) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.disallowAddToBackStack();

        if(stateChange.getDirection() == StateChange.FORWARD) {
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
        } else if(stateChange.getDirection() == StateChange.BACKWARD) {
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
        }

        for(Parcelable _oldPath : stateChange.getPreviousState()) {
            Paths.Path oldPath = (Paths.Path ) _oldPath;
            Fragment fragment = fragmentManager.findFragmentByTag(oldPath.getFragmentTag());
            if(fragment != null) {
                if(!stateChange.getNewState().contains(oldPath)) {
                    fragmentTransaction.remove(fragment);
                } else if(!fragment.isDetached()) {
                    fragmentTransaction.detach(fragment);
                }
            }
        }
        for(Parcelable _newPath : stateChange.getNewState()) {
            Paths.Path  newPath = (Paths.Path ) _newPath;
            Fragment fragment = fragmentManager.findFragmentByTag(newPath.getFragmentTag());
            if(newPath.equals(stateChange.topNewState())) {
                if(fragment != null) {
                    if(fragment.isDetached()) {
                        fragmentTransaction.attach(fragment);
                    }
                } else {
                    fragment = newPath.createFragment();
                    fragmentTransaction.add(containerId, fragment, newPath.getFragmentTag());
                }
            } else {
                if(fragment != null && !fragment.isDetached()) {
                    fragmentTransaction.detach(fragment);
                }
            }
        }
        fragmentTransaction.commitNow();
    }
}