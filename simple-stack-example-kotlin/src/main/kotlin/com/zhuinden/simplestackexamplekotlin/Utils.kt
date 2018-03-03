package com.zhuinden.simplestackexamplekotlin

import android.support.v4.app.Fragment

/**
 * Created by zhuinden on 2018. 03. 03..
 */
val Fragment.requireArguments
    get() = this.arguments ?: throw IllegalStateException("Arguments should exist!")