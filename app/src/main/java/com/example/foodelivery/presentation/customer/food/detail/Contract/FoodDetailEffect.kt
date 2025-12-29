package com.example.foodelivery.presentation.customer.food.detail.Contract

import com.example.foodelivery.core.base.ViewSideEffect

sealed class FoodDetailEffect :ViewSideEffect  {
    object NavigateBack : FoodDetailEffect()
    data class ShowToast(val msg: String) : FoodDetailEffect()
    object NavigateToCart : FoodDetailEffect() // Nếu muốn thêm xong chuyển sang giỏ luôn
}