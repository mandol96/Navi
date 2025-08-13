package com.cho.navi.di

import android.content.Context
import androidx.credentials.CredentialManager
import com.cho.navi.data.auth.GoogleSignInClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppClientModule {

    @Provides
    fun provideCredentialManager(@ApplicationContext context: Context): CredentialManager {
        return CredentialManager.create(context)
    }

    @Provides
    fun provideGoogleSignInClient(@ApplicationContext context: Context): GoogleSignInClient {
        return GoogleSignInClient(context, CredentialManager.create(context))
    }
}