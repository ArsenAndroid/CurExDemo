package am.gtest.curex.demo.data.local.dao

import am.gtest.curex.demo.data.model.BalanceModel
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BalancesDao {

    @Query("SELECT * FROM tbl_balances")
    fun getLocalBalances(): Flow<List<BalanceModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBalances(balances: List<BalanceModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBalance(balance: BalanceModel)

    @Query("DELETE FROM tbl_balances")
    suspend fun deleteBalances()

    @Query("DELETE FROM tbl_balances WHERE currency = :currency")
    suspend fun deleteBalance(currency: String)
}