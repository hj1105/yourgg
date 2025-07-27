package org.geng.yourgg.config

import okhttp3.OkHttpClient
import org.geng.yourgg.retrofit.RiotApiService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Configuration
class RetrofitConfig {

    @Value("\${riot.api.key}")
    private lateinit var apiKey: String

    @Bean
    fun okHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val newUrl = originalRequest.url().newBuilder()
                    .addQueryParameter("api_key", apiKey)
                    .build()
                val newRequest = originalRequest.newBuilder().url(newUrl).build()
                chain.proceed(newRequest)
            }.build()
    }

    // 지역(Regional) API 서비스들을 Map 형태로 주입
    @Bean
    fun regionalApiServices(okHttpClient: OkHttpClient): Map<String, RiotApiService> {
        val regionalHosts = listOf("asia", "americas", "europe", "sea")
        return regionalHosts.associateWith { host ->
            createRetrofit("https://$host.api.riotgames.com", okHttpClient)
                .create(RiotApiService::class.java)
        }
    }

    private fun createRetrofit(baseUrl: String, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}