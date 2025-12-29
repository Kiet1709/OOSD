package com.example.foodelivery.presentation.admin.food.detail.contract

import android.net.Uri
import com.example.foodelivery.core.base.ViewState

data class FoodDetailState(
    // Trạng thái chung
    val isLoading: Boolean = false,
    val isEditMode: Boolean = false, // false = Thêm mới, true = Sửa
    val existingFoodId: String? = null,

    // Dữ liệu Form
    val name: String = "",
    val price: String = "", // Dùng String để dễ handle nhập liệu, sẽ parse sang Double sau
    val description: String = "",

    // Hình ảnh
    val selectedImageUri: Uri? = null, // Ảnh mới chọn từ thư viện (Local)
    val serverImageUrl: String? = null, // Ảnh cũ từ server (Remote) - chỉ dùng khi Edit

    // Validation Errors (Hiển thị lỗi màu đỏ dưới ô nhập)
    val nameError: String? = null,
    val priceError: String? = null
): ViewState