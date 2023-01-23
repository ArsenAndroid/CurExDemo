package am.gtest.curex.demo.di

import am.gtest.curex.demo.utils.MyPrefs
import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PrefsModule {

    @Singleton
    @Provides
    @Named(MyPrefs.PREF_FILE_CURRENT_USER)
    fun provideSharedPreferenceCurrentUser(@ApplicationContext ctx: Context): SharedPreferences {
        return ctx.getSharedPreferences(MyPrefs.PREF_FILE_CURRENT_USER, Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    @Named(MyPrefs.PREF_FILE_ANY_USER)
    fun provideSharedPreferenceAnyUser(@ApplicationContext ctx: Context): SharedPreferences {
        return ctx.getSharedPreferences(MyPrefs.PREF_FILE_ANY_USER, Context.MODE_PRIVATE)
    }
}