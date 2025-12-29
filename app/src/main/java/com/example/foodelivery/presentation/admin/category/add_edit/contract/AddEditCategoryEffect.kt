package com.example.foodelivery.presentation.admin.category.add_edit.contract

import com.example.foodelivery.core.base.ViewSideEffect

sealed class AddEditCategoryEffect : ViewSideEffect {
    data class ShowToast(val message: String) : AddEditCategoryEffect()
    object NavigateBack : AddEditCategoryEffect()
}