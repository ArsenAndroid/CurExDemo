package am.gtest.curex.demo.data.model

data class ExchangeModel(
    val sellCurrency: String,
    var sellBalance: Double = 0.0,
    var sellAmount: Double = 0.0,
    val receiveCurrency: String,
    var receiveBalance: Double = 0.0,
    var receiveAmount: Double = 0.0,
    var fees: Double = 0.0,
)