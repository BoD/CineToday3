package org.jraf.android.cinetoday.api

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ApiModule {
    @Binds
    fun TheaterRemoteSource(
        theaterRemoteSource: TheaterRemoteSourceImpl,
    ): TheaterRemoteSource

    companion object {
        @Provides
        @Singleton
        fun provideApolloClient() = createApolloClient()
    }
}
