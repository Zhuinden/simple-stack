package com.community.simplestackkotlindaggerexample.application

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.community.simplestackkotlindaggerexample.R
import com.community.simplestackkotlindaggerexample.core.navigation.FragmentStateChanger
import com.community.simplestackkotlindaggerexample.screens.home.HomeKey
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.StateChanger
import com.zhuinden.simplestack.navigator.Navigator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), StateChanger {
    private lateinit var fragmentStateChanger: FragmentStateChanger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        fragmentStateChanger = FragmentStateChanger(supportFragmentManager, R.id.root)

        Navigator.configure()
            .setStateChanger(this)
            .install(this, root, History.single(HomeKey()))
    }

    override fun onBackPressed() {
        if (!Navigator.onBackPressed(this)) {
            super.onBackPressed()
        }
    }

    override fun handleStateChange(stateChange: StateChange, completionCallback: StateChanger.Callback) {
        if (stateChange.isTopNewKeyEqualToPrevious) {
            completionCallback.stateChangeComplete()
            return
        }
        fragmentStateChanger.handleStateChange(stateChange)
        completionCallback.stateChangeComplete()
    }

    // share activity through context
    override fun getSystemService(name: String): Any? = when (name) {
        TAG -> this
        else -> super.getSystemService(name)
    }

    companion object {
        private const val TAG = "MainActivity"

        @SuppressLint("WrongConstant")
        fun get(context: Context): MainActivity =
            context.getSystemService(TAG) as MainActivity
    }
}