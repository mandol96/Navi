package com.cho.navi.data.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.cho.navi.BuildConfig
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val credentialManager: CredentialManager,
    private val auth: FirebaseAuth
) {

    suspend fun signInWithGoogle(context: Context): Result<FirebaseUser?> {
        return runCatching {
            val authorizedIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(true)
                .setServerClientId(BuildConfig.CLIENT_ID)
                .setAutoSelectEnabled(true)
                .build()

            val allAccountsOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(BuildConfig.CLIENT_ID)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(authorizedIdOption)
                .addCredentialOption(allAccountsOption)
                .build()


            val result = try {
                credentialManager.getCredential(context, request)
            } catch (e: GetCredentialException) {
                    val fallbackRequest = GetCredentialRequest.Builder()
                        .addCredentialOption(allAccountsOption)
                        .build()
                    credentialManager.getCredential(context, fallbackRequest)
            }
            val credential = result.credential

            val googleIdTokenCredential = GoogleIdTokenCredential.Companion.createFrom(credential.data)
            val googleIdToken = googleIdTokenCredential.idToken

            val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)

            auth.signInWithCredential(firebaseCredential).await().user
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}