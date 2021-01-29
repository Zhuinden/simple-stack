package com.zhuinden.simplestackdemomultistack.util

import androidx.annotation.IdRes
import it.sephiroth.android.library.bottomnavigation.BottomNavigation

inline fun BottomNavigation.onMenuItemSelected(
    crossinline onMenuItemSelected: (menuItemId: Int, itemIndex: Int, b: Boolean) -> Unit
): BottomNavigation.OnMenuItemSelectionListener {
    return onMenuItemClick(onMenuItemSelected = onMenuItemSelected, onMenuItemReselected = { _, _, _ -> })
}

inline fun BottomNavigation.onMenuItemClick(
    crossinline onMenuItemSelected: (menuItemId: Int, itemIndex: Int, b: Boolean) -> Unit,
    crossinline onMenuItemReselected: (menuItemId: Int, itemIndex: Int, b: Boolean) -> Unit
): BottomNavigation.OnMenuItemSelectionListener {
    val listener = object : BottomNavigation.OnMenuItemSelectionListener {
        override fun onMenuItemSelect(@IdRes menuItemId: Int, itemIndex: Int, b: Boolean) {
            onMenuItemSelected(menuItemId, itemIndex, b)
        }

        override fun onMenuItemReselect(@IdRes menuItemId: Int, itemIndex: Int, b: Boolean) {
            onMenuItemReselected(menuItemId, itemIndex, b)
        }
    }
    setOnMenuItemClickListener(listener)
    return listener
}