package com.zhuinden.simplestackexamplescoping

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.KeyChange
import com.zhuinden.simplestack.KeyChanger
import com.zhuinden.simplestack.navigator.Navigator
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by Zhuinden on 2018.09.17.
 */
class MainActivity : AppCompatActivity(), KeyChanger {
    private lateinit var fragmentKeyChanger: FragmentKeyChanger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        fragmentKeyChanger = FragmentKeyChanger(supportFragmentManager, R.id.root)

        Navigator.configure()
            .setKeyChanger(this)
            .setScopedServices(ScopeConfiguration())
            .setShouldPersistContainerChild(false)
            .install(this, root, History.of(WordListKey()))
    }

    override fun onBackPressed() {
        if (!Navigator.onBackPressed(this)) {
            super.onBackPressed()
        }
    }

    override fun handleKeyChange(keyChange: KeyChange, completionCallback: KeyChanger.Callback) {
        if (keyChange.isTopNewKeyEqualToPrevious) {
            completionCallback.keyChangeComplete()
            return
        }
        fragmentKeyChanger.handleKeyChange(keyChange)
        completionCallback.keyChangeComplete()
    }
}
