package com.zhuinden.simplestackdemoexamplefragments.core.navigation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.*
import com.zhuinden.simplestack.KeyContextWrapper
import com.zhuinden.simplestackdemoexamplefragments.util.requireArguments

/**
 * Created by Zhuinden on 2017.01.26..
 */

abstract class BaseFragment : Fragment() {
    private lateinit var key: FragmentKey

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(getKey<FragmentKey>().menu(), menu)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        requireArguments.getParcelable<FragmentKey>(KEY_TAG).let { key ->
            this.key = key!!
            LayoutInflater.from(KeyContextWrapper(inflater.context, key)).inflate(key.layout(), container, false)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    @Suppress("UNCHECKED_CAST")
    fun <K : FragmentKey> getKey(): K = key as K

    companion object {
        const val KEY_TAG = "KEY"
    }
}
