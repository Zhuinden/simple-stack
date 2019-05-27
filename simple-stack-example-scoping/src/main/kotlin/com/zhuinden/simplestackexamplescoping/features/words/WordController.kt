package com.zhuinden.simplestackexamplescoping.features.words

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.Bundleable
import com.zhuinden.simplestackexamplescoping.utils.EventEmitter
import com.zhuinden.statebundle.StateBundle

class WordController(
    private val backstack: Backstack
) : NewWordFragment.ActionHandler,
    WordListFragment.ActionHandler,
    WordListFragment.DataProvider,
    Bundleable {
    sealed class Events {
        data class NewWordAdded(val word: String) : Events()
    }

    private inner class WordEventEmitter : EventEmitter<Events>() {
        public override fun emit(event: Events) {
            super.emit(event)
        }
    }

    private val wordEventEmitter = WordEventEmitter()
    val eventEmitter: EventEmitter<Events> get() = wordEventEmitter

    private val mutableWords: MutableLiveData<List<String>> = MutableLiveData()
    override val wordList: LiveData<List<String>>
        get() = mutableWords

    init {
        mutableWords.value = listOf("Bogus", "Magic", "Scoping mechanisms")
    }

    private fun addWordToList(word: String) {
        mutableWords.run {
            postValue(value!!.toMutableList().also { list -> list.add(word) })
        }
        wordEventEmitter.emit(Events.NewWordAdded(word))
    }

    override fun onAddNewWordClicked() {
        backstack.goTo(NewWordKey())
    }

    override fun onAddWordClicked(word: String) {
        if (word.isNotEmpty()) {
            addWordToList(word)
        }
        backstack.goBack()
    }

    // NOTE: Data is typically in the database, so do this only for transient state.
    override fun toBundle(): StateBundle = StateBundle().apply {
        putStringArrayList("words", ArrayList(mutableWords.value))
    }

    override fun fromBundle(bundle: StateBundle?) {
        bundle?.run {
            mutableWords.value = getStringArrayList("words")
        }
    }
}