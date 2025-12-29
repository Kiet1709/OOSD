package com.example.foodelivery.domain.usecase.cart
import com.example.foodelivery.domain.repository.ICartRepository
import javax.inject.Inject
class RemoveCartItemUseCase @Inject constructor(private val repo: ICartRepository) {
    suspend operator fun invoke(id: String) = repo.removeFromCart(id)
}