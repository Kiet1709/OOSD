package com.example.foodelivery.domain.usecase.admin

import android.net.Uri
import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.data.repository.StorageRepositoryImpl
import javax.inject.Inject

class UploadImageUseCase @Inject constructor(
    private val storageRepository: StorageRepositoryImpl
) {
    suspend operator fun invoke(uri: Uri): Resource<String> {
        return storageRepository.uploadImage(uri, "food_images")
    }
}