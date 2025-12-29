package com.example.foodelivery.data.repository

import android.net.Uri
import com.example.foodelivery.core.common.Resource
import java.util.UUID
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StorageRepositoryImpl @Inject constructor(
    private val storage: FirebaseStorage
) {
    suspend fun uploadImage(uri: Uri, folder: String): Resource<String> {
        return try {
            val filename = UUID.randomUUID().toString()
            val ref = storage.reference.child("$folder/$filename")
            ref.putFile(uri).await()
            val url = ref.downloadUrl.await().toString()
            Resource.Success(url)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Upload ảnh thất bại")
        }
    }
}