package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.tasks;


import android.support.annotation.NonNull;

import com.zhuinden.simplestackdemoexamplemvp.presentation.objects.Task;

import java.util.List;

/**
 * This specifies the contract between the view and the tasksPresenter.
 */
public interface TasksContract {
    interface View {
        void setLoadingIndicator(boolean active);
        void showTasks(List<Task> tasks);
        void showAddTask();
        void showTaskDetailsUi(String taskId);
        void showTaskMarkedComplete();
        void showTaskMarkedActive();
        void showCompletedTasksCleared();
        void showLoadingTasksError();
        void showNoTasks();
        void showActiveFilterLabel();
        void showCompletedFilterLabel();
        void showAllFilterLabel();
        void showNoActiveTasks();
        void showNoCompletedTasks();
        void showSuccessfullySavedMessage();
        boolean isActive();
        void showFilteringPopUpMenu();
    }

    interface Presenter {
        void result(int requestCode, int resultCode);
        void loadTasks(boolean forceUpdate);
        void addNewTask();
        void openTaskDetails(@NonNull Task requestedTask);
        void completeTask(@NonNull Task completedTask);
        void activateTask(@NonNull Task activeTask);
        void clearCompletedTasks();
        void setFiltering(TasksFilterType requestType);
        TasksFilterType getFiltering();
    }
}
