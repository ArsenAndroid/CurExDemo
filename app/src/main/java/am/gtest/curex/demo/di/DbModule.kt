package am.gtest.curex.demo.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import am.gtest.curex.demo.data.local.AppDatabase
import am.gtest.curex.demo.data.local.dao.BalancesDao
import am.gtest.curex.demo.data.local.dao.RatesDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DbModule {

    @Singleton
    @Provides
    fun provideAppDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(app, AppDatabase::class.java, "curex_room")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideBalancesDao(appDatabase: AppDatabase): BalancesDao = appDatabase.balancesDao()

    @Singleton
    @Provides
    fun provideRatesDao(appDatabase: AppDatabase): RatesDao = appDatabase.ratesDao()
}