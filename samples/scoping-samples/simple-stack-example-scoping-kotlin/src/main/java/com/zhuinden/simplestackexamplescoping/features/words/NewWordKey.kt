package com.zhuinden.simplestackexamplescoping.features.words

import androidx.fragment.app.Fragment
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackexamplescoping.core.navigation.BaseKey
import kotlinx.parcelize.Parcelize

/**
 * Created by Zhuinden on 2018.09.17.
 */
@Parcelize
data class NewWordKey(val placeholder: String = "") : BaseKey() {
    override fun instantiateFragment(): Fragment = NewWordFragment()

    override fun bindServices(serviceBinder: ServiceBinder) {}
}