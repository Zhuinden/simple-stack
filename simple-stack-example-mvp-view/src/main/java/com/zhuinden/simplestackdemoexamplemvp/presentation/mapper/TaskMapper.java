package com.zhuinden.simplestackdemoexamplemvp.presentation.mapper;

import com.zhuinden.simplestackdemoexamplemvp.data.entity.DbTask;
import com.zhuinden.simplestackdemoexamplemvp.presentation.objects.Task;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Owner on 2017. 01. 26..
 */

@Singleton
public class TaskMapper {
    @Inject
    public TaskMapper() {
    }

    public Task fromRealm(DbTask dbTask) {
        return Task.createCompletedTaskWithId(dbTask.getTitle(), dbTask.getDescription(), dbTask.getId(), dbTask.getCompleted());
    }

    public DbTask toRealm(Task task) {
        DbTask dbTask = new DbTask();
        dbTask.setId(task.id());
        dbTask.setCompleted(task.completed());
        dbTask.setDescription(task.description());
        dbTask.setTitle(task.title());
        return dbTask;
    }
}
