package com.zhuinden.simplestackexamplekotlin.delegate

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.RelativeLayout
import butterknife.BindView
import butterknife.ButterKnife
import com.zhuinden.simplestack.BackstackDelegate
import com.zhuinden.simplestack.HistoryBuilder
import com.zhuinden.simplestack.navigator.DefaultStateChanger
import com.zhuinden.simplestackexamplekotlin.R
import com.zhuinden.simplestackexamplekotlin.common.BackstackService
import com.zhuinden.simplestackexamplekotlin.common.FirstKey

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
        backstackDelegate.registerForLifecycleCallbacks(this)
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

    override fun onBackPressed() {
        if (!backstackDelegate.onBackPressed()) {
            super.onBackPressed()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        backstackDelegate.persistViewToState(root.getChildAt(0))
    }

    override fun getSystemService(name: String): Any {
        if (name == BackstackService.TAG) {
            return backstackDelegate.backstack
        }
        return super.getSystemService(name)
    }
}
