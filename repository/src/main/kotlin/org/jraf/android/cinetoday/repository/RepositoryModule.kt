package org.jraf.android.cinetoday.repository

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Binds
    fun bindsTheaterRepository(
        theaterRepository: TheaterRepositoryImpl,
    ): TheaterRepository
}
