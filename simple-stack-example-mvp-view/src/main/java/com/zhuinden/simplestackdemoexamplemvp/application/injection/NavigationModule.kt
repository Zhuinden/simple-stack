package com.zhuinden.simplestackdemoexamplemvp.application.injection

import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestackdemoexamplemvp.util.BackstackHolder

import dagger.Module
import dagger.Provides

/**
 * Created by Owner on 2017. 01. 27..
 */

@Module
object NavigationModule {
    @Provides
    @JvmStatic
    fun backstack(backstackHolder: BackstackHolder): Backstack =
        backstackHolder.backstack
}
