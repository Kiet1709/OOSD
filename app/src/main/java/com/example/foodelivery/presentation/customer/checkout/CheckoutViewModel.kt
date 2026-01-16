package com.example.foodelivery.presentation.customer.checkout

import androidx.lifecycle.SavedStateHandle
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
import com.example.foodelivery.ui.theme.navigation.Route
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val placeOrderUseCase: PlaceOrderUseCase,
    private val getCartUseCase: GetCartUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel<CheckoutState, CheckoutIntent, CheckoutEffect>(CheckoutState()) {

    companion object {
        private const val DELIVERY_FEE = 15000.0
    }

    init {
        loadData()
    }

    private fun loadData() {
        val addressFromCart = savedStateHandle.get<String>(Route.Checkout.ARG_ADDRESS)
        if (!addressFromCart.isNullOrBlank()) {
            setState { copy(address = addressFromCart) }
        } else {
            viewModelScope.launch {
                getUserInfoUseCase().collect { user ->
                    val dbAddress = user?.address ?: ""
                    setState {
                        if (address.isBlank()) copy(address = dbAddress) else this
                    }
                }
            }
        }

        viewModelScope.launch {
            getCartUseCase().collect { cartItems ->
                val uiItems = cartItems.map { it.toUiModel() }

                val subTotal = uiItems.sumOf { it.itemTotal }
                val deliveryFee = if (subTotal > 0) DELIVERY_FEE else 0.0
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
    }
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
            if (state.address.isBlank()) {
                setEffect { CheckoutEffect.ShowToast("Vui lòng nhập địa chỉ nhận hàng") }
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
