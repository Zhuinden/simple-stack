package com.zhuinden.simplestackkotlindaggerexample;

import com.zhuinden.simplestackkotlindaggerexample.reponses.AllUsersResponse
import io.reactivex.Observable
import retrofit2.http.GET

interface ApiService {
    @GET("users.json")
    fun getAllUsers(): Observable<AllUsersResponse>
}