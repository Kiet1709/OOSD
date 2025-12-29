package com.example.foodelivery.domain.usecase.auth

import com.example.foodelivery.domain.model.User
import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.repository.IAuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: IAuthRepository
) {
    suspend operator fun invoke(email: String, pass: String): Resource<User> {
        // 1. Validate cơ bản (Business Logic)
        if (email.isBlank() || pass.isBlank()) {
            return Resource.Error("Vui lòng nhập đầy đủ thông tin")
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Resource.Error("Email không hợp lệ")
        }

        // 2. Gọi Repository
        return authRepository.login(email, pass)
    }
}