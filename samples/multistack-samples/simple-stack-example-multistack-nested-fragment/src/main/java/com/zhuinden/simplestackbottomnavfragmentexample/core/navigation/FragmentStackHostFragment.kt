package com.zhuinden.simplestackbottomnavfragmentexample.core.navigation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.zhuinden.simplestack.SimpleStateChanger
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestackbottomnavfragmentexample.R
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentStateChanger
import com.zhuinden.simplestackextensions.fragmentsktx.lookup

class FragmentStackHostFragment : Fragment(R.layout.stack_host_fragment), SimpleStateChanger.NavigationHandler {
    companion object {
        fun newInstance(stackHostId: String): FragmentStackHostFragment = FragmentStackHostFragment().apply {
            arguments = Bundle().also { bundle ->
                bundle.putString("stackHostId", stackHostId)
            }
        }
    }

    private lateinit var stateChanger: DefaultFragmentStateChanger

    private val stackHost by lazy { lookup<FragmentStackHost>(requireArguments().getString("stackHostId")!!) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        stateChanger = DefaultFragmentStateChanger(childFragmentManager, R.id.containerStackHost)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        stackHost.backstack.setStateChanger(SimpleStateChanger(this))
    }

    override fun onResume() {
        super.onResume()

        stackHost.backstack.reattachStateChanger()

        stackHost.isActiveForBack = true
    }

    override fun onPause() {
        stackHost.isActiveForBack = false

        stackHost.backstack.detachStateChanger()

        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        stackHost.backstack.executePendingStateChange()
    }

    override fun onNavigationEvent(stateChange: StateChange) {
        stateChanger.handleStateChange(stateChange)
    }
}