package am.gtest.curex.demo.data.repo.balances

import am.gtest.curex.demo.data.model.BalanceModel
import am.gtest.curex.demo.data.model.ExchangeModel
import am.gtest.curex.demo.data.remote.response.MyResponse
import kotlinx.coroutines.flow.Flow

interface BalancesRepository {

    fun getLocalBalances(): Flow<List<BalanceModel>>

    suspend fun insertInitialBalance()

    fun doExchange(exchangeModel: ExchangeModel): Flow<MyResponse<ExchangeModel>>
}