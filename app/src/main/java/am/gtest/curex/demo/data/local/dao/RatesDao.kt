package am.gtest.curex.demo.data.local.dao

import am.gtest.curex.demo.data.model.RateModel
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RatesDao {

    @Query("SELECT * FROM tbl_rates")
    fun getLocalRates(): Flow<List<RateModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRates(rates: List<RateModel>)

    @Query("DELETE FROM tbl_rates")
    suspend fun deleteRates()
}