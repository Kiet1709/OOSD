package com.example.foodelivery.domain.usecase.auth

import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.User
import com.example.foodelivery.domain.repository.IAuthRepository
import com.example.foodelivery.domain.repository.IUserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: IAuthRepository, // Để tạo tài khoản (Email/Pass)
    private val userRepository: IUserRepository  // Để lưu thông tin (Role/Name)
) {
    /**
     * Quy trình Atomic:
     * 1. Validate
     * 2. Tạo Auth
     * 3. Lấy UID -> Tạo User Entity (Role=CUSTOMER) -> Lưu Firestore
     */
    operator fun invoke(name: String, email: String, pass: String, phone: String, role: String = "CUSTOMER"): Flow<Resource<String>> = flow {
        // 1. Validate Input (Fail Fast)
        if (name.isBlank() || email.isBlank() || pass.isBlank() || phone.isBlank()) {
            emit(Resource.Error("Vui lòng nhập đầy đủ thông tin"))
            return@flow
        }

        emit(Resource.Loading())

        try {
            // 2. Gọi Auth Repo
            val authResult = authRepository.register(name, email, pass, phone)

            if (authResult is Resource.Success) {
                // Lấy User từ kết quả trả về của Auth
                val createdUser = authResult.data
                val userId = createdUser?.id

                if (!userId.isNullOrBlank()) {
                    // 3. Data Layer: Tạo User Entity đầy đủ với ROLE
                    val newUser = User(
                        id = userId,
                        name = name,
                        email = email,
                        phoneNumber = phone,
                        avatarUrl = "",
                        address = "",
                        role = role // <--- QUAN TRỌNG: Lưu role tại đây
                    )

                    // 4. Lưu xuống Firestore
                    val saveResult = userRepository.saveUserInfo(newUser)

                    if (saveResult is Resource.Success) {
                        emit(Resource.Success("Đăng ký thành công!"))
                    } else {
                        emit(Resource.Error("Tạo tài khoản được nhưng lỗi lưu dữ liệu."))
                    }
                } else {
                    emit(Resource.Error("Lỗi hệ thống: Không lấy được ID người dùng."))
                }
            } else {
                // Lỗi từ Auth (Email tồn tại, pass yếu...)
                emit(Resource.Error(authResult.message ?: "Đăng ký thất bại"))
            }

        } catch (e: Exception) {
            emit(Resource.Error("Lỗi ngoại lệ: ${e.message}"))
        }
    }
}