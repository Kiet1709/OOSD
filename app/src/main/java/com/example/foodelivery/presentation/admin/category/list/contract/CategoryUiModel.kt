package com.example.foodelivery.presentation.admin.category.list.contract

data class CategoryUiModel(
    val id: String,
    val name: String,
    val imageUrl: String,
    val totalFoods: Int
)