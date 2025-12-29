package com.example.foodelivery.presentation.admin.food.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.Food
import com.example.foodelivery.domain.usecase.admin.UploadImageUseCase
import com.example.foodelivery.domain.usecase.food.AddFoodUseCase
import com.example.foodelivery.domain.usecase.food.GetFoodDetailUseCase
import com.example.foodelivery.domain.usecase.food.UpdateFoodUseCase
import com.example.foodelivery.presentation.admin.food.detail.contract.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FoodDetailViewModel @Inject constructor(
    private val getFoodDetailUseCase: GetFoodDetailUseCase,
    private val addFoodUseCase: AddFoodUseCase,
    private val updateFoodUseCase: UpdateFoodUseCase,
    private val uploadImageUseCase: UploadImageUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(FoodDetailState())
    val state = _state.asStateFlow()

    private val _effect = Channel<FoodDetailEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        // Lấy ID từ Navigation argument
        val foodId = savedStateHandle.get<String>("foodId")
        if (foodId != null && foodId != "new") {
            processIntent(FoodDetailIntent.LoadFoodDetail(foodId))
        }
    }

    fun processIntent(intent: FoodDetailIntent) {
        when (intent) {
            is FoodDetailIntent.LoadFoodDetail -> loadDetail(intent.id)
            is FoodDetailIntent.NameChanged -> _state.update { it.copy(name = intent.name, nameError = null) }
            is FoodDetailIntent.PriceChanged -> _state.update { it.copy(price = intent.price, priceError = null) }
            is FoodDetailIntent.DescriptionChanged -> _state.update { it.copy(description = intent.desc) }
            is FoodDetailIntent.ImageSelected -> _state.update { it.copy(selectedImageUri = intent.uri) }
            FoodDetailIntent.ClickSubmit -> submit()
        }
    }

    private fun loadDetail(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = getFoodDetailUseCase(id)) {
                is Resource.Success -> {
                    val food = result.data
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isEditMode = true,
                            existingFoodId = food?.id,
                            name = food?.name ?: "",
                            price = food?.price?.toLong()?.toString() ?: "",
                            description = food?.description ?: "",
                            serverImageUrl = food?.imageUrl
                        )
                    }
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false) }
                    sendEffect(FoodDetailEffect.ShowToast("Lỗi tải thông tin"))
                    sendEffect(FoodDetailEffect.NavigateBack)
                }
                else -> {}
            }
        }
    }

    private fun submit() {
        if (!validate()) return

        val s = _state.value
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // 1. Upload ảnh (nếu có chọn mới)
            var finalUrl = s.serverImageUrl ?: ""
            if (s.selectedImageUri != null) {
                when (val uploadRes = uploadImageUseCase(s.selectedImageUri)) {
                    is Resource.Success -> finalUrl = uploadRes.data ?: ""
                    is Resource.Error -> {
                        _state.update { it.copy(isLoading = false) }
                        sendEffect(FoodDetailEffect.ShowToast("Lỗi upload ảnh: ${uploadRes.message}"))
                        return@launch
                    }
                    else -> {}
                }
            }

            // 2. Tạo object
            val food = Food(
                id = s.existingFoodId ?: "",
                name = s.name,
                price = s.price.toDouble(),
                description = s.description,
                imageUrl = finalUrl,
                categoryId = "DEFAULT", // Có thể thêm dropdown category sau
                rating = 5.0,
                isAvailable = true
            )

            // 3. Save
            val result = if (s.isEditMode) updateFoodUseCase(food) else addFoodUseCase(food)

            _state.update { it.copy(isLoading = false) }

            when (result) {
                is Resource.Success -> {
                    sendEffect(FoodDetailEffect.ShowToast(if (s.isEditMode) "Cập nhật thành công" else "Thêm mới thành công"))
                    sendEffect(FoodDetailEffect.NavigateBack)
                }
                is Resource.Error -> sendEffect(FoodDetailEffect.ShowToast(result.message ?: "Lỗi lưu dữ liệu"))
                else -> {}
            }
        }
    }

    private fun validate(): Boolean {
        var isValid = true
        val s = _state.value

        if (s.name.isBlank()) {
            _state.update { it.copy(nameError = "Tên món không được trống") }
            isValid = false
        }
        val priceVal = s.price.toDoubleOrNull()
        if (priceVal == null || priceVal <= 0) {
            _state.update { it.copy(priceError = "Giá tiền không hợp lệ") }
            isValid = false
        }

        return isValid
    }

    private fun sendEffect(effect: FoodDetailEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}