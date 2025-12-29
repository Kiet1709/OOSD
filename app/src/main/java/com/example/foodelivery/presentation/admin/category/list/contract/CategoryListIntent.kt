package com.example.foodelivery.presentation.admin.category.list.contract

import com.example.foodelivery.core.base.ViewIntent

// 3. INTENT
sealed class CategoryListIntent : ViewIntent {
    data class SearchCategory(val query: String) : CategoryListIntent()
    object ClickAddCategory : CategoryListIntent()
    data class ClickEditCategory(val id: String) : CategoryListIntent()
    data class ClickDeleteCategory(val id: String) : CategoryListIntent()
}