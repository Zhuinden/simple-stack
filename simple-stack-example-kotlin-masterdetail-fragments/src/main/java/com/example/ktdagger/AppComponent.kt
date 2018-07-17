package com.example.ktdagger

import com.example.ktdagger.userdetail.UsersFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    (AppModule::class)
])
interface AppComponent {
    fun inject(usersFragment: UsersFragment)
}

