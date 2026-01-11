package com.example.foodelivery.presentation.admin.category.add_edit.contract

import android.net.Uri
import com.example.foodelivery.core.base.ViewSideEffect
import com.example.foodelivery.core.base.ViewIntent
import com.example.foodelivery.core.base.ViewState

// --- STATE ---
data class AddEditCategoryState(
    val isLoading: Boolean = false,
    val isEditMode: Boolean = false,
    val categoryId: String? = null,
    val name: String = "",
    val imageUrl: String? = null,
    val imageUri: Uri? = null, // For selected image from device
    val nameError: String? = null,
    val imageError: String? = null
) : ViewState

// --- INTENT ---
sealed class AddEditCategoryIntent : ViewIntent {
    data class LoadCategory(val id: String) : AddEditCategoryIntent()
    data class NameChanged(val value: String) : AddEditCategoryIntent()
    data class ImageUrlChanged(val value: String) : AddEditCategoryIntent()
    data class ImageSelected(val uri: Uri) : AddEditCategoryIntent()
    object Submit : AddEditCategoryIntent()
    object ClickBack : AddEditCategoryIntent()
}

// --- EFFECT ---
sealed class AddEditCategoryEffect : ViewSideEffect {
    data class ShowToast(val message: String) : AddEditCategoryEffect()
    object NavigateBack : AddEditCategoryEffect()
}