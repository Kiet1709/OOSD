package com.example.foodelivery.presentation.admin.category.add_edit.contract

import android.net.Uri
import com.example.foodelivery.core.base.ViewState

data class AddEditCategoryState(
    val isEditMode: Boolean = false,
    val categoryId: String? = null,
    val name: String = "",
    val imageUri: Uri? = null,
    val imageUrl: String? = null,
    val isLoading: Boolean = false,
    val nameError: String? = null,
    val imageError: String? = null
) : ViewState