package com.example.foodelivery.presentation.customer.food.detail

import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.base.BaseViewModel
import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.CartItem
import com.example.foodelivery.domain.repository.ICartRepository
import com.example.foodelivery.domain.repository.IFoodRepository
import com.example.foodelivery.presentation.customer.food.detail.Contract.*
import com.example.foodelivery.presentation.customer.home.contract.FoodUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FoodDetailViewModel @Inject constructor(
    private val foodRepository: IFoodRepository,
    private val cartRepository: ICartRepository
) : BaseViewModel<FoodDetailState, FoodDetailIntent, FoodDetailEffect>(FoodDetailState()) {

    fun setEvent(intent: FoodDetailIntent) = handleIntent(intent)

    override fun handleIntent(intent: FoodDetailIntent) {
        when(intent) {
            is FoodDetailIntent.LoadDetail -> loadFoodDetail(intent.foodId)
            FoodDetailIntent.IncreaseQuantity -> updateQuantity(1)
            FoodDetailIntent.DecreaseQuantity -> updateQuantity(-1)
            FoodDetailIntent.ClickAddToCart -> addToCart()
            FoodDetailIntent.ClickBack -> setEffect { FoodDetailEffect.NavigateBack }
        }
    }

    private fun loadFoodDetail(id: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            when(val result = foodRepository.getFoodDetail(id)) {
                is Resource.Success -> {
                    val data = result.data ?: return@launch
                    val uiFood = FoodUiModel(data.id, data.name, data.imageUrl, data.price, 4.8, "20 min")
                    setState { copy(isLoading = false, food = uiFood, totalPrice = uiFood.price * quantity) }
                }
                is Resource.Error -> setEffect { FoodDetailEffect.ShowToast(result.message ?: "Lỗi") }
                else -> {}
            }
        }
    }

    private fun updateQuantity(delta: Int) {
        val newQty = (currentState.quantity + delta).coerceAtLeast(1)
        setState { copy(quantity = newQty, totalPrice = newQty * (currentState.food?.price ?: 0.0)) }
    }

    private fun addToCart() {
        val food = currentState.food ?: return
        viewModelScope.launch {
            cartRepository.addToCart(CartItem(food.id, food.name, food.price, currentState.quantity, food.imageUrl, ""))
            setEffect { FoodDetailEffect.ShowToast("Đã thêm vào giỏ hàng!") }
            setEffect { FoodDetailEffect.NavigateToCart }
        }
    }
}