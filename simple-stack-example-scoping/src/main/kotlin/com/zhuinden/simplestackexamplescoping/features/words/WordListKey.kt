package com.zhuinden.simplestackexamplescoping.features.words

import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackexamplescoping.core.navigation.BaseKey
import com.zhuinden.simplestackexamplescoping.core.scoping.HasServices
import com.zhuinden.simplestackexamplescoping.utils.add
import kotlinx.android.parcel.Parcelize

/**
 * Created by Zhuinden on 2018.09.17.
 */
@Parcelize
data class WordListKey(val placeholder: String = "") : BaseKey, HasServices {
    override fun getScopeTag(): String = fragmentTag

    override fun bindServices(serviceBinder: ServiceBinder) {
        serviceBinder.add(WordController())
    }

    override fun createFragment() = WordListFragment()
}
