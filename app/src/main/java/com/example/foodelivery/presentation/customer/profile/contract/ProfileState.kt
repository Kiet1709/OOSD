package com.example.foodelivery.presentation.customer.profile.contract

import com.example.foodelivery.core.base.ViewState

data class UserProfileUiModel(
    val id: String,
    val name: String,
    val phone: String,
    val email: String,
    val avatarUrl: String?,
    val membershipLevel: String = "Thành viên Bạc", // Ví dụ: Bạc, Vàng, Kim Cương
    val loyaltyPoints: Int = 0
)

// --- STATE ---
data class ProfileState(
    val isLoading: Boolean = false,
    val user: UserProfileUiModel? = null,
    val appVersion: String = "1.0.0"
) : ViewState