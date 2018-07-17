package com.example.ktdagger

import android.content.Context
import com.example.ktdagger.schedulers.BaseSchedulerProvider
import com.example.ktdagger.schedulers.SchedulerProvider

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

import javax.inject.Singleton

import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


@Module
class AppModule(private val context: Context) {

    @Provides
    @Singleton
    internal fun provideApplicationContext(): Context {
        return context
    }

    @Provides
    internal fun provideScheduler(): BaseSchedulerProvider {
        return SchedulerProvider.getInstance()
    }

    @Provides
    internal fun provideCompositeDisposable(): CompositeDisposable {
        return CompositeDisposable()
    }

    @Provides
    @Singleton
    internal fun provideService(gson: Gson, okHttpClient: OkHttpClient): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build()
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    internal fun provideGson(): Gson {
        val gsonBuilder = GsonBuilder()
        return gsonBuilder.create()
    }

    @Provides
    @Singleton
    internal fun provideHttpCache(context: Context): Cache {
        return Cache(context.cacheDir, 10000)
    }

    @Provides
    @Singleton
    internal fun provideOkhttpClient(cache: Cache): OkHttpClient {
        val clientBuilder = OkHttpClient.Builder()
        clientBuilder.cache(cache)

        clientBuilder.connectTimeout(10, TimeUnit.DAYS)
        clientBuilder.readTimeout(10, TimeUnit.DAYS)

        clientBuilder
            .addInterceptor(HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.HEADERS))

        return clientBuilder.build()
    }
}
