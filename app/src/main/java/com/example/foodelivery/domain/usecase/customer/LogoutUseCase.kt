package com.example.foodelivery.domain.usecase.profile

import com.example.foodelivery.domain.repository.IUserRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: IUserRepository
) {
    suspend operator fun invoke() = repository.logout()
}