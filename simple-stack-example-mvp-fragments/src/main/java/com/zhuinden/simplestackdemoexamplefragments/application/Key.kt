package com.zhuinden.simplestackdemoexamplefragments.application

import android.os.Parcelable
import android.support.annotation.DrawableRes
import android.support.v4.app.Fragment
import android.view.View

/**
 * Created by Owner on 2017. 01. 12..
 */

interface Key : Parcelable {
    val fragmentTag: String

    val isFabVisible: Boolean

    fun layout(): Int

    fun newFragment(): Fragment

    fun menu(): Int

    fun navigationViewId(): Int

    fun shouldShowUp(): Boolean

    fun fabClickListener(fragment: Fragment): View.OnClickListener

    @DrawableRes
    fun fabDrawableIcon(): Int
}
