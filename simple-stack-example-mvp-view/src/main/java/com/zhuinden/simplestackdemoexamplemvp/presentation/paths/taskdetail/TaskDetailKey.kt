package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.taskdetail

import android.view.View

import com.zhuinden.simplestack.navigator.ViewChangeHandler
import com.zhuinden.simplestack.navigator.changehandlers.SegueViewChangeHandler
import com.zhuinden.simplestackdemoexamplemvp.R
import com.zhuinden.simplestackdemoexamplemvp.application.Key
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TaskDetailKey(val taskId: String) : Key {
    override fun layout(): Int = R.layout.path_taskdetail

    override val isFabVisible: Boolean
        get() = true

    override fun viewChangeHandler(): ViewChangeHandler = SegueViewChangeHandler()

    override fun menu(): Int = R.menu.taskdetail_fragment_menu

    override fun navigationViewId(): Int = 0

    override fun shouldShowUp(): Boolean = true

    override fun fabClickListener(view: View): View.OnClickListener = View.OnClickListener { v ->
        val taskDetailView = view as TaskDetailView
        taskDetailView.editTask()
    }

    override fun fabDrawableIcon(): Int = R.drawable.ic_edit
}
