package com.zhuinden.simplestackkotlindaggerexample

import com.zhuinden.simplestackkotlindaggerexample.userdetail.UsersFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    (AppModule::class)
])
interface AppComponent {
    fun inject(usersFragment: UsersFragment)
}

