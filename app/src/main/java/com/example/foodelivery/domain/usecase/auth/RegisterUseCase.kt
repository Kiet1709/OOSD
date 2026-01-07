package com.example.foodelivery.domain.usecase.auth

import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.User
import com.example.foodelivery.domain.repository.IAuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: IAuthRepository
) {
    suspend operator fun invoke(
        name: String,
        email: String,
        pass: String,
        phone: String,
        role: String
    ): Resource<User> {
        // 1. Validate cơ bản
        if (name.isBlank() || email.isBlank() || pass.isBlank() || phone.isBlank()) {
            return Resource.Error("Vui lòng điền đầy đủ thông tin")
        }
        if (pass.length < 6) {
            return Resource.Error("Mật khẩu phải từ 6 ký tự trở lên")
        }

        // 2. Gọi Repository để đăng ký (Lưu ý: Repository cần update để nhận thêm role)
        // Vì interface cũ chưa hỗ trợ role, ta sẽ update interface hoặc xử lý trong impl.
        // Ở đây giả định repository.register đã được update hoặc ta sẽ update nó ngay sau đây.
        return repository.register(name, email, pass, phone, role) 
    }
}