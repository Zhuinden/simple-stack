package com.zhuinden.simplestackexamplescoping.features.words

import android.os.Bundle
import android.view.View
import com.zhuinden.simplestackexamplescoping.R
import com.zhuinden.simplestackexamplescoping.databinding.NewWordFragmentBinding
import com.zhuinden.simplestackexamplescoping.utils.onClick
import com.zhuinden.simplestackexamplescoping.utils.viewBinding
import com.zhuinden.simplestackextensions.fragments.KeyedFragment
import com.zhuinden.simplestackextensions.fragmentsktx.lookup


/**
 * Created by Zhuinden on 2018.09.17.
 */

class NewWordFragment : KeyedFragment(R.layout.new_word_fragment) {
    private val binding by viewBinding(NewWordFragmentBinding::bind)

    interface ActionHandler {
        fun onAddWordClicked(word: String)
    }

    private val actionHandler by lazy { lookup<ActionHandler>() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonAddNewWord.onClick {
            val word = binding.textInputNewWord.text.toString().trim()
            actionHandler.onAddWordClicked(word)
        }
    }
}
