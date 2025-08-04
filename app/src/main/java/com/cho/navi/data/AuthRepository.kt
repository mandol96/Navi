package com.cho.navi.data

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
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
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(BuildConfig.CLIENT_ID)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

//            try {
            val result = credentialManager.getCredential(context, request)
            val credential = result.credential

            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val googleIdToken = googleIdTokenCredential.idToken

            val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)

            auth.signInWithCredential(firebaseCredential).await().user
//            } catch (e: GetCredentialException) {
//                Log.e("CredentialManager", "No credential available", e)
//                null
//            }
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}