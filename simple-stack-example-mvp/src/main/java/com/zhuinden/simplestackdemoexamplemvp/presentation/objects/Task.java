package com.zhuinden.simplestackdemoexamplemvp.presentation.objects;

/**
 * Created by Owner on 2017. 01. 25..
 */


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

import java.util.UUID;

/**
 * Immutable model class for a Task.
 */
@AutoValue
public abstract class Task {
    @NonNull
    public abstract String id();

    @Nullable
    public abstract String title();

    @Nullable
    public abstract String description();

    public abstract boolean completed();

    public static Task createNewActiveTask(String title, String description) {
        return createCompletedTaskWithId(title, description, UUID.randomUUID().toString(), false);
    }

    public static Task createActiveTaskWithId(String title, String description, String id) {
        return createCompletedTaskWithId(title, description, id, false);
    }

    public static Task createCompletedTask(String title, String description, boolean completed) {
        return createCompletedTaskWithId(title, description, UUID.randomUUID().toString(), completed);
    }

    public static Task createCompletedTaskWithId(String title, String description, String id, boolean completed) {
        return new AutoValue_Task(id, title, description, completed);
    }

    @Nullable
    public String getTitleForList() {
        if (!(title() == null || "".equals(title()))) {
            return title();
        } else {
            return description();
        }
    }

    public boolean isCompleted() {
        return completed();
    }

    public boolean isActive() {
        return !completed();
    }

    public boolean isEmpty() {
        return (title() == null || "".equals(title())) && (description() == null || "".equals(description()));
    }
}
