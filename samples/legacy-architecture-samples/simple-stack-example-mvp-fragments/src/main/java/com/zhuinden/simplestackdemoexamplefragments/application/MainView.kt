package com.zhuinden.simplestackdemoexamplefragments.application

import android.content.Context
import android.content.res.Configuration
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import android.util.AttributeSet
import android.view.View
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestackdemoexamplefragments.R
import com.zhuinden.simplestackdemoexamplefragments.core.navigation.FragmentKey
import com.zhuinden.simplestackdemoexamplefragments.features.statistics.StatisticsKey
import com.zhuinden.simplestackdemoexamplefragments.features.tasks.TasksKey
import com.zhuinden.simplestackdemoexamplefragments.util.showIf
import kotlinx.android.synthetic.main.activity_main.view.*

/**
 * Created by Zhuinden on 2018. 08. 20.
 */

class MainView : DrawerLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private lateinit var drawerLayout: DrawerLayout

    private lateinit var drawerToggle: ActionBarDrawerToggle

    private val navigationItemSelectedListener = NavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.list_navigation_menu_item -> Navigator.getBackstack(context).goTo(TasksKey())
            R.id.statistics_navigation_menu_item -> Navigator.getBackstack(context).goTo(StatisticsKey())
            else -> {
            }
        }
        setCheckedItem(item.getItemId())
        // Close the navigation drawer when an item is selected.
        drawerLayout.closeDrawers()
        true
    }

    private fun setCheckedItem(navigationItemId: Int) {
        val menu = navigationView.menu
        for (i in 0 until menu.size()) {
            val menuItem = menu.getItem(i)
            menuItem.isChecked = menuItem.itemId == navigationItemId
        }
    }

    fun setupViewsForKey(key: FragmentKey) {
        val actionBar = MainActivity[context].supportActionBar!!
        if (key.shouldShowUp()) {
            setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START)
            drawerToggle.isDrawerIndicatorEnabled = false
            actionBar.setDisplayHomeAsUpEnabled(true)
        } else {
            setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START)
            actionBar.setDisplayHomeAsUpEnabled(false)
            drawerToggle.isDrawerIndicatorEnabled = true
        }
        drawerToggle.syncState()
        setCheckedItem(key.navigationViewId())
        MainActivity[context].invalidateOptionsMenu()

        buttonAddTask.showIf { key.isFabVisible }

        val fragment = MainActivity[context].supportFragmentManager.findFragmentByTag(key.fragmentTag)!!
        buttonAddTask.setOnClickListener(key.fabClickListener(fragment))
        if (key.fabDrawableIcon() != 0) {
            buttonAddTask.setImageResource(key.fabDrawableIcon())
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        drawerLayout = this
    }

    fun onCreate() {
        navigationView.setNavigationItemSelectedListener(navigationItemSelectedListener)

        MainActivity[context].setSupportActionBar(toolbar)

        val actionBar = MainActivity[context].supportActionBar!!

        drawerToggle = object : ActionBarDrawerToggle(MainActivity[context], drawerLayout, toolbar, R.string.open, R.string.close) {
            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                MainActivity[context].invalidateOptionsMenu()
            }

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                MainActivity[context].invalidateOptionsMenu()
            }
        }
        @Suppress("DEPRECATION")
        drawerLayout.setDrawerListener(drawerToggle)

        drawerToggle.setToolbarNavigationClickListener { _ -> Navigator.getBackstack(context).goBack() }
        actionBar.setDisplayHomeAsUpEnabled(false)
        actionBar.setHomeButtonEnabled(true)
    }

    fun onPostCreate() {
        drawerToggle.syncState()
    }

    fun onConfigChanged(newConfig: Configuration) {
        drawerToggle.onConfigurationChanged(newConfig)
    }

    fun onBackPressed(): Boolean {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers()
            return true
        }
        return false
    }
}
