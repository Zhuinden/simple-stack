package com.zhuinden.simplestackdemoexample.delegate

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.RelativeLayout
import butterknife.BindView
import butterknife.ButterKnife
import com.zhuinden.simplestack.BackstackDelegate
import com.zhuinden.simplestack.HistoryBuilder
import com.zhuinden.simplestack.navigator.DefaultStateChanger
import com.zhuinden.simplestackdemoexample.R
import com.zhuinden.simplestackdemoexample.common.BackstackService
import com.zhuinden.simplestackdemoexample.common.FirstKey

class BackstackDelegateActivity : AppCompatActivity() {
    @BindView(R.id.root)
    lateinit var root: RelativeLayout

    lateinit internal var backstackDelegate: BackstackDelegate
    lateinit internal var defaultStateChanger: DefaultStateChanger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        backstackDelegate = BackstackDelegate(null)
        backstackDelegate.onCreate(savedInstanceState, //
                lastCustomNonConfigurationInstance, //
                HistoryBuilder.single(FirstKey.create()))
        defaultStateChanger = DefaultStateChanger.configure()
                .setStatePersistenceStrategy(object : DefaultStateChanger.StatePersistenceStrategy {
                    override fun persistViewToState(previousKey: Any, previousView: View) {
                        backstackDelegate.persistViewToState(previousView)
                    }

                    override fun restoreViewFromState(newKey: Any, newView: View) {
                        backstackDelegate.restoreViewFromState(newView)
                    }
                })
                .create(this, root)
        backstackDelegate.setStateChanger(defaultStateChanger)
    }

    override fun onRetainCustomNonConfigurationInstance(): Any {
        return backstackDelegate.onRetainCustomNonConfigurationInstance()
    }

    override fun onPostResume() {
        super.onPostResume()
        backstackDelegate.onPostResume()
    }

    override fun onBackPressed() {
        if (!backstackDelegate.onBackPressed()) {
            super.onBackPressed()
        }
    }

    override fun onPause() {
        backstackDelegate.onPause()
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        backstackDelegate.persistViewToState(root.getChildAt(0))
        backstackDelegate.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        backstackDelegate.onDestroy()
        super.onDestroy()
    }

    override fun getSystemService(name: String): Any {
        if (name == BackstackService.TAG) {
            return backstackDelegate.backstack
        }
        return super.getSystemService(name)
    }
}
