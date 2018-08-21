package com.zhuinden.simplestackdemoexamplefragments.util

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import com.zhuinden.simplestack.KeyContextWrapper
import com.zhuinden.simplestackdemoexamplefragments.application.Key
import com.zhuinden.statebundle.StateBundle

/**
 * Created by Zhuinden on 2017.01.26..
 */

abstract class BaseFragment<V : BaseViewContract, P : BasePresenter<V>> : Fragment() {

    abstract val presenter: P

    abstract fun getThis(): V

    private lateinit var key: Key

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        if (savedInstanceState != null) {
            presenter.fromBundle(savedInstanceState.getParcelable<StateBundle>("PRESENTER_STATE"))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(getKey<Key>().menu(), menu)
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = arguments!!.getParcelable<Key>(KEY_TAG).let { key ->
        this.key = key
        LayoutInflater.from(KeyContextWrapper(inflater.context, key)).inflate(key.layout(), container, false)
    } 
        
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.attachFragment(getThis())
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("PRESENTER_STATE", presenter.toBundle())
    }

    override fun onDestroyView() {
        presenter.detachFragment(getThis())
        super.onDestroyView()
    }

    @Suppress("UNCHECKED_CAST")
    fun <K : Key> getKey(): K = key as K

    companion object {
        const val KEY_TAG = "KEY"
    }
}
