package com.zhuinden.simplestackexamplescoping

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.StateChanger
import com.zhuinden.simplestack.navigator.Navigator
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by Zhuinden on 2018.09.17.
 */
class MainActivity : AppCompatActivity(), StateChanger {
    private lateinit var fragmentStateChanger: FragmentStateChanger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        fragmentStateChanger = FragmentStateChanger(supportFragmentManager, R.id.root)

        Navigator.configure()
            .setStateChanger(this)
            .setScopedServices(ScopeConfiguration())
            .install(this, root, History.of(WordListKey()))
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
}
