package com.example.foodelivery.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.repository.IStorageRepository
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class StorageRepositoryImpl @Inject constructor(
    private val firebaseStorage: FirebaseStorage, // Vẫn giữ để không lỗi Dagger, dù không dùng đến
    private val context: Context
) : IStorageRepository {


     // Chuyển đổi ảnh từ Uri -> Bitmap -> Nén -> Chuỗi Base64

    override suspend fun uploadImage(uri: Uri?, folder: String): Resource<String> {
        return withContext(Dispatchers.IO) {
            try {
                if (uri == null) return@withContext Resource.Error("Uri không hợp lệ")

                // 1. Đọc Uri thành Bitmap
                val inputStream = context.contentResolver.openInputStream(uri)
                val originalBitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                if (originalBitmap == null) return@withContext Resource.Error("Không thể đọc ảnh")

                // 2. Nén ảnh nhỏ lại (QUAN TRỌNG: Để không vượt quá 1MB giới hạn của Firestore)
                // Resize xuống còn khoảng 600px (đủ nét cho điện thoại)
                val scaledBitmap = resizeBitmap(originalBitmap, 600)

                // 3. Chuyển Bitmap thành Base64 String
                val outputStream = ByteArrayOutputStream()
                // Nén format JPEG, chất lượng 70% để giảm dung lượng string
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
                val byteArray = outputStream.toByteArray()

                // Tạo chuỗi Base64
                val base64String = Base64.encodeToString(byteArray, Base64.NO_WRAP)

                // Thêm prefix để thư viện ảnh (Coil) hiểu đây là ảnh Base64
                val finalResult = "data:image/jpeg;base64,$base64String"

                Resource.Success(finalResult)

            } catch (e: Exception) {
                e.printStackTrace()
                Resource.Error("Lỗi xử lý ảnh: ${e.message}")
            }
        }
    }


    override suspend fun uploadImages(uris: List<Uri>, folder: String): Resource<List<String>> {
        val list = mutableListOf<String>()
        for (uri in uris) {
            when (val res = uploadImage(uri, folder)) {
                is Resource.Success -> {
                    res.data?.let { list.add(it) }
                }
                is Resource.Error -> {
                    // Nếu 1 ảnh lỗi, có thể return Error ngay hoặc bỏ qua tùy logic của bạn
                    // Ở đây mình chọn return Error luôn để báo user
                    return Resource.Error(res.message ?: "Lỗi khi upload một trong các ảnh")
                }
                else -> {}
            }
        }
        return Resource.Success(list)
    }


    override suspend fun deleteImage(imageUrl: String): Resource<Boolean> {
        return Resource.Success(true)
    }


    override suspend fun imageExists(imageUrl: String): Resource<Boolean> {
        return Resource.Success(true)
    }


    private fun resizeBitmap(source: Bitmap, maxLength: Int): Bitmap {
        try {
            if (source.height >= maxLength || source.width >= maxLength) {
                var targetWidth = maxLength
                var targetHeight = maxLength

                if (source.height > source.width) {
                    // Ảnh dọc
                    targetHeight = maxLength
                    val ratio = source.width.toFloat() / source.height
                    targetWidth = (maxLength * ratio).toInt()
                } else {
                    // Ảnh ngang hoặc vuông
                    targetWidth = maxLength
                    val ratio = source.height.toFloat() / source.width
                    targetHeight = (maxLength * ratio).toInt()
                }
                return Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false)
            }
            return source
        } catch (e: Exception) {
            return source
        }
    }
}