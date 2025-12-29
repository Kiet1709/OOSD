package com.example.foodelivery.domain.usecase.profile

import com.example.foodelivery.domain.model.User
import com.example.foodelivery.domain.repository.IUserRepository
import javax.inject.Inject

class UpdateUserProfileUseCase @Inject constructor(
    private val repository: IUserRepository
) {
    suspend operator fun invoke(user: User): Result<Unit> {
        // Có thể thêm Validate dữ liệu ở đây nếu cần (VD: tên không được rỗng)
        if (user.name.isBlank()) {
            return Result.failure(Exception("Tên không được để trống"))
        }
        return repository.updateUser(user)
    }
}