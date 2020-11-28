package com.zhuinden.simplestackdemoexamplefragments.features.taskdetail

import android.view.View
import androidx.fragment.app.Fragment
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackdemoexamplefragments.R
import com.zhuinden.simplestackdemoexamplefragments.core.navigation.BaseKey
import com.zhuinden.simplestackdemoexamplefragments.data.repository.TaskRepository
import com.zhuinden.simplestackextensions.services.DefaultServiceProvider
import com.zhuinden.simplestackextensions.servicesktx.lookup
import kotlinx.parcelize.Parcelize

/**
 * Created by Zhuinden on 2018. 08. 20.
 */

@Parcelize
data class TaskDetailKey(val taskId: String) : BaseKey(), DefaultServiceProvider.HasServices {
    override fun bindServices(serviceBinder: ServiceBinder) {
        with(serviceBinder) {
            addService(TaskDetailFragment.CONTROLLER_TAG, TaskDetailPresenter(
                lookup<TaskRepository>(),
                backstack
            ))
        }
    }

    override fun getScopeTag(): String = "TaskDetail[$taskId]"

    override fun layout(): Int = R.layout.path_taskdetail

    override val isFabVisible: Boolean
        get() = true

    override fun instantiateFragment(): Fragment = TaskDetailFragment()

    override fun menu(): Int = R.menu.taskdetail_fragment_menu

    override fun navigationViewId(): Int = 0

    override fun shouldShowUp(): Boolean = true

    override fun fabClickListener(f: Fragment): View.OnClickListener {
        return View.OnClickListener { v ->
            val fragment = f as TaskDetailFragment
            fragment.editTask()
        }
    }

    override fun fabDrawableIcon(): Int = R.drawable.ic_edit
}
