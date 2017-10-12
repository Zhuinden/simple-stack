package com.example.stackmasterdetailfrag;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.example.stackmasterdetailfrag.application.Path;
import com.example.stackmasterdetailfrag.paths.NoDetailsPath;
import com.zhuinden.simplestack.StateChange;

import java.util.List;

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
        FragmentTransaction fragmentTransaction = beginFragmentTransaction(stateChange);

        Path noDetailKey = NoDetailsPath.create();
        Fragment noDetailsFragment = fragmentManager.findFragmentByTag(noDetailKey.getFragmentTag());
        if(noDetailsFragment != null) {
            fragmentTransaction.remove(noDetailsFragment);
        }

        List<Path> previousState = stateChange.getPreviousState();
        List<Path> newState = stateChange.getNewState();
        for(Path oldPath : previousState) {
            Fragment fragment = fragmentManager.findFragmentByTag(oldPath.getFragmentTag());
            if(fragment != null) {
                if(!newState.contains(oldPath)) {
                    fragmentTransaction.remove(fragment);
                } else if(!oldPath.equals(stateChange.topNewState())) {
                    fragmentTransaction.detach(fragment);
                }
            }
        }

        for(Path newPath : newState) {
            Fragment fragment = fragmentManager.findFragmentByTag(newPath.getFragmentTag());
            if(!newPath.equals(stateChange.topNewState())) {
                if(fragment != null && !fragment.isDetached()) {
                    fragmentTransaction.detach(fragment);
                }
            }
        }
        fragmentTransaction.commitNow();

        Path newPath = stateChange.topNewState();
        Fragment fragment = fragmentManager.findFragmentByTag(newPath.getFragmentTag());
        if(fragment != null) {
            if(fragment.isDetached() || fragment.getView() == null) {
                //fragmentTransaction.attach(fragment); // does not work with config change
                fragmentTransaction = beginFragmentTransaction(stateChange);
                Fragment.SavedState savedState = fragmentManager.saveFragmentInstanceState(fragment);
                fragmentTransaction.remove(fragment);
                fragmentTransaction.commitNow();
                fragmentTransaction = beginFragmentTransaction(stateChange);
                fragment.setInitialSavedState(savedState);
                fragmentTransaction.add(containerId, fragment, newPath.getFragmentTag());
            }
        } else {
            fragmentTransaction = beginFragmentTransaction(stateChange);
            fragment = newPath.createFragment();
            fragmentTransaction.add(containerId, fragment, newPath.getFragmentTag());
        }

        fragmentTransaction.commitNow();
    }

    @NonNull
    private FragmentTransaction beginFragmentTransaction(StateChange stateChange) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.disallowAddToBackStack();

        if(stateChange.getDirection() == StateChange.FORWARD) {
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
        } else if(stateChange.getDirection() == StateChange.BACKWARD) {
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
        }
        return fragmentTransaction;
    }
}