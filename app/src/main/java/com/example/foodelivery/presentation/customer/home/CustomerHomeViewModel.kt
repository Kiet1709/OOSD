package com.example.foodelivery.presentation.customer.home

import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.base.BaseViewModel
import com.example.foodelivery.core.common.MockData
import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.Food
import com.example.foodelivery.domain.repository.IFoodRepository
import com.example.foodelivery.domain.repository.IUserRepository
import com.example.foodelivery.presentation.customer.home.contract.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerHomeViewModel @Inject constructor(
    private val userRepository: IUserRepository,
    private val foodRepository: IFoodRepository
) : BaseViewModel<CustomerHomeState, CustomerHomeIntent, CustomerHomeEffect>(CustomerHomeState()) {

    init {
        handleIntent(CustomerHomeIntent.LoadHomeData)
    }

    override fun handleIntent(intent: CustomerHomeIntent) {
        when(intent) {
            CustomerHomeIntent.LoadHomeData -> loadData()
            CustomerHomeIntent.Refresh -> loadData()
            
            // Nhóm các Intent điều hướng lại cho gọn
            is CustomerHomeIntent.ClickFood -> setEffect { CustomerHomeEffect.NavigateToFoodDetail(intent.foodId) }
            is CustomerHomeIntent.ClickCategory -> setEffect { CustomerHomeEffect.NavigateToCategory(intent.categoryId) }
            CustomerHomeIntent.ClickCart -> setEffect { CustomerHomeEffect.NavigateToCart }
            CustomerHomeIntent.ClickProfile -> setEffect { CustomerHomeEffect.NavigateToProfile }
            CustomerHomeIntent.ClickSettings -> setEffect { CustomerHomeEffect.NavigateToSettings }
            
            CustomerHomeIntent.ClickSearch -> setEffect { CustomerHomeEffect.ShowToast("Tính năng tìm kiếm đang phát triển") }
            CustomerHomeIntent.ClickLogout -> logout()
        }
    }

    private fun logout() {
        viewModelScope.launch {
            userRepository.logout()
            setEffect { CustomerHomeEffect.ShowToast("Đăng xuất thành công!") }
            delay(500)
            setEffect { CustomerHomeEffect.NavigateToLogin }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }

            // SRP: ViewModel chỉ điều phối dữ liệu, không chứa logic mapping phức tạp
            combine(
                userRepository.getUser(),
                foodRepository.getMenu()
            ) { user, foodResult ->
                
                // Xử lý dữ liệu món ăn tách biệt
                val foods = processFoodResult(foodResult)

                Triple(user, foods, MockData.categories)
            }.collect { (user, foods, categories) ->
                setState {
                    copy(
                        isLoading = false,
                        userName = user?.name ?: "Khách hàng",
                        avatarUrl = user?.avatarUrl.takeIf { !it.isNullOrBlank() } ?: "https://i.pravatar.cc/150?img=12",
                        categories = categories,
                        popularFoods = foods,
                        recommendedFoods = foods.shuffled().take(5)
                    )
                }
            }
        }
    }

    // Helper function: Tách logic xử lý kết quả API ra khỏi luồng chính
    private fun processFoodResult(result: Resource<List<Food>>): List<FoodUiModel> {
        return when(result) {
            is Resource.Success -> {
                result.data?.map { it.toUiModel() } ?: emptyList()
            }
            is Resource.Error -> {
                setEffect { CustomerHomeEffect.ShowToast(result.message ?: "Lỗi tải món ăn") }
                emptyList()
            }
            else -> emptyList()
        }
    }

    // Extension function: Chuyển đổi Domain Model -> UI Model
    private fun Food.toUiModel(): FoodUiModel {
        return FoodUiModel(
            id = this.id,
            name = this.name,
            imageUrl = this.imageUrl,
            price = this.price,
            rating = this.rating,
            time = "20 min" // Có thể tính toán time thật ở đây nếu cần
        )
    }
}