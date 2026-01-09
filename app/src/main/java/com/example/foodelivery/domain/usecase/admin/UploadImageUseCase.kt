package com.example.foodelivery.domain.usecase.admin

import android.net.Uri
import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.repository.IStorageRepository
import javax.inject.Inject

class UploadImageUseCase @Inject constructor(
    private val storageRepository: IStorageRepository
) {

    suspend operator fun invoke(uri: Uri?): Resource<String> {
        // Validate
        if (uri == null) {
            return Resource.Error("Chưa chọn ảnh")
        }

        // Upload to "images" folder in Firebase Storage
        return storageRepository.uploadImage(uri, "images")
    }
}