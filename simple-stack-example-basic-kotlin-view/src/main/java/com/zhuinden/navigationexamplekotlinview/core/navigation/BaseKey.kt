package com.zhuinden.navigationexamplekotlinview.core.navigation

import android.os.Parcelable

import com.zhuinden.simplestack.navigator.DefaultViewKey
import com.zhuinden.simplestack.navigator.ViewChangeHandler
import com.zhuinden.simplestack.navigator.changehandlers.FadeViewChangeHandler

/**
 * Created by Owner on 2017. 06. 29..
 */

abstract class BaseKey : DefaultViewKey, Parcelable {
    override fun viewChangeHandler(): ViewChangeHandler = FadeViewChangeHandler()
}
