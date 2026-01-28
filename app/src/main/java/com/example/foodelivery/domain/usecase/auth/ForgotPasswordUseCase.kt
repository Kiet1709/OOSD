package com.example.foodelivery.domain.usecase.auth

import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.repository.IAuthRepository
import javax.inject.Inject

class ForgotPasswordUseCase @Inject constructor(
    private val repository: IAuthRepository
) {
    suspend operator fun invoke(email: String): Resource<Unit> {
        if (email.isBlank()) {
            return Resource.Error("Vui lòng nhập Email để đặt lại mật khẩu")
        }
        // Logic nghiệp vụ: Kiểm tra format Email đơn giản
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Resource.Error("Email không hợp lệ")
        }

        return repository.sendPasswordResetEmail(email)
    }
}