package com.zhuinden.simplestackdemoexamplefragments.core.navigation

import android.view.View
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey

/**
 * Created by Zhuinden on 2017. 01. 12..
 */

abstract class FragmentKey : DefaultFragmentKey() {
    abstract val isFabVisible: Boolean

    abstract fun layout(): Int

    abstract fun menu(): Int

    abstract fun navigationViewId(): Int

    abstract fun shouldShowUp(): Boolean

    abstract fun fabClickListener(fragment: Fragment): View.OnClickListener

    @DrawableRes
    abstract fun fabDrawableIcon(): Int
}
