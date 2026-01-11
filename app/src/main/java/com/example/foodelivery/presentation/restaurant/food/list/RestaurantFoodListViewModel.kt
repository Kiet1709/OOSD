package com.example.foodelivery.presentation.restaurant.food.list

import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.base.BaseViewModel
import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.repository.IFoodRepository
import com.example.foodelivery.presentation.restaurant.food.list.contract.*
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantFoodListViewModel @Inject constructor(
    private val foodRepository: IFoodRepository
) : BaseViewModel<RestaurantFoodListState, RestaurantFoodListIntent, RestaurantFoodListEffect>(RestaurantFoodListState()) {

    init {
        handleIntent(RestaurantFoodListIntent.LoadFoods)
    }

    fun setEvent(intent: RestaurantFoodListIntent) {
        handleIntent(intent)
    }

    override fun handleIntent(intent: RestaurantFoodListIntent) {
        when (intent) {
            is RestaurantFoodListIntent.LoadFoods -> loadFoods()
            is RestaurantFoodListIntent.ClickDeleteFood -> {
                setState { copy(foodToDelete = intent.food) }
            }
            is RestaurantFoodListIntent.ConfirmDeleteFood -> {
                deleteFood()
            }
            is RestaurantFoodListIntent.DismissDeleteDialog -> {
                setState { copy(foodToDelete = null) }
            }
            is RestaurantFoodListIntent.ClickAddFood -> {
                setEffect { RestaurantFoodListEffect.NavigateToAddFood }
            }
            is RestaurantFoodListIntent.ClickEditFood -> {
                setEffect { RestaurantFoodListEffect.NavigateToEditFood(intent.foodId) }
            }
        }
    }

    private fun loadFoods() {
        viewModelScope.launch {
            val restaurantId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            foodRepository.getMenuByRestaurantId(restaurantId).collect { result ->
                when (result) {
                    is Resource.Loading -> setState { copy(isLoading = true) }
                    is Resource.Success -> {
                        setState { copy(isLoading = false, foods = result.data ?: emptyList()) }
                    }
                    is Resource.Error -> {
                        setState { copy(isLoading = false) }
                        setEffect { RestaurantFoodListEffect.ShowToast(result.message ?: "Lỗi tải danh sách món ăn") }
                    }
                }
            }
        }
    }

    private fun deleteFood() {
        val food = uiState.value.foodToDelete ?: return
        viewModelScope.launch {
            when (val result = foodRepository.deleteFood(food.id)) {
                is Resource.Success -> {
                    setState { copy(foodToDelete = null) }
                    setEffect { RestaurantFoodListEffect.ShowToast("Đã xóa '${food.name}' thành công") }
                }
                is Resource.Error -> {
                    setState { copy(foodToDelete = null) }
                    setEffect { RestaurantFoodListEffect.ShowToast(result.message ?: "Lỗi xóa món ăn") }
                }
                else -> {}
            }
        }
    }
}