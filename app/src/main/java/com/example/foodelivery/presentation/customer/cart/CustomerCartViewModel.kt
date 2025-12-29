package com.example.foodelivery.presentation.customer.cart

import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.base.BaseViewModel
import com.example.foodelivery.domain.model.CartItem
import com.example.foodelivery.domain.usecase.cart.GetCartUseCase
import com.example.foodelivery.domain.usecase.cart.RemoveCartItemUseCase
import com.example.foodelivery.domain.usecase.cart.UpdateCartQuantityUseCase
import com.example.foodelivery.presentation.customer.cart.contract.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerCartViewModel @Inject constructor(
    private val getCartUseCase: GetCartUseCase,
    private val updateCartQuantityUseCase: UpdateCartQuantityUseCase,
    private val removeCartItemUseCase: RemoveCartItemUseCase
) : BaseViewModel<CartState, CartIntent, CartEffect>(CartState()) {

    // [SENIOR]: Quản lý Job để tránh leak khi gọi load nhiều lần
    private var cartJob: Job? = null

    init {
        subscribeToCart()
    }

    private fun subscribeToCart() {
        // Hủy job cũ nếu đang chạy để tránh tạo nhiều luồng collect cùng lúc
        cartJob?.cancel()
        cartJob = viewModelScope.launch {
            setState { copy(isLoading = true) }

            getCartUseCase().collectLatest { domainItems ->
                // 1. Mapping: Domain -> UI Model
                val uiItems = domainItems.map { it.toUiModel() }

                // 2. Tính toán tiền nong (Business Logic)
                val subTotal = uiItems.sumOf { it.itemTotal }
                val delivery = 15000.0
                val discount = 0.0
                val final = if (subTotal > 0) subTotal + delivery - discount else 0.0

                // 3. Update State
                setState {
                    copy(
                        isLoading = false,
                        items = uiItems,
                        subTotal = subTotal,
                        deliveryFee = delivery,
                        discountAmount = discount,
                        finalTotal = final
                    )
                }
            }
        }
    }

    // Public fun để UI gọi
    fun setEvent(intent: CartIntent) = handleIntent(intent)

    override fun handleIntent(intent: CartIntent) {
        when (intent) {
            // [FIX LỖI EXHAUSTIVE]: Thêm nhánh này vào
            CartIntent.LoadCart -> subscribeToCart()

            is CartIntent.IncreaseQty -> updateQuantity(intent.foodId, 1)
            is CartIntent.DecreaseQty -> updateQuantity(intent.foodId, -1)
            is CartIntent.RemoveItem -> removeItem(intent.foodId)

            CartIntent.ClickCheckout -> {
                if (!currentState.isCartEmpty) {
                    setEffect { CartEffect.NavigateToCheckout }
                } else {
                    setEffect { CartEffect.ShowToast("Giỏ hàng đang trống!") }
                }
            }
            CartIntent.ClickGoHome -> setEffect { CartEffect.NavigateToHome }
        }
    }

    private fun updateQuantity(foodId: String, delta: Int) {
        viewModelScope.launch {
            val currentItem = currentState.items.find { it.foodId == foodId }
            if (currentItem != null) {
                val newQty = currentItem.quantity + delta
                updateCartQuantityUseCase(foodId, newQty)
            }
        }
    }

    private fun removeItem(foodId: String) {
        viewModelScope.launch {
            removeCartItemUseCase(foodId)
        }
    }
}

// Extension Mapper
private fun CartItem.toUiModel(): CartItemUiModel {
    return CartItemUiModel(
        foodId = this.foodId,
        name = this.name,
        imageUrl = this.imageUrl,
        price = this.price,
        quantity = this.quantity,
        note = this.note
    )
}