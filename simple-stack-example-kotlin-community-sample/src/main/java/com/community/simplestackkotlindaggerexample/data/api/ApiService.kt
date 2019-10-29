package com.community.simplestackkotlindaggerexample.data.api

import io.reactivex.Observable
import retrofit2.http.GET

interface ApiService {
    @GET("users.json")
    fun getAllUsers(): Observable<AllUsersResponse>
}