package am.gtest.curex.demo.data.local

import am.gtest.curex.demo.data.local.dao.RatesDao
import am.gtest.curex.demo.data.model.RateModel
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Named

@ExperimentalCoroutinesApi
@SmallTest
@HiltAndroidTest
//@UninstallModules(DbModule::class)
class RatesDaoTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    @Named("test_db")
    lateinit var database: AppDatabase
    private lateinit var dao: RatesDao

    @Before
    fun setup() {
        hiltRule.inject()
        dao = database.ratesDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertProductIntoDb() = runTest {

        val rates = generateRates()

        dao.insertRates(rates)

        val dbRates = dao.getLocalRates().first()
        assertThat(dbRates).contains(rates[0])
    }

    @Test
    fun deleteProductFromDb() = runTest {

        val rates = generateRates()

        dao.insertRates(rates)
        dao.deleteRates()

        val dbRates = dao.getLocalRates().first()
        assertThat(dbRates).doesNotContain(rates[0])
    }

    private fun generateRates(): List<RateModel> {
        return listOf(RateModel(currency = "EUR", rate = 1.0))
    }
}