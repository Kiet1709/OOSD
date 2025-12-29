package com.example.foodelivery.presentation.customer.home

import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.base.BaseViewModel
import com.example.foodelivery.domain.repository.IUserRepository
import com.example.foodelivery.presentation.customer.home.contract.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerHomeViewModel @Inject constructor(
    private val userRepository: IUserRepository
) : BaseViewModel<CustomerHomeState, CustomerHomeIntent, CustomerHomeEffect>(CustomerHomeState()) {

    init {
        handleIntent(CustomerHomeIntent.LoadHomeData)
    }

    override fun handleIntent(intent: CustomerHomeIntent) {
        when(intent) {
            CustomerHomeIntent.LoadHomeData -> loadData()
            CustomerHomeIntent.Refresh -> loadData()

            is CustomerHomeIntent.ClickFood -> setEffect { CustomerHomeEffect.NavigateToFoodDetail(intent.foodId) }
            is CustomerHomeIntent.ClickCategory -> setEffect { CustomerHomeEffect.NavigateToCategory(intent.categoryId) }

            CustomerHomeIntent.ClickCart -> setEffect { CustomerHomeEffect.NavigateToCart }
            CustomerHomeIntent.ClickProfile -> setEffect { CustomerHomeEffect.NavigateToProfile }
            CustomerHomeIntent.ClickSearch -> setEffect { CustomerHomeEffect.ShowToast("Tính năng tìm kiếm đang phát triển") }

            // [THÊM MỚI]: Xử lý Menu Header
            CustomerHomeIntent.ClickSettings -> setEffect { CustomerHomeEffect.NavigateToSettings }
            CustomerHomeIntent.ClickLogout -> logout()
        }
    }

    // Public fun cho UI gọi
    fun setEvent(intent: CustomerHomeIntent) = handleIntent(intent)

    // Xử lý đăng xuất
    private fun logout() {
        viewModelScope.launch {
            // 1. Gọi Repository để xóa token/cache
            userRepository.logout()
            // 2. Thông báo UI chuyển màn hình
            setEffect { CustomerHomeEffect.ShowToast("Đăng xuất thành công!") }
            delay(500) // Delay xíu cho User đọc Toast
            setEffect { CustomerHomeEffect.NavigateToLogin }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            // Mock Data
            val mockCategories = listOf(
                CategoryUiModel("1", "Burger", "https://cdn-icons-png.flaticon.com/512/3075/3075977.png"),
                CategoryUiModel("2", "Pizza", "https://cdn-icons-png.flaticon.com/512/1404/1404945.png"),
                CategoryUiModel("3", "Cơm", "https://cdn-icons-png.flaticon.com/512/261/261444.png"),
                CategoryUiModel("4", "Đồ uống", "https://cdn-icons-png.flaticon.com/512/2405/2405597.png")
            )

            val mockFoods = listOf(
                FoodUiModel("f1", "Burger Bò Phô Mai", "https://images.unsplash.com/photo-1568901346375-23c9450c58cd", 55000.0, 4.8, "15-20 min"),
                FoodUiModel("f2", "Pizza Hải Sản", "https://images.unsplash.com/photo-1513104890138-7c749659a591", 120000.0, 4.5, "25-30 min"),
                FoodUiModel("f3", "Cơm Tấm Sườn Bì", "https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2", 45000.0, 4.9, "10-15 min")
            )

            // Lấy thông tin User
            userRepository.getUser().collect { user ->
                setState {
                    copy(
                        isLoading = false,
                        userName = user?.name ?: "Khách hàng",
                        avatarUrl = if (user?.avatarUrl.isNullOrBlank()) "https://i.pravatar.cc/150?img=12" else user!!.avatarUrl,
                        categories = mockCategories,
                        popularFoods = mockFoods,
                        recommendedFoods = mockFoods.shuffled()
                    )
                }
            }
        }
    }
}