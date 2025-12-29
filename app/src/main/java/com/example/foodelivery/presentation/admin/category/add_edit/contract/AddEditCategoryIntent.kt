package com.example.foodelivery.presentation.admin.category.add_edit.contract

import android.net.Uri
import com.example.foodelivery.core.base.ViewIntent

sealed class AddEditCategoryIntent : ViewIntent {
    data class LoadCategory(val id: String) : AddEditCategoryIntent()
    data class NameChanged(val value: String) : AddEditCategoryIntent()
    data class ImageSelected(val uri: Uri?) : AddEditCategoryIntent()
    object Submit : AddEditCategoryIntent()
    object ClickBack : AddEditCategoryIntent()
}