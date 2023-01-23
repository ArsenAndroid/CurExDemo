package am.gtest.curex.demo.data.repo.rates

import am.gtest.curex.demo.data.local.dao.RatesDao
import am.gtest.curex.demo.data.model.RateModel
import am.gtest.curex.demo.data.remote.api.ApiInterfaceRates
import com.google.gson.JsonObject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException

class RatesRepositoryImpl(
    private val apiInterfaceRates: ApiInterfaceRates,
    private val ratesDao: RatesDao
) : RatesRepository {

//    private val logTag = "Log: " + javaClass.simpleName

    override fun getLocalRates(): Flow<List<RateModel>> = ratesDao.getLocalRates()

    override suspend fun getRemoteRates() {
        try {
            val response = apiInterfaceRates.getRates()

            if (response.isSuccessful) {
                response.body()?.rates?.let {
                    val ratesList = parseRates(it)
                    ratesDao.deleteRates()
                    ratesDao.insertRates(ratesList)
                }
            }
        } catch (e: CancellationException) {
            e.printStackTrace()
        } catch (e: HttpException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun parseRates(rates: JsonObject): List<RateModel> {

        val ratesList = mutableListOf<RateModel>()

        for (rate in rates.entrySet()) {
            val rateName = rate?.key
            var rateValue = -1.0

            try {
                rateValue = rate.value.asDouble
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (!rateName.isNullOrEmpty() && rateValue > 0) {
                val rateModel = RateModel(currency = rateName, rate = rateValue)
                ratesList.add(rateModel)
            }
        }

        return ratesList
    }
}