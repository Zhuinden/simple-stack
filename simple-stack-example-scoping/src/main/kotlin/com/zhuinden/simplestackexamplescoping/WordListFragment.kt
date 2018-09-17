package com.zhuinden.simplestackexamplescoping

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.word_list_view.*

/**
 * Created by Zhuinden on 2018.09.17.
 */

class WordListFragment : BaseFragment() {
    val adapter = WordListAdapter()

    private val wordController by lazy { lookup<WordController>() }
    private val commandQueue by lazy { wordController.commandQueue }
    private val wordList by lazy { wordController.wordList }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.word_list_view, container, false)

    @Suppress("NAME_SHADOWING")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter

        buttonGoToAddNewWord.onClick { view ->
            view.backstack.goTo(NewWordKey())
        }

        wordList.observe(this /*getViewLifecycle()*/, Observer { words ->
            adapter.updateWords(words!!)
        })
    }

    override fun onStart() {
        super.onStart()
        commandQueue.setReceiver { event ->
            when (event) {
                is WordController.Events.NewWordAdded -> showToast("Added ${event.word}")
            }.safe()
        }
    }

    override fun onStop() {
        commandQueue.detachReceiver()
        super.onStop()
    }
}

