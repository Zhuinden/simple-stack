package com.zhuinden.simplestackkotlindaggerexample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.jetbrains.anko.intentFor

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intentFor<MainActivity>()
        startActivity(intent)
        finish()
    }
}