package com.example.foodelivery.presentation.admin.food.detail.contract

import android.net.Uri
import com.example.foodelivery.core.base.ViewIntent

sealed class FoodDetailIntent : ViewIntent {
    // Load dữ liệu (nếu là sửa)
    data class LoadFoodDetail(val id: String) : FoodDetailIntent()

    // Nhập liệu
    data class NameChanged(val name: String) : FoodDetailIntent()
    data class PriceChanged(val price: String) : FoodDetailIntent()
    data class DescriptionChanged(val desc: String) : FoodDetailIntent()
    data class ImageSelected(val uri: Uri?) : FoodDetailIntent()

    // Bấm nút Lưu
    object ClickSubmit : FoodDetailIntent()
}