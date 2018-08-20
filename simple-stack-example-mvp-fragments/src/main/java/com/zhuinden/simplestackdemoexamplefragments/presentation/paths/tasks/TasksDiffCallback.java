package com.zhuinden.simplestackdemoexamplefragments.presentation.paths.tasks;

import android.support.v7.util.DiffUtil;

import com.zhuinden.simplestackdemoexamplefragments.presentation.objects.Task;

import java.util.List;

/**
 * Created by Zhuinden on 2017.01.27..
 */

class TasksDiffCallback
        extends DiffUtil.Callback {
    private List<Task> oldTasks;
    private List<Task> newTasks;

    public TasksDiffCallback(List<Task> oldTasks, List<Task> newTasks) {
        this.oldTasks = oldTasks;
        this.newTasks = newTasks;
    }

    @Override
    public int getOldListSize() {
        return oldTasks == null ? 0 : oldTasks.size();
    }

    @Override
    public int getNewListSize() {
        return newTasks.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return newTasks.get(newItemPosition).getId().equals(oldTasks.get(oldItemPosition).getId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return newTasks.get(newItemPosition).equals(oldTasks.get(oldItemPosition));
    }
}