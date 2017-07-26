package com.example.android.architecture.blueprints.todoapp.core.viewmodels;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

/**
 * Fragments do not have `onRetainCustomNonConfigurationInstance()`, so we must bind a retained fragment that exists along with it.
 *
 * The other option would be to make the fragments be retained themselves.
 */
public class ViewModelLifecycleHelper {
    private ViewModelLifecycleHelper() {
    }

    @NonNull
    public static <T> T getViewModel(AppCompatActivity activity, String viewModelTag) {
        ViewModelHolder viewModelHolder = (ViewModelHolder) activity.getSupportFragmentManager().findFragmentByTag(viewModelTag);
        if(viewModelHolder == null) {
            throw new IllegalStateException("View model holder for [" + viewModelTag + "] does not exist");
        }
        T viewModel = viewModelHolder.getViewModel();
        if(viewModel == null) {
            throw new IllegalStateException("View model for [" + viewModelTag + "] does not exist");
        }
        return viewModel;
    }

    public interface ViewModelCreator<T> {
        T create();
    }

    public static <T> T bindViewModel(AppCompatActivity activity, ViewModelCreator<T> viewModelCreator, String viewModelTag) {
        ViewModelHolder viewModelHolder = (ViewModelHolder) activity.getSupportFragmentManager().findFragmentByTag(viewModelTag);
        if(viewModelHolder == null) {
            viewModelHolder = new ViewModelHolder();
            activity.getSupportFragmentManager().beginTransaction().add(viewModelHolder, viewModelTag).commitNow();
        }
        if(viewModelHolder.getViewModel() == null) {
            T t = viewModelCreator.create();
            viewModelHolder.setViewModel(t);
        }
        return viewModelHolder.getViewModel();
    }

    public static void destroyViewModel(AppCompatActivity activity, String viewModelTag) {
        ViewModelHolder viewModelHolder = (ViewModelHolder) activity.getSupportFragmentManager().findFragmentByTag(viewModelTag);
        if(viewModelHolder != null) {
            activity.getSupportFragmentManager().beginTransaction().remove(viewModelHolder).commitNow();
        }
    }
}
