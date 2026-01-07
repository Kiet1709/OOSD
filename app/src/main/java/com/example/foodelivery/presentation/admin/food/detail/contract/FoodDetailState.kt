package com.example.foodelivery.presentation.admin.food.detail.contract

import android.net.Uri
import com.example.foodelivery.core.base.ViewState
import com.example.foodelivery.domain.model.Category

data class FoodDetailState(
    // Trạng thái chung
    val isLoading: Boolean = false,
    val isEditMode: Boolean = false,
    val existingFoodId: String? = null,

    // Dữ liệu Form
    val name: String = "",
    val price: String = "",
    val description: String = "",
    
    // Hình ảnh
    val selectedImageUri: Uri? = null, 
    val serverImageUrl: String? = null, 
    
    // Danh mục
    val categories: List<Category> = emptyList(), // Danh sách danh mục để chọn
    val selectedCategoryId: String = "", // ID danh mục đang chọn

    // Errors
    val nameError: String? = null,
    val priceError: String? = null
): ViewState