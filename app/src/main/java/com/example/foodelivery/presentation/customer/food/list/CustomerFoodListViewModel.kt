package com.example.foodelivery.presentation.customer.food.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.base.BaseViewModel
import com.example.foodelivery.domain.repository.IFoodRepository
import com.example.foodelivery.presentation.customer.food.list.contract.CustomerFoodListEffect
import com.example.foodelivery.presentation.customer.food.list.contract.CustomerFoodListIntent
import com.example.foodelivery.presentation.customer.food.list.contract.CustomerFoodListState
import com.example.foodelivery.ui.theme.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerFoodListViewModel @Inject constructor(
    private val foodRepository: IFoodRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<CustomerFoodListState, CustomerFoodListIntent, CustomerFoodListEffect>(CustomerFoodListState()) {

    init {
        savedStateHandle.get<String>(Route.CustomerFoodList.ARG_TYPE)?.let {
            handleIntent(CustomerFoodListIntent.LoadFoods(it))
        }
    }

    fun setEvent(intent: CustomerFoodListIntent) = handleIntent(intent)

    override fun handleIntent(intent: CustomerFoodListIntent) {
        when (intent) {
            is CustomerFoodListIntent.LoadFoods -> loadFoods(intent.categoryId)
            is CustomerFoodListIntent.ClickFood -> setEffect { CustomerFoodListEffect.NavigateToFoodDetail(intent.foodId) }
        }
    }

    private fun loadFoods(categoryId: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            try {
                val foods = foodRepository.getFoodsByCategory(categoryId)
                setState { copy(isLoading = false, foods = foods) }
            } catch (e: Exception) {
                setState { copy(isLoading = false) }
                setEffect { CustomerFoodListEffect.ShowToast("Không thể tải danh sách món ăn") }
            }
        }
    }
}
