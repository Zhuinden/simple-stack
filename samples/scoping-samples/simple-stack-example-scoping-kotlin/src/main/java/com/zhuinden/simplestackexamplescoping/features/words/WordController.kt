package com.zhuinden.simplestackexamplescoping.features.words

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.zhuinden.eventemitter.EventEmitter
import com.zhuinden.eventemitter.EventSource
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.Bundleable
import com.zhuinden.statebundle.StateBundle

class WordEventEmitter(
    private val eventEmitter: EventSource<WordController.Events>
) : EventSource<WordController.Events> by eventEmitter

class WordController(
    private val backstack: Backstack
) : NewWordFragment.ActionHandler,
    WordListFragment.ActionHandler,
    WordListFragment.DataProvider,
    Bundleable {
    sealed class Events {
        data class NewWordAdded(val word: String) : Events()
    }

    private val wordEventEmitter = EventEmitter<Events>()
    val eventEmitter = WordEventEmitter(wordEventEmitter)

    private val mutableWords: MutableLiveData<List<String>> = MutableLiveData(
        listOf("Bogus", "Magic", "Scoping mechanisms")
    )

    override val wordList: LiveData<List<String>>
        get() = mutableWords

    private fun addWordToList(word: String) {
        mutableWords.run {
            postValue(value!!.toMutableList().also { list -> list.add(word) })
        }
        wordEventEmitter.emit(Events.NewWordAdded(word))
    }

    override fun onAddNewWordClicked() {
        backstack.goTo(NewWordKey)
    }

    override fun onAddWordClicked(word: String) {
        if (word.isNotEmpty()) {
            addWordToList(word)
        }
        backstack.goBack()
    }

    // NOTE: Data is typically in the database, so do this only for non-transient state.
    override fun toBundle(): StateBundle = StateBundle().apply {
        putStringArrayList("words", ArrayList(mutableWords.value))
    }

    override fun fromBundle(bundle: StateBundle?) {
        bundle?.run {
            mutableWords.value = getStringArrayList("words")
        }
    }
}