package com.zhuinden.simplestackexamplemvvm.application.injection

import android.app.Application
import android.content.Context
import android.content.res.Resources
import dagger.Module
import dagger.Provides

/**
 * Created by Zhuinden on 2017.07.25..
 */
@Module
class AndroidModule {
    @Provides
    fun context(application: Application): Context = application

    @Provides
    fun resources(application: Application): Resources = application.resources
}