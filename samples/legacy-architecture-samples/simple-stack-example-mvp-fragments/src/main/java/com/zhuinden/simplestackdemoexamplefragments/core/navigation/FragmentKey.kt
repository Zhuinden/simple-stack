package com.zhuinden.simplestackdemoexamplefragments.core.navigation

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import android.view.View

/**
 * Created by Zhuinden on 2017. 01. 12..
 */

interface FragmentKey : Parcelable {
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
