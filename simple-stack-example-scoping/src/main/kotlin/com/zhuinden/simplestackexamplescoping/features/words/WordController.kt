package com.zhuinden.simplestackexamplescoping.features.words

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.zhuinden.commandqueue.CommandQueue
import com.zhuinden.simplestack.Bundleable
import com.zhuinden.simplestack.History
import com.zhuinden.statebundle.StateBundle

class WordController : Bundleable {
    sealed class Events {
        data class NewWordAdded(val word: String) : Events()
    }

    private val mutableWords: MutableLiveData<List<String>> = MutableLiveData()

    val commandQueue: CommandQueue<Events> = CommandQueue()

    init {
        mutableWords.value = History.of("Bogus", "Magic", "Scoping mechanisms")
    }

    val wordList: LiveData<List<String>>
        get() = mutableWords

    fun addWordToList(word: String) {
        mutableWords.run {
            postValue(value!!.toMutableList().also { list -> list.add(word) })
        }
        commandQueue.sendEvent(Events.NewWordAdded(word))
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