package am.gtest.curex.demo.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import am.gtest.curex.demo.data.remote.api.ApiInterfaceRates
import am.gtest.curex.demo.utils.MyGlobals
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Singleton
    @Provides
    fun provideApiInterfaceRates(retrofit: Retrofit): ApiInterfaceRates = retrofit.create(ApiInterfaceRates::class.java)

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(MyGlobals.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {

        val client = OkHttpClient.Builder()

            // for logs
//            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))

            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)

            .addInterceptor { chain: Interceptor.Chain ->
                val reqBuilder = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json")

                val request = reqBuilder.build()
                val response = chain.proceed(request)

                response
            }

            .build()

        return client
    }
}