package com.example.habittrackerapp.di

import com.example.habittrackerapp.data.remote.FirestoreHabitDataSource
import com.example.habittrackerapp.data.remote.HabitRemoteDataSource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Provides
    @Singleton
    fun provideHabitRemoteDataSource(
        firestore: FirebaseFirestore
    ): HabitRemoteDataSource {
        return FirestoreHabitDataSource(firestore)
    }
}