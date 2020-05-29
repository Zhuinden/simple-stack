package com.community.simplestackkotlindaggerexample.application.injection

import android.content.Context
import com.community.simplestackkotlindaggerexample.core.schedulers.SchedulerProvider
import com.community.simplestackkotlindaggerexample.core.schedulers.SchedulerProviderImpl
import com.community.simplestackkotlindaggerexample.data.api.ApiService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
class AppModule(private val context: Context) {
    @Provides
    fun applicationContext(): Context = context

    @Provides
    fun scheduler(impl: SchedulerProviderImpl): SchedulerProvider = impl

    @Provides
    @Singleton
    fun apiService(gson: Gson, okHttpClient: OkHttpClient): ApiService =
        Retrofit.Builder().apply {
            baseUrl("http://10.0.2.2:3000/")
            addConverterFactory(GsonConverterFactory.create(gson))
            addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            client(okHttpClient)
        }.build().create(ApiService::class.java)

    @Provides
    @Singleton
    fun gson(): Gson = GsonBuilder().setLenient().create()

    @Provides
    @Singleton
    fun httpCache(context: Context): Cache = Cache(context.cacheDir, 10000)

    @Provides
    @Singleton
    fun okhttpClient(cache: Cache): OkHttpClient = OkHttpClient.Builder().apply {
        cache(cache)
        connectTimeout(10, TimeUnit.SECONDS)
        readTimeout(10, TimeUnit.SECONDS)
        addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
    }.build()
}
