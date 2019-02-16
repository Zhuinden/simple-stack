package com.zhuinden.simplestackexamplescoping

import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.ScopeKey

interface WordScope : ScopeKey.Child {
    companion object {
        const val SCOPE_TAG = "WORD"
    }

    override fun getParentScopes(): List<String> = History.of(SCOPE_TAG)
}