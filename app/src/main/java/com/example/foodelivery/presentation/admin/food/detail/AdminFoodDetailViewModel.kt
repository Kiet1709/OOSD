package com.example.foodelivery.presentation.admin.food.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.base.BaseViewModel
import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.Food
import com.example.foodelivery.domain.model.Category
import com.example.foodelivery.domain.repository.ICategoryRepository
import com.example.foodelivery.domain.usecase.admin.UploadImageUseCase
import com.example.foodelivery.domain.usecase.food.AddFoodUseCase
import com.example.foodelivery.domain.usecase.food.GetFoodDetailUseCase
import com.example.foodelivery.domain.usecase.food.UpdateFoodUseCase
import com.example.foodelivery.presentation.admin.food.detail.contract.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminFoodDetailViewModel @Inject constructor(
    private val getFoodDetailUseCase: GetFoodDetailUseCase,
    private val addFoodUseCase: AddFoodUseCase,
    private val updateFoodUseCase: UpdateFoodUseCase,
    private val uploadImageUseCase: UploadImageUseCase,
    private val categoryRepository: ICategoryRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<FoodDetailState, FoodDetailIntent, FoodDetailEffect>(FoodDetailState()) {

    init {
        // 1. Load Categories
        loadCategories()

        // 2. Load Food Detail
        val foodId = savedStateHandle.get<String>("foodId")
        if (foodId != null && foodId != "new") {
            sendIntent(FoodDetailIntent.LoadFoodDetail(foodId))
        }
    }

    override fun handleIntent(intent: FoodDetailIntent) {
        when (intent) {
            is FoodDetailIntent.LoadFoodDetail -> loadDetail(intent.id)
            is FoodDetailIntent.NameChanged -> setState { copy(name = intent.name, nameError = null) }
            is FoodDetailIntent.PriceChanged -> setState { copy(price = intent.price, priceError = null) }
            is FoodDetailIntent.DescriptionChanged -> setState { copy(description = intent.desc) }
            is FoodDetailIntent.ImageSelected -> setState { copy(selectedImageUri = intent.uri) }
            
            is FoodDetailIntent.ImageUrlChanged -> setState { copy(serverImageUrl = intent.url) }
            is FoodDetailIntent.CategorySelected -> setState { copy(selectedCategoryId = intent.categoryId) }
            
            FoodDetailIntent.ClickSubmit -> submit()
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.getCategories().collectLatest { result ->
                when (result) {
                    is Resource.Success -> {
                        val list = result.data ?: emptyList()
                        setState { copy(categories = list) }
                        
                        // Nếu là Add Mode và chưa chọn danh mục, chọn cái đầu tiên
                        if (!currentState.isEditMode && list.isNotEmpty() && currentState.selectedCategoryId.isEmpty()) {
                            setState { copy(selectedCategoryId = list.first().id) }
                        }
                    }
                    is Resource.Error -> {
                        setEffect { FoodDetailEffect.ShowToast("Lỗi tải danh mục: ${result.message}") }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun loadDetail(id: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            when (val result = getFoodDetailUseCase(id)) {
                is Resource.Success -> {
                    val food = result.data
                    if (food != null) {
                        setState {
                            copy(
                                isLoading = false,
                                isEditMode = true,
                                existingFoodId = food.id,
                                name = food.name,
                                price = food.price.toLong().toString(),
                                description = food.description,
                                serverImageUrl = food.imageUrl,
                                selectedCategoryId = food.categoryId
                            )
                        }
                    }
                }
                is Resource.Error -> {
                    setState { copy(isLoading = false) }
                    setEffect { FoodDetailEffect.ShowToast("Lỗi tải thông tin") }
                    setEffect { FoodDetailEffect.NavigateBack }
                }
                else -> {}
            }
        }
    }

    private fun submit() {
        if (!validate()) return

        val s = currentState
        viewModelScope.launch {
            setState { copy(isLoading = true) }

            var finalUrl = s.serverImageUrl ?: ""
            if (s.selectedImageUri != null) {
                when (val uploadRes = uploadImageUseCase(s.selectedImageUri)) {
                    is Resource.Success -> finalUrl = uploadRes.data ?: ""
                    is Resource.Error -> {
                        setState { copy(isLoading = false) }
                        setEffect { FoodDetailEffect.ShowToast("Lỗi upload ảnh: ${uploadRes.message}") }
                        return@launch
                    }
                    else -> {}
                }
            }

            val food = Food(
                id = s.existingFoodId ?: "",
                name = s.name,
                price = s.price.toDouble(),
                description = s.description,
                imageUrl = finalUrl,
                categoryId = s.selectedCategoryId, 
                rating = 5.0,
                isAvailable = true
            )

            val result = if (s.isEditMode) updateFoodUseCase(food) else addFoodUseCase(food)

            setState { copy(isLoading = false) }

            when (result) {
                is Resource.Success -> {
                    setEffect { FoodDetailEffect.ShowToast(if (s.isEditMode) "Cập nhật thành công" else "Thêm mới thành công") }
                    setEffect { FoodDetailEffect.NavigateBack }
                }
                is Resource.Error -> setEffect { FoodDetailEffect.ShowToast(result.message ?: "Lỗi lưu dữ liệu") }
                else -> {}
            }
        }
    }

    private fun validate(): Boolean {
        var isValid = true
        val s = currentState

        if (s.name.isBlank()) {
            setState { copy(nameError = "Tên món không được trống") }
            isValid = false
        }
        val priceVal = s.price.toDoubleOrNull()
        if (priceVal == null || priceVal <= 0) {
            setState { copy(priceError = "Giá tiền không hợp lệ") }
            isValid = false
        }
        if (s.selectedCategoryId.isEmpty()) {
             setEffect { FoodDetailEffect.ShowToast("Vui lòng chọn danh mục") }
             isValid = false
        }

        return isValid
    }
}