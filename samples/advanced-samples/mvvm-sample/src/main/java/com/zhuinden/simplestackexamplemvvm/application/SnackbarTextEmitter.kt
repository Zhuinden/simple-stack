package com.zhuinden.simplestackexamplemvvm.application

import com.zhuinden.eventemitter.EventEmitter
import com.zhuinden.eventemitter.EventSource

class SnackbarTextEmitter {
    private val emitter: EventEmitter<Int> = EventEmitter()
    val snackbarText: EventSource<Int> = emitter

    fun emit(text: Int) {
        emitter.emit(text)
    }
}