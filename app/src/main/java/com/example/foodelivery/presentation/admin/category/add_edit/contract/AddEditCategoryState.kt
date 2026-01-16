package com.example.foodelivery.presentation.admin.category.add_edit.contract

import android.net.Uri
import com.example.foodelivery.core.base.ViewState

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