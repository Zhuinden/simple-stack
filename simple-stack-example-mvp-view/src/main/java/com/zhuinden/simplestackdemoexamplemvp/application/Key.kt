package com.zhuinden.simplestackdemoexamplemvp.application

import android.os.Parcelable
import android.support.annotation.DrawableRes
import android.view.View

import com.zhuinden.simplestack.navigator.StateKey

/**
 * Created by Owner on 2017. 01. 12..
 */

interface Key : StateKey, Parcelable {
    val isFabVisible: Boolean

    override fun layout(): Int

    fun menu(): Int

    fun navigationViewId(): Int

    fun shouldShowUp(): Boolean

    fun fabClickListener(view: View): View.OnClickListener

    @DrawableRes
    fun fabDrawableIcon(): Int
}
