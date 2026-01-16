package com.example.foodelivery.presentation.customer.cart

import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.base.BaseViewModel
import com.example.foodelivery.core.common.Constants
import com.example.foodelivery.domain.model.CartItem
import com.example.foodelivery.domain.usecase.cart.GetCartUseCase
import com.example.foodelivery.domain.usecase.cart.RemoveCartItemUseCase
import com.example.foodelivery.domain.usecase.cart.UpdateCartQuantityUseCase
import com.example.foodelivery.domain.usecase.customer.GetUserInfoUseCase
import com.example.foodelivery.presentation.customer.cart.contract.*
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerCartViewModel @Inject constructor(
    private val getCartUseCase: GetCartUseCase,
    private val updateCartQuantityUseCase: UpdateCartQuantityUseCase,
    private val removeCartItemUseCase: RemoveCartItemUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase
) : BaseViewModel<CartState, CartIntent, CartEffect>(CartState()) {

    init {
        getData()
    }

    private fun getData() {
        viewModelScope.launch {
            getCartUseCase().collect { cartItems ->
                val uiItems = cartItems.map { it.toUiModel() }
                val subTotal = uiItems.sumOf { it.itemTotal }
                val deliveryFee = if (subTotal > 0) Constants.DELIVERY_FEE else 0.0
                val finalTotal = subTotal + deliveryFee

                setState {
                    copy(
                        items = uiItems,
                        subTotal = subTotal,
                        deliveryFee = deliveryFee,
                        finalTotal = finalTotal,
                        isLoading = false
                    )
                }
            }
        }

        viewModelScope.launch {
            getUserInfoUseCase().collect { user ->
                val userAddress = user?.address ?: ""
                setState {
                    if (address.isBlank()) {
                        copy(address = userAddress)
                    } else {
                        this
                    }
                }
            }
        }
    }

    fun setEvent(intent: CartIntent) = handleIntent(intent)

    override fun handleIntent(intent: CartIntent) {
        when (intent) {
            is CartIntent.IncreaseQty -> updateQuantity(intent.foodId, 1)
            is CartIntent.DecreaseQty -> updateQuantity(intent.foodId, -1)
            is CartIntent.RemoveItem -> removeItem(intent.foodId)
            CartIntent.ClickGoHome -> setEffect { CartEffect.NavigateToHome }
            is CartIntent.UpdateAddress -> setState { copy(address = intent.address) }
            CartIntent.ClickCheckout -> navigateToCheckout()
            else -> {}
        }
    }

    private fun navigateToCheckout() {
        val state = uiState.value
        if (state.items.isEmpty()) {
            setEffect { CartEffect.ShowToast("Giỏ hàng đang trống!") }
            return
        }
        if (state.address.isBlank()) {
            setEffect { CartEffect.ShowToast("Vui lòng nhập địa chỉ nhận hàng!") }
            return
        }
        setEffect { CartEffect.NavigateToCheckout(state.address) }
    }

    private fun updateQuantity(foodId: String, delta: Int) {
        viewModelScope.launch {
            try {
                val currentItem = uiState.value.items.find { it.foodId == foodId }
                if (currentItem == null) {
                    setEffect { CartEffect.ShowToast("Không tìm thấy sản phẩm") }
                    return@launch
                }
                val newQty = currentItem.quantity + delta
                if (newQty <= 0) {
                    removeItem(foodId)
                    return@launch
                }
                if (newQty > 50) {
                    setEffect { CartEffect.ShowToast("Không thể thêm quá 50 sản phẩm") }
                    return@launch
                }
                updateCartQuantityUseCase(foodId, newQty)
            } catch (e: Exception) {
                setEffect { CartEffect.ShowToast("Lỗi cập nhật số lượng") }
            }
        }
    }

    private fun removeItem(foodId: String) {
        viewModelScope.launch {
            try {
                removeCartItemUseCase(foodId)
            } catch (e: Exception) {
                setEffect { CartEffect.ShowToast("Lỗi xóa sản phẩm") }
            }
        }
    }
}

private fun CartItem.toUiModel() = CartItemUiModel(
    foodId = foodId,
    name = name,
    imageUrl = imageUrl,
    price = price,
    quantity = quantity,
    note = note,
    restaurantId = restaurantId
)
