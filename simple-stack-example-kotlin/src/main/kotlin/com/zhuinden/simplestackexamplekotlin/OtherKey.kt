package com.zhuinden.simplestackexamplekotlin

import android.os.Parcelable
import paperparcel.PaperParcel

/**
 * Created by Owner on 2017.11.13.
 */
@PaperParcel
object OtherKey : BaseKey() {
    override fun createFragment(): BaseFragment = OtherFragment()

    @JvmField val CREATOR: Parcelable.Creator<OtherKey> = PaperParcelOtherKey.CREATOR

    override fun toString(): String = javaClass.name // you NEED to implement this in an `object` because of Kotlin!
}
