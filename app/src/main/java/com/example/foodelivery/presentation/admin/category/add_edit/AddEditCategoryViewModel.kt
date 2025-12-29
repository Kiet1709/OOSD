package com.example.foodelivery.presentation.admin.category.add_edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.Category
import com.example.foodelivery.domain.usecase.admin.UploadImageUseCase
import com.example.foodelivery.domain.usecase.category.*
import com.example.foodelivery.presentation.admin.category.add_edit.contract.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditCategoryViewModel @Inject constructor(
    private val addUseCase: AddCategoryUseCase,
    private val updateUseCase: UpdateCategoryUseCase,
    private val getByIdUseCase: GetCategoryByIdUseCase,
    private val uploadImageUseCase: UploadImageUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(AddEditCategoryState())
    val state = _state.asStateFlow()

    private val _effect = Channel<AddEditCategoryEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        // Lấy ID truyền từ Navigation: "categoryId"
        val id = savedStateHandle.get<String>("categoryId")
        if (id != null && id != "new") {
            processIntent(AddEditCategoryIntent.LoadCategory(id))
        }
    }

    fun processIntent(intent: AddEditCategoryIntent) {
        when (intent) {
            is AddEditCategoryIntent.LoadCategory -> loadData(intent.id)
            is AddEditCategoryIntent.NameChanged -> _state.update { it.copy(name = intent.value, nameError = null) }
            is AddEditCategoryIntent.ImageSelected -> _state.update { it.copy(imageUri = intent.uri, imageError = null) }
            AddEditCategoryIntent.ClickBack -> sendEffect(AddEditCategoryEffect.NavigateBack)
            AddEditCategoryIntent.Submit -> submit()
        }
    }

    private fun loadData(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = getByIdUseCase(id)) {
                is Resource.Success -> {
                    val cat = result.data
                    _state.update { it.copy(isLoading = false, isEditMode = true, categoryId = cat?.id, name = cat?.name ?: "", imageUrl = cat?.imageUrl) }
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false) }
                    sendEffect(AddEditCategoryEffect.ShowToast("Lỗi tải dữ liệu"))
                    sendEffect(AddEditCategoryEffect.NavigateBack)
                }
                else -> {}
            }
        }
    }

    private fun submit() {
        val s = _state.value
        if (s.name.isBlank()) {
            _state.update { it.copy(nameError = "Tên không được để trống") }
            return
        }
        if (!s.isEditMode && s.imageUri == null) {
            _state.update { it.copy(imageError = "Vui lòng chọn ảnh") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // Upload ảnh (nếu có)
            var finalUrl = s.imageUrl ?: ""
            if (s.imageUri != null) {
                when (val uploadRes = uploadImageUseCase(s.imageUri)) {
                    is Resource.Success -> finalUrl = uploadRes.data ?: ""
                    is Resource.Error -> {
                        _state.update { it.copy(isLoading = false) }
                        sendEffect(AddEditCategoryEffect.ShowToast("Lỗi upload ảnh"))
                        return@launch
                    }
                    else -> {}
                }
            }

            // Save
            val result = if (s.isEditMode) {
                updateUseCase(Category(s.categoryId!!, s.name, finalUrl))
            } else {
                addUseCase(s.name, finalUrl)
            }

            _state.update { it.copy(isLoading = false) }

            when (result) {
                is Resource.Success -> {
                    sendEffect(AddEditCategoryEffect.ShowToast("Thành công"))
                    sendEffect(AddEditCategoryEffect.NavigateBack)
                }
                is Resource.Error -> sendEffect(AddEditCategoryEffect.ShowToast(result.message ?: "Lỗi"))
                else -> {}
            }
        }
    }

    private fun sendEffect(effect: AddEditCategoryEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}