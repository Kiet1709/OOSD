package com.example.foodelivery.presentation.customer.profile.contract

import com.example.foodelivery.core.base.ViewIntent
import com.example.foodelivery.presentation.customer.food.list.contract.FoodListIntent

sealed class ProfileIntent : ViewIntent {
    object LoadProfile : ProfileIntent()
    object ClickEditProfile : ProfileIntent()
    object ClickAddress : ProfileIntent()
    object ClickOrderHistory : ProfileIntent()
    object ClickPaymentMethods : ProfileIntent()
    object ClickSupport : ProfileIntent()
    object ClickLogout : ProfileIntent()

    object ClickBack : ProfileIntent()

}