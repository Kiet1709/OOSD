package com.example.foodelivery.domain.usecase.customer

import com.example.foodelivery.domain.model.CartItem
import com.example.foodelivery.domain.repository.ICartRepository
import javax.inject.Inject

class AddToCartUseCase @Inject constructor(
    private val repository: ICartRepository
) {
    suspend operator fun invoke(item: CartItem) {
        if (item.quantity > 0) {
            repository.addToCart(item)
        }
    }
}