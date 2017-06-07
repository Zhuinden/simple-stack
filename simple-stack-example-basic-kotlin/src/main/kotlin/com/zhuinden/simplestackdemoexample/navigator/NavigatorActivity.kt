package com.zhuinden.simplestackdemoexample.navigator

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.RelativeLayout
import butterknife.BindView
import butterknife.ButterKnife
import com.zhuinden.simplestack.HistoryBuilder
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestackdemoexample.R
import com.zhuinden.simplestackdemoexample.common.BackstackService
import com.zhuinden.simplestackdemoexample.common.FirstKey

/**
 * Created by Owner on 2017. 04. 07..
 */

class NavigatorActivity : AppCompatActivity() {
    @BindView(R.id.root)
    lateinit var root: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        Navigator.install(this, root, HistoryBuilder.single(FirstKey.create()))
    }

    override fun onBackPressed() {
        if (!Navigator.onBackPressed(this)) {
            super.onBackPressed()
        }
    }

    override fun getSystemService(name: String): Any {
        if (name == BackstackService.TAG) {
            return Navigator.getBackstack(this)
        }
        return super.getSystemService(name)
    }
}
