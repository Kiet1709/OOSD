package com.example.foodelivery.presentation.admin.category.list.contract

import com.example.foodelivery.core.base.ViewSideEffect

sealed class CategoryListEffect : ViewSideEffect {
    data class ShowToast(val message: String) : CategoryListEffect()
    // Navigation (Sau này sẽ dẫn sang màn hình Add/Edit)
    object NavigateToAddScreen : CategoryListEffect()
    data class NavigateToEditScreen(val id: String) : CategoryListEffect()
}