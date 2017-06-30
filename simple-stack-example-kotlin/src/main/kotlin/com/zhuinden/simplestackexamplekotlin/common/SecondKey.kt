package com.zhuinden.simplestackexamplekotlin.common

import com.zhuinden.simplestack.navigator.ViewChangeHandler
import com.zhuinden.simplestack.navigator.changehandlers.NoOpViewChangeHandler
import com.zhuinden.simplestackexamplekotlin.R
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * Created by Owner on 2017. 01. 12..
 */
@PaperParcel
data class SecondKey protected constructor(protected val tag: String = SecondKey::javaClass.name) //
    : PaperParcelable, Key {
    override fun layout(): Int = R.layout.path_second

    override fun viewChangeHandler(): ViewChangeHandler = NoOpViewChangeHandler()

    companion object {
        @JvmField val CREATOR = PaperParcelSecondKey.CREATOR

        fun create(): SecondKey {
            return SecondKey()
        }
    }
}
