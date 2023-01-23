package am.gtest.curex.demo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_rates")
data class RateModel(
    @PrimaryKey
    val currency: String,
    val rate: Double,
)