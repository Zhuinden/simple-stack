package com.example.ktdagger;

import com.example.ktdagger.reponses.AllUsersResponse
import io.reactivex.Observable
import retrofit2.http.GET

interface ApiService {
    @GET("users.json")
    fun getAllUsers(): Observable<AllUsersResponse>
}