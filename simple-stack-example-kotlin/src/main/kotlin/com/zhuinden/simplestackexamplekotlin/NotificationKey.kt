package com.zhuinden.simplestackexamplekotlin

import android.os.Parcelable
import paperparcel.PaperParcel

/**
 * Created by Owner on 2017. 06. 29..
 */
@PaperParcel
object NotificationKey : BaseKey() {
    override fun createFragment(): BaseFragment = NotificationFragment()

    @JvmField val CREATOR: Parcelable.Creator<NotificationKey> = PaperParcelNotificationKey.CREATOR

    override fun toString(): String = javaClass.name // you NEED to implement this in an `object` because of Kotlin!
}
