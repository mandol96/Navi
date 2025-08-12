package com.cho.navi.di

import com.cho.navi.data.PostRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    fun provideStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    @Provides
    fun providePostRepository(
        db: FirebaseFirestore, storage: FirebaseStorage
    ): PostRepository {
        return PostRepository(db, storage)
    }

}