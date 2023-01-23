package am.gtest.curex.demo.data.repo

import am.gtest.curex.demo.data.model.BalanceModel
import am.gtest.curex.demo.data.model.ExchangeModel
import am.gtest.curex.demo.data.remote.response.MyResponse
import am.gtest.curex.demo.data.repo.balances.BalancesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeBalancesRepository : BalancesRepository {

    override fun getLocalBalances(): Flow<List<BalanceModel>> = flow {
        emit(emptyList())
    }

    override suspend fun insertInitialBalance() {

    }

    override fun doExchange(exchangeModel: ExchangeModel): Flow<MyResponse<ExchangeModel>> = flow {
        emit(MyResponse.Success(exchangeModel))
    }
}