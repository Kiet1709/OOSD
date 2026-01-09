package com.example.foodelivery.presentation.customer.food.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.base.BaseViewModel
import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.CartItem
import com.example.foodelivery.domain.repository.ICartRepository
import com.example.foodelivery.domain.repository.IFoodRepository
import com.example.foodelivery.presentation.customer.food.detail.Contract.*
// [ĐÃ XÓA IMPORT FoodUiModel]
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FoodDetailViewModel @Inject constructor(
    private val foodRepository: IFoodRepository,
    private val cartRepository: ICartRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<FoodDetailState, FoodDetailIntent, FoodDetailEffect>(FoodDetailState()) {

    // Lấy ID từ navigation args (nếu cần)
    init {
        savedStateHandle.get<String>("foodId")?.let { loadFoodDetail(it) }
    }

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
                    // [SỬA]: Gán trực tiếp Food, không cần tạo FoodUiModel
                    setState {
                        copy(
                            isLoading = false,
                            food = data,
                            totalPrice = data.price * quantity
                        )
                    }
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
            try {
                android.util.Log.d("FoodDetailVM", "🛒 Adding to cart: ${food.name}")

                cartRepository.addToCart(
                    CartItem(
                        foodId = food.id,
                        name = food.name,
                        price = food.price,
                        quantity = currentState.quantity,
                        imageUrl = food.imageUrl,
                        note = ""
                    )
                )

                android.util.Log.d("FoodDetailVM", "✅ Added to cart successfully")
                setEffect { FoodDetailEffect.ShowToast("Đã thêm vào giỏ hàng!") }
                setEffect { FoodDetailEffect.NavigateToCart }
            } catch (e: Exception) {
                android.util.Log.e("FoodDetailVM", "❌ Error adding to cart: ${e.message}")
                setEffect { FoodDetailEffect.ShowToast("Lỗi thêm vào giỏ: ${e.message}") }
            }
        }
    }
}