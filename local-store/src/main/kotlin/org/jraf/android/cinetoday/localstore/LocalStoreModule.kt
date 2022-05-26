package org.jraf.android.cinetoday.localstore

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface LocalStoreModule {
    @Binds
    fun TheaterLocalSource(
        theaterLocalSource: TheaterLocalSourceImpl,
    ): TheaterLocalSource

    companion object {
        @Provides
        @Singleton
        fun provideSqldelightDatabase(@ApplicationContext context: Context): Database = createSqldelightDatabase(context)
    }
}
