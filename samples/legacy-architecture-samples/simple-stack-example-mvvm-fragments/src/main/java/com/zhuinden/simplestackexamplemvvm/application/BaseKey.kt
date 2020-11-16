package com.zhuinden.simplestackexamplemvvm.application


import android.content.res.Resources
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey
import com.zhuinden.simplestackextensions.services.DefaultServiceProvider

/**
 * Created by Zhuinden on 2017.07.26..
 */
abstract class BaseKey : DefaultFragmentKey(), DefaultServiceProvider.HasServices {
    override fun getFragmentTag(): String = toString()
    override fun getScopeTag(): String = fragmentTag

    override fun bindServices(serviceBinder: ServiceBinder) {
    }

    abstract val isFabVisible: Boolean
    abstract fun setupFab(fragment: Fragment, fab: FloatingActionButton)

    open fun title(resources: Resources): String? {
        return null
    }

    abstract fun navigationViewId(): Int
    abstract fun shouldShowUp(): Boolean
}