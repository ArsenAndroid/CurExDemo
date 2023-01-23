package am.gtest.curex.demo.ui

import am.gtest.curex.demo.data.model.BalanceModel
import am.gtest.curex.demo.data.model.ExchangeModel
import am.gtest.curex.demo.data.model.RateModel
import am.gtest.curex.demo.data.remote.response.MyResponse
import am.gtest.curex.demo.data.repo.balances.BalancesRepository
import am.gtest.curex.demo.data.repo.rates.RatesRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val balancesRepository: BalancesRepository,
    private val ratesRepository: RatesRepository
) : ViewModel() {

//    private val logTag = "Log: " + javaClass.simpleName

    private val _exchangeChannelFlow = Channel<MyResponse<ExchangeModel>>()
    val exchangeChannelFlow = _exchangeChannelFlow.receiveAsFlow()

    fun getLocalBalances(): Flow<List<BalanceModel>> = balancesRepository.getLocalBalances()

    fun insertInitialBalance() {
        viewModelScope.launch {
            balancesRepository.insertInitialBalance()
        }
    }

    fun getLocalRates(): Flow<List<RateModel>> = ratesRepository.getLocalRates()

    fun getRemoteRates() {
        viewModelScope.launch {
            ratesRepository.getRemoteRates()
        }
    }

    fun doExchange(exchangeModel: ExchangeModel) {
        balancesRepository.doExchange(exchangeModel)
            .onEach { response ->
                _exchangeChannelFlow.send(response)
            }
            .launchIn(viewModelScope)
    }
}