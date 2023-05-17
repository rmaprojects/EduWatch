package com.rmaprojects.core.di

import com.rmaprojects.core.domain.interactor.UseCasesInteractor
import com.rmaprojects.core.domain.use_cases.EduWatchUseCases
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    @Binds
    abstract fun provideUseCases(useCasesInteractor: UseCasesInteractor): EduWatchUseCases
}