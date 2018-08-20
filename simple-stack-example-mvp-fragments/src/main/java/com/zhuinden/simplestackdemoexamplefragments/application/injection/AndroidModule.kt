package com.zhuinden.simplestackdemoexamplefragments.application.injection

import android.content.Context
import android.content.res.Resources
import com.zhuinden.simplestackdemoexamplefragments.application.CustomApplication
import dagger.Module
import dagger.Provides
import javax.inject.Named

/**
 * Created by Owner on 2017. 01. 27..
 */
@Module
object AndroidModule {
    @Provides
    @Named("applicationContext")
    @JvmStatic
    fun applicationContext(): Context = CustomApplication.get()

    @Provides
    @JvmStatic
    fun resources(@Named("applicationContext") context: Context): Resources = context.resources
}
