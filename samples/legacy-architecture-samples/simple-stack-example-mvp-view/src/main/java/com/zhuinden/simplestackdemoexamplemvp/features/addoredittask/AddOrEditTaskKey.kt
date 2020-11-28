package com.zhuinden.simplestackdemoexamplemvp.features.addoredittask

import android.view.View
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestack.navigator.ViewChangeHandler
import com.zhuinden.simplestack.navigator.changehandlers.SegueViewChangeHandler
import com.zhuinden.simplestackdemoexamplemvp.R
import com.zhuinden.simplestackdemoexamplemvp.core.navigation.ViewKey
import com.zhuinden.simplestackdemoexamplemvp.data.repository.TaskRepository
import com.zhuinden.simplestackdemoexamplemvp.util.MessageQueue
import com.zhuinden.simplestackextensions.services.DefaultServiceProvider
import com.zhuinden.simplestackextensions.servicesktx.lookup
import kotlinx.parcelize.Parcelize

sealed class AddOrEditTaskKey(val parent: ViewKey, val taskId: String = "") : ViewKey, DefaultServiceProvider.HasServices {
    override fun getScopeTag() = "AddOrEditTask"

    override fun bindServices(serviceBinder: ServiceBinder) {
        with(serviceBinder) {
            addService(AddOrEditTaskView.CONTROLLER_TAG, AddOrEditTaskPresenter(
                lookup<TaskRepository>(),
                lookup<MessageQueue>(),
                backstack
            ))
        }
    }

    @Parcelize
    data class AddTaskKey(val parentKey: ViewKey) : AddOrEditTaskKey(parentKey)

    @Parcelize
    data class EditTaskKey(val parentKey: ViewKey, val taskID: String) : AddOrEditTaskKey(parentKey, taskID)

    override fun layout(): Int = R.layout.path_addoredittask

    override val isFabVisible: Boolean
        get() = true

    override fun viewChangeHandler(): ViewChangeHandler = SegueViewChangeHandler()

    override fun menu(): Int = R.menu.empty_menu

    override fun navigationViewId(): Int = 0

    override fun shouldShowUp(): Boolean = true

    override fun fabClickListener(view: View): View.OnClickListener =
        View.OnClickListener { v ->
            val addOrEditTaskView = view as AddOrEditTaskView
            addOrEditTaskView.fabClicked()
        }

    override fun fabDrawableIcon(): Int = R.drawable.ic_done
}
