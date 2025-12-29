package com.example.foodelivery.domain.usecase.auth

import com.example.foodelivery.domain.repository.IAuthRepository
import javax.inject.Inject

class CheckUserRoleUseCase @Inject constructor(
    private val repository: IAuthRepository
) {
    suspend operator fun invoke(): String {
        val user = repository.getCurrentUser()
        return user?.role ?: "GUEST" // Mặc định là Khách nếu chưa đăng nhập
    }
}