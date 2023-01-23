package am.gtest.curex.demo.data.remote.api

import am.gtest.curex.demo.data.remote.response.RatesResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiInterfaceRates {

    @GET("currency-exchange-rates")
    suspend fun getRates(): Response<RatesResponse>
}