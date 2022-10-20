package com.zhuinden.simplestackexamplescoping.features.words

import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackexamplescoping.core.navigation.BaseKey
import com.zhuinden.simplestackextensions.servicesktx.add
import com.zhuinden.simplestackextensions.servicesktx.get
import com.zhuinden.simplestackextensions.servicesktx.rebind
import kotlinx.parcelize.Parcelize

/**
 * Created by Zhuinden on 2018.09.17.
 */
@Parcelize
data object WordListKey : BaseKey() {
    override fun bindServices(serviceBinder: ServiceBinder) {
        with(serviceBinder) {
            add(WordController(backstack))
            rebind<WordListFragment.DataProvider>(get<WordController>())
            rebind<WordListFragment.ActionHandler>(get<WordController>())
            rebind<NewWordFragment.ActionHandler>(get<WordController>())
            add(get<WordController>().eventEmitter)
        }
    }

    override fun instantiateFragment() = WordListFragment()
}