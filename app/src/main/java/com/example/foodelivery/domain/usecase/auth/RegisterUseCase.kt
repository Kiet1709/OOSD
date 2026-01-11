package com.example.foodelivery.domain.usecase.auth

import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.repository.IAuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: IAuthRepository
) {
    suspend operator fun invoke(
        name: String,
        email: String,
        pass: String,
        phone: String,
        role: String // Added role
    ): Flow<Resource<String>> = flow {
        emit(Resource.Loading())

        if (name.isBlank() || email.isBlank() || pass.isBlank() || phone.isBlank()) {
            emit(Resource.Error("Vui lòng điền đầy đủ thông tin."))
            return@flow
        }
        if (pass.length < 6) {
            emit(Resource.Error("Mật khẩu phải có ít nhất 6 ký tự."))
            return@flow
        }

        when(val result = authRepository.register(name, email, pass, phone, role)) {
            is Resource.Success -> emit(Resource.Success("Đăng ký thành công!"))
            is Resource.Error -> emit(Resource.Error(result.message ?: "Đã xảy ra lỗi không xác định."))
            else -> {}
        }
    }
}