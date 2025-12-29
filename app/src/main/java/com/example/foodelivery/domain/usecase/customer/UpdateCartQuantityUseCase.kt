package com.example.foodelivery.domain.usecase.cart
import com.example.foodelivery.domain.repository.ICartRepository
import javax.inject.Inject
class UpdateCartQuantityUseCase @Inject constructor(private val repo: ICartRepository) {
    suspend operator fun invoke(id: String, qty: Int) = repo.updateQuantity(id, qty)
}