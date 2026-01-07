package com.example.foodelivery.presentation.customer.home

import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.base.BaseViewModel
import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.Category
import com.example.foodelivery.domain.model.Food
import com.example.foodelivery.domain.repository.ICategoryRepository
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
    private val foodRepository: IFoodRepository,
    private val categoryRepository: ICategoryRepository
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

            combine(
                userRepository.getUser(),
                foodRepository.getMenu(),
                categoryRepository.getCategories()
            ) { user, foodResult, categoryResult ->
                
                val foods = processFoodResult(foodResult)
                val categories = processCategoryResult(categoryResult)

                Triple(user, foods, categories)
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

    private fun processCategoryResult(result: Resource<List<Category>>): List<CategoryUiModel> {
        return when(result) {
            is Resource.Success -> {
                result.data?.map { CategoryUiModel(it.id, it.name, it.imageUrl) } ?: emptyList()
            }
            is Resource.Error -> {
                setEffect { CustomerHomeEffect.ShowToast(result.message ?: "Lỗi tải danh mục") }
                emptyList()
            }
            else -> emptyList()
        }
    }

    private fun Food.toUiModel(): FoodUiModel {
        return FoodUiModel(
            id = this.id,
            name = this.name,
            imageUrl = this.imageUrl,
            price = this.price,
            rating = this.rating,
            time = "20 min"
        )
    }
}