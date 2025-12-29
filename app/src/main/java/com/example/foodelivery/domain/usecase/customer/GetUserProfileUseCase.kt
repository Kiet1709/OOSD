package com.example.foodelivery.domain.usecase.profile

import com.example.foodelivery.domain.model.User
import com.example.foodelivery.domain.repository.IUserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val repository: IUserRepository
) {
    operator fun invoke(): Flow<User?> = repository.getUser()
}