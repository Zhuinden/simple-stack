package com.zhuinden.simplestackdemoexamplefragments.presentation.objects;

/**
 * Created by Owner on 2017. 01. 25..
 */


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.zhuinden.simplestackdemoexamplefragments.util.Strings;

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
        return new AutoValue_Task.Builder().setId(id).setTitle(title).setDescription(description).setCompleted(completed).build();
    }

    public Builder toBuilder() {
        return new AutoValue_Task.Builder(this);
    }

    @Nullable
    public String getTitleForList() {
        if(!Strings.isNullOrEmpty(title())) {
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
        return Strings.isNullOrEmpty(title()) && Strings.isNullOrEmpty(description());
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setId(String id);

        public abstract Builder setTitle(String title);

        public abstract Builder setDescription(String description);

        public abstract Builder setCompleted(boolean completed);

        public abstract Task build();
    }
}
