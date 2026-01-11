package com.example.foodelivery.domain.usecase.customer

import com.example.foodelivery.domain.model.User
import com.example.foodelivery.domain.repository.IUserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(
    private val userRepository: IUserRepository
) {
    operator fun invoke(): Flow<User?> = userRepository.getUser()
}