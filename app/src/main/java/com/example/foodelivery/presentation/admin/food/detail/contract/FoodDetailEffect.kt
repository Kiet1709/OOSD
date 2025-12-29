package com.example.foodelivery.presentation.admin.food.detail.contract

import com.example.foodelivery.core.base.ViewSideEffect

sealed class FoodDetailEffect : ViewSideEffect {
    data class ShowToast(val message: String) : FoodDetailEffect()
    object NavigateBack : FoodDetailEffect() // Lưu xong thì quay lại list
}