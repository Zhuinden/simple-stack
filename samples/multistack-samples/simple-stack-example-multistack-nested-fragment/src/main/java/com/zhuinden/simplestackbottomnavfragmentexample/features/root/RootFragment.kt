package com.zhuinden.simplestackbottomnavfragmentexample.features.root

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.zhuinden.simplestackbottomnavfragmentexample.R
import com.zhuinden.simplestackbottomnavfragmentexample.core.navigation.FragmentStackHostFragment
import com.zhuinden.simplestackbottomnavfragmentexample.databinding.RootFragmentBinding
import com.zhuinden.simplestackbottomnavfragmentexample.utils.viewBinding
import com.zhuinden.simplestackextensions.fragments.KeyedFragment

class RootFragment : KeyedFragment(R.layout.root_fragment) {
    private val binding by viewBinding(RootFragmentBinding::bind)

    private lateinit var firstFragment: FragmentStackHostFragment
    private lateinit var secondFragment: FragmentStackHostFragment
    private lateinit var thirdFragment: FragmentStackHostFragment

    private val fragments: Array<Fragment> get() = arrayOf(firstFragment, secondFragment, thirdFragment)
    private var selectedIndex = 0

    private val tabs: Array<TextView>
        get() = binding.run {
            arrayOf(buttonFirstTab, buttonSecondTab, buttonThirdTab)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            val firstFragment = FragmentStackHostFragment.newInstance(RootScreen.FIRST_STACK).also { this.firstFragment = it }
            val secondFragment = FragmentStackHostFragment.newInstance(RootScreen.SECOND_STACK).also { this.secondFragment = it }
            val thirdFragment = FragmentStackHostFragment.newInstance(RootScreen.THIRD_STACK).also { this.thirdFragment = it }

            childFragmentManager.beginTransaction()
                .add(R.id.containerBottomNavContent, firstFragment, "firstFragment")
                .add(R.id.containerBottomNavContent, secondFragment, "secondFragment")
                .add(R.id.containerBottomNavContent, thirdFragment, "thirdFragment")
                .selectFragment(selectedIndex)
                .commit()
        } else {
            selectedIndex = savedInstanceState.getInt("selectedIndex", 0)

            firstFragment = childFragmentManager.findFragmentByTag("firstFragment") as FragmentStackHostFragment
            secondFragment = childFragmentManager.findFragmentByTag("secondFragment") as FragmentStackHostFragment
            thirdFragment = childFragmentManager.findFragmentByTag("thirdFragment") as FragmentStackHostFragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabs.forEachIndexed { index, textView ->
            textView.setOnClickListener {
                selectFragment(index)
            }
        }

        setupTabSelectedState(selectedIndex)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("selectedIndex", selectedIndex)
    }

    private fun FragmentTransaction.selectFragment(selectedIndex: Int): FragmentTransaction {
        fragments.forEachIndexed { index, fragment ->
            if (index == selectedIndex) {
                attach(fragment)
            } else {
                detach(fragment)
            }
        }

        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)

        return this
    }

    private fun setupTabSelectedState(selectedIndex: Int) {
        tabs.forEachIndexed { index, textView ->
            textView.setTextColor(when {
                index == selectedIndex -> ContextCompat.getColor(requireContext(), R.color.tab_selected)
                else -> ContextCompat.getColor(requireContext(), R.color.tab_unselected)
            })
        }
    }

    private fun selectFragment(indexToSelect: Int) {
        this.selectedIndex = indexToSelect

        setupTabSelectedState(indexToSelect)

        childFragmentManager.beginTransaction()
            .selectFragment(indexToSelect)
            .commit()
    }
}