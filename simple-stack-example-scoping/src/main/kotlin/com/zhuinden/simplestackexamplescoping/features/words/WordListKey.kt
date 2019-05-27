package com.zhuinden.simplestackexamplescoping.features.words

import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackexamplescoping.core.navigation.BaseKey
import com.zhuinden.simplestackexamplescoping.core.scoping.HasServices
import com.zhuinden.simplestackexamplescoping.utils.add
import com.zhuinden.simplestackexamplescoping.utils.get
import com.zhuinden.simplestackexamplescoping.utils.rebind
import kotlinx.android.parcel.Parcelize

/**
 * Created by Zhuinden on 2018.09.17.
 */
@Parcelize
data class WordListKey(val placeholder: String = "") : BaseKey, HasServices {
    companion object {
        const val WORD_CONTROLLER_EVENTS = "WordController.Events"
    }

    override fun getScopeTag(): String = fragmentTag

    override fun bindServices(serviceBinder: ServiceBinder) {
        with(serviceBinder) {
            add(WordController(backstack))
            rebind<WordListFragment.DataProvider>(get<WordController>())
            rebind<WordListFragment.ActionHandler>(get<WordController>())
            rebind<NewWordFragment.ActionHandler>(get<WordController>())
            add(get<WordController>().eventEmitter, WORD_CONTROLLER_EVENTS)
        }
    }

    override fun createFragment() = WordListFragment()
}
