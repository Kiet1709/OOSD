package com.example.foodelivery.presentation.admin.food.detail.contract

import android.net.Uri
import com.example.foodelivery.core.base.ViewIntent

sealed class FoodDetailIntent : ViewIntent {
    data class LoadFoodDetail(val id: String) : FoodDetailIntent()
    data class NameChanged(val name: String) : FoodDetailIntent()
    data class PriceChanged(val price: String) : FoodDetailIntent()
    data class DescriptionChanged(val desc: String) : FoodDetailIntent()
    
    // Hình ảnh
    data class ImageSelected(val uri: Uri) : FoodDetailIntent()
    data class ImageUrlChanged(val url: String) : FoodDetailIntent() // Mới: Nhập URL trực tiếp

    // Danh mục
    data class CategorySelected(val categoryId: String) : FoodDetailIntent() // Mới: Chọn danh mục

    object ClickSubmit : FoodDetailIntent()
}