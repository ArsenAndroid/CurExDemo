package am.gtest.curex.demo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_balances")
data class BalanceModel(
    @PrimaryKey
    val currency: String,
    var balance: Double = 0.0,
)