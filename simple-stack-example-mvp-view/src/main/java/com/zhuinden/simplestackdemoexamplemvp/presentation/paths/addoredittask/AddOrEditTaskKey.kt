package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.addoredittask

import android.view.View
import com.zhuinden.simplestack.ScopedServices
import com.zhuinden.simplestack.navigator.ViewChangeHandler
import com.zhuinden.simplestack.navigator.changehandlers.SegueViewChangeHandler
import com.zhuinden.simplestackdemoexamplemvp.R
import com.zhuinden.simplestackdemoexamplemvp.application.Injector
import com.zhuinden.simplestackdemoexamplemvp.application.Key
import com.zhuinden.simplestackdemoexamplemvp.util.scoping.ServiceProvider
import kotlinx.android.parcel.Parcelize

sealed class AddOrEditTaskKey(val parent: Key, val taskId: String = "") : Key, ServiceProvider.HasServices {
    override fun getScopeTag() = "AddOrEditTask"

    override fun bindServices(serviceBinder: ScopedServices.ServiceBinder) {
        serviceBinder.add(AddOrEditTaskView.CONTROLLER_TAG, Injector.get().addOrEditTaskPresenter())
    }

    @Parcelize
    data class AddTaskKey(val parentKey: Key): AddOrEditTaskKey(parentKey)

    @Parcelize
    data class EditTaskKey(val parentKey: Key, val taskID: String): AddOrEditTaskKey(parentKey, taskID)

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
