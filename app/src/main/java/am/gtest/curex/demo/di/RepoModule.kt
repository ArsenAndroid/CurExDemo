package am.gtest.curex.demo.di

import am.gtest.curex.demo.data.local.dao.BalancesDao
import am.gtest.curex.demo.data.local.dao.RatesDao
import am.gtest.curex.demo.data.remote.api.ApiInterfaceRates
import am.gtest.curex.demo.data.repo.balances.BalancesRepository
import am.gtest.curex.demo.data.repo.balances.BalancesRepositoryImpl
import am.gtest.curex.demo.data.repo.rates.RatesRepository
import am.gtest.curex.demo.data.repo.rates.RatesRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepoModule {

    @Singleton
    @Provides
    fun provideRatesRepository(api: ApiInterfaceRates, ratesDao: RatesDao): RatesRepository {
        return RatesRepositoryImpl(api, ratesDao)
    }

    @Singleton
    @Provides
    fun provideBalancesRepository(balancesDao: BalancesDao): BalancesRepository {
        return BalancesRepositoryImpl(balancesDao)
    }
}