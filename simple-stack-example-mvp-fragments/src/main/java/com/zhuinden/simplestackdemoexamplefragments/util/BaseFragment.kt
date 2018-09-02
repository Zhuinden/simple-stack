package com.zhuinden.simplestackdemoexamplefragments.util

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import com.zhuinden.simplestack.KeyContextWrapper
import com.zhuinden.simplestackdemoexamplefragments.application.Key

/**
 * Created by Zhuinden on 2017.01.26..
 */

abstract class BaseFragment<V, P : MvpPresenter<V>> : Fragment() {
    private lateinit var key: Key

    protected abstract val presenter: P

    protected abstract fun getThis(): V

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(getKey<Key>().menu(), menu)
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = requireArguments.getParcelable<Key>(KEY_TAG).let { key ->
        this.key = key
        LayoutInflater.from(KeyContextWrapper(inflater.context, key)).inflate(key.layout(), container, false)
    } 
        
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.attachView(getThis())
    }

    override fun onDestroyView() {
        presenter.detachView(getThis())
        super.onDestroyView()
    }

    @Suppress("UNCHECKED_CAST")
    fun <K : Key> getKey(): K = key as K

    companion object {
        const val KEY_TAG = "KEY"
    }
}
