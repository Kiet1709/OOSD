package com.example.foodelivery.presentation.customer.checkout

import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.base.BaseViewModel
import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.CartItem
import com.example.foodelivery.domain.usecase.cart.GetCartUseCase
import com.example.foodelivery.domain.usecase.customer.GetUserInfoUseCase
import com.example.foodelivery.domain.usecase.customer.PlaceOrderUseCase
import com.example.foodelivery.presentation.customer.cart.contract.CartItemUiModel
import com.example.foodelivery.presentation.customer.checkout.contract.CheckoutEffect
import com.example.foodelivery.presentation.customer.checkout.contract.CheckoutIntent
import com.example.foodelivery.presentation.customer.checkout.contract.CheckoutState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val placeOrderUseCase: PlaceOrderUseCase,
    getCartUseCase: GetCartUseCase, // Lấy dữ liệu từ đây
    getUserInfoUseCase: GetUserInfoUseCase // Và từ đây
) : BaseViewModel<CheckoutState, CheckoutIntent, CheckoutEffect>(CheckoutState()) {

    companion object {
        private const val DELIVERY_FEE = 15000.0
    }

    // Tự lấy dữ liệu, không cần truyền qua navigation nữa
    override val uiState = combine(
        getCartUseCase(),
        getUserInfoUseCase()
    ) { cartItems, user ->
        val uiItems = cartItems.map { it.toUiModel() }
        val subTotal = uiItems.sumOf { it.itemTotal }
        val deliveryFee = if (subTotal > 0) DELIVERY_FEE else 0.0
        val finalTotal = subTotal + deliveryFee

        CheckoutState(
            items = uiItems,
            address = user?.address ?: "",
            subTotal = subTotal,
            deliveryFee = deliveryFee,
            finalTotal = finalTotal
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CheckoutState(isLoading = true))

    fun setEvent(intent: CheckoutIntent) = handleIntent(intent)

    override fun handleIntent(intent: CheckoutIntent) {
        when (intent) {
            CheckoutIntent.ConfirmOrder -> confirmOrder()
        }
    }

    private fun confirmOrder() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val state = uiState.value
            if (state.items.isEmpty()) {
                setEffect { CheckoutEffect.ShowToast("Giỏ hàng trống") }
                setState { copy(isLoading = false) }
                return@launch
            }

            val restaurantId = state.items.first().restaurantId
            val result = placeOrderUseCase(
                shippingAddress = state.address,
                items = state.items.map { it.toDomain() },
                totalPrice = state.finalTotal,
                restaurantId = restaurantId
            )

            when (result) {
                is Resource.Success -> {
                    setEffect { CheckoutEffect.NavigateToHome }
                }
                is Resource.Error -> {
                    setEffect { CheckoutEffect.ShowToast(result.message ?: "Lỗi đặt hàng") }
                }
                else -> {}
            }
            setState { copy(isLoading = false) }
        }
    }
}

// Các hàm mapper tiện ích
private fun CartItem.toUiModel() = CartItemUiModel(
    foodId = foodId,
    name = name,
    imageUrl = imageUrl,
    price = price,
    quantity = quantity,
    note = note,
    restaurantId = restaurantId
)

private fun CartItemUiModel.toDomain() = CartItem(
    foodId = foodId,
    name = name,
    price = price,
    quantity = quantity,
    imageUrl = imageUrl,
    note = note ?: "",
    restaurantId = restaurantId
)
