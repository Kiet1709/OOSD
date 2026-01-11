package com.example.foodelivery.presentation.restaurant.food.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.base.BaseViewModel
import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.Food
import com.example.foodelivery.domain.repository.ICategoryRepository
import com.example.foodelivery.domain.repository.IFoodRepository
import com.example.foodelivery.presentation.restaurant.food.detail.contract.*
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantAddEditFoodViewModel @Inject constructor(
    private val foodRepository: IFoodRepository,
    private val categoryRepository: ICategoryRepository,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel<RestaurantAddEditFoodState, RestaurantAddEditFoodIntent, RestaurantAddEditFoodEffect>(RestaurantAddEditFoodState()) {

    private var originalFood: Food? = null

    init {
        loadCategories()
        savedStateHandle.get<String>("foodId")?.let {
            if (it != "new") {
                handleIntent(RestaurantAddEditFoodIntent.LoadFoodDetails(it))
            }
        }
    }

    fun setEvent(intent: RestaurantAddEditFoodIntent) {
        handleIntent(intent)
    }

    override fun handleIntent(intent: RestaurantAddEditFoodIntent) {
        when (intent) {
            is RestaurantAddEditFoodIntent.LoadFoodDetails -> loadFoodDetails(intent.foodId)
            is RestaurantAddEditFoodIntent.NameChanged -> setState { copy(name = intent.name, nameError = null) }
            is RestaurantAddEditFoodIntent.DescriptionChanged -> setState { copy(description = intent.description) }
            is RestaurantAddEditFoodIntent.PriceChanged -> setState { copy(price = intent.price, priceError = null) }
            is RestaurantAddEditFoodIntent.ImageUrlChanged -> setState { copy(imageUrl = intent.imageUrl) }
            is RestaurantAddEditFoodIntent.CategorySelected -> setState { copy(categoryId = intent.categoryId) }
            is RestaurantAddEditFoodIntent.Submit -> submit()
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.getCategories().collect { result ->
                if (result is Resource.Success) {
                    setState { copy(categories = result.data ?: emptyList()) }
                }
            }
        }
    }

    private fun loadFoodDetails(foodId: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            when (val result = foodRepository.getFoodDetail(foodId)) {
                is Resource.Success -> {
                    val food = result.data!!
                    originalFood = food // Keep the original food to preserve rating
                    setState {
                        copy(
                            isLoading = false,
                            isEditMode = true,
                            foodId = food.id,
                            name = food.name,
                            description = food.description,
                            price = food.price.toString(),
                            imageUrl = food.imageUrl,
                            categoryId = food.categoryId
                        )
                    }
                }
                is Resource.Error -> {
                    setState { copy(isLoading = false) }
                    setEffect { RestaurantAddEditFoodEffect.ShowToast(result.message ?: "Lỗi tải thông tin món ăn") }
                }
                else -> {}
            }
        }
    }

    private fun submit() {
        val state = uiState.value
        if (state.name.isBlank()) {
            setState { copy(nameError = "Tên món ăn không được để trống") }
            return
        }
        val price = state.price.toDoubleOrNull()
        if (price == null || price <= 0) {
            setState { copy(priceError = "Giá không hợp lệ") }
            return
        }

        viewModelScope.launch {
            setState { copy(isLoading = true) }
            // Correct and most reliable way to get current user's ID
            val restaurantId = FirebaseAuth.getInstance().currentUser?.uid

            if (restaurantId.isNullOrBlank()) {
                setEffect { RestaurantAddEditFoodEffect.ShowToast("Lỗi: Không thể xác thực nhà hàng.") }
                setState { copy(isLoading = false) }
                return@launch
            }

            val food = Food(
                id = state.foodId ?: "",
                name = state.name,
                description = state.description,
                price = price,
                imageUrl = state.imageUrl,
                categoryId = state.categoryId,
                restaurantId = restaurantId,
                rating = originalFood?.rating ?: 0.0, // Preserve rating on edit, default to 0 for new
                isAvailable = true
            )

            val result = if (state.isEditMode) {
                foodRepository.updateFood(food)
            } else {
                foodRepository.addFood(food)
            }

            setState { copy(isLoading = false) }
            when (result) {
                is Resource.Success -> {
                    val message = if (state.isEditMode) "Cập nhật thành công" else "Thêm món ăn thành công"
                    setEffect { RestaurantAddEditFoodEffect.ShowToast(message) }
                    setEffect { RestaurantAddEditFoodEffect.NavigateBack }
                }
                is Resource.Error -> {
                    setEffect { RestaurantAddEditFoodEffect.ShowToast(result.message ?: "Đã xảy ra lỗi") }
                }
                else -> {}
            }
        }
    }
}