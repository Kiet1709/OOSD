package com.example.foodelivery.presentation.customer.home

import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.base.BaseViewModel
import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.repository.ICategoryRepository
import com.example.foodelivery.domain.repository.IFoodRepository
import com.example.foodelivery.domain.repository.IUserRepository
import com.example.foodelivery.presentation.customer.home.contract.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerHomeViewModel @Inject constructor(
    private val userRepository: IUserRepository,
    private val foodRepository: IFoodRepository,
    private val categoryRepository: ICategoryRepository
) : BaseViewModel<CustomerHomeState, CustomerHomeIntent, CustomerHomeEffect>(CustomerHomeState()) {

    private val _foods = foodRepository.getMenu()
    private val _categories = categoryRepository.getCategories()
    private val _user = userRepository.getUser()

    val homeState = combine(_user, _categories, _foods) { user, categoriesResult, foodsResult ->
        val categories = (categoriesResult as? Resource.Success)?.data?.map { 
            CategoryUiModel(it.id, it.name, it.imageUrl)
        } ?: emptyList()

        val foods = (foodsResult as? Resource.Success)?.data ?: emptyList()

        CustomerHomeState(
            isLoading = foodsResult is Resource.Loading || categoriesResult is Resource.Loading,
            user = user,
            categories = categories,
            foods = foods // 2. Assign the full list of foods
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CustomerHomeState())

    fun setEvent(intent: CustomerHomeIntent) {
        handleIntent(intent)
    }

    override fun handleIntent(intent: CustomerHomeIntent) {
        when (intent) {
            is CustomerHomeIntent.ClickFood -> setEffect { CustomerHomeEffect.NavigateToFoodDetail(intent.food.id) }
            is CustomerHomeIntent.ClickCategory -> setEffect { CustomerHomeEffect.NavigateToCategory(intent.categoryId) }
            CustomerHomeIntent.ClickCart -> setEffect { CustomerHomeEffect.NavigateToCart }
            CustomerHomeIntent.ClickProfile -> setEffect { CustomerHomeEffect.NavigateToProfile }
            CustomerHomeIntent.ClickSettings -> setEffect { CustomerHomeEffect.NavigateToSettings }
            CustomerHomeIntent.ClickChangePassword -> setEffect { CustomerHomeEffect.NavigateToChangePassword }
            CustomerHomeIntent.ClickLogout -> logout()
            CustomerHomeIntent.ClickCurrentOrder -> setEffect { CustomerHomeEffect.NavigateToOrderHistory } // Add this
            else -> {}
        }
    }

    private fun logout() {
        viewModelScope.launch {
            userRepository.logout()
            setEffect { CustomerHomeEffect.NavigateToLogin }
        }
    }
}