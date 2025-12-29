package com.example.foodelivery.presentation.customer.profile.contract

import com.example.foodelivery.core.base.ViewSideEffect

sealed class ProfileEffect : ViewSideEffect {
    object NavigateToEditProfile : ProfileEffect()
    object NavigateToAddressList : ProfileEffect()
    object NavigateToOrderHistory : ProfileEffect()
    object NavigateToLogin : ProfileEffect() // Đăng xuất thành công
    data class ShowToast(val msg: String) : ProfileEffect()
}