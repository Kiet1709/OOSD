package com.example.foodelivery.presentation.customer.food.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.base.BaseViewModel
import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.CartItem
import com.example.foodelivery.domain.repository.ICartRepository
import com.example.foodelivery.domain.repository.IFoodRepository
import com.example.foodelivery.domain.repository.IUserRepository
import com.example.foodelivery.presentation.customer.food.detail.contract.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FoodDetailViewModel @Inject constructor(
    private val foodRepo: IFoodRepository,
    private val userRepo: IUserRepository,
    private val cartRepo: ICartRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<FoodDetailState, FoodDetailIntent, FoodDetailEffect>(FoodDetailState()) {

    init {
        savedStateHandle.get<String>("foodId")?.let {
            handleIntent(FoodDetailIntent.LoadFoodDetail(it))
        }
    }

    fun setEvent(intent: FoodDetailIntent) {
        handleIntent(intent)
    }

    override fun handleIntent(intent: FoodDetailIntent) {
        when (intent) {
            is FoodDetailIntent.LoadFoodDetail -> loadData(intent.foodId)
            is FoodDetailIntent.IncreaseQuantity -> {
                val newQuantity = uiState.value.quantity + 1
                setState { copy(quantity = newQuantity) }
            }
            is FoodDetailIntent.DecreaseQuantity -> {
                val newQuantity = if (uiState.value.quantity > 1) uiState.value.quantity - 1 else 1
                setState { copy(quantity = newQuantity) }
            }
            is FoodDetailIntent.AddToCart -> addToCart()
        }
    }

    private fun loadData(foodId: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val foodResult = foodRepo.getFoodDetail(foodId)

            if (foodResult is Resource.Success) {
                val food = foodResult.data!!
                val restaurantResult = userRepo.getUserById(food.restaurantId)
                setState {
                    copy(
                        isLoading = false,
                        food = food,
                        restaurant = restaurantResult
                    )
                }
            } else {
                setState { copy(isLoading = false) }
                setEffect { FoodDetailEffect.ShowToast("Không thể tải thông tin món ăn") }
            }
        }
    }

    private fun addToCart() {
        viewModelScope.launch {
            val state = uiState.value
            val food = state.food
            if (food != null) {
                // Corrected: Create a CartItem object and pass it to the repository
                val cartItem = CartItem(
                    foodId = food.id,
                    name = food.name,
                    price = food.price,
                    imageUrl = food.imageUrl,
                    quantity = state.quantity,
                    note = "", // Assuming note can be empty for now
                    restaurantId = food.restaurantId // Add this line
                )
                cartRepo.addToCart(cartItem)
                setEffect { FoodDetailEffect.ShowToast("Đã thêm vào giỏ hàng") }
                setEffect { FoodDetailEffect.NavigateBack }
            }
        }
    }
}