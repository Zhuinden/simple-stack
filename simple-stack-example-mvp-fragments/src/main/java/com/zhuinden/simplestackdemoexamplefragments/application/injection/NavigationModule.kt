package com.zhuinden.simplestackdemoexamplefragments.application.injection

import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestackdemoexamplefragments.util.BackstackHolder

import dagger.Module
import dagger.Provides

/**
 * Created by Zhuinden on 2018. 08. 20.
 */

@Module
object NavigationModule {
    @Provides
    @JvmStatic
    fun backstack(backstackHolder: BackstackHolder): Backstack = backstackHolder.backstack
}