package com.zhuinden.simplestackexamplekotlin

import android.os.Parcelable
import paperparcel.PaperParcel

/**
 * Created by Owner on 2017.11.13.
 */

@PaperParcel
data class DashboardKey(val tag: String = DashboardKey::class.java.name) : BaseKey() {
    // using data class makes you not need to implement hashCode/equals/toString
    // the other option is using `object` where you MUST override `toString`
    override fun createFragment(): BaseFragment = DashboardFragment()

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<DashboardKey> = PaperParcelDashboardKey.CREATOR
    }
}
