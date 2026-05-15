package com.example.shilpakala.utils

import android.content.Context
import com.example.shilpakala.domain.model.UserProfile
import com.example.shilpakala.domain.model.PortfolioPhoto
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CloudSyncService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun hasFirebase(): Boolean = FirebaseApp.getApps(context).isNotEmpty()

    suspend fun syncPhoto(photo: PortfolioPhoto): Boolean {
        if (!hasFirebase()) return false
        return runCatching {
            FirebaseFirestore.getInstance()
                .collection("photos")
                .document(photo.id)
                .set(photo)
                .await()
            true
        }.getOrDefault(false)
    }

    suspend fun syncProfile(profile: UserProfile): Boolean {
        if (!hasFirebase()) return false
        return runCatching {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(profile.id)
                .set(profile)
                .await()
            true
        }.getOrDefault(false)
    }

    suspend fun deletePhoto(photoId: String) {
        if (!hasFirebase()) return
        runCatching {
            FirebaseFirestore.getInstance()
                .collection("photos")
                .document(photoId)
                .delete()
                .await()
        }
    }

    suspend fun signUpWithFirebase(email: String, password: String): String? {
        if (!hasFirebase()) return null
        return runCatching {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).await().user?.uid
        }.getOrNull()
    }

    suspend fun loginWithFirebase(email: String, password: String): String? {
        if (!hasFirebase()) return null
        return runCatching {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).await().user?.uid
        }.getOrNull()
    }

    suspend fun loginWithGoogleIdToken(idToken: String): String? {
        if (!hasFirebase()) return null
        return runCatching {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            FirebaseAuth.getInstance().signInWithCredential(credential).await().user?.uid
        }.getOrNull()
    }

    fun currentFirebaseUserId(): String? = if (hasFirebase()) FirebaseAuth.getInstance().currentUser?.uid else null

    fun logoutFirebase() {
        if (hasFirebase()) FirebaseAuth.getInstance().signOut()
    }
}
