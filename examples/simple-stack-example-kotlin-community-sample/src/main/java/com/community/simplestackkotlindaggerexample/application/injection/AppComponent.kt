package com.community.simplestackkotlindaggerexample.application.injection

import com.community.simplestackkotlindaggerexample.screens.users.UsersFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AppModule::class
])
interface AppComponent {
    fun inject(usersFragment: UsersFragment)
}