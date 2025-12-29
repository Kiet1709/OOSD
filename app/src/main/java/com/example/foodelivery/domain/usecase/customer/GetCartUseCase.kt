package com.example.foodelivery.domain.usecase.cart
import com.example.foodelivery.domain.repository.ICartRepository
import javax.inject.Inject

class GetCartUseCase @Inject constructor(private val repo: ICartRepository) {
    operator fun invoke() = repo.getCart()
}