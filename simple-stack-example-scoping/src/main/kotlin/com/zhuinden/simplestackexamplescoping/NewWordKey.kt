package com.zhuinden.simplestackexamplescoping

import kotlinx.android.parcel.Parcelize

/**
 * Created by Zhuinden on 2018.09.17.
 */
@Parcelize
data class NewWordKey(val placeholder: String = "") : BaseKey, WordScope {
    override fun createFragment(): BaseFragment = NewWordFragment()
}
