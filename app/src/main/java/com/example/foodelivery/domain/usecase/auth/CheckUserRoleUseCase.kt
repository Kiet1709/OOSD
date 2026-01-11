package com.example.foodelivery.domain.usecase.auth

import com.example.foodelivery.domain.model.User
import com.example.foodelivery.domain.repository.IAuthRepository
import javax.inject.Inject

class CheckUserRoleUseCase @Inject constructor(
    private val repository: IAuthRepository
) {
    suspend operator fun invoke(): User? {
        return repository.getCurrentUser()
    }
}