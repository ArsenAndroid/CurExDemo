package am.gtest.curex.demo.data.repo.balances

import am.gtest.curex.demo.data.local.dao.BalancesDao
import am.gtest.curex.demo.data.model.BalanceModel
import am.gtest.curex.demo.data.model.ExchangeModel
import am.gtest.curex.demo.data.remote.response.MyResponse
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class BalancesRepositoryImpl(
    private val balancesDao: BalancesDao
) : BalancesRepository {

//    private val logTag = "Log: " + javaClass.simpleName

    override fun getLocalBalances(): Flow<List<BalanceModel>> = balancesDao.getLocalBalances()

    override suspend fun insertInitialBalance() {
        val initialBalance = BalanceModel(
            currency = "EUR",
            balance = 1000.0
        )
        balancesDao.insertBalance(initialBalance)
    }

    override fun doExchange(exchangeModel: ExchangeModel): Flow<MyResponse<ExchangeModel>> = flow {
        try {
            val newSellBalance = exchangeModel.sellBalance - exchangeModel.sellAmount - exchangeModel.fees
            val newReceiveBalance = exchangeModel.receiveBalance + exchangeModel.receiveAmount
            val sellBalanceModel = BalanceModel(exchangeModel.sellCurrency, newSellBalance)
            val receiveBalanceModel = BalanceModel(exchangeModel.receiveCurrency, newReceiveBalance)
            val changingBalances = listOf(sellBalanceModel, receiveBalanceModel)

            balancesDao.insertBalances(changingBalances)

            emit(MyResponse.Success(exchangeModel))

        } catch (e: CancellationException) {
            e.printStackTrace()
        } catch (e: Exception) {
            emit(MyResponse.Error())
        }
    }
}