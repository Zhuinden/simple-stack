package com.zhuinden.simplestackexamplemvvm.features.addedittask


import android.content.res.Resources
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackexamplemvvm.R
import com.zhuinden.simplestackexamplemvvm.application.BaseKey
import com.zhuinden.simplestackexamplemvvm.application.injection.Injector
import com.zhuinden.simplestackexamplemvvm.data.Task
import com.zhuinden.simplestackextensions.servicesktx.add
import kotlinx.android.parcel.Parcelize

/**
 * Created by Zhuinden on 2017.07.26..
 */
@Parcelize
data class AddEditTaskKey(val task: Task?) : BaseKey() {
    override fun instantiateFragment(): Fragment = AddEditTaskFragment()

    override fun bindServices(serviceBinder: ServiceBinder) {
        with(serviceBinder) {
            add(AddEditTaskViewModel(
                Injector.get().resources(),
                Injector.get().tasksDataSource(),
                Injector.get().messageQueue(),
                backstack,
                getKey(),
            ))
        }
    }

    override val isFabVisible: Boolean
        get() = true

    override fun navigationViewId(): Int {
        return 0
    }

    override fun shouldShowUp(): Boolean {
        return true
    }

    override fun setupFab(fragment: Fragment, fab: FloatingActionButton) {
        fab.setImageResource(R.drawable.ic_done)
        fab.setOnClickListener {
            (fragment as AddEditTaskFragment).onSaveTaskClicked()
        }
    }

    override fun title(resources: Resources): String? = when {
        task != null -> resources.getString(R.string.edit_task)
        else -> resources.getString(R.string.add_task)
    }
}