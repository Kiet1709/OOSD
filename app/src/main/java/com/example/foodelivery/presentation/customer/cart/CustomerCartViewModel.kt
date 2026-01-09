// ============================================
// Customer Cart ViewModel
// ============================================

package com.example.foodelivery.presentation.customer.cart

import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.base.BaseViewModel
import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.CartItem
import com.example.foodelivery.domain.model.Order
import com.example.foodelivery.domain.model.OrderStatus
import com.example.foodelivery.domain.usecase.cart.GetCartUseCase
import com.example.foodelivery.domain.usecase.cart.RemoveCartItemUseCase
import com.example.foodelivery.domain.usecase.cart.UpdateCartQuantityUseCase
import com.example.foodelivery.domain.usecase.customer.PlaceOrderUseCase
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
    private val removeCartItemUseCase: RemoveCartItemUseCase,
    private val placeOrderUseCase: PlaceOrderUseCase
) : BaseViewModel<CartState, CartIntent, CartEffect>(CartState()) {

    companion object {
        private const val DELIVERY_FEE = 15000.0
    }

    private var cartSubscriptionJob: Job? = null

    init {
        subscribeToCart()
    }

    fun setEvent(intent: CartIntent) = handleIntent(intent)

    override fun handleIntent(intent: CartIntent) {
        when (intent) {
            CartIntent.LoadCart -> subscribeToCart()

            is CartIntent.IncreaseQty -> {
                updateQuantity(intent.foodId, 1)
            }

            is CartIntent.DecreaseQty -> {
                updateQuantity(intent.foodId, -1)
            }

            is CartIntent.RemoveItem -> {
                removeItem(intent.foodId)
            }

            CartIntent.ClickGoHome -> {
                setEffect { CartEffect.NavigateToHome }
            }

            is CartIntent.UpdateAddress -> {
                setState { copy(address = intent.address) }
            }

            CartIntent.ClickCheckout -> {
                processCheckout()
            }
        }
    }

    private fun subscribeToCart() {
        cartSubscriptionJob?.cancel()
        cartSubscriptionJob = viewModelScope.launch {
            try {
                setState { copy(isLoading = true) }

                getCartUseCase().collectLatest { domainItems ->
                    try {
                        val uiItems = domainItems.map { it.toUiModel() }
                        val subTotal = uiItems.sumOf { it.itemTotal }
                        val deliveryFee = if (subTotal > 0) DELIVERY_FEE else 0.0
                        val finalTotal = subTotal + deliveryFee

                        setState {
                            copy(
                                isLoading = false,
                                items = uiItems,
                                subTotal = subTotal,
                                deliveryFee = deliveryFee,
                                finalTotal = finalTotal
                            )
                        }
                    } catch (e: Exception) {
                        setState { copy(isLoading = false) }
                        setEffect { CartEffect.ShowToast("Lỗi xử lý giỏ hàng") }
                    }
                }
            } catch (e: Exception) {
                setState { copy(isLoading = false) }
                setEffect { CartEffect.ShowToast("Lỗi tải giỏ hàng") }
            }
        }
    }

    private fun processCheckout() {
        val state = currentState

        when {
            state.isCartEmpty -> {
                setEffect { CartEffect.ShowToast("Giỏ hàng đang trống!") }
                return
            }

            state.address.isBlank() -> {
                setEffect { CartEffect.ShowToast("Vui lòng nhập địa chỉ nhận hàng!") }
                return
            }

            state.finalTotal <= 0 -> {
                setEffect { CartEffect.ShowToast("Giá tiền không hợp lệ!") }
                return
            }
        }

        viewModelScope.launch {
            try {
                setState { copy(isLoading = true) }

                val domainItems = state.items.map { uiItem ->
                    CartItem(
                        foodId = uiItem.foodId,
                        name = uiItem.name,
                        imageUrl = uiItem.imageUrl,
                        price = uiItem.price,
                        quantity = uiItem.quantity,
                        note = uiItem.note ?: ""
                    )
                }

                val result = placeOrderUseCase(
                    shippingAddress = state.address,
                    items = domainItems,
                    totalPrice = state.finalTotal
                )

                setState { copy(isLoading = false) }

                when (result) {
                    is Resource.Success -> {
                        val orderId = result.data ?: ""
                        setEffect { CartEffect.ShowToast("Đặt hàng thành công!") }
                        setEffect { CartEffect.NavigateToTracking(orderId) }
                    }

                    is Resource.Error -> {
                        val msg = result.message ?: "Lỗi đặt hàng"
                        setEffect { CartEffect.ShowToast(msg) }
                    }

                    else -> {
                        // Handle other states if needed
                    }
                }

            } catch (e: Exception) {
                setState { copy(isLoading = false) }
                setEffect { CartEffect.ShowToast("Lỗi: ${e.message}") }
            }
        }
    }

    private fun updateQuantity(foodId: String, delta: Int) {
        viewModelScope.launch {
            try {
                val currentItem = currentState.items.find { it.foodId == foodId }

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

    override fun onCleared() {
        cartSubscriptionJob?.cancel()
        super.onCleared()
    }
}

private fun CartItem.toUiModel() = CartItemUiModel(
    foodId = foodId,
    name = name,
    imageUrl = imageUrl,
    price = price,
    quantity = quantity,
    note = note
)