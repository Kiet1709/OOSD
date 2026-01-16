package com.example.foodelivery.presentation.restaurant.food.detail.contract

import com.example.foodelivery.core.base.ViewIntent

sealed class RestaurantAddEditFoodIntent : ViewIntent {
    data class LoadFoodDetails(val foodId: String) : RestaurantAddEditFoodIntent()
    data class NameChanged(val name: String) : RestaurantAddEditFoodIntent()
    data class DescriptionChanged(val description: String) : RestaurantAddEditFoodIntent()
    data class PriceChanged(val price: String) : RestaurantAddEditFoodIntent()
    data class ImageUrlChanged(val imageUrl: String) : RestaurantAddEditFoodIntent()
    data class CategorySelected(val categoryId: String) : RestaurantAddEditFoodIntent()
    object Submit : RestaurantAddEditFoodIntent()
}