package am.gtest.curex.demo.data.repo.rates

import am.gtest.curex.demo.data.model.RateModel
import kotlinx.coroutines.flow.Flow

interface RatesRepository {

    fun getLocalRates(): Flow<List<RateModel>>

    suspend fun getRemoteRates()
}