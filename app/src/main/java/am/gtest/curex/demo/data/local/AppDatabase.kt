package am.gtest.curex.demo.data.local

import am.gtest.curex.demo.data.local.dao.BalancesDao
import am.gtest.curex.demo.data.local.dao.RatesDao
import am.gtest.curex.demo.data.model.BalanceModel
import am.gtest.curex.demo.data.model.RateModel
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [BalanceModel::class, RateModel::class],
    exportSchema = false,
    version = 2
)

abstract class AppDatabase : RoomDatabase() {
    abstract fun balancesDao(): BalancesDao
    abstract fun ratesDao(): RatesDao
}
