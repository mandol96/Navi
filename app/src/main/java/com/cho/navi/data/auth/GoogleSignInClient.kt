package com.cho.navi.data.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.cho.navi.BuildConfig
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

class GoogleSignInClient(
    private val context: Context,
    private val credentialManager: CredentialManager
) {

    suspend fun getGoogleIdToken(): Result<String> {
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

            val googleIdTokenCredential =
                GoogleIdTokenCredential.Companion.createFrom(credential.data)

            googleIdTokenCredential.idToken
        }
    }
}