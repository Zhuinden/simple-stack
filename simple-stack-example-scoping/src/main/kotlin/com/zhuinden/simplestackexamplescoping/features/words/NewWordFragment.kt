package com.zhuinden.simplestackexamplescoping.features.words

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zhuinden.simplestackexamplescoping.R
import com.zhuinden.simplestackexamplescoping.core.navigation.BaseFragment
import com.zhuinden.simplestackexamplescoping.utils.lookup
import com.zhuinden.simplestackexamplescoping.utils.onClick
import kotlinx.android.synthetic.main.new_word_fragment.*


/**
 * Created by Zhuinden on 2018.09.17.
 */

class NewWordFragment : BaseFragment() {
    interface ActionHandler {
        fun onAddWordClicked(newWordFragment: NewWordFragment, word: String)
    }

    private val actionHandler by lazy { lookup<ActionHandler>() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.new_word_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonAddNewWord.onClick {
            val word = textInputNewWord.text.toString().trim()
            actionHandler.onAddWordClicked(this, word)
        }
    }
}
