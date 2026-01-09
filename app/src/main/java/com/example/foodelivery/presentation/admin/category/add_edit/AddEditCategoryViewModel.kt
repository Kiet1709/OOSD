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
        // Get categoryId from navigation argument
        val id = savedStateHandle.get<String>("categoryId")
        if (id != null && id != "new") {
            processIntent(AddEditCategoryIntent.LoadCategory(id))
        }
    }

    fun processIntent(intent: AddEditCategoryIntent) {
        when (intent) {
            is AddEditCategoryIntent.LoadCategory -> loadData(intent.id)
            is AddEditCategoryIntent.NameChanged -> {
                _state.update { it.copy(name = intent.value, nameError = null) }
            }
            is AddEditCategoryIntent.ImageSelected -> {
                _state.update { it.copy(imageUri = intent.uri, imageError = null) }
            }
            AddEditCategoryIntent.ClickBack -> {
                sendEffect(AddEditCategoryEffect.NavigateBack)
            }
            AddEditCategoryIntent.Submit -> {
                submit()
            }
        }
    }

    private fun loadData(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            when (val result = getByIdUseCase(id)) {
                is Resource.Success -> {
                    val cat = result.data
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isEditMode = true,
                            categoryId = cat?.id,
                            name = cat?.name ?: "",
                            imageUrl = cat?.imageUrl
                        )
                    }
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false) }
                    sendEffect(AddEditCategoryEffect.ShowToast("Lỗi tải dữ liệu"))
                    sendEffect(AddEditCategoryEffect.NavigateBack)
                }
                else -> {
                    _state.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    private fun submit() {
        val currentState = _state.value

        // Validate name
        if (currentState.name.isBlank()) {
            _state.update { it.copy(nameError = "Tên không được để trống") }
            return
        }

        // Validate image (required for new category)
        if (!currentState.isEditMode && currentState.imageUri == null) {
            _state.update { it.copy(imageError = "Vui lòng chọn ảnh") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // Step 1: Upload image if selected
            var finalImageUrl = currentState.imageUrl ?: ""

            if (currentState.imageUri != null) {
                when (val uploadResult = uploadImageUseCase(currentState.imageUri)) {
                    is Resource.Success -> {
                        finalImageUrl = uploadResult.data ?: ""
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(isLoading = false) }
                        sendEffect(AddEditCategoryEffect.ShowToast("Lỗi upload ảnh: ${uploadResult.message}"))
                        return@launch
                    }
                    else -> {
                        _state.update { it.copy(isLoading = false) }
                        sendEffect(AddEditCategoryEffect.ShowToast("Lỗi upload ảnh"))
                        return@launch
                    }
                }
            }

            // Step 2: Save category
            val saveResult = if (currentState.isEditMode) {
                updateUseCase(
                    Category(
                        id = currentState.categoryId ?: "",
                        name = currentState.name,
                        imageUrl = finalImageUrl
                    )
                )
            } else {
                addUseCase(currentState.name, finalImageUrl)
            }

            _state.update { it.copy(isLoading = false) }

            when (saveResult) {
                is Resource.Success -> {
                    val message = if (currentState.isEditMode) "Cập nhật thành công" else "Thêm thành công"
                    sendEffect(AddEditCategoryEffect.ShowToast(message))
                    sendEffect(AddEditCategoryEffect.NavigateBack)
                }
                is Resource.Error -> {
                    sendEffect(AddEditCategoryEffect.ShowToast("Lỗi: ${saveResult.message}"))
                }
                else -> {
                    sendEffect(AddEditCategoryEffect.ShowToast("Lỗi không xác định"))
                }
            }
        }
    }

    private fun sendEffect(effect: AddEditCategoryEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}