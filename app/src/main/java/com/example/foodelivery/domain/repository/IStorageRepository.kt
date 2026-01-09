package com.example.foodelivery.domain.repository

import android.net.Uri
import com.example.foodelivery.core.common.Resource

interface IStorageRepository {

    suspend fun uploadImage(uri: Uri?, folder: String): Resource<String>

    suspend fun uploadImages(uris: List<Uri>, folder: String): Resource<List<String>>

    suspend fun deleteImage(imageUrl: String): Resource<Boolean>

    suspend fun imageExists(imageUrl: String): Resource<Boolean>
}