package com.example.foodelivery.presentation.admin.food.list.contract

import com.example.foodelivery.core.base.ViewIntent

sealed class FoodListIntent : ViewIntent {
    // 1. Tìm kiếm
    data class SearchFood(val query: String) : FoodListIntent()

    // 2. Các thao tác Click
    object ClickAddFood : FoodListIntent()
    data class ClickEditFood(val id: String) : FoodListIntent()
    data class ClickDeleteFood(val id: String) : FoodListIntent()

    // 3. Nghiệp vụ nhanh: Bật/Tắt còn hàng
    data class ToggleAvailability(val id: String) : FoodListIntent()
}