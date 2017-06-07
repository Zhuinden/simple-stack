package com.zhuinden.simplestackdemoexample.common

import com.zhuinden.simplestack.navigator.ViewChangeHandler
import com.zhuinden.simplestack.navigator.changehandlers.NoOpViewChangeHandler
import com.zhuinden.simplestackdemoexample.R

import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * Created by Owner on 2017. 01. 12..
 */

@Suppress("ProtectedInFinal")
@PaperParcel
data class FirstKey protected constructor(protected val tag: String = FirstKey::javaClass.name) //
    : PaperParcelable, Key {
    override fun layout(): Int {
        return R.layout.path_first
    }

    override fun viewChangeHandler(): ViewChangeHandler {
        return NoOpViewChangeHandler()
    }

    companion object {
        @JvmField val CREATOR = PaperParcelFirstKey.CREATOR

        fun create(): FirstKey {
            return FirstKey()
        }
    }
}
