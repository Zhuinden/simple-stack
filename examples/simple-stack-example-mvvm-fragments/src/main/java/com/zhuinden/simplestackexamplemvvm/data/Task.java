/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zhuinden.simplestackexamplemvvm.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.zhuinden.simplestackexamplemvvm.util.Strings;

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
        return createTaskWithId(title, description, UUID.randomUUID().toString(), false);
    }

    public static Task createActiveTaskWithId(String title, String description, String id) {
        return createTaskWithId(title, description, id, false);
    }

    public static Task createTaskWithId(String title, String description, String id, boolean completed) {
        return new AutoValue_Task.Builder() //
                .setId(id) //
                .setTitle(title) //
                .setDescription(description) //
                .setCompleted(completed) //
                .build();
    }

    public static Builder newBuilder() {
        return new AutoValue_Task.Builder();
    }

    public abstract Builder toBuilder();

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
