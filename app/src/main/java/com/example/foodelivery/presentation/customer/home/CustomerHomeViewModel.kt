package com.example.foodelivery.presentation.customer.home

import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.base.BaseViewModel
import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.repository.ICategoryRepository
import com.example.foodelivery.domain.repository.IFoodRepository
import com.example.foodelivery.domain.repository.IUserRepository
import com.example.foodelivery.presentation.customer.home.contract.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerHomeViewModel @Inject constructor(
    private val userRepository: IUserRepository,
    private val foodRepository: IFoodRepository,
    private val categoryRepository: ICategoryRepository
) : BaseViewModel<CustomerHomeState, CustomerHomeIntent, CustomerHomeEffect>(CustomerHomeState()) {

    init {
        handleIntent(CustomerHomeIntent.LoadHomeData)
    }

    fun setEvent(intent: CustomerHomeIntent) = handleIntent(intent)

    override fun handleIntent(intent: CustomerHomeIntent) {
        when(intent) {
           CustomerHomeIntent.ClickViewAllPopular -> setEffect {
                CustomerHomeEffect.NavigateToFoodList("popular")
            }
            CustomerHomeIntent.ClickViewAllRecommended -> setEffect {
                CustomerHomeEffect.NavigateToFoodList("recommended")
            }

            CustomerHomeIntent.LoadHomeData -> loadData()
            CustomerHomeIntent.Refresh -> loadData()

            is CustomerHomeIntent.ClickFood -> setEffect {
                CustomerHomeEffect.NavigateToFoodDetail(intent.foodId)
            }
            is CustomerHomeIntent.ClickCategory -> setEffect {
                CustomerHomeEffect.NavigateToCategory(intent.categoryId)
            }
            // [THÊM MỚI]: Xử lý click vào menu đơn hàng
            CustomerHomeIntent.ClickCurrentOrder -> {
                setEffect { CustomerHomeEffect.NavigateToTracking("ORD-CURRENT-DEMO") }
            }

            CustomerHomeIntent.ClickCart -> setEffect { CustomerHomeEffect.NavigateToCart }
            CustomerHomeIntent.ClickProfile -> setEffect { CustomerHomeEffect.NavigateToProfile }
            CustomerHomeIntent.ClickSearch -> setEffect {
                CustomerHomeEffect.ShowToast("Tính năng tìm kiếm đang phát triển")
            }

            CustomerHomeIntent.ClickSettings -> setEffect { CustomerHomeEffect.NavigateToSettings }
            CustomerHomeIntent.ClickLogout -> logout()
        }
    }

    private fun logout() {
        viewModelScope.launch {
            userRepository.logout()
            setEffect { CustomerHomeEffect.ShowToast("Đã đăng xuất!") }
            delay(500)
            setEffect { CustomerHomeEffect.NavigateToLogin }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }

            // 1. ✅ Lấy Categories từ Firebase
            launch {
                categoryRepository.getCategories().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            val categories = result.data?.map { category ->
                                CategoryUiModel(
                                    id = category.id,
                                    name = category.name,
                                    iconUrl = category.imageUrl
                                )
                            } ?: emptyList()

                            android.util.Log.d("HomeVM", "✅ Categories loaded: ${categories.size}")
                            setState { copy(categories = categories) }
                        }
                        is Resource.Error -> {
                            android.util.Log.e("HomeVM", "❌ Error categories: ${result.message}")
                        }
                        else -> {}
                    }
                }
            }

            // 2. ✅ Lấy User
            launch {
                userRepository.getUser().collect { user ->
                    setState { copy(user = user) }
                }
            }

            // 3. ✅ Lấy Foods
            launch {
                foodRepository.getMenu().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            val foods = result.data ?: emptyList()
                            android.util.Log.d("HomeVM", "✅ Foods loaded: ${foods.size}")

                            setState {
                                copy(
                                    isLoading = false,
                                    popularFoods = foods.sortedByDescending { it.rating }.take(10),
                                    recommendedFoods = foods.shuffled().take(10)
                                )
                            }
                        }
                        is Resource.Loading -> {
                            if (uiState.value.popularFoods.isEmpty()) {
                                setState { copy(isLoading = true) }
                            }
                        }
                        is Resource.Error -> {
                            android.util.Log.e("HomeVM", "❌ Error foods: ${result.message}")
                            setState { copy(isLoading = false) }
                            setEffect { CustomerHomeEffect.ShowToast(result.message ?: "Lỗi tải món ăn") }
                        }
                    }
                }
            }
        }
    }
}