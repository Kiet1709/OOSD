package com.example.foodelivery.presentation.admin.food.contract

import com.example.foodelivery.core.base.ViewIntent
import com.example.foodelivery.core.base.ViewSideEffect
import com.example.foodelivery.core.base.ViewState
import com.example.foodelivery.domain.model.Category
import com.example.foodelivery.domain.model.Food

data class AdminFoodState(
    val isLoading: Boolean = false,
    val foods: List<Food> = emptyList(),
    val categories: List<Category> = emptyList(),
    val error: String? = null,
    val currentFood: Food? = null // Dùng khi Edit
) : ViewState

sealed class AdminFoodIntent : ViewIntent {
    object LoadData : AdminFoodIntent() // Load list food & categories
    data class LoadFoodDetail(val id: String) : AdminFoodIntent()
    data class DeleteFood(val id: String) : AdminFoodIntent()
    
    // Save (Add or Update)
    data class SaveFood(
        val id: String? = null,
        val name: String, 
        val desc: String, 
        val price: Double, 
        val imageUrl: String, 
        val categoryId: String
    ) : AdminFoodIntent()
}

sealed class AdminFoodEffect : ViewSideEffect {
    data class ShowToast(val message: String) : AdminFoodEffect()
    object NavigateBack : AdminFoodEffect()
}